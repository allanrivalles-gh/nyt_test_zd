package com.theathletic.release

import com.theathletic.release.data.GithubRepository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.RefSpec

/**
 * This task bumps the app version file minor version by 1, pushes it and creates a PR to merge it.
 */
class CreateMinorVersionBumpPR : GitAction {

    override fun run(git: Git, githubRepository: GithubRepository) {
        val versionFile = VersionFile(git.repository.directory.parentFile)

        versionFile.update(
            major = versionFile.majorVersion,
            minor = versionFile.minorVersion + 1,
            patch = 0
        )

        val branchName = "version-bump-${versionFile.majorVersion}-${versionFile.minorVersion}"
        val commitMessage = "Bumping version to: ${versionFile.versionString}"
        commitVersionFile(git, branchName, commitMessage)
        println("Creating PR to bump version file to ${versionFile.versionString}")
        githubRepository.createPullRequest(branchName, "develop", commitMessage)
    }

    private fun commitVersionFile(
        git: Git,
        branchName: String,
        commitMessage: String
    ) {
        git.apply {
            checkout()
                .setName(branchName)
                .setCreateBranch(true)
                .call()

            add()
                .addFilepattern("mobile/version.properties")
                .call()

            commit()
                .setMessage(commitMessage)
                .call()

            push()
                .setRemote("origin")
                .setRefSpecs(RefSpec(branchName))
                .call()

            // Reset back to develop for any other tasks that come after this one
            checkout()
                .setName("develop")
                .call()
        }
    }
}