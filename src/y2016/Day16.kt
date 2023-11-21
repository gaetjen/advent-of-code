package y2016

object Day16 {
    fun part1(init: String, space: Int): String {
        var data = init
        while (data.length < space) {
            data = generateNext(data)
        }
        return checkSum(data.take(space))
    }

    private fun generateNext(data: String): String {
        val tail = data.reversed().replace('0', 'a').replace('1', 'b').replace('a', '1').replace('b', '0')
        return data + "0" + tail
    }

    private fun checkSum(str: String): String {
        val checks = str.chunked(2).joinToString("") {
            if (it[0] == it[1]) {
                "1"
            } else {
                "0"
            }
        }
        if (checks.length % 2 == 0) {
            return checkSum(checks)
        }
        return checks
    }
}

fun main() {
    val testInput = "10000"
    val testDiskSpace = 20
    println("------Tests------")
    println(Day16.part1(testInput, testDiskSpace))

    println("------Real------")
    val input = "00101000101111010"
    val diskSpace = 272
    println(Day16.part1(input, diskSpace))
    val diskSpace2 = 35_651_584
    println(Day16.part1(input, diskSpace2))
}