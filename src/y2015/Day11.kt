package y2015

object Day11 {

    fun next(str: String): String {
        val chars = str.toCharArray().toMutableList()
        var idx = str.length - 1
        while (idx >= 0) {
            chars[idx] = chars[idx] + 1
            if (chars[idx] > 'z') {
                chars[idx] = 'a'
                idx--
            } else {
                break
            }
        }
        return chars.joinToString(separator = "") { it.toString() }
    }

    fun isSecure(password: String) : Boolean {
        if (password.contains(Regex("[iol]"))) {
            return false
        }
        if (!password.contains(Regex(
            "abc|bcd|cde|def|efg|fgh|pqr|qrs|rst|stu|tuv|uvw|vwx|wxy|xyz"
        ))) {
            return false
        }
        return password.windowed(2, 1).filter { it[0] == it[1] }.toSet().size >= 2
    }

    fun part1(input: String): String {
        var result = input
        while (true) {
            result = next(result)
            if (isSecure(result)) {
                break
            }
        }
        return result
    }

    fun part2(input: String): String {
        return part1(part1(input))
    }
}

fun main() {
    println((-1).mod(3))
    println(Day11.next("zzz"))
    println("ghjaabcc is secure: ${Day11.isSecure("ghjaabcc")}")
    val testInput = "abcdefgh"
    println("------Tests------")
    println(Day11.part1(testInput))
    println(Day11.part1("ghijklmn"))
    println(Day11.part2(testInput))

    println("------Real------")
    val input = "hxbxwxba"
    println(Day11.part1(input))
    println(Day11.part2(input))
}
