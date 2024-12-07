package y2024

import util.readInput
import util.timingStatistics

data class Equation(
    val result: Long,
    val operands: List<Long>
) {
    fun canBeSolvedWith(operators: List<(Long, Long) -> Long>): Boolean {
        return canBeSolvedWith(operands.first(), operands.drop(1), operators)
    }

    private fun canBeSolvedWith(left: Long, tail: List<Long>, operators: List<(Long, Long) -> Long>) : Boolean {
        if (left > result) return false
        if (tail.size == 1) {
            operators.forEach { op ->
                if (op(left, tail.first()) == result) return true
            }
            return false
        }
        operators.forEach { op ->
            val nextLeft = op(left, tail.first())
            if (canBeSolvedWith(nextLeft, tail.drop(1), operators)) {
                return true
            }
        }
        return false
    }
}

object Day07 {
    private fun parse(input: List<String>): List<Equation> {
        return input.map { line ->
            val (result, operands) = line.split(": ")
            Equation(
                result = result.toLong(),
                operands = operands.split(" ").map { it.toLong() }
            )
        }
    }

    fun part1(input: List<String>): Long {
        val equations = parse(input)
        return equations.filter { it.canBeSolvedWith(operatorsPart1) }.sumOf { it.result }
    }

    private val operatorsPart1 = listOf<(Long, Long) -> Long>(
        { a, b -> a + b },
        { a, b -> a * b }
    )

    private val operatorsPart2 = operatorsPart1 + listOf(
        { a, b -> "$a$b".toLong() }
    )

    fun part2(input: List<String>): Long {
        val equations = parse(input)
        return equations.filter { it.canBeSolvedWith(operatorsPart2) }.sumOf { it.result }
    }
}

fun main() {
    val testInput = """
        190: 10 19
        3267: 81 40 27
        83: 17 5
        156: 15 6
        7290: 6 8 6 15
        161011: 16 10 13
        192: 17 8 14
        21037: 9 7 18 13
        292: 11 6 16 20
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day07.part1(testInput))
    println(Day07.part2(testInput))

    println("------Real------")
    val input = readInput(2024, 7)
    println("Part 1 result: ${Day07.part1(input)}")
    println("Part 2 result: ${Day07.part2(input)}")
    timingStatistics { Day07.part1(input) }
    timingStatistics { Day07.part2(input) }
}