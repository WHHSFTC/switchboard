package org.thenuts.switchboard.command

import org.thenuts.switchboard.core.Frame

class CommandScheduler : CommandManager {
    private var _nodes = mutableListOf<Node>()
    val nodes: List<Node>
        get() = _nodes

    private val edges = mutableSetOf<Edge>()

//    var resources = mutableMapOf<Any, Resource>()

    private val edgeRemovals = mutableListOf<Edge>()
    private val edgeInsertions = mutableListOf<Edge>()
    private val insertions = mutableListOf<Node>()
    private val removals = mutableSetOf<Node>()

    sealed class Node(var prereqs: MutableList<Edge> = mutableListOf(), var postreqs: MutableList<Edge> = mutableListOf()) {
        class CommandNode(val cmd: Command) : Node()

        class ResourceNode(val res: Resource) : Node()

        var workingPrereqs = mutableListOf<Edge>()
    }

    data class Edge(var owner: Owner, val before: Node, val after: Node) {
        enum class Owner {
            DELETE,
            BEFORE,
            AFTER,
            BOTH;

            companion object {
                val values = values();
            }

            operator fun plus(that: Owner) = values[this.ordinal or that.ordinal]

            operator fun minus(that: Owner) = values[this.ordinal and that.ordinal.inv() and 3]
        }
    }

    fun Resource.node() = _nodes.find { it is Node.ResourceNode && it.res == this }
    fun Command.node() = _nodes.find { it is Node.CommandNode && it.cmd == this }

    fun addCommand(cmd: Command): Boolean {
        if (cmd.node() != null) return false
        insertions += Node.CommandNode(cmd)
        return true
    }

    fun removeCommand(cmd: Command): Boolean {
        removals += cmd.node() ?: return false
        return true
    }

    // CommandParent methods
    override fun handleRegisterPrerequisite(src: Command, prereq: Command) {
        edgeInsertions.add(Edge(Edge.Owner.AFTER, prereq.node() ?: return, src.node() ?: return))
    }

    override fun handleRegisterPostrequisite(src: Command, postreq: Command) {
        edgeInsertions.add(Edge(Edge.Owner.BEFORE, src.node() ?: return, postreq.node() ?: return))
    }

    override fun handleDeregisterPrerequisite(src: Command, prereq: Command) {
        edgeRemovals.add(Edge(Edge.Owner.AFTER, prereq.node() ?: return, src.node() ?: return))
    }

    override fun handleDeregisterPostrequisite(src: Command, postreq: Command) {
        edgeRemovals.add(Edge(Edge.Owner.AFTER, src.node() ?: return, postreq.node() ?: return))
    }

/*
    override fun handleFinish(src: Command) {
        removals.add(src)
    }

*/
    data class Resource(val key: Any, val value: Any, val owner: Command?)

/*
    override fun offerResource(provider: Command, key: Any, value: Any, mutable: Boolean, internal: Boolean) {
        resources[key] = Resource(value, provider)
    }

    override fun requestResource(user: Command, key: Any, mutable: Boolean, internal: Boolean): Any? {
        synchronized(resources) {
            if (key in resources) {
                val res = resources[key]!!
                res.owner = user
            }
        }
        return null
    }

    override fun releaseResource(src: Command, key: Any) {
        synchronized(resources) {
            if (key in resources) {
                return resources[key]!!.value
            }
        }
    }
*/

    fun update(frame: Frame) {
        removeEdges()
        removeNodes()
        insertNodes(frame)
        val needSort = edgeInsertions.isNotEmpty()
        insertEdges()
        if (needSort) {
            val acyclic = topSort()
            assert(acyclic) { "Graph has cycles" }
        }
        _nodes.forEach {
            when (it) {
                is Node.CommandNode -> {
                    it.cmd.update(frame)
                    if (it.cmd.done)
                        removals += it
                }
                is Node.ResourceNode -> TODO()
            }
        }
    }

    fun clear() {
        _nodes.forEach {
            when (it) {
                is Node.CommandNode -> {
                    it.cmd.cleanup()
                }
                is Node.ResourceNode -> TODO()
            }
        }
        _nodes.clear()
        edges.clear()
        edgeRemovals.clear()
        edgeInsertions.clear()
        removals.clear()
        insertions.clear()
    }

    private fun removeEdges() {
        edgeRemovals.forEach f@{ (owner, before, after) ->
            val f = edges.find { (o, b, a) -> b == before && a == after }
            if (f != null) {
                f.owner -= owner
                if (f.owner == Edge.Owner.DELETE) {
                    before.postreqs.removeIf { it.after == after }
                    after.prereqs.removeIf { it.before == before }
                    edges.remove(f)
                }
            }
        }
        edgeRemovals.clear()
    }

    private fun insertEdges() {
        edgeInsertions.removeIf f@{ e ->
            val f = edges.find { (o, b, a) -> b == e.before && a == e.after }
            if (f != null) {
                f.owner += e.owner
                return@f true
            }
            if (_nodes.contains(e.before) && _nodes.contains(e.after)) {
                if (checkCycle(e)) return@f false

                e.before.postreqs += e
                e.after.prereqs += e

                edges += e

                return@f true
            }
            return@f false
        }
    }

    private fun insertNodes(frame: Frame) {
        _nodes.addAll(insertions)
        insertions.forEach {
            if (it is Node.CommandNode) {
                it.cmd.setManager(this)
                it.cmd.init()
                it.cmd.start(frame)
            }
        }
        insertions.clear()
    }

    private fun removeNodes() {
        removals.forEach f@{ removal ->
            val badEdges = removal.prereqs + removal.postreqs

            _nodes.forEach { node ->
                node.prereqs.removeAll(badEdges)
                node.postreqs.removeAll(badEdges)
            }

            _nodes.remove(removal)

            if (removal is Node.CommandNode) {
                removal.cmd.cleanup()
            }

//            resources.entries.removeAll { (_, res) ->
//                res.owner == it
//            }
        }

        removals.clear()
    }

    private fun checkCycle(edge: Edge): Boolean {
        // bfs forward through tree starting at after, ending at before to find a cycle
        val queue = mutableListOf(edge.after)
        var i = 0
        while (i < queue.size) {
            val node = queue[i]
            node.postreqs.forEach {
                val post = it.after
                if (post == edge.before) {
                    return true
                }
                if (post !in queue) {
                    queue += post
                }
            }
            i++
        }
        return false
    }

    private fun topSort(): Boolean {
        val sorted = _nodes.filter { it.prereqs.isEmpty() }.toMutableList()
        var i = 0;

        _nodes.forEach { it.workingPrereqs.clear(); it.prereqs.mapTo(it.workingPrereqs) { it } }

        while (i < sorted.size) {
            val n = sorted[i]
            n.postreqs. forEach { edge ->
                val aft = edge.after
                val pres = aft.workingPrereqs
                pres.remove(edge)
                if (pres.isEmpty()) {
                    sorted.add(aft)
                }
            }
            i++
        }

        _nodes.forEach { it.workingPrereqs.clear() }

        return if (sorted.size == _nodes.size) {
            _nodes = sorted
            true
        } else {
            false
        }
    }
}