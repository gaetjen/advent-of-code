package y2022

import util.readInput
import kotlin.math.abs

object Day15 {
    data class SensorBeacon(val sensor: Pos, val beacon: Pos) {
        constructor(s: List<Int>, b: List<Int>) : this(s.toPair(), b.toPair())

        fun manhattanDist(): Int {
            return sensor.manhattanDist(beacon)
        }
    }

    fun Pos.manhattanDist(other: Pos): Int {
        return abs(this.first - other.first) + abs(this.second - other.second)
    }

    fun <T> List<T>.toPair(): Pair<T, T> {
        val (first, second) = this
        return first to second
    }

    private fun parse(input: List<String>): List<SensorBeacon> {
        return input.map { line ->
            val s = line.substringAfter("x=").substringBefore(":").split((", y=")).map { it.toInt() }
            val b = line.substringAfterLast("x=").split((", y=")).map { it.toInt() }
            SensorBeacon(s, b)
        }
    }

    fun part1(input: List<String>, line: Int): Int {
        val sensorBeacons = parse(input)
        val coveredPositions = coveredPositionsInLine(sensorBeacons, line)
        val minX = coveredPositions.minOf { it.first }
        val maxX = coveredPositions.maxOf { it.last }
        var count = 0
        for (i in minX..maxX) {
            if (coveredPositions.any { i in it }) {
                count++
            }
        }
        return count - sensorBeacons.filter { it.beacon.second == line }.map { it.beacon }.distinct().count()
    }

    fun part2(input: List<String>, max: Int): Long {
        val sensorBeacons = parse(input)
        val coveredPositions = (0..max).map { line ->
            coveredPositionsInLine(sensorBeacons, line)
        }
        var y = -1
        var yLine = listOf<IntRange>()
        coveredPositions.forEachIndexed { idx, line ->
            var mergedRanges = line
            for (i in 0..line.size) {
                val overlaps = mergedRanges.filter { overlapsOrTouches(mergedRanges[0], it) }.toSet()
                val newMerged = overlaps.minOf { it.first }..overlaps.maxOf { it.last }
                mergedRanges = mergedRanges.filter { it !in overlaps } + listOf(newMerged)
            }
            if (mergedRanges.size == 2) {
                y = idx
                yLine = mergedRanges
                return@forEachIndexed
            }
        }
        println(y)
        println(yLine)
        val x = yLine.minBy { it.first }.last + 1
        return x * 4_000_000L + y
    }

    private fun coveredPositionsInLine(
        sensorBeacons: List<SensorBeacon>,
        line: Int,
    ) = sensorBeacons.map {
        val diff = it.manhattanDist() - abs(it.sensor.second - line)
        if (diff >= 0) {
            ((it.sensor.first - diff)..(it.sensor.first + diff))
        } else {
            IntRange.EMPTY
        }
    }.filter { !it.isEmpty() }


    private fun overlapsOrTouches(range1: IntRange, range2: IntRange): Boolean {
        val extended = (range2.first - 1)..(range2.last + 1)
        return listOf(
            range1.first in extended,
            range1.last in extended,
            range2.first in range1
        ).any { it }
    }
}

fun main() {
    val testInput = """
        Sensor at x=2, y=18: closest beacon is at x=-2, y=15
        Sensor at x=9, y=16: closest beacon is at x=10, y=16
        Sensor at x=13, y=2: closest beacon is at x=15, y=3
        Sensor at x=12, y=14: closest beacon is at x=10, y=16
        Sensor at x=10, y=20: closest beacon is at x=10, y=16
        Sensor at x=14, y=17: closest beacon is at x=10, y=16
        Sensor at x=8, y=7: closest beacon is at x=2, y=10
        Sensor at x=2, y=0: closest beacon is at x=2, y=10
        Sensor at x=0, y=11: closest beacon is at x=2, y=10
        Sensor at x=20, y=14: closest beacon is at x=25, y=17
        Sensor at x=17, y=20: closest beacon is at x=21, y=22
        Sensor at x=16, y=7: closest beacon is at x=15, y=3
        Sensor at x=14, y=3: closest beacon is at x=15, y=3
        Sensor at x=20, y=1: closest beacon is at x=15, y=3
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day15.part1(testInput, 10))
    println(Day15.part2(testInput, 20))

    println("------Real------")
    val input = readInput("resources/2022/day15")
    println(Day15.part1(input, 2000000))
    println(3405562L * 4000000L + 3246513L)
    println(Day15.part2(input, 4000000))
}
