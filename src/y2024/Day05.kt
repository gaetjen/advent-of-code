package y2024

import util.Pos
import util.readInput
import util.split
import util.timingStatistics

object Day05 {
    private fun parse(input: List<String>): Pair<List<Pos>, List<Map<Int, Int>>> {
        val (ruleInputs, pagesInput) = input.split { it.isBlank() }
        val rules = ruleInputs.map { line ->
            line.split("|").map { it.toInt() }
        }.map { it[0] to it[1] }

        val pages = pagesInput.map { line ->
            line.split(",").withIndex().associate { (idx, n) -> n.toInt() to idx }
        }

        return rules to pages
    }

    fun part1(input: List<String>): Int {
        val (rules, pagePositions) = parse(input)
        return pagePositions.filter { pages ->
            rules.all { (first, second) ->
                val pageFirst = pages[first]
                val pageSecond = pages[second]
                pageFirst == null || pageSecond == null || pageFirst < pageSecond
            }
        }.sumOf { middle(it) }
    }

    fun middle(list: Map<Int, Int>): Int {
        return list.entries.sortedBy { it.value }.map { it.key }[list.size / 2]
    }

    fun part2(input: List<String>): Int {
        val (rules, pagePositions) = parse(input)
        val incorrect = pagePositions.filter { pages ->
            rules.any { (first, second) ->
                val pageFirst = pages[first]
                val pageSecond = pages[second]
                !(pageFirst == null || pageSecond == null || pageFirst < pageSecond)
            }
        }
        return incorrect.sumOf { pages ->
            val applicableRules = rules.filter { pages[it.first] != null && pages[it.second] != null }
            val pageNumberFirstCounts = applicableRules.map { it.first }.groupingBy { it }.eachCount()
            val sorted = pages.keys.sortedByDescending { pageNumberFirstCounts[it] }
            val foo = sorted.withIndex().associate { (idx, n) -> n to idx }
            if (applicableRules.any { (first, second) ->
                val pageFirst = foo[first]
                val pageSecond = foo[second]
                pageFirst == null || pageSecond == null || pageFirst > pageSecond

            }) {
                println("Not correctly sorted: $sorted")
            }
            sorted[pages.size / 2]
        }
    }
}

fun main() {
    val testInput = """
        47|53
        97|13
        97|61
        97|47
        75|29
        61|13
        75|53
        29|13
        97|29
        53|29
        61|53
        97|53
        61|29
        47|13
        75|47
        97|75
        47|61
        75|61
        47|29
        75|13
        53|13

        75,47,61,53,29
        97,61,53,29,13
        75,29,13
        75,97,47,61,53
        61,13,29
        97,13,75,29,47
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day05.part1(testInput))
    println(Day05.part2(testInput))

    println("------Real------")
    val input = readInput(2024, 5)
    println("Part 1 result: ${Day05.part1(input)}")
    println("Part 2 result: ${Day05.part2(input)}")
    timingStatistics { Day05.part1(input) }
    timingStatistics { Day05.part2(input) }
}