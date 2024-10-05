package y2017

import util.readInput
import util.timingStatistics
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sqrt

typealias Pos3D = Triple<Long, Long, Long>

data class Particle(
    val position: Pos3D,
    val velocity: Pos3D,
    val acceleration: Pos3D
) {
    fun next(): Particle {
        val newVelocity = velocity + acceleration
        return Particle(
            position + newVelocity,
            newVelocity,
            acceleration
        )
    }

    fun positionAtTime(n: Long): Pos3D {
        val x = coordinateAtTime(n) { this.first }
        val y = coordinateAtTime(n) { this.second }
        val z = coordinateAtTime(n) { this.third }
        return Triple(x, y, z)
    }

    private fun coordinateAtTime(
        n: Long,
        f: Triple<Long, Long, Long>.() -> Long
    ): Long {
        return position.f() + n * velocity.f() + acceleration.f() * n * (n + 1) / 2
    }
}

fun Pos3D.magnitude(): Long = abs(first) + abs(second) + abs(third)

operator fun Pos3D.plus(other: Pos3D): Pos3D {
    return Triple(this.first + other.first, this.second + other.second, this.third + other.third)
}

object Day20 {
    private fun parse(input: List<String>): List<Particle> {
        return input.map {
            val (_, p, v, a) = it.split("=")
            Particle(
                stuffToTriple(p),
                stuffToTriple(v),
                stuffToTriple(a)
            )
        }
    }

    private fun stuffToTriple(string: String): Pos3D {
        val (x, y, z) = string.substringAfter('<').substringBefore('>').split(',').map { it.toLong() }
        return Triple(x, y, z)
    }

    fun part1(input: List<String>): Int {
        val parsed = parse(input)
        return parsed.withIndex().minBy { (_, particle) -> particle.acceleration.magnitude() }.index
    }

    fun part2(input: List<String>): Int {
        val particles = parse(input)
        var iteration = 0
        var leftParticles = particles
        while (iteration < 1_000) {
            iteration++
            val nextParticles = leftParticles
                .map { it.next() }
                .groupBy { it.position }
                .filterValues { it.size == 1 }
                .values.flatten()
            if (nextParticles.size < leftParticles.size) iteration = 0
            leftParticles = nextParticles
        }
        return leftParticles.size
    }

    fun part2Wrong(input: List<String>): Int {
        val particles = parse(input)
        val leftParticles = particles.filter { p1 ->
            particles.all { p2 -> p1 == p2 || collisionStep(p1, p2) == null }
        }
        return leftParticles.size
    }

    private fun collisionStep(
        p1: Particle,
        p2: Particle
    ): Long? {
        val a = (p1.acceleration.first - p2.acceleration.first) / 2.0
        val b = p1.velocity.first - p2.velocity.first + a
        val c = p1.position.first - p2.position.first
        val candidate = if (a != 0.0) {
            (-b + sqrt(b * b - 4 * a * c)) / (2 * a)
        } else if (b != 0.0) {
            -c / b
        } else {
            return null
        }
        if (floor(candidate) != candidate || candidate < 0) return null
        if (candidate == Double.POSITIVE_INFINITY) throw IllegalStateException("candidate == infinity")
        val longCandidate = candidate.toLong()
        return if (p1.positionAtTime(longCandidate) == p2.positionAtTime(longCandidate)) {
            println(longCandidate)
            longCandidate
        } else {
            null
        }
    }
}

fun main() {
    val testInput = """
        p=<3,0,0>, v=<2,0,0>, a=<-1,0,0>
        p=<4,0,0>, v=<0,0,0>, a=<-2,0,0>
    """.trimIndent().split("\n")
    val testInput2 = """
        p=<-6,0,0>, v=<3,0,0>, a=<0,0,0>
        p=<-4,0,0>, v=<2,0,0>, a=<0,0,0>
        p=<-2,0,0>, v=<1,0,0>, a=<0,0,0>
        p=<3,0,0>, v=<-1,0,0>, a=<0,0,0>
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day20.part1(testInput))
    println(Day20.part2(testInput2))

    val p = Particle(
        Triple(0, 0, 0),
        Triple(0, 0, 0),
        Triple(1, 0, 0)
    )
    p.next()
    println("------Real------")
    val input = readInput(2017, 20)
    println("Part 1 result: ${Day20.part1(input)}")
    println("Part 2 result: ${Day20.part2(input)}")
    timingStatistics { Day20.part1(input) }
    timingStatistics { Day20.part2(input) }
}