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
    val (minX, maxX, minY, maxY) = """target area: x=(\d+)\.\.(\d+), y=(-\d+)\.\.(-\d+)""".toRegex()
        .matchEntire(readInput().single())!!
        .destructured
        .let { (minX, maxX, minY, maxY) ->
            listOf(minX.toInt(), maxX.toInt(), minY.toInt(), maxY.toInt())
        }

    val targetXs = minX..maxX
    val targetYs = minY..maxY

    val hits = sequence {
        (-1_000..1_000).forEach { initialVX ->
            (-1_000..1_000).forEach loop@{ initialVY ->
                var x = 0
                var y = 0

                var vX = initialVX
                var vY = initialVY

                var highest = Int.MIN_VALUE

                while (true) {
                    x += vX
                    y += vY

                    vX -= vX.sign
                    vY -= 1

                    highest = maxOf(y, highest)

                    if (x in targetXs && y in targetYs) {
                        yield(highest)
                        return@loop
                    }

                    if ((x < minX && vX <= 0) || (x > maxX && vX >= 0) || (y < minY && vY <= 0))
                        return@loop
                }
            }
        }
    }

    println("Part 1: ${hits.maxOf { it }}")
    println("Part 2: ${hits.count()}")
}