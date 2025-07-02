package com.example.gezginrehberbt.util

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>() {
        constructor() : this(Unit as T)
    }
    data class Error(val message: String) : Result<Nothing>() {
        constructor(throwable: Throwable) : this(throwable.message ?: "Unknown error")
    }
    object Loading : Result<Nothing>()
    
    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun error(message: String): Result<Nothing> = Error(message)
        fun error(throwable: Throwable): Result<Nothing> = Error(throwable)
        fun loading(): Result<Nothing> = Loading
    }
}

val <T> Result<T>.isSuccess: Boolean
    get() = this is Result.Success

val <T> Result<T>.isError: Boolean
    get() = this is Result.Error

val <T> Result<T>.isLoading: Boolean
    get() = this is Result.Loading

fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

fun <T> Result<T>.onError(action: (String) -> Unit): Result<T> {
    if (this is Result.Error) action(message)
    return this
}

fun <T> Result<T>.onLoading(action: () -> Unit): Result<T> {
    if (this is Result.Loading) action()
    return this
}
