/*
 *     Foresst: ForesstDispatchers.kt
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

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

object ForesstDispatchers {
    private val gameExecutor = Executors.newSingleThreadExecutor { Thread(it, "Foresst-Game-Thread") }
    val Game: CoroutineDispatcher = gameExecutor.asCoroutineDispatcher()

    private val modsExecutor = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors().coerceAtLeast(2) // keep it balanced
    ) { Thread(it, "Foresst-Mods-Thread") }
    val Mods: CoroutineDispatcher = modsExecutor.asCoroutineDispatcher()

    private val windowExecutor = Executors.newSingleThreadExecutor { Thread(it, "Foresst-WindowAndInput-Thread") }
    val WindowAndInput: CoroutineDispatcher = windowExecutor.asCoroutineDispatcher()

    private val loggerExecutor = Executors.newSingleThreadExecutor { Thread(it, "Foresst-Logger-Thread") }
    val Logger: CoroutineDispatcher = loggerExecutor.asCoroutineDispatcher()

    fun shutdown() {
        windowExecutor.shutdown()
        modsExecutor.shutdown()
        loggerExecutor.shutdown()
        gameExecutor.shutdown()
    }
}