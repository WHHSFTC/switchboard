package org.thenuts.switchboard.command

import org.thenuts.switchboard.command.store.Resource
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
        }

        _nodes = DirectedAcyclicGraph.topSort(_nodes, edges, strict).toMutableList()

        _nodes.forEach { node ->
            if (node is CommandNode) {
                if (node.runner.step(frame))
                    removals += node
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
    }

    private fun beginNode(node: Node) { }
}