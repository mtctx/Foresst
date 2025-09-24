/*
 *     Foresst: LoggerUtils.kt
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

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
object LoggerUtils {
    val TIME_FORMAT = LocalDateTime.Format {
        hour(Padding.ZERO)
        char(':')
        minute(Padding.ZERO)
        char(':')
        second(Padding.ZERO)
        char(':')
        secondFraction(3)
    }

    val DATE_FORMAT = LocalDateTime.Format {
        day(Padding.ZERO)
        char('.')
        monthNumber(Padding.ZERO)
        char('.')
        year(Padding.ZERO)
    }

    fun getFormattedTime(timestamp: Instant = Clock.System.now()): String =
        timestamp.toLocalDateTime(TimeZone.currentSystemDefault()).format(TIME_FORMAT)

    fun getFormattedDate(): String =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).format(DATE_FORMAT)


    val logsDir = Path("logs", getFormattedDate()).toAbsolutePath()

    fun getLogFileForStrategy(strategyName: String): Path {
        return logsDir.resolve("${strategyName.lowercase()}.log")
    }
}