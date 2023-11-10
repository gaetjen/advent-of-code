package y2015

object Day25 {

    fun part1(row: Int, column: Int): Long {
        val position = indicesToPosition(row, column)
        println("position: $position")
        var current = 20151125L
        repeat(position - 1) {
            current = (current * 252533) % 33554393
        }
        return current
    }

    private fun indicesToPosition(row: Int, column: Int): Int {
        var add = 1
        var currentPosition = 1
        repeat(row -1 ) {
            currentPosition += add
            add += 1
        }
        add += 1
        repeat(column - 1) {
            currentPosition += add
            add += 1
        }
        return currentPosition
    }
}

fun main() {
    val testRow = 4
    val testColumn = 3
    println("------Tests------")
    println(Day25.part1(testRow, testColumn))

    val row = 2978
    val column = 3083
    println("------Real------")
    println(Day25.part1(row, column))
}
