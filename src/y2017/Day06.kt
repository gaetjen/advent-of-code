package y2017

import util.readInput
import util.timingStatistics

object Day06 {
    private fun parse(input: List<String>): MutableList<Int> {
        return input.first().split(Regex("\\s")).map { it.toInt() }.toMutableList()
    }

    fun part1(input: List<String>): Int {
        val banks = parse(input)
        var steps = 0
        val knownStates = mutableSetOf<List<Int>>()
        while (true) {
            if (banks in knownStates) {
                return steps
            }
            knownStates.add(banks.toList())
            var toDistribute = banks.max()
            var idx = banks.indexOf(toDistribute)
            banks[idx] = 0
            while (toDistribute > 0) {
                idx = (idx + 1) % banks.size
                banks[idx]++
                toDistribute--
            }
            steps++
        }
    }

    fun part2(input: List<String>): Int {
        val banks = parse(input)
        var steps = 0
        val knownStates = mutableMapOf<List<Int>, Int>()
        while (true) {
            if (banks in knownStates) {
                return steps - knownStates[banks]!!
            }
            knownStates[banks] = steps
            var toDistribute = banks.max()
            var idx = banks.indexOf(toDistribute)
            banks[idx] = 0
            while (toDistribute > 0) {
                idx = (idx + 1) % banks.size
                banks[idx]++
                toDistribute--
            }
            steps++
        }
    }
}

fun main() {
    val testInput = """
        0 2 7 0
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day06.part1(testInput))
    println(Day06.part2(testInput))

    println("------Real------")
    val input = readInput(2017, 6)
    println("Part 1 result: ${Day06.part1(input)}")
    println("Part 2 result: ${Day06.part2(input)}")
    timingStatistics { Day06.part1(input) }
    timingStatistics { Day06.part2(input) }
}