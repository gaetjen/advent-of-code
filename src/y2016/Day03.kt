package y2016

import util.readInput
import util.transpose

object Day03 {
    private fun parse(input: List<String>): List<List<Int>> {
        return input.map { line ->
            line.trim().split(Regex("\\s+")).map {
                it.toInt()
            }
        }
    }

    fun part1(input: List<String>): Int {
        val parsed = parse(input)
        return parsed.count {
            it.sum() - it.max() > it.max()
        }
    }

    fun part2(input: List<String>): Int {
        val parsed = parse(input)
        return parsed.chunked(3).map {
            it.transpose()
        }.flatten().count {
            it.sum() - it.max() > it.max()
        }
    }
}

fun main() {
    val testInput = """
        5 10 25
        5 10 25
        2 2 3
        101 301 501
        102 302 502
        103 303 503
        201 401 601
        202 402 602
        203 403 603
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day03.part1(testInput))
    println(Day03.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2016/day03")
    println(Day03.part1(input))
    println(Day03.part2(input))
}