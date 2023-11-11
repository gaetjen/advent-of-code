package y2016

import util.Cardinal
import util.Turn
import util.plus
import util.readInput
import util.times
import kotlin.math.abs

object Day01 {
    private fun parse(input: List<String>): List<Pair<Turn, Int>> {
        return input.first().split(", ").map {
            Turn.fromChar(it.first()) to it.drop(1).toInt()
        }
    }

    fun part1(input: List<String>): Int {
        val directions = parse(input)
        var currentPos = 0 to 0
        var currentDirection = Cardinal.NORTH
        directions.forEach {
            currentDirection = currentDirection.turn(it.first)
            currentPos += (currentDirection.relativePos * it.second)
        }
        return abs(currentPos.first) + abs(currentPos.second)
    }

    fun part2(input: List<String>): Int {
        val directions = parse(input)
        var currentPos = 0 to 0
        var currentDirection = Cardinal.NORTH
        val visited = mutableSetOf(currentPos)
        directions.forEach {
            currentDirection = currentDirection.turn(it.first)
            repeat(it.second) {
                currentPos = currentDirection.of(currentPos)
                if (!visited.add(currentPos)) {
                    return abs(currentPos.first) + abs(currentPos.second)
                }
            }
        }
        error("missed crossing")
    }
}

fun main() {
    val testInput = """
        R8, R4, R4, R8
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day01.part1(testInput))
    println(Day01.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2016/day01")
    println(Day01.part1(input))
    println(Day01.part2(input))
}