package y2018

import util.readInput
import util.timingStatistics
import java.util.PriorityQueue

data class StepNode(
    val name: Char,
    val dependsOn: MutableList<StepNode> = mutableListOf()
) {
    fun time(): Int {
        return name - 'A' + 1
    }
}

object Day07 {
    private fun parse(input: List<String>): Collection<StepNode> {
        val dependencies = input.map { line ->
            line[5] to line[36]
        }
        val nodes = (dependencies.map { it.second } + dependencies.map { it.first }).distinct().associateWith { StepNode(it) }
        dependencies.forEach { (pre, post) ->
            nodes[post]?.dependsOn?.add(nodes[pre]!!)
        }
        return nodes.values
    }

    fun part1(input: List<String>): String {
        val nodes = parse(input).sortedBy { it.name }.toMutableList()
        var result = ""
        while (nodes.isNotEmpty()) {
            val noDependencies = nodes.first {
                it.dependsOn.isEmpty()
            }
            result += noDependencies.name
            nodes.remove(noDependencies)
            nodes.forEach {
                it.dependsOn.remove(noDependencies)
            }
        }
        return result
    }

    fun part2(input: List<String>, workers: Int = 5, baseTime: Int = 60): Int {
        val remainingSteps = parse(input).sortedBy { it.name }.toMutableList()
        var time = 0
        val inProgress = PriorityQueue<Pair<Int, StepNode>> { (t1, _), (t2, _)  ->
            t1.compareTo(t2)
        }
        while (remainingSteps.isNotEmpty()) {
            val noDependencies = remainingSteps.filter { it.dependsOn.isEmpty() }.take(workers - inProgress.size)
            noDependencies.forEach {
                inProgress.add(time + it.time() + baseTime to it)
                remainingSteps.remove(it)
            }
            do {
                val doneStep = inProgress.remove()
                time = doneStep.first
                remainingSteps.forEach {
                    it.dependsOn.remove(doneStep.second)
                }
            } while (inProgress.peek()?.first == time)
        }
        return time
    }
}

fun main() {
    val testInput = """
        Step C must be finished before step A can begin.
        Step C must be finished before step F can begin.
        Step A must be finished before step B can begin.
        Step A must be finished before step D can begin.
        Step B must be finished before step E can begin.
        Step D must be finished before step E can begin.
        Step F must be finished before step E can begin.
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day07.part1(testInput))
    println(Day07.part2(testInput, 2, 0))

    println("------Real------")
    val input = readInput(2018, 7)
    println("Part 1 result: ${Day07.part1(input)}")
    println("Part 2 result: ${Day07.part2(input)}")
    timingStatistics { Day07.part1(input) }
    timingStatistics { Day07.part2(input) }
}