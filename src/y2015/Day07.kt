package y2015

import util.readInput

data class Signal(
    val instruction: String,
    var result: UShort? = null
) {
    fun update(result: UShort): UShort {
        this.result = result
        return result
    }
}

fun lShift(x: UShort, y: Int) : UShort {
    return x.rotateLeft(y) and (UShort.Companion.MAX_VALUE - ones(y)).toUShort()
}

fun rShift(x: UShort, y: Int): UShort {
    return x.rotateRight(y) and ones((16 - y))
}

fun ones(x: Int): UShort {
    var result = 2
    repeat(x - 1) {
        result *= 2
    }
    return (result - 1).toUShort()
}

object Day07 {
    private fun parse(input: List<String>): Map<String, Signal> {
        return input.map {
            it.split(" -> ")
        }.associate {
            it.last() to Signal(it.first())
        }
    }

    fun part1(input: List<String>): UShort {
        val signals = parse(input)
        signals.forEach {
            println("${it.key}: ${evaluate(signals, it.key)}")
        }
        return evaluate(signals, signals.keys.minOf { it })
    }

    private fun evaluate(signals: Map<String, Signal>, id: String): UShort {
        if (id == "1") {
            return 1.toUShort()
        }
        if (signals[id] == null) {
            println("no thing: $id")
        }
        val signal = signals[id]!!
        if (signal.result != null) {
            return  signal.result!!
        }
        val instructions = signal.instruction.split(' ')
        if (instructions.size == 1) {
            return signal.update(instructions.first().toUShort())
        }
        if (instructions.size == 2) {
            return signal.update(evaluate(signals, instructions.last()).inv())
        }
        return when (instructions[1]) {
            "LSHIFT" -> signal.update(lShift(
                evaluate(signals, instructions.first()),
                instructions.last().toInt()
            ))
            "RSHIFT" -> signal.update(rShift(
                evaluate(signals, instructions.first()),
                instructions.last().toInt()
            ))
            "OR" -> signal.update(evaluate(signals, instructions.first()) or evaluate(signals, instructions.last()))
            else -> signal.update(evaluate(signals, instructions.first()) and evaluate(signals, instructions.last()))
        }
    }

    fun part2(input: List<String>): UShort {
        val signals = parse(input)
        signals["b"]!!.result = 46065.toUShort()
        return evaluate(signals, signals.keys.minOf { it })
    }
}

fun main() {
    val testInput = """
        543 -> a
        53 -> b
        123 -> x
        456 -> y
        x AND y -> d
        x OR y -> e
        x LSHIFT 2 -> f
        y RSHIFT 2 -> g
        NOT x -> h
        NOT y -> i
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day07.part1(testInput))
    println(Day07.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2015/day07")
    println(Day07.part1(input))
    println(Day07.part2(input))
}
