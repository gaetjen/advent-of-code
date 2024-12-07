package y2024

import util.readInput
import util.timingStatistics

data class Equation(
    val result: Long,
    val operands: List<Long>
) {
    fun isSolvedBy(operators: List<(Long, Long) -> Long>): Boolean {
        val start = operands.first()
        return operands.drop(1).zip(operators).fold(initial = start) { acc, (operand, operator) ->
            operator(acc, operand)
        } == result
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
        return equations.filter { isSolvable(it) }.sumOf { it.result }
    }

    private val operatorsPart1 = listOf<(Long, Long) -> Long>(
        { a, b -> a + b },
        { a, b -> a * b }
    )

    private val operatorsPart2 = operatorsPart1 + listOf(
        { a, b -> "$a$b".toLong() }
    )

    fun isSolvable(equation: Equation, operators: List<(Long, Long) -> Long> = operatorsPart1): Boolean {
        val numberOperators = equation.operands.size - 1
        val operandCombinations = combinations(operators, numberOperators)
        operandCombinations.forEach {
            if (equation.isSolvedBy(it)) {
                return true
            }
        }
        return false
    }

    /**
     * generate all the ways to select n elements from list (respecting order)
     */
    fun <T> combinations(
        list: List<T>,
        n: Int
    ): List<List<T>> {
        if (n == 1) {
            return list.map { listOf(it) }
        }
        val tails = combinations(list, n - 1)
        return tails.flatMap { tail ->
            list.map { listOf(it) + tail }
        }
    }

    fun part2(input: List<String>): Long {
        val equations = parse(input)
        return equations.filter { isSolvable(it, operatorsPart2) }.sumOf { it.result }
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