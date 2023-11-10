package y2015

object Day23 {
    fun part1(n: Int): Int {
        var steps = 0
        var current = n.toLong()
        while (true) {
            if (current == 1L) {
                return steps
            }
            steps++
            current = if (current % 2 == 0L) {
                current / 2
            } else {
                current * 3 + 1
            }
        }
    }

}


fun main() {
    val start = ((2 * 3 * 3* 3 + 2) *3 + 2)* 3 * 3* 3 + 1
    val start2 = (((((((3 + 2) *3 + 2) * 9 + 2) * 3 +1) * 3 + 1) * 3 + 2) * 3 + 1) * 9 + 1
    println("Start: $start")
    println("start 2: $start2")
    println("------Real------")
    println(Day23.part1(start))
    println(Day23.part1(start2))
}
