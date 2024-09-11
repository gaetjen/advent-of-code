package y2017

import util.readInput
import util.timingStatistics

object Day16 {
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
    println(Day16.part1(testInput))
    println(Day16.part2(testInput))

    println("------Real------")
    val input = readInput(2017, 16)
    println("Part 1 result: ${Day16.part1(input)}")
    println("Part 2 result: ${Day16.part2(input)}")
    timingStatistics { Day16.part1(input) }
    timingStatistics { Day16.part2(input) }
}