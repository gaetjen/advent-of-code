package y2016

import util.Direction
import util.Pos
import util.md5

object Day17 {
    val viablePositions = List(4) { x -> List(4) { y -> x to y } }.flatten().toSet()

    fun part1(input: String): String {
        var states = listOf((0 to 3) to "")
        while (true) {
            states = states.flatMap {
                nextStates(input, it.first, it.second)
            }
            states.firstOrNull { it.first == 3 to 0 }?.let {
                return it.second
            }
        }
    }

    private val directions = mapOf(
        Direction.UP to (0 to 'U'),
        Direction.DOWN to (1 to 'D'),
        Direction.LEFT to (2 to 'L'),
        Direction.RIGHT to (3 to 'R')
    )

    private fun nextStates(
        input: String,
        pos: Pos,
        hist: String
    ): Iterable<Pair<Pos, String>> {
        val hash = (input + hist).md5().take(4)
        return directions.filter { (_, v) ->
            hash[v.first].code >= 'b'.code
        }.map { (k, v) ->
            k.move(pos) to hist + v.second
        }.filter {
            it.first in viablePositions
        }
    }

    fun part2(input: String): Int {
        var states = listOf((0 to 3) to "")
        val solutions = mutableListOf<String>()
        var steps = 0
        while (states.isNotEmpty()) {
            states = states.flatMap {
                nextStates(input, it.first, it.second)
            }
            val ends = states.filter { it.first == 3 to 0 }
            solutions.addAll(
                ends.map { it.second }
            )
            states = states - ends.toSet()
            steps++
        }
        return solutions.last().length
    }
}

fun main() {
    val testInput1 = "ihgpwlah"
    val testInput2 = "kglvqrro"
    val testInput3 = "ulqzkmiv"
    println("------Tests------")
    println(Day17.part1(testInput1))
    println(Day17.part1(testInput2))
    println(Day17.part1(testInput3))
    println(Day17.part2(testInput1))
    println(Day17.part2(testInput2))
    println(Day17.part2(testInput3))

    println("------Real------")
    val input = "mmsxrhfx"
    println(Day17.part1(input))
    println(Day17.part2(input))
}