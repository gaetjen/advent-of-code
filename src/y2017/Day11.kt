package y2017

import util.Pos
import util.plus
import util.readInput
import util.timingStatistics
import kotlin.math.max
import kotlin.math.min

enum class HexDirection(private val relativePos: Pos) {
    NORTH(-1 to 0),
    NORTHEAST(0 to 1),
    SOUTHEAST(1 to 1),
    SOUTH(1 to 0),
    SOUTHWEST(0 to -1),
    NORTHWEST(-1 to -1);

    fun of(pos: Pair<Int, Int>): Pair<Int, Int> {
        return pos + relativePos
    }

    companion object {
        fun fromString(str: String): HexDirection {
            return when (str.lowercase()) {
                "n" -> NORTH
                "ne" -> NORTHEAST
                "se" -> SOUTHEAST
                "s" -> SOUTH
                "sw" -> SOUTHWEST
                "nw" -> NORTHWEST
                else -> throw IllegalArgumentException("Unknown hex direction: $str")
            }
        }
    }
}

fun Pos.hexNeighbors(): List<Pos> = HexDirection.entries.map { it.of(this) }

object Day11 {
    private fun parse(input: List<String>): List<HexDirection> {
        return input.first().split(",").map { HexDirection.fromString(it) }
    }

    fun part1(input: List<String>): Int {
        val directions = parse(input)
        val endPos = directions.fold(0 to 0) { acc, d -> d.of(acc) }
        return distanceFromOrigin(endPos)

    }

    private fun distanceFromOrigin(pos: Pos): Int {
        val (x, y) = pos
        return when {
            x >= 0 && y >= 0 -> max(x, y)
            x >= 0 && y < 0 -> x - y
            x < 0 && y >= 0 -> y - x
            else -> -min(x, y) // endX < 0 && endY < 0
        }
    }

    fun part2(input: List<String>): Int {
        val directions = parse(input)
        val path = directions.runningFold(0 to 0) {acc, d -> d.of(acc)}
        return path.maxOf { distanceFromOrigin(it) }
    }
}

fun main() {
    val testInput = """
        se,sw,se,sw,sw
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day11.part1(testInput))
    println(Day11.part2(testInput))

    println("------Real------")
    val input = readInput(2017, 11)
    println("Part 1 result: ${Day11.part1(input)}")
    println("Part 2 result: ${Day11.part2(input)}")
    timingStatistics { Day11.part1(input) }
    timingStatistics { Day11.part2(input) }
}