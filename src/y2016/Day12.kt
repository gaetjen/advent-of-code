package y2016

import util.readInput

data class MachineState(
    val instructions: List<Instruction>,
    var instructionIdx: Int = 0,
    val registers: MutableMap<Char, Int> = mutableMapOf('a' to 0, 'b' to 0, 'c' to 0, 'd' to 0)
) {
    fun step() {
        instructions[instructionIdx].executeOn(this)
    }
}

sealed class Instruction {
    companion object {
        fun parse(string: String): Instruction {
            val els = string.split(" ")
            return when (els[0]) {
                "cpy" -> Copy(
                    fromRegister = if (els[1][0] in "abcd") els[1][0] else null,
                    value = els[1].toIntOrNull(),
                    targetRegister = els[2][0]
                )
                "inc" -> Inc(els[1][0])
                "dec" -> Dec(els[1][0])
                "jnz" -> Jnz(els[1][0], els[2].toInt())
                else -> error("invalid instruction")
            }
        }
    }

    open fun executeOn(state: MachineState) {
        state.instructionIdx += 1
    }
}

data class Copy(
    val fromRegister: Char?,
    val value: Int?,
    val targetRegister: Char
) : Instruction() {
    override fun executeOn(state: MachineState) {
        super.executeOn(state)
        state.registers[targetRegister] = fromRegister?.let { state.registers[it] } ?: value!!
    }
}

data class Inc(
    val register: Char
) : Instruction() {
    override fun executeOn(state: MachineState) {
        super.executeOn(state)
        state.registers[register] = (state.registers[register] ?: 0) + 1
    }
}

data class Dec(
    val register: Char
) : Instruction() {
    override fun executeOn(state: MachineState) {
        super.executeOn(state)
        state.registers[register] = (state.registers[register] ?: 0) - 1
    }
}

data class Jnz(
    val register: Char,
    val offset: Int
): Instruction() {
    override fun executeOn(state: MachineState) {
        if (state.registers[register] != 0) {
            state.instructionIdx += offset
        } else {
            super.executeOn(state)
        }
    }
}

object Day12 {
    private fun parse(input: List<String>): MachineState {
        val instructions = input.map {
            Instruction.parse(it)
        }
        return MachineState(instructions)
    }

    fun part1(input: List<String>): Int? {
        val state = parse(input)
        while (state.instructionIdx < state.instructions.size) {
            state.step()
        }
        println(state.registers)
        return state.registers['a']
    }

    fun part2(input: List<String>): Int? {
        val state = parse(input)
        state.registers['c'] = 1
        while (state.instructionIdx < state.instructions.size) {
            state.step()
        }
        println(state.registers)
        return state.registers['a']
    }
}

fun main() {
    val testInput = """
        cpy 41 a
        inc a
        inc a
        dec a
        jnz a 2
        dec a
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day12.part1(testInput))
    println(Day12.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2016/day12")
    println(Day12.part1(input))
    println(Day12.part2(input))
}