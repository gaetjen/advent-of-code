package y2016

import util.readInput

object Day13 {
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
    println(Day13.part1(testInput))
    println(Day13.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2016/day13")
    println(Day13.part1(input))
    println(Day13.part2(input))
}