package y2024

import util.readInput
import util.timingStatistics
import kotlin.math.abs

object Day02 {
    private fun parse(input: List<String>): List<List<Int>> {
        return input.map { it.split(" ").map { it.toInt() } }
    }

    fun part1(input: List<String>): Int {
        val parsed = parse(input)
        return parsed.count { isSafe(it) }
    }

    fun isSafe(row: List<Int>): Boolean {
        if (row.sorted() == row || row.sortedDescending() == row) {
            return row.zipWithNext().all { (a, b) -> abs(a - b) in 1..3 }
        }
        return false
    }

    fun part2Safe(row: List<Int>): Boolean {
        if (isSafe(row)) {
            return true
        }
        row.indices.forEach { idx ->
            val buffered = row.toMutableList()
            buffered.removeAt(idx)
            if (isSafe(buffered)) {
                return true
            }
        }
        return false
    }

    fun part2(input: List<String>): Int {
        val parsed = parse(input)
        return parsed.count { part2Safe(it) }

    }
}

fun main() {
    val testInput = """
        7 6 4 2 1
        1 2 7 8 9
        9 7 6 2 1
        1 3 2 4 5
        8 6 4 4 1
        1 3 6 7 9
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day02.part1(testInput))
    println(Day02.part2(testInput))

    println("------Real------")
    val input = readInput(2024, 2)
    println("Part 1 result: ${Day02.part1(input)}")
    println("Part 2 result: ${Day02.part2(input)}")
    timingStatistics { Day02.part1(input) }
    timingStatistics { Day02.part2(input) }
}