package com.theathletic.data

import com.theathletic.utility.coroutines.DispatcherProvider
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

data class TestRemoteModel(val id: String, val userId: Long)
data class TestDbModel(val id: Long, val userId: Long)

class TestFetcher(
    private val fakeDatabase: FakeDatabase,
    dispatcherProvider: DispatcherProvider
) :
    RemoteToLocalFetcher<
        TestFetcher.Params,
        TestRemoteModel,
        TestDbModel>(dispatcherProvider) {

    data class Params(val userId: Long)

    private var continuation: Continuation<TestRemoteModel?>? = null

    override suspend fun makeRemoteRequest(params: Params): TestRemoteModel? = suspendCoroutine {
        continuation = it
    }

    override fun mapToLocalModel(
        params: Params,
        remoteModel: TestRemoteModel
    ) = TestDbModel(
        id = remoteModel.id.toLong(),
        userId = remoteModel.userId
    )

    override suspend fun saveLocally(params: Params, dbModel: TestDbModel) {
        if (failDbWrites) throw IllegalStateException("Bad db write")
        fakeDatabase.save(dbModel)
    }

    // Test controls

    var failDbWrites = false

    fun succeed(model: TestRemoteModel?) {
        continuation?.resumeWith(Result.success(model))
        continuation = null
    }

    fun fail() {
        continuation?.resumeWith(Result.failure(RuntimeException("Error")))
        continuation = null
    }
}

class TestSubscriber(
    private val fakeDatabase: FakeDatabase,
    dispatcherProvider: DispatcherProvider
) : RemoteToLocalSubscriber<
    TestSubscriber.Params,
    TestRemoteModel,
    TestDbModel>(dispatcherProvider) {

    data class Params(val userId: Long)

    private val remoteResponseFlow = MutableSharedFlow<TestRemoteModel>(
        0, 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    fun receiveRemoteMessage(remoteModel: TestRemoteModel) {
        remoteResponseFlow.tryEmit(remoteModel)
    }

    override suspend fun makeRemoteRequest(params: Params) = remoteResponseFlow

    override fun mapToLocalModel(
        params: Params,
        remoteModel: TestRemoteModel
    ) = TestDbModel(
        id = remoteModel.id.toLong(),
        userId = remoteModel.userId
    )

    override suspend fun saveLocally(params: Params, dbModel: TestDbModel) {
        fakeDatabase.save(dbModel)
    }
}

class FakeDatabase {
    private val sharedFlow = MutableSharedFlow<TestDbModel>(
        0, 1,
        BufferOverflow.DROP_OLDEST
    )

    val valueStream: Flow<TestDbModel> = sharedFlow

    fun save(dbModel: TestDbModel) {
        sharedFlow.tryEmit(dbModel)
    }
}