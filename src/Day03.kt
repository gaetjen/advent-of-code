object Day03 {
    fun part1(input: List<String>): Int {
        return input.sumOf { wrongItem(it) }
    }

    private fun wrongItem(input: String): Int {
        val (c1, c2) = input.chunked(input.length / 2) {
            it.toSet()
        }
        val wrongItem = c1.intersect(c2).first()
        return itemToPriority(wrongItem)
    }

    private fun itemToPriority(item: Char) = if (item in ('a'..'z')) {
        (item - 'a') + 1
    } else {
        (item - 'A') + 27
    }

    fun part2(input: List<String>): Int {
        return input.chunked(3).sumOf { badge(it) }
    }

    private fun badge(items: List<String>) :Int {
        return items
            .map { it.toSet() }
            .reduce { acc, chars -> acc.intersect(chars) }
            .first()
            .let { itemToPriority(it) }
    }
}

fun main() {
    val input = readInput("resources/day03")
    println(Day03.part1(input))
    println(Day03.part2(input))
}