package com.theathletic.release

import com.theathletic.release.data.GithubRepository
import org.eclipse.jgit.api.Git

/**
 * This task just ensures we clone the repo successfully to verify authentication works.
 */
class ValidateAuthentication : GitAction {
    override fun run(git: Git, githubRepository: GithubRepository) {
        val versionFile = VersionFile(git.repository.directory.parentFile)
        githubRepository.validateAuth()
        println("Current versionFile branch name: ${versionFile.currentStableBranchName}")
        println("Successfully cloned git and hit the github.com API!")
    }
}