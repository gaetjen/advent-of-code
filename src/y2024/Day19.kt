package y2024

import util.readInput
import util.split
import util.timingStatistics

object Day19 {
    private fun parse(input: List<String>): Pair<List<String>, List<String>> {
        val (towels, designs) = input.split { it.isEmpty() }
        return towels.first().split(", ") to designs
    }

    fun part1(input: List<String>): Int {
        val (towels, designs) = parse(input)
        return designs.count {
            canMake(it, towels)
        }
    }

    private fun canMake(
        design: String,
        towels: List<String>
    ): Boolean {
        towels.asSequence().forEach {
            val next = design.removePrefix(it)
            if (next.isEmpty()) return true
            if (next.length < design.length && canMake(next, towels)) return true
        }
        return false
    }

    private fun countCombinations(
        design: String,
        towels: List<String>
    ): Long {
        return countCombinationsWithCache(design, towels, mutableMapOf())
    }

    private fun countCombinationsWithCache(
        design: String,
        towels: List<String>,
        cache: MutableMap<String, Long>
    ): Long {
        if (cache[design] != null) {
            return cache.getValue(design)
        }
        val result = towels.sumOf {
            val next = design.removePrefix(it)
            if (next.isEmpty()) {
                1L
            } else if (next.length < design.length) {
                countCombinationsWithCache(next, towels, cache)
            } else {
                0L
            }
        }
        cache[design] = result
        return result
    }

    fun part2(input: List<String>): Long {
        val (towels, designs) = parse(input)
        return designs.sumOf {
            countCombinations(it, towels)
        }
    }
}

fun main() {
    val testInput = """
        r, wr, b, g, bwu, rb, gb, br

        brwrr
        bggr
        gbbr
        rrbgbr
        ubwu
        bwurrg
        brgr
        bbrgwb
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day19.part1(testInput))
    println(Day19.part2(testInput))

    println("------Real------")
    val input = readInput(2024, 19)
    println("Part 1 result: ${Day19.part1(input)}")
    println("Part 2 result: ${Day19.part2(input)}")
    timingStatistics { Day19.part1(input) }
    timingStatistics { Day19.part2(input) }
}