/*
 *     Foresst: Localization.kt
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

package dev.mtctx.foresst.configs

import dev.mtctx.foresst.io.appDir
import dev.mtctx.foresst.io.serializeAndWrite
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Localization(
    @Transient val language: String = "en"
    // TODO: Extend with strings, possibly also log messages, ...
) {
    fun save() = appDir.resolve("localization/$language.json").toFile().serializeAndWrite(this)
}