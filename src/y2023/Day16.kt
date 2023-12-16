package y2023

import util.Cardinal
import util.Pos
import util.readInput
import util.timingStatistics
import y2023.Day14.coerce

object Day16 {
    data class Tile(
        val pos: Pos,
        val type: Type,
    )

    enum class Type(val c: Char) {
        SPLITTER_UP('|'), SPLITTER_FLAT('-'), MIRROR_BACK('\\'), MIRROR_FORWARD('/');

        fun nextDirectionAfter(direction: Cardinal): List<Cardinal> {
            return when (this) {
                SPLITTER_UP -> {
                    when (direction) {
                        Cardinal.NORTH, Cardinal.SOUTH -> listOf(direction)
                        Cardinal.EAST, Cardinal.WEST -> listOf(Cardinal.NORTH, Cardinal.SOUTH)
                    }
                }

                SPLITTER_FLAT -> {
                    when (direction) {
                        Cardinal.NORTH, Cardinal.SOUTH -> listOf(Cardinal.EAST, Cardinal.WEST)
                        Cardinal.EAST, Cardinal.WEST -> listOf(direction)
                    }
                }

                MIRROR_BACK -> {
                    when (direction) {
                        Cardinal.NORTH -> listOf(Cardinal.WEST)
                        Cardinal.SOUTH -> listOf(Cardinal.EAST)
                        Cardinal.EAST -> listOf(Cardinal.SOUTH)
                        Cardinal.WEST -> listOf(Cardinal.NORTH)
                    }
                }

                MIRROR_FORWARD -> {
                    when (direction) {
                        Cardinal.NORTH -> listOf(Cardinal.EAST)
                        Cardinal.SOUTH -> listOf(Cardinal.WEST)
                        Cardinal.EAST -> listOf(Cardinal.NORTH)
                        Cardinal.WEST -> listOf(Cardinal.SOUTH)
                    }
                }

            }
        }
    }

    data class Energization(
        val pos: Pos,
        val direction: Cardinal
    )

    private fun tileType(c: Char): Type {
        return when (c) {
            '-' -> Type.SPLITTER_FLAT
            '|' -> Type.SPLITTER_UP
            '\\' -> Type.MIRROR_BACK
            '/' -> Type.MIRROR_FORWARD
            else -> throw IllegalArgumentException("Unknown tile type: $c")
        }
    }

    private fun parse(input: List<String>): List<Tile> {
        return input.flatMapIndexed { row, line ->
            line.mapIndexedNotNull { col, c ->
                if (c == '.') null else Tile(
                    pos = Pos(row, col),
                    type = tileType(c)
                )
            }
        }
    }

    fun part1(input: List<String>): Long {
        val parsed = parse(input)
        val dimensions = input.size - 1 to input.first().length - 1
        val tilesByPos = parsed.associateBy { it.pos }
        return countEnergized(tilesByPos, dimensions, 0 to 0, Cardinal.EAST)
    }

    private fun countEnergized(
        tilesByPos: Map<Pos, Tile>,
        dimensions: Pair<Int, Int>,
        start: Pos,
        direction: Cardinal
    ): Long {
        val energized = mutableSetOf<Energization>()
        var frontier = setOf(Energization(start, direction))
        while (frontier.isNotEmpty()) {
            energized.addAll(frontier)
            frontier = frontier.flatMap {
                next(it, tilesByPos, dimensions)
            }.toSet()
            frontier = frontier - energized
        }
        return energized.map { it.pos }.toSet().size.toLong()
    }

    private fun next(
        from: Energization,
        tilesByPos: Map<Pair<Int, Int>, Tile>,
        dimensions: Pair<Int, Int>
    ): List<Energization> {
        val tile = tilesByPos[from.pos]
        val nextDirections = tile?.type?.nextDirectionAfter(from.direction) ?: listOf(from.direction)
        return nextDirections.map { dir ->
            Energization(
                dir.of(from.pos),
                dir
            )
        }.filter { it.pos == it.pos.coerce(dimensions) }
    }

    fun part2(input: List<String>): Long {
        val parsed = parse(input)
        val dimensions = input.size - 1 to input.first().length - 1
        val tilesByPos = parsed.associateBy { it.pos }
        val starts = (0..dimensions.first).flatMap { row ->
            listOf((row to 0) to Cardinal.EAST, (row to dimensions.second) to Cardinal.WEST)
        } + (0..dimensions.second).flatMap { col ->
            listOf((0 to col) to Cardinal.SOUTH, (dimensions.first to col) to Cardinal.NORTH)
        }
        return starts.maxOf { start ->
            countEnergized(tilesByPos, dimensions, start.first, start.second)

        }
    }
}

fun main() {
    val testInput = """
        .|...\....
        |.-.\.....
        .....|-...
        ........|.
        ..........
        .........\
        ..../.\\..
        .-.-/..|..
        .|....-|.\
        ..//.|....
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day16.part1(testInput))
    println(Day16.part2(testInput))

    println("------Real------")
    val input = readInput(2023, 16)
    println("Part 1 result: ${Day16.part1(input)}")
    println("Part 2 result: ${Day16.part2(input)}")
    timingStatistics { Day16.part1(input) }
    timingStatistics { Day16.part2(input) }
}
