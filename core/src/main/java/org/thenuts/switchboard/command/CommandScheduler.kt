package org.thenuts.switchboard.command

import org.thenuts.switchboard.core.Frame

class CommandScheduler : CommandManager {
    var cmds = mutableListOf<Command>()
    val prereqs = mutableMapOf<Command, MutableSet<Command>>()
    val postreqs = mutableMapOf<Command, MutableSet<Command>>()
    var edges = mutableSetOf<Edge>()

//    var resources = mutableMapOf<Any, Resource>()

    var edgeRemovals = mutableListOf<Edge>()
    var edgeAdditions = mutableListOf<Edge>()
    var additions = mutableListOf<Command>()
    var removals = mutableSetOf<Command>()

    data class Edge(var owner: Edge.Owner, val before: Command, val after: Command) {
        enum class Owner(private val n: Int) {
            DELETE(0),
            BEFORE(1),
            AFTER(1),
            BOTH(2);

            operator fun plus(that: Owner) =
                when {
                    this == that -> this
                    this.n == that.n -> BOTH
                    this.n > that.n -> this
                    this.n < that.n -> that
                    else -> DELETE.also { assert(false) { "Unreachable branch" } }
                }

            operator fun minus(that: Owner) =
                when {
                    this == that -> DELETE
                    this.n == that.n -> this
                    this.n <= that.n -> DELETE
                    that == DELETE -> this
                    that == AFTER -> BEFORE
                    else -> AFTER
                }
        }
    }

    fun addCommand(cmd: Command) {
        additions.add(cmd)
    }

    fun removeCommand(cmd: Command) {
        removals.add(cmd)
    }

    // CommandParent methods
    override fun handleRegisterPrerequisite(src: Command, prereq: Command) {
        edgeAdditions.add(Edge(Edge.Owner.AFTER, prereq, src))
    }

    override fun handleRegisterPostrequisite(src: Command, postreq: Command) {
        edgeAdditions.add(Edge(Edge.Owner.BEFORE, src, postreq))
    }

    override fun handleDeregisterPrerequisite(src: Command, prereq: Command) {
        edgeRemovals.add(Edge(Edge.Owner.AFTER, prereq, src))
    }

    override fun handleDeregisterPostrequisite(src: Command, postreq: Command) {
        edgeRemovals.add(Edge(Edge.Owner.AFTER, src, postreq))
    }

/*
    override fun handleFinish(src: Command) {
        removals.add(src)
    }

    data class Resource(var value: Any, val scope: Command?, var owner: Command? = null)

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
        cmds.forEach {
            it.update(frame)
        }
        removeEdges()
        removeCommands()
        addCommands()
        addEdges()
    }

    private fun removeEdges() {
        edgeRemovals.forEach f@{ (owner, before, after) ->
            val f = edges.find { (o, b, a) -> b == before && a == after }
            if (f != null) {
                f.owner -= owner
                if (f.owner == Edge.Owner.DELETE) {
                    val post = postreqs[before]
                    val pre = prereqs[after]

                    post?.remove(after)
                    pre?.remove(before)
                }
            }
            edges.remove(f)
        }
        edgeRemovals.clear()
    }

    private fun addEdges() {
        edgeAdditions.removeIf f@{ (owner, before, after) ->
            val f = edges.find { (o, b, a) -> b == before && a == after }
            if (f != null) {
                f.owner += owner
                return@f true
            }
            if (cmds.contains(before) && cmds.contains(after)) {
                val post = postreqs[before]
//                if (post?.contains(after) == true) return@f true

                val pre = prereqs[after]
//                if (pre?.contains(before) == true) return@f true

                post?.add(after)
                pre?.add(before)

                val acyclic = topSort()
                if (acyclic) return@f true

                // undo edge creation
                post?.remove(after)
                pre?.remove(before)
                return@f false
            }
            return@f false
        }
    }

    private fun addCommands() {
        cmds.addAll(additions)
        additions.forEach {
            prereqs[it] = mutableSetOf()
            postreqs[it] = mutableSetOf()
        }
        additions.forEach {
            it.setManager(this)
        }
        additions.forEach {
            it.load()
        }
        additions.clear()
    }

    private fun removeCommands() {
        prereqs.forEach { (c, p) ->
            p.removeAll(removals)
        }
        postreqs.forEach { (c, p) ->
            p.removeAll(removals)
        }

        removals.forEach {
            prereqs.remove(it)
            postreqs.remove(it)

//            resources.entries.removeAll { (_, res) ->
//                res.owner == it
//            }
        }

        cmds.removeAll {
            if (it in removals) {
                it.cleanup()
                true
            } else false
        }

        topSort()

        removals.clear()
    }

    private fun topSort(): Boolean {
        val frozen = cmds.toMutableList()

        // shadow prereqs with working copy that we can remove edges from
        val prereqs = mutableMapOf<Command, MutableSet<Command>>().also {
            this.prereqs.onEach { (k, v) -> it[k] = v.toMutableSet() }
        }

        val sorted = frozen.filter { this.prereqs[it]?.isEmpty() ?: false }.toMutableList()
        var i = 0;
        while (i < sorted.size) {
            val n = sorted[i]
            postreqs[n]?.let { posts ->
                posts.forEach {
                    prereqs[it]?.let { pres ->
                        pres.remove(n)
                        if (pres.isEmpty()) {
                            sorted.add(it)
                        }
                    }
                }
            }
            i++
        }

        return if (sorted.size == frozen.size) {
            cmds = sorted
            true
        } else {
            false
        }
    }
}