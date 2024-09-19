package y2017

import util.PosL
import util.timingStatistics
import kotlin.experimental.xor

object Day15 {
    val factorA = 16807
    val factorB = 48271

    fun generate(
        start: Long,
        factor: Int
    ) = sequence {
        var current = start
        while (true) {
            current = (current * factor) % 2_147_483_647
            yield(current)
        }
    }

    fun generatePart2(
        start: Long,
        factor: Int,
        divisible: Int
    ) = sequence {
        var current = start
        while (true) {
            current = (current * factor) % 2_147_483_647
            if ((current % divisible) == 0L) yield(current)
        }
    }


    fun part1(input: PosL): Int {
        return generate(input.first, factorA).zip(generate(input.second, factorB))
            .take(40_000_000)
            .count { (a, b) ->
                a.toShort().xor(b.toShort()) == 0.toShort()
            }
    }

    fun part2(input: PosL): Int {
        return generatePart2(input.first, factorA, 4).zip(generatePart2(input.second, factorB, 8))
            .take(5_000_000)
            .count { (a, b) ->
                a.toShort().xor(b.toShort()) == 0.toShort()
            }
    }
}

fun main() {
    val testInput = 65L to 8921L
    println("------Tests------")
    println(Day15.part1(testInput))
    println(Day15.part2(testInput))

    println("------Real------")
    val input = 116L to 299L
    println("Part 1 result: ${Day15.part1(input)}")
    println("Part 2 result: ${Day15.part2(input)}")
    timingStatistics { Day15.part1(input) }
    timingStatistics { Day15.part2(input) }
}