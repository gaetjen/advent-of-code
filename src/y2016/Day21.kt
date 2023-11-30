package y2016

import util.AbstractInstruction
import util.AbstractMachine
import util.readInput

class ScrambleMachine(
    override val instructions: List<ScrambleInstruction>,
    override var state: String
) : AbstractMachine<String>()

sealed class ScrambleInstruction : AbstractInstruction<String>() {
    companion object {
        fun parse(string: String): ScrambleInstruction {
            val els = string.split(" ")
            return when (els[0]) {
                "swap" -> {
                    if (els[1] == "position") {
                        SwapPosition(els[2].toInt(), els.last().toInt())
                    } else {
                        SwapLetter(els[2][0], els.last()[0])
                    }
                }

                "rotate" -> {
                    when (els[1]) {
                        "left" -> RotateLeft(els[2].toInt())
                        "right" -> RotateRight(els[2].toInt())
                        else -> RotateByLetter(els.last()[0])
                    }
                }

                "reverse" -> {
                    Reverse(els[2].toInt(), els.last().toInt())
                }

                "move" -> {
                    Move(els[2].toInt(), els.last().toInt())
                }

                else -> error("unkonwn instruction")
            }
        }
    }
    data class SwapPosition(
        val p1: Int,
        val p2: Int
    ) : ScrambleInstruction() {
        override fun executeOn(state: String, idx: Int): Pair<String, Int> {
            val tmp = state[p1]
            val new = state.replaceRange(p1..p1, state[p2].toString())
            return new.replaceRange(p2..p2, tmp.toString()) to idx + 1
        }
    }

    data class SwapLetter(
        val l1: Char,
        val l2: Char
    ) : ScrambleInstruction() {
        override fun executeOn(state: String, idx: Int): Pair<String, Int> {
            return state.replace(l1, '%').replace(l2, l1).replace('%', l2) to idx + 1
        }
    }

    data class RotateLeft(
        val n: Int
    ) : ScrambleInstruction() {
        override fun executeOn(state: String, idx: Int): Pair<String, Int> {
            return state.drop(n) + state.take(n) to idx + 1
        }
    }

    data class RotateRight(
        val n: Int
    ) : ScrambleInstruction() {
        override fun executeOn(state: String, idx: Int): Pair<String, Int> {
            return state.drop(state.length - n) + state.take(state.length - n) to idx + 1
        }
    }

    data class RotateByLetter(
        val c: Char
    ) : ScrambleInstruction() {
        override fun executeOn(state: String, idx: Int): Pair<String, Int> {
            val pos = state.indexOf(c)
            val rotate = RotateRight(
                (pos + 1 + if (pos >= 4) 1 else 0) % state.length
            )
            return rotate.executeOn(state, idx)
        }
    }

    data class Reverse(
        val start: Int,
        val stop: Int
    ) : ScrambleInstruction() {
        override fun executeOn(state: String, idx: Int): Pair<String, Int> {
            return state.replaceRange(start..stop, state.substring(start..stop).reversed()) to idx + 1
        }
    }

    data class Move(
        val p1: Int,
        val p2: Int
    ) : ScrambleInstruction() {
        override fun executeOn(state: String, idx: Int): Pair<String, Int> {
            val c = state[p1]
            val list = state.toMutableList()
            list.removeAt(p1)
            list.add(p2, c)
            return list.joinToString(separator = "") to idx + 1
        }
    }
}

class UnScrambleMachine(
    override val instructions: List<UnScrambleInstruction>,
    override var state: String
) : AbstractMachine<String>()

