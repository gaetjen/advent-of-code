package y2024

import util.readInput
import util.timingStatistics
import util.transpose
import kotlin.math.abs

object Day01 {
    private fun parse(input: List<String>): List<List<Int>> {
        return input.map { line -> line.split(Regex("\\s+")).map { it.toInt() } }.transpose()
    }

    fun part1(input: List<String>): Int {
        val (l1, l2) = parse(input)
        return l1.sorted().zip(l2.sorted()).sumOf { (a, b) -> abs(a - b) }
    }

    fun part2(input: List<String>): Int {
        val (l1, l2) = parse(input)
        return l1.sumOf { a -> l2.count { it == a } * a }
    }
}

fun main() {
    val testInput = """
        3   4
        4   3
        2   5
        1   3
        3   9
        3   3
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