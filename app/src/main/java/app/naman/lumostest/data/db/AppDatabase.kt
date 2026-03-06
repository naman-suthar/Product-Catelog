package app.naman.lumostest.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import app.naman.lumostest.data.db.entity.CategoryEntity
import app.naman.lumostest.data.db.entity.ProductEntity

@Database(entities = [ProductEntity::class, CategoryEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
}
