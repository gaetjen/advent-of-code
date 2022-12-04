object Day04 {
    fun part1(input: List<String>): Int {
        return input.count { fullyContains(it) }
    }

    private fun fullyContains(input: String): Boolean {
        val (p1, p2) = input.split(",")
            .map {
                it.split("-")
                    .map { x -> x.toInt() }
            }
        val range1 =(p1[0]..p1[1])
            val range2 = (p2[0]..p2[1])
        return (p1[0] in range2 && p1[1] in range2) || (p2[0] in range1 && p2[1] in range1)
    }

    fun part2(input: List<String>): Int {
        return input.count {partlyContains(it)}
    }

    private fun partlyContains(input: String) : Boolean{
        val (p1, p2) = input.split(",")
            .map {
                it.split("-")
                    .map { x -> x.toInt() }
            }
        val range1 =(p1[0]..p1[1])
        val range2 = (p2[0]..p2[1])
        return (p1[0] in range2 || p1[1] in range2) || (p2[0] in range1 || p2[1] in range1)
    }
}

fun main() {
    val input = readInput("resources/day04")
    println(Day04.part1(input))
    println(Day04.part2(input))
}