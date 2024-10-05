package y2017

import util.Cardinal
import util.Pos
import util.Turn
import util.readInput
import util.timingStatistics

data class Carrier(
    val position: Pos,
    val direction: Cardinal,
    val jizzCounter: Int = 0
) {
    fun burst(grid: Set<Pos>) : Pair<Set<Pos>, Carrier> {
        val currentInfected = position in grid
        val newDirection = if (currentInfected) {
            direction.turn(Turn.RIGHT)
        } else {
            direction.turn(Turn.LEFT)
        }
        val newGrid = if (currentInfected) grid - position else grid + position
        return newGrid to Carrier(
            newDirection.of(position),
            newDirection,
            if (currentInfected) jizzCounter else jizzCounter + 1
        )
    }


    fun burstV2(grid: MutableMap<Pos, CellState>): Carrier {
        val currentState = grid[position] ?: CellState.Clean
        val newDirection = when (currentState) {
            CellState.Infected -> direction.turn(Turn.RIGHT)
            CellState.Weakened -> direction
            CellState.Flagged -> direction.turn(Turn.LEFT).turn(Turn.LEFT)
            CellState.Clean -> direction.turn(Turn.LEFT)
        }
        when (currentState) {
            CellState.Infected -> grid[position] = CellState.Flagged
            CellState.Weakened -> grid[position] = CellState.Infected
            CellState.Flagged -> grid.remove(position)
            CellState.Clean -> grid[position] = CellState.Weakened
        }
        return Carrier(
            newDirection.of(position),
            newDirection,
            if(currentState == CellState.Weakened) jizzCounter + 1 else jizzCounter
        )
    }
}


enum class CellState {
    Infected, Weakened, Flagged, Clean
}


object Day22 {
    private fun parse(input: List<String>): Pair<Set<Pos>, Carrier> {
        val infected = input.flatMapIndexed { rowIdx, row ->
            row.mapIndexedNotNull { colIdx, c -> if (c == '#') rowIdx to colIdx  else null}
        }.toSet()
        val start = input.size / 2 to input.first().length / 2
        return infected to Carrier(start, Cardinal.NORTH)
    }

    fun part1(input: List<String>): Int {
        var (grid, carrier) = parse(input)
        repeat(10_000) {
            val bla = carrier.burst(grid)
            grid = bla.first
            carrier = bla.second
        }
        return carrier.jizzCounter
    }

    fun part2(input: List<String>): Int {
        var (grid, carrier) = parse(input)
        val gridV2 = grid.associateWith { CellState.Infected }.toMutableMap()
        repeat(10_000_000) {
            carrier = carrier.burstV2(gridV2)
        }
        return carrier.jizzCounter
    }
}

fun main() {
    val testInput = """
        ..#
        #..
        ...
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day22.part1(testInput))
    println(Day22.part2(testInput))

    println("------Real------")
    val input = readInput(2017, 22)
    println("Part 1 result: ${Day22.part1(input)}")
    println("Part 2 result: ${Day22.part2(input)}")
    timingStatistics { Day22.part1(input) }
    timingStatistics { Day22.part2(input) }
}