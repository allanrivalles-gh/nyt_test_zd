package com.theathletic.release

import com.theathletic.release.data.GithubRepository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevWalk

class CreateReleaseTag : GitAction {
    override fun run(git: Git, githubRepository: GithubRepository) {
        val versionFile = VersionFile(git.repository.directory.parentFile)
        val branchName = versionFile.currentStableBranchName
        val releaseTag = versionFile.currentStableReleaseTag

        println("Tagging $branchName with tag: $releaseTag")

        git.apply {
            val walker = RevWalk(repository)
            val stableBranchId = repository.resolve("origin/$branchName")
            val objectToTag = walker.lookupCommit(stableBranchId)

            val tagRef = tag()
                .setObjectId(objectToTag)
                .setName(releaseTag)
                .setMessage("Public Release $releaseTag")
                .call()

            if (tagRef != null) {
                push()
                    .setRemote("origin")
                    .setPushTags()
                    .call()
                println("Successfully tagged $branchName with $releaseTag")
            } else {
                println("Failed to create tag $releaseTag")
            }
        }
    }
}