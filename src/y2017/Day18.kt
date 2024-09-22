package y2017

import util.AbstractInstruction
import util.AbstractMachine
import util.readInput
import util.timingStatistics

data class DuetMachineState(
    val registers: MutableMap<Char, Long> = mutableMapOf(),
    val lastSound: Long = 0
)

class DuetMachine(
    override val instructions: List<DuetInstruction>,
    override var state: DuetMachineState,
) : AbstractMachine<DuetMachineState>()

/**
 * snd X plays a sound with a frequency equal to the value of X.
 * set X Y sets register X to the value of Y.
 * add X Y increases register X by the value of Y.
 * mul X Y sets register X to the result of multiplying the value contained in register X by the value of Y.
 * mod X Y sets register X to the remainder of dividing the value contained in register X by the value of Y (that is, it sets X to the result of X modulo Y).
 * rcv X recovers the frequency of the last sound played, but only when the value of X is not zero. (If it is zero, the command does nothing.)
 * jgz X Y jumps with an offset of the value of Y, but only if the value of X is greater than zero. (An offset of 2 skips the next instruction, an offset of -1 jumps to the previous instruction, and so on.)
 */

sealed class DuetInstruction : AbstractInstruction<DuetMachineState>() {
    companion object {
        fun parse(string: String): DuetInstruction {
            val els = string.split(" ")
            return when (els[0]) {
                "snd" -> Sound(Operand.fromString(els[1]))
                "set" -> Set(Operand.Register(els[1].first()), Operand.fromString(els[2]))
                "add" -> Add(Operand.Register(els[1].first()), Operand.fromString(els[2]))
                "mul" -> Mul(Operand.Register(els[1].first()), Operand.fromString(els[2]))
                "mod" -> Mod(Operand.Register(els[1].first()), Operand.fromString(els[2]))
                "rcv" -> Recover(Operand.fromString(els[1]))
                "jgz" -> Jgz(Operand.fromString(els[1]), Operand.fromString(els[2]))
                else -> error("invalid instruction")
            }
        }
    }

    sealed class Operand {
        companion object {
            fun fromString(str: String): Operand {
                return str.toLongOrNull()?.let {
                    Number(it)
                } ?: Register(str.first())
            }
        }

        abstract operator fun get(state: DuetMachineState): Long
        data class Register(val name: Char) : Operand() {
            override operator fun get(state: DuetMachineState): Long {
                return state.registers[name] ?: 0
            }
        }

        data class Number(val value: Long) : Operand() {
            override operator fun get(state: DuetMachineState): Long {
                return value
            }
        }
    }

    data class Sound(val op: Operand) : DuetInstruction() {
        override fun executeOn(
            state: DuetMachineState,
            idx: Int
        ): Pair<DuetMachineState, Int> {
            return state.copy(
                lastSound = op[state]
            ) to idx + 1
        }
    }

    data class Recover(val op: Operand) : DuetInstruction() {
        override fun executeOn(
            state: DuetMachineState,
            idx: Int
        ): Pair<DuetMachineState, Int> {
            return if (op[state] == 0L) {
                state to idx + 1
            } else {
                state to -1
            }
        }
    }

    data class Set(
        val target: Operand.Register,
        val op: Operand
    ) : DuetInstruction() {
        override fun executeOn(
            state: DuetMachineState,
            idx: Int
        ): Pair<DuetMachineState, Int> {
            state.registers[target.name] = op[state]
            return state to idx + 1
        }
    }

    data class Add(
        val target: Operand.Register,
        val op: Operand
    ) : DuetInstruction() {
        override fun executeOn(
            state: DuetMachineState,
            idx: Int
        ): Pair<DuetMachineState, Int> {
            state.registers[target.name] = op[state] + (state.registers[target.name] ?: 0)
            return state to idx + 1
        }
    }

    data class Mul(
        val target: Operand.Register,
        val op: Operand
    ) : DuetInstruction() {
        override fun executeOn(
            state: DuetMachineState,
            idx: Int
        ): Pair<DuetMachineState, Int> {
            state.registers[target.name] = op[state] * (state.registers[target.name] ?: 0)
            return state to idx + 1
        }
    }

    data class Mod(
        val target: Operand.Register,
        val op: Operand
    ) : DuetInstruction() {
        override fun executeOn(
            state: DuetMachineState,
            idx: Int
        ): Pair<DuetMachineState, Int> {
            state.registers[target.name] = (state.registers[target.name] ?: 0).mod(op[state])
            return state to idx + 1
        }
    }

    data class Jgz(
        val cmp: Operand,
        val offset: Operand
    ) : DuetInstruction() {
        override fun executeOn(
            state: DuetMachineState,
            idx: Int
        ): Pair<DuetMachineState, Int> {
            return if (cmp[state] > 0) {
                state to idx + offset[state].toInt()
            } else {
                state to idx + 1
            }
        }
    }
}

object Day18 {
    private fun parse(input: List<String>): List<DuetInstruction> {
        return input.map {
            DuetInstruction.parse(it)
        }
    }

    fun part1(input: List<String>): Long {
        val parsed = parse(input)
        val machine = DuetMachine(parsed, DuetMachineState())
        return machine.run().lastSound
    }

    fun part2(input: List<String>): Long {
        val parsed = parse(input)
        return 0L
    }
}

fun main() {
    val testInput = """
        set a 1
        add a 2
        mul a a
        mod a 5
        snd a
        set a 0
        rcv a
        jgz a -1
        set a 1
        jgz a -2
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day18.part1(testInput))
    val testInput2 = """
        snd 1
        snd 2
        snd p
        rcv a
        rcv b
        rcv c
        rcv d
    """.trimIndent()
    println(Day18.part2(testInput))

    println("------Real------")
    val input = readInput(2017, 18)
    println("Part 1 result: ${Day18.part1(input)}")
    println("Part 2 result: ${Day18.part2(input)}")
    //timingStatistics { Day18.part1(input) }
    timingStatistics { Day18.part2(input) }
}