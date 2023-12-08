package y2023

import util.product
import util.readInput
import util.timingStatistics
import y2015.Day20
import y2022.Day15.toPair

object Day08 {
    private fun parse(input: List<String>): Pair<String, Map<String, Pair<String, String>>> {
        val leftRight = input.first()

        val map = input.drop(2).map { line ->
            val (left, right) = line.split(" = ")
            left to right.replace("(", "").replace(")", "").split(", ").toPair()
        }.toMap()
        return leftRight to map
    }

    fun part1(input: List<String>): Int {
        val (moves, map) = parse(input)
        var steps = 0
        var current = "AAA"
        while (current != "ZZZ") {
            current = if (moves[steps % moves.length] == 'L') {
                map[current]!!.first
            } else {
                map[current]!!.second

            }
            steps++
        }
        return steps
    }

    fun part2x(input: List<String>): Long {
        val (moves, map) = parse(input)
        val starts = map.keys.filter { it.endsWith('A') }
        var steps = 0L
        var currents = starts
        while (currents.any { !it.endsWith('Z') }) {
            currents = currents.map {
                if (moves[(steps % moves.length).toInt()] == 'L') {
                    map[it]!!.first
                } else {
                    map[it]!!.second
                }
            }
            steps++
        }
        return steps
    }

    fun part2(input: List<String>): Long {
        val (moves, map) = parse(input)
        val starts = map.keys.filter { it.endsWith('A') }
        val cycles = starts.map { getCycle(moves, map, it) }
        val cycleLengths = cycles.map { it.last().stepsSinceLast }
        val factors = cycleLengths.map { Day20.factors(it.toInt()) }
        val uncommonFactors = factors.map { it[2] }
        val common = factors.first().last()
        val lcm = uncommonFactors.map { it.toLong() }.product() * common
        return lcm
    }

    fun getCycle(moves: String, map: Map<String, Pair<String, String>>, start: String): MutableList<CycleStep> {
        var steps = 0L
        var current = start
        val cycle = mutableListOf<CycleStep>()
        while (true) {
            current = if (moves[(steps % moves.length).toInt()] == 'L') {
                map[current]!!.first
            } else {
                map[current]!!.second
            }
            if (current.endsWith('Z')) {
                cycle.add(
                    CycleStep(
                        steps,
                        steps - (cycle.lastOrNull()?.totalSteps ?: 0),
                        steps % moves.length,
                        current
                    )
                )
            }
            if (cycle.distinctBy { "${it.instructionPosition} ${it.position}" }.size < cycle.size) {
                break
            }
            steps++
        }
        return cycle
    }

    data class CycleStep(
        val totalSteps: Long,
        val stepsSinceLast: Long,
        val instructionPosition: Long,
        val position: String
    )
}

fun main() {
    val testInput = """
        RL

        AAA = (BBB, CCC)
        BBB = (DDD, EEE)
        CCC = (ZZZ, GGG)
        DDD = (DDD, DDD)
        EEE = (EEE, EEE)
        GGG = (GGG, GGG)
        ZZZ = (ZZZ, ZZZ)
    """.trimIndent().split("\n")

    val testInput2 = """
        LR

        11A = (11B, XXX)
        11B = (XXX, 11Z)
        11Z = (11B, XXX)
        22A = (22B, XXX)
        22B = (22C, 22C)
        22C = (22Z, 22Z)
        22Z = (22B, 22B)
        XXX = (XXX, XXX)
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day08.part1(testInput))
    //println(Day08.part2(testInput2))

    println("------Real------")
    val input = readInput(2023, 8)
    println("Part 1 result: ${Day08.part1(input)}")
    println("Part 2 result: ${Day08.part2(input)}")
    timingStatistics { Day08.part1(input) }
    timingStatistics { Day08.part2(input) }
}