package y2024

import util.AbstractInstruction
import util.AbstractMachine
import util.product
import util.readInput
import util.split
import util.timingStatistics

data class ThreeBitMachine(
    override val instructions: List<ThreeBitInstruction>,
    override var state: ThreeBitMachineState
) : AbstractMachine<ThreeBitMachineState>() {

}

data class ThreeBitMachineState(
    val registers: MutableMap<Char, Long>,
    val output: MutableList<Int> = mutableListOf(),
) {
    fun comboOperand(operand: Int): Long {
        return when {
            operand <= 3 -> operand.toLong()
            operand == 4 -> registers['A'] ?: 0L
            operand == 5 -> registers['B'] ?: 0L
            operand == 6 -> registers['C'] ?: 0L
            else -> error("Invalid combo operand: $operand")
        }
    }
}

sealed class ThreeBitInstruction : AbstractInstruction<ThreeBitMachineState>() {
    data class Adv(val operand: Int) : ThreeBitInstruction() {
        override fun executeOn(
            state: ThreeBitMachineState,
            idx: Int
        ): Pair<ThreeBitMachineState, Int> {
            val numerator = state.registers['A'] ?: 0
            val denominator = List(state.comboOperand(operand).toInt()) { 2 }.product()
            state.registers['A'] = numerator / denominator
            return state to idx + 2
        }
    }

    data class Bxl(val operand: Int) : ThreeBitInstruction() {
        override fun executeOn(
            state: ThreeBitMachineState,
            idx: Int
        ): Pair<ThreeBitMachineState, Int> {
            state.registers['B'] = state.registers['B']?.xor(operand.toLong()) ?: error("Register B is null")
            return state to idx + 2
        }
    }

    data class Bst(val operand: Int) : ThreeBitInstruction() {
        override fun executeOn(
            state: ThreeBitMachineState,
            idx: Int
        ): Pair<ThreeBitMachineState, Int> {
            state.registers['B'] = state.comboOperand(operand) % 8
            return state to idx + 2
        }
    }

    data class Jnz(val operand: Int) : ThreeBitInstruction() {
        override fun executeOn(
            state: ThreeBitMachineState,
            idx: Int
        ): Pair<ThreeBitMachineState, Int> {
            return if (state.registers['A'] == 0L) {
                state to idx + 2
            } else {
                state to operand
            }
        }
    }

    data object Bxc : ThreeBitInstruction() {
        override fun executeOn(
            state: ThreeBitMachineState,
            idx: Int
        ): Pair<ThreeBitMachineState, Int> {
            state.registers['B'] = state.registers['B']?.xor(state.registers['C']!!)!!
            return state to idx + 2
        }
    }

    data class Out(val operand: Int) : ThreeBitInstruction() {
        override fun executeOn(
            state: ThreeBitMachineState,
            idx: Int
        ): Pair<ThreeBitMachineState, Int> {
            state.output.add((state.comboOperand(operand) % 8).toInt())
            return state to idx + 2
        }
    }

    data class Bdv(val operand: Int) : ThreeBitInstruction() {
        override fun executeOn(
            state: ThreeBitMachineState,
            idx: Int
        ): Pair<ThreeBitMachineState, Int> {
            val numerator = state.registers['A'] ?: error("Register A is null")
            val denominator = List(state.comboOperand(operand).toInt()) { 2 }.product()
            state.registers['B'] = numerator / denominator
            return state to idx + 2
        }
    }

    data class Cdv(val operand: Int) : ThreeBitInstruction() {
        override fun executeOn(
            state: ThreeBitMachineState,
            idx: Int
        ): Pair<ThreeBitMachineState, Int> {
            val numerator = state.registers['A'] ?: error("Register B is null")
            val denominator = List(state.comboOperand(operand).toInt()) { 2 }.product()
            state.registers['C'] = numerator / denominator
            return state to idx + 2
        }
    }

    companion object {
        fun fromCode(code: Int, operand: Int): ThreeBitInstruction {
            return when (code) {
                0 -> Adv(operand)
                1 -> Bxl(operand)
                2 -> Bst(operand)
                3 -> Jnz(operand)
                4 -> Bxc
                5 -> Out(operand)
                6 -> Bdv(operand)
                7 -> Cdv(operand)
                else -> throw IllegalArgumentException("Invalid instruction code: $code")
            }
        }
    }
}

object Day17 {
    private fun parse(input: List<String>): Pair<ThreeBitMachineState, List<ThreeBitInstruction>> {
        val (registers, program) = input.split { it.isBlank() }
        val registerValues = registers.map {
            it.substringAfterLast(" ").toLong()
        }
        val state = ThreeBitMachineState(
            mutableMapOf(
                'A' to registerValues[0],
                'B' to registerValues[1],
                'C' to registerValues[2]
            )
        )
        val instructions = program.first().substringAfter(" ")
            .split(",")
            .map { it.toInt() }
            .windowed(2, step = 1, partialWindows = true) {
                if (it.size == 2) {
                    ThreeBitInstruction.fromCode(it[0], it[1])
                } else {
                    ThreeBitInstruction.fromCode(it[0], -1)
                }
            }
        return state to instructions
    }

    fun part1(input: List<String>): String {
        val (state, program) = parse(input)
        val machine = ThreeBitMachine(program, state)
        val end = machine.run()
        return end.output.joinToString(",")
    }

    fun part2(input: List<String>): Long {
        val (_, program) = parse(input)
        val programCode = input.last().substringAfter(" ").split(",").map { it.toInt() }
        //var aValue = List(16 * 3 - 1) {2L}.product() + 1
        var aValue = 1L
        while (true) {
            val machine = ThreeBitMachine(program,
                ThreeBitMachineState(
                    mutableMapOf(
                        'A' to aValue,
                        'B' to 0,
                        'C' to 0
                    )
                )
            )
            val end = machine.run()
            if (end.output == programCode) {
                return aValue
            }
            println(aValue.toString(2).chunked(3).joinToString("|") + "   : $aValue")
            println(end.output.asBits())
            aValue *= 2
            //if (aValue % 10_000 == 0) {
            //println("aValue: $aValue")
                //println(programCode)
            //}
            if (aValue > 1_000_000_000_000_000) {
                println(programCode.asBits())
                return -1
            }
        }
    }

    private fun List<Int>.asBits(): String {
        return this.joinToString("|") {
            it.toString(2).padStart(3, '0')
        }
    }
}

fun main() {
    val testInput = """
        Register A: 729
        Register B: 0
        Register C: 0

        Program: 0,1,5,4,3,0
    """.trimIndent().split("\n")

    val testPart2 = """
        Register A: 2024
        Register B: 0
        Register C: 0

        Program: 0,3,5,4,3,0
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day17.part1(testInput))
    //println(Day17.part2(testPart2))

    println("------Real------")
    val input = readInput(2024, 17)
    println("Part 1 result: ${Day17.part1(input)}")
    println("Part 2 result: ${Day17.part2(input)}")
    timingStatistics { Day17.part1(input) }
    //timingStatistics { Day17.part2(input) }
}