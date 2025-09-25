/*
 *     Foresst: GLDataType.kt
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

import org.lwjgl.opengl.GL46.*

enum class GLDataType(val size: Int, val glType: Int) {

    Float(1, GL_FLOAT),
    Integer(1, GL_INT),
    Boolean(1, GL_BOOL),
    Vec2(2, GL_FLOAT),
    Vec3(3, GL_FLOAT),
    Vec4(4, GL_FLOAT),
    Mat2(2 * 2, GL_FLOAT),
    Mat3(3 * 3, GL_FLOAT),
    Mat4(4 * 4, GL_FLOAT);

    fun byteSize(): Int =
        size * when (glType) {
            GL_INT -> Int.SIZE_BYTES
            GL_FLOAT -> kotlin.Float.SIZE_BYTES
            GL_DOUBLE -> Double.SIZE_BYTES
            GL_BOOL -> Byte.SIZE_BYTES
            else -> throw IllegalArgumentException("Unsupported GL type: $glType")
        }

}