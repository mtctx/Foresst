/*
 *     Foresst: Interactable.kt
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

package dev.mtctx.foresst

import dev.mtctx.foresst.entity.LivingEntity

interface Interactable {
    fun onClick(click: MouseClick, entity: LivingEntity)
}

interface InteractableBlock : Interactable {
    fun onWalkOver(entity: LivingEntity)
    fun onJumpOn(entity: LivingEntity)
    fun onBreak(entity: LivingEntity)
    fun onDrop(entity: LivingEntity)
    fun onPlace(entity: LivingEntity)
    fun onPickUp(entity: LivingEntity)
}

interface InteractableItem : Interactable {
    fun onDrop(entity: LivingEntity)
    fun onPickUp(entity: LivingEntity)
}

interface InteractableEntity : Interactable {
    fun onKill(killer: LivingEntity)
    fun onSpawn()
}

enum class MouseClick(val id: Int) {
    LEFT(0),
    MIDDLE(1),
    RIGHT(2),
    FOURTH(3),
    FIFTH(4),
}