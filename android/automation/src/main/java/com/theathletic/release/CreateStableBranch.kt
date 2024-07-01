package com.theathletic.release

import com.theathletic.release.data.GithubRepository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.RefSpec

/**
 * This task cuts and pushes a stable branch for the version currently set in the app version file.
 */
class CreateStableBranch : GitAction {
    override fun run(git: Git, githubRepository: GithubRepository) {
        val versionFile = VersionFile(git.repository.directory.parentFile)
        val branchName = versionFile.upcomingStableBranchName

        println("Creating Stable branch for release: $branchName")
        git.apply {
            branchCreate()
                .setName(branchName)
                .call()

            push()
                .setRemote("origin")
                .setRefSpecs(RefSpec(branchName))
                .call()
        }
    }
}