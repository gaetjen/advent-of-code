package y2023

import util.readInput
import util.split
import util.timingStatistics

object Day12 {
    enum class SpringCondition(val c: Char) {
        BROKEN('#'), OPERATIONAL('.'), UNKNOWN('?')
    }

    val conditions = SpringCondition.entries.associateBy { it.c }

    private fun parse(input: List<String>): List<Pair<List<SpringCondition>, List<Int>>> {
        return input.map { line ->
            val (conditionString, groupSizes) = line.split(" ")
            conditionString.map { conditions[it]!! } to groupSizes.split(",").map { it.toInt() }
        }
    }

    fun part1(input: List<String>): Int {
        val parsed = parse(input)
        return parsed.sumOf { (conditions, groupSizes) ->
            arrangementCount(conditions, groupSizes)
        }
    }

    private fun arrangementCount(conditions: List<SpringCondition>, groupSizes: List<Int>): Int {
        return allCombinations(conditions).count { actualGroupSizes(it) == groupSizes }
    }

    private fun allCombinations(conditions: List<SpringCondition>): Sequence<List<SpringCondition>> = sequence {
        if (conditions.count { it == SpringCondition.UNKNOWN } == 0) {
            yield(conditions)
        } else {
            val unknownIndex = conditions.indexOfFirst { it == SpringCondition.UNKNOWN }
            val fixedPart = conditions.subList(0, unknownIndex)
            val remaining = conditions.subList(unknownIndex + 1, conditions.size)
            allCombinations(remaining).forEach { tail ->
                yield(fixedPart + listOf(SpringCondition.OPERATIONAL) + tail)
                yield(fixedPart + listOf(SpringCondition.BROKEN) + tail)
            }
        }
    }

    private fun parseFast(input: List<String>): List<Pair<String, List<Int>>> {
        return input.map { line ->
            val (conditionString, groupSizes) = line.split(" ")
            conditionString
                .replace('?', 'U')
                .replace('#', 'B')
                .replace("\\.+".toRegex(), "O") to groupSizes.split(",").map { it.toInt() }
        }
    }

    fun part1Fast(input: List<String>, terminateEarly: Boolean = true): Long {
        val parsed = parseFast(input)
        return parsed.sumOf { (conditions, groupSizes) ->
            arrangementCount(conditions, groupSizes, terminateEarly)
        }
    }

    private fun arrangementCount(conditions: String, groupSizes: List<Int>, terminateEarly: Boolean): Long {
        if (terminateEarly) {
            // do some quick checks for hopefully some early termination optimization
            val remainingBroken = conditions.count { it == 'B' }
            val remainingUnknown = conditions.count { it == 'U' }
            val expectedBroken = groupSizes.sum()
            if (remainingBroken > expectedBroken) {
                return 0
            }
            if (remainingBroken + remainingUnknown < expectedBroken) {
                return 0
            }
            if (remainingUnknown == 0 && remainingBroken == expectedBroken) {
                return if (actualGroupSizes(conditions) == groupSizes) 1 else 0
            }
            // this one is still needed for not early termination
            if (expectedBroken == 0) {
                return 1
            }
        }
        // should never throw if we have the terminateEarly branch
        val nextGroupSize = groupSizes.first()
        val match = """^[^B]*?(?<brokens>[BU]{$nextGroupSize})(U|O|$)""".toRegex().find(conditions) ?: return 0

        val withConsumed = arrangementCount(conditions.substring(match.range.last + 1), groupSizes.drop(1), terminateEarly)
        val brokens = match.groups["brokens"]
        return if (brokens?.value?.first() == 'U') {
            withConsumed + arrangementCount(
                conditions.substring(brokens.range.first + 1),
                groupSizes,
                terminateEarly
            )
        } else {
            withConsumed
        }
    }

    private fun actualGroupSizes(conditions: List<SpringCondition>): List<Int> {
        return conditions
            .split { it == SpringCondition.OPERATIONAL }
            .map { it.size }
            .filter { it > 0 }
    }

    private fun actualGroupSizes(conditions: String): List<Int> {
        return conditions
            .split("O")
            .map { it.length }
            .filter { it > 0 }
    }

    fun debug(input: List<String>) {
        val parsedExpected = parse(input)
        val parsedActual = parseFast(input)
        parsedExpected.zip(parsedActual).forEach { (expected, actual) ->
            val expectedCount = arrangementCount(expected.first, expected.second)
            val actualCount = arrangementCount(actual.first, actual.second, true)
            if (expectedCount.toLong() == actualCount) {
                println("OK: ${expected.first} ${expected.second} -> $expectedCount")
            } else {
                println("FAIL: ${expected.first} ${expected.second} -> $expectedCount != $actualCount")
                println(actual)
                return
            }
        }
    }

    fun part2(input: List<String>): Long {
        val parsed = parse(input)
        return 0L
    }
}

fun main() {
    val testInput = """
        ???.### 1,1,3
        .??..??...?##. 1,1,3
        ?#?#?#?#?#?#?#? 1,3,1,6
        ????.#...#... 4,1,1
        ????.######..#####. 1,6,5
        ?###???????? 3,2,1
    """.trimIndent().split("\n")
    println("------Tests------")
    println("${Day12.part1(testInput)}")
    println("${Day12.part1Fast(testInput)}")
    println(Day12.part2(testInput))

    println("------Real------")
    val input = readInput(2023, 12)
    //println("Part 1 result: ${Day12.part1(input)}")
    println("Part 1 result: ${Day12.part1Fast(input)}")
    println("Part 2 result: ${Day12.part2(input)}")
    //timingStatistics { Day12.part1(input) }
    timingStatistics { Day12.part1Fast(input) }
    //timingStatistics { Day12.part2(input) }
}