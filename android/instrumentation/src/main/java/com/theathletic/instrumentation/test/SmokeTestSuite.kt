package com.theathletic.instrumentation.test

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    ApplicationStartScenario::class,
    LoginScenario::class,
    BasicFeedScenario::class
)
class SmokeTestSuite