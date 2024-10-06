package y2018

import util.Pos
import util.readInput
import util.split
import util.timingStatistics

data class GuardShift(
    val guardId: Int,
    val sleepTimes: List<Pos>
)

object Day04 {
    private fun parse(input: List<String>): List<GuardShift> {
        return input.sorted().split(matchInPost = true) {
            it.endsWith("begins shift")
        }.drop(1).map { lines ->
            val guardId = lines.first().substringAfter("#").substringBefore(" ").toInt()
            GuardShift(
                guardId = guardId,
                sleepTimes = lines.drop(1).map {
                    it.substringAfter(":").substringBefore("]").toInt()
                }.chunked(2).map { it.first() to it[1] }
            )
        }
    }

    fun part1(input: List<String>): Int {
        val shifts = parse(input)
        val sleepiest = shifts.groupBy { it.guardId }.maxBy { (_, shifts) ->
            shifts.flatMap { it.sleepTimes }.sumOf { it.second - it.first }
        }
        val sleepRanges = sleepiest.value.flatMap { it.sleepTimes }.map { it.first until it.second }
        val sleepyMinute = (0..59).maxBy { minute -> sleepRanges.count { range -> minute in range } }
        return sleepyMinute * sleepiest.key
    }

    fun part2(input: List<String>): Int {
        val shifts = parse(input)
        val sleepRanges = shifts
            .groupBy { it.guardId }
            .mapValues { (_, shifts) ->
                val sleepRanges = shifts
                    .flatMap { it.sleepTimes }
                    .map { it.first until it.second }
                (0..59).map { minute -> sleepRanges.count { range -> minute in range } to minute }
                    .maxBy { it.first }
            }
        return sleepRanges.maxBy { it.value.first }.let { it.key * it.value.second }
    }
}

fun main() {
    val testInput = """
        [1518-11-01 00:00] Guard #10 begins shift
        [1518-11-01 00:05] falls asleep
        [1518-11-01 00:25] wakes up
        [1518-11-01 00:30] falls asleep
        [1518-11-01 00:55] wakes up
        [1518-11-01 23:58] Guard #99 begins shift
        [1518-11-02 00:40] falls asleep
        [1518-11-02 00:50] wakes up
        [1518-11-03 00:05] Guard #10 begins shift
        [1518-11-03 00:24] falls asleep
        [1518-11-03 00:29] wakes up
        [1518-11-04 00:02] Guard #99 begins shift
        [1518-11-04 00:36] falls asleep
        [1518-11-04 00:46] wakes up
        [1518-11-05 00:03] Guard #99 begins shift
        [1518-11-05 00:45] falls asleep
        [1518-11-05 00:55] wakes up
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day04.part1(testInput))
    println(Day04.part2(testInput))

    println("------Real------")
    val input = readInput(2018, 4)
    println("Part 1 result: ${Day04.part1(input)}")
    println("Part 2 result: ${Day04.part2(input)}")
    timingStatistics { Day04.part1(input) }
    timingStatistics { Day04.part2(input) }
}