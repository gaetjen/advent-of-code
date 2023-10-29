package y2022

import util.Cardinal
import util.Cardinal.*
import util.Pos
import util.minMax
import util.readInput
import java.util.Collections

object Day23 {
    private fun parse(input: List<String>): Set<Pos> {
        return input
            .mapIndexed { row, s ->
                s.mapIndexed { col, c ->
                    if (c == '#') row to col else null
                }
            }.flatten().filterNotNull()
            .toSet()
    }

    val dirSequence = sequence {
        val dirs = Cardinal.values().toList()
        while (true) {
            yield(dirs.toList())
            Collections.rotate(dirs, -1)
        }
    }

    fun Pos.neighbors(): List<Pos> {
        return Cardinal.values().map { it.of(this) } + Cardinal.diagonals.map { (one, two) -> one.of(two.of(this)) }
    }

    fun Pos.neighborsIn(d: Cardinal): List<Pos> {
        val ortho = d.of(this)
        return when (d) {
            NORTH, SOUTH -> listOf(ortho, EAST.of(ortho), WEST.of(ortho))
            WEST, EAST -> listOf(ortho, NORTH.of(ortho), SOUTH.of(ortho))
        }
    }

    fun part1(input: List<String>): Int {
        var elfPositions = parse(input)
        val iter = dirSequence.iterator()
        repeat(10) {
            val newPositions = moveElvesOnce(iter, elfPositions)
            if (newPositions.size != elfPositions.size) error("number of elves changed! ${elfPositions.size} -> ${newPositions.size}")
            elfPositions = newPositions
        }
        val (minP, maxP) = minMax(elfPositions)
        return (maxP.first - minP.first + 1) * (maxP.second - minP.second + 1) - elfPositions.size
    }

    private fun moveElvesOnce(
        iter: Iterator<List<Cardinal>>,
        elfPositions: Set<Pos>,
    ): Set<Pos> {
        val dirs = iter.next()
        val (stable, unstable) = elfPositions.partition { it.neighbors().all { n -> n !in elfPositions } }
        val proposes = unstable.groupBy { p ->
            dirs.firstOrNull { d ->
                p.neighborsIn(d).all { it !in elfPositions }
            }?.of(p)
        }
        val blocked = proposes[null] ?: listOf()
        val (moved, conflict) = proposes.keys.filterNotNull().partition { proposes[it]?.size == 1 }
        val conflicted = conflict.flatMap { proposes[it] ?: listOf() }
        return (stable + blocked + moved + conflicted).toSet()
    }

    fun part2(input: List<String>): Int {
        var elfPositions = parse(input)
        val iter = dirSequence.iterator()
        var roundNumber = 1
        while (true) {
            val newPositions = moveElvesOnce(iter, elfPositions)
            if (newPositions == elfPositions) {
                return roundNumber
            } else {
                elfPositions = newPositions
                roundNumber++
            }
        }
    }
}

fun main() {
    val testInput = """
        ....#..
        ..###.#
        #...#.#
        .#...##
        #.###..
        ##.#.##
        .#..#..
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day23.part1(testInput))
    println(Day23.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2022/day23")
    println(Day23.part1(input))
    println(Day23.part2(input))
}
