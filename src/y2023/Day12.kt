package y2023

import util.readInput
import util.split

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

    /*private fun arrangementCount(conditions: List<SpringCondition>, groupSizes: List<Int>): Int {
        val actual = actualGroupSizes(conditions)
        if (conditions.count { it == SpringCondition.UNKNOWN } == 0) {
            return if (actual == groupSizes) {
                1
            } else {
                0
            }
        }

        val unknownIndex = conditions.indexOfFirst { it == SpringCondition.UNKNOWN }
        if (unknownIndex > 0) {
            val fixedPart = conditions.subList(0, unknownIndex + 1)
            val fixedPartSizes = actualGroupSizes(fixedPart.dropLast(1))

            val isMatching = fixedPartSizes.zip(groupSizes).all { it.first == it.second }
            val lastFixedPart = fixedPart[unknownIndex - 1]
            // the counts are matching and the last part is broken so the unknown element must be operational
            if (isMatching && lastFixedPart == SpringCondition.BROKEN) {

            }
        }

        if (!isMatching) {
            return 0
        }

        // If the last fixed part is operational, the next
        if (conditions[unknownIndex - 1] == SpringCondition.OPERATIONAL) {
            return arrangementCount(conditions.subList(unknownIndex + 1, conditions.size), groupSizes.drop(fixedPartSizes.size))
        }
        val remaining = conditions.subList(unknownIndex, conditions.size)
        check(remaining[0] == SpringCondition.UNKNOWN)


        TODO("Not yet implemented")
    }*/

    private fun actualGroupSizes(conditions: List<SpringCondition>): List<Int> {
        return conditions
            .split { it == SpringCondition.OPERATIONAL }
            .map { it.size }
            .filter { it > 0 }
    }

    fun part2(input: List<String>): Long {
        val parsed = parse(input)
        return 0L
    }
}

fun main() {
    val a = listOf(1, 2, 3, 4, 5, 6, 7, 8)
    val b = listOf(1, 2, 3)
    println(a.zip(b))
    println(b.subList(3, 3))
    val testInput = """
        ..###...?..##.?.### 3,2,3
        ???.### 1,1,3
        .??..??...?##. 1,1,3
        ?#?#?#?#?#?#?#? 1,3,1,6
        ????.#...#... 4,1,1
        ????.######..#####. 1,6,5
        ?###???????? 3,2,1
    """.trimIndent().split("\n")
    println("------Tests------")
    println("${Day12.part1(testInput)}, expected 22")
    println(Day12.part2(testInput))

    println("------Real------")
    val input = readInput(2023, 12)
    println("Part 1 result: ${Day12.part1(input)}")
    println("Part 2 result: ${Day12.part2(input)}")
    //timingStatistics { Day12.part1(input) }
    //timingStatistics { Day12.part2(input) }
}