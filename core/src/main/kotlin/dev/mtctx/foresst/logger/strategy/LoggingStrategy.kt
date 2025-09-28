/*
 *     Foresst: LoggingStrategy.kt
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

package dev.mtctx.foresst.logger.strategy

import dev.mtctx.foresst.logger.ANSI
import dev.mtctx.foresst.logger.LoggerConfig
import dev.mtctx.foresst.logger.LoggerUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
open class LoggingStrategy(
    private val strategyName: String, private val coroutineScope: CoroutineScope, private val mutex: Mutex,
    private val ansiColor: String
) {
    open suspend fun log(
        config: LoggerConfig,
        timestamp: Instant,
        logToConsole: Boolean,
        content: Array<out Any>
    ) {
        val formattedTimestamp = LoggerUtils.getFormattedTime(timestamp)
        val message = generateMessage(config, formattedTimestamp, content)

        try {
            if (logToConsole) println(
                ANSI.translateToANSI(message)
            )
            writeToFile(LoggerUtils.getLogFileForStrategy(config.logsDirectory, strategyName), message)
        } catch (e: IOException) {
            System.err.println("Log write error: " + e.message)
        }
    }

    open fun generateMessage(
        config: LoggerConfig, formattedTimestamp: String,
        content: Array<out Any>
    ): String = config.format(
        formattedTimestamp,
        "$ansiColor$strategyName${ANSI.RESET}",
        config.name,
        content
    )

    @Throws(IOException::class)
    open suspend fun writeToFile(logFilePath: Path, message: String): Unit =
        mutex.withLock {
            withContext(Dispatchers.IO) {
                Files.write(
                    logFilePath,
                    listOf(message),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
                )
            }
        }
}