package y2023

import util.measuredTime
import util.readInput

object Day21 {
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
    println(Day21.part1(testInput))
    println(Day21.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2023/day21")
    measuredTime { Day21.part1(input) }
    measuredTime { Day21.part2(input) }
}