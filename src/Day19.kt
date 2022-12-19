import kotlin.math.max

object Day19 {
    class Resources(
        ore: Int = 0,
        clay: Int = 0,
        obsidian: Int = 0,
        geode: Int = 0,
    ) {
        constructor(list: List<Int>) : this(list[0], list[1], list[2], list[3])

        val amounts = listOf(ore, clay, obsidian, geode)
        val ore: Int
            get() = amounts[0]
        val clay: Int
            get() = amounts[1]
        val obsidian: Int
            get() = amounts[2]
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

        fun maxGeodes(): Int {
            if (remainingMinutes == 1) {
                return collected.geode + robotNumbers.geode
            }
            if (key() in calculatedStates) {
                cacheLookups++
                return calculatedStates[key()] ?: error("checked null first")
            }


            val minutes = minutesToMake()
            val newStates = minutes.mapIndexed { idx, min ->
                if (min == null) {
                    null
                } else if (min >= remainingMinutes) {
                    val res = collected.geode + robotNumbers.geode * remainingMinutes
                    calculatedStates[key()] = res
                    return res
                } else {
                    this.copy(
                        remainingMinutes = remainingMinutes - min - 1,
                        collected = collected + robotNumbers * (min + 1) - blueprint[idx],
                        robotNumbers = robotNumbers + singleRobots[idx]
                    )
                }
            }.filterNotNull().sortedByDescending { it.collected.geode }
            var currentBest = 0
            newStates.forEach {
                currentBest = max(currentBest, it.maxGeodes())
            }
            return currentBest
        }

        fun minutesToMake(): List<Int?> {
            return blueprint.costList().map { minutesToMake(it) }
        }

        private fun minutesToMake(cost: Resources): Int? {
            val remainingCost = cost - collected
            /*if (remainingCost.amounts.all { it <= 0 }) {
                return 1
            }*/
            if (cost.amounts.zip(robotNumbers.amounts).any { (c, r) -> c > 0 && r == 0 }) {
                return null
            }
            return remainingCost.amounts.zip(robotNumbers.amounts).maxOfOrNull { (c, r) -> if (c <= 0) 1 else c / r + 1 }
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
        val factories = blueprints.map { FactoryState(it, remainingMinutes = remainingMinutes) }
        return factories.map { it.maxGeodes() }.mapIndexed { index, geodes ->
            println("${index + 1}: $geodes")
            (index + 1) * geodes
        }.sum()
    }

    fun part2(input: List<String>): Long {
        val parsed = parse(input)
        return 0L
    }
}

fun main() {
    val testInput1 = """
        Blueprint 1: Each ore robot costs 100 ore. Each clay robot costs 100 ore. Each obsidian robot costs 100 ore and 100 clay. Each geode robot costs 100 ore and 100 obsidian.        
    """.trimIndent().split("\n")
    //println(Day19.part1(testInput1, 30))
    val testInput = """
        Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
        Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day19.part1(testInput, 24))
    println(Day19.part2(testInput))

    println("------Real------")
    val input = readInput("resources/day19")
    println(Day19.part1(input))
    println(Day19.part2(input))
}
