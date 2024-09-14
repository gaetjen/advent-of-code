package y2017

import util.AbstractInstruction
import util.AbstractMachine
import util.readInput
import util.timingStatistics

class Registers(
    val map: MutableMap<String, Int>,
    var max: Int
)

class RegisterMachine(
    override val instructions: List<AbstractInstruction<Registers>>,
    override var state: Registers = Registers(mutableMapOf<String, Int>().withDefault { 0 }, 0)
) : AbstractMachine<Registers>()

class RegisterInstruction(
    val op: (Registers) -> Int,
    val cmp: (Registers) -> Boolean
) : AbstractInstruction<Registers>() {
    companion object {
        fun parse(input: String): RegisterInstruction {
            val (opInput, cmpInput) = input.split(" if ")
            val (register, opStr, n) = opInput.split(" ")
            val op: (Int, Int) -> Int = if (opStr == "inc") Int::plus else Int::minus
            val (cmpRegister, cmpOpStr, cmpN) = cmpInput.split(" ")
            val cmpOp: (Int, Int) -> Boolean = when (cmpOpStr) {
                ">" -> { a, b -> a > b }
                ">=" -> { a, b -> a >= b }
                "<" -> { a, b -> a < b }
                "<=" -> { a, b -> a <= b }
                "==" -> { a, b -> a == b }
                "!=" -> { a, b -> a != b }
                else -> throw IllegalArgumentException("unknown comparison operator: $cmpOpStr")
            }
            return RegisterInstruction(
                { registers ->
                    val newValue = op(registers.map.getValue(register), n.toInt())
                    registers.map[register] = newValue
                    newValue
                },
                { registers -> cmpOp(registers.map.getValue(cmpRegister), cmpN.toInt()) }
            )
        }
    }

    override fun executeOn(
        state: Registers,
        idx: Int
    ): Pair<Registers, Int> {
        if (cmp(state)) {
            val newValue = op(state)
            if (newValue > state.max) state.max = newValue
        }
        return state to idx + 1
    }
}

object Day08 {
    private fun parse(input: List<String>): RegisterMachine {
        val instructions = input.map { RegisterInstruction.parse(it) }
        return RegisterMachine(instructions)
    }

    fun part1(input: List<String>): Int {
        val machine = parse(input)
        val result = machine.run()
        return result.map.values.max()
    }

    fun part2(input: List<String>): Int {
        val machine = parse(input)
        val result = machine.run()
        return result.max
    }
}

fun main() {
    val testInput = """
        b inc 5 if a > 1
        a inc 1 if b < 5
        c dec -10 if a >= 1
        c inc -20 if c == 10
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day08.part1(testInput))
    println(Day08.part2(testInput))

    println("------Real------")
    val input = readInput(2017, 8)
    println("Part 1 result: ${Day08.part1(input)}")
    println("Part 2 result: ${Day08.part2(input)}")
    timingStatistics { Day08.part1(input) }
    timingStatistics { Day08.part2(input) }
}