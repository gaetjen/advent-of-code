package y2017

import util.readInput
import util.timingStatistics

object Day23 {
    fun part1(input: List<String>): Number {
        val parsed = Day18.parse(input)
        val machine = DuetMachine(parsed, DuetMachineState())
        return sequence {
            with(machine) {
                yield(run().mulCounter)
            }
        }.last()
    }

    fun part2(input: List<String>): Long {
        //val parsed = parse(input)
        return 0L
    }
}

fun main() {
    println("------Real------")
    val input = readInput(2017, 23)
    println("Part 1 result: ${Day23.part1(input)}")
    println("Part 2 result: ${Day23.part2(input)}")
    timingStatistics { Day23.part1(input) }
    timingStatistics { Day23.part2(input) }
}