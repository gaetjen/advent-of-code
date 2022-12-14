object Day14 {
    private fun parse(input: List<String>): Set<Pos> {
        return input.map { lineToCoordinates(it) }.flatten().toSet()
    }

    private fun lineToCoordinates(line: String): List<Pos> {
        1..5
        return line.split(Regex(" -> |,"))
            .asSequence()
            .map { it.toInt() }
            .chunked(2)
            .map { (x, y) -> x to y }
            .windowed(2, 1)
            .map { (p1, p2) -> (p1..p2) }
            .flatten()
            .toList()
    }

    operator fun Pos.rangeTo(other: Pos): List<Pos> {
        return if (first == other.first) {
            if (second < other.second) {
                (second..other.second).map { first to it }
            } else {
                (second downTo other.second).map { first to it }
            }
        } else {
            if (first < other.first) {
                (first..other.first).map { it to second }
            } else {
                (first downTo other.first).map { it to second }
            }
        }
    }

    fun part1(input: List<String>): Int {
        val blocked = parse(input).toMutableSet()
        val maxY = blocked.maxBy { it.second }.second
        val start = 500 to 0
        val blocks = blocked.size
        outer@ while (true) {
            var currentSand = start
            while (true) {
                val next = oneSandStep(currentSand, blocked)!!
                if (next == currentSand) {
                    blocked.add(currentSand)
                    break
                }
                currentSand = next
                if (currentSand.second > maxY) {
                    break@outer
                }
            }
        }
        return blocked.size - blocks
    }

    private fun oneSandStep(start: Pos, blocks: Set<Pos>): Pos? {
        val down = Direction.UP.move(start)
        val downLeft = Direction.LEFT.move(down)
        val downRight = Direction.RIGHT.move(down)
        return listOf(down, downLeft, downRight, start).firstOrNull { it !in blocks }
    }

    fun part2(input: List<String>): Int {
        val blocked = parse(input).toMutableSet()
        val maxY = blocked.maxBy { it.second }.second + 2
        blocked.addAll((500 - 2 * maxY to maxY)..(500 + 2 * maxY to maxY))
        val start = 500 to 0
        val blocks = blocked.size
        outer@ while (true) {
            var currentSand = start
            while (true) {
                val next = oneSandStep(currentSand, blocked) ?: break@outer
                if (next == currentSand) {
                    blocked.add(currentSand)
                    break
                }
                currentSand = next
            }
        }
        return blocked.size - blocks
    }
}

fun main() {
    val testInput = """
        498,4 -> 498,6 -> 496,6
        503,4 -> 502,4 -> 502,9 -> 494,9
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day14.part1(testInput))
    println(Day14.part2(testInput))

    println("------Real------")
    val input = readInput("resources/day14")
    println(Day14.part1(input))
    println(Day14.part2(input))
}
