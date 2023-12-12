package y2023

import util.readInput
import util.split
import util.timingStatistics
import y2023.Day12.part1Fast

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

    private val cache = mutableMapOf<Pair<String, List<Int>>, Long>()

    private fun arrangementCount(conditions: String, groupSizes: List<Int>, terminateEarly: Boolean): Long {
        val cached = cache[conditions to groupSizes]
        if (cached != null) {
            return cached
        }
        if (terminateEarly) {
            // do some quick checks for hopefully some early termination optimization
            val remainingBroken = conditions.count { it == 'B' }
            val remainingUnknown = conditions.count { it == 'U' }
            val expectedBroken = groupSizes.sum()
            if (remainingBroken > expectedBroken) {
                cache[conditions to groupSizes] = 0
                return 0
            }
            if (remainingBroken + remainingUnknown < expectedBroken) {
                cache[conditions to groupSizes] = 0
                return 0
            }
            if (remainingUnknown == 0 && remainingBroken == expectedBroken) {
                return if (actualGroupSizes(conditions) == groupSizes) {
                    cache[conditions to groupSizes] = 1
                    1
                } else{
                    cache[conditions to groupSizes] = 0
                    0
                }
            }
            // this one is still needed for not early termination
            if (expectedBroken == 0) {
                cache[conditions to groupSizes] = 1
                return 1
            }
        }
        // should never throw if we have the terminateEarly branch
        val nextGroupSize = groupSizes.first()
        // we may not skip any broken springs
        // then we take the correct number of broken or unknown springs
        // it may not be followed immediately by a broken spring, i.e. next is unknown, operational or end of string
        val match = """^[^B]*?(?<brokens>[BU]{$nextGroupSize})(U|O|$)""".toRegex().find(conditions) ?: return 0

        val withConsumed = arrangementCount(conditions.substring(match.range.last + 1), groupSizes.drop(1), terminateEarly)
        val brokens = match.groups["brokens"]
        return if (brokens?.value?.first() == 'U') {
            val operationalFirst = arrangementCount(
                conditions.substring(brokens.range.first + 1),
                groupSizes,
                terminateEarly
            )
            cache[conditions to groupSizes] = withConsumed + operationalFirst
            withConsumed + operationalFirst
        } else {
            cache[conditions to groupSizes] = withConsumed
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

    fun part2(input: List<String>, withLogging: Boolean = false): Long {
        val parsed = parseFast(input)
        var progress = 0
        val counts =  parsed.map { (conditions, groupSizes) ->
            val expandedConditions = List(5) { conditions }.joinToString(separator = "U")
            val expandedGroupSizes = List(5) { groupSizes }.flatten()
            if (withLogging) print("${++progress}/${parsed.size} $expandedConditions $expandedGroupSizes")
            val count = arrangementCount(expandedConditions, expandedGroupSizes, true)
            if (withLogging) println(" -> $count")
            count
        }
        if (withLogging) {
            println("Cache: ${cache.size}")
        }
        cache.clear()
        return counts.sum()
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
    println(Day12.part1(testInput))
    println(part1Fast(testInput))
    println(Day12.part2(testInput))

    println("------Real------")
    val input = readInput(2023, 12)
    //println("Part 1 result: ${Day12.part1(input)}")
    println("Part 1 result: ${part1Fast(input)}")
    println("Part 2 result: ${Day12.part2(input, true)}")
    //timingStatistics { Day12.part1(input) }
    timingStatistics { part1Fast(input) }
    timingStatistics { Day12.part2(input) }
}