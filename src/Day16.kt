import java.lang.Integer.max

object Day16 {
    class Valve(
        val id: String,
        val rate: Int,
        var neighbors: List<Valve>,
        private val neighborIds: List<String>,
    ) {
        companion object {
            fun of(line: String): Valve {
                val id = line.drop(6).take(2)
                val rate = line.substringAfter("rate=").substringBefore(";").toInt()
                val neighborIds = line.substringAfter("valves ").split(", ")
                return Valve(id, rate, listOf(), neighborIds)
            }
        }

        fun updateNeighbors(valves: Map<String, Valve>) {
            neighbors = neighborIds.map { valves[it] ?: error("neighbor not in map") }
        }

        override fun toString(): String {
            return "$id: $rate → $neighborIds"
        }

        fun distances(keys: Set<String>): Map<Pair<String, String>, Int> {
            val remaining = keys.toMutableSet()
            remaining.remove(id)
            val rtn = mutableMapOf((id to id) to 0)
            var dist = 1
            var frontier = neighbors.toSet()
            do {
                rtn.putAll(frontier.associate { (id to it.id) to dist })
                remaining.removeAll(frontier.map { it.id }.toSet())
                frontier = frontier.map { it.neighbors }.flatten().filter { it.id in remaining }.toSet()
                dist++
            } while (remaining.isNotEmpty())
            return rtn
        }
    }

    class PressureSearch(val valves: Map<String, Valve>) {
        val distances: Map<Pair<String, String>, Int> = getAllDistances(valves)
        /*init {

            println(distances)
        }*/

        private fun getAllDistances(valves: Map<String, Valve>): Map<Pair<String, String>, Int> {
            val rtn = mutableMapOf<Pair<String, String>, Int>()

            valves.forEach {
                rtn.putAll(it.value.distances(valves.keys))
            }
            return rtn
        }

        fun maxReleaseFrom(
            state: SearchState,
            bestSoFar: Int,
        ): Int {
            val optimistic = state.unOpened.map { it.rate }.sortedDescending()
                .mapIndexed { index, rate -> max(0,(state.remainingMinutes - (index + 1) * 2)) * rate }
                .fold(0) { a, b -> a + b } + state.releasedSoFar
            if (optimistic <= bestSoFar) {
                return state.releasedSoFar
            }
            val nextSteps = state.unOpened.map {
                val remaining = state.remainingMinutes - distances[state.position to it.id]!! - 1
                SearchState(it.id, remaining, remaining * it.rate, state.unOpened - setOf(it), state.releasedSoFar + remaining * it.rate)
            }.sortedByDescending { it.releasedSoFar }
            val bestNext = nextSteps.maxOfOrNull { it.releasedHere }
            if (bestNext == null || bestNext <= 0) {
                return state.releasedSoFar
            }
            var currentBest = bestSoFar
            for (n in nextSteps) {
                currentBest = max(currentBest, maxReleaseFrom(n, currentBest))
            }
            return currentBest
        }
    }

    data class SearchState(
        val position: String,
        val remainingMinutes: Int,
        val releasedHere: Int,
        val unOpened: Set<Valve>,
        val releasedSoFar: Int,
    )

    private fun parse(input: List<String>): Map<String, Valve> {
        val valves = input.map { Valve.of(it) }
        val map = valves.associateBy { it.id }
        valves.forEach { it.updateNeighbors(map) }
        return map
    }

    fun part1(input: List<String>): Int {
        val valves = parse(input)
        println(valves)
        println("nonzero " + valves.filterValues { it.rate > 0 })
        println("nonzero count " + valves.count { it.value.rate > 0 })
        val s = PressureSearch(valves)
        return s.maxReleaseFrom(SearchState("AA", 30, 0, valves.values.filter { it.rate != 0 }.toSet(), 0), 0)
    }

    fun part2(input: List<String>): Long {
        val parsed = parse(input)
        return 0L
    }
}

fun main() {
    val testInput = """
        Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
        Valve BB has flow rate=13; tunnels lead to valves CC, AA
        Valve CC has flow rate=2; tunnels lead to valves DD, BB
        Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
        Valve EE has flow rate=3; tunnels lead to valves FF, DD
        Valve FF has flow rate=0; tunnels lead to valves EE, GG
        Valve GG has flow rate=0; tunnels lead to valves FF, HH
        Valve HH has flow rate=22; tunnel leads to valves GG
        Valve II has flow rate=0; tunnels lead to valves AA, JJ
        Valve JJ has flow rate=21; tunnel leads to valves II
    """.trimIndent().split("\n")
    println("------Tests------")
    println("part 1: " + Day16.part1(testInput))
    println("part 2: " + Day16.part2(testInput))

    println("------Real------")
    val input = readInput("resources/day16")
    println("part 1: " + Day16.part1(input))
    println("part 2: " + Day16.part2(input))
}
