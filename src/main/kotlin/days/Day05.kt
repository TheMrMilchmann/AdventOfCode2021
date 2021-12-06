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
    data class Point(val x: Int, val y: Int)
    data class Segment(val start: Point, val end: Point)

    fun Point.diff(other: Point, selector: (Point) -> Int): IntRange =
        minOf(selector(this), selector(other))..maxOf(selector(this), selector(other))

    val segments = readInput().map { it.split(" -> ") }
        .map { (start, end) ->
            Segment(
                start.split(",").let { (x, y) -> Point(x.toInt(), y.toInt()) },
                end.split(",").let { (x, y) -> Point(x.toInt(), y.toInt()) }
            )
        }

    val grid = Array(segments.maxOf { maxOf(it.start.y, it.end.y) } + 1) {
        IntArray( segments.maxOf { maxOf(it.start.x, it.end.x) } + 1)
    }

    for (segment in segments) {
        if (segment.start.x == segment.end.x) {
            segment.start.diff(segment.end, Point::y).forEach { y ->
                grid[y][segment.start.x]++
            }
        } else if (segment.start.y == segment.end.y) {
            segment.start.diff(segment.end, Point::x).forEach { x ->
                grid[segment.start.y][x]++
            }
        }
    }

    println("Part 1: ${grid.flatMap { it.filter { it > 1 } }.count()}")
}