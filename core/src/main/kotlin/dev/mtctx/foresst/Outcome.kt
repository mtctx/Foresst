/*
 *     Foresst: Outcome.kt
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

package dev.mtctx.foresst

import dev.mtctx.foresst.Outcome.Failure
import dev.mtctx.foresst.Outcome.Success

sealed interface Outcome<out T> {
    val succeeded: Boolean
    val failed: Boolean get() = !succeeded

    @JvmInline
    value class Success<T>(val value: T) : Outcome<T> {
        override val succeeded: Boolean get() = true
        override fun toString(): String = "Success(value=$value, type=${value!!.javaClass.name})"
    }

    data class Failure(val message: String, val throwable: Throwable? = null) : Outcome<Nothing> {
        override val succeeded: Boolean get() = false
        override fun toString(): String =
            "Failure(message=$message" + if (throwable != null) ", cause=${throwable.javaClass.name})" else ")"

        fun toStringWithStackTrace(): String =
            "Failure(message=$message" + if (throwable != null) ", stacktrace=${throwable.stackTraceToString()})" else ")"
    }
}

inline fun <T, R> Outcome<T>.map(transform: (T) -> R): Outcome<R> = when (this) {
    is Success -> Success(transform(value))
    is Failure -> this
}

inline fun <T, R> Outcome<T>.flatMap(transform: (T) -> Outcome<R>): Outcome<R> = when (this) {
    is Success -> transform(value)
    is Failure -> this
}

inline fun <T, R> Outcome<T>.mapCatching(transform: (T) -> R): Outcome<R> = when (this) {
    is Success ->
        try {
            Success(transform(value))
        } catch (t: Throwable) {
            Failure("Exception in mapCatching", t)
        }

    is Failure -> this
}

inline fun <T> Outcome<T>.mapFailure(transform: (Failure) -> Failure): Outcome<T> = when (this) {
    is Success -> this
    is Failure -> transform(this)
}

inline fun <T, R> Outcome<T>.fold(onSuccess: (T) -> R, onFailure: (String, Throwable?) -> R): R = when (this) {
    is Success -> onSuccess(value)
    is Failure -> onFailure(message, throwable)
}

inline fun <T> Outcome<T>.recover(recoverBlock: (Failure) -> T): Outcome<T> = when (this) {
    is Success -> this
    is Failure -> success(recoverBlock(this))
}

inline fun <T> Outcome<T>.recoverCatching(recoverBlock: (Failure) -> T): Outcome<T> = when (this) {
    is Success -> this
    is Failure ->
        try {
            success(recoverBlock(this))
        } catch (t: Throwable) {
            failure("Exception in recoverCatching", t)
        }
}

fun <T> Outcome<T>.getOrNull(): T? = when (this) {
    is Success -> value
    is Failure -> null
}

fun <T> Outcome<T>.exceptionOrNull(): Throwable? = when (this) {
    is Success -> null
    is Failure -> throwable
}

fun <T> Outcome<T>.toResult(): Result<T> = when (this) {
    is Success -> Result.success(value)
    is Failure -> Result.failure(throwable ?: Exception(message))
}

fun <T> Result<T>.toOutcome(): Outcome<T> = fold(::success, ::failure)

inline fun <T> runCatchingOutcome(block: () -> T): Outcome<T> {
    return try {
        success(block())
    } catch (e: Throwable) {
        failure(e)
    }
}

inline fun <T> T.runCatchingOutcome(block: () -> T): Outcome<T> =
    try {
        success(block())
    } catch (t: Throwable) {
        failure("Exception in runCatching Outcome", t)
    }

fun success(): Success<Boolean> = Success(true)
fun <T> success(value: T): Success<T> = Success(value)
inline fun <T> success(block: () -> T): Success<T> {
    return Success(block())
}

fun failure(message: String, throwable: Throwable? = null): Failure = Failure(message, throwable)
fun failure(throwable: Throwable): Failure = Failure("No message provided.", throwable)