package y2022

import util.readInput
import kotlin.math.abs

object Day09 {

    enum class Direction {
        UP, DOWN, LEFT, RIGHT;

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
    }

    data class Move(val direction: Direction, val steps: Int)

    private fun parse(input: String): Move {
        return Move(
            Direction.fromChar(input.first()),
            input.split(" ").last().toInt()
        )
    }

    fun part1(input: List<String>): Int {
        val moves = input.map { parse(it) }
        var headPos = 0 to 0
        var tailPos = headPos
        val visited = mutableSetOf(tailPos)
        moves.forEach { move ->
            repeat(move.steps) {
                // move head
                headPos = move.direction.move(headPos)
                // follow tail
                tailPos = follow(headPos, tailPos)
                // add new pos to visited
                visited.add(tailPos)
            }
        }

        return visited.size
    }

    private fun follow(headPos: Pair<Int, Int>, tailPos: Pair<Int, Int>): Pair<Int, Int> {
        val (headX, headY) = headPos
        val (tailX, tailY) = tailPos
        val (distX, distY) = headX - tailX to headY - tailY
        if (distX == 0 && abs(distY) == 2) {
            assert(abs(distY) == 2)
            return tailX to tailY + (distY / 2)
        }
        if (distY == 0 && abs(distX) == 2) {
            return tailX + (distX / 2) to tailY
        }
        if (abs(distX) <= 1 && abs(distY) <= 1) {
            return tailPos
        }
        // diagonal
        return tailX + (distX / abs(distX)) to tailY + (distY / abs(distY))
    }

    fun part2(input: List<String>): Int {
        val moves = input.map { parse(it) }
        val rope = MutableList(10) { 0 to 0 }
        val visited = mutableSetOf(rope.last())
        moves.forEach { move ->
            repeat(move.steps) {
                rope.forEachIndexed { index, pos ->
                    if (index == 0) {
                        rope[0] = move.direction.move(pos)
                    } else {
                        rope[index] = follow(rope[index - 1], pos)
                    }
                }
                visited.add(rope.last())
            }
        }
        return visited.size
    }
}


fun main() {
    println(listOf(1, 2, 3).slice(0..1))

    val testInput = """
        R 4
        U 4
        L 3
        D 1
        R 4
        D 1
        L 5
        R 2
    """.trimIndent().split("\n")
    val test2 = """
        R 5
        U 8
        L 8
        D 3
        R 17
        D 10
        L 25
        U 20
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day09.part1(testInput))
    println(Day09.part2(test2))

    println("------Real------")
    val input = readInput("resources/2022/day09")
    // wrong: 11292
    println(Day09.part1(input))
    // wrong: 2627
    println(Day09.part2(input))
}
