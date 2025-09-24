/*
 *     Foresst: LoggerDSL.kt
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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import java.nio.file.Path
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class LoggerDSL {
    var name: String = "Foresst"
    var logsDirectory: Path = LoggerUtils.logsDir
    var coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob())
    var format: (timestamp: String, strategyName: String, loggerName: String, content: Array<out Any>) -> String =
        { timestamp, strategyName, loggerName, content ->
            "[$timestamp] - $strategyName - $loggerName - ${content.joinToString { it.toString() }}"
        }
    var logChannelSize: Int = Channel.UNLIMITED
    var logChannel: Channel<LogMessage> = Channel(logChannelSize)

    fun build(): Logger = Logger(LoggerConfig(name, logsDirectory, coroutineScope, format))
}

fun createLogger(block: LoggerDSL.() -> Unit): Logger = LoggerDSL().apply(block).build()
fun createLogger() = Logger(LoggerConfig())