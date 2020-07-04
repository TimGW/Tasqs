package com.timgortworst.tasqs.domain.usecase.splash

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.timgortworst.tasqs.BuildConfig
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.presentation.base.model.UpdateAction
import com.timgortworst.tasqs.presentation.usecase.splash.ForcedUpdateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ForcedUpdateUseCaseImpl : ForcedUpdateUseCase {
    private val remoteConfig = FirebaseRemoteConfig.getInstance()
    private var currentVersion: String = "1.0.0" // fallback

    data class Params(val appVersion: String)

    override fun execute(params: Params) = flow {
        currentVersion = stripVersionNameSuffix(params.appVersion)
        val requiredVersion = stripVersionNameSuffix(remoteConfig.getString(
            KEY_CURRENT_REQUIRED_VERSION
        ))
        val recommendedVersion = stripVersionNameSuffix(remoteConfig.getString(
            KEY_CURRENT_RECOMMENDED_VERSION
        ))

        val requiredVersionInt: Int = stripDots(requiredVersion).toInt()
        val recommendedVersionInt: Int = stripDots(recommendedVersion).toInt()
        val currentVersionInt: Int = stripDots(currentVersion).toInt()

        val updateUrl = remoteConfig.getString(KEY_UPDATE_URL)

        when {
            requiredVersionInt > currentVersionInt -> emit(Response.Success(UpdateAction.required(updateUrl)))
            recommendedVersionInt > currentVersionInt -> emit(Response.Success(UpdateAction.recommended(updateUrl)))
            else -> emit(Response.Success(UpdateAction.none))
        }
    }.flowOn(Dispatchers.Default)

    private fun stripDots(input: String) = input.replace(".", "", ignoreCase = true)

    private fun stripVersionNameSuffix(version: String?): String {
        return version?.replace("[a-zA-Z]|-".toRegex(), "") ?: currentVersion
    }

    companion object {
        const val KEY_CURRENT_REQUIRED_VERSION = BuildConfig.FORCE_UPDATE_KEY
        const val KEY_CURRENT_RECOMMENDED_VERSION = BuildConfig.RECOMMENDED_UPDATE_KEY
        const val KEY_UPDATE_URL = "store_url_android"
    }
}
