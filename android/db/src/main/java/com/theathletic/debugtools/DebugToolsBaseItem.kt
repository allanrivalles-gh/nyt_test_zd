package com.theathletic.debugtools

import androidx.databinding.ObservableBoolean
import androidx.room.Entity

abstract class DebugToolsBaseItem

@Entity(tableName = "developer_tools_modified_remote_config", primaryKeys = ["entryKey"])
data class RemoteConfigEntity(
    val entryKey: String,
    var entryValue: Boolean,
    var certainValue: ObservableBoolean
) : DebugToolsBaseItem()