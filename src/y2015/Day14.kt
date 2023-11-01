package y2015

import util.readInput
import kotlin.math.min

data class Reindeer(
    val speed: Int,
    val endurance: Int,
    val rest: Int
) {
    fun distance(testTime: Int): Int {
        val cycleLength = endurance + rest
        val distancePerCycle = endurance * speed
        val numCycles = testTime / cycleLength
        val remaining = min(testTime % cycleLength, endurance)
        return numCycles * distancePerCycle + remaining * speed
    }
}

object Day14 {
    private fun parse(input: List<String>): List<Reindeer> {
        return input.map {
            val words = it.split(' ')
            Reindeer(
                words[3].toInt(),
                words[6].toInt(),
                words[words.size - 2].toInt()
            )
        }
    }

    fun part1(input: List<String>, testTime: Int): Int {
        val parsed = parse(input)
        return parsed.maxOf {
            it.distance(testTime)
        }
    }

    fun part2(input: List<String>, realTime: Int): Int {
        val parsed = parse(input)
        val allDistances = (1..realTime).map { t ->
            parsed.map { it.distance(t) }
        }
        val points = allDistances.fold(List(parsed.size) {0}) { acc, distances ->
            val leadDistance = distances.max()
            val points = distances.map { if (it == leadDistance) 1 else 0 }
            acc.zip(points).map { it.first + it.second }
        }
        return points.max()
    }
}

fun main() {
    val testInput = """
        Comet can fly 14 km/s for 10 seconds, but then must rest for 127 seconds.
        Dancer can fly 16 km/s for 11 seconds, but then must rest for 162 seconds.
    """.trimIndent().split("\n")
    val testTime = 1000
    println("------Tests------")
    println(Day14.part1(testInput, testTime))
    println(Day14.part2(testInput, testTime))

    println("------Real------")
    val input = readInput("resources/2015/day14")
    val realTime = 2503
    println(Day14.part1(input, realTime))
    println(Day14.part2(input, realTime))
}
