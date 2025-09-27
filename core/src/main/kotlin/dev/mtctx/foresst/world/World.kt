/*
 *     Foresst: World.kt
 *     Copyright (C) 2025 mtctx, kvxd
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.mtctx.foresst.world

import dev.mtctx.foresst.Location
import dev.mtctx.foresst.Outcome
import dev.mtctx.foresst.world.block.Block

open class World(val name: String) {

    init {
        // TODO: Load world
    }

    fun getBlock(location: Location): Outcome<Block> {
        // TODO: Return block from location
    }

    fun placeBlock(block: Block, action: (block: Block, world: World) -> Unit = { _, _ -> }) {
        // TODO: Place block
        action(block, this)
    }

    fun destroyBlock(block: Block, action: (block: Block, world: World) -> Unit = { _, _ -> }) {
        // TODO: Destroy block
        action(block, this)
    }

    fun save() {}

    companion object {
        fun create(name: String): Outcome<World> {
            // TODO: Create world
        }

        fun delete(name: String): Outcome<World> {
            // TODO: Delete world
        }

        fun load(name: String): Outcome<World> {
            // TODO: Load world
        }

        fun getByName(name: String): Outcome<World> {
            // TODO: Load world if not loaded
            // TODO: Return world
        }
    }
}