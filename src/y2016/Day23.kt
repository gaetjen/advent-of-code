package y2016

import util.readInput

object Day23 {
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
    println(Day23.part1(testInput))
    println(Day23.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2016/day23")
    println(Day23.part1(input))
    println(Day23.part2(input))
}