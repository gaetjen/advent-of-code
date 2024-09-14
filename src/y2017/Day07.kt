package y2017

import util.readInput
import util.timingStatistics

class Program(
    val name: String,
    val weight: Int,
) {
    companion object {
        fun fromInput(input: String) : Pair<Program, List<String>> {
            val parts = input.split(" -> ")
            val (name, weight) = parts[0].split(" ")
            val weightValue = weight.drop(1).dropLast(1).toInt()
            val children = if (parts.size > 1) {
                parts[1].split(", ")
            } else {
                emptyList()
            }
            return Program(name, weightValue) to children
        }
    }
    var parent: Program? = null
        set(value) {
            field = value
            value?.children?.add(this)
        }
    val root: Program
        get() = (parent?.root) ?: this
    val children = mutableSetOf<Program>()

    val totalWeight: Int by lazy {
        if (children.isEmpty()) {
            weight
        } else {
            weight + children.sumOf { it.totalWeight }
        }
    }

    override fun toString(): String {
        return "$name ($weight) -> ${parent?.name}"
    }
}

object Day07 {
    private fun parse(input: List<String>): Map<String, Program> {
        val detached = input
            .map { Program.fromInput(it) }
        val programs = detached.associate { it.first.name to it.first }
        val children = detached.associate { it.first.name to it.second }
        children.forEach { (programName, cs) ->
            cs.forEach { childName ->
                programs[childName]?.parent = programs[programName]
            }
        }
        return programs
    }

    fun part1(input: List<String>): String {
        val parsed = parse(input)
        return parsed.values.first().root.name
    }

    fun part2(input: List<String>): Int {
        val root = parse(input).values.first().root
        val imbalancedProgram = root.children.first { c ->
            (root.children - c).map { it.totalWeight }.distinct().size == 1
        }
        val weightError = (root.children - imbalancedProgram).first().totalWeight - imbalancedProgram.totalWeight
        return correctedWeight(imbalancedProgram, weightError)
    }

    private fun correctedWeight(
        imbalancedProgram: Program,
        weightError: Int
    ): Int {
        if (imbalancedProgram.children.isEmpty() || imbalancedProgram.children.map { it.totalWeight }.distinct().size == 1) {
            return imbalancedProgram.weight + weightError
        }
        val imbalancedChild = imbalancedProgram.children.first { c ->
            c.totalWeight + weightError == (imbalancedProgram.children - c).first().totalWeight
        }
        return correctedWeight(imbalancedChild, weightError)
    }
}

fun main() {
    val testInput = """
        pbga (66)
        xhth (57)
        ebii (61)
        havc (66)
        ktlj (57)
        fwft (72) -> ktlj, cntj, xhth
        qoyq (66)
        padx (45) -> pbga, havc, qoyq
        tknk (41) -> ugml, padx, fwft
        jptl (61)
        ugml (68) -> gyxo, ebii, jptl
        gyxo (61)
        cntj (57)
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day07.part1(testInput))
    println(Day07.part2(testInput))

    println("------Real------")
    val input = readInput(2017, 7)
    println("Part 1 result: ${Day07.part1(input)}")
    println("Part 2 result: ${Day07.part2(input)}")
    timingStatistics { Day07.part1(input) }
    timingStatistics { Day07.part2(input) }
}