package y2016

import util.readInput

interface NumberGiver {
    fun getNumberFor(target: Int): Int
}

data class Input(val n: Int) : NumberGiver {
    override fun getNumberFor(target: Int): Int {
        return n
    }
}

data class Bot(
    val id: Int,
    val inputMap: Map<Int, List<NumberGiver>>,
    val lowTarget: Int,
    val highTarget: Int
) : NumberGiver {
    private var inputValues: List<Int>? = null
    override fun getNumberFor(target: Int): Int {
        if (inputValues == null) {
            inputValues = inputMap[this.id]!!.map { it.getNumberFor(this.id) }
        }
        return when (target) {
            lowTarget -> inputValues!!.min()
            highTarget -> inputValues!!.max()
            else -> error("$target not a valid target for ${this.id} with targets $lowTarget and $highTarget")
        }
    }

    override fun toString(): String {
        return "$id, $lowTarget, $highTarget"
    }
}

object Day10 {
    private fun parse(input: List<String>): Map<Int, List<NumberGiver>> {
        val inputBots = mutableMapOf<Int, MutableList<NumberGiver>>()
        input.forEach { line ->
            val els = line.split(" ")
            if (els[0] == "value") {
                val newInput = Input(els[1].toInt())
                val target = els.last().toInt()
                addNewNumberGiverToBotMap(inputBots, target, newInput)
            } else {
                val id = els[1].toInt()
                val lowTarget = if (els[5] == "output") -els[6].toInt() - 5000 else els[6].toInt()
                val highTarget = if (els[10] == "output") -els.last().toInt() - 5000 else els.last().toInt()
                val newBot = Bot(
                    id,
                    inputBots,
                    lowTarget,
                    highTarget
                )
                addNewNumberGiverToBotMap(inputBots, lowTarget, newBot)
                addNewNumberGiverToBotMap(inputBots, highTarget, newBot)
            }
        }
        return inputBots
    }

    private fun addNewNumberGiverToBotMap(
        bots: MutableMap<Int, MutableList<NumberGiver>>,
        target: Int,
        new: NumberGiver
    ) {
        if (bots[target] == null) {
            bots[target] = mutableListOf(new)
        } else {
            bots[target]!!.add(new)
        }
    }

    fun part1(input: List<String>, cmp1: Int, cmp2: Int): Set<Int> {
        val botMap = parse(input)
        val botLookup = botMap.values.flatten().filterIsInstance<Bot>().associateBy { it.id }
        val trace1 = getTrace(botMap, botLookup, cmp1)
        val trace2 = getTrace(botMap, botLookup, cmp2)
        return trace1.intersect(trace2)
    }

    private fun getTrace(botMap: Map<Int, List<NumberGiver>>, botLookup: Map<Int, Bot>, n: Int): Set<Int> {
        val start = botMap.entries.first { entry ->
            entry.value.any { it is Input && it.n == n }
        }.key
        println("first bot for $n: $start")
        val trace = mutableSetOf(start)
        var bot = botLookup[start]
        while (bot != null) {
            bot = if (bot.getNumberFor(bot.lowTarget) == n) {
                botLookup[bot.lowTarget]
            } else {
                botLookup[bot.highTarget]
            }
            bot?.id?.let { trace.add(it) }
        }
        return trace
    }

    fun part2(input: List<String>): Int {
        val botMap = parse(input)
        val botLookup = botMap.values.flatten().filterIsInstance<Bot>().associateBy { it.id }
        val botZero = botLookup.values.first { it.lowTarget == -5000 || it.highTarget == -5000 }
        val botOne = botLookup.values.first { it.lowTarget == -5001 || it.highTarget == -5001 }
        val botTwo = botLookup.values.first { it.lowTarget == -5002 || it.highTarget == -5002 }
        return botZero.getNumberFor(-5000) * botOne.getNumberFor(-5001) * botTwo.getNumberFor(-5002)
    }
}

fun main() {
    val testInput = """
        value 5 goes to bot 2
        bot 2 gives low to bot 1 and high to bot 0
        value 3 goes to bot 1
        bot 1 gives low to output 1 and high to bot 0
        bot 0 gives low to output 2 and high to output 0
        value 2 goes to bot 2
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day10.part1(testInput, 5, 2))
    println(Day10.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2016/day10")
    println(Day10.part1(input, 61, 17))
    println(Day10.part2(input))
}