package y2016

object Day19 {

    fun part1(input: Int): Int {
        // Josephus: https://www.youtube.com/watch?v=uCsD3ZGzMgE
        return (input.toString(2).drop(1) + "1").toInt(2)
    }

    fun part2Slow(input: Int): Int {
        val alive = MutableList(input) { it + 1 }
        var currentIdx = 0
        while (alive.size > 1) {
            if (alive.size % 100_000 == 0) {
                println("${alive.size} left")
            }
            val nextKill = currentIdx + alive.size / 2
            if (nextKill >= alive.size) {
                alive.removeAt(nextKill % alive.size)
                if (currentIdx == alive.size) {
                    currentIdx = 0
                }
            } else {
                alive.removeAt(nextKill)
                currentIdx = (currentIdx + 1) % alive.size
            }
        }
        return alive.first()
    }

     fun part2(input: Int): Int {
         var oneSurvives = 2
         while (oneSurvives * 3 - 2 <= input) {
             oneSurvives = oneSurvives * 3 - 2
         }
         return if (input == oneSurvives) {
             1
         } else if (input - oneSurvives < oneSurvives - 1) {
             input - oneSurvives + 1
         } else {
             val next = oneSurvives * 3 - 2
             val distanceToNext = next - input
             next - distanceToNext * 2 + 1
         }
     }
}

fun main() {
    for (i in (1..16)) {
        println("$i: " + Day19.part1(i))
    }
    println("--------PART 2---------")
    for (i in (1..82)) {
        println("$i: " + Day19.part2Slow(i))
    }
    println("-------VERIFICATION--------")
    for (i in (1..82)) {
        println("$i: " + Day19.part2(i))
    }
    println("------Real------")
    val input =3_004_953
    println(Day19.part1(input))
    println(Day19.part2(input))
}