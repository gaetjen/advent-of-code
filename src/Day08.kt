object Day08 {

    fun part1(input: List<String>): Int {
        val heightGrid = input.mapIndexed { rowIdx, row ->
            row.mapIndexed { colIdx, h -> Tree(h, rowIdx, colIdx) }
        }
        return heightGrid
            .flatten()
            .count {
                isVisible(it, heightGrid)
            }
    }

    private fun isVisible(tree: Tree, grid: List<List<Tree>>): Boolean {
        return isVisible(tree.row, getCol(grid, tree.col)) || isVisible(tree.col, getRow(grid, tree.row))
    }

    private fun isVisible(idx: Int, line: List<Tree>): Boolean {
        return line.take(idx).all { it.height < line[idx].height } || line.drop(idx + 1).all { it.height < line[idx].height }
    }

    class Tree(height: Char, val row: Int, val col: Int) {
        val height = height.digitToInt()
    }


    fun part2(input: List<String>): Int {
        val heightGrid = input.mapIndexed { rowIdx, row ->
            row.mapIndexed { colIdx, h -> Tree(h, rowIdx, colIdx) }
        }
        return heightGrid
            .flatten()
            .maxOf { scenicScore(it, heightGrid) }
    }

    private fun scenicScore(tree: Tree, grid: List<List<Tree>>): Int {
        return scenicScore(tree.row, getCol(grid, tree.col)) * scenicScore(tree.col, getRow(grid, tree.row))
    }

    private fun scenicScore(idx: Int, line: List<Tree>): Int {
        val treeHeight = line[idx].height
        val before = line.take(idx).reversed()
        val after = line.drop(idx + 1)
        return viewDistance(before, treeHeight) * viewDistance(after, treeHeight)
    }

    private fun viewDistance(trees: List<Tree>, treeHeight: Int): Int {
        val visible = trees.takeWhile { it.height < treeHeight }.count()
        val extraTree = if (trees.drop(visible).firstOrNull()?.height == treeHeight) 1 else 0
        return visible + extraTree
    }

}


fun main() {
    val testInput = """
    30373
    25512
    65332
    33549
    35390
""".trimIndent().split("\n")
    //listOf<Boolean>().all { true }
    println("all of empty: " + listOf<Boolean>().all { true })
    println(Day08.part1(testInput))
    val input = readInput("resources/day08")
    println(Day08.part1(input))
    println(Day08.part2(testInput))
    println(Day08.part2(input))
}