sealed class UnScrambleInstruction : AbstractInstruction<String>() {
    companion object {
        fun parse(string: String): UnScrambleInstruction {
            val els = string.split(" ")
            return when (els[0]) {
                "swap" -> {
                    if (els[1] == "position") {
                        SwapPosition(els[2].toInt(), els.last().toInt())
                    } else {
                        SwapLetter(els[2][0], els.last()[0])
                    }
                }

                "rotate" -> {
                    when (els[1]) {
                        "left" -> RotateLeft(els[2].toInt())
                        "right" -> RotateRight(els[2].toInt())
                        else -> RotateByLetter(els.last()[0])
                    }
                }

                "reverse" -> {
                    Reverse(els[2].toInt(), els.last().toInt())
                }

                "move" -> {
                    Move(els[2].toInt(), els.last().toInt())
                }

                else -> error("unkonwn instruction")
            }
        }
    }
    data class SwapPosition(
        val p1: Int,
        val p2: Int
    ) : UnScrambleInstruction() {
        override fun executeOn(state: String, idx: Int): Pair<String, Int> {
            val tmp = state[p1]
            val new = state.replaceRange(p1..p1, state[p2].toString())
            return new.replaceRange(p2..p2, tmp.toString()) to idx + 1
        }
    }

    data class SwapLetter(
        val l1: Char,
        val l2: Char
    ) : UnScrambleInstruction() {
        override fun executeOn(state: String, idx: Int): Pair<String, Int> {
            return state.replace(l1, '%').replace(l2, l1).replace('%', l2) to idx + 1
        }
    }

    data class RotateLeft(
        val n: Int
    ) : UnScrambleInstruction() {
        override fun executeOn(state: String, idx: Int): Pair<String, Int> {
            return state.drop(state.length - n) + state.take(state.length - n) to idx + 1
        }
    }

    data class RotateRight(
        val n: Int
    ) : UnScrambleInstruction() {
        override fun executeOn(state: String, idx: Int): Pair<String, Int> {
            return state.drop(n) + state.take(n) to idx + 1
        }
    }

    /**
     * 0 -1-> 1
     * 1 -2-> 3
     * 2 -3-> 5
     * 3 -4-> 7
     * 4 -6-> 2
     * 5 -7-> 4
     * 6 -0-> 6
     * 7 -1-> 0
     */
    val reverseMap = mapOf(
        1 to 1,
        3 to 2,
        5 to 3,
        7 to 4,
        2 to 6,
        4 to 7,
        6 to 0,
        0 to 1
    )
    data class RotateByLetter(
        val c: Char
    ) : UnScrambleInstruction() {
        override fun executeOn(state: String, idx: Int): Pair<String, Int> {
            val pos = state.indexOf(c)
            val rotate = RotateRight(
                reverseMap[pos] ?: error("bad")
            )
            return rotate.executeOn(state, idx)
        }
    }

    data class Reverse(
        val start: Int,
        val stop: Int
    ) : UnScrambleInstruction() {
        override fun executeOn(state: String, idx: Int): Pair<String, Int> {
            return state.replaceRange(start..stop, state.substring(start..stop).reversed()) to idx + 1
        }
    }

    data class Move(
        val p1: Int,
        val p2: Int
    ) : UnScrambleInstruction() {
        override fun executeOn(state: String, idx: Int): Pair<String, Int> {
            val c = state[p2]
            val list = state.toMutableList()
            list.removeAt(p2)
            list.add(p1, c)
            return list.joinToString(separator = "") to idx + 1
        }
    }
}

object Day21 {
    private fun parse(input: List<String>): List<ScrambleInstruction> {
        return input.map {
            ScrambleInstruction.parse(it)
        }
    }

    fun part1(input: List<String>, start: String): String {
        val parsed = parse(input)
        val machine = ScrambleMachine(parsed, start)
        return machine.run()
    }


    private fun parseUnscramble(input: List<String>): List<UnScrambleInstruction> {
        return input.map {
            UnScrambleInstruction.parse(it)
        }.reversed()
    }

    fun part2(input: List<String>, end: String): String {
        val parsed = parseUnscramble(input)
        val machine = UnScrambleMachine(parsed, end)
        return machine.run()
    }
}

fun main() {
    val testInput = """
        swap position 4 with position 0
        swap letter d with letter b
        reverse positions 0 through 4
        rotate left 1 step
        move position 1 to position 4
        move position 3 to position 0
        rotate based on position of letter b
        rotate based on position of letter d
    """.trimIndent().split("\n")
    val testStart = "abcde"
    println("------Tests------")
    println(Day21.part1(testInput, testStart))
    //println(Day21.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2016/day21")
    val start = "abcdefgh"
    println(Day21.part1(input, start))
    val end = "fbgdceah"
    println(Day21.part2(input, end))
}
