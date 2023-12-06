package y2023

import util.product
import util.readInput
import util.timingStatistics
import kotlin.math.sqrt

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

    fun part2(input: List<String>): Int {
        val (time, distance) = parse2(input)
        return (0..time + 1).count { t ->
            (time - t) * t > distance
        }
    }

    fun part2Optimized(input: List<String>): Long {
        val (time, distance) = parse2(input)
        val firstWin = (0..time + 1).first { t ->
            (time - t) * t > distance
        }
        val lastWin = (time + 1 downTo 0).first { t ->
            (time - t) * t > distance
        }
        return lastWin - firstWin + 1
    }

    fun parse2(input: List<String>): Pair<Long, Long> {
        val time = input[0].dropWhile { !it.isDigit() }.replace(" ", "").toLong()
        val distance = input[1].dropWhile { !it.isDigit() }.replace(" ", "").toLong()
        return time to distance
    }

    // distance: D = (T - t) * t
    // -t² + Tt - D = 0
    // t = (-T ± sqrt(T² - 4D)) / -2
    fun part2Optimized2(input: List<String>): Long {
        val (time, distance) = parse2(input)
        val root = sqrt(time * time - 4 * distance.toDouble())
        val t1 = (-time + root) / -2
        val t2 = (-time - root) / -2
        return t2.toLong() - t1.toLong()
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
    println(Day06.part2Optimized(testInput))
    println(Day06.part2Optimized2(testInput))

    println("------Real------")
    val input = readInput(2023, 6)
    println("Part 1 result: ${Day06.part1(input)}")
    println("Part 2 result: ${Day06.part2(input)}")
    println("Part 2 result: ${Day06.part2Optimized(input)}")
    println("Part 2 result: ${Day06.part2Optimized2(input)}")
    timingStatistics { Day06.part1(input) }
    timingStatistics { Day06.part2(input) }
    timingStatistics { Day06.part2Optimized(input) }
    timingStatistics { Day06.part2Optimized2(input) }
}