package y2017

import util.readInput
import util.split
import util.timingStatistics

data class TuringMachine(
    var currentState: TuringMachineState,
    val states: Map<Char, TuringMachineState>,
    val tape: MutableSet<Int>,
    var cursorPosition: Int
) {
    fun checkSum() = tape.size

    fun step() {
        if(cursorPosition in tape) {
            if (currentState.oneInstruction.writeOne) tape.add(cursorPosition) else tape.remove(cursorPosition)
            cursorPosition += currentState.oneInstruction.cursorMovement
            currentState = states[currentState.oneInstruction.nextState]!!
        } else {
            if (currentState.zeroInstruction.writeOne) tape.add(cursorPosition) else tape.remove(cursorPosition)
            cursorPosition += currentState.zeroInstruction.cursorMovement
            currentState = states[currentState.zeroInstruction.nextState]!!
        }
    }
}

data class TuringMachineState(
    val name: Char,
    val zeroInstruction: TuringMachineInstruction,
    val oneInstruction: TuringMachineInstruction
)

data class TuringMachineInstruction(
    val writeOne: Boolean,
    val cursorMovement: Int,
    val nextState: Char
)

object Day25 {
    private fun parse(input: List<String>): Pair<TuringMachine, Int> {
        val bla = input.split { it.isEmpty() }
        val init = bla.first()

        val startState = init.first().dropLast(1).last()
        val steps = init.last().split(" ").dropLast(1).last().toInt()

        val stateInputs = bla.drop(1)
        val states = stateInputs.associate { stateInput ->
            val name = stateInput.first().dropLast(1).last()
            name to TuringMachineState(
                name = name,
                zeroInstruction = TuringMachineInstruction(
                    writeOne = stateInput[2].dropLast(1).last() == '1',
                    cursorMovement = if (stateInput[3].split(" ").last() == "right.") 1 else -1,
                    nextState = stateInput[4].dropLast(1).last()
                ),
                oneInstruction = TuringMachineInstruction(
                    writeOne = stateInput[6].dropLast(1).last() == '1',
                    cursorMovement = if (stateInput[7].split(" ").last() == "right.") 1 else -1,
                    nextState = stateInput[8].dropLast(1).last()
                )
            )
        }

        return TuringMachine(
            currentState = states[startState]!!,
            states = states,
            tape = mutableSetOf(),
            cursorPosition = 0
        ) to steps
    }

    fun part1(input: List<String>): Int {
        val (machine, maxSteps) = parse(input)
        repeat(maxSteps) {
            machine.step()
        }
        return machine.checkSum()
    }
}

fun main() {
    val testInput = """
        Begin in state A.
Perform a diagnostic checksum after 6 steps.

In state A:
  If the current value is 0:
    - Write the value 1.
    - Move one slot to the right.
    - Continue with state B.
  If the current value is 1:
    - Write the value 0.
    - Move one slot to the left.
    - Continue with state B.

In state B:
  If the current value is 0:
    - Write the value 1.
    - Move one slot to the left.
    - Continue with state A.
  If the current value is 1:
    - Write the value 1.
    - Move one slot to the right.
    - Continue with state A.
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day25.part1(testInput))

    println("------Real------")
    val input = readInput(2017, 25)
    println("Part 1 result: ${Day25.part1(input)}")
    timingStatistics { Day25.part1(input) }
}