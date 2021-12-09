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

import utils.readInput

fun main() {
    data class Point(val x: Int, val y: Int)

    val columns = readInput().map { it.toList().map(Char::digitToInt) }

    val mWidth = columns.first().size
    val mHeight = columns.size

    fun isLowPoint(x: Int, y: Int): Boolean =
        (y - 1 < 0 || columns[y][x] < columns[y - 1][x])
            && (y + 1 >= mHeight || columns[y][x] < columns[y + 1][x])
            && (x - 1 < 0 || columns[y][x] < columns[y][x - 1])
            && (x + 1 >= mWidth || columns[y][x] < columns[y][x + 1])

    val lowPoints = (0 until mHeight).flatMap { y -> (0 until mWidth).mapNotNull { x -> if (isLowPoint(x, y)) Point(x, y) else null } }

    fun Point.basinSize(): Int {
        val visited = mutableSetOf<Point>()

        val queue = ArrayDeque<Point>()
        queue.add(this)

        while (!queue.isEmpty()) {
            val point = queue.removeFirst()
            val (x, y) = point

            if (columns[y][x] < 9 && visited.add(point)) {
                if (y - 1 >= 0) queue.add(Point(x, y - 1))
                if (y + 1 < mHeight) queue.add(Point(x, y + 1))
                if (x - 1 >= 0) queue.add(Point(x - 1, y))
                if (x + 1 < mWidth) queue.add(Point(x + 1, y))
            }
        }

        return visited.size
    }

    println("Part 1: ${lowPoints.sumOf { (x, y) -> columns[y][x] + 1 }}")
    println("Part 2: ${lowPoints.map(Point::basinSize).sortedDescending().take(3).reduce(Int::times)}")
}