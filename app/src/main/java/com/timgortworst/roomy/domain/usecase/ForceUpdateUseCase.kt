package com.timgortworst.roomy.domain.usecase

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.timgortworst.roomy.BuildConfig

class ForceUpdateUseCase(private val onUpdateNeededListener: OnUpdateNeededListener?) {
    private var currentVersion: String = "1.0.0" // fallback

    interface OnUpdateNeededListener {
        fun onUpdateNeeded(updateUrl: String)
        fun onUpdateRecommended(updateUrl: String)
        fun noUpdateNeeded()
    }

    fun check(remoteConfig: FirebaseRemoteConfig, appVersion: String) {
        currentVersion = stripVersionNameSuffix(appVersion)
        val requiredVersion = stripVersionNameSuffix(remoteConfig.getString(KEY_CURRENT_REQUIRED_VERSION))
        val recommendedVersion = stripVersionNameSuffix(remoteConfig.getString(KEY_CURRENT_RECOMMENDED_VERSION))

        val requiredVersionInt: Int = requiredVersion.replace(".", "", ignoreCase = true).toInt()
        val recommendedVersionInt: Int = recommendedVersion.replace(".", "", ignoreCase = true).toInt()
        val currentVersionInt: Int = currentVersion.replace(".", "", ignoreCase = true).toInt()

        val updateUrl = remoteConfig.getString(KEY_UPDATE_URL)

        when {
            requiredVersionInt > currentVersionInt -> onUpdateNeededListener?.onUpdateNeeded(updateUrl)
            recommendedVersionInt > currentVersionInt -> onUpdateNeededListener?.onUpdateRecommended(updateUrl)
            else -> onUpdateNeededListener?.noUpdateNeeded()
        }
    }

    private fun stripVersionNameSuffix(version: String?): String {
        return version?.replace("[a-zA-Z]|-".toRegex(), "") ?: currentVersion
    }

    class Builder(private val remoteConfig: FirebaseRemoteConfig) {
        private var onUpdateNeededListener: OnUpdateNeededListener? = null

        fun onUpdateNeeded(onUpdateNeededListener: OnUpdateNeededListener): Builder {
            this.onUpdateNeededListener = onUpdateNeededListener
            return this
        }

        fun build(): ForceUpdateUseCase {
            return ForceUpdateUseCase(onUpdateNeededListener)
        }

        fun check(appVersion: String): ForceUpdateUseCase {
            val forceUpdateChecker = build()
            forceUpdateChecker.check(remoteConfig, appVersion)

            return forceUpdateChecker
        }
    }

    companion object {
        const val KEY_CURRENT_REQUIRED_VERSION = BuildConfig.FORCE_UPDATE_KEY
        const val KEY_CURRENT_RECOMMENDED_VERSION = BuildConfig.RECOMMENDED_UPDATE_KEY
        const val KEY_UPDATE_URL = "store_url_android"

        fun with(remoteConfig: FirebaseRemoteConfig): Builder {
            return Builder(remoteConfig)
        }
    }
}
