package app.naman.lumostest.domain.model

sealed class DataResult<out T> {
    data class Success<T>(val data: T, val isFromCache: Boolean = false) : DataResult<T>()
    data class Error(val error: AppError) : DataResult<Nothing>()
}
