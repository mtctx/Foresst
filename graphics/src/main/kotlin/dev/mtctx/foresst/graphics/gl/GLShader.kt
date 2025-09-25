/*
 *     Foresst: GLShader.kt
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

package dev.mtctx.foresst.graphics.gl

import dev.mtctx.foresst.graphics.Renderable
import dev.mtctx.foresst.resource.Resource
import dev.mtctx.foresst.resource.asText
import glm_.mat2x2.Mat2
import glm_.mat3x3.Mat3
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import org.lwjgl.opengl.GL46.*

class GLShader(
    modules: List<ShaderModule>
) : Renderable {

    private var program: Int = 0
    private val uniformLocationCache = mutableMapOf<String, Int>()

    init {
        if (modules.isEmpty()) throw IllegalArgumentException("No shader modules provided")

        val duplicates = modules.groupingBy { it.shaderType }
            .eachCount()
            .filterValues { it > 1 }
            .keys

        if (duplicates.isNotEmpty()) {
            throw IllegalArgumentException("Duplicate shader modules for types: ${duplicates.joinToString()}")
        }

        program = glCreateProgram().also { if (it == 0) throw RuntimeException("glCreateProgram returned 0") }

        val shaderIds = mutableListOf<Int>()
        try {
            for (module in modules) {
                val shaderId = compileModule(module)
                shaderIds += shaderId
                glAttachShader(program, shaderId)
            }

            glLinkProgram(program)
            val linkStatus = glGetProgrami(program, GL_LINK_STATUS)
            if (linkStatus == GL_FALSE) {
                val infoLog = glGetProgramInfoLog(program)
                throw RuntimeException("Failed to link program: $infoLog")
            }

            for (id in shaderIds) {
                glDetachShader(program, id)
                glDeleteShader(id)
            }

        } catch (e: Exception) {
            for (id in shaderIds) {
                if (glIsShader(id)) glDeleteShader(id)
            }
            if (glIsProgram(program)) {
                glDeleteProgram(program)
                program = 0
            }
            throw e
        }
    }

    private fun compileModule(module: ShaderModule): Int {
        val shaderId = glCreateShader(module.shaderType.id)
        if (shaderId == 0) throw RuntimeException("glCreateShader returned 0 for type=${module.shaderType}")

        glShaderSource(shaderId, module.res.asText())
        glCompileShader(shaderId)

        val status = glGetShaderi(shaderId, GL_COMPILE_STATUS)
        if (status == GL_FALSE) {
            val infoLog = glGetShaderInfoLog(shaderId)
            glDeleteShader(shaderId)
            throw RuntimeException("Failed to compile shader (${module.name ?: module.shaderType}): $infoLog")
        }

        return shaderId
    }

    fun use(block: Context.() -> Unit) {
        bind()
        try {
            Context(this).block()
        } finally {
            unbind()
        }
    }

    fun bind() {
        if (program == 0) throw IllegalStateException("Program has been deleted")
        glUseProgram(program)
    }

    fun unbind() {
        glUseProgram(0)
    }

    override fun render() {
        bind()
    }

    override fun close() {
        unbind()
    }

    fun delete() {
        if (program != 0 && glIsProgram(program)) {
            glDeleteProgram(program)
            program = 0
        }
    }

    data class ShaderModule(val res: Resource, val shaderType: GLShaderType, val name: String? = null)

    class Context internal constructor(private val shader: GLShader) {

        private fun getLocation(name: String): Int =
            shader.uniformLocationCache.getOrPut(name) {
                glGetUniformLocation(shader.program, name)
            }

        fun uniformInt(name: String, value: Int) {
            val loc = getLocation(name)
            if (loc != -1) glUniform1i(loc, value)
        }

        fun uniformFloat(name: String, value: Float) {
            val loc = getLocation(name)
            if (loc != -1) glUniform1f(loc, value)
        }

        fun uniformVec2(name: String, value: Vec2) {
            val loc = getLocation(name)
            if (loc != -1) glUniform2f(loc, value.x, value.y)
        }

        fun uniformVec3(name: String, value: Vec3) {
            val loc = getLocation(name)
            if (loc != -1) glUniform3f(loc, value.x, value.y, value.z)
        }

        fun uniformVec4(name: String, value: Vec4) {
            val loc = getLocation(name)
            if (loc != -1) glUniform4f(loc, value.x, value.y, value.z, value.w)
        }

        fun uniformMat2(name: String, value: Mat2) {
            val loc = getLocation(name)
            if (loc != -1) glUniformMatrix2fv(loc, false, value.array)
        }

        fun uniformMat3(name: String, value: Mat3) {
            val loc = getLocation(name)
            if (loc != -1) glUniformMatrix3fv(loc, false, value.array)
        }

        fun uniformMat4(name: String, value: Mat4) {
            val loc = getLocation(name)
            if (loc != -1) glUniformMatrix4fv(loc, false, value.array)
        }

        fun uniformIntArray(name: String, values: IntArray) {
            val loc = getLocation(name)
            if (loc != -1) glUniform1iv(loc, values)
        }
    }
}
