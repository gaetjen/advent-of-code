fun main() {
    fun inputToCalories(input: List<String>): List<Int> {
        return input
            .split { it.isEmpty() }
            .map {
                it.sumOf { str -> str.toInt() }
            }
    }

    fun part1(input: List<String>): Int? {
        return inputToCalories(input).maxOrNull()
    }

    fun part2(input: List<String>): Int {
        return inputToCalories(input).sortedDescending().take(3).sum()
    }


    val input = readInput("resources/day01_part1")
    println(part1(input))
    println(part2(input))
}
