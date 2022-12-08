object Day08 {
    private fun transpose(matrix: List<List<Int>>): List<List<Int>> {
        return List(matrix.first().size) { stackIdx ->
            List(matrix.size) { rowIdx ->
                matrix[rowIdx][stackIdx]
            }
        }
    }

    fun part1(input: List<String>): Int {
        val heightGrid = input.map { row ->
            row.map { it.toString().toInt() }
        }
        val transposed = transpose(heightGrid)
        val visible = heightGrid.mapIndexed { rowIdx, row ->
            row.mapIndexed { colIdx, height ->
                val fromSide = row.take(colIdx).all { it < height } ||
                        row.drop(colIdx + 1).all { it < height }
                val col = transposed[colIdx]
                val fromNS = col.take(rowIdx).all { it < height } ||
                        col.drop(rowIdx + 1).all { it < height }
                fromSide || fromNS
            }
        }.flatten()
        return visible.count { it }
    }

    fun part2(input: List<String>): Int {
        val heightGrid = input.map { row ->
            row.map { it.toString().toInt() }
        }
        val transposed = transpose(heightGrid)
        val scores = heightGrid.mapIndexed { rowIdx, row ->
            row.mapIndexed { colIdx, height ->
                val col = transposed[colIdx]
                var left = 0
                for (h in row.take(colIdx).reversed()) {
                    left++
                    if (h >= height) break
                }
                var right = 0
                for (h in row.drop(colIdx + 1)) {
                    right++
                    if (h >= height) break
                }
                var top = 0
                for (h in col.take(rowIdx).reversed()) {
                    top++
                    if (h >= height) break
                }
                var bottom = 0
                for (h in col.drop(rowIdx + 1)) {
                    bottom++
                    if (h >= height) break
                }
                left * right * bottom * top
            }
        }.flatten()
        return scores.max()
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
