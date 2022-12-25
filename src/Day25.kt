object Day25 {
    private fun powersOf(n: Int) = sequence {
        var p = 1L
        while (true) {
            yield(p)
            p *= n
        }
    }

    private val breaks = powersOf(5).runningFold(0L) { acc, p -> acc + p * 2 }.takeWhile { it < 33078355623611 }.toList()

    private fun snafuToDecimal(input: String): Long {
        return input.reversed().map {
            when (it) {
                '-' -> -1
                '=' -> -2
                else -> it.digitToInt()
            }
        }.asSequence().zip(powersOf(5)).sumOf { (digit, power) ->
            digit * power
        }
    }

    fun part1(input: List<String>): String {
        val total = input.sumOf { snafuToDecimal(it) }
        println("total: $total")
        println("breaks: $breaks")
        repeat(30) {
            println("$it ->\t\t${decimalToSnafu(it.toLong())}")
        }
        return decimalToSnafu(total)
    }

    private fun decimalToSnafu(n: Long): String {
        val normal = n.toString(5).reversed().map { it.digitToInt() } + 0
        var carryOver = 0
        return normal.map {
            val new = it + carryOver
            if (new > 2) {
                carryOver = 1
                new - 5
            } else {
                carryOver = 0
                new
            }
        }.joinToString("") {
            when (it) {
                -2 -> "="
                -1 -> "-"
                else -> it.toString()
            }
        }.reversed()
    }
}

fun main() {
    val testInput = """
        1=-0-2
        12111
        2=0=
        21
        2=01
        111
        20012
        112
        1=-1=
        1-12
        12
        1=
        122
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day25.part1(testInput))

    println("------Real------")
    val input = readInput("resources/day25")
    println(Day25.part1(input))
}
