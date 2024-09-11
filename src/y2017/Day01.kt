package y2017

import util.readInput
import util.timingStatistics

object Day01 {
    private fun parse(input: List<String>): List<Long> {
        return input.first().map { it.digitToInt().toLong() }
    }

    fun part1(input: List<String>): Long {
        val digits = parse(input)
        return (digits + digits.first()).zipWithNext().filter { (a, b) -> a == b }.sumOf { it.first }
    }

    fun part2(input: List<String>): Long {
        val digits = parse(input)
        return digits.zip(digits.drop(digits.size / 2)).filter { (a, b) -> a == b }.sumOf { it.first * 2 }
    }
}

fun main() {
    val testInput = """
        12131415
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day01.part1(testInput))
    println(Day01.part2(testInput))

    println("------Real------")
    val input = readInput(2017, 1)
    println("Part 1 result: ${Day01.part1(input)}")
    println("Part 2 result: ${Day01.part2(input)}")
    timingStatistics { Day01.part1(input) }
    timingStatistics { Day01.part2(input) }
}