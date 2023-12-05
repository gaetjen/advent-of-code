package y2023

import util.measuredTime
import util.readInput
import util.split

object Day05 {
    data class MyMap(val destinationStart: Long, val sourceRange: LongRange) {
        fun destinationFor(source: Long): Long {
            return destinationStart + (source - sourceRange.first)
        }
    }

    private fun parse(input: List<String>): Pair<List<Long>, List<List<MyMap>>> {
        val parts = input.split { it.isEmpty() }
        val seeds = parts[0][0].split(" ").drop(1).map { it.toLong() }
        val maps = parts.drop(1).map { map ->
            map.drop(1).map { line ->
                val (destinationStart, sourceStart, range) = line.split(" ").map { it.toLong() }
                MyMap(
                    destinationStart,
                    sourceStart until sourceStart + range
                )
            }
        }
        return seeds to maps
    }

    fun part1(input: List<String>): Long {
        val (seeds, maps) = parse(input)
        val locations = seeds.map { seed ->
            maps.fold(seed) { currentNumber, map ->
                val match = map.find { currentNumber in it.sourceRange }
                match?.destinationFor(currentNumber) ?: currentNumber
            }
        }
        return locations.min()
    }

    fun part2(input: List<String>): Long {
        val (seeds, maps) = parse(input)
        val seedRanges = seeds.chunked(2).map { it[0] until it[0] + it[1] }
        val locationRanges = maps.fold(seedRanges) { currentRanges, map ->
            // val sumOfRanges = currentRanges.sumOf { it.last - it.first + 1 }
            // println("sum of Ranges: $sumOfRanges")
            val allCoveringMap = getAllCoveringMap(map)
            currentRanges.flatMap { range ->
                val overlapping = allCoveringMap
                    .dropWhile { it.sourceRange.last < range.first }
                    .takeWhile { it.sourceRange.first <= range.last }
                overlapping.mapIndexed { index, myMap ->
                    val start = if (index == 0) {
                        myMap.destinationFor(range.first)
                    } else {
                        myMap.destinationFor(myMap.sourceRange.first)
                    }
                    val end = if (index == overlapping.size - 1) {
                        myMap.destinationFor(range.last)
                    } else {
                        myMap.destinationFor(myMap.sourceRange.last)
                    }
                    start..end
                }
            }
        }
        return locationRanges.minOf { it.first }
    }

    fun part2Naive(input: List<String>): Long {
        val (seeds, maps) = parse(input)
        val seedRanges = seeds.chunked(2).map { it[0] until it[0] + it[1] }
        return seedRanges.minOf { range ->
            println("evaluating range $range -> ${range.last - range.first + 1} seeds")
            range.minOf { seed ->
                maps.fold(seed) { currentNumber, map ->
                    val match = map.find { currentNumber in it.sourceRange }
                    match?.destinationFor(currentNumber) ?: currentNumber
                }
            }
        }
    }

    private fun getAllCoveringMap(exampleMap: List<MyMap>): List<MyMap> {
        val sorted = exampleMap.sortedBy { it.sourceRange.first }
        val basicCovers = sorted.windowed(2)
            .flatMap { (first, second) ->
                if (first.sourceRange.last + 1 == second.sourceRange.first) {
                    // no gap to cover
                    listOf(first)
                } else {
                    listOf(
                        first,
                        MyMap(
                            first.sourceRange.last + 1,
                            first.sourceRange.last + 1 until second.sourceRange.first
                        )
                    )
                }
            }
        val start = if (sorted.first().sourceRange.first == 0L) {
            listOf()
        } else {
            listOf(MyMap(0, 0 until sorted.first().sourceRange.first))
        }
        return start + basicCovers + listOf(
            sorted.last(),
            MyMap(sorted.last().sourceRange.last + 1, sorted.last().sourceRange.last + 1..Long.MAX_VALUE)
        )
    }
}

fun main() {
    val testInput = """
        seeds: 79 14 55 13

        seed-to-soil map:
        50 98 2
        52 50 48

        soil-to-fertilizer map:
        0 15 37
        37 52 2
        39 0 15

        fertilizer-to-water map:
        49 53 8
        0 11 42
        42 0 7
        57 7 4

        water-to-light map:
        88 18 7
        18 25 70

        light-to-temperature map:
        45 77 23
        81 45 19
        68 64 13

        temperature-to-humidity map:
        0 69 1
        1 0 69

        humidity-to-location map:
        60 56 37
        56 93 4
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day05.part1(testInput))
    println(Day05.part2(testInput))

    println("------Real------")
    val input = readInput(2023, 5)
    measuredTime { Day05.part1(input) }
    measuredTime { Day05.part2(input) }
    measuredTime { Day05.part2Naive(input) }
}