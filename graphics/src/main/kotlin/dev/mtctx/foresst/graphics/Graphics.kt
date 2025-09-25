/*
 *     Foresst: Graphics.kt
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

package dev.mtctx.foresst.graphics

import dev.mtctx.foresst.graphics.gl.GLShader
import dev.mtctx.foresst.graphics.gl.GLShaderType
import dev.mtctx.foresst.graphics.renderers.VoxelRenderer
import dev.mtctx.foresst.resource.Resource
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL46.*


// TERRIBLE CODE AHEAD; WATCH YOUR STEP
object Graphics {

    lateinit var window: Window
    lateinit var shader: GLShader
    private val renderers = mutableListOf<Renderer>()

    fun run() {
        init()
        render()
    }

    fun init() {
        GLFWErrorCallback.createPrint(System.err).set()
        check(glfwInit()) { "Unable to initialize GLFW" }
        window = Window()
        GL.createCapabilities()

        val fragment = Resource("frag.fs")
        val vertex = Resource("vert.vs")

        shader = GLShader(
            listOf(
                GLShader.ShaderModule(
                    fragment,
                    GLShaderType.FRAGMENT
                ),
                GLShader.ShaderModule(
                    vertex,
                    GLShaderType.VERTEX
                )
            )
        )

        renderers.add(VoxelRenderer(shader))
    }

    fun render() {
        while (!glfwWindowShouldClose(window.handle)) {
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            val deltaTime = glfwGetTime().toFloat()

            renderers.forEach {
                it.update(deltaTime)
                it.render()
            }

            glfwSwapBuffers(window.handle)
            glfwPollEvents()
        }

        renderers.forEach(Renderer::close)
    }

}