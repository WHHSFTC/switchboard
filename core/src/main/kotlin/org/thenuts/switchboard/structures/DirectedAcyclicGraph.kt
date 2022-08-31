package org.thenuts.switchboard.structures

object DirectedAcyclicGraph {
    abstract class Node {
        internal val prereqs = mutableSetOf<Edge>()
        internal val postreqs = mutableSetOf<Edge>()
        internal val workingPrereqs = mutableSetOf<Edge>()
    }

    data class Edge(val before: Node, val after: Node, val priority: Int) {
        var enabled: Boolean = false
            internal set
    }

    private fun checkCycle(edge: Edge): Boolean {
        // bfs forward through tree starting at after, ending at before to find a cycle
        val queue = mutableListOf(edge.after)
        var i = 0
        while (i < queue.size) {
            val node = queue[i]
            node.postreqs.forEach {
//                if (it.priority > edge.priority || !edge.enabled) {
//                    // greater value means lower priority, so we skip those edges
//                    return@forEach
//                }
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

    /**
     * Topological sorts a directed acyclic graph including all of [nodes] and some of [edges] and
     * returns the result.
     *
     * @param nodes List of nodes in the graph
     * @param edges List of directed edges to define the order.
     * @param strict Whether to reject graphs with cycles. Useful for testing, but is usually a bad
     * idea for complex graphs, like in the root DAG of an OpMode.
     *
     * Each edge has a priority, and cycles are resolved by eliminating the lowest priority edge.
     * Edges are processed in order of decreasing priority (increasing [Edge.priority] value), and
     * an edge that completes a cycle is eliminated.
     */
    @Throws(GraphException::class)
    fun topSort(
        nodes: List<Node>,
        edges: List<Edge>,
        strict: Boolean,
    ) : List<Node> {
        nodes.forEach {
            it.postreqs.clear()
            it.prereqs.clear()
            it.workingPrereqs.clear()
        }

        // sorting all at once each cycle avoids problems with mutable edge priority
        // if priorities don't change between calls, this should be practically a O(n) no-op
        val prioritizedEdges = edges.sortedBy { it.priority }
        prioritizedEdges.forEach {
            if (it.before == it.after) {
                it.enabled = false
                return@forEach
            }

            val en = !checkCycle(it)
            it.enabled = en

            if (en) {
                it.before.postreqs += it
                it.after.prereqs += it
            } else if (strict) {
                throw GraphException("DAG in strict mode contains cycle.")
            }
        }

        // begin with "start nodes"
        val sorted = nodes.filter { it.prereqs.none { edge -> edge.enabled } }.toMutableList()
        var i = 0

        nodes.forEach { it.workingPrereqs.clear(); it.workingPrereqs.addAll(it.prereqs) }

        while (i < sorted.size) {
            val n = sorted[i]
            n.postreqs.forEach { edge ->
                if (edge.enabled) {
                    val aft = edge.after
                    val pres = aft.workingPrereqs
                    pres.remove(edge)
                    if (pres.none { e -> e.enabled }) {
                        sorted.add(aft)
                    }
                }
            }
            i++
        }

        nodes.forEach { it.workingPrereqs.clear() }

        if (sorted.size == nodes.size) {
            return sorted
        } else {
            throw GraphException("Failed to eliminate cycles by selectively disabling edges.")
        }
    }
}

class GraphException(msg: String) : Exception(msg)
