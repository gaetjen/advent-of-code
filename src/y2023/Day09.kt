package y2023

import util.readInput
import util.timingStatistics

object Day09 {
    private fun parse(input: List<String>): List<List<Long>> {
        return input.map { line ->
            line.split(" ").map { it.toLong() }
        }
    }

    fun part1(input: List<String>): Long {
        val parsed = parse(input)
        return parsed.sumOf { extrapolated(it) }
    }

    fun gradient(lst: List<Long>): List<Long> {
        return lst.zipWithNext { a, b -> b - a }
    }

    private fun allGradients(lst: List<Long>): List<List<Long>> {
        return buildList {
            add(lst)
            while (!last().all { it == 0L }) {
                add(gradient(last()))
            }
        }
    }

    private fun extrapolated(lst: List<Long>): Long {
        return allGradients(lst).sumOf { it.last() }
    }

    fun part2(input: List<String>): Long {
        val parsed = parse(input)
        return parsed.sumOf { line ->
            allGradients(line).map {
                it.first()
            }.reduceRight { previous, acc ->
                previous - acc
            }
        }
    }
}

fun main() {
    val testInput = """
        0 3 6 9 12 15
        1 3 6 10 15 21
        10 13 16 21 30 45
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day09.part1(testInput))
    println(Day09.part2(testInput))

    println("------Real------")
    val input = readInput(2023, 9)
    println("Part 1 result: ${Day09.part1(input)}")
    println("Part 2 result: ${Day09.part2(input)}")
    timingStatistics { Day09.part1(input) }
    timingStatistics { Day09.part2(input) }
}