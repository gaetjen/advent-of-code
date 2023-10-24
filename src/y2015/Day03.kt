package y2015

import util.Direction
import util.readInput

object Day03 {
    private fun parse(input: List<String>): List<Direction> {
        return input.first()
            .replace('>', 'R')
            .replace('<', 'L')
            .replace('^', 'U')
            .replace('v', 'D')
            .map { Direction.fromChar(it) }
    }

    fun part1(input: List<String>): Int {
        val parsed = parse(input)
        val visited = allVisited(parsed)
        return visited.size
    }

    private fun allVisited(parsed: List<Direction>): MutableSet<Pair<Int, Int>> {
        val visited = mutableSetOf(0 to 0)
        //var lastPos = 0 to 0
        parsed.fold(0 to 0) { acc, dir ->
            val new = dir.move(acc)
            visited.add(new)
            new
        }
        return visited
    }

    fun part2(input: List<String>): Int {
        val parsed = parse(input)
        val chunks = parsed.chunked(2)
        val santa = chunks.map { it.first() }
        val robo = chunks.map { it.last() }
        val visited = allVisited(santa) + allVisited(robo)
        return visited.size
    }
}

fun main() {
    val testInput = """
        ^>v<
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day03.part1(testInput))
    println(Day03.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2015/day03")
    println(Day03.part1(input))
    println(Day03.part2(input))
}
