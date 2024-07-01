package com.theathletic.extension

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import retrofit2.HttpException

fun Throwable.isNetworkUnavailable(): Boolean = this is SocketTimeoutException || this is ConnectException || this is UnknownHostException || (this is HttpException && this.code() == 504)