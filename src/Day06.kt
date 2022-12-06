import kotlin.math.max

object Day06 {
    fun part1(input: String): Int {
        return input
            .mapIndexed { index, _ ->
                index + 1 to
                        input
                            .drop(max(0, index - 3))
                            .take(4)
                            .toSet()
                            .count()
            }
            .first { (_, distinct) -> distinct == 4 }
            .first
    }

    fun part2(input: String): Int {
        return input
            .mapIndexed { index, _ ->
                index + 1 to
                        input
                            .drop(max(0, index - 13))
                            .take(14)
                            .toSet()
                            .count()
            }
            .first { (_, distinct) -> distinct == 14 }
            .first
    }

}

fun main() {
    println(Day06.part1("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw"))
    val input = readInput("resources/day06")
    println(Day06.part1(input.first()))
    println(Day06.part2(input.first()))
}