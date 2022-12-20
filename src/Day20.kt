object Day20 {
    class Node(
        val value: Long,
        var prev: Node?,
        var next: Node?,
    ) {
        fun move(mod: Int? = null) {
            prev?.next = next
            next?.prev = prev
            var remainingDist = value
            if (mod != null) remainingDist %= (mod - 1) //Math.floorMod(remainingDist, mod.toLong())
            var insertionNode = this.prev
            if (remainingDist > 0) {
                while (remainingDist != 0L) {
                    insertionNode = insertionNode?.next!!
                    remainingDist--
                }
            } else {
                while (remainingDist != 0L) {
                    insertionNode = insertionNode?.prev!!
                    remainingDist++
                }
            }
            this.next = insertionNode?.next
            this.prev = insertionNode
            insertionNode?.next?.prev = this
            insertionNode?.next = this
        }

        fun toList(): List<Long> {
            val start = this
            var it = this.next
            val res = mutableListOf(start.value)
            while (it != start) {
                res.add(it?.value!!)
                it = it.next
            }
            return res
        }
    }

    private fun parse(input: List<String>, key: Long = 1L): List<Node> {
        val nodes = input.map { Node(it.toLong() * key, null, null) }
        nodes.windowed(3).forEach {
            it[1].prev = it[0]
            it[1].next = it[2]
        }
        nodes.first().prev = nodes.last()
        nodes.first().next = nodes[1]
        nodes.last().next = nodes.first()
        nodes.last().prev = nodes[nodes.size - 2]
        return nodes
    }

    fun part1(input: List<String>): Long {
        val file = parse(input)
        if (file.size != file.distinct().size) {
            println("total: " + file.size)
            println("unique: " + file.distinct().size)
        }
        println(file.first().toList())
        file.forEach {
            it.move()
            //println(it.toList())
        }
        val list = file.first().toList()
        val zeroIdx = list.indexOf(0)
        return (1..3).sumOf { list[(zeroIdx + 1000 * it) % list.size] }
    }

    fun part2(input: List<String>): Long {
        val file = parse(input, key = 811589153)
        val ln = file.size
        repeat(10) {
            file.forEach {
                it.move(ln)
            }
        }
        val list = file.first().toList()
        val zeroIdx = list.indexOf(0)
        return (1..3).sumOf { list[(zeroIdx + 1000 * it) % list.size] }
    }
}

fun main() {
    val testInput = """
        1
        2
        -3
        3
        -2
        0
        4
    """.trimIndent().split("\n")
    println("------Tests------")
    println(Day20.part1(testInput))
    println(Day20.part2(testInput))

    println("------Real------")
    val input = readInput("resources/day20")
    println(Day20.part1(input))
    println(Day20.part2(input))
}
