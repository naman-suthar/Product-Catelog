package app.naman.lumostest.data.repository

import app.naman.lumostest.data.api.ProductApi
import app.naman.lumostest.data.api.dto.ProductDto
import app.naman.lumostest.data.db.CategoryDao
import app.naman.lumostest.data.db.ProductDao
import app.naman.lumostest.data.db.entity.CategoryEntity
import app.naman.lumostest.data.db.entity.ProductEntity
import app.naman.lumostest.domain.model.AppError
import app.naman.lumostest.domain.model.Category
import app.naman.lumostest.domain.model.DataResult
import app.naman.lumostest.domain.model.Product
import app.naman.lumostest.domain.repository.ProductRepository
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApi,
    private val dao: ProductDao,
    private val categoryDao: CategoryDao
) : ProductRepository {

    override fun getProducts(skip: Int, limit: Int): Flow<DataResult<List<Product>>> = flow {
        val cached = dao.getProducts(limit = limit, offset = skip)
        if (cached.isNotEmpty()) {
            emit(DataResult.Success(cached.map { it.toDomain() }, isFromCache = true))
        }

        try {
            val response = api.getProducts(limit = limit, skip = skip)
            val entities = response.products.map { it.toEntity() }
            if (skip == 0) dao.deleteAll()
            dao.insertAll(entities)
            emit(DataResult.Success(entities.map { it.toDomain() }, isFromCache = false))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (cached.isEmpty()) {
                emit(DataResult.Error(e.toAppError()))
            }
        }
    }

    override fun getProductById(id: Int): Flow<DataResult<Product>> = flow {
        val cached = dao.getProductById(id)
        if (cached != null) {
            emit(DataResult.Success(cached.toDomain(), isFromCache = true))
        }

        try {
            val dto = api.getProductById(id)
            val entity = dto.toEntity()
            dao.insert(entity)
            emit(DataResult.Success(entity.toDomain(), isFromCache = false))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (cached == null) {
                emit(DataResult.Error(e.toAppError()))
            }
        }
    }

    override fun getCategories(): Flow<DataResult<List<Category>>> = flow {
        val cached = categoryDao.getAll()
        if (cached.isNotEmpty()) {
            emit(DataResult.Success(cached.map { Category(slug = it.slug, name = it.name) }, isFromCache = true))
        }

        try {
            val dtos = api.getCategories()
            val entities = dtos.map { CategoryEntity(slug = it.slug, name = it.name) }
            categoryDao.deleteAll()
            categoryDao.insertAll(entities)
            emit(DataResult.Success(entities.map { Category(slug = it.slug, name = it.name) }, isFromCache = false))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (cached.isEmpty()) {
                emit(DataResult.Error(e.toAppError()))
            }
        }
    }

    override fun getProductsByCategory(slug: String, skip: Int, limit: Int): Flow<DataResult<List<Product>>> = flow {
        val cached = dao.getProductsByCategory(slug = slug, limit = limit, offset = skip)
        if (cached.isNotEmpty()) {
            emit(DataResult.Success(cached.map { it.toDomain() }, isFromCache = true))
        }

        try {
            val response = api.getProductsByCategory(slug = slug, limit = limit, skip = skip)
            val entities = response.products.map { it.toEntity() }
            if (skip == 0) dao.deleteByCategory(slug)
            dao.insertAll(entities)
            emit(DataResult.Success(entities.map { it.toDomain() }, isFromCache = false))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (cached.isEmpty()) {
                emit(DataResult.Error(e.toAppError()))
            }
        }
    }
}

private fun ProductDto.toEntity() = ProductEntity(
    id = id,
    title = title,
    description = description,
    price = price,
    discountPercentage = discountPercentage,
    rating = rating,
    stock = stock,
    brand = brand,
    category = category,
    thumbnail = thumbnail,
    images = images.joinToString(",")
)

private fun ProductEntity.toDomain() = Product(
    id = id,
    title = title,
    description = description,
    price = price,
    discountPercentage = discountPercentage,
    rating = rating,
    stock = stock,
    brand = brand,
    category = category,
    thumbnail = thumbnail,
    images = if (images.isBlank()) emptyList() else images.split(",")
)

private fun Exception.toAppError(): AppError = when (this) {
    is UnknownHostException, is ConnectException -> AppError.Network()
    is SocketTimeoutException -> AppError.Timeout()
    is HttpException -> AppError.Http(
        code = code(),
        message = message ?: "Server error."
    )
    else -> AppError.Unknown(message = message ?: "Unexpected error.")
}
