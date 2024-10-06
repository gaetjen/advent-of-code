package y2018

import util.readInput
import util.timingStatistics

object Day01 {
    private fun parse(input: List<String>): List<Int> {
        return input.map { it.toInt() }
    }

    fun part1(input: List<String>): Int {
        val parsed = parse(input)
        return parsed.sum()
    }

    fun part2(input: List<String>): Long {
        val parsed = parse(input)
        val visitedFrequencies = mutableSetOf(0L)
        var currentFrequency = 0L
        while (true) {
            parsed.forEach {
                currentFrequency += it
                if (currentFrequency in visitedFrequencies) return currentFrequency else visitedFrequencies.add(currentFrequency)
            }
        }
    }
}

fun main() {
    val testInput = """
        +1
        -2
        +3
        +1
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day01.part1(testInput))
    println(Day01.part2(testInput))

    println("------Real------")
    val input = readInput(2018, 1)
    println("Part 1 result: ${Day01.part1(input)}")
    println("Part 2 result: ${Day01.part2(input)}")
    timingStatistics { Day01.part1(input) }
    timingStatistics { Day01.part2(input) }
}