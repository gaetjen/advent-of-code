package util

import kotlin.time.measureTime

fun measuredTime(block: () -> Any) {
    val result: String
    measureTime {
        result = block().toString()
    }.also {
        println("Time: $it")
        println("Result: $result")
    }
}