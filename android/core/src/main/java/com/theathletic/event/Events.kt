package com.theathletic.event

import com.theathletic.utility.Event

// TT Global events

class SnackbarEvent(val message: String) : Event()
class SnackbarEventRes(val msgResId: Int) : Event()
class ToastEvent(val message: String) : Event()
object HapticSuccessFeedback : Event()
object HapticFailureFeedback : Event()
object NetworkErrorEvent : Event()
class DataChangeEvent : Event()
class ToolbarCollapseEvent : Event()

// Tt Podcast Player
class SetSleepTimerEvent(val sleepDelay: Long) : Event()

class CancelSleepTimerEvent : Event()