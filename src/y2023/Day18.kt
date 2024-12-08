package y2023

import util.Cardinal
import util.neighborsManhattan
import util.readInput
import util.times
import util.timingStatistics
import util.toLong

object Day18 {
    data class DigInstruction(
        val direction: Cardinal,
        val distance: Long
    )

    val dirMap = mapOf(
        'R' to Cardinal.EAST,
        'L' to Cardinal.WEST,
        'U' to Cardinal.NORTH,
        'D' to Cardinal.SOUTH
    )

    private fun parse(input: List<String>): List<DigInstruction> {
        return input.map { line ->
            val (dir, dist, _) = line.split(' ')
            DigInstruction(
                direction = dirMap[dir.first()]!!,
                distance = dist.toLong()
            )
        }
    }

    fun part1(input: List<String>): Int {
        val parsed = parse(input)
        val start = 0 to 0
        val edges = mutableListOf(start)
        parsed.forEach { digInstruction ->
            repeat(digInstruction.distance.toInt()) {
                edges.add(
                    digInstruction.direction.of(edges.last())
                )
            }
        }
        val startRow = edges.minOf { it.first } - 1
        val endRow = edges.maxOf { it.first } + 1
        val startCol = edges.minOf { it.second } - 1
        val endCol = edges.maxOf { it.second } + 1
        val outsides = mutableSetOf(startRow to startCol)
        var frontier = setOf(start)
        while (frontier.isNotEmpty()) {
            frontier = frontier.flatMap { pos ->
                pos.neighborsManhattan()
            }.filter {
                it.first in startRow..endRow && it.second in startCol..endCol
            }.filter {
                it !in outsides && it !in edges
            }.toSet()
            outsides.addAll(frontier)
        }
        val totalSize = (endRow - startRow + 1) * (endCol - startCol + 1)
        return totalSize - outsides.size
    }

    val dirMap2 = mapOf(
        '0' to Cardinal.EAST,
        '1' to Cardinal.SOUTH,
        '2' to Cardinal.WEST,
        '3' to Cardinal.NORTH
    )

    private fun parse2(input: List<String>): List<DigInstruction> {
        return input.map { line ->
            val (_, _, color) = line.split(' ')
            val dist = color.drop(2).take(5)
            DigInstruction(
                direction = dirMap2[color[7]]!!,
                distance = dist.toLong(16)
            )
        }
    }

    fun part2(input: List<String>): Long {
        val parsed = parse2(input)
        val start = 0L to 0L
        val corners = mutableListOf(start)
        parsed.forEach { digInstruction ->
            corners.add(
                // the minus is actually a plus, because of platform declaration clash
                (digInstruction.direction.relativePos.toLong() * digInstruction.distance) * corners.last()
            )
        }
        val correctedCorners = (listOf(parsed.last()) + parsed).zipWithNext().zip(corners).map { (digs, pos) ->
            if (digs.second.direction == Cardinal.NORTH ||
                digs.first.direction == Cardinal.NORTH
            ) {
                pos * (0L to -1L)
            } else {
                pos
            }
        }
        check(correctedCorners.size == parsed.size)
        val cornerColLookup = correctedCorners.groupBy { it.second }
        val startCol = correctedCorners.minOf { it.second }

        var activeRanges = listOf<LongRange>()
        var totalArea = 0L
        var lastCol = startCol
        cornerColLookup.keys.sorted().forEach { colIdx ->
            totalArea += columnArea(activeRanges) * (colIdx - lastCol)
            lastCol = colIdx
            val cornersInColumn = cornerColLookup[colIdx]?.map { it.first }
            if (cornersInColumn != null) {
                val activeCorners = activeRanges.flatMap { range ->
                    listOf(range.first, range.last)
                }
                val allCorners = (cornersInColumn + activeCorners).sorted()
                val duplicates = allCorners.zipWithNext().filter { (first, second) ->
                    first == second
                }.map { it.first }.toSet()
                val noDuplicates = allCorners.filter { it !in duplicates }
                check(noDuplicates.size % 2 == 0) {
                    "no duplicates should be even: \n$allCorners\n$duplicates\n$noDuplicates"
                }

                activeRanges = noDuplicates.chunked(2).map { it.first()..it.last() }
            } else {
                error("no corners in column by key: $colIdx")
            }
        }
        check(activeRanges.isEmpty()) { "active ranges should be empty: $activeRanges, $totalArea" }

        return totalArea
    }

    private fun columnArea(activeRanges: List<LongRange>) = activeRanges.sumOf { it.last - it.first + 1 }
}

fun main() {
    val testInput = """
        R 6 (#70c710)
        D 5 (#0dc571)
        L 2 (#5713f0)
        D 2 (#d2c081)
        R 2 (#59c680)
        D 2 (#411b91)
        L 5 (#8ceee2)
        U 2 (#caa173)
        L 1 (#1b58a2)
        U 2 (#caa171)
        R 2 (#7807d2)
        U 3 (#a77fa3)
        L 2 (#015232)
        U 2 (#7a21e3)
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day18.part1(testInput))
    println(Day18.part2(testInput))

    println("------Real------")
    val input = readInput(2023, 18)
    println("Part 1 result: ${Day18.part1(input)}")
    println("Part 2 result: ${Day18.part2(input)}")
    timingStatistics { Day18.part1(input) }
    timingStatistics { Day18.part2(input) }
}
