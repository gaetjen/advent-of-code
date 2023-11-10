package y2015

import util.generateTakes
import util.product
import util.readInput

object Day24 {
    private fun parse(input: List<String>): List<Long> {
        return input.map { it.toLong() }
    }

    fun part1(input: List<String>, compartments: Int): Long {
        val weights = parse(input)
        val total = weights.sum()
        val perCompartment = total / compartments
        println("total: $total")
        println("per compartment: $perCompartment")
        var passengerSize = 1
        var passengerCandidates: List<List<Long>>
        while (true) {
            passengerCandidates = generateTakes(weights, passengerSize).filter {
                it.sum() == perCompartment
            }.toList()
            if (passengerCandidates.isNotEmpty()) {
                break
            } else {
                passengerSize++
            }
        }
        return passengerCandidates.minOf {
            it.product()
        }
    }
}

fun main() {
    val testInput = """
        1
        2
        3
        4
        5
        7
        8
        9
        10
        11
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day24.part1(testInput, 3))
    println(Day24.part1(testInput, 4))

    println("------Real------")
    val input = readInput("resources/2015/day24")
    println(Day24.part1(input, 3))
    println(Day24.part1(input, 4))
}
