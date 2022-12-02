/**
 * A > Y
 * B > Z
 * C > X
 * X > B
 * Y > C
 * Z > A
 */

val oppMap = mapOf('A' to 0, 'B' to 1, 'C' to 2)
val meMap = mapOf('X' to 0, 'Y' to 1, 'Z' to 2)

fun part1(input: List<String>): Int {
    return input.sumOf { inputToScore(it) }
}

fun inputToNums(input: String): Pair<Int, Int> {
    return oppMap[input.first()]!! to meMap[input.last()]!!
}

fun score(opp: Int, me: Int): Int {
    return when (opp) {
        (me + 1) % 3 -> 0
        me -> 3
        else -> 6
    }
}

fun inputToScore(input: String): Int {
    val (opp, me) = inputToNums(input)
    return me + 1 + score(opp, me)
}

fun part2(input: List<String>): Int {
    return input.sumOf { inputToScore2(it) }
}

fun inputToScore2(input: String): Int {
    val (opp, result) = inputToNums(input)
    val myPlay = (opp + result + 2) % 3
    return result * 3 + 1 + myPlay
}

fun main() {
    val input = readInput("resources/day02")
    println(part1(input))
    println(part2(input))
}
