package y2023

import util.Cardinal
import util.Pos
import util.neighborsManhattan
import util.printGrid
import util.readInput

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
        '-' to listOf(Cardinal.WEST, Cardinal.EAST),
        'J' to listOf(Cardinal.NORTH, Cardinal.WEST),
        '|' to listOf(Cardinal.NORTH, Cardinal.SOUTH),
        'F' to listOf(Cardinal.SOUTH, Cardinal.EAST),
        '7' to listOf(Cardinal.SOUTH, Cardinal.WEST)
    )


    enum class EnclosedState {
        OUTSIDE, ON, INSIDE
    }

    val stateTransitionLookup = mapOf(
        EnclosedState.OUTSIDE to mapOf(
            '└' to EnclosedState.ON,
            '─' to null,
            '┘' to null,
            '│' to EnclosedState.INSIDE,
            'S' to EnclosedState.INSIDE,
            '┌' to EnclosedState.ON,
            '┐' to null,
        ),
        EnclosedState.ON to mapOf(
            '└' to EnclosedState.ON,
            '─' to EnclosedState.ON,
            '┘' to EnclosedState.OUTSIDE,
            '│' to EnclosedState.OUTSIDE,
            'S' to EnclosedState.OUTSIDE,
            '┌' to null,
            '┐' to EnclosedState.OUTSIDE,
        ),
        EnclosedState.INSIDE to mapOf(
            '└' to EnclosedState.ON,
            '─' to null,
            '┘' to null,
            '│' to EnclosedState.OUTSIDE,
            'S' to EnclosedState.OUTSIDE,
            '┌' to EnclosedState.ON,
            '┐' to null,
        ),
    )

    data class PipeSection(
        val pos: Pos,
        val char: Char,
    ) {
        val prettyChar = prettyPrinting[char] ?: char
        val neighborDirections = neighborLookup[char] ?: listOf()
    }

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
        val completePipe = getCompletePipe(startNeighbors, start, pipeLookup)
        return completePipe
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

    fun printPipe(pipe: List<PipeSection>, insides: Set<Pos> = setOf()) {
        printGrid(pipe.associate {
            it.pos to "$BLACK$WHITE_BACKGROUND${it.prettyChar}$RESET"
        } + insides.associateWith { "I" })
    }

    fun part2(input: List<String>): Int {
        val pipeSections = parse(input)
        val completePipe = sectionsToPipeLoop(pipeSections)
        val completePipeLookup = completePipe.associateBy { it.pos }
        val startRow = completePipe.minOf { it.pos.first }
        val startCol = completePipe.minOf { it.pos.second }
        val endRow = completePipe.maxOf { it.pos.first }
        val endCol = completePipe.maxOf { it.pos.second }
        var totalInside = 0
        val insides = mutableSetOf<Pos>()
        (startRow..endRow).forEach { row ->
            var state = EnclosedState.OUTSIDE
            (startCol..endCol).forEach { col ->
                val next = completePipeLookup[row to col]
                if (next == null && state == EnclosedState.INSIDE) {
                    totalInside++
                    insides.add(row to col)
                } else if (next != null) {
                    state = stateTransitionLookup[state]!![next.prettyChar]!!
                }
            }
        }
        printPipe(completePipe, insides)
        return totalInside
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
    //println(Day10.part2(testInput))

    println("------Real------")
    val input = readInput(2023, 10)
    println("Part 1 result: ${Day10.part1(input)}")
    println("Part 2 result: ${Day10.part2(input)}")
    //timingStatistics { Day10.part1(input) }
    //timingStatistics { Day10.part2(input) }
}