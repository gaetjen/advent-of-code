package y2018

import util.Pos
import util.neighborsManhattan
import util.readInput
import util.timingStatistics
import y2022.Day15.manhattanDist

object Day06 {
    private fun parse(input: List<String>): List<Pos> {
        return input.map { line ->
            line.split(", ").map { it.toInt() }.let {
                it.first() to it.last()
            }
        }
    }

    fun part1(input: List<String>): Int {
        val parsed = parse(input)
        val allCoordinates = (parsed.minOf { it.first }..parsed.maxOf { it.first }).map { x ->
            (parsed.minOf { it.second }..parsed.maxOf { it.second }).map { x to it }
        }
        val edges =
            (allCoordinates.first() + allCoordinates.last() + allCoordinates.map { it.first() } + allCoordinates.map { it.last() }).toSet()
        val totality = allCoordinates.flatten().toMutableSet()
        totality.removeAll(parsed.toSet())
        val areas = parsed.map { mutableSetOf(it) }
        val frontiers = areas.toList()
        while (totality.isNotEmpty()) {
            val newFrontiers = frontiers.map { frontier ->
                frontier.flatMap { it.neighborsManhattan() }.toSet() intersect totality
            }
            val allNew = mutableSetOf<Pos>()
            val ties = mutableSetOf<Pos>()
            newFrontiers.forEach {
                ties.addAll(allNew intersect it)
                allNew.addAll(it)
            }
            totality.removeAll(allNew)
            areas.zip(newFrontiers) { area, new ->
                area.addAll(new - ties)
            }
        }

        return areas.filter { it.intersect(edges).isEmpty() }.maxOf { it.size }
    }

    fun part2(input: List<String>): Int {
        val parsed = parse(input)
        val allCoordinates = (parsed.minOf { it.first }..parsed.maxOf { it.first }).map { x ->
            (parsed.minOf { it.second }..parsed.maxOf { it.second }).map { x to it }
        }.flatten().toMutableSet()
        return allCoordinates.filter { thing ->
            parsed.sumOf { it.manhattanDist(thing)  } < 10_000
        }.size
    }
}

fun main() {
    val testInput = """
        1, 1
        1, 6
        8, 3
        3, 4
        5, 5
        8, 9
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day06.part1(testInput))
    println(Day06.part2(testInput))

    println("------Real------")
    val input = readInput(2018, 6)
    println("Part 1 result: ${Day06.part1(input)}")
    println("Part 2 result: ${Day06.part2(input)}")
    timingStatistics { Day06.part1(input) }
    timingStatistics { Day06.part2(input) }
}