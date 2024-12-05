package y2024

import util.readInput
import util.timingStatistics

object Day06 {
    private fun parse(input: List<String>): Any {
        TODO()
    }

    fun part1(input: List<String>): Long {
        val parsed = parse(input)
        return 0L
    }

    fun part2(input: List<String>): Long {
        val parsed = parse(input)
        return 0L
    }
}

fun main() {
    val testInput = """
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day06.part1(testInput))
    println(Day06.part2(testInput))

    println("------Real------")
    val input = readInput(2024, 6)
    println("Part 1 result: ${Day06.part1(input)}")
    println("Part 2 result: ${Day06.part2(input)}")
    timingStatistics { Day06.part1(input) }
    timingStatistics { Day06.part2(input) }
}