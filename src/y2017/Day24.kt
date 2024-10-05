package y2017

import util.Pos
import util.readInput
import util.timingStatistics

data class Bridge(
    val lastPort: Int,
    val components: List<Pair<Int, Int>>
)

object Day24 {
    private fun parse(input: List<String>): List<Pair<Int, Int>> {
        return input.map { component ->
            val (p1, p2) = component.split("/").map { it.toInt() }
            p1 to p2
        }
    }

    fun part1(input: List<String>): Int {
        val components = parse(input).toSet()
        val bridges = bridge(0, components)
        val strongest = bridges.maxBy { bridge -> bridge.sumOf { it.first + it.second } }
        return strongest.sumOf { it.first + it.second }
    }

    private fun bridge(lastPort: Int, remainingComponents: Set<Pair<Int, Int>>): List<List<Pos>> {
        return remainingComponents
            .filter { it.first == lastPort || it.second == lastPort }
            .flatMap { (p1, p2) ->
                val nextPort = if (p1 == lastPort) p2 else p1
                val tails = bridge(nextPort, remainingComponents - (p1 to p2))
                if (tails.isEmpty()) {
                    listOf(listOf(p1 to p2))
                } else {
                    tails.map { listOf(p1 to p2) + it }
                }
            }

    }

    fun part2(input: List<String>): Int {
        val components = parse(input).toSet()
        val bridges = bridge(0, components)
        val longestLength = bridges.maxOf { it.size }
        val longestBridges = bridges.filter { it.size == longestLength }
        val strongest = longestBridges.maxBy { bridge -> bridge.sumOf { it.first + it.second } }
        return strongest.sumOf { it.first + it.second }
    }
}

fun main() {
    val testInput = """
        0/2
        2/2
        2/3
        3/4
        3/5
        0/1
        10/1
        9/10
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day24.part1(testInput))
    println(Day24.part2(testInput))

    println("------Real------")
    val input = readInput(2017, 24)
    println("Part 1 result: ${Day24.part1(input)}")
    println("Part 2 result: ${Day24.part2(input)}")
    timingStatistics { Day24.part1(input) }
    timingStatistics { Day24.part2(input) }
}