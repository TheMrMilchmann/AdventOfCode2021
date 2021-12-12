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
    val input = buildMap<String, MutableSet<String>> {
        readInput().forEach { line ->
            val (a, b) = line.split("-")
            this.computeIfAbsent(a) { mutableSetOf() }.add(b)
            this.computeIfAbsent(b) { mutableSetOf() }.add(a)
        }
    }

    fun String.countPaths(
        path: List<String> = emptyList(),
        visited: Map<String, Int> = emptyMap(),
        predicate: (node: String, visited: Map<String, Int>) -> Boolean
    ): Int = when (this) {
        "end" -> 1
        else -> {
            @Suppress("NAME_SHADOWING")
            val visited = if (this == lowercase())
                visited + mapOf(this to (if (this in visited) 2 else 1))
            else
                visited

            input[this]!!.filter { it != "start" && predicate(it, visited) }
                .sumOf { it.countPaths(path = path + this, visited = visited, predicate = predicate) }
        }
    }

    println("Part 1: ${"start".countPaths { node, visited -> node !in visited } }")
    println("Part 2: ${"start".countPaths { node, visited -> node !in visited || visited.none { (_, v) -> v == 2 } } }")
}