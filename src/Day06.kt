object Day06 {
    fun part1(input: String): Int {
        return numberOfCharsBeforeNDistinct(input, 4)
    }

    private fun numberOfCharsBeforeNDistinct(input: String, n: Int) = input
        .mapIndexed { index, _ ->
            index + n to
                    input
                        .drop(index)
                        .take(n)
                        .toSet()
                        .count()
        }
        .first { (_, distinct) -> distinct == n }
        .first

    fun part2(input: String): Int {
        return numberOfCharsBeforeNDistinct(input, 14)
    }

}

fun main() {
    println(Day06.part1("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw"))
    val input = readInput("resources/day06")
    println(Day06.part1(input.first()))
    println(Day06.part2(input.first()))
}