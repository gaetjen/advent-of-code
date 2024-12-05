package util


fun main() {
    val x = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
    //println("joinToString")
    timingStatistics(minRuns = 10_000, maxRuns = 10_000) { x.joinToString(separator = "") }
    println("buildString")
    timingStatistics(minRuns = 10_000, maxRuns = 10_000) { buildString { x.forEach { append(it) } } }
}