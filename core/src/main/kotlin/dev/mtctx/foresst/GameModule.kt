/*
 *     Foresst: GameModule.kt
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

import kotlinx.coroutines.CoroutineScope

abstract class GameModule() {
    abstract suspend fun start(coroutineScope: CoroutineScope)
    abstract suspend fun stop(coroutineScope: CoroutineScope)
}

fun gameModule(
    startLambda: suspend (coroutineScope: CoroutineScope) -> Unit,
    stopLambda: suspend (coroutineScope: CoroutineScope) -> Unit
) = object : GameModule() {
    override suspend fun start(coroutineScope: CoroutineScope) = startLambda(coroutineScope)
    override suspend fun stop(coroutineScope: CoroutineScope) = stopLambda(coroutineScope)
}