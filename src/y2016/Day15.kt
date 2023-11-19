package y2016

import util.readInput

object Day15 {
    private fun parse(input: List<String>): List<Pair<Int, Int>> {
        return input.map {
            val els = it.split(' ')
            els[3].toInt() to els.last().dropLast(1).toInt()
        }
    }

    fun part1(input: List<String>): Int {
        val initialState = parse(input)
        val (divisors, remainders) = initialState.mapIndexed { idx, (divisor, rest) ->
            divisor to (divisor - rest - idx - 1).mod(divisor)
        }.unzip()
        var result = 0
        while (true) {
            if (divisors.map { result % it } == remainders) {
                return result
            }
            result++
        }
    }

    fun part2(input: List<String>): Int {
        val initialState = parse(input) + listOf(11 to 0)
        val (divisors, remainders) = initialState.mapIndexed { idx, (divisor, rest) ->
            divisor to (divisor - rest - idx - 1).mod(divisor)
        }.unzip()
        var result = 0
        while (true) {
            if (divisors.map { result % it } == remainders) {
                return result
            }
            result++
        }
    }
}

fun main() {
    val testInput = """
        Disc #1 has 5 positions; at time=0, it is at position 4.
        Disc #2 has 2 positions; at time=0, it is at position 1.
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day15.part1(testInput))
    println(Day15.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2016/day15")
    println(Day15.part1(input))
    println(Day15.part2(input))
}