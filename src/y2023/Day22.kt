package y2023

import util.readInput
import util.timingStatistics

object Day22 {
    data class Brick(
        val xRange: IntRange,
        val yRange: IntRange,
        val zRange: IntRange
    ) {
        fun xyOverlap(other: Brick): Boolean {
            return xRange.intersects(other.xRange) && yRange.intersects(other.yRange)
        }
    }

    fun IntRange.intersects(other: IntRange): Boolean {
        return (first in other || last in other || other.first in this || other.last in this)
    }

    private fun parse(input: List<String>): List<Brick> {
        return input.map { line ->
            val (starts, ends) = line.split('~').map { raw -> raw.split(',').map { it.toInt() } }
            Brick(
                starts[0]..ends[0],
                starts[1]..ends[1],
                starts[2]..ends[2]
            )
        }.sortedBy { it.zRange.first }
    }

    fun part1(input: List<String>): Int {
        val bricks = parse(input)
        val fallenBricks = fallenBricks(bricks)
        val disintegratable = fallenBricks.filter { brick ->
            val sameColumns = fallenBricks.filter { it.xyOverlap(brick) }
            val above = sameColumns.filter { brick.zRange.last + 1 == it.zRange.first }
            if (above.isEmpty()) {
                true
            } else {
                above.all { aboveBrick ->
                    val sameColumnsAbove = fallenBricks.filter { it.xyOverlap(aboveBrick) }
                    val below = sameColumnsAbove.filter { aboveBrick.zRange.first - 1 == it.zRange.last }
                    below.size >= 2
                }
            }
        }.toSet()
        return disintegratable.size
    }

    private fun fallenBricks(bricks: List<Brick>): MutableList<Brick> {
        val fallenBricks = mutableListOf<Brick>()
        bricks.forEach { brick ->
            val z = fallenBricks.filter { brick.xyOverlap(it) }.maxOfOrNull { it.zRange.last } ?: 0
            fallenBricks.add(
                Brick(
                    brick.xRange,
                    brick.yRange,
                    z + 1..(z + 1 + brick.zRange.last - brick.zRange.first)
                )
            )
        }
        return fallenBricks
    }

    fun part2(input: List<String>): Int {
        val bricks = parse(input)
        val fallenBricks = fallenBricks(bricks)
        val brickLookupBottom = fallenBricks.groupBy { it.zRange.first }
        val brickLookupTop = fallenBricks.groupBy { it.zRange.last }
        return fallenBricks.sumOf { brick ->
            val allToFall = mutableSetOf<Brick>()
            var top = listOf(brick)
            while (top.isNotEmpty()) {
                top = top.flatMap { current ->
                    val supportedByCurrent = (brickLookupBottom[current.zRange.last + 1] ?: emptyList())
                        .filter { it.xyOverlap(current) }
                    supportedByCurrent.filter { above ->
                        val supportingAbove = (brickLookupTop[above.zRange.first - 1] ?: emptyList())
                            .filter { it.xyOverlap(above) }
                        check(supportingAbove.isNotEmpty()) { "No support for $above" }
                        supportingAbove.all { it in allToFall  || it in top }
                    }
                }
                allToFall.addAll(top)
            }

            allToFall.size
        }
    }
}

fun main() {
    val testInput = """
        1,0,1~1,2,1
        0,0,2~2,0,2
        0,2,3~2,2,3
        0,0,4~0,2,4
        2,0,5~2,2,5
        0,1,6~2,1,6
        1,1,8~1,1,9
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day22.part1(testInput))
    println(Day22.part2(testInput))

    println("------Real------")
    val input = readInput(2023, 22)
    println("Part 1 result: ${Day22.part1(input)}")
    println("Part 2 result: ${Day22.part2(input)}")
    timingStatistics { Day22.part1(input) }
    timingStatistics { Day22.part2(input) }
}