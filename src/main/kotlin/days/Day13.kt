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
import kotlin.math.*

fun main() {
    data class Point(val x: Int, val y: Int)

    abstract class Fold {
        abstract operator fun invoke(point: Point): Point
    }

    class Horizontal(val pos: Int) : Fold() {
        override fun invoke(point: Point) = point.copy(y = pos - abs(point.y - pos))
    }

    class Vertical(val pos: Int) : Fold() {
        override fun invoke(point: Point) = point.copy(x = pos - abs(point.x - pos))
    }

    val points = readInput().takeWhile { it.isNotEmpty() }.map {
        val (x, y) = it.split(",")
        Point(x.toInt(), y.toInt())
    }

    val foldInstructions = readInput().dropWhile { !it.startsWith("fold") }.map {
        val (axis, pos) = """fold along (\w)=(\d+)""".toRegex().matchEntire(it)!!.destructured

        when (axis) {
            "x" -> ::Vertical
            "y" -> ::Horizontal
            else -> error("Unexpected axis: $axis")
        }(pos.toInt())
    }

    fun part1(): Int = points.map { (foldInstructions.first())(it) }.toSet().size

    fun part2(): String = buildString {
        val finalPoints = points.map { point -> foldInstructions.fold(point) { acc, foldInstruction -> foldInstruction(acc) } }

        val minX = finalPoints.minOf(Point::x)
        val minY = finalPoints.minOf(Point::y)
        val maxX = finalPoints.maxOf(Point::x)
        val maxY = finalPoints.maxOf(Point::y)

        return (minY..maxY).joinToString(separator = System.lineSeparator()) { y ->
            (minX..maxX).joinToString(separator = "") { x -> if (Point(x, y) in finalPoints) "█" else " " }
        }
    }

    println("Part 1: ${part1()}")
    println("Part 2: \n${part2()}")
}