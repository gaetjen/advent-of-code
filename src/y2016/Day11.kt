package y2016

import util.readInput

object Day11 {

    fun part1(input: List<String>): Int {
        // items per floor = 4, 5, 1
        val cumulative = listOf(4, 9, 10)
        /**
         * bringing n items up one floor taken 2*n - 3 steps (one up, one down each, minus the last down, and on the last up you can take
         * 2 items)
         *
         * In general we can always move the generators first, we never need to move items back down, except to make sure the elevator
         * operates
         */
        return cumulative.sumOf { it * 2 - 3 }
    }

    fun part2(input: List<String>): Int {
        // items per floor = 8, 5, 1
        val cumulative = listOf(8, 13, 14)
        return cumulative.sumOf { it * 2 - 3 }
    }
}

fun main() {
    val testInput = """
        The first floor contains a hydrogen-compatible microchip and a lithium-compatible microchip.
        The second floor contains a hydrogen generator.
        The third floor contains a lithium generator.
        The fourth floor contains nothing relevant.
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day11.part1(testInput))
    println(Day11.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2016/day11")
    println(Day11.part1(input))
    println(Day11.part2(input))
}