/*
 *     Foresst: Path.kt
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

import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories

val appDir: Path by lazy {
    val foresstHome = System.getenv("FORESST_HOME") ?: System.getProperty("foresst.home")
    if (foresstHome != null && foresstHome.isNotBlank() && foresstHome.isNotEmpty()) return@lazy Path(
        foresstHome
    ).createDirectories()

    val os = System.getProperty("os.name").lowercase()
    val home = System.getProperty("user.home")
    val appName = "foresst"

    when {
        os.contains("win") -> {
            val appData = System.getenv("APPDATA")
            if (appData != null) Path(appData, appName) else Path(home, appName)
        }

        else -> Path(home, ".$appName").toAbsolutePath()
    }.createDirectories().apply {
        System.setProperty("foresst.home", toAbsolutePath().toString())
    }
}

val localizationDir: Path by lazy { appDir.resolve("localization") }
val modsDir: Path by lazy { appDir.resolve("mods") }
val logsDir: Path by lazy { appDir.resolve("logs") }