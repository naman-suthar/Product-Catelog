package app.naman.lumostest.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.naman.lumostest.data.db.entity.ProductEntity

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity)

    @Query("SELECT * FROM products ORDER BY id ASC LIMIT :limit OFFSET :offset")
    suspend fun getProducts(limit: Int, offset: Int): List<ProductEntity>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Int): ProductEntity?

    @Query("DELETE FROM products")
    suspend fun deleteAll()

    @Query("DELETE FROM products WHERE category = :slug")
    suspend fun deleteByCategory(slug: String)

    @Query("SELECT * FROM products WHERE category = :slug ORDER BY id ASC LIMIT :limit OFFSET :offset")
    suspend fun getProductsByCategory(slug: String, limit: Int, offset: Int): List<ProductEntity>
}
