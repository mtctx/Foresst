/*
 *     Foresst: GLVertexBuffer.kt
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

import dev.mtctx.foresst.graphics.Renderable
import org.lwjgl.opengl.GL46.*

class GLVertexBuffer(vertices: FloatArray, val layout: GLBufferLayout) : Renderable {

    private var id: Int = 0
    var count: Int = vertices.size

    init {
        id = glCreateBuffers()
        bind()

        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)
    }

    fun bind() {
        glBindBuffer(GL_ARRAY_BUFFER, id)
    }

    fun update(vertices: FloatArray) {
        bind()
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices)
        count = vertices.size
    }

    override fun render() {
        bind()
    }

    override fun close() {
        glDeleteBuffers(id)
    }

}