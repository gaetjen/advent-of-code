package y2015

import util.readInput

object Day13 {
    private fun parse(input: List<String>): Pair<Set<String>, Map<Pair<String, String>, Int>> {
        val names = mutableSetOf<String>()
        val happiness = input.associate {
            val elements = it.split(' ')
            val p1 = elements.first()
            val p2 = elements.last().dropLast(1)
            names.add(p1)
            names.add(p2)
            val amount = elements[3].toInt()
            (p1 to p2) to  if (elements[2] == "gain") {
                amount
            } else {
                -amount
            }
        }
        return names to happiness
    }

    fun part1(input: List<String>): Int {
        val (names, happiness) = parse(input)
        return maxHappiness(
            happiness,
            names.first(),
            names - names.first(),
            0,
            names.first()
        )
    }

    private fun maxHappiness(
        edges: Map<Pair<String, String>, Int>,
        latest: String,
        remaining: Set<String>,
        happiness: Int,
        start: String
    ): Int {
        if (remaining.isEmpty()) {
            return happiness + edges[latest to start]!! + edges[start to latest]!!
        }
        return remaining.maxOf {
            maxHappiness(
                edges,
                it,
                remaining - it,
                happiness + edges[latest to it]!! + edges[it to latest]!!,
                start
            )
        }
    }

    fun part2(input: List<String>): Int {
        val (names, happiness) = parse(input)
        val extraEdges = happiness + names.associate {
            (it to "Me") to 0
        } + names.associate {
            ("Me" to it) to 0
        }
        return maxHappiness(
            extraEdges,
            "Me",
            names,
            0,
            "Me"
        )
    }
}

fun main() {
    val testInput = """
        Alice would gain 54 happiness units by sitting next to Bob.
        Alice would lose 79 happiness units by sitting next to Carol.
        Alice would lose 2 happiness units by sitting next to David.
        Bob would gain 83 happiness units by sitting next to Alice.
        Bob would lose 7 happiness units by sitting next to Carol.
        Bob would lose 63 happiness units by sitting next to David.
        Carol would lose 62 happiness units by sitting next to Alice.
        Carol would gain 60 happiness units by sitting next to Bob.
        Carol would gain 55 happiness units by sitting next to David.
        David would gain 46 happiness units by sitting next to Alice.
        David would lose 7 happiness units by sitting next to Bob.
        David would gain 41 happiness units by sitting next to Carol.
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day13.part1(testInput))
    println(Day13.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2015/day13")
    println(Day13.part1(input))
    println(Day13.part2(input))
}
