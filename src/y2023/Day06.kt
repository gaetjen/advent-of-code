package y2023

import util.product
import util.readInput
import util.timingStatistics

object Day06 {
    private fun parse(input: List<String>): Pair<List<Int>, List<Int>> {
        val time = input[0].split(Regex("\\s+")).drop(1).map { it.toInt() }
        val distance = input[1].split(Regex("\\s+")).drop(1).map { it.toInt() }
        return Pair(time, distance)
    }

    fun part1(input: List<String>): Int {
        val (times, records) = parse(input)
        val allDistances = times.mapIndexed { idx, t ->
            distancesPerTime(t).count { d -> d > records[idx] }
        }
        return allDistances.product()
    }

    fun distancesPerTime(t: Int): List<Int> {
        return List(t + 1) {
            (t - it) * it
        }
    }

    fun parse2(input: List<String>): Pair<Long, Long> {
        val time = input[0].split(Regex("\\s+")).drop(1).joinToString(separator = "").toLong()
        val distance = input[1].split(Regex("\\s+")).drop(1).joinToString(separator = "").toLong()
        return time to distance
    }

    fun part2(input: List<String>): Int {
        val (time, distance) = parse2(input)
        return (0..time + 1).count { t ->
            (time - t) * t > distance
        }
    }
}

fun main() {
    val testInput = """
        Time:      7  15   30
        Distance:  9  40  200
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day06.part1(testInput))
    println(Day06.part2(testInput))

    println("------Real------")
    val input = readInput(2023, 6)
    println("Part 1 result: ${Day06.part1(input)}")
    println("Part 2 result: ${Day06.part2(input)}")
    timingStatistics { Day06.part1(input) }
    timingStatistics { Day06.part2(input) }
}