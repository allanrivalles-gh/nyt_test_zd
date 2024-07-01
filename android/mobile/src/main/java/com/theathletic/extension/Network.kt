package com.theathletic.extension

import com.theathletic.network.rest.RestResponseHandler
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

// TT Schedulers
@Deprecated("Use coroutines instead of Rx")
fun <T> Single<T>.applySchedulers(): Single<T> {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

@Deprecated("Use coroutines instead of Rx")
fun <T> Maybe<T>.applySchedulers(): Maybe<T> {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

@Deprecated("Use coroutines instead of Rx")
fun <T> Flowable<T>.applySchedulers(): Flowable<T> {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread(), true)
}

@Deprecated("Use coroutines instead of Rx")
fun <T> Observable<T>.applySchedulers(): Observable<T> {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread(), true)
}

@Deprecated("Use coroutines instead of Rx")
fun Completable.applySchedulers(): Completable {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

// TT HTTP Error catching
@Suppress("unused")
@Deprecated("Use coroutines instead of Rx")
fun <T : Response<*>> Single<T>.catchHttpError(
    responseHandler: RestResponseHandler
): Single<T> = flatMap {
    if (responseHandler.isSuccess(it)) {
        Single.just(it)
    } else {
        Single.error<T>(responseHandler.createHttpException(it))
    }
}

@Suppress("unused")
@Deprecated("Use coroutines instead of Rx")
fun <T : Response<*>> Maybe<T>.catchHttpError(
    responseHandler: RestResponseHandler
): Maybe<T> = flatMap {
    if (responseHandler.isSuccess(it)) {
        Maybe.just(it)
    } else {
        Maybe.error<T>(responseHandler.createHttpException(it))
    }
}

/**
 * Applies schedulers, catches HTTP errors + maps to it's body
 * (which is nonnull because the body is null only if error - which is skipped)
 * WARNING: Doesn't accept 204(empty body) (will result in NullPointerException). Use [mapRestRequestWithEmpty]
 */
@Deprecated("Use coroutines instead of Rx")
fun <T> Single<Response<T>>.mapRestRequest(
    responseHandler: RestResponseHandler = RestResponseHandler()
): Single<T> = this
    .applySchedulers()
    .catchHttpError(responseHandler)
    .map { it.body() }

@Deprecated("Use coroutines instead of Rx")
fun <T> Maybe<Response<T>>.mapRestRequest(
    responseHandler: RestResponseHandler = RestResponseHandler()
): Maybe<T> = this
    .applySchedulers()
    .catchHttpError(responseHandler)
    .map { it.body() }

@Deprecated("Use coroutines instead of Rx")
fun Completable.mapRestRequest(): Completable = this
    .applySchedulers()

/**
 * Accepts 204 which will result int calling onComplete
 * @see mapRestRequest
 */
@Deprecated("Use coroutines instead of Rx")
fun <A> Single<Response<A>>.mapRestRequestWithEmpty(
    responseHandler: RestResponseHandler = RestResponseHandler()
): Maybe<A> = this
    .applySchedulers()
    .catchHttpError(responseHandler)
    .flatMapMaybe { response -> response.body()?.let { Maybe.just(it) } ?: Maybe.empty() }

@Deprecated("Use coroutines instead of Rx")
fun <A> Maybe<Response<A>>.mapRestRequestWithEmpty(
    responseHandler: RestResponseHandler = RestResponseHandler()
): Maybe<A> = this
    .applySchedulers()
    .catchHttpError(responseHandler)
    .flatMap { response -> response.body()?.let { Maybe.just(it) } ?: Maybe.empty() }