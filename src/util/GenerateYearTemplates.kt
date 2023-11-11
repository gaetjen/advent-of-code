package util

import java.io.File

fun main() {
    val year = 2016
    val folder = "src/y$year"
    val template = readInput("resources/util/template")
    (1..25).forEach { day ->
        val dayString = String.format("%02d", day)
        val f = File("$folder/Day$dayString.kt")
        f.writeText(
            template.joinToString(separator = "\n") {
                it.replace("\$year", year.toString())
                    .replace("\$day", dayString)
            }
        )
    }
}
