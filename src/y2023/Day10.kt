package y2023

import util.Cardinal
import util.Pos
import util.neighborsManhattan
import util.plus
import util.printGrid
import util.readInput
import util.timingStatistics
import y2023.Day10.EnclosedState.INSIDE
import y2023.Day10.EnclosedState.ON_NORTH_IN
import y2023.Day10.EnclosedState.ON_NORTH_OUT
import y2023.Day10.EnclosedState.ON_SOUTH_IN
import y2023.Day10.EnclosedState.ON_SOUTH_OUT
import y2023.Day10.EnclosedState.OUTSIDE
import java.io.File

object Day10 {
    val WHITE_BACKGROUND = "\u001b[47m"
    val BLACK = "\u001b[0;30m"
    val RESET = "\u001B[0m"


    val prettyPrinting = mapOf(
        'L' to '└',
        '-' to '─',
        'J' to '┘',
        '|' to '│',
        'F' to '┌',
        '7' to '┐'
    )

    val neighborLookup = mapOf(
        'L' to listOf(Cardinal.NORTH, Cardinal.EAST),
        '-' to listOf(Cardinal.EAST, Cardinal.WEST),
        'J' to listOf(Cardinal.NORTH, Cardinal.WEST),
        '|' to listOf(Cardinal.NORTH, Cardinal.SOUTH),
        'F' to listOf(Cardinal.SOUTH, Cardinal.EAST),
        '7' to listOf(Cardinal.SOUTH, Cardinal.WEST)
    )

    val reverseNeighborLookup = neighborLookup.map { (k, v) ->
        v.toSet() to k
    }.toMap()

    data class PipeSection(
        val pos: Pos,
        val char: Char,
    ) {
        val prettyChar = prettyPrinting[char] ?: char
        val neighborDirections = neighborLookup[char] ?: listOf()
    }

    enum class EnclosedState {
        OUTSIDE, ON_NORTH_OUT, ON_SOUTH_OUT, ON_NORTH_IN, ON_SOUTH_IN, INSIDE
    }

    val stateTransitionLookup = mapOf(
        OUTSIDE to mapOf(
            '└' to ON_NORTH_OUT,
            '│' to INSIDE,
            '┌' to ON_SOUTH_OUT,
        ),
        INSIDE to mapOf(
            '└' to ON_NORTH_IN,
            '│' to OUTSIDE,
            '┌' to ON_SOUTH_IN,
        ),
        ON_NORTH_OUT to mapOf(
            '─' to ON_NORTH_OUT,
            '┘' to OUTSIDE,
            '┐' to INSIDE,
        ),
        ON_SOUTH_OUT to mapOf(
            '─' to ON_SOUTH_OUT,
            '┘' to INSIDE,
            '┐' to OUTSIDE,
        ),
        ON_NORTH_IN to mapOf(
            '─' to ON_NORTH_IN,
            '┘' to INSIDE,
            '┐' to OUTSIDE,
        ),
        ON_SOUTH_IN to mapOf(
            '─' to ON_SOUTH_IN,
            '┘' to OUTSIDE,
            '┐' to INSIDE,
        ),
    )

    private fun parse(input: List<String>): List<PipeSection> {
        return input.mapIndexed { rowIdx, line ->
            line.mapIndexedNotNull { colIdx, char ->
                if (char == '.') null else PipeSection(Pos(rowIdx, colIdx), char)
            }
        }.flatten()
    }

    fun part1(input: List<String>): Int {
        val pipeSections = parse(input)
        val completePipe = sectionsToPipeLoop(pipeSections)
        return completePipe.size / 2
    }

    private fun sectionsToPipeLoop(pipeSections: List<PipeSection>): List<PipeSection> {
        val pipeLookup = pipeSections.associateBy { it.pos }
        val start = pipeSections.first { it.char == 'S' }
        val startNeighbors = start.pos.neighborsManhattan().mapNotNull {
            pipeLookup[it]
        }.filter { candidate ->
            pipeLookup[candidate.pos]!!.neighborDirections.any { direction ->
                pipeLookup[direction.of(candidate.pos)]?.char == 'S'
            }
        }
        val anonymousStart = getAnonymousStart(start, startNeighbors.first(), startNeighbors.last())
        val completePipe = getCompletePipe(startNeighbors, anonymousStart, pipeLookup + (anonymousStart.pos to anonymousStart))
        return completePipe
    }

    private fun getAnonymousStart(start: PipeSection, n1: PipeSection, n2: PipeSection): PipeSection {
        val startNeighborDirections = Cardinal.entries.filter { it.of(start.pos) in listOf(n1.pos, n2.pos) }.toSet()
        return PipeSection(
            start.pos,
            reverseNeighborLookup[startNeighborDirections] ?: error("Invalid start neighbors for $start: $startNeighborDirections")
        )
    }

    private fun getCompletePipe(
        startNeighbors: List<PipeSection>,
        start: PipeSection,
        pipeLookup: Map<Pos, PipeSection>
    ): List<PipeSection> {
        return buildList {
            add(startNeighbors.first())
            add(start)
            add(startNeighbors.last())
            var current = this.last()
            while (true) {
                val newNeighbors = current.neighborDirections.mapNotNull { pipeLookup[it.of(current.pos)] }
                current = newNeighbors.first { it != this[this.size - 2] }
                if (current != this.first()) {
                    add(current)
                } else {
                    break
                }
            }
        }
    }

