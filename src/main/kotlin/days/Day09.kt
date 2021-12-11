/*
 * Copyright (c) 2021 Leon Linhart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package days

import utils.*

fun main() {
    val input = readInput().map { it.toList().map(Char::digitToInt) }.toGrid()

    fun isLowPoint(x: HPos, y: VPos): Boolean =
        input.getAdjacentPositions(GridPos(x, y)).all { input[x, y] < input[it] }

    val lowPoints = input.horizontalIndices.flatMap { x -> input.verticalIndices.mapNotNull { y -> if (isLowPoint(x, y)) GridPos(x, y) else null } }

    fun GridPos.basinSize(): Int {
        val visited = mutableSetOf<GridPos>()

        val queue = ArrayDeque<GridPos>()
        queue.add(this)

        while (!queue.isEmpty()) {
            val pos = queue.removeFirst()

            if (input[pos] < 9 && visited.add(pos)) {
                queue.addAll(input.getAdjacentPositions(pos))
            }
        }

        return visited.size
    }

    println("Part 1: ${lowPoints.sumOf { (x, y) -> input[x, y] + 1 }}")
    println("Part 2: ${lowPoints.map(GridPos::basinSize).sortedDescending().take(3).reduce(Int::times)}")
}