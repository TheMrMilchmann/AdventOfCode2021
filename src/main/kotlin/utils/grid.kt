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
package utils

@JvmInline
value class HPos(val intValue: Int) {

    operator fun minus(value: Int): HPos =
        HPos(intValue - value)

    operator fun plus(value: Int): HPos =
        HPos(intValue + value)

}

@JvmInline
value class VPos(val intValue: Int) {

    operator fun minus(value: Int): VPos =
        VPos(intValue - value)

    operator fun plus(value: Int): VPos =
        VPos(intValue + value)

}

data class GridPos(val x: HPos, val y: VPos)

fun <E> List<List<E>>.toGrid(): Grid<E> = Grid(
    grid = flatten(),
    height = size,
    width = map { it.size }.toSet().single()
)

class Grid<E>(
    private val grid: List<E>,
    private val width: Int,
    private val height: Int
) {

    init {
        require(grid.size == width * height)
    }

    val horizontalIndices: List<HPos> get() = (0 until width).map { HPos(it) }
    val verticalIndices: List<VPos> get() = (0 until height).map { VPos(it) }

    operator fun contains(pos: GridPos): Boolean =
        pos.x in horizontalIndices && pos.y in verticalIndices

    operator fun get(pos: GridPos): E =
        this[pos.x, pos.y]

    operator fun get(x: HPos, y: VPos): E {
        return grid[y.intValue * width + x.intValue]
    }

    fun getAdjacentPositions(pos: GridPos, includeDiagonals: Boolean = false): List<GridPos> = buildList {
        pos.copy(x = pos.x - 1).also { if (it in this@Grid) add(it) }
        pos.copy(x = pos.x + 1).also { if (it in this@Grid) add(it) }
        pos.copy(y = pos.y - 1).also { if (it in this@Grid) add(it) }
        pos.copy(y = pos.y + 1).also { if (it in this@Grid) add(it) }

        if (includeDiagonals) {
            pos.copy(x = pos.x - 1, y = pos.y - 1).also { if (it in this@Grid) add(it) }
            pos.copy(x = pos.x - 1, y = pos.y + 1).also { if (it in this@Grid) add(it) }
            pos.copy(x = pos.x + 1, y = pos.y - 1).also { if (it in this@Grid) add(it) }
            pos.copy(x = pos.x + 1, y = pos.y + 1).also { if (it in this@Grid) add(it) }
        }
    }

}