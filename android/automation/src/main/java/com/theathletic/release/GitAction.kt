package com.theathletic.release

import com.theathletic.release.data.GithubRepository
import org.eclipse.jgit.api.Git

interface GitAction {
    fun run(git: Git, githubRepository: GithubRepository)
}