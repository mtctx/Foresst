/*
 *     Foresst: VoxelRenderer.kt
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

package dev.mtctx.foresst.graphics.renderers

import dev.mtctx.foresst.graphics.Renderer
import dev.mtctx.foresst.graphics.gl.GLDataType
import dev.mtctx.foresst.graphics.gl.GLShader
import dev.mtctx.foresst.graphics.gl.GLVertexArray
import dev.mtctx.foresst.graphics.gl.buffer.GLBufferLayout
import dev.mtctx.foresst.graphics.gl.buffer.GLIndexBuffer
import dev.mtctx.foresst.graphics.gl.buffer.GLVertexBuffer
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3

class VoxelRenderer(private val shader: GLShader) : Renderer {

    private val vao: GLVertexArray

    private var model: Mat4 = Mat4(1.0f)
    private var view: Mat4 = glm.lookAt(
        Vec3(2f, 2f, 2f),
        Vec3(0f, 0f, 0f),
        Vec3(0f, 1f, 0f)
    )
    private var projection: Mat4 = glm.perspective(glm.radians(45f), 800f / 600f, 0.1f, 100f)

    init {
        // Cube vertices
        val vertices = floatArrayOf(
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            -0.5f, 0.5f, -0.5f,
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f
        )

        val indices = intArrayOf(
            0, 1, 2, 2, 3, 0,
            4, 5, 6, 6, 7, 4,
            0, 1, 5, 5, 4, 0,
            2, 3, 7, 7, 6, 2,
            0, 3, 7, 7, 4, 0,
            1, 2, 6, 6, 5, 1
        )

        val layout = GLBufferLayout(mapOf("position" to GLDataType.VEC3))
        val vbo = GLVertexBuffer(vertices, layout)
        val ibo = GLIndexBuffer(indices)

        vao = GLVertexArray(ibo)
        vao.addVertexBuffer(vbo)
    }

    override fun render() {
        shader.use {
            val mvp = projection * view * model
            uniformMat4("mvp", mvp)

            vao.render()
        }
    }

    override fun close() {
        vao.close()
    }
}
