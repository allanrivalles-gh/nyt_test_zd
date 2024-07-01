package com.theathletic.repository

import com.apollographql.apollo3.exception.ApolloHttpException
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.theathletic.network.EmptyResponseException
import com.theathletic.network.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.CoroutineContext

/**
 * `block` is responsible for making a blocking call on a background thread
 */
suspend fun <T> safeApiRequest(
    coroutineContext: CoroutineContext = Dispatchers.IO,
    block: suspend () -> T?
): ResponseStatus<T> {
    return try {
        // TODO: Remove double context switch one we finish Rx->coroutines migration
        val response = withContext(coroutineContext) {
            block()
        }

        if (response == null) {
            ResponseStatus.Error(EmptyResponseException())
        } else {
            ResponseStatus.Success(response)
        }
    } catch (e: IOException) {
        ResponseStatus.Error(e)
    } catch (e: HttpException) {
        ResponseStatus.Error(e)
    } catch (e: ApolloHttpException) {
        ResponseStatus.Error(e)
    } catch (e: ApolloNetworkException) {
        ResponseStatus.Error(e)
    } catch (e: Exception) {
        ResponseStatus.Error(e)
    }
}