/*
 *     Foresst: File.kt
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

package dev.mtctx.foresst.io

import dev.mtctx.foresst.*
import dev.mtctx.foresst.configs.Config
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer
import java.io.File

fun <T> File.serializeAndWrite(
    serializer: SerializationStrategy<T>, content: T,
    callback: (file: File, value: String) -> Unit = { _, _ -> }
): File {
    val encoded = json.encodeToString(serializer, content)
    writeText(encoded)
    callback(this, encoded)
    return this
}

inline fun <reified T> File.serializeAndWrite(
    value: T,
    noinline callback: (file: File, value: String) -> Unit = { _, _ -> }
): File =
    serializeAndWrite(json.serializersModule.serializer(), value, callback)

fun <T> File.readAndDeserialize(
    deserializer: DeserializationStrategy<T>,
    callback: (file: File, value: Outcome<T>) -> Unit = { _, _ -> }
): Outcome<T> {
    val file = this
    if (!exists()) return failure("File does not exist (Path: $absolutePath)").apply { callback(file, this) }
    val content = readText()
    if (content.isEmpty() || content.isBlank()) return failure("File does not contain any content").apply {
        callback(
            file,
            this
        )
    }
    return success(json.decodeFromString(deserializer, content)).apply { callback(file, this) }
}

inline fun <reified T> File.readAndDeserialize(noinline callback: (file: File, value: Outcome<T>) -> Unit = { _, _ -> }): Outcome<T> =
    readAndDeserialize(json.serializersModule.serializer<T>(), callback)

val foresstConfig: Config by lazy {
    val file = appDir.resolve("config.json").toFile()
    file.parentFile.mkdirs()
    val defaultConfig = Config()

    if (!file.exists()) file.createNewFile()
    file.readAndDeserialize<Config> { f, outcome ->
        if (outcome is Outcome.Failure) f.serializeAndWrite(defaultConfig)
    }.getOrNull() ?: defaultConfig
}
