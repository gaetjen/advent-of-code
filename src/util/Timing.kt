package util

import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime

fun timingStatistics(
    maxRuns: Int = 1000,
    minRuns: Int = 20,
    minTimeSpent: Duration = 500.milliseconds,
    block: () -> Any
) {
    try {
        buildList<Duration> {
            while (size < minRuns || reduce { acc, it -> acc + it } < minTimeSpent) {
                add(measureTime { block() })
                if (size >= maxRuns) break
            }
        }.let { durations ->
            val totalDuration = durations.reduce { acc, it -> acc + it }
            val average = totalDuration.times(1.0 / durations.size)
            val standardDeviation = sqrt(durations.sumOf { (it - average).inWholeMicroseconds.toDouble().pow(2) } / durations.size).microseconds
            println(
                "\u001b[36mRuntime: $average, σ: $standardDeviation (${durations.size} runs)\u001b[0m".replace("us", "µs")
            )
        }
    } catch (e: Exception) {
        println("Error while timing: ${e.javaClass.simpleName} ${e.message}")
    }
}
