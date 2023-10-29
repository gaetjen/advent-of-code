package y2015

import util.readInput


object Day09 {
    private fun parse(input: List<String>): MutableMap<String, MutableMap<String, Int>> {
        val edges = mutableMapOf<String, MutableMap<String, Int>>()
        input.forEach { str ->
            val (a, _, b, _, d) = str.split(' ')
            val distance = d.toInt()
            if (edges[a] == null) {
                edges[a] = mutableMapOf(b to distance)
            } else {
                edges[a]!![b] = distance
            }
            if (edges[b] == null) {
                edges[b] = mutableMapOf(a to distance)
            } else {
                edges[b]!![a] = distance
            }
        }
        return edges
    }

    fun part1(input: List<String>): Int {
        val edges = parse(input)
        return shortest(edges, edges.keys)
    }

    private fun shortest(
        edges: Map<String, Map<String, Int>>,
        unvisited: Set<String>,
        current: String? = null,
        distance: Int = 0
    ): Int {
        if (unvisited.isEmpty()) {
            return distance
        }
        return if (current == null) {
            unvisited.minOf {
                shortest(
                    edges,
                    unvisited - it,
                    it
                )
            }
        } else {
            unvisited.minOf {
                shortest(
                    edges,
                    unvisited - it,
                    it,
                    edges[current]!![it]!! + distance
                )
            }
        }
    }

    fun part2(input: List<String>): Int {
        val edges = parse(input)
        return longest(edges, edges.keys)
    }

    private fun longest(
        edges: Map<String, Map<String, Int>>,
        unvisited: Set<String>,
        current: String? = null,
        distance: Int = 0
    ): Int {
        if (unvisited.isEmpty()) {
            return distance
        }
        return if (current == null) {
            unvisited.maxOf {
                longest(
                    edges,
                    unvisited - it,
                    it
                )
            }
        } else {
            unvisited.maxOf {
                longest(
                    edges,
                    unvisited - it,
                    it,
                    edges[current]!![it]!! + distance
                )
            }
        }
    }
}

fun main() {
    val testInput = """
        London to Dublin = 464
        London to Belfast = 518
        Dublin to Belfast = 141
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day09.part1(testInput))
    println(Day09.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2015/day09")
    println(Day09.part1(input))
    println(Day09.part2(input))
}
