package app.naman.lumostest.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.naman.lumostest.domain.model.Category
import app.naman.lumostest.domain.model.DataResult
import app.naman.lumostest.domain.model.Product
import app.naman.lumostest.domain.repository.ProductRepository
import app.naman.lumostest.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PAGE_SIZE = 20

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Product>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Product>>> = _uiState.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _categoriesState = MutableStateFlow<UiState<List<Category>>>(UiState.Loading)
    val categoriesState: StateFlow<UiState<List<Category>>> = _categoriesState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private var currentSkip = 0
    private var canLoadMore = true
    private val allProducts = mutableListOf<Product>()
    private var loadProductsJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        loadCategories()
        loadProducts(refresh = true)
    }

    fun loadCategories() {
        viewModelScope.launch {
            repository.getCategories().collect { result ->
                when (result) {
                    is DataResult.Success -> _categoriesState.value = if (result.data.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(data = result.data, isFromCache = result.isFromCache)
                    }
                    is DataResult.Error -> _categoriesState.value = UiState.Error(result.error)
                }
            }
        }
    }

    fun selectCategory(slug: String?) {
        if (_selectedCategory.value == slug) return
        loadMoreJob?.cancel()
        loadMoreJob = null
        _isLoadingMore.value = false
        _selectedCategory.value = slug
        loadProducts(refresh = true)
    }

    fun loadProducts(refresh: Boolean = false) {
        loadProductsJob?.cancel()
        if (refresh) {
            currentSkip = 0
            canLoadMore = true
            if (_uiState.value is UiState.Success) {
                _isRefreshing.value = true
            } else {
                allProducts.clear()
                _uiState.value = UiState.Loading
            }
        }
        loadProductsJob = viewModelScope.launch {
            buildProductFlow(skip = currentSkip).collect { result ->
                when (result) {
                    is DataResult.Success -> {
                        if (refresh) allProducts.clear()
                        if (result.data.isEmpty() && allProducts.isEmpty()) {
                            _uiState.value = UiState.Empty
                        } else {
                            canLoadMore = result.data.size >= PAGE_SIZE
                            val seenIds = allProducts.asSequence().map { it.id }.toMutableSet()
                            val uniqueNew = result.data.filter { seenIds.add(it.id) }
                            allProducts.addAll(uniqueNew)
                            _uiState.value = UiState.Success(
                                data = allProducts.toList(),
                                isFromCache = result.isFromCache
                            )
                        }
                        _isRefreshing.value = false
                    }
                    is DataResult.Error -> {
                        if (allProducts.isEmpty()) {
                            _uiState.value = UiState.Error(result.error)
                        }
                        _isRefreshing.value = false
                    }
                }
            }
        }
    }

    fun loadNextPage() {
        if (!canLoadMore || _isLoadingMore.value || _isRefreshing.value) return
        val current = _uiState.value
        if (current !is UiState.Success) return

        _isLoadingMore.value = true
        currentSkip += PAGE_SIZE

        loadMoreJob = viewModelScope.launch {
            buildProductFlow(skip = currentSkip).collect { result ->
                _isLoadingMore.value = false
                when (result) {
                    is DataResult.Success -> {
                        if (result.data.isEmpty()) {
                            canLoadMore = false
                        } else {
                            canLoadMore = result.data.size >= PAGE_SIZE
                            val seenIds = allProducts.asSequence().map { it.id }.toMutableSet()
                            val uniqueNew = result.data.filter { seenIds.add(it.id) }
                            allProducts.addAll(uniqueNew)
                            _uiState.update {
                                UiState.Success(
                                    data = allProducts.toList(),
                                    isFromCache = result.isFromCache
                                )
                            }
                        }
                    }
                    is DataResult.Error -> {
                        currentSkip -= PAGE_SIZE
                    }
                }
            }
        }
    }

    private fun buildProductFlow(skip: Int): Flow<DataResult<List<Product>>> {
        val slug = _selectedCategory.value
        return if (slug == null) {
            repository.getProducts(skip = skip, limit = PAGE_SIZE)
        } else {
            repository.getProductsByCategory(slug = slug, skip = skip, limit = PAGE_SIZE)
        }
    }
}
