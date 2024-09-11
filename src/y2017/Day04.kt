package y2017

import util.readInput
import util.timingStatistics

object Day04 {
    private fun parse(input: List<String>): List<List<String>> {
        return input.map {
            it.split(" ")
        }
    }

    fun part1(input: List<String>): Long {
        val phrases = parse(input)
        return phrases.count {
            it.distinct().size == it.size
        }.toLong()
    }

    fun part2(input: List<String>): Long {
        val phrases = parse(input).map { words ->
            words.map { w -> w.toCharArray().sorted().toString() }
        }
        return phrases.count {
            it.distinct().size == it.size
        }.toLong()
    }
}

fun main() {
    val testInput = """
        aa bb cc dd ee
        aa bb cc dd aa
        aa bb cc dd aaa
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day04.part1(testInput))
    println(Day04.part2(testInput))

    println("------Real------")
    val input = readInput(2017, 4)
    println("Part 1 result: ${Day04.part1(input)}")
    println("Part 2 result: ${Day04.part2(input)}")
    timingStatistics { Day04.part1(input) }
    timingStatistics { Day04.part2(input) }
}