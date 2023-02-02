package me.injent.foodlib.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed interface ResultOf<out T> {
    data class Success<T>(val data: T) : ResultOf<T>
    data class Error(val exception: Throwable? = null) : ResultOf<Nothing>
    object Loading : ResultOf<Nothing>
}

fun <T> Flow<T>.asResult(): Flow<ResultOf<T>> {
    return this
        .map<T, ResultOf<T>> {
            ResultOf.Success(it)
        }
        .onStart { emit(ResultOf.Loading) }
        .catch { emit(ResultOf.Error(it)) }
}


/**
 * `true` if [Result] is of type [Success] & holds non-null [Success.data].
 */
val ResultOf<*>.succeeded
    get() = this is ResultOf.Success && data != null