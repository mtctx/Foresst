/*
 *     Foresst: Block.kt
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

package dev.mtctx.foresst.world.block

import dev.mtctx.foresst.*
import dev.mtctx.foresst.world.World

data class BlockType(val identifier: Identifier, val drops: List<Identifier>, val texture: String)

open class Block(
    type: BlockType,
    protected open var location: Location,
    protected open var world: String = "world"
) {
    protected open val identifier = type.identifier
    protected open val drops = type.drops
    protected open var texture: String = type.texture
    protected open var onBreakAction: (block: Block, world: World) -> Unit = { _, _ -> }
    protected open var onPlaceAction: (block: Block, world: World) -> Unit = { _, _ -> }
    open val isInteractable: Boolean = false

    open fun texture(): String = texture
    open fun texture(texture: String) = apply { this.texture = texture }

    fun location(): Location = location
    fun location(location: Location) = apply {
        this.location = location
    }

    open fun place(): Outcome<Boolean> {
        return when (val worldOutcome = world()) {
            is Outcome.Success -> {
                place(worldOutcome.value)
                success()
            }

            is Outcome.Failure -> return failure("Couldn't load World or it doesn't exist", outcome = worldOutcome)
        }
    }

    open fun place(world: World) {
        world.placeBlock(this, onBreakAction)
    }

    open fun destroy(): Outcome<Boolean> {
        return when (val worldOutcome = world()) {
            is Outcome.Success -> {
                destroy(worldOutcome.value)
                success()
            }

            is Outcome.Failure -> return failure("Couldn't load World or it doesn't exist", outcome = worldOutcome)
        }
    }

    open fun destroy(world: World) {
        world.destroyBlock(this, onBreakAction)
    }

    open fun onPlace(action: (block: Block, world: World) -> Unit): Block {
        this.onPlaceAction = action
        return this
    }

    open fun onPlace(block: Block = this) {
        when (val worldOutcome = block.world()) {
            is Outcome.Success -> {
                this.onPlaceAction(block, worldOutcome.value)
            }

            else -> {}
        }
    }

    open fun onBreak(action: (block: Block, world: World) -> Unit): Block {
        this.onBreakAction = action
        return this
    }

    open fun onBreak(block: Block = this) {
        when (val worldOutcome = block.world()) {
            is Outcome.Success -> {
                this.onBreakAction(block, worldOutcome.value)
            }

            else -> {}
        }
    }

    fun world(): Outcome<World> {
        return World.getByName(world)
    }

    fun moveToWorld(world: World): Outcome<Block> {
        val destroyOutcome = destroy()
        if (destroyOutcome is Outcome.Failure) return failure(
            "Couldn't load World or it doesn't exist",
            outcome = destroyOutcome
        )
        this.world = world.name
        val placeOutcome = place()
        if (placeOutcome is Outcome.Failure) return failure(
            "Couldn't load World or it doesn't exist",
            outcome = placeOutcome
        )
        return success(this)
    }

    fun moveToWorld(world: String): Outcome<Block> {
        val destroyOutcome = destroy()
        if (destroyOutcome is Outcome.Failure) return failure(
            "Couldn't load World or it doesn't exist",
            outcome = destroyOutcome
        )
        this.world = world
        val placeOutcome = place()
        if (placeOutcome is Outcome.Failure) return failure(
            "Couldn't load World or it doesn't exist",
            outcome = placeOutcome
        )
        return success(this)
    }
}