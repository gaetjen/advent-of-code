import java.io.File

object Day14 {
    private fun parse(input: List<String>): Any {
        TODO()
    }

    fun part1(input: List<String>): Long {
        val parsed = parse(input)
        return 0L
    }

    fun part2(input: List<String>): Long {
        val parsed = parse(input)
        return 0L
    }
}

fun main() {
    val day = "14"
    val f = File("src/Day$day.kt")
    val content = f.readText()
    val nextDay = String.format("%02d", day.toInt() + 1)
    File("src/Day$nextDay.kt").writeText(content.replace(day, nextDay))
    f.writeText(content.split("\n").filterIndexed { idx, _ -> idx > 1 && idx !in 19..24 }.joinToString("\n"))
    val testInput = """
        
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day14.part1(testInput))
    println(Day14.part2(testInput))

    println("------Real------")
    val input = readInput("resources/day14")
    println(Day14.part1(input))
    println(Day14.part2(input))
}
