/*
 *     Foresst: Logger.kt
 *     Copyright (C) 2025 mtctx
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.mtctx.foresst.logger

import dev.mtctx.foresst.logger.strategy.LoggingStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.sync.Mutex
import java.nio.file.Files
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

suspend fun main() {
    val logger = logger()
    logger.info("Hello, World!")
}

class Logger(private val config: LoggerConfig) {
    private var name: String
    var coroutineScope: CoroutineScope
    var mutex = Mutex()
    private val defaultLoggingStrategies: DefaultLoggingStrategies

    init {
        Files.createDirectories(config.logsDirectory)
        name = config.name
        coroutineScope = config.coroutineScope
        defaultLoggingStrategies = DefaultLoggingStrategies(coroutineScope, mutex)
    }

    fun name(name: String) {
        this.name = name
    }

    @OptIn(ExperimentalTime::class)
    suspend fun log(strategy: LoggingStrategy, logToConsole: Boolean, vararg content: Any) =
        strategy.log(
            config,
            Clock.System.now(),
            logToConsole,
            *content
        )

    suspend fun debug(vararg content: Any, logToConsole: Boolean = true) =
        log(defaultLoggingStrategies.debugStrategy, logToConsole, *content)

    suspend fun error(vararg content: Any, logToConsole: Boolean = true) =
        log(defaultLoggingStrategies.errorStrategy, logToConsole, *content)

    suspend fun fatal(vararg content: Any, logToConsole: Boolean = true) =
        log(defaultLoggingStrategies.fatalStrategy, logToConsole, *content)

    suspend fun info(vararg content: Any, logToConsole: Boolean = true) =
        log(defaultLoggingStrategies.infoStrategy, logToConsole, *content)

    suspend fun warn(vararg content: Any, logToConsole: Boolean = true) =
        log(defaultLoggingStrategies.warnStrategy, logToConsole, *content)
}