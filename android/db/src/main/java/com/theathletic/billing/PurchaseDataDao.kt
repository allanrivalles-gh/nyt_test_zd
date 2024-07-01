package com.theathletic.billing

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
abstract class PurchaseDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(purchaseDataEntity: PurchaseDataEntity)

    @Query("select * from purchase_data where googleToken = :token")
    abstract suspend fun getDataByToken(token: String): PurchaseDataEntity?

    @Query("select * from purchase_data where isSubPurchase = :isSubPurchase")
    abstract suspend fun getDataByType(isSubPurchase: Boolean): List<PurchaseDataEntity>

    @Query("delete from purchase_data where googleToken = :token")
    abstract suspend fun deleteDataByToken(token: String)
}