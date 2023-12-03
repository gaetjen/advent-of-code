package y2023

import util.measuredTime
import util.product
import util.readInput

object Day02 {
    private fun parse(input: List<String>): List<List<Map<String, Int>>> {
        return input.map { line ->
            line.substringAfter(": ").split("; ").map { game ->
                game.split(", ").associate { colorCount ->
                    val (count, color) = colorCount.split(" ")
                    color to count.toInt()
                }
            }
        }
    }

    fun part1(input: List<String>, target: Map<String, Int>): Int {
        val games = parse(input)
        val maxGameResults = games.map { game ->
            target.keys.associateWith { color -> game.maxOf { it[color] ?: 0 } }
        }
        return maxGameResults
            .mapIndexedNotNull { index, maxes ->
                if (target.all { (color, count) ->
                        (maxes[color] ?: 0) <= count
                    }) {
                    index + 1
                } else {
                    null
                }
            }.sum()
    }

    private val colors = listOf("red", "green", "blue")

    fun part2(input: List<String>): Int {
        val games = parse(input)
        val maxGameResults = games.map { game ->
            colors.associateWith { color -> game.maxOf { it[color] ?: 0 } }
        }
        return maxGameResults.sumOf { maxes ->
            maxes.values.product()
        }
    }
}

fun main() {
    val target = mapOf("red" to 12, "green" to 13, "blue" to 14)
    val testInput = """
        Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
        Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
        Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
        Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
        Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day02.part1(testInput, target))
    println(Day02.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2023/day02")
    measuredTime { Day02.part1(input, target) }
    measuredTime { Day02.part2(input) }
}