package y2016

import util.readInput

object Day07 {

    fun part1(input: List<String>): Int {
        return input.count {
            supportTls(it)
        }
    }

    private fun supportTls(ip: String): Boolean {
        val (supernet, hypernet) =  ip.split(Regex("[\\[\\]]"))
            .mapIndexed { index, s -> index to s }
            .partition { it.first % 2 == 0 }
        return supernet.any { hasAbba(it.second) } && hypernet.none { hasAbba(it.second) }
    }

    private fun hasAbba(string: String): Boolean {
        val re = Regex("(.)(.)\\2\\1")
        return re.findAll(string).any {
            it.value.toSet().size == 2
        }
    }

    fun part2(input: List<String>): Int {
        return input.count {
            supportSsl(it)
        }
    }

    private fun supportSsl(ip: String): Boolean {
        val (supernet, hypernet) =  ip.split(Regex("[\\[\\]]"))
            .mapIndexed { index, s -> index to s }
            .partition { it.first % 2 == 0 }
        return hasAba(supernet.map { it.second }, hypernet.map { it.second })
    }

    private fun hasAba(superNet: List<String>, hyperNet: List<String>): Boolean {
        val re = Regex("(.)(?=.\\1)")
        val superAbas = getAbas(superNet, re)
        val hyperBabs = getAbas(hyperNet, re)
        return superAbas.toSet().intersect(hyperBabs.map { it.reversed() }.toSet()).isNotEmpty()
    }

    private fun getAbas(superNet: List<String>, re: Regex) = superNet.flatMap { nets ->
        re.findAll(nets).map { nets.substring(it.range.first, it.range.first + 2) }
    }.filter {
        it.toSet().size == 2
    }
}

fun main() {
    val testInput = """
        abba[mnop]qrst
        abcd[bddb]xyyx
        aaaa[qwer]tyui
        ioxxoj[asdfgh]zxcvbn
        aba[bab]xyz
        xyx[xyx]xyx
        aaa[kek]eke
        zazbzb[bzb]cdb
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day07.part1(testInput))
    println(Day07.part2(testInput))

    println("------Real------")
    val input = readInput("resources/2016/day07")
    println(Day07.part1(input))
    println(Day07.part2(input))
}