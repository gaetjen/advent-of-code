package y2024

import util.Pos
import util.get
import util.inGridSize
import util.neighborsManhattan
import util.readInput
import util.timingStatistics

object Day10 {
    private fun parse(input: List<String>): List<List<Int>> {
        return input.map { line ->
            line.map { it.digitToInt() }
        }
    }

    fun part1(input: List<String>): Int {
        val map = parse(input)
        val trailheads = map.withIndex().flatMap { (rowIdx, row) -> row.withIndex().filter { it.value == 0 }.map { rowIdx to it.index } }
        return trailheads.sumOf {
            destinations(it, map).distinct().size
        }
    }

    private fun destinations(
        trailhead: Pos,
        map: List<List<Int>>
    ): List<Pos> {
        var frontier = listOf(trailhead)
        (1..9).forEach { height ->
            frontier = frontier.flatMap { it.neighborsManhattan() }
                .filter { it.inGridSize(map.size to map.first().size) }
                .filter { map[it] == height }
        }
        return frontier
    }

    fun part2(input: List<String>): Int {
        val map = parse(input)
        val trailheads = map.withIndex().flatMap { (rowIdx, row) -> row.withIndex().filter { it.value == 0 }.map { rowIdx to it.index } }
        return trailheads.sumOf {
            destinations(it, map).size
        }
    }
}

fun main() {
    val testInput = """
        89010123
        78121874
        87430965
        96549874
        45678903
        32019012
        01329801
        10456732
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day10.part1(testInput))
    println(Day10.part2(testInput))

    println("------Real------")
    val input = readInput(2024, 10)
    println("Part 1 result: ${Day10.part1(input)}")
    println("Part 2 result: ${Day10.part2(input)}")
    timingStatistics { Day10.part1(input) }
    timingStatistics { Day10.part2(input) }
}