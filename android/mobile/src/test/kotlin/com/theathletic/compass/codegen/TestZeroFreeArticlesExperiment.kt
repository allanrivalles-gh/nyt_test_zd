package com.theathletic.compass.codegen

import com.theathletic.BuildConfig
import com.theathletic.compass.CompassClient
import com.theathletic.compass.Experiment
import com.theathletic.compass.FieldResponse
import com.theathletic.compass.Variant
import com.theathletic.debugtools.DebugPreferences
import com.theathletic.utility.logging.ICrashLogHandler
import com.theathletic.user.UserManager
import kotlin.Boolean
import kotlin.Long
import kotlin.String
import kotlin.collections.Map

/**
 * This class was generated by the 'compass' plugin
 */
class TestZeroFreeArticlesExperiment(
    override var name: String = "zero free articles experiment",
    override var exists: Boolean = false,
    override var activeVariant: Variant? = ZeroFreeArticlesExperimentVariant.CTRL(),
    override val crashLogHandler: ICrashLogHandler,
    override val debugPreferences: DebugPreferences
) : Experiment() {
  /**
   * Returns the active variant indicated by the server config, or CTRL if not able to obtain a
   * config
   */
  val variant: ZeroFreeArticlesExperimentVariant
    get() {
      if (client == null || client?.configState?.get() != CompassClient.ConfigState.POPULATED) {
        val exception = IllegalAccessException("""The compass client configState must be POPULATED
          in order to reference a variant""")
        if (BuildConfig.DEBUG) {
          throw exception
        } else {
          crashLogHandler.logException(exception)
        }
      }

      if (debugPreferences.compassSelectedVariantMap[name] != null) {
        return when(debugPreferences.compassSelectedVariantMap[name]) {
          "A" -> ZeroFreeArticlesExperimentVariant.A()
          "B" -> ZeroFreeArticlesExperimentVariant.B()
          else -> ZeroFreeArticlesExperimentVariant.CTRL()
        }
      }

      if (exists) {
        return activeVariant as ZeroFreeArticlesExperimentVariant
      }

      return ZeroFreeArticlesExperimentVariant.CTRL()
    }

  override fun copy(activeVariant: Variant, exists: Boolean) =
      TestZeroFreeArticlesExperiment(activeVariant = activeVariant, exists = exists, crashLogHandler
      = crashLogHandler, debugPreferences = debugPreferences)

  fun postExposure(userId: Long = UserManager.getCurrentUserId()) {
    client?.postExposure(this, userId)
  }

  sealed class ZeroFreeArticlesExperimentVariant : Variant {
    data class CTRL(
      override val _name: String = "CTRL",
      var product: String = "com.theathletic.annual",
      var message: String = "there is no cow level"
    ) : ZeroFreeArticlesExperimentVariant() {
      override fun populateFromFieldMap(fieldMap: Map<String, FieldResponse>,
          crashLogHandler: ICrashLogHandler) = CTRL(
      product = fieldMap["product"]?.value ?: product,
      message = fieldMap["message"]?.value ?: message)
    }

    data class A(
      override val _name: String = "A",
      var product: String = "com.theathletic.semi-monthly",
      var message: String = "there is no cow level"
    ) : ZeroFreeArticlesExperimentVariant() {
      override fun populateFromFieldMap(fieldMap: Map<String, FieldResponse>,
          crashLogHandler: ICrashLogHandler) = A(
      product = fieldMap["product"]?.value ?: product,
      message = fieldMap["message"]?.value ?: message)
    }

    data class B(
      override val _name: String = "B",
      var product: String = "com.theathletic.semi-monthly",
      var message: String = "there is no cow level"
    ) : ZeroFreeArticlesExperimentVariant() {
      override fun populateFromFieldMap(fieldMap: Map<String, FieldResponse>,
          crashLogHandler: ICrashLogHandler) = B(
      product = fieldMap["product"]?.value ?: product,
      message = fieldMap["message"]?.value ?: message)
    }
  }
}