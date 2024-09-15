package y2017

import util.readInput
import util.timingStatistics

object Day12 {
    private fun parse(input: List<String>): Map<Int, List<Int>> {
        return input.associate { ln ->
            val (k, es) = ln.split(" <-> ")
            k.toInt() to es.split(", ").map { it.toInt() }
        }
    }

    fun part1(input: List<String>): Int {
        val edges = parse(input)
        val visited = mutableSetOf<Int>()
        var frontier = setOf(0)
        while (frontier.isNotEmpty()) {
            visited.addAll(frontier)
            frontier = frontier.map { edges[it]!! }.flatten().toSet() - visited
        }
        return visited.size
    }

    fun part2(input: List<String>): Long {
        val parsed = parse(input)
        return 0L
    }
}

fun main() {
    val testInput = """
        0 <-> 2
        1 <-> 1
        2 <-> 0, 3, 4
        3 <-> 2, 4
        4 <-> 2, 3, 6
        5 <-> 6
        6 <-> 4, 5
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day12.part1(testInput))
    println(Day12.part2(testInput))

    println("------Real------")
    val input = readInput(2017, 12)
    println("Part 1 result: ${Day12.part1(input)}")
    println("Part 2 result: ${Day12.part2(input)}")
    timingStatistics { Day12.part1(input) }
    timingStatistics { Day12.part2(input) }
}