package com.theathletic.compass.codegen

import com.theathletic.compass.Experiment
import com.theathletic.compass.ICompassExperiment
import com.theathletic.compass.Variant
import com.theathletic.debugtools.DebugPreferences
import com.theathletic.utility.logging.ICrashLogHandler
import kotlin.String
import kotlin.collections.HashMap
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class VariantKey(
  val expName: String,
  val variantId: String
)

/**
 * This class was generated by the 'compass' plugin
 */
object CompassTestExperiment : ICompassExperiment(), KoinComponent {
  val crashLogHandler: ICrashLogHandler by inject<ICrashLogHandler>()

  val debugPreferences: DebugPreferences by inject<DebugPreferences>()

  val ADS_V1_EXPERIMENT: TestAdsV1Experiment
    get() = experimentMap["Ads v1"]!! as TestAdsV1Experiment

  val PUSH_OPT_IN_PRE_PROMPT: TestPushOptInPrePrompt
    get() = experimentMap["Push opt-in pre prompt"]!! as TestPushOptInPrePrompt

  val NO_TEAM_LEAGUE_SELECTION_IN_OB: TestNoTeamLeagueSelectionInOB
    get() = experimentMap["No team/league selection in OB"]!! as TestNoTeamLeagueSelectionInOB

  val EMPTY_FIELD_EXPERIMENT: TestEmptyFieldExperiment
    get() = experimentMap["empty field experiment"]!! as TestEmptyFieldExperiment

  val PROFILE: TestProfile
    get() = experimentMap["profile"]!! as TestProfile

  val ZERO_FREE_ARTICLES_EXPERIMENT: TestZeroFreeArticlesExperiment
    get() = experimentMap["zero free articles experiment"]!! as TestZeroFreeArticlesExperiment

  val CLASS: TestClass
    get() = experimentMap["class"]!! as TestClass

    private val defaultExperimentMap: HashMap<String, Experiment> = hashMapOf(
        "Ads v1" to TestAdsV1Experiment(crashLogHandler = crashLogHandler,
            debugPreferences = debugPreferences),
        "Push opt-in pre prompt" to TestPushOptInPrePrompt(crashLogHandler = crashLogHandler,
            debugPreferences = debugPreferences),
        "No team/league selection in OB" to TestNoTeamLeagueSelectionInOB(crashLogHandler =
        crashLogHandler, debugPreferences = debugPreferences),
        "empty field experiment" to TestEmptyFieldExperiment(crashLogHandler = crashLogHandler,
            debugPreferences = debugPreferences),
        "profile" to TestProfile(crashLogHandler = crashLogHandler, debugPreferences =
        debugPreferences),
        "zero free articles experiment" to TestZeroFreeArticlesExperiment(crashLogHandler =
        crashLogHandler, debugPreferences = debugPreferences),
        "class" to TestClass(crashLogHandler = crashLogHandler, debugPreferences = debugPreferences)
    )

  override var experimentMap: HashMap<String, Experiment> = defaultExperimentMap

