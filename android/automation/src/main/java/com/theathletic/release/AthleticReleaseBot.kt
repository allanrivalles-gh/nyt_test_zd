package com.theathletic.release

import com.theathletic.release.data.GithubRepository
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.SshTransport
import org.eclipse.jgit.transport.sshd.SshdSessionFactoryBuilder
import org.eclipse.jgit.util.FS
import java.io.File
import kotlin.system.exitProcess

/**
 * To execute the tasks in this bot, be sure to have:
 * 1) a valid id_rsa file in your ~/.ssh directory that you use to run local git commands
 * 2) a github.com token with the following roles: repo (all roles), admin:org->read:org
 * that is set as ATHLETIC_ANDROID_GIT_TOKEN in your shell environment when running gradle
 */
class AthleticReleaseBot(private val gitToken: String) {

    companion object {
        private const val ATHLETIC_ANDROID_GIT_TOKEN = "ATHLETIC_ANDROID_GIT_TOKEN"
        private const val REPO_URL = "git@github.com:TheAthletic/android.git"

        @JvmStatic
        fun main(args: Array<String>) {
            val token = System.getenv(ATHLETIC_ANDROID_GIT_TOKEN)
            if (token == null) {
                println("Please set: ATHLETIC_ANDROID_GIT_TOKEN in your environment")
                exitProcess(-1)
            }
            val actions = when (args[0]) {
                "cutStable" -> listOf(
                    CreateStableBranch(), CreateGHBeta(), CreateMinorVersionBumpPR()
                )
                "createMergeback" -> listOf(CreateMergebackPR())
                "finalizeRelease" -> listOf(CreateReleaseTag())
                "validateAuthentication" -> listOf(ValidateAuthentication())
                else -> emptyList()
            }
            AthleticReleaseBot(token).run(actions)
        }
    }

    fun run(actions: List<GitAction>) {
        val sshDir = File(FS.DETECTED.userHome(), "/.ssh")
        val apacheSessionFactory =
            SshdSessionFactoryBuilder().setPreferredAuthentications("publickey")
                .setHomeDirectory(FS.DETECTED.userHome()).setSshDirectory(sshDir).build(null)

        val git = Git.cloneRepository().setURI(REPO_URL).setTransportConfigCallback { transport ->
            (transport as SshTransport).sshSessionFactory = apacheSessionFactory
        }.call()

        val githubRepository = GithubRepository(gitToken)

        try {
            actions.forEach { it.run(git, githubRepository) }
        } catch (e: Exception) {
            throw e
        } finally {
            git.close()
            FileUtils.deleteDirectory(git.repository.directory.parentFile)
        }
    }
}