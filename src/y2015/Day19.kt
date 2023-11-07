package y2015

import util.readInput
import util.split
import y2015.Day19.analysis
import kotlin.math.max

typealias Replacements = Map<String, List<String>>
typealias Molecule = List<String>

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

    private fun oneStepP2(
        startMolecule: Molecule,
        replacements: Map<String, List<Molecule>>
    ): List<Molecule> {
        return startMolecule.flatMapIndexed { idx, element ->
            (replacements[element] ?: listOf()).map {
                startMolecule.safeTake(idx - 1) + it + startMolecule.drop(idx + 1)
            }
        }
    }

    private fun Molecule.safeTake(idx: Int) = this.take(max(0, idx))

    private fun parse2(input: List<String>): Pair<Map<String, String>, String> {
        val replacements = input.dropLast(5).associate {
            val (original, replace) = it.split(" => ")
            replace to original
        }
        return replacements to input.last()
    }

    fun part2(input: List<String>): Int {
        val (replacementsStr, targetStr) = parse(input)
        val target = moleculeToElements(targetStr)
        val replacements = replacementsStr.mapValues { (_, results) ->
            results.map {
                moleculeToElements(it)
            }
        }
        var steps = 1
        var stepMolecules = setOf(listOf("H", "F"), listOf("N", "Al"), listOf("O", "Mg"))
        val alreadyExplored = mutableSetOf<Molecule>()
        alreadyExplored.addAll(stepMolecules)
        while (target !in stepMolecules) {
            println("step: $steps")
            println("molecules: ${stepMolecules.size}")
            println("explored: ${alreadyExplored.size}")
            println("longest: ${alreadyExplored.maxBy { it.size }}")
            stepMolecules = stepMolecules.flatMap {
                oneStepP2(it, replacements)
            }.toSet()
            val intersect = stepMolecules.intersect(alreadyExplored)
            println("duplicates: ${intersect.size}")
            stepMolecules = stepMolecules - intersect
            val new = stepMolecules.size
            println("new molecules: $new")
            alreadyExplored.addAll(stepMolecules)
            stepMolecules = stepMolecules.filter { reachable(it, targetStr) }.toSet()
            println("dead ends: ${new - stepMolecules.size}")
            steps++
        }
        return steps
    }

    private val finals = setOf("C", "Rn", "Ar", "Y")

    private fun reachable(molecule: List<String>, targetStr: String): Boolean {
        val re = molecule.joinToString(separator = "", prefix = "^", postfix = "$") {
            if (it in finals) it else "@"
        }.replace(Regex("@+"), ".+")
        return Regex(re).matchEntire(targetStr) != null
    }

    fun analysis(input: List<String>) {
        val (replacements, target) = parse(input)
        val validKeys = mutableSetOf("e")
        var newKeys = setOf("e")
        repeat(3) {
            newKeys = newKeys.flatMap { key -> (replacements[key] ?: listOf()).flatMap { moleculeToElements(it) } }.toSet()
            validKeys.addAll(newKeys)
            println("new: $newKeys")
            println(validKeys)
        }
        println("actual keys: ${validKeys.intersect(replacements.keys)}")
        println("dead ends: ${validKeys - replacements.keys}")

        val (inverse, _) = parse2(input)
        println("original keys: ${inverse.keys.size}")
        val unreachable = inverse.filterKeys {
            it.contains(Regex("Rn|Ar|Y|C[^a]]"))
        }
        println("contains dead end: ${unreachable.size}")
        println(unreachable)

        /*replacements.forEach { (key, values) ->
            //println("reachable from $key")
            moleculeToElements(values.joinToString("")).toSet().forEach {
                println("    $key --> $it")
            }
        }*/

        val foo = replacements.values.flatten().map { mol ->
            val els = moleculeToElements(mol)
            els to finals.any { it in els }
        }
        val (dead, notDead) = foo.partition { it.second }
        println("partition first:")
        dead.forEach {
            val fResults = it.first.filter { it in finals}
            println("${it.first.size} $fResults, ${fResults.size}")
        }
        println("partition second:")
        notDead.forEach {
            println(it.first.size)
        }

        val targetElements = moleculeToElements(target)
        println("target length: ${targetElements.size}")
        println("target dead ends: ${targetElements.filter { it in finals }.size}")
        println("target only dead ends: ${targetElements.filter { it in finals }}")
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
    //println(Day19.part2(input))
}

/**
 * total length: 274
 * Only dead ends:
[C, Rn, Rn, Rn, Ar, Ar, Rn, Ar, Rn, Ar, Rn, Ar, Rn, Rn, Ar, Ar, Rn, Rn, Ar, Y, Rn, Y, Ar, Ar, Rn, Ar, Rn, Y, Rn, Ar, Ar, Rn, Rn, Ar, Y, Ar, Rn, Ar, Rn, Y, Ar, Rn, Ar, Rn, Y, Ar, Rn, Ar, Rn, Ar, Rn, Ar, Rn, Rn, Y, Ar, Ar, Ar, Rn, Rn, Ar, Ar, Rn, Ar, Rn, Ar, Rn, Y, Ar, Rn, Ar]
 * Rn / Ar: 31
 * Y: 8
 * C: 1
 *
 * 31 replacements had at least +3
 * of those, at least an additional 6 had an additional +2 for Y
 *
 * option 1:
 *      C was added without Y → two additional +2 for .Rn.Y.Ar
 * option 2:
 *      C was added with one Y → one additional +2 for CRN.Y.AR, one +2 for .Rn.Y.Ar
 * option 3
 *      C was added with two Ys → one additional +4 for CRnFYFYFAr
 * C is already accounted for in all cases as it is included in the +3 for .Rn.Ar
 * C always gets added together with Rn/Ar, so it does not add a replacement.
 * The 2 additional Ys always add +4 to the length.
 *
 * 31 .Rn.Ar replacements add length 31 * 3 = 93
 * with 8 Ys (+16): 109
 * 274 - 109 + 31 = 165 + 31 = 196
 * subtract one, because at step zero we have length 1 → 195
 */