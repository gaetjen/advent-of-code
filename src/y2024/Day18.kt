package y2024

import util.Pos
import util.inGridSize
import util.neighborsManhattan
import util.readInput
import util.timingStatistics
import java.util.PriorityQueue

object Day18 {
    private fun parse(input: List<String>): List<Pos> {
        return input.map { line ->
            val (x, y) = line.split(",").map { it.toInt() }
            x to y
        }
    }

    fun part1(input: List<String>, maxCoordinate: Int = 70): Int {
        val parsed = parse(input)
        return shortestPath(parsed.take(1024).toSet(), maxCoordinate)
    }

    private fun shortestPath(
        blocked: Set<Pos>,
        maxCoordinate: Int
    ): Int {
        val visited = mutableSetOf(0 to 0)
        val queue = PriorityQueue<Pair<Pos, Int>>(compareBy { it.second })
        queue.add((0 to 0) to 0)
        while (queue.isNotEmpty()) {
            val nextToExpand = queue.poll()!!
            nextToExpand.first.neighborsManhattan().filter { it !in blocked && it.inGridSize(maxCoordinate + 1 to maxCoordinate + 1) }.forEach {
                if (it == maxCoordinate to maxCoordinate) {
                    return nextToExpand.second + 1
                }
                if (it !in visited) {
                    queue.add(it to nextToExpand.second + 1)
                    visited.add(it)
                }
            }
        }
        error("path not found")
    }

    fun part2(input: List<String>): String {
        val parsed = parse(input)
        var curIdx = 0
        try {
            parsed.indices.forEach { i ->
                curIdx = i
                shortestPath(parsed.take(i + 1).toSet(), 70)
            }
        } catch (e: Exception) {
            return input[curIdx]
        }
        return "Not found"
    }
}

fun main() {
    val testInput = """
        5,4
        4,2
        4,5
        3,0
        2,1
        6,3
        2,4
        1,5
        0,6
        3,3
        2,6
        5,1
        1,2
        5,5
        2,5
        6,5
        1,4
        0,4
        6,4
        1,1
        6,1
        1,0
        0,5
        1,6
        2,0
    """.trimIndent().split("\n")
    println("------Tests------")
    //println(Day18.part1(testInput, 6))
    println(Day18.part2(testInput))

    println("------Real------")
    val input = readInput(2024, 18)
    println("Part 1 result: ${Day18.part1(input)}")
    println("Part 2 result: ${Day18.part2(input)}")
    timingStatistics { Day18.part1(input) }
    timingStatistics { Day18.part2(input) }
}