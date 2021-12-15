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
import java.util.*

fun main() {
    val costs = readInput().map { it.toList().map(Char::digitToInt) }.toGrid()

    fun Grid<Int>.calculateCost(start: GridPos, dest: GridPos): Int {
        fun flatIndexOf(pos: GridPos) =
            (pos.y.intValue * width) + pos.x.intValue

        val risks = IntArray(positions.size) { Int.MAX_VALUE }
        risks[flatIndexOf(start)] = 0

        val queue = PriorityQueue<Pair<GridPos, Int>>(compareBy { (_, priority) -> priority })
        queue.add(start to 0)

        while (queue.isNotEmpty()) {
            val (pos, _) = queue.remove()
            val index = flatIndexOf(pos)
            if (pos == dest) return risks[index]

            for (vPos in getAdjacentPositions(pos)) {
                val vIndex = flatIndexOf(vPos)

                val alt = risks[index] + this[vPos]

                if (alt < risks[vIndex]) {
                    risks[vIndex] = alt
                    queue.add(vPos to alt)
                }
            }
        }

        error("Cost calculation aborted unexpectedly")
    }

    fun part1() = costs.calculateCost(start = costs.positions.first(), costs.positions.last())

    fun part2(): Int {
        val actualGrid = Grid(costs.width * 5, costs.height * 5) { pos ->
            val increase = (pos.x.intValue / costs.width) + (pos.y.intValue / costs.height)
            ((costs[pos.x % costs.width, pos.y % costs.height] + increase - 1) % 9) + 1
        }

        return actualGrid.calculateCost(start = actualGrid.positions.first(), actualGrid.positions.last())
    }

    println("Part 1: ${part1()}")
    println("Part 2: ${part2()}")
}