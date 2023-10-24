package y2022

import util.readInput

object Day10 {
    sealed class Command {
        abstract fun process(register: Int): List<Int>

        object NoOp : Command() {
            override fun process(register: Int): List<Int> {
                return listOf(register)
            }
        }

        class Add(private val amount: Int) : Command() {
            override fun process(register: Int): List<Int> {
                return listOf(register, register + amount)
            }
        }
    }

    private fun parse(input: String): Command {
        return when (input) {
            "noop" -> Command.NoOp
            else -> Command.Add(input.split(" ").last().toInt())
        }
    }

    fun part1(input: List<String>): Int {
        val states = inputToStates(input)
        val sigIdxs = 20..220 step 40
        return sigIdxs.sumOf {
            states[it - 1] * it
        }
    }

    fun part2(input: List<String>) {
        val states = inputToStates(input)
        val spritePos = states.map { it - 1..it + 1 }
        val cursorPos = List(6) { List(40) { it } }
        val screen = cursorPos.flatten().mapIndexed { cycle, pos ->
            if (pos in spritePos[cycle]) {
                "██"
            } else {
                "  "
            }
        }.chunked(40).joinToString("\n") { it.joinToString("") }
        println(screen)
    }

    private fun inputToStates(input: List<String>): MutableList<Int> {
        val commands = input.map { parse(it) }
        val states = mutableListOf(1)
        commands.forEach {
            states.addAll(it.process(states.last()))
        }
        return states
    }
}

fun main() {
    val testInput = """
        addx 15
        addx -11
        addx 6
        addx -3
        addx 5
        addx -1
        addx -8
        addx 13
        addx 4
        noop
        addx -1
        addx 5
        addx -1
        addx 5
        addx -1
        addx 5
        addx -1
        addx 5
        addx -1
        addx -35
        addx 1
        addx 24
        addx -19
        addx 1
        addx 16
        addx -11
        noop
        noop
        addx 21
        addx -15
        noop
        noop
        addx -3
        addx 9
        addx 1
        addx -3
        addx 8
        addx 1
        addx 5
        noop
        noop
        noop
        noop
        noop
        addx -36
        noop
        addx 1
        addx 7
        noop
        noop
        noop
        addx 2
        addx 6
        noop
        noop
        noop
        noop
        noop
        addx 1
        noop
        noop
        addx 7
        addx 1
        noop
        addx -13
        addx 13
        addx 7
        noop
        addx 1
        addx -33
        noop
        noop
        noop
        addx 2
        noop
        noop
        noop
        addx 8
        noop
        addx -1
        addx 2
        addx 1
        noop
        addx 17
        addx -9
        addx 1
        addx 1
        addx -3
        addx 11
        noop
        noop
        addx 1
        noop
        addx 1
        noop
        noop
        addx -13
        addx -19
        addx 1
        addx 3
        addx 26
        addx -30
        addx 12
        addx -1
        addx 3
        addx 1
        noop
        noop
        noop
        addx -9
        addx 18
        addx 1
        addx 2
        noop
        noop
        addx 9
        noop
        noop
        noop
        addx -1
        addx 2
        addx -37
        addx 1
        addx 3
        noop
        addx 15
        addx -21
        addx 22
        addx -6
        addx 1
        noop
        addx 2
        addx 1
        noop
        addx -10
        noop
        noop
        addx 20
        addx 1
        addx 2
        addx 2
        addx -6
        addx -11
        noop
        noop
        noop
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day10.part1(testInput))
    println(Day10.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2022/day10")
    println(Day10.part1(input))
    println(Day10.part2(input))
}
