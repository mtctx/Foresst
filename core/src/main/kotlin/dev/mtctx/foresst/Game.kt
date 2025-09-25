/*
 *     Foresst: Game.kt
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

import dev.mtctx.foresst.logger.UseSynchronousFunctionsWithCaution
import dev.mtctx.foresst.logger.createLogger
import kotlinx.coroutines.*

@OptIn(UseSynchronousFunctionsWithCaution::class)
open class Game(private vararg val modules: GameModule = emptyArray()) {
    private var shouldStop = false
    private var hasStarted = false
    private var cleanedUp = false
    private val logger = createLogger()
    private val coroutineScope = CoroutineScope(ForesstDispatchers.Game + SupervisorJob())
    private lateinit var gameJob: Job

    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            if (cleanedUp) return@Thread
            runBlocking { shutdown() }
        })
    }

    fun start() {
        if (hasStarted) logger.errorSync("Game has already been started.")
        shouldStop = false
        hasStarted = true

        gameJob = coroutineScope.launch {
            modules.forEach { it.start(coroutineScope) }

            logger.info("Game started.")
            while (!shouldStop) {
                delay(1000)
            }

            shutdown()
        }
    }

    fun stop() {
        shouldStop = true
        hasStarted = false
        gameJob.cancel()
        cleanedUp = true
    }

    suspend fun shutdown() {
        cleanedUp = true
        stop()
        modules.reversed().forEach { it.stop(coroutineScope) }
        ForesstDispatchers.shutdown()
    }
}

class GameBuilder {
    private val modules = mutableListOf<GameModule>()

    fun module(module: GameModule) = apply { modules += module }
    fun modules(vararg modules: GameModule) = apply { this.modules += modules }
    fun module(
        startLambda: suspend (coroutineScope: CoroutineScope) -> Unit,
        stopLambda: suspend (coroutineScope: CoroutineScope) -> Unit
    ) = apply { modules += gameModule(startLambda, stopLambda) }

    fun build() = Game(*modules.toTypedArray())
}

fun createGame(builder: GameBuilder.() -> Unit): Game = GameBuilder().apply(builder).build()

