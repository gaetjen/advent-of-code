import kotlin.math.ceil

object Day19 {
    class Resources(
        ore: Int = 0,
        clay: Int = 0,
        obsidian: Int = 0,
        geode: Int = 0,
    ) {
        constructor(list: List<Int>) : this(list[0], list[1], list[2], list[3])

        val amounts = listOf(ore, clay, obsidian, geode)
        val geode: Int
            get() = amounts[3]


        operator fun plus(other: Resources): Resources {
            return Resources(amounts.zip(other.amounts).map { (t, o) -> t + o })
        }

        operator fun minus(other: Resources): Resources {
            return Resources(amounts.zip(other.amounts).map { (t, o) -> t - o })
        }

        operator fun div(other: Resources): Resources {
            return Resources(amounts.zip(other.amounts).map { (t, o) -> if (o == 0) 0 else t / o + 1 })
        }

        operator fun times(factor: Int): Resources {
            return Resources(amounts.map { it * factor })
        }

        override fun toString(): String {
            return amounts.toString()
        }
    }

    data class Blueprint(
        val id: Int,
        val oreRobotCost: Resources,
        val clayRobotCost: Resources,
        val obsidianRobotCost: Resources,
        val geodeRobotCost: Resources,
    ) {
        fun costList() = listOf(oreRobotCost, clayRobotCost, obsidianRobotCost, geodeRobotCost)
        operator fun get(idx: Int): Resources {
            return costList()[idx]
        }
    }

    val singleRobots = listOf(
        Resources(1),
        Resources(clay = 1),
        Resources(obsidian = 1),
        Resources(geode = 1)
    )

    data class FactoryState(
        val blueprint: Blueprint,
        val remainingMinutes: Int = 24,
        val collected: Resources = Resources(),
        val robotNumbers: Resources = Resources(1),
    ) {
        companion object {
            val calculatedStates: MutableMap<List<Int>, Int> = mutableMapOf()
            var cacheLookups = 0
        }

        fun key(): List<Int> {
            return collected.amounts + robotNumbers.amounts + listOf(remainingMinutes)
        }

        val maxBots = transpose(blueprint.costList().map { it.amounts }).map { it.max() }

        fun maxGeodes(): Int {
            if (remainingMinutes == 0) {
                return collected.geode
            }
            if (remainingMinutes == 1) {
                return collected.geode + robotNumbers.geode
            }
            if (key() in calculatedStates) {
                cacheLookups++
                return calculatedStates[key()] ?: error("checked null first")
            }


            val minutes = minutesToMake()
            val skipState = this.copy(
                remainingMinutes = 0,
                collected = collected + robotNumbers * remainingMinutes
            )

            val newStates = minutes.mapIndexed { idx, min ->
                if (min == null) {
                    null
                } else if (min >= remainingMinutes || (idx < 3 && maxBots[idx] < robotNumbers.amounts[idx])) {
                    skipState
                } else {
                    this.copy(
                        remainingMinutes = remainingMinutes - min,
                        collected = collected + robotNumbers * min - blueprint[idx],
                        robotNumbers = robotNumbers + singleRobots[idx]
                    )
                }
            }

            val res = if (minutes.last() == 1) {
                newStates.last()?.maxGeodes() ?: skipState.maxGeodes()
            } else
                newStates.filterNotNull().distinct().maxOf {
                it.maxGeodes()
            }
            calculatedStates[key()] = res
            return res
        }

        fun minutesToMake(): List<Int?> {
            return blueprint.costList().map { minutesToMake(it) }
        }

        private fun minutesToMake(cost: Resources): Int? {
            val remainingCost = cost - collected
            if (cost.amounts.zip(robotNumbers.amounts).any { (c, r) -> c > 0 && r == 0 }) {
                return null
            }
            return remainingCost.amounts.zip(robotNumbers.amounts).maxOfOrNull { (c, r) -> minutesToGetResources(c, r) }
        }

        private fun minutesToGetResources(c: Int, r: Int): Int {
            return if (c <= 0) {
                1
            } else {
                ceil(c.toDouble() / r).toInt() + 1
            }
        }
    }

    private fun parse(input: List<String>): List<Blueprint> {
        return input.mapIndexed { idx, line ->
            val costs = line.split("robot costs ").drop(1)
            val orePerOre = costs[0].takeWhile { it != ' ' }.toInt()
            val orePerClay = costs[1].takeWhile { it != ' ' }.toInt()
            val orePerObs = costs[2].takeWhile { it != ' ' }.toInt()
            val orePerGeode = costs[3].takeWhile { it != ' ' }.toInt()
            val clayPerObs = costs[2].split("and ")[1].takeWhile { it != ' ' }.toInt()
            val obsPerGeode = costs[3].split("and ")[1].takeWhile { it != ' ' }.toInt()
            Blueprint(
                idx + 1,
                Resources(orePerOre),
                Resources(orePerClay),
                Resources(orePerObs, clayPerObs),
                Resources(orePerGeode, obsidian = obsPerGeode)
            )
        }
    }

    fun part1(input: List<String>, remainingMinutes: Int = 24): Int {
        val blueprints = parse(input)
        println(blueprints)
        val factories = blueprints.map { FactoryState(it, remainingMinutes = remainingMinutes) }
        return factories.asSequence().map {
            val res = it.maxGeodes()
            println("did cache lookups: " + FactoryState.cacheLookups)
            println("number states: " + FactoryState.calculatedStates.size)
            FactoryState.calculatedStates.clear()
            res
        }.mapIndexed { index, geodes ->
            println("${index + 1}: $geodes")
            (index + 1) * geodes
        }.sum()
    }

    fun part2(input: List<String>): Long {
        val blueprints = parse(input).take(3)
        val factories = blueprints.map { FactoryState(it, remainingMinutes = 32) }
        val geodes = factories.asSequence().map {
            val res = it.maxGeodes()
            println("did cache lookups: " + FactoryState.cacheLookups)
            println("number states: " + FactoryState.calculatedStates.size)
            FactoryState.calculatedStates.clear()
            res
        }.mapIndexed { index, geodes ->
            println("${index + 1}: $geodes")
            geodes
        }
        return geodes.reduce { acc, i -> acc * i }.toLong()
    }
}

fun main() {
    val testInput1 = """
        Blueprint 1: Each ore robot costs 100 ore. Each clay robot costs 100 ore. Each obsidian robot costs 100 ore and 100 clay. Each geode robot costs 100 ore and 100 obsidian.        
    """.trimIndent().split("\n")
    println(Day19.part1(testInput1, 30))
    val testInput = """
        Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
        Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.
    """.trimIndent().split("\n")
    println("------Tests------")
    println("part 1: " + Day19.part1(testInput))
    //println("part 2: " + Day19.part2(testInput))

    println("------Real------")
    val input = readInput("resources/day19")
    println("part 1: " + Day19.part1(input))
    // bp 1: 10, bp 3: 37
    println("part 2: " + Day19.part2(input))
}
