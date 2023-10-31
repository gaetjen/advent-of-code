package y2015

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.longOrNull
import util.readInput

object Day12 {
    private fun parse(input: String): JsonElement {
        return Json.parseToJsonElement(input)
    }

    fun part1(input: String): Long {
        val parsed = parse(input)
        val primitives = parsed.toPrimitives()
        return primitives.mapNotNull { it.longOrNull }.sum()
    }

    private fun JsonElement.toPrimitives(): List<JsonPrimitive> {
        return when (this) {
            is JsonPrimitive -> listOf(this)
            is JsonArray -> this.map { it.toPrimitives() }.flatten()
            is JsonObject -> this.values.map { it.toPrimitives() }.flatten()
        }
    }

    private fun JsonElement.toPrimitivesNoRed(): List<JsonPrimitive> {
        return when (this) {
            is JsonPrimitive -> listOf(this)
            is JsonArray -> this.map { it.toPrimitivesNoRed() }.flatten()
            is JsonObject -> {
                if (this.values.any { it is JsonPrimitive && it.content == "red" }) {
                    listOf()
                } else {
                    this.values.map { it.toPrimitivesNoRed() }.flatten()
                }
            }
        }
    }

    fun part2(input: String): Long {
        val parsed = parse(input)
        val primitives = parsed.toPrimitivesNoRed()
        return primitives.mapNotNull { it.longOrNull }.sum()
    }
}

fun main() {
    val testInput = """
        [1,{"c":"red","b":2},3]
    """.trimIndent()
    println("------Tests------")
    println(Day12.part1(testInput))
    println(Day12.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2015/day12").first()
    println(Day12.part1(input))
    println(Day12.part2(input))
}
