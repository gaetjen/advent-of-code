package y2024

import util.PosL
import util.readInput
import util.split
import util.times
import util.timingStatistics
import kotlin.math.roundToLong

data class ClawMachine(
    val buttonA: PosL,
    val buttonB: PosL,
    val prize: PosL
)

object Day13 {
    private fun parse(input: List<String>): List<ClawMachine> {
        val regexButton = """Button [AB]: X\+(\d+), Y\+(\d+)""".toRegex()
        val regexPrize = """Prize: X=(\d+), Y=(\d+)""".toRegex()
        return input.split { it.isBlank() }.map { (a, b, p) ->
            val (ax, ay) = regexButton.matchEntire(a)!!.groupValues.drop(1).map { it.toLong() }
            val (bx, by) = regexButton.matchEntire(b)!!.groupValues.drop(1).map { it.toLong() }
            val (px, py) = regexPrize.matchEntire(p)!!.groupValues.drop(1).map { it.toLong() }
            ClawMachine(
                ax to ay,
                bx to by,
                px to py
            )
        }
    }


    fun part1(input: List<String>): Long {
        val clawMachines = parse(input)
        return clawMachines.sumOf { solve(it) }
    }

    /**
     * m * ax + n * bx = px
     * m * ay + n * by = py
     * m = (px - n * bx) / ax
     * (px - n * bx) * ay / ax + n * by = py
     * (px * ay - n * ay * bx) / ax + n * by = py
     * n * (by - ay * bx / ax) = py - px * ay / ax
     * n = (py - (px * ay) / ax) )/ (by - ay * bx / ax)
     */
    private fun solve(clawMachine: ClawMachine): Long {
        val n =
            ((clawMachine.prize.second - (clawMachine.prize.first * clawMachine.buttonA.second).toDouble() / clawMachine.buttonA.first) /
                    (clawMachine.buttonB.second - clawMachine.buttonA.second * clawMachine.buttonB.first.toDouble() / clawMachine.buttonA.first)).roundToLong()
        val m = (clawMachine.prize.first - n * clawMachine.buttonB.first) / clawMachine.buttonA.first
        return if ((clawMachine.buttonA * m) * (clawMachine.buttonB * n) == clawMachine.prize) {
            (m * 3 + n)
        } else {
            0
        }
    }

    fun part2(input: List<String>): Long {
        val clawMachines = parse(input).map {
            it.copy(
                prize = it.prize * (10_000_000_000_000L to 10_000_000_000_000L)
            )
        }
        return clawMachines.sumOf { solve(it) }
    }
}

fun main() {
    val testInput = """
        Button A: X+94, Y+34
        Button B: X+22, Y+67
        Prize: X=8400, Y=5400

        Button A: X+26, Y+66
        Button B: X+67, Y+21
        Prize: X=12748, Y=12176

        Button A: X+17, Y+86
        Button B: X+84, Y+37
        Prize: X=7870, Y=6450

        Button A: X+69, Y+23
        Button B: X+27, Y+71
        Prize: X=18641, Y=10279
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day13.part1(testInput))
    println(Day13.part2(testInput))

    println("------Real------")
    val input = readInput(2024, 13)
    println("Part 1 result: ${Day13.part1(input)}")
    println("Part 2 result: ${Day13.part2(input)}")
    timingStatistics { Day13.part1(input) }
    timingStatistics { Day13.part2(input) }
}