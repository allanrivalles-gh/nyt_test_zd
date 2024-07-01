package com.theathletic.release

import com.theathletic.release.data.GithubRepository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.RefSpec

/**
 * This task creates a branch and PR to merge any patches landed on the stable branch back to
 * the develop branch.
 */
class CreateMergebackPR : GitAction {

    override fun run(git: Git, githubRepository: GithubRepository) {
        val versionFile = VersionFile(git.repository.directory.parentFile)
        val currentStableBranch = versionFile.currentStableBranchName
        val mergeBranch = "merge/$currentStableBranch-to-develop"
        val commitMessage = "Merging $currentStableBranch to develop"
        createMergebackBranch(git, currentStableBranch, mergeBranch, commitMessage)
        println("Creating PR to merge $currentStableBranch back to develop")
        githubRepository.createPullRequest(
            branchName = mergeBranch,
            baseBranchName = "develop",
            title = commitMessage,
            body = """
                $commitMessage

                If approved and no conflicts, be sure to run Rebase and merge, NOT SQUASH & MERGE
            """.trimIndent()
        )
    }

    private fun createMergebackBranch(
        git: Git,
        stableBranch: String,
        mergeBranchName: String,
        commitMessage: String
    ) {
        git.apply {
            val stableBranchId = repository.resolve("origin/$stableBranch")
            checkout()
                .setName("develop")
                .call()

            checkout()
                .setName(mergeBranchName)
                .setCreateBranch(true)
                .call()

            merge()
                .include(stableBranchId)
                .setMessage(commitMessage)
                .call()

            push()
                .setRemote("origin")
                .setRefSpecs(RefSpec(mergeBranchName))
                .call()

            // Reset back to develop for any other tasks that come after this one
            checkout()
                .setName("develop")
                .call()
        }
    }
}