  override val variantMap: HashMap<VariantKey, Variant> = hashMapOf(
    VariantKey(EMPTY_FIELD_EXPERIMENT.name,
        TestEmptyFieldExperiment.EmptyFieldExperimentVariant.A()._name) to
        TestEmptyFieldExperiment.EmptyFieldExperimentVariant.A(),
    VariantKey(EMPTY_FIELD_EXPERIMENT.name,
        TestEmptyFieldExperiment.EmptyFieldExperimentVariant.B()._name) to
        TestEmptyFieldExperiment.EmptyFieldExperimentVariant.B(),
    VariantKey(EMPTY_FIELD_EXPERIMENT.name,
        TestEmptyFieldExperiment.EmptyFieldExperimentVariant.CTRL()._name) to
        TestEmptyFieldExperiment.EmptyFieldExperimentVariant.CTRL(),
    VariantKey(EMPTY_FIELD_EXPERIMENT.name,
        TestEmptyFieldExperiment.EmptyFieldExperimentVariant.A()._name) to
        TestEmptyFieldExperiment.EmptyFieldExperimentVariant.A(),
    VariantKey(EMPTY_FIELD_EXPERIMENT.name,
        TestEmptyFieldExperiment.EmptyFieldExperimentVariant.B()._name) to
        TestEmptyFieldExperiment.EmptyFieldExperimentVariant.B(),
    VariantKey(EMPTY_FIELD_EXPERIMENT.name,
        TestEmptyFieldExperiment.EmptyFieldExperimentVariant.CTRL()._name) to
        TestEmptyFieldExperiment.EmptyFieldExperimentVariant.CTRL(),
    VariantKey(EMPTY_FIELD_EXPERIMENT.name,
        TestEmptyFieldExperiment.EmptyFieldExperimentVariant.A()._name) to
        TestEmptyFieldExperiment.EmptyFieldExperimentVariant.A(),
    VariantKey(EMPTY_FIELD_EXPERIMENT.name,
        TestEmptyFieldExperiment.EmptyFieldExperimentVariant.B()._name) to
        TestEmptyFieldExperiment.EmptyFieldExperimentVariant.B(),
    VariantKey(EMPTY_FIELD_EXPERIMENT.name,
        TestEmptyFieldExperiment.EmptyFieldExperimentVariant.CTRL()._name) to
        TestEmptyFieldExperiment.EmptyFieldExperimentVariant.CTRL(),
    VariantKey(PROFILE.name, TestProfile.ProfileVariant.A()._name) to
        TestProfile.ProfileVariant.A(),
    VariantKey(PROFILE.name, TestProfile.ProfileVariant.B()._name) to
        TestProfile.ProfileVariant.B(),
    VariantKey(PROFILE.name, TestProfile.ProfileVariant.CTRL()._name) to
        TestProfile.ProfileVariant.CTRL(),
    VariantKey(ZERO_FREE_ARTICLES_EXPERIMENT.name,
        TestZeroFreeArticlesExperiment.ZeroFreeArticlesExperimentVariant.A()._name) to
        TestZeroFreeArticlesExperiment.ZeroFreeArticlesExperimentVariant.A(),
    VariantKey(ZERO_FREE_ARTICLES_EXPERIMENT.name,
        TestZeroFreeArticlesExperiment.ZeroFreeArticlesExperimentVariant.B()._name) to
        TestZeroFreeArticlesExperiment.ZeroFreeArticlesExperimentVariant.B(),
    VariantKey(ZERO_FREE_ARTICLES_EXPERIMENT.name,
        TestZeroFreeArticlesExperiment.ZeroFreeArticlesExperimentVariant.CTRL()._name) to
        TestZeroFreeArticlesExperiment.ZeroFreeArticlesExperimentVariant.CTRL(),
    VariantKey(ZERO_FREE_ARTICLES_EXPERIMENT.name,
        TestZeroFreeArticlesExperiment.ZeroFreeArticlesExperimentVariant.A()._name) to
        TestZeroFreeArticlesExperiment.ZeroFreeArticlesExperimentVariant.A(),
    VariantKey(ZERO_FREE_ARTICLES_EXPERIMENT.name,
        TestZeroFreeArticlesExperiment.ZeroFreeArticlesExperimentVariant.B()._name) to
        TestZeroFreeArticlesExperiment.ZeroFreeArticlesExperimentVariant.B(),
    VariantKey(ZERO_FREE_ARTICLES_EXPERIMENT.name,
        TestZeroFreeArticlesExperiment.ZeroFreeArticlesExperimentVariant.CTRL()._name) to
        TestZeroFreeArticlesExperiment.ZeroFreeArticlesExperimentVariant.CTRL(),
      VariantKey(ADS_V1_EXPERIMENT.name,
          TestAdsV1Experiment.AdsV1ExperimentVariant.A()._name) to
              TestAdsV1Experiment.AdsV1ExperimentVariant.A(),
      VariantKey(ADS_V1_EXPERIMENT.name,
          TestAdsV1Experiment.AdsV1ExperimentVariant.CTRL()._name) to
              TestAdsV1Experiment.AdsV1ExperimentVariant.CTRL()
  )

  fun resetMap() {
      experimentMap = defaultExperimentMap
  }
}
