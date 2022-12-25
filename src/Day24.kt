import Cardinal.EAST
import Cardinal.NORTH
import Cardinal.SOUTH
import Cardinal.WEST
import Cardinal.values
import kotlin.system.measureTimeMillis

object Day24 {
    data class Blizzard(val pos: Pos, val dir: Cardinal)

    data class Valley(
        var blizzards: List<Blizzard>,
        val height: Int,
        val width: Int,
    ) {
        val end = height to width - 1
        fun step() {
            blizzards = blizzards.map {
                val (newR, newC) = it.dir.of(it.pos)
                it.copy(pos = newR.mod(height) to newC.mod(width))
            }
        }
    }

    private fun parse(input: List<String>): Valley {
        val blizzards = input.drop(1).dropLast(1).mapIndexed { rowIdx, ln ->
            ln.drop(1).dropLast(1).mapIndexed { colIdx, c ->
                fromChar(c)?.let { Blizzard(rowIdx to colIdx, it) }
            }
        }.flatten().filterNotNull()
        return Valley(blizzards, input.size - 2, input.first().length - 2)
    }

    fun fromChar(c: Char) = when (c) {
        '>' -> EAST
        '<' -> WEST
        '^' -> NORTH
        'v' -> SOUTH
        else -> null
    }

    fun part1(input: List<String>): Int {
        val valley = parse(input)
        var frontier = setOf(-1 to 0)
        var minutes = 0
        while (valley.end !in frontier) {
            minutes++
            valley.step()
            frontier = nextFrontier(frontier, valley)
        }
        return minutes
    }

    fun part2(input: List<String>): Int {
        val valley = parse(input)
        val sizes = mutableListOf(1)
        var frontier = setOf(-1 to 0)
        var minutes = 0
        while (valley.end !in frontier) {
            minutes++
            valley.step()
            frontier = nextFrontier(frontier, valley)
            sizes.add(frontier.size)
        }
        printGrid(frontier.associateWith { "[]" } + valley.blizzards.associate { it.pos to "**" }, 2)
        frontier = setOf(valley.end)
        while (-1 to 0 !in frontier) {
            minutes++
            valley.step()
            frontier = nextFrontier(frontier, valley)
            sizes.add(frontier.size)
        }
        printGrid(frontier.associateWith { "[]" } + valley.blizzards.associate { it.pos to "**" }, 2)
        frontier = setOf(-1 to 0)
        while (valley.end !in frontier) {
            minutes++
            valley.step()
            frontier = nextFrontier(frontier, valley)
            sizes.add(frontier.size)
        }
        printGrid(frontier.associateWith { "[]" } + valley.blizzards.associate { it.pos to "**" }, 2)
        println("max frontier: ${sizes.max()}")
        return minutes
    }

    private fun nextFrontier(
        frontier: Set<Pair<Int, Int>>,
        valley: Valley,
    ) = frontier.flatMap { p ->
        values().map { it.of(p) } + p
    }
        .filter {
            (it.first in 0 until valley.height && it.second in 0 until valley.width)
                    || it == -1 to 0 || it == valley.end
        }.toSet() - valley.blizzards.map { it.pos }.toSet()
}

fun main() {
    val testInput = """
        #.######
        #>>.<^<#
        #.<..<<#
        #>v.><>#
        #<^v^^>#
        ######.#
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day24.part1(testInput))
    println(Day24.part2(testInput))

    println("------Real------")
    val input = readInput("resources/day24")
    println(Day24.part1(input))
    val t = measureTimeMillis {
        println(Day24.part2(input))
    }
    println("millis elapsed: $t")
}
