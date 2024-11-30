package y2018

import util.readInput
import util.timingStatistics

data class Tree(
    val children: List<Tree>,
    val metaData: List<Int>
) {
    companion object {
        fun fromList(list: List<Int>): Tree {
            return if (list.first() == 0) {
                Tree(listOf(), list.drop(2))
            } else {
                TODO()
            }
        }
    }
}

object Day08 {
    private fun parse(input: List<String>): Any {
        val inputNumbers = input.first().split(" ").map { it.toInt() }

        return Unit
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
        2 3 0 3 10 11 12 1 1 0 1 99 2 1 1 2
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day08.part1(testInput))
    println(Day08.part2(testInput))

    println("------Real------")
    val input = readInput(2018, 8)
    println("Part 1 result: ${Day08.part1(input)}")
    println("Part 2 result: ${Day08.part2(input)}")
    timingStatistics { Day08.part1(input) }
    timingStatistics { Day08.part2(input) }
}