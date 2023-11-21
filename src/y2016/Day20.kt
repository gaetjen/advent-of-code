package y2016

import util.readInput

enum class Indicator {
    START, STOP
}

object Day20 {
    private fun parse(input: List<String>): List<Pair<Long, Indicator>> {
        return input.flatMap { line ->
            val (start, stop) = line.split("-").map { it.toLong() }
            listOf(start to Indicator.START, stop to Indicator.STOP)
        }.sortedBy { it.first }
    }

    fun part1(input: List<String>) {
        val parsed = parse(input)
        val stackDepth = parsed.scan(0) { acc, pair ->
            if (pair.second == Indicator.START) {
                acc + 1
            } else {
                acc - 1
            }
        }.drop(1)
        println(parsed)
        println(stackDepth)
        val idx = stackDepth.indexOf(0)
        println("stop: ${parsed[idx]}")
        println("next: ${parsed[idx + 1]}")
        part2(parsed, stackDepth)
    }

    fun part2(parsed: List<Pair<Long, Indicator>>, stackDepth: List<Int>) {
        val gaps = stackDepth.dropLast(1)
            .mapIndexedNotNull { idx, depth ->
            if (depth != 0) {
                null
            } else {
                parsed[idx + 1].first - parsed[idx].first - 1
            }
        }
        println("gaps: $gaps")
        println("total: ${gaps.sum()}")
    }
}

fun main() {
    val testInput = """
        5-8
        0-2
        4-7
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day20.part1(testInput))

    println("------Real------")
    val input = readInput("resources/2016/day20")
    println(Day20.part1(input))
}