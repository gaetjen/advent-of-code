package y2024

import util.readInput
import util.split
import util.timingStatistics

sealed interface Instruction

data object Enable: Instruction
data object Disable: Instruction
data class Mul(val a: Int, val b: Int): Instruction

object Day03 {
    private fun parse(input: List<String>): List<Instruction> {
        return input.flatMap { line ->
            Regex("mul\\((\\d{1,3}),(\\d{1,3})\\)|(do\\(\\))|(don't\\(\\))").findAll(line).map {
                val (a, b, enable, disable) = it.destructured
                when {
                    enable.isNotEmpty() -> Enable
                    disable.isNotEmpty() -> Disable
                    else -> Mul(a.toInt(), b.toInt())
                }
            }.toList()
        }
    }

    fun part1(input: List<String>): Int {
        val parsed = parse(input)
        return parsed.sumOf { ins ->
            when (ins) {
                is Enable -> 0
                is Disable -> 0
                is Mul -> ins.a * ins.b
            }
        }
    }

    fun part2(input: List<String>): Int {
        val parsed = parse(input)
        return parsed.split(matchInPost = true) { it is Enable }
            .flatMap { enabledInstructions ->
                enabledInstructions.takeWhile { it !is Disable }
            }.sumOf {
                if (it is Mul) it.a * it.b else 0
            }
    }
}

fun main() {
    val testInput = """
        xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day03.part1(testInput))
    println(Day03.part2(testInput))

    println("------Real------")
    val input = readInput(2024, 3)
    println("Part 1 result: ${Day03.part1(input)}")
    println("Part 2 result: ${Day03.part2(input)}")
    timingStatistics { Day03.part1(input) }
    timingStatistics { Day03.part2(input) }
}