package app.naman.lumostest.ui

import app.naman.lumostest.domain.model.AppError

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T, val isFromCache: Boolean = false) : UiState<T>()
    data class Error(val error: AppError) : UiState<Nothing>()
    data object Empty : UiState<Nothing>()
}
