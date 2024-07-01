package com.theathletic.billing

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope

class BillingRepository @AutoKoin(scope = Scope.SINGLE) constructor(
    private val purchaseDataDao: PurchaseDataDao,
    private val billingApi: BillingApi
) {

    suspend fun savePurchaseData(purchaseDataEntity: PurchaseDataEntity) {
        purchaseDataDao.insert(purchaseDataEntity)
    }

    suspend fun getPurchaseDataByToken(token: String) = purchaseDataDao.getDataByToken(token)

    suspend fun getPurchaseDataByType(isSubPurchase: Boolean) = purchaseDataDao.getDataByType(isSubPurchase)

    suspend fun deletePurchaseDataByToken(token: String) {
        purchaseDataDao.deleteDataByToken(token)
    }

    suspend fun registerGooglePurchase(
        purchaseDataEntity: PurchaseDataEntity,
        deviceId: String?
    ) = billingApi.registerGoogleSubscription(
        subscriptionId = purchaseDataEntity.productSku,
        token = purchaseDataEntity.googleToken,
        source = purchaseDataEntity.source,
        articleId = purchaseDataEntity.lastArticleId,
        podcastEpisodeId = purchaseDataEntity.lastPodcastId,
        deviceId = deviceId,
        price = purchaseDataEntity.price,
        priceCurrency = purchaseDataEntity.priceCurrency,
        productSku = purchaseDataEntity.productSku,
        planTerm = purchaseDataEntity.planTerm,
        planNum = purchaseDataEntity.planNum
    )
}