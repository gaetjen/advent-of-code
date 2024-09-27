package y2017

import util.readInput
import util.timingStatistics

abstract class SuspendingMachine<S> {
    abstract val instructions: List<SuspendingInstruction<S>>
    private var instructionIdx: Int = 0
    abstract var state: S

    suspend fun SequenceScope<Long>.run(log: Boolean = false): S {
        while (instructionIdx in instructions.indices) {
            step()
            if (log) {
                println(state)
            }
        }
        return state
    }

    suspend fun SequenceScope<Long>.step() {
        val stepResult = with(instructions[instructionIdx]) {
            executeOn(state, instructionIdx)
        }
        state = stepResult.first
        instructionIdx = stepResult.second
    }
}

abstract class SuspendingInstruction<S> {
    abstract suspend fun SequenceScope<Long>.executeOn(
        state: S,
        idx: Int
    ): Pair<S, Int>
}

data class DuetMachineState(
    val registers: MutableMap<Char, Long> = mutableMapOf(),
    val lastSound: Long = 0,
    var partner: Iterator<Long>? = null,
    var sends: MutableList<Long>? = null,
    var receives: MutableList<Long>? = null
)

class DuetMachine(
    override val instructions: List<DuetInstruction>,
    override var state: DuetMachineState,
) : SuspendingMachine<DuetMachineState>()

/**
 * snd X plays a sound with a frequency equal to the value of X.
 * set X Y sets register X to the value of Y.
 * add X Y increases register X by the value of Y.
 * mul X Y sets register X to the result of multiplying the value contained in register X by the value of Y.
 * mod X Y sets register X to the remainder of dividing the value contained in register X by the value of Y (that is, it sets X to the result of X modulo Y).
 * rcv X recovers the frequency of the last sound played, but only when the value of X is not zero. (If it is zero, the command does nothing.)
 * jgz X Y jumps with an offset of the value of Y, but only if the value of X is greater than zero. (An offset of 2 skips the next instruction, an offset of -1 jumps to the previous instruction, and so on.)
 */

sealed class DuetInstruction : SuspendingInstruction<DuetMachineState>() {
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

        fun parsePart2(string: String): DuetInstruction {
            return when {
                string.startsWith("snd") -> Send(Operand.fromString(string.substringAfter(" ")))
                string.startsWith("rcv") -> Receive(Operand.Register(string.substringAfter(" ").first()))
                else -> parse(string)
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
        override suspend fun SequenceScope<Long>.executeOn(
            state: DuetMachineState,
            idx: Int
        ): Pair<DuetMachineState, Int> {
            return state.copy(
                lastSound = op[state]
            ) to idx + 1
        }
    }

    data class Recover(val op: Operand) : DuetInstruction() {
        override suspend fun SequenceScope<Long>.executeOn(
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

    data class Send(val op: Operand) : DuetInstruction() {
        override suspend fun SequenceScope<Long>.executeOn(
            state: DuetMachineState,
            idx: Int
        ): Pair<DuetMachineState, Int> {
            return if (state.sends != null) {
                state.sends!!.add(op[state])
                state to idx + 1
            } else {
                yield(op[state])
                // why create a new attribute when we have a perfectly good one lying around unused?
                state.copy(lastSound = state.lastSound + 1) to idx + 1
            }
        }

    }

    data class Receive(val op: Operand.Register) : DuetInstruction() {
        override suspend fun SequenceScope<Long>.executeOn(
            state: DuetMachineState,
            idx: Int
        ): Pair<DuetMachineState, Int> {
            return if (state.partner != null) {
                if (state.partner!!.hasNext()) {
                    state.registers[op.name] = state.partner!!.next()
                    state to idx + 1
                } else {
                    state to -1
                }
            } else {
                if (state.receives?.isNotEmpty() == true) {
                    state.registers[op.name] = state.receives!!.removeFirst()
                    state to idx + 1
                } else {
                    state to -1
                }
            }
        }
    }

    data class Set(
        val target: Operand.Register,
        val op: Operand
    ) : DuetInstruction() {
        override suspend fun SequenceScope<Long>.executeOn(
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
        override suspend fun SequenceScope<Long>.executeOn(
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
        override suspend fun SequenceScope<Long>.executeOn(
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
        override suspend fun SequenceScope<Long>.executeOn(
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
        override suspend fun SequenceScope<Long>.executeOn(
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

    private fun parse2(input: List<String>): List<DuetInstruction> {
        return input.map {
            DuetInstruction.parsePart2(it)
        }
    }

    fun part1(input: List<String>): Long {
        val parsed = parse(input)
        val machine = DuetMachine(parsed, DuetMachineState())
        return sequence {
            with(machine) {
                yield(run().lastSound)
            }
        }.last()
    }

    fun part2(input: List<String>): Long {
        val parsed = parse2(input)
        val sendList = mutableListOf<Long>()
        val machineState0 = DuetMachineState(
            registers = mutableMapOf('p' to 0),
            sends = sendList
        )
        val machineState1 = DuetMachineState(
            registers = mutableMapOf('p' to 1),
            receives = sendList
        )
        var result = 0L
        val machine1Sequence = sequence {
            with(DuetMachine(parsed, machineState1)) {
                result = run().lastSound
            }
        }
        machineState0.partner = machine1Sequence.iterator()
        val machine = DuetMachine(parsed, machineState0)
        sequence {
            with(machine) {
                run(log = false)
            }
            yield(123L)
        }.iterator().next()
        return result
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
    """.trimIndent().split("\n")
    println(Day18.part2(testInput2))

    println("------Real------")
    val input = readInput(2017, 18)
    println("Part 1 result: ${Day18.part1(input)}")
    println("Part 2 result: ${Day18.part2(input)}")
    timingStatistics { Day18.part1(input) }
    timingStatistics { Day18.part2(input) }
}