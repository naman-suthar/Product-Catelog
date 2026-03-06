package app.naman.lumostest.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.naman.lumostest.domain.model.DataResult
import app.naman.lumostest.domain.model.Product
import app.naman.lumostest.domain.repository.ProductRepository
import app.naman.lumostest.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: Int = checkNotNull(savedStateHandle["id"])

    private val _uiState = MutableStateFlow<UiState<Product>>(UiState.Loading)
    val uiState: StateFlow<UiState<Product>> = _uiState.asStateFlow()

    init {
        loadProduct()
    }

    fun loadProduct() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            repository.getProductById(productId).collect { result ->
                _uiState.value = when (result) {
                    is DataResult.Success -> UiState.Success(result.data, result.isFromCache)
                    is DataResult.Error -> UiState.Error(result.error)
                }
            }
        }
    }
}
