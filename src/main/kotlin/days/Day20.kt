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
    data class GridWithDefault<E>(val grid: Grid<E>, val default: E)

    val (algorithm, image) = readInput().let { lines ->
        lines.first() to GridWithDefault(lines.drop(2).map { it.toList().map { it == '#' } }.toGrid(), false)
    }

    val images: Sequence<GridWithDefault<Boolean>> = generateSequence(image) { (grid, default) ->
        GridWithDefault(
            Grid(grid.width + 2, grid.height + 2) { (destX, destY) ->
                val x = destX - 1
                val y = destY - 1

                val index = buildString {
                    fun at(x: HPos, y: VPos): String = if (grid.getOrDefault(x, y, default)) "1" else "0"

                    append(at(x - 1, y - 1))
                    append(at(x, y - 1))
                    append(at(x + 1, y - 1))

                    append(at(x - 1, y))
                    append(at(x, y))
                    append(at(x + 1, y))

                    append(at(x - 1, y + 1))
                    append(at(x, y + 1))
                    append(at(x + 1, y + 1))
                }.toInt(radix = 2)

                algorithm[index] == '#'
            },
            default = (!default && algorithm.first() == '#') || (default && algorithm.last() == '#')
        )
    }

    println("Part 1: ${images.drop(2).first().grid.count { it }}")
    println("Part 2: ${images.drop(50).first().grid.count { it }}")
}