package y2017

import util.readInput
import util.timingStatistics
import kotlin.math.abs

object Day02 {
    private fun parse(input: List<String>): List<List<Long>> {
        return input.map { line ->
            line.split(Regex("\\s")).map { it.toLong() }
        }
    }

    fun part1(input: List<String>): Long {
        val parsed = parse(input)
        val maxes = parsed.map { it.max() }
        val mins = parsed.map { it.min() }
        return maxes.zip(mins).map { (a, b) -> abs(a - b) }.sum()
    }

    fun part2(input: List<String>): Long {
        val parsed = parse(input).map { ln ->
            val line = ln.sortedDescending()
            line.firstNotNullOf { first ->
                line.firstOrNull {first % it == 0L && first != it}?.let { first / it }
            }
        }
        return parsed.sum()
    }
}

fun main() {
    val testInput = """
        5 9 2 8
        9 4 7 3
        3 8 6 5
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day02.part1(testInput))
    println(Day02.part2(testInput))

    println("------Real------")
    val input = readInput(2017, 2)
    println("Part 1 result: ${Day02.part1(input)}")
    println("Part 2 result: ${Day02.part2(input)}")
    timingStatistics { Day02.part1(input) }
    timingStatistics { Day02.part2(input) }
}