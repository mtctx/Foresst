/*
 *     Foresst: conversion.kt
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

package dev.mtctx.foresst.resource

import java.io.InputStream
import java.nio.charset.Charset

fun Resource.asText(charset: String = "UTF-8"): String =
    file.readText(Charset.forName(charset))

fun Resource.asByteArray(): ByteArray =
    file.readBytes()

fun Resource.asInputStream(): InputStream =
    file.inputStream()

fun Resource.asReader(charset: String = "UTF-8") =
    file.reader(Charset.forName(charset))

fun Resource.asLines(charset: String = "UTF-8"): Sequence<String> =
    file.useLines(Charset.forName(charset)) { it.toList() }.asSequence()