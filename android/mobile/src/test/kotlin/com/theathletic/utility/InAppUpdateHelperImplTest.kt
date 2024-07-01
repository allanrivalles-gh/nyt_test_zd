package com.theathletic.utility

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.OnSuccessListener
import com.google.android.play.core.tasks.Task
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class InAppUpdateHelperImplTest {

    @Mock private lateinit var appUpdateManager: AppUpdateManager
    @Mock private lateinit var preferences: IPreferences

    private lateinit var inAppUpdateHelper: InAppUpdateHelperImpl

    private companion object {
        const val VERSION_IS_NOT_STALE = 29
        const val VERSION_IS_STALE = 30

        const val NEW_VERSION_CODE = 123
    }

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        inAppUpdateHelper = InAppUpdateHelperImpl(appUpdateManager, preferences, mock())
    }

    @Test
    fun `handleActivityResult does nothing when result is ok`() {
        val updateInfoTask = mock<Task<AppUpdateInfo>>()
        whenever(appUpdateManager.appUpdateInfo).thenReturn(updateInfoTask)

        inAppUpdateHelper.handleActivityResult(RESULT_OK)
        verify(updateInfoTask, never()).addOnSuccessListener(anyOrNull())
    }

    @Test
    fun `handleActivityResult updates preferences when result is not ok`() {
        val updateInfoTask = mock<Task<AppUpdateInfo>>()
        whenever(appUpdateManager.appUpdateInfo).thenReturn(updateInfoTask)

        inAppUpdateHelper.handleActivityResult(RESULT_CANCELED)

        val captor = argumentCaptor<OnSuccessListener<AppUpdateInfo>>()
        verify(updateInfoTask).addOnSuccessListener(captor.capture())

        captor.firstValue.onSuccess(mock { on { availableVersionCode() } doReturn NEW_VERSION_CODE })
        verify(preferences).lastDeclinedUpdateVersionCode = NEW_VERSION_CODE
    }

    @Ignore
    fun `checkForAppStoreUpdate starts update flow`() {
        val updateInfo = createUpdateInfo(
            UpdateAvailability.UPDATE_AVAILABLE,
            true,
            VERSION_IS_STALE
        )

        attemptToLaunchFlow(updateInfo)

        verify(appUpdateManager).startUpdateFlowForResult(
            eq(updateInfo),
            any<Activity>(),
            anyOrNull(),
            anyInt()
        )
    }

    @Test
    fun `checkForAppStoreUpdate doesn't start flow when update is not available`() {
        val updateInfo = createUpdateInfo(
            UpdateAvailability.UPDATE_NOT_AVAILABLE,
            true,
            VERSION_IS_STALE
        )

        attemptToLaunchFlow(updateInfo)

        verify(appUpdateManager, never()).startUpdateFlowForResult(
            eq(updateInfo),
            any<Activity>(),
            anyOrNull(),
            anyInt()
        )
    }

    @Test
    fun `checkForAppStoreUpdate doesn't start flow when update type is not allowed`() {
        val updateInfo = createUpdateInfo(
            UpdateAvailability.UPDATE_AVAILABLE,
            false,
            VERSION_IS_STALE
        )

        attemptToLaunchFlow(updateInfo)

        verify(appUpdateManager, never()).startUpdateFlowForResult(
            eq(updateInfo),
            any<Activity>(),
            anyOrNull(),
            anyInt()
        )
    }

    @Test
    fun `checkForAppStoreUpdate doesn't start flow when client version is not stale`() {
        val updateInfo = createUpdateInfo(
            UpdateAvailability.UPDATE_AVAILABLE,
            true,
            VERSION_IS_NOT_STALE
        )

        attemptToLaunchFlow(updateInfo)

        verify(appUpdateManager, never()).startUpdateFlowForResult(
            eq(updateInfo),
            any<Activity>(),
            anyOrNull(),
            anyInt()
        )
    }

    @Test
    fun `checkForAppStoreUpdate doesn't start flow when user already declined the update`() {
        val updateInfo = createUpdateInfo(
            UpdateAvailability.UPDATE_AVAILABLE,
            true,
            VERSION_IS_STALE
        )
        whenever(preferences.lastDeclinedUpdateVersionCode).thenReturn(NEW_VERSION_CODE)

        attemptToLaunchFlow(updateInfo)

        verify(appUpdateManager, never()).startUpdateFlowForResult(
            eq(updateInfo),
            any<Activity>(),
            anyOrNull(),
            anyInt()
        )
    }

    @Ignore
    fun `checkForAppStoreUpdate uses onDownloadCompete when update is downloaded`() {
        val onDownloadCompete = mock<()->Unit>()
        invokeInstallStateUpdateListener(InstallStatus.DOWNLOADED, onDownloadCompete)

        verify(onDownloadCompete).invoke()
    }

    @Ignore
    fun `checkForAppStoreUpdate does nothing when update is not downloaded`() {
        val onDownloadCompete = mock<()->Unit>()
        invokeInstallStateUpdateListener(InstallStatus.DOWNLOADING, onDownloadCompete)

        verify(onDownloadCompete, never()).invoke()
    }

    @Ignore
    fun `checkForAppStoreUpdate updates preferences when download is canceled`() {
        val onDownloadCompete = mock<()->Unit>()
        invokeInstallStateUpdateListener(InstallStatus.CANCELED, onDownloadCompete)

        verify(onDownloadCompete, never()).invoke()
        verify(preferences).lastDeclinedUpdateVersionCode = NEW_VERSION_CODE
    }

    private fun attemptToLaunchFlow(
        updateInfo: AppUpdateInfo,
        onDownloadCompete: () -> Unit = mock()
    ) {
        val updateInfoTask = mock<Task<AppUpdateInfo>>()
        whenever(appUpdateManager.appUpdateInfo).thenReturn(updateInfoTask)

        inAppUpdateHelper.checkForAppStoreUpdate(mock(), 1, onDownloadCompete)

        val captor = argumentCaptor<OnSuccessListener<AppUpdateInfo>>()
        verify(updateInfoTask).addOnSuccessListener(captor.capture())

        captor.firstValue.onSuccess(updateInfo)
    }

    private fun createUpdateInfo(
        availability: Int,
        isTypeAllowed: Boolean,
        clientStaleness: Int
    ): AppUpdateInfo = mock {
        on { updateAvailability() } doReturn availability
        on { isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) } doReturn isTypeAllowed
        on { clientVersionStalenessDays() } doReturn clientStaleness
        on { availableVersionCode() } doReturn NEW_VERSION_CODE
    }

    private fun invokeInstallStateUpdateListener(
        installStatus: Int,
        onDownloadCompete: () -> Unit
    ) {
        val updateInfo = createUpdateInfo(
            UpdateAvailability.UPDATE_AVAILABLE,
            true,
            VERSION_IS_STALE
        )
        attemptToLaunchFlow(updateInfo, onDownloadCompete)

        val captor = argumentCaptor<InstallStateUpdatedListener>()
        verify(appUpdateManager).registerListener(captor.capture())

        captor.firstValue.onStateUpdate(mock { on { installStatus() } doReturn installStatus })
    }

    @Test
    fun `checkForFinishedUpdate uses onDownloadComplete when update is downloaded`() {
        val updateInfo = mock<AppUpdateInfo> {
            on { installStatus() } doReturn InstallStatus.DOWNLOADED
        }
        val task = mock<Task<AppUpdateInfo>>()
        whenever(appUpdateManager.appUpdateInfo).thenReturn(task)

        val onDownloadComplete = mock<() -> Unit>()
        inAppUpdateHelper.checkForFinishedUpdate(onDownloadComplete)

        val captor = argumentCaptor<OnSuccessListener<AppUpdateInfo>>()
        verify(task).addOnSuccessListener(captor.capture())

        captor.firstValue.onSuccess(updateInfo)
        verify(onDownloadComplete).invoke()
    }

    @Test
    fun `checkForFinishedUpdate doesn't use onDownloadComplete when update is not downloaded`() {
        val updateInfo = mock<AppUpdateInfo> {
            on { installStatus() } doReturn InstallStatus.DOWNLOADING
        }
        val task = mock<Task<AppUpdateInfo>>()
        whenever(appUpdateManager.appUpdateInfo).thenReturn(task)

        val onDownloadComplete = mock<() -> Unit>()
        inAppUpdateHelper.checkForFinishedUpdate(onDownloadComplete)

        val captor = argumentCaptor<OnSuccessListener<AppUpdateInfo>>()
        verify(task).addOnSuccessListener(captor.capture())

        captor.firstValue.onSuccess(updateInfo)
        verify(onDownloadComplete, never()).invoke()
    }

    @Test
    fun `completeUpdate calls AppUpdateManager completeUpdate`() {
        inAppUpdateHelper.completeUpdate()
        verify(appUpdateManager).completeUpdate()
    }
}