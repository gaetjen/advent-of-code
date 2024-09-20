package y2017

import util.AbstractInstruction
import util.AbstractMachine
import util.readInput
import util.timingStatistics

class DancingMachine(
    override val instructions: List<AbstractInstruction<List<Char>>>,
    override var state: List<Char>
) : AbstractMachine<List<Char>>()

sealed class DancingInstruction : AbstractInstruction<List<Char>>() {
    class Spin(private val n: Int) : DancingInstruction() {
        override fun executeOn(
            state: List<Char>,
            idx: Int
        ): Pair<List<Char>, Int> {
            return state.takeLast(n) + state.dropLast(n) to idx + 1
        }
    }

    class Exchange(
        private val pos1: Int,
        private val pos2: Int
    ) : DancingInstruction() {
        override fun executeOn(
            state: List<Char>,
            idx: Int
        ): Pair<List<Char>, Int> {
            val c1 = state[pos1]
            val newState = state.toMutableList()
            newState[pos1] = newState[pos2]
            newState[pos2] = c1
            return newState to idx + 1
        }
    }

    class Partner(
        private val c1: Char,
        private val c2: Char
    ) : DancingInstruction() {
        override fun executeOn(
            state: List<Char>,
            idx: Int
        ): Pair<List<Char>, Int> {
            val newState = state.toMutableList()
            val pos1 = state.indexOf(c1)
            val pos2 = state.indexOf(c2)
            newState[pos2] = c1
            newState[pos1] = c2
            return newState to idx + 1
        }
    }

    companion object {
        fun fromString(str: String): DancingInstruction {
            return when(str.first()) {
                's' -> Spin(str.drop(1).toInt())
                'x' -> {
                    val (p1, p2) = str.drop(1).split("/").map { it.toInt() }
                    Exchange(p1, p2)
                }
                'p' -> {
                    val (c1, c2) = str.drop(1).split("/")
                    Partner(c1.first(), c2.first())
                }
                else -> throw IllegalArgumentException("unkonwn instruction: $str")
            }
        }
    }
}

object Day16 {
    private const val START = "abcdefghijklmnop"
    private fun parse(input: List<String>): List<DancingInstruction> {
        return input.first().split(",").map {
            DancingInstruction.fromString(it)
        }
    }

    fun part1(input: List<String>): String {
        val machine = DancingMachine(
            instructions = parse(input),
            state = START.toList()
        )
        return machine.run().joinToString("")
    }

    fun part2(input: List<String>): String {
        val instructions = parse(input)
        val states = mutableListOf(START)
        do {
            val newState = DancingMachine(
                instructions = instructions,
                state = states.last().toList()
            ).run().joinToString("")
            states.add(newState)
        } while (states.last() != states.first())
        return states[1_000_000_000 % (states.size - 1)]
    }
}

fun main() {
    val testInput = """
        s3
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day16.part1(testInput))
    println(Day16.part2(testInput))

    println("------Real------")
    val input = readInput(2017, 16)
    println("Part 1 result: ${Day16.part1(input)}")
    println("Part 2 result: ${Day16.part2(input)}")
    timingStatistics { Day16.part1(input) }
    timingStatistics { Day16.part2(input) }
}