package y2015

import util.readInput
import util.split
import y2015.Day19.analysis

typealias Replacements = Map<String, List<String>>

object Day19 {
    private fun parse(input: List<String>): Pair<Replacements, String> {
        val replacements = input.dropLast(2).map {
            val (original, replace) = it.split(" => ")
            original to replace
        }.groupBy {
            it.first
        }.mapValues { (_, v) -> v.map { it.second } }
        return replacements to input.last()
    }

    fun part1(input: List<String>): Int {
        val (replacements, start) = parse(input)
        val elements = moleculeToElements(start)
        println(elements)
        val results = oneStepReplacements(elements, replacements)
        return results.size
    }

    private fun moleculeToElements(start: String) = start.toList().split(matchInPost = true) {
        it.isUpperCase()
    }.drop(1).map {
        it.joinToString(separator = "")
    }

    private fun oneStepReplacements(
        elements: List<String>,
        replacements: Replacements
    ) = elements.flatMapIndexed { idx, element ->
        val tmp = elements.toMutableList()
        (replacements[element] ?: listOf()).map {
            tmp[idx] = it
            tmp.joinToString(separator = "")
        }
    }.toSet()


    private fun parse2(input: List<String>): Pair<Map<String, String>, String> {
        val replacements = input.dropLast(5).associate {
            val (original, replace) = it.split(" => ")
            replace to original
        }
        return replacements to input.last()
    }

    private val unreachableKeys = ("ThRnFAr, TiRnFAr, PRnFAr, SiRnFYFAr, SiRnMgAr, CRnAlAr, CRnFYFYFAr, CRnFYMgAr, CRnMgYFAr, NRnFYFAr, " +
            "NRnMgAr, ORnFAr, CRnFAr, CRnFYFAr, CRnMgAr, NRnFAr, SiRnFAr").split(", ").toSet()

    fun part2(input: List<String>): Int {
        val (allReplacements, target) = parse2(input)
        val replacements = allReplacements.filter { it.key !in  unreachableKeys}
        //val replacements = allReplacements
        val lastSteps = setOf("HF, NAl", "OMg")
        var steps = 0
        var stepMolecules = setOf(target)
        val analyzedMolecules = mutableSetOf(target)
        while (lastSteps.intersect(stepMolecules).isEmpty()) {
            stepMolecules = previousMolecules(stepMolecules, replacements) - analyzedMolecules
            analyzedMolecules.addAll(stepMolecules)
            steps++
            println("step: $steps")
            println("analyzed: ${analyzedMolecules.size}")
            println("current candidates: ${stepMolecules.size}")
            println("dead ends: ${stepMolecules.count { it.contains("HF") || it.contains("Nal") || it.contains("OMg") }}")
            println("shortest: ${stepMolecules.minOf { it.length }}")
        }
        return steps + 1
    }

    private fun previousMolecules(stepMolecules: Set<String>, replacements: Map<String, String>): Set<String> {
        return replacements.flatMap { (orig, repl) ->
            stepMolecules.flatMap {
                substituteAll(it, orig, repl)
            }
        }.toSet()
    }

    private fun substituteAll(str: String, orig: String, repl: String): List<String> {
        val regex = Regex(orig)
        return regex.findAll(str).map {
            str.replaceRange(
                it.range, repl
            )
        }.toList()
    }

    fun analysis(input: List<String>) {
        val (replacements, _) = parse(input)
        val validKeys = mutableSetOf("e")
        var newKeys = setOf("e")
        repeat(3) {
            newKeys = newKeys.flatMap { key -> (replacements[key] ?: listOf()).flatMap{ moleculeToElements(it) } }.toSet()
            validKeys.addAll(newKeys)
            println("new: $newKeys")
            println(validKeys)
        }
        println("actual keys: ${validKeys.intersect(replacements.keys)}")
        println("dead ends: ${validKeys - replacements.keys}")

        val (inverse, _) = parse2(input)
        println("original keys: ${inverse.keys.size}")
        val unreachable = inverse.filterKeys {
            it.contains(Regex("Rn|Ar|Y|(C^a)"))
        }
        println("contains dead end: ${unreachable.size}")
        println(unreachable)
    }
}

fun main() {
    val testInput = """
        H => HO
        H => OH
        O => HH
        e => H
        e => O
        e => X
        
        HOHOHO
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day19.part1(testInput))
    //println(Day19.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2015/day19")
    println(Day19.part1(input))
    analysis(input)
    println(Day19.part2(input))
}
