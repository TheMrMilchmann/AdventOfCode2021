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
    data class Cuboid(
        val minX: Int, val maxX: Int,
        val minY: Int, val maxY: Int,
        val minZ: Int, val maxZ: Int
    )

    data class BootStep(val cuboid: Cuboid, val state: Boolean)

    val pattern = """x=(-?\d+)\.\.(-?\d+),y=(-?\d+)\.\.(-?\d+),z=(-?\d+)\.\.(-?\d+)""".toRegex()

    val input = readInput().map { line ->
        val (state, cuboid) = line.split(" ")
        val (minX, maxX, minY, maxY, minZ, maxZ) = pattern.matchEntire(cuboid)?.destructured ?: error("Could not parse: $cuboid")

        BootStep(
            state = state == "on",
            cuboid = Cuboid(minX.toInt(), maxX.toInt() + 1, minY.toInt(), maxY.toInt() + 1, minZ.toInt(), maxZ.toInt() + 1)
        )
    }

    val xs = input.flatMap { listOf(it.cuboid.minX, it.cuboid.maxX) }.distinct().sorted()
    val ys = input.flatMap { listOf(it.cuboid.minY, it.cuboid.maxY) }.distinct().sorted()
    val zs = input.flatMap { listOf(it.cuboid.minZ, it.cuboid.maxZ) }.distinct().sorted()

    val cXs = xs.withIndex().associateBy({ it.value }, { it.index })
    val cYs = ys.withIndex().associateBy({ it.value }, { it.index })
    val cZs = zs.withIndex().associateBy({ it.value }, { it.index })

    val box = Array(xs.size) {
        Array(ys.size) {
            BooleanArray(zs.size)
        }
    }

    input.forEach { step ->
        for (cX in cXs[step.cuboid.minX]!! until cXs[step.cuboid.maxX]!!) {
            for (cY in cYs[step.cuboid.minY]!! until cYs[step.cuboid.maxY]!!) {
                for (cZ in cZs[step.cuboid.minZ]!! until cZs[step.cuboid.maxZ]!!) {
                    box[cX][cY][cZ] = step.state
                }
            }
        }
    }

    fun part1(): Long {
        var res = 0L

        for (x in 0 until xs.size - 1) {
            for (y in 0 until ys.size - 1) {
                for (z in 0 until zs.size - 1) {
                    if (box[x][y][z] && -50 <= xs[x] && xs[x] <= 50 && -50 <= ys[y] && ys[y] <= 50 && -50 <= zs[z] && zs[z] <= 50)
                        res += (xs[x + 1] - xs[x]).toLong() * (ys[y + 1] - ys[y]) * (zs[z + 1] - zs[z])
                }
            }
        }

        return res
    }

    fun part2(): Long {
        var res = 0L

        for (x in 0 until xs.size - 1) {
            for (y in 0 until ys.size - 1) {
                for (z in 0 until zs.size - 1) {
                    if (box[x][y][z])
                        res += (xs[x + 1] - xs[x]).toLong() * (ys[y + 1] - ys[y]) * (zs[z + 1] - zs[z])
                }
            }
        }

        return res
    }

    println("Part 1: ${part1()}")
    println("Part 2: ${part2()}")
}