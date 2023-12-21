package y2023

import util.Pos
import util.neighborsManhattan
import util.readInput
import util.timingStatistics

object Day21 {
    private fun parse(input: List<String>): Pair<Set<Pair<Int, Int>>, Pair<Int, Int>> {
        var start = 0 to 0
        return input.flatMapIndexed { row, line ->
            line.mapIndexedNotNull { col, char ->
                if (char == 'S') {
                    start = row to col
                }
                if (char in ".S") {
                    row to col
                } else {
                    null
                }
            }
        }.toSet() to start
    }

    fun part1(input: List<String>, steps: Int): Int {
        val (positions, start) = parse(input)
        var frontier = setOf(start)
        repeat(steps) {
            frontier = frontier.flatMap { pos ->
                pos.neighborsManhattan()
            }.filter {
                it in positions
            }.toSet()
        }
        return frontier.size
    }

    fun part2(input: List<String>, steps: Long): Long {
        val (positions, start) = parse(input)
        val tileSize = positions.maxOf { it.first } + 1 to positions.maxOf { it.second } + 1
        val garden = Garden(reachablePositions(positions, start), tileSize)
        val (odds, evens) = garden.oddEvenTilePositions(start)
        val fullyCoveredTilesInOneDirection = ((start.first + steps) / tileSize.first)
        val smallCornersPerSide = fullyCoveredTilesInOneDirection
        val bigCornersPerSide = fullyCoveredTilesInOneDirection - 1
        val lastTilePenetration = (start.first + steps) % tileSize.first
        check(tileSize.first % 2 == 1) { "only works for odd tile sizes" }
        check(tileSize.first == tileSize.second) { "only works for square tiles" }
        check(start.first == start.second && start.first == tileSize.first / 2) { "only works for start in the middle of the garden" }
        val (ringAreasWithStart, ringAreasWithoutStart) = manhattanRingAreas(fullyCoveredTilesInOneDirection)
        val (withStartTiles, withoutStartTiles) = if (steps % 2L == 1L) {
            odds to evens
        } else {
            evens to odds
        }
        val fullyCoveredTiles = ringAreasWithStart * withStartTiles + ringAreasWithoutStart * withoutStartTiles
        val pointCornerTiles = garden.pointCorners(start, lastTilePenetration)
        val pointCornerNumbers = pointCornerTiles.sumOf { it.size }
        val cornersStarts = listOf(
            0 to 0,
            0 to tileSize.second - 1,
            tileSize.first - 1 to tileSize.second - 1,
            tileSize.first - 1 to 0
        )
        val bigCornerTiles = cornersStarts.map {
            garden.reachableFromIn(it, lastTilePenetration + lastTilePenetration / 2)
        }
        val smallCornerTiles = cornersStarts.map {
            garden.reachableFromIn(it, lastTilePenetration / 2 - 1)
        }
        val bigCornerNumbers = bigCornerTiles.sumOf { it.size } * bigCornersPerSide
        val smallCornerNumbers = smallCornerTiles.sumOf { it.size } * smallCornersPerSide
        return fullyCoveredTiles + pointCornerNumbers + bigCornerNumbers + smallCornerNumbers
    }

    private fun reachablePositions(positions: Set<Pos>, start: Pos): Set<Pos> {
        var frontier = setOf(start)
        val visited = mutableSetOf(start)
        while (frontier.isNotEmpty()) {
            frontier = frontier.flatMap { pos ->
                pos.neighborsManhattan()
            }.filter {
                it in positions && it !in visited
            }.toSet()
            visited.addAll(frontier)
        }
        return visited
    }

    private fun manhattanRingAreas(radius: Long): Pair<Long, Long> {
        if (radius <= 1) {
            return 1L to 0L
        }
        return if (radius % 2 == 0L) {
            (radius - 1) * (radius - 1) to (radius * radius)
        } else {
            (radius * radius) to ((radius - 1) * (radius - 1))
        }
    }

    data class Garden(
        val positions: Set<Pos>,
        val tileSize: Pos,
    ) {
        fun oddEvenTilePositions(start: Pos): Pair<Int, Int> {
            val evens = positions.count {
                (it.first + it.second) % 2 == (start.first + start.second) % 2
            }
            val odds = positions.size - evens
            return odds to evens
        }

        fun pointCorners(start: Pos, steps: Long): List<Set<Pos>> {
            val starts = listOf(
                0 to start.second,
                start.first to tileSize.second - 1,
                tileSize.first - 1 to start.second,
                start.first to 0
            )
            return starts.map {
                reachableFromIn(it, steps)
            }
        }

        fun reachableFromIn(start: Pos, steps: Long): Set<Pos> {
            var frontier = setOf(start)
            repeat(steps.toInt()) {
                frontier = frontier.flatMap { pos ->
                    pos.neighborsManhattan()
                }.filter {
                    it in positions
                }.toSet()
            }
            return frontier
        }
    }
}

fun main() {
    val testInput = """
        ...........
        .....###.#.
        .###.##..#.
        ..#.#...#..
        ....#.#....
        .##..S####.
        .##..#...#.
        .......##..
        .##.#.####.
        .##..##.##.
        ...........
    """.trimIndent().split("\n")
    val testSteps = 6
    val testInput2 = """
        .....
        .....
        ..S..
        .....
        .....
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day21.part1(testInput, testSteps))
    listOf(7, 12, 17).forEach {
        println(Day21.part2(testInput2, it.toLong()))
    }

    // too low: 617555274788215
    // too low: 617561397599903
    println("------Real------")
    val input = readInput(2023, 21)
    val steps = 64
    val steps2 = 26501365L
    println("Part 1 result: ${Day21.part1(input, steps)}")
    println("Part 2 result: ${Day21.part2(input, steps2)}")
    timingStatistics { Day21.part1(input, steps) }
    timingStatistics { Day21.part2(input, steps2) }
}
