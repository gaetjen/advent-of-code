package util

abstract class AbstractMachine<S> {
    abstract val instructions: List<AbstractInstruction<S>>
    private var instructionIdx: Int = 0
    abstract var state: S

    fun run(log: Boolean = false): S {
        while (instructionIdx < instructions.size) {
            step()
            if (log) {
                println(state)
            }
        }
        return state
    }

    fun step() {
        val stepResult = instructions[instructionIdx].executeOn(state, instructionIdx)
        state = stepResult.first
        instructionIdx = stepResult.second
    }
}

abstract class AbstractInstruction<S> {
    abstract fun executeOn(state: S, idx: Int): Pair<S, Int>
}
