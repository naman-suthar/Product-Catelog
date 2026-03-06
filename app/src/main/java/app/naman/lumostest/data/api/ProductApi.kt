package app.naman.lumostest.data.api

import app.naman.lumostest.data.api.dto.CategoryDto
import app.naman.lumostest.data.api.dto.ProductDto
import app.naman.lumostest.data.api.dto.ProductsResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {
    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): ProductsResponseDto

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): ProductDto

    @GET("products/categories")
    suspend fun getCategories(): List<CategoryDto>

    @GET("products/category/{slug}")
    suspend fun getProductsByCategory(
        @Path("slug") slug: String,
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): ProductsResponseDto
}
