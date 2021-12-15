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
    val gridPositions = costs.positions

    fun calculateCost(start: Int, dest: Int): Int {
        val risks = IntArray(gridPositions.size) { Int.MAX_VALUE }
        risks[start] = 0

        val queue = PriorityQueue<Pair<Int, Int>>(compareBy { (_, cost) -> cost })
        queue.add(0 to 0)

        while (queue.isNotEmpty()) {
            val (index, _) = queue.poll()
            if (index == dest) return risks[index]

            val pos = gridPositions[index]

            for (v in costs.getAdjacentPositions(pos).map(gridPositions::indexOf)) {
                val alt = risks[index] + costs[gridPositions[v]]

                if (alt < risks[v]) {
                    risks[v] = alt
                    queue.add(v to alt)
                }
            }
        }

        error("Cost calculation aborted unexpectedly")
    }

    println("Part 1: ${calculateCost(start = 0, dest = gridPositions.lastIndex)}")
}