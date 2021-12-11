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

    data class Data(val grid: Grid<Int>, val flashes: Int = 0, val step: Int = 0)

    fun Data.computeStep(): Data {
        val energy = grid.toMap().mapValues { (_, v) -> v + 1 }.toMutableMap()
        var numFlashes = 0

        val queue = ArrayDeque(energy.filterValues { it == 10 }.keys)
        numFlashes += queue.size

        while (!queue.isEmpty()) {
            val pos = queue.removeFirst()

            for (adjacentPos in grid.getAdjacentPositions(pos, includeDiagonals = true)) {
                if (energy.put(adjacentPos, minOf(energy[adjacentPos]!! + 1, 10)) != 10 && energy[adjacentPos] == 10) {
                    numFlashes++
                    queue.add(adjacentPos)
                }
            }
        }

        return Data(
            grid = grid.map { pos, _ -> energy[pos]!! % 10 },
            flashes = numFlashes,
            step = step + 1
        )
    }

    val data = Data(input)

    println("Part 1: ${generateSequence(data) { it.computeStep() }.take(100).map { it.flashes }.sum()}")
    println("Part 2: ${generateSequence(data) { it.computeStep() }.dropWhile { it.flashes != it.grid.size }.first().step}")
}