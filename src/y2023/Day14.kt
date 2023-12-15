package y2023

import util.Cardinal
import util.Pos
import util.get
import util.readInput
import util.set
import util.timingStatistics
import util.transpose

object Day14 {
    enum class TileType(val c: Char) {
        ROLLER('O'), FIXED('#'), EMPTY('.')
    }

    data class Tile(
        val type: TileType,
        val pos: Pos,
    )

    private fun parse(input: List<String>): MutableList<MutableList<Tile>> {
        return input.mapIndexed { row, s ->
            s.mapIndexedNotNull { col, c ->
                when (c) {
                    '#' -> {
                        Tile(
                            TileType.FIXED,
                            row to col
                        )
                    }

                    'O' -> {
                        Tile(
                            TileType.ROLLER,
                            row to col
                        )
                    }

                    '.' -> {
                        Tile(
                            TileType.EMPTY,
                            row to col
                        )
                    }

                    else -> {
                        null
                    }
                }
            }.toMutableList()
        }.toMutableList()
    }

    fun part1(input: List<String>): Int {
        val rocks = parse(input)
        rocks.flatten().forEach {
            if (it.type == TileType.ROLLER) {
                roll(it.pos, Cardinal.NORTH, rocks)
            }
        }
        return rocks.transpose().sumOf {
            load(it)
        }
    }

    fun load(row: List<Tile>): Int {
        return row.indices
            .reversed()
            .zip(row)
            .filter { it.second.type == TileType.ROLLER }
            .sumOf { it.first + 1 }
    }

    fun Pos.coerce(maxes: Pos): Pos = first.coerceIn(0, maxes.first) to second.coerceIn(0, maxes.second)

    fun roll(pos: Pos, dir: Cardinal, rocks: MutableList<MutableList<Tile>>) {
        val newPos = dir.of(pos).coerce(rocks.size - 1 to rocks[0].size - 1)
        if (rocks[newPos].type != TileType.EMPTY) {
            return
        }
        rocks[newPos] = Tile(TileType.ROLLER, newPos)
        rocks[pos] = Tile(TileType.EMPTY, pos)
        roll(newPos, dir, rocks)
    }


    fun part2(input: List<String>): Int {
        val rocks = parse(input)
        val cycle = listOf(
            Cardinal.NORTH to { it: List<Tile> -> it.sortedBy { it.pos.first } },
            Cardinal.WEST to { it: List<Tile> -> it.sortedBy { it.pos.second } },
            Cardinal.SOUTH to { it: List<Tile> -> it.sortedByDescending { it.pos.first } },
            Cardinal.EAST to { it: List<Tile> -> it.sortedByDescending { it.pos.second } },
        )
        val stateHistory = mutableListOf(
            rocksToString(rocks) to rocks.transpose().sumOf {
                load(it)
            }
        )
        while (stateHistory.last() !in stateHistory.subList(0, stateHistory.size - 1)) {
            cycle.forEach { (dir, sort) ->
                sort(rocks.flatten().filter { it.type == TileType.ROLLER }).forEach {
                    roll(it.pos, dir, rocks)
                }
            }
            stateHistory.add(rocksToString(rocks) to rocks.transpose().sumOf {
                load(it)
            })
        }
        val cycleStart = stateHistory.indexOfFirst { it == stateHistory.last() }
        val cycleLength = stateHistory.size - cycleStart - 1
        val targetIdx = (1_000_000_000 - cycleStart) % cycleLength + cycleStart
        return stateHistory[targetIdx].second
    }

    private fun rocksToString(rocks: MutableList<MutableList<Tile>>) = rocks.joinToString("\n") { row ->
        row.joinToString("") { it.type.c.toString() }
    }
}

fun main() {
    val testInput = """
        O....#....
        O.OO#....#
        .....##...
        OO.#O....O
        .O.....O#.
        O.#..O.#.#
        ..O..#O..O
        .......O..
        #....###..
        #OO..#....
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day14.part1(testInput))
    println(Day14.part2(testInput))

    println("------Real------")
    val input = readInput(2023, 14)
    println("Part 1 result: ${Day14.part1(input)}")
    println("Part 2 result: ${Day14.part2(input)}")
    timingStatistics { Day14.part1(input) }
    timingStatistics { Day14.part2(input) }
}