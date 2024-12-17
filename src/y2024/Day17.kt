package y2024

import util.AbstractInstruction
import util.AbstractMachine
import util.product
import util.readInput
import util.split
import util.timingStatistics
import kotlin.math.pow

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
        fun fromCode(
            code: Int,
            operand: Int
        ): ThreeBitInstruction {
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
        val fixedInputs = MutableList(programCode.size) { 0 }
        //findBestInputs(fixedInputs, program, programCode, 3)
        val doubleInputs = fixedInputs.chunked(2).map { it.toStartValue(3).toInt() }.toMutableList()
        assert(doubleInputs.toStartValue(6) == fixedInputs.toStartValue(3))
        println(doubleInputs)
        findBestInputs(doubleInputs, program, programCode, 6)
        println(doubleInputs)
        return doubleInputs.toStartValue(6)
    }

    private fun findBestInputs(
        fixedInputs: MutableList<Int>,
        program: List<ThreeBitInstruction>,
        programCode: List<Int>,
        bitsPerInput: Int
    ) {
        val maxValue = 2.0.pow(bitsPerInput).toInt() - 1
        var matching = 0
        while (true) {
            fixedInputs.indices.forEach { idx ->
                val bestValue = (0..maxValue).maxBy { newTestValue ->
                    val inputs = fixedInputs.toMutableList()
                    inputs[idx] = newTestValue
                    val machine = ThreeBitMachine(
                        program,
                        ThreeBitMachineState(
                            mutableMapOf(
                                'A' to inputs.toStartValue(bitsPerInput),
                                'B' to 0,
                                'C' to 0
                            )
                        )
                    )
                    val endState = machine.run()
                    endState.output.asBits().zip(programCode.asBits()).count { it.first == it.second }
                }
                fixedInputs[idx] = bestValue
            }

            val machine = ThreeBitMachine(
                program,
                ThreeBitMachineState(
                    mutableMapOf(
                        'A' to fixedInputs.toStartValue(bitsPerInput),
                        'B' to 0,
                        'C' to 0
                    )
                )
            )
            val end = machine.run()
            val newMatch = end.output.asBits().zip(programCode.asBits()).count { it.first == it.second }
            if (newMatch == matching) {
                println("Matching: $matching/${programCode.asBits().length}")
                println(end.output.asBits())
                println(programCode.asBits())
                break
            } else {
                matching = newMatch
            }
        }
    }

    private fun List<Int>.asBits(): String {
        return this.joinToString("|") {
            it.toString(2).padStart(3, '0')
        }
    }

    private fun List<Int>.toStartValue(bitsPerValue: Int): Long {
        return this.mapIndexed { index, n ->
            n * 2.0.pow(index * bitsPerValue).toLong()
        }.sum()
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
    println(Day17.part2(testPart2))

    println("------Real------")
    val input = readInput(2024, 17)
    println("Part 1 result: ${Day17.part1(input)}")
    println("Part 2 result: ${Day17.part2(input)}")
    timingStatistics { Day17.part1(input) }
    //timingStatistics { Day17.part2(input) }
}