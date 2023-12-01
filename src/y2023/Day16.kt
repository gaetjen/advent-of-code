package y2023

import util.readInput

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
    val input = readInput("resources/2023/day16")
    println(Day16.part1(input))
    println(Day16.part2(input))
}