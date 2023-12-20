package y2023

import util.readInput
import util.timingStatistics
import java.util.ArrayDeque
import java.util.Queue

typealias Pulse = Boolean
typealias State = Boolean

object Day20 {
    sealed class Module {
        abstract val destinations: List<String>
        abstract val id: String
        abstract fun computeOutput(signal: Signal): Pulse?
    }

    data class FlipFlop(
        override val id: String,
        var state: State = false,
        override val destinations: List<String>
    ) : Module() {
        override fun computeOutput(signal: Signal): Pulse? {
            if (!signal.pulse) {
                state = !state
                return state
            }
            return null
        }
    }

    data class Conjunction(
        override val id: String,
        val memory: MutableMap<String, Pulse>,
        override val destinations: List<String>
    ) : Module() {
        override fun computeOutput(signal: Signal): Pulse {
            memory[signal.source] = signal.pulse
            return !memory.values.all { it }
        }
    }

    data class Broadcaster(
        override val id: String = "broadcaster",
        override val destinations: List<String>
    ) : Module() {
        override fun computeOutput(signal: Signal): Pulse {
            return signal.pulse
        }
    }

    data class Signal(
        val pulse: Pulse,
        val source: String,
        val target: String
    )

    private fun parse(input: List<String>): Map<String, Module> {
        return input.map { line ->
            val (type, destinations) = line.split(" -> ")
            val destList = destinations.split(", ")
            when (line.first()) {
                '%' -> FlipFlop(id = type.drop(1), destinations = destList)
                '&' -> Conjunction(id = type.drop(1), memory = mutableMapOf(), destinations = destList)
                else -> Broadcaster(destinations = destList)
            }
        }.associateBy { it.id }
    }

    fun part1(input: List<String>): Long {
        val parsed = parse(input)
        initializeConjunctions(parsed)
        var lowCount = 0
        var highCount = 0
        repeat(1000) {
            val (low, high) = pushButton(parsed)
            lowCount += low
            highCount += high
        }
        return lowCount.toLong() * highCount.toLong()
    }

    private fun initializeConjunctions(parsed: Map<String, Module>): List<String> {
        val inverseMap = parsed.values.flatMap { module ->
            module.destinations.map { it to module.id }
        }.groupBy { it.first }
            .mapValues { (_, destinationSourcePairs) ->
                destinationSourcePairs.map { it.second }
            }
        parsed.values.forEach { module ->
            if (module is Conjunction) {
                inverseMap[module.id]?.forEach {
                    module.memory[it] = false
                }
            }
        }
        return inverseMap[inverseMap["rx"]!!.first()]!!
    }

    private fun pushButton(parsed: Map<String, Module>) : Pair<Int, Int>{
        var lowCount = 0
        var highCount = 0
        val signals: Queue<Signal> = ArrayDeque()
        signals.add(Signal(false, "button", "broadcaster"))
        while (signals.isNotEmpty()) {
            val signal = signals.poll()
            if (!signal.pulse) lowCount++ else highCount++
            val module = parsed[signal.target] ?: continue
            val output = module.computeOutput(signal)
            module.destinations.forEach { destination ->
                if (output != null) {
                    signals.add(
                        Signal(
                            pulse = output,
                            source = module.id,
                            target = destination
                        )
                    )
                }
            }
        }

        return lowCount to highCount
    }

    fun part2(input: List<String>): Int {
        var parsed = parse(input)
        val conjunctionInputs = initializeConjunctions(parsed)
        println("conjunction inputs: $conjunctionInputs")
        val inputFirsts = conjunctionInputs.map { toTrack ->
            parsed = parse(input)
            initializeConjunctions(parsed)
            var buttonPushes = 0
            while (true) {
                buttonPushes++
                if (pushButton2(parsed, toTrack, true)) return@map buttonPushes
                if (buttonPushes % 1000000 == 0) {
                    println("button pushes: $buttonPushes")
                }
            }
        }
        println("input firsts: $inputFirsts") // https://www.wolframalpha.com/input?i=lcm
        return -1
    }

    private fun pushButton2(parsed: Map<String, Module>, trackedInput: String, expectedValue: Boolean) : Boolean {
        val signals: Queue<Signal> = ArrayDeque()
        signals.add(Signal(false, "button", "broadcaster"))
        while (signals.isNotEmpty()) {
            val signal = signals.poll()
            if (signal.source == trackedInput && signal.pulse == expectedValue) return true
            val module = parsed[signal.target] ?: continue
            val output = module.computeOutput(signal)
            module.destinations.forEach { destination ->
                if (output != null) {
                    signals.add(
                        Signal(
                            pulse = output,
                            source = module.id,
                            target = destination
                        )
                    )
                }
            }
        }
        return false
    }
}

fun main() {
    val testInput = """
        broadcaster -> a
        %a -> inv, con
        &inv -> b
        %b -> con
        &con -> rx
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day20.part1(testInput))
    println(Day20.part2(testInput))

    println("------Real------")
    val input = readInput(2023, 20)
    println("Part 1 result: ${Day20.part1(input)}")
    println("Part 2 result: ${Day20.part2(input)}")
    timingStatistics { Day20.part1(input) }
    //timingStatistics { Day20.part2(input) }
}