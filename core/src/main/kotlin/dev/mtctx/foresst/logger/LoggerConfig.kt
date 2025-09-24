/*
 *     Foresst: LoggerConfig.kt
 *     Copyright (C) 2025 mtctx, kvxd
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
data class LoggerConfig(
    val name: String = "Foresst",
    val logsDirectory: Path = LoggerUtils.logsDir,
    val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob()),
    val format: (timestamp: String, strategyName: String, loggerName: String, content: Array<out Any>) -> String = { timestamp, strategyName, loggerName, content ->
        "[$timestamp] - $strategyName - $loggerName - ${content.joinToString { it.toString() }}"
    },
    val logChannelSize: Int = Channel.UNLIMITED,
    val logChannel: Channel<LogMessage> = Channel(logChannelSize),
)