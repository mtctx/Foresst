/*
 *     Foresst: Logger.kt
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

import dev.mtctx.foresst.logger.strategy.LoggingStrategy
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ChannelResult
import kotlinx.coroutines.sync.Mutex
import java.nio.file.Files
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(UseSynchronousFunctionsWithCaution::class)
suspend fun main() {
    val logger = createLogger()
    logger.info("Hello, World!")

    logger.waitForLogChannelToClose()
}

@OptIn(ExperimentalTime::class)
class Logger(private val config: LoggerConfig) {
    private var name: String
    var coroutineScope: CoroutineScope
    var mutex = Mutex()
    private val defaultLoggingStrategies: DefaultLoggingStrategies
    private val logChannel: Channel<LogMessage>
    private val logChannelJob: Job

    init {
        Files.createDirectories(config.logsDirectory)
        name = config.name
        coroutineScope = config.coroutineScope
        defaultLoggingStrategies = DefaultLoggingStrategies(coroutineScope, mutex)
        logChannel = config.logChannel
        logChannelJob = startListeningForMessages()
    }

    private fun startListeningForMessages() = coroutineScope.launch {
        for (message in logChannel) {
            message.strategy.log(
                config,
                message.timestamp,
                message.logToConsole,
                message.content
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun waitForLogChannelToClose() = runBlocking {
        logChannel.close()
        logChannelJob.join()
    }

    fun name(name: String) {
        this.name = name
    }

    suspend fun log(logMessage: LogMessage) = logChannel.send(logMessage)
    suspend fun log(strategy: LoggingStrategy, logToConsole: Boolean, vararg content: Any) = log(
        LogMessage(
            arrayOf(*content),
            logToConsole,
            Clock.System.now(),
            strategy
        )
    )

    fun logSync(logMessage: LogMessage): ChannelResult<Unit> = logChannel.trySend(logMessage)
    fun logSync(strategy: LoggingStrategy, logToConsole: Boolean, timestamp: Instant, vararg content: Any) = logSync(
        LogMessage(
            arrayOf(*content),
            logToConsole,
            timestamp,
            strategy
        )
    )

    suspend fun debug(vararg content: Any, logToConsole: Boolean = true) =
        log(LogMessage(content, logToConsole, Clock.System.now(), defaultLoggingStrategies.debugStrategy))

    @UseSynchronousFunctionsWithCaution
    fun debugSync(vararg content: Any, logToConsole: Boolean = true): ChannelResult<Unit> =
        logSync(LogMessage(content, logToConsole, Clock.System.now(), defaultLoggingStrategies.debugStrategy))

    suspend fun error(vararg content: Any, logToConsole: Boolean = true) =
        log(LogMessage(content, logToConsole, Clock.System.now(), defaultLoggingStrategies.errorStrategy))

    @UseSynchronousFunctionsWithCaution
    fun errorSync(vararg content: Any, logToConsole: Boolean = true): ChannelResult<Unit> =
        logSync(LogMessage(content, logToConsole, Clock.System.now(), defaultLoggingStrategies.errorStrategy))

    suspend fun fatal(vararg content: Any, logToConsole: Boolean = true) =
        log(LogMessage(content, logToConsole, Clock.System.now(), defaultLoggingStrategies.fatalStrategy))

    @UseSynchronousFunctionsWithCaution
    fun fatalSync(vararg content: Any, logToConsole: Boolean = true): ChannelResult<Unit> =
        logSync(LogMessage(content, logToConsole, Clock.System.now(), defaultLoggingStrategies.fatalStrategy))

    suspend fun info(vararg content: Any, logToConsole: Boolean = true) =
        log(LogMessage(content, logToConsole, Clock.System.now(), defaultLoggingStrategies.infoStrategy))

    @UseSynchronousFunctionsWithCaution
    fun infoSync(vararg content: Any, logToConsole: Boolean = true): ChannelResult<Unit> =
        logSync(LogMessage(content, logToConsole, Clock.System.now(), defaultLoggingStrategies.infoStrategy))

    suspend fun warn(vararg content: Any, logToConsole: Boolean = true) =
        log(LogMessage(content, logToConsole, Clock.System.now(), defaultLoggingStrategies.warnStrategy))

    @UseSynchronousFunctionsWithCaution
    fun warnSync(vararg content: Any, logToConsole: Boolean = true): ChannelResult<Unit> =
        logSync(LogMessage(content, logToConsole, Clock.System.now(), defaultLoggingStrategies.warnStrategy))
}

@RequiresOptIn("This is a non-blocking, semi-synchronous logging function. It queues log messages for later processing, which might not be immediate. Prefer the corresponding 'suspend' function for guaranteed asynchronous processing.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
annotation class UseSynchronousFunctionsWithCaution