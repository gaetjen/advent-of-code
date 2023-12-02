package y2016

import util.AbstractInstruction
import util.AbstractMachine
import util.readInput

data class CrackingState(
    val instructions: MutableList<CrackingInstruction>,
    val registers: MutableMap<Char, Int> = mutableMapOf('a' to 7, 'b' to 0, 'c' to 0, 'd' to 0)
)

class CrackingMachine(
    override val instructions: List<AbstractInstruction<CrackingState>>,
    override var state: CrackingState
) : AbstractMachine<CrackingState>()

sealed class CrackingInstruction : AbstractInstruction<CrackingState>() {
    companion object {
        fun parse(string: String): CrackingInstruction {
            val els = string.split(" ")
            return when (els[0]) {
                "cpy" -> Copy(els[1], els[2])
                "inc" -> Inc(els[1])
                "dec" -> Dec(els[1])
                "jnz" -> Jnz(els[1], els[2])
                "tgl" -> Toggle(els[1])
                else -> error("invalid instruction")
            }
        }
    }

    data class Copy(
        val from: String,
        val to: String
    ) : CrackingInstruction() {
        override fun executeOn(state: CrackingState, idx: Int): Pair<CrackingState, Int> {
            if (to.toIntOrNull() == null) {
                val fromValue = from.toIntOrNull() ?: state.registers[from.first()]
                state.registers[to.first()] = fromValue!!
            }
            return state to idx + 1
        }
    }

    data class Inc(
        val register: String
    ) : CrackingInstruction() {
        override fun executeOn(state: CrackingState, idx: Int): Pair<CrackingState, Int> {
            if (register.toIntOrNull() == null) {
                state.registers[register.first()] = state.registers[register.first()]!! + 1
            }
            return state to idx + 1
        }
    }

    data class Dec(
        val register: String
    ) : CrackingInstruction() {
        override fun executeOn(state: CrackingState, idx: Int): Pair<CrackingState, Int> {
            if (register.toIntOrNull() == null) {
                state.registers[register.first()] = state.registers[register.first()]!! - 1
            }
            return state to idx + 1
        }
    }

    data class Jnz(
        val register: String,
        val offset: String
    ) : CrackingInstruction() {
        override fun executeOn(state: CrackingState, idx: Int): Pair<CrackingState, Int> {
            val compareValue = register.toIntOrNull() ?: state.registers[register.first()]
            val offsetValue = offset.toIntOrNull() ?: state.registers[offset.first()]
            return if (offsetValue != null && compareValue != 0) {
                state to idx + offsetValue
            } else {
                state to idx + 1
            }
        }
    }

    data class Toggle(
        val offset: String
    ) : CrackingInstruction() {
        override fun executeOn(state: CrackingState, idx: Int): Pair<CrackingState, Int> {
            val offsetValue = offset.toIntOrNull() ?: state.registers[offset.first()]!!
            if (idx + offsetValue in state.instructions.indices) {
                state.instructions[idx + offsetValue] = toggle(state.instructions[idx + offsetValue])
            }
            return state to idx + 1
        }

        private fun toggle(instruction: CrackingInstruction): CrackingInstruction {
            return when (instruction) {
                is Copy -> Jnz(instruction.from, instruction.to)
                is Dec -> Inc(instruction.register)
                is Inc -> Dec(instruction.register)
                is Jnz -> Copy(instruction.register, instruction.offset)
                is Toggle -> Inc(instruction.offset)
            }
        }
    }
}

object Day23 {
    private fun parse(input: List<String>): MutableList<CrackingInstruction> {
        return input.map {
            CrackingInstruction.parse(it)
        }.toMutableList()
    }

    fun part1(input: List<String>): Int? {
        val parsed = parse(input)
        val machine = CrackingMachine(parsed, CrackingState(parsed))
        return machine.run().registers['a']
    }

    fun part2(input: List<String>) {
        (6..12).forEach {
            val parsed = parse(input)
            val machine = CrackingMachine(parsed, CrackingState(
                parsed,
                mutableMapOf('a' to it, 'b' to 0, 'c' to 0, 'd' to 0)
            ))
            println("$it: " + machine.run().registers['a'])
        }
    }
}

fun main() {
    val testInput = """
        cpy 2 a
        tgl a
        tgl a
        tgl a
        cpy 1 a
        dec a
        dec a
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day23.part1(testInput))
    println(Day23.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2016/day23")
    println(Day23.part1(input))
    println(Day23.part2(input))
}