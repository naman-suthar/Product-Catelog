package app.naman.lumostest.domain.repository

import app.naman.lumostest.domain.model.Category
import app.naman.lumostest.domain.model.DataResult
import app.naman.lumostest.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(skip: Int, limit: Int): Flow<DataResult<List<Product>>>
    fun getProductById(id: Int): Flow<DataResult<Product>>
    fun getCategories(): Flow<DataResult<List<Category>>>
    fun getProductsByCategory(slug: String, skip: Int, limit: Int): Flow<DataResult<List<Product>>>
}
