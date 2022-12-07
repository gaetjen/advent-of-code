object Day07 {
    data class File(
        val size: Long?,
        val parent: File? = null,
        val children: MutableList<File> = mutableListOf(),
        val name: String
    ) {
        fun totalSize(): Long {
            if (children.size == 0) {
                return size ?: 0
            }
            return children.sumOf { it.totalSize() }
        }
    }

    fun part1(input: List<String>): Long {
        val allDirs = dirsFromInput(input)
        println("directory sizes: " + allDirs.map { it.totalSize() })
        println("all directories have zero size: " + allDirs.all { it.size == null })
        println("number directories: " + allDirs.size)
        println("number lines starting with \"dir\": " + input.filter { it.startsWith("dir ") }.size)
        return allDirs.map { it.totalSize() }.filter { it <= 100000 }.sum()
    }

    private fun dirsFromInput(input: List<String>): MutableList<File> {
        val root = File(null, name = "")
        var currentDir = root
        val allDirs = mutableListOf(root)
        var idx = 0
        while (idx < input.size) {
            when (input[idx]) {
                "$ cd .." -> {
                    currentDir = currentDir.parent!!
                    idx++
                }

                "$ ls" -> idx = processLs(input, idx, allDirs, currentDir)
                else -> { // $ cd <dir>
                    currentDir = currentDir.children.first { it.name == input[idx].split(" ").last() }
                    idx++
                }
            }
        }
        return allDirs
    }

    private fun processLs(input: List<String>, idx: Int, allDirs: MutableList<File>, thisDir: File): Int {
        var rtnIdx = idx + 1
        while (rtnIdx < input.size && input[rtnIdx][0] != '$') {
            val (p1, p2) = input[rtnIdx].split(" ")
            when (p1) {
                "dir" -> {
                    val newDir = File(null, thisDir, name = p2)
                    allDirs.add(newDir)
                    thisDir.children.add(newDir)
                }

                else -> {
                    val newFile = File(p1.toLong(), name = p2)
                    thisDir.children.add(newFile)
                }
            }
            rtnIdx++
        }
        return rtnIdx
    }

    fun part2(input: List<String>): Long {
        val allDirs = dirsFromInput(input)
        val totalMem = allDirs[0].totalSize()
        val freeSpace = 70000000 - totalMem
        println("free: $freeSpace")
        val toFree = 30000000 - freeSpace
        println("need to free $toFree")
        return allDirs
            .filter { it.totalSize() >= toFree }
            .minBy { it.totalSize() }
            .totalSize()
    }
}

val testInput = """
    ${'$'} cd /
    ${'$'} ls
    dir a
    14848514 b.txt
    8504156 c.dat
    dir d
    ${'$'} cd a
    ${'$'} ls
    dir e
    29116 f
    2557 g
    62596 h.lst
    ${'$'} cd e
    ${'$'} ls
    584 i
    ${'$'} cd ..
    ${'$'} cd ..
    ${'$'} cd d
    ${'$'} ls
    4060174 j
    8033020 d.log
    5626152 d.ext
    7214296 k
""".trimIndent()
fun main() {
    println(Day07.part1(testInput.split("\n").drop(1)))
    // wrong: 1081027
    val input = readInput("resources/day07")
    println(Day07.part1(input.drop(1)))
    println(Day07.part2(input.drop(1)))
}
