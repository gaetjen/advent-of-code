package y2017

import util.readInput
import util.timingStatistics

object Day13 {
    private fun parse(input: List<String>): Map<Int, Int> {
        return input.associate {
            val (layer, depth) = it.split(": ")
            layer.toInt() to depth.toInt()
        }
    }

    fun part1(input: List<String>): Int {
        val wall = parse(input)
        return wall.filter { (layer, depth) ->
            (layer % ((depth - 1) * 2) == 0)
        }.map { (layer, depth) ->
            layer * depth
        }.sum()
    }

    fun part2(input: List<String>): Int {
        val wall = parse(input)
        var result = 0
        while (true) {
            if (wall.all { (layer, depth) -> (layer + result) % ((depth - 1) * 2) != 0 }) {
                return result
            }
            result++
        }
    }
}

fun main() {
    val testInput = """
        0: 3
        1: 2
        4: 4
        6: 4
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day13.part1(testInput))
    println(Day13.part2(testInput))

    println("------Real------")
    val input = readInput(2017, 13)
    println("Part 1 result: ${Day13.part1(input)}")
    println("Part 2 result: ${Day13.part2(input)}")
    timingStatistics { Day13.part1(input) }
    timingStatistics { Day13.part2(input) }
}