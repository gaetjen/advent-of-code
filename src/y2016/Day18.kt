package y2016

import util.readInput

object Day18 {
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
    println(Day18.part1(testInput))
    println(Day18.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2016/day18")
    println(Day18.part1(input))
    println(Day18.part2(input))
}