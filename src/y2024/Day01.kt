package y2024

import util.readInput
import util.timingStatistics

object Day01 {
    private fun parse(input: List<String>): Any {
        TODO()
    }

    fun part1(input: List<String>): Int {
        val parsed = parse(input)
        return 0
    }

    fun part2(input: List<String>): Int {
        val parsed = parse(input)
        return 0
    }
}

fun main() {
    val testInput = """
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day01.part1(testInput))
    println(Day01.part2(testInput))

    println("------Real------")
    val input = readInput(2024, 1)
    println("Part 1 result: ${Day01.part1(input)}")
    println("Part 2 result: ${Day01.part2(input)}")
    timingStatistics { Day01.part1(input) }
    timingStatistics { Day01.part2(input) }
}