package y2017

import util.readInput
import util.timingStatistics

object Day05 {
    private fun parse(input: List<String>): MutableList<Int> {
        return input.map { it.toInt() }.toMutableList()
    }

    fun part1(input: List<String>): Long {
        val jumps = parse(input)
        var currentIdx = 0
        var currentStep = 0
        while (true) {
            if (currentIdx < 0 || currentIdx >= jumps.size) {
                return currentStep.toLong()
            }
            jumps[currentIdx]++
            currentIdx += jumps[currentIdx] - 1
            currentStep++
        }
    }

    fun part2(input: List<String>): Long {
        val jumps = parse(input)
        var currentIdx = 0
        var currentStep = 0
        while (true) {
            if (currentIdx < 0 || currentIdx >= jumps.size) {
                return currentStep.toLong()
            }
            val jumpDistance = jumps[currentIdx]
            if (jumps[currentIdx] >= 3) {
                jumps[currentIdx]--
            } else {
                jumps[currentIdx]++
            }
            currentIdx += jumpDistance
            currentStep++
        }
    }
}

fun main() {
    val testInput = """
        0
        3
        0
        1
        -3
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day05.part1(testInput))
    println(Day05.part2(testInput))

    println("------Real------")
    val input = readInput(2017, 5)
    println("Part 1 result: ${Day05.part1(input)}")
    println("Part 2 result: ${Day05.part2(input)}")
    timingStatistics { Day05.part1(input) }
    timingStatistics { Day05.part2(input) }
}