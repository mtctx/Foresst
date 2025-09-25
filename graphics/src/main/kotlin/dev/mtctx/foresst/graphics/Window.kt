/*
 *     Foresst: Window.kt
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

package dev.mtctx.foresst.graphics

import dev.mtctx.foresst.GameModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL46.glViewport
import org.lwjgl.system.MemoryUtil
import java.io.Closeable

class Window(
    var width: Int = 800,
    var height: Int = 600,
    var handle: Long = -1
) : Closeable, GameModule() {

    init {
        runBlocking { start(CoroutineScope(Dispatchers.Main)) }
    }

    override suspend fun start(coroutineScope: CoroutineScope) {
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)

        handle = glfwCreateWindow(width, height, "Foresst", MemoryUtil.NULL, MemoryUtil.NULL)
        if (handle == MemoryUtil.NULL) throw RuntimeException("Failed to create the GLFW window")

        glfwMakeContextCurrent(handle)
        glfwSwapInterval(1)

        glfwShowWindow(handle)

        glfwSetFramebufferSizeCallback(handle) { _: Long, newWidth: Int, newHeight: Int ->
            this.width = newWidth
            this.height = newHeight
            glViewport(0, 0, width, height)
        }
    }

    override suspend fun stop(coroutineScope: CoroutineScope) {
        close()
    }

    override fun close() {
        Callbacks.glfwFreeCallbacks(handle)
        glfwDestroyWindow(handle)
    }

}