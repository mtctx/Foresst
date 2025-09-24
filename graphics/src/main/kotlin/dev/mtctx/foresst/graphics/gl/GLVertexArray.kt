/*
 *     Foresst: GLVertexArray.kt
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

package dev.mtctx.foresst.graphics.gl

import dev.mtctx.foresst.graphics.Renderable
import dev.mtctx.foresst.graphics.gl.buffer.GLIndexBuffer
import dev.mtctx.foresst.graphics.gl.buffer.GLVertexBuffer
import org.lwjgl.opengl.GL46.*

class GLVertexArray(private val indexBuffer: GLIndexBuffer? = null, private val mode: Int = GL_TRIANGLES) : Renderable {

    private var id: Int = 0
    private val vertexBuffers = mutableListOf<GLVertexBuffer>()

    init {
        id = glCreateVertexArrays()
        glBindVertexArray(id)

        indexBuffer?.bind()

        // unbind after setup
        glBindVertexArray(0)
    }

    fun addVertexBuffer(vertexBuffer: GLVertexBuffer) {
        if (vertexBuffer in vertexBuffers) return

        bind()
        vertexBuffer.bind()

        check(vertexBuffer.layout.bufferElements.isNotEmpty()) { "Layout contains no elements" }

        vertexBuffer.layout.bufferElements.forEachIndexed { index, element ->
            glEnableVertexAttribArray(index)
            glVertexAttribPointer(
                index,
                element.dataType.size,
                element.dataType.glType,
                false,
                vertexBuffer.layout.stride,
                element.offset.toLong()
            )
        }

        vertexBuffers += vertexBuffer
    }

    fun removeVertexBuffer(vertexBuffer: GLVertexBuffer) {
        bind()
        vertexBuffer.bind()
        vertexBuffer.close()

        vertexBuffers -= vertexBuffer
    }

    fun bind() {
        glBindVertexArray(id)
    }

    override fun render() {
        bind()

        if (indexBuffer != null && indexBuffer.count > 0) {
            glDrawElements(mode, indexBuffer.count, GL_UNSIGNED_INT, 0)
        } else {
            vertexBuffers.forEach { vertexBuffer ->
                glDrawArrays(mode, 0, vertexBuffer.count)
            }
        }
    }

    override fun close() {
        glBindVertexArray(0)

        vertexBuffers.forEach(GLVertexBuffer::close)
        indexBuffer?.close()
        glDeleteVertexArrays(id)
    }

}