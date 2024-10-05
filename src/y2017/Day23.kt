package y2017

import util.readInput
import util.timingStatistics
import y2017.Day23.program

object Day23 {
    fun part1(input: List<String>): Number {
        val parsed = Day18.parse(input)
        val machine = DuetMachine(parsed, DuetMachineState())
        return sequence {
            with(machine) {
                yield(run().mulCounter)
            }
        }.last()
    }
    fun program(log: Boolean): Long {
        /**
         * INITIALIZATION
         * set b 84
         * set c b
         * jnz a 2
         * jnz 1 5
         * mul b 100
         * sub b -100000
         * set c b
         * sub c -17000
         */
        var b = 108_400L
        val c = 125_400L
        var d: Long
        var h = 0L

        while (true) {
            var f = true // A: set f 1
            d = 2 // set d 2
            do { // B: set e 2
                if (b % d == 0L) {
                    /**
                     * C: set g d
                     * mul g e
                     * sub g b
                     * jnz g 2
                     * set f 0
                     * sub e -1
                     * set g e
                     * sub g b
                     * jnz g -8 GOTO C
                     */
                    f = false
                }
                d++ // sub d -1
                /**
                 * set g d
                 * sub g b
                 * jnz g -13 GOTO B
                 */
            } while (d != b)
            /**
             * jnz f 2
             * sub h -1
             */
            if (!f) {
                if (log) println("incrementing h: $h, b=$b, c=$c, d=$d")
                h++
            } else {
                if (log) println("skipped incrementing h: $h, b=$b, c=$c, d=$d")
            }
            /**
             * set g b
             * sub g c
             * jnz g 2
             * jnz 1 3 BREAK
             */
            if (b == c) {
                break
            }
            b += 17 // sub b -17
        } //jnz 1 -23 GOTO A
        return h
    }
}

fun main() {
    println("------Real------")
    val input = readInput(2017, 23)
    println("Part 1 result: ${Day23.part1(input)}")
    println("Part 2 result: ${program(false)}")
    timingStatistics { Day23.part1(input) }
    timingStatistics { program(false) }
}
