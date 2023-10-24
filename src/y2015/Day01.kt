package y2015

import util.readInput

object Day01 {
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
    println(Day01.part1(testInput))
    println(Day01.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2015/day01")
    println(Day01.part1(input))
    println(Day01.part2(input))
}
