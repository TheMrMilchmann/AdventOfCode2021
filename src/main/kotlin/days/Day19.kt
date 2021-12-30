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
    data class Vec3(val x: Int, val y: Int, val z: Int) {

        val rolled by lazy { Vec3(x, z, -y) }
        val turned by lazy { Vec3(-y, x, z) }

        /* https://stackoverflow.com/questions/16452383/how-to-get-all-24-rotations-of-a-3-dimensional-array */
        val rotations by lazy {
            var vec = this

            buildList {
                for (cycle in 0..1) {
                    for (step in 0..2) {
                        vec = vec.rolled
                        add(vec)

                        for (i in 0..2) {
                            vec = vec.turned
                            add(vec)
                        }
                    }

                    vec = vec.rolled.turned.rolled
                }
            }
        }

        operator fun minus(other: Vec3) =
            Vec3(x - other.x, y - other.y, z - other.z)

        operator fun plus(other: Vec3) =
            Vec3(x + other.x, y + other.y, z + other.z)

    }

    data class Scanner(val id: Int, val coords: List<Vec3>) {

        val rotations: List<List<Vec3>> by lazy {
            coords.flatMap { it.rotations.mapIndexed { index, vec -> index to vec } }
                .groupBy(keySelector = { it.first }, valueTransform = { it.second })
                .values
                .toList()
        }

        fun findMatchingFor(other: Set<Vec3>): Set<Vec3>? {
            for (rot in rotations) {
                for (source in rot) {
                    for (target in other) {
                        val offset = target - source

                        if (rot.map { it + offset }.count { it in other } >= 12)
                            return rot.map { it + offset }.toHashSet()
                    }
                }
            }

            return null
        }

    }

    fun List<String>.parseInput(): List<Scanner> = buildList {
        var offset = 0

        while (offset < this@parseInput.size) {
            val (scannerID) = """--- scanner (\d+) ---""".toRegex().matchEntire(this@parseInput[offset++])!!.destructured
            val coords = this@parseInput.drop(offset).takeWhile { it.isNotEmpty() }.map { it.split(',').map(String::toInt).let { (x, y, z) -> Vec3(x, y, z) } }

            add(Scanner(scannerID.toInt(), coords))
            offset += (1 + coords.size)
        }
    }

    val input = readInput().parseInput()

    var base = input[0].coords.toSet()
    var scanners = input.drop(1)

    while (scanners.isNotEmpty()) {
        for (scanner in scanners) {
            val matching = scanner.findMatchingFor(base)

            if (matching != null) {
                base = base + matching
                scanners = (scanners - scanner)
                break
            }
        }
    }

    println("Part 1: ${base.toSet().size}")
}