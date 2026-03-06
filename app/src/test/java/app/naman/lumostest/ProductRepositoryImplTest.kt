package app.naman.lumostest

import app.naman.lumostest.data.api.ProductApi
import app.naman.lumostest.data.api.dto.ProductDto
import app.naman.lumostest.data.api.dto.ProductsResponseDto
import app.naman.lumostest.data.db.CategoryDao
import app.naman.lumostest.data.db.ProductDao
import app.naman.lumostest.data.db.entity.ProductEntity
import app.naman.lumostest.data.repository.ProductRepositoryImpl
import app.naman.lumostest.domain.model.AppError
import app.naman.lumostest.domain.model.DataResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProductRepositoryImplTest {

    private lateinit var api: ProductApi
    private lateinit var dao: ProductDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var repository: ProductRepositoryImpl

    private val sampleDto = ProductDto(
        id = 1, title = "iPhone 9", description = "An apple mobile", price = 549.99,
        discountPercentage = 12.96, rating = 4.69, stock = 94, brand = "Apple",
        category = "smartphones", thumbnail = "https://i.dummyjson.com/1.jpg",
        images = listOf("https://i.dummyjson.com/1.jpg")
    )

    private val sampleEntity = ProductEntity(
        id = 1, title = "iPhone 9", description = "An apple mobile", price = 549.99,
        discountPercentage = 12.96, rating = 4.69, stock = 94, brand = "Apple",
        category = "smartphones", thumbnail = "https://i.dummyjson.com/1.jpg",
        images = "https://i.dummyjson.com/1.jpg"
    )

    @Before
    fun setup() {
        api = mockk()
        dao = mockk(relaxed = true)
        categoryDao = mockk(relaxed = true)
        repository = ProductRepositoryImpl(api, dao, categoryDao)
    }

    @Test
    fun `getProducts success - returns network data and caches it`() = runTest {
        val response = ProductsResponseDto(
            products = listOf(sampleDto), total = 1, skip = 0, limit = 20
        )
        coEvery { api.getProducts(any(), any()) } returns response

        val result = repository.getProducts(skip = 0, limit = 20).first()

        assertTrue(result is DataResult.Success)
        val success = result as DataResult.Success
        assertFalse(success.isFromCache)
        assertEquals(1, success.data.size)
        assertEquals("iPhone 9", success.data[0].title)
        coVerify { dao.insertAll(any()) }
    }

    @Test
    fun `getProducts network error with cache - returns cached data with isFromCache=true`() = runTest {
        coEvery { api.getProducts(any(), any()) } throws Exception("No internet")
        coEvery { dao.getProducts(any(), any()) } returns listOf(sampleEntity)

        val result = repository.getProducts(skip = 0, limit = 20).first()

        assertTrue(result is DataResult.Success)
        val success = result as DataResult.Success
        assertTrue(success.isFromCache)
        assertEquals("iPhone 9", success.data[0].title)
    }

    @Test
    fun `getProducts network error no cache - returns Error`() = runTest {
        coEvery { api.getProducts(any(), any()) } throws java.net.UnknownHostException("No internet")
        coEvery { dao.getProducts(any(), any()) } returns emptyList()

        val result = repository.getProducts(skip = 0, limit = 20).first()

        assertTrue(result is DataResult.Error)
        assertTrue((result as DataResult.Error).error is AppError.Network)
    }

    @Test
    fun `getProductById success - returns product and caches it`() = runTest {
        coEvery { api.getProductById(1) } returns sampleDto
        coEvery { dao.getProductById(1) } returns null

        val result = repository.getProductById(1).first()

        assertTrue(result is DataResult.Success)
        assertFalse((result as DataResult.Success).isFromCache)
        assertEquals("iPhone 9", result.data.title)
        coVerify { dao.insert(any()) }
    }

    @Test
    fun `getProductById network error with cache - returns cached product`() = runTest {
        coEvery { api.getProductById(1) } throws Exception("Timeout")
        coEvery { dao.getProductById(1) } returns sampleEntity

        val result = repository.getProductById(1).first()

        assertTrue(result is DataResult.Success)
        assertTrue((result as DataResult.Success).isFromCache)
    }

    @Test
    fun `getProductById network error no cache - returns Error`() = runTest {
        coEvery { api.getProductById(1) } throws Exception("Timeout")
        coEvery { dao.getProductById(1) } returns null

        val result = repository.getProductById(1).first()

        assertTrue(result is DataResult.Error)
    }

    @Test
    fun `getProducts prefers network when available even if cache exists`() = runTest {
        coEvery { dao.getProducts(any(), any()) } returns listOf(sampleEntity)
        coEvery { api.getProducts(any(), any()) } returns ProductsResponseDto(
            products = listOf(sampleDto),
            total = 1,
            skip = 0,
            limit = 20
        )

        val emissions = repository.getProducts(skip = 0, limit = 20).toList()

        assertEquals(1, emissions.size)
        assertFalse((emissions[0] as DataResult.Success).isFromCache)
    }

    @Test
    fun `getCategories network failure with empty cache returns Error`() = runTest {
        coEvery { categoryDao.getAll() } returns emptyList()
        coEvery { api.getCategories() } throws java.net.UnknownHostException()

        val result = repository.getCategories().first()

        assertTrue(result is DataResult.Error)
        assertTrue((result as DataResult.Error).error is AppError.Network)
    }

    @Test
    fun `getProductsByCategory refresh clears category bucket before insert`() = runTest {
        coEvery { dao.getProductsByCategory(any(), any(), any()) } returns emptyList()
        coEvery { api.getProductsByCategory(any(), any(), any()) } returns ProductsResponseDto(
            products = listOf(sampleDto),
            total = 1,
            skip = 0,
            limit = 20
        )

        repository.getProductsByCategory(slug = "smartphones", skip = 0, limit = 20).first()

        coVerify { dao.deleteByCategory("smartphones") }
        coVerify { dao.insertAll(any()) }
    }
}
