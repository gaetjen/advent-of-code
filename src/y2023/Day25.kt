package y2023

import util.readInput
import util.timingStatistics

object Day25 {
    private fun parse(input: List<String>): List<Pair<String, String>> {
        return input.flatMap { line ->
            val (p1, p2) = line.split(": ")
            p2.split(" ").flatMap {
                listOf(p1 to it, it to p1)
            }
        }
    }

    fun part1(input: List<String>): Int {
        val edges = parse(input)
        val lookup = edges.groupBy { it.first }.mapValues { (_, v) -> v.map { it.second } }
        var components = calculateRandomizedConnectedComponents(edges, lookup)
        while (components.size != 2) {
            components = calculateRandomizedConnectedComponents(edges, lookup)
        }
        return components.first().size * components.last().size
    }

    private fun calculateRandomizedConnectedComponents(
        edges: List<Pair<String, String>>,
        lookup: Map<String, List<String>>
    ): List<Set<String>> {
        val edgeUseCount = edges.associateWith { 0 }.toMutableMap()
        val randomStartStop = lookup.keys.toList().shuffled().zipWithNext()
        randomStartStop.forEach { (n1, n2) ->
            shortestPathEdges(n1, n2, lookup).forEach { edge ->
                edgeUseCount[edge] = edgeUseCount[edge]!! + 1
            }
        }
        val consolidatedEdges = edgeUseCount.mapValues { (edge, count) ->
            if (edge.first < edge.second) {
                count + edgeUseCount[edge.second to edge.first]!!
            } else {
                0
            }
        }
        val cutEdges = consolidatedEdges.entries.sortedByDescending { it.value }.take(3).map { it.key }
        val splitGraph = lookup.toMutableMap()

        cutEdges.forEach { (n1, n2) ->
            splitGraph[n1] = splitGraph[n1]!!.filter { it != n2 }
            splitGraph[n2] = splitGraph[n2]!!.filter { it != n1 }
        }

        return connectedComponents(splitGraph)
    }

    private fun connectedComponents(splitGraph: MutableMap<String, List<String>>): List<Set<String>> {
        val rtn = mutableListOf<Set<String>>()
        val remaining = splitGraph.keys.toMutableSet()
        while (remaining.isNotEmpty()) {
            val start = remaining.first()
            var frontier = setOf(start)
            val component = mutableSetOf(start)
            while (frontier.isNotEmpty()) {
                frontier = frontier.flatMap { node ->
                    splitGraph[node]!!.filter { it in remaining }
                }.toSet()
                component.addAll(frontier)
                remaining.removeAll(frontier)
            }
            rtn.add(component)
        }
        return rtn
    }

    private fun shortestPathEdges(node1: String, node2: String, graph: Map<String, List<String>>): List<Pair<String, String>> {
        val path = shortestPath(node1, node2, graph)
        return path.zipWithNext()
    }

    private fun shortestPath(node1: String, node2: String, graph: Map<String, List<String>>): List<String> {
        val visited = mutableSetOf(node1)
        var paths = listOf(listOf(node1))
        while (true) {
            paths = paths.flatMap { path ->
                val last = path.last()
                val nexts = graph[last]!!.filter { it !in visited }
                visited.addAll(nexts)
                if (node2 in nexts) {
                    return path + node2
                }
                nexts.map { next ->
                    path + next
                }
            }
        }
    }
}

fun main() {
    val testInput = """
        jqt: rhn xhk nvd
        rsh: frs pzl lsr
        xhk: hfx
        cmg: qnr nvd lhk bvb
        rhn: xhk bvb hfx
        bvb: xhk hfx
        pzl: lsr hfx nvd
        qnr: nvd
        ntq: jqt hfx bvb xhk
        nvd: lhk
        lsr: lhk
        rzs: qnr cmg lsr rsh
        frs: qnr lhk lsr
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day25.part1(testInput))

    println("------Real------")
    val input = readInput(2023, 25)
    println("Part 1 result: ${Day25.part1(input)}")
    timingStatistics { Day25.part1(input) }
}