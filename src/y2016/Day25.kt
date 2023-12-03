package y2016

import util.measuredTime
import util.readInput

object Day25 {
    fun part1(input: List<String>): Int {
        val parsed = Day23.parse(input)
        var result = 0
        var maxValid = 0
        val re = "(01)*|(01)*0".toRegex()
        while (maxValid < 100) {
            result++
            val machine = CrackingMachine(
                instructions = parsed,
                CrackingState(
                    instructions = parsed,
                    registers = mutableMapOf('a' to result, 'b' to 0, 'c' to 0, 'd' to 0)
                )
            )
            while (true) {
                machine.step()
                if (!re.matches(machine.state.output)) {
                    println("Invalid output for $result: ${machine.state.output}")
                    break
                } else {
                    if (machine.state.output.length > maxValid) {
                        maxValid = machine.state.output.length
                        println("new maxValid for $result: $maxValid")
                    }
                    if (maxValid > 99) {
                        break
                    }
                }
            }
        }
        return result
    }
}

fun main() {
    println("------Real------")
    val input = readInput("resources/2016/day25")
    measuredTime { Day25.part1(input) }
}
