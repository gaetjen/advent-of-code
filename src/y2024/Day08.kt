package y2024

import util.Pos
import util.generateTakes
import util.minus
import util.plus
import util.readInput
import util.timingStatistics

data class AntennaMap(
    val size: Pos,
    val antennaPositions: Map<Char, Set<Pos>>
) {
    fun outsideMap(p: Pos): Boolean {
        return p.first < 0 || p.second < 0 || p.first > size.first - 1 || p.second > size.second - 1
    }
}

object Day08 {
    private fun parse(input: List<String>): AntennaMap {
        val result = mutableMapOf<Char, Set<Pos>>()
        input.withIndex().forEach { (row, line) ->
            line.withIndex().forEach { (col, c) ->
                if (c != '.') {
                    result.compute(c) { _, locations ->
                        if (locations == null) {
                            setOf(row to col)
                        } else {
                            locations + (row to col)
                        }
                    }
                }
            }
        }
        return AntennaMap(
            input.size to input[0].length,
            result
        )
    }

    fun part1(input: List<String>): Int {
        val map = parse(input)
        val antiNodes = map.antennaPositions.flatMap { (_, positions) ->
            antiNodes(positions)
        }.toSet().filter {
            !map.outsideMap(it)
        }
        return antiNodes.size
    }

    private fun antiNodes(antennas: Set<Pos>): Set<Pos> {
        return generateTakes(antennas.toList(), 2).flatMap { (a1, a2) ->
            val diff = a1 - a2
            setOf(
                a1 + diff,
                a1 - diff,
                a2 + diff,
                a2 - diff
            ) - setOf(a1, a2)
        }.toSet()
    }

    fun part2(input: List<String>): Int {
        val map = parse(input)
        val antiNodes = map.antennaPositions.flatMap { (_, positions) ->
            antiNodesPart2(map, positions)
        }.toSet()
        return antiNodes.size
    }

    private fun antiNodesPart2(
        map: AntennaMap,
        positions: Set<Pos>
    ): Set<Pos> {
        return generateTakes(positions.toList(), 2).flatMap { (a1, a2) ->
            val diff = a1 - a2
            val increasing = generateSequence(a1) { s ->
                s + diff
            }.takeWhile { !map.outsideMap(it) }
            val decreasing = generateSequence(a1) { s ->
                s - diff
            }.takeWhile { !map.outsideMap(it) }
            increasing + decreasing
        }.toSet()
    }
}

fun main() {
    val testInput = """
        ............
        ........0...
        .....0......
        .......0....
        ....0.......
        ......A.....
        ............
        ............
        ........A...
        .........A..
        ............
        ............
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day08.part1(testInput))
    println(Day08.part2(testInput))

    println("------Real------")
    val input = readInput(2024, 8)
    println("Part 1 result: ${Day08.part1(input)}")
    println("Part 2 result: ${Day08.part2(input)}")
    timingStatistics { Day08.part1(input) }
    timingStatistics { Day08.part2(input) }
}