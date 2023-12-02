package util

import kotlin.math.max
import kotlin.math.min


typealias Pos = Pair<Int, Int>
typealias PosL = Pair<Long, Long>

operator fun Pos.plus(b: Pos) = this.first + b.first to this.second + b.second
operator fun Pos.times(b: Int) = this.first * b to this.second * b
fun Pos.inverse() = this.second to this.first

fun <T> transpose(matrix: List<List<T>>): List<List<T>> {
    return List(matrix.first().size) { rowIdx ->
        List(matrix.size) { colIdx ->
            matrix[colIdx][rowIdx]
        }
    }
}

operator fun <T> List<List<T>>.get(p: Pos) = this[p.first][p.second]
operator fun <T> MutableList<MutableList<T>>.set(p: Pos, value: T) {
    this[p.first][p.second] = value
}
fun <T> getRow(grid: List<List<T>>, rowIdx: Int) = grid[rowIdx]
fun <T> getCol(grid: List<List<T>>, colIdx: Int) = grid.map { it[colIdx] }

fun <T> getRange(grid: List<List<T>>, startRow: Int, startCol: Int, stopRow: Int, stopCol: Int) : List<T> {
    return (startRow..stopRow).map { x ->
        (startCol..stopCol).map { y ->
            grid[x to y]
        }
    }.flatten()
}

enum class Cardinal(val relativePos: Pos) {
    NORTH(-1 to 0),
    EAST(0 to 1),
    SOUTH(1 to 0),
    WEST(0 to -1);

    companion object {
        val diagonals = listOf(NORTH to WEST, NORTH to EAST, SOUTH to WEST, SOUTH to EAST)
    }

    fun of(pos: Pair<Int, Int>): Pair<Int, Int> {
        return pos + relativePos
    }

    fun turn(direction: Turn): Cardinal {
        return when (direction) {
            Turn.RIGHT -> Cardinal.entries[(this.ordinal + 1) % 4]
            Turn.LEFT -> Cardinal.entries[(this.ordinal - 1).mod(4)]
        }
    }
}

enum class Turn {
    LEFT, RIGHT;
    companion object {
        fun fromChar(c: Char): Turn {
            return when (c) {
                'L' -> LEFT
                'R' -> RIGHT
                else -> error("$c is not a turn indicator")
            }
        }
    }
}

enum class Direction {
    UP, RIGHT, DOWN, LEFT;

    companion object {
        fun fromChar(c: Char): Direction {
            return when (c) {
                'R' -> RIGHT
                'U' -> UP
                'D' -> DOWN
                'L' -> LEFT
                else -> error("$c is not a direction")
            }
        }
    }

    fun move(pos: Pair<Int, Int>): Pair<Int, Int> {
        val (fromX, fromY) = pos
        return when (this) {
            UP -> fromX to fromY + 1
            DOWN -> fromX to fromY - 1
            LEFT -> fromX - 1 to fromY
            RIGHT -> fromX + 1 to fromY
        }
    }

    fun moveL(pos: Pair<Long, Long>): Pair<Long, Long> {
        val (fromX, fromY) = pos
        return when (this) {
            UP -> fromX to fromY + 1
            DOWN -> fromX to fromY - 1
            LEFT -> fromX - 1 to fromY
            RIGHT -> fromX + 1 to fromY
        }
    }
}

fun minMax(gridPositions: Set<Pos>): Pair<Pos, Pos> {
    val minRow = gridPositions.minOf { it.first }
    val maxRow = gridPositions.maxOf { it.first }
    val minCol = gridPositions.minOf { it.second }
    val maxCol = gridPositions.maxOf { it.second }
    return (minRow to minCol) to (maxRow to maxCol)
}

fun printGrid(positions: Map<Pos, String>, width: Int = 1) {
    println("number positions in grid: ${positions.size}")
    val (min, max) = minMax(positions.keys)
    val (minRow, minCol) = min
    val (maxRow, maxCol) = max
    val result = List(maxRow - minRow + 1) { rowIdx ->
        List(maxCol - minCol + 1) { colIdx ->
            if (rowIdx + minRow to colIdx + minCol in positions) {
                positions[rowIdx + minRow to colIdx + minCol]
            } else {
                " ".repeat(width)
            }
        }.joinToString("")
    }
    println(result.joinToString("\n"))
    println("------------------------END GRID------------------------")
}

fun <T> printMatrix(grid: List<List<T>>, toString: (T) -> String) {
    grid.map { row ->
        val output = row.joinToString(separator = "") { item ->
            toString(item)
        }
        println(output)
    }
}

fun <T> getNeighbors(grid: List<List<T>>, pos: Pos): List<T> {
    val minRow = max(0, pos.first - 1)
    val maxRow = min(pos.first + 1, grid.size - 1)
    val minCol = max(0, pos.second - 1)
    val maxCol = min(pos.second + 1, grid.first().size - 1)
    return (minRow..maxRow).flatMap { row ->
        (minCol..maxCol).mapNotNull { col ->
            if (row to col != pos) grid[row to col] else null
        }
    }
}
