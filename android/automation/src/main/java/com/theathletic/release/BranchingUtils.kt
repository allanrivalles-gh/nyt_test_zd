package com.theathletic.release

// TODO(Todd) this is brittle, figure out a better way to handle this
val VersionFile.currentStableBranchName
    get() = "stable/$majorVersion-${minorVersion - 1}"

val VersionFile.currentStableReleaseTag
    get() = "v$majorVersion.${minorVersion - 1}.$patchVersion"

val VersionFile.upcomingBetaReleaseTag
    get() = "v$majorVersion.$minorVersion.$patchVersion-beta"

val VersionFile.upcomingStableBranchName
    get() = "stable/$majorVersion-$minorVersion"