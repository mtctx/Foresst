/*
 *     Foresst: Resource.kt
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

package dev.mtctx.foresst.resource

import java.io.File
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class Resource(resourcePath: String) {

    private val inputStream: InputStream =
        ResourceLoader.getResourceAsStream(resourcePath)
            ?: throw IllegalStateException("Resource $resourcePath not found.")

    private val tempFile: Path by lazy {
        val tempFile = Files.createTempFile("foresst-", ".tmp")
        tempFile.toFile().deleteOnExit()
        Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING)
        tempFile
    }

    val file: File
        get() = tempFile.toFile()
}