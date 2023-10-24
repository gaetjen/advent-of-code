package y2015

import util.readInput

object Day02 {
    private fun parse(input: List<String>): List<Triple<Int, Int, Int>> {
        return input.map {
            it.split('x').map { it.toInt() }
        }.map { Triple(it.first(), it.last(), it[1]) }
    }

    fun part1(input: List<String>): Long {
        val parsed = parse(input)
        return parsed.map {
            areas(it)
        }.sumOf { it.first + it.second }.toLong()
    }

    private fun areas(sides: Triple<Int, Int, Int>): Pair<Int, Int> {
        val (a, b, c) = sides
        val sideAreas = listOf(a*b, b*c, a*c)
        return sideAreas.sum() * 2 to sideAreas.min()
    }

    fun part2(input: List<String>): Int {
        val parsed = parse(input)
        return parsed.sumOf {
            volume(it) + smallestCircumference(it)
        }
    }

    private fun volume(sides: Triple<Int, Int, Int>): Int {
        val (a, b, c) = sides
        return a * b * c
    }

    private fun smallestCircumference(sides: Triple<Int, Int, Int>): Int {
        val (a, b, c) = sides
        return ((a + b + c) - listOf(a, b, c).max()) * 2
    }
}

fun main() {
    val testInput = """
        1x1x10
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day02.part1(testInput))
    println(Day02.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2015/day02")
    println(Day02.part1(input))
    println(Day02.part2(input))
}
