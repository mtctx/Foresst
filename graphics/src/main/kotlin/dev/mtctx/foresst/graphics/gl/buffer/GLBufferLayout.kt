/*
 *     Foresst: GLBufferLayout.kt
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

package dev.mtctx.foresst.graphics.gl.buffer

import dev.mtctx.foresst.graphics.gl.GLDataType

class GLBufferLayout(map: Map<String, GLDataType>) {

    var stride: Int = 0
    var bufferElements = mutableListOf<GLBufferElement>()

    init {
        require(map.isNotEmpty()) { "Can't create buffer layout with empty map" }

        calculateOffsets(map)

        // stride represents the byte offset between consecutive elements within the buffer
        stride =
            bufferElements.lastOrNull()?.let {
                it.offset + it.dataType.byteSize()
            } ?: 0
    }

    private fun calculateOffsets(map: Map<String, GLDataType>) {
        var offset = 0
        map.forEach { (name, dataType) ->
            val bufferElement = GLBufferElement(name, dataType, offset)
            bufferElements += bufferElement
            offset += dataType.byteSize()
        }
    }

}