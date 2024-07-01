package com.theathletic.worker

import java.util.concurrent.Executor

class SynchronousExecutor : Executor {
    override fun execute(command: Runnable) {
        command.run()
    }
}