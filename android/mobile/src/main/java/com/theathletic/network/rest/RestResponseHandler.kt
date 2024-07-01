package com.theathletic.network.rest

import retrofit2.HttpException
import retrofit2.Response

class RestResponseHandler {
    fun isSuccess(response: Response<*>): Boolean {
        return response.isSuccessful && response.code() >= 200 && response.code() < 300
    }
    fun createHttpException(response: Response<*>): HttpException {
        return HttpException(response)
    }
}