package com.theathletic.release

import com.theathletic.release.data.GithubRepository
import org.eclipse.jgit.api.Git

/**
 * This task creates a Github Prerelease with a beta tag and release notes based off the
 * stable branch indicated in the app version file.
 */
class CreateGHBeta : GitAction {
    override fun run(git: Git, githubRepository: GithubRepository) {
        val versionFile = VersionFile(git.repository.directory.parentFile)
        val branchName = versionFile.upcomingStableBranchName
        val betaTag = versionFile.upcomingBetaReleaseTag
        println("Creating Pre-Release on github for $branchName")
        githubRepository.createGHPrerelease(
            betaTag,
            branchName,
            "${versionFile.majorVersion}.${versionFile.minorVersion}.0 Public Release"
        )
        println("Go to https://github.com/TheAthletic/android/releases/tag/$betaTag to view the prerelease")
    }
}