package co.ke.xently.features.storeservice.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface StoreServiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(vararg storeCategories: StoreServiceEntity)
}
