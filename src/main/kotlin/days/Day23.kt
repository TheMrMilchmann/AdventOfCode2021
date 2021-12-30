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
import java.util.*
import kotlin.math.*

/*
 * I had some weird problems in my initial implementation and looked up similar
 * solutions. It worked but now this just likes a copy of
 * https://github.com/dfings/advent-of-code/blob/main/src/2021/problem_23.main.kts
 */
fun main() {
    data class Vec2(val x: Int, val y: Int) {

        infix fun distanceTo(other: Vec2) =
            (x - other.x).absoluteValue + (y - other.y).absoluteValue

    }

    data class Amphipod(val type: Type, val pos: Vec2) {
        val isInCorrectRoom: Boolean get() = (pos.x == type.roomX)
        val isInHallway: Boolean get() = (pos.y == 0)
    }

    data class State(val amphipods: List<Amphipod>, val totalCost: Int) {
        val isFinal: Boolean get() = amphipods.all(Amphipod::isInCorrectRoom)

        fun move(amphipod: Amphipod, pos: Vec2): State = State(
            amphipods = amphipods.map {
                if (it == amphipod)
                    amphipod.copy(pos = pos)
                else
                    it
            }.sortedBy { it.hashCode() },
            totalCost = totalCost + (amphipod.type.cost * (amphipod.pos distanceTo pos))
        )

    }

    fun State.toPossibleSuccessors(roomSize: Int, hallway: List<Vec2>) = buildList {
        fun Amphipod.isRoomSorted() =
            amphipods.none { it.pos.x == type.roomX && it.type != type }

        fun Amphipod.canMoveThroughHallway(x: Int) =
            (pos.x < x && amphipods.none { it.isInHallway && it.pos.x > pos.x && it.pos.x <= x }) ||
            (pos.x > x && amphipods.none { it.isInHallway && it.pos.x < pos.x && it.pos.x >= x })

        fun firstSlotInRoom(roomX: Int): Vec2 =
            Vec2(roomX, (1..roomSize).takeWhile { y -> amphipods.none { it.pos.x == roomX && it.pos.y == y } }.last())

        for (amphipod in amphipods) {
            if (amphipod.isRoomSorted() && amphipod.isInCorrectRoom)
                continue

            if (!amphipod.isInHallway && amphipods.none { it.pos.x == amphipod.pos.x && it.pos.y < amphipod.pos.y })
                addAll(hallway.filter { amphipod.canMoveThroughHallway(it.x) }.map { move(amphipod, it) })
            else if (amphipod.isInHallway && amphipod.canMoveThroughHallway(amphipod.type.roomX) && amphipod.isRoomSorted())
                add(move(amphipod, firstSlotInRoom(amphipod.type.roomX)))
        }
    }

    fun State.calculateCost(roomSize: Int, hallway: List<Vec2>): Int {
        val visited = mutableSetOf<List<Amphipod>>()
        val queue = PriorityQueue<State>(compareBy { (_, totalCost) -> totalCost })
        queue.add(this)

        while (queue.isNotEmpty()) {
            val state = queue.remove()

            if (state.amphipods in visited)
                continue
            else if (state.isFinal)
                return state.totalCost

            visited += state.amphipods
            state.toPossibleSuccessors(roomSize, hallway).forEach(queue::add)
        }

        error("Cost calculation aborted unexpectedly")
    }

    data class Input(val state: State, val roomSize: Int, val hallway: List<Vec2>)
    val (state, roomSize, hallway) = readInput().let { lines ->
        Input(
            state = State(
                amphipods = lines.drop(1).flatMapIndexed { y, line ->
                    line.mapIndexedNotNull { x, c ->
                        val type = when (c) {
                            'A' -> Type.AMBER
                            'B' -> Type.BRONZE
                            'C' -> Type.COPPER
                            'D' -> Type.DESERT
                            else -> null
                        }

                        type?.let { Amphipod(type, Vec2(x - 1, y)) }
                    }
                }.sortedBy { it.hashCode() },
                totalCost = 0
            ),
            roomSize = lines.size - 3,
            hallway = lines[1].indices
                .drop(0)
                .take(lines[1].length - 2)
                .filter { Type.values().none { type -> type.roomX == it } }
                .map { Vec2(x = it, y = 0) }
        )
    }

    println("Part 1: ${state.calculateCost(roomSize, hallway)}")
}

private enum class Type(val roomX: Int, val cost: Int) {
    AMBER(2, 1),
    BRONZE(4, 10),
    COPPER(6, 100),
    DESERT(8, 1000)
}