    fun printPipe(pipe: List<PipeSection>, others: Set<Pos> = setOf()) {
        printGrid(pipe.associate {
            it.pos to "$BLACK$WHITE_BACKGROUND${it.prettyChar}$RESET"
        } + others.associateWith { "." })
    }

    fun part2(input: List<String>, withPrint: Boolean = false): Int {
        val pipeSections = parse(input)
        val completePipe = sectionsToPipeLoop(pipeSections)
        val completePipeLookup = completePipe.associateBy { it.pos }
        val startRow = completePipe.minOf { it.pos.first }
        val startCol = completePipe.minOf { it.pos.second }
        val endRow = completePipe.maxOf { it.pos.first } + 1
        val endCol = completePipe.maxOf { it.pos.second } + 1
        val start = startRow to startCol
        val outsides = mutableSetOf(start)
        var frontier = setOf(start)
        while (frontier.isNotEmpty()) {
            frontier = frontier.flatMap { pos ->
                cornerNeighbors(pos, completePipeLookup).map { it.of(pos) }
            }.filter {
                it.first in startRow..endRow && it.second in startCol..endCol
            }.filter {
                it !in outsides
            }.toSet()
            outsides.addAll(frontier)
        }
        outsides.removeIf { it in completePipeLookup.keys }
        if (withPrint) {
            printPipe(completePipe, outsides)
        }
        val total = (endRow - startRow + 1) * (endCol - startCol + 1)
        return total - outsides.size - completePipe.size
    }

    fun cornerNeighbors(pos: Pos, pipe: Map<Pos, PipeSection>): List<Cardinal> {
        val southEastNeighbors = pipe[pos]?.neighborDirections ?: listOf()
        val northWestNeighbors = pipe[pos + (-1 to -1)]?.neighborDirections ?: listOf()
        return listOfNotNull(
            if (Cardinal.WEST !in southEastNeighbors) Cardinal.SOUTH else null,
            if (Cardinal.NORTH !in southEastNeighbors) Cardinal.EAST else null,
            if (Cardinal.EAST !in northWestNeighbors) Cardinal.NORTH else null,
            if (Cardinal.SOUTH !in northWestNeighbors) Cardinal.WEST else null,
        )
    }

    fun part2Iterative(input: List<String>, withPrint: Boolean = false): Int {
        val pipeSections = parse(input)
        val completePipe = sectionsToPipeLoop(pipeSections)
        val completePipeLookup = completePipe.associateBy { it.pos }
        val insides = getInsides(completePipe, completePipeLookup, withPrint)
        return insides.size
    }

    private fun getInsides(
        completePipe: List<PipeSection>,
        completePipeLookup: Map<Pos, PipeSection>,
        withPrint: Boolean
    ): MutableSet<Pos> {
        val startRow = completePipe.minOf { it.pos.first }
        val startCol = completePipe.minOf { it.pos.second }
        val endRow = completePipe.maxOf { it.pos.first }
        val endCol = completePipe.maxOf { it.pos.second }
        val insides = mutableSetOf<Pos>()
        (startRow..endRow).forEach { row ->
            var state = OUTSIDE
            (startCol..endCol).forEach { col ->
                val next = completePipeLookup[row to col]
                if (next == null && state == INSIDE) {
                    insides.add(row to col)
                } else if (next != null) {
                    state = stateTransitionLookup[state]!![next.prettyChar]!!
                }
            }
        }
        if (withPrint) {
            printPipe(completePipe, insides)
        }
        return insides
    }

    fun writeSolutionInfo(input: List<String>) {
        val pipeSections = parse(input)
        val completePipe = sectionsToPipeLoop(pipeSections)
        val completePipeLookup = completePipe.associateBy { it.pos }
        val partIndices = prettyPrinting.keys.associateWith { prettyPrinting.keys.indexOf(it) + 1 } + mapOf(
            'S' to prettyPrinting.keys.size * 2 + 1,
            '.' to 0
        )
        println(partIndices)
        val str = input.mapIndexed { row, line ->
            line.mapIndexed { col, c ->
                val partIdx = partIndices[c] ?: error("Invalid char $c")
                if (row to col in completePipeLookup) {
                    partIdx + prettyPrinting.keys.size
                } else {
                    partIdx
                }
            }.joinToString(prefix = "[", postfix = "]")
        }.joinToString(prefix = "[", postfix = "]")
        File("src", "resources/util/2023-day10.json").writeText(str)
    }
}

fun main() {
    val testInput = """
        ..F7.
        .FJ|.
        SJ.L7
        |F--J
        LJ...
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day10.part1(testInput))
    println(Day10.part2(testInput))

    println("------Real------")
    val input = readInput(2023, 10)
    println("Part 1 result: ${Day10.part1(input)}")
    println("Part 2 result: ${Day10.part2(input, withPrint = true)}")
    println("Part 2 result iterative: ${Day10.part2Iterative(input, withPrint = true)}")
    timingStatistics { Day10.part1(input) }
    timingStatistics { Day10.part2(input) }
    timingStatistics { Day10.part2Iterative(input) }
    Day10.writeSolutionInfo(input)
}