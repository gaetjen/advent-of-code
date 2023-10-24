package y2015

import util.readInput

object Day01 {
    private fun parse(input: List<String>): String {
        return input.first()
    }

    fun part1(input: List<String>): Long {
        val parsed = parse(input)
        return parsed.count { it == '(' }.toLong() - parsed.count { it == ')' }.toLong()
    }

    fun part2(input: List<String>): Long {

        val parsed = parse(input)
        return parsed.scan(0L) { acc, c ->
            if (c == '(') acc + 1 else acc - 1
        }.indexOfFirst { it == -1L }.toLong()
    }
}

fun main() {
    val testInput = """
        ))(((((
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day01.part1(testInput))
    println(Day01.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2015/day01")
    println(Day01.part1(input))
    println(Day01.part2(input))
}
