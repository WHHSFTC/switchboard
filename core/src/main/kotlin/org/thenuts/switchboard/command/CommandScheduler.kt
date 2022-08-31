package org.thenuts.switchboard.command

import org.thenuts.switchboard.command.store.Box
import org.thenuts.switchboard.command.store.Resource
import org.thenuts.switchboard.command.store.ResourceHandler
import org.thenuts.switchboard.structures.DirectedAcyclicGraph
import org.thenuts.switchboard.util.Frame
import org.thenuts.switchboard.structures.DirectedAcyclicGraph.Node
import org.thenuts.switchboard.structures.DirectedAcyclicGraph.Edge

class CommandScheduler(val strict: Boolean = false) {
    private var _nodes = mutableListOf<Node>()
    val nodes: List<Node>
        get() = _nodes

    private val edges = mutableListOf<Edge>()

    private val insertions = mutableListOf<Node>()
    private val removals = mutableSetOf<Node>()

    class CommandNode(val runner: CommandRunner) : Node()

    class ResourceNode(val res: Resource) : Node()

    private fun Command.node() = _nodes.find { it is CommandNode && it.runner.cmd == this }

    fun addCommand(cmd: Command): Boolean {
        if (cmd.node() != null) return false
        insertions += CommandNode(CommandRunner(cmd))
        return true
    }

    fun removeCommand(cmd: Command): Boolean {
        removals += cmd.node() ?: return false
        return true
    }

    private fun makeOrGetResource(key: Any): ResourceNode {
        var f = _nodes.filterIsInstance<ResourceNode>().find { it.res.key == key }
        if (f == null) {
            f = ResourceNode(Resource(key))
            insertions += f
        }
        return f
    }

    fun update(frame: Frame) {
        removals.forEach { cleanupNode(it) }
        _nodes.removeAll(removals)
        removals.clear()

        edges.clear()

        insertions.forEach { beginNode(it) }
        _nodes.addAll(insertions)
        insertions.clear()

        fun addEdge(edge: Edge) {
            val match = edges.find { e -> e.before == edge.before && e.after == edge.before }
            if (match != null) {
                if (match.priority > edge.priority)
                    edges -= match
                else
                    return
            }
            edges += edge
        }

        _nodes.filterIsInstance<CommandNode>().forEach { cnode ->
            val pres = cnode.runner.cmd.prereqs
            val posts = cnode.runner.cmd.postreqs

            pres.forEach { (before, priority) ->
                before.node()?.let { b ->
                    addEdge(Edge(b, cnode, priority))
                }
            }

            posts.forEach { (after, priority) ->
                after.node()?.let { a ->
                    addEdge(Edge(cnode, a, priority))
                }
            }

            val deps = cnode.runner.cmd.dependencies
            deps.forEach { (k, v) ->
                val rnode = makeOrGetResource(k).also {
                    it.res.box?.let { box -> v.box = Box(box.inner) }
                }

                if (v is ResourceHandler.Writeable) {
                    addEdge(Edge(cnode, rnode, v.priority))
                } else if (v is ResourceHandler.Readable) {
                    addEdge(Edge(rnode, cnode, v.priority))
                }
            }
        }

        // makeOrGetResource can introduce new resource nodes, but they can't be added while iterating over _nodes
        insertions.forEach { beginNode(it) }
        _nodes.addAll(insertions)
        insertions.clear()

        _nodes = DirectedAcyclicGraph.topSort(_nodes, edges, strict).toMutableList()

        _nodes.forEach { node ->
            when (node) {
                is CommandNode -> {
                    if (node.runner.step(frame))
                        removals += node
                }
                is ResourceNode -> {
                    assert(node.prereqs.size <= 1) { "a ResourceNode should not have more than 1 supplier" }
                    node.prereqs.firstOrNull()?.let { edge ->
                        (edge.before as? CommandNode)?.let { cn ->
                            cn.runner.cmd.dependencies[node.res.key]!!.let { handler ->
                                assert(handler is ResourceHandler.Writeable<*>)
                                node.res.box = handler.box
                            }
                        }
                    }

                    node.postreqs.forEach { edge ->
                        (edge.after as? CommandNode)?.let { cn ->
                            cn.runner.cmd.dependencies[node.res.key]!!.let { handler ->
                                assert(handler is ResourceHandler.Readable<*>)
                                handler.box = node.res.box
                            }
                        }
                    }
                }
            }
        }
    }

    fun clear() {
        _nodes.forEach {
            cleanupNode(it)
        }
        _nodes.clear()
        edges.clear()
        removals.clear()
        insertions.clear()
    }

    private fun cleanupNode(node: Node) {
        edges.removeAll { it.before == node || it.after == node }

        if (node is CommandNode) {
            node.runner.interrupt()
        }

//            resources.entries.removeAll { (_, res) ->
//                res.owners == it
//            }
    }

    private fun beginNode(node: Node) { }
}