package com.timgortworst.roomy.ui.settings.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.settings.view.SettingsView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers

class SettingsPresenter(
        val view: SettingsView,
        val userRepository: UserRepository
) : DefaultLifecycleObserver {

    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun deleteUser(uid: String) {

        val user = FirebaseAuth.getInstance().currentUser

        user?.delete()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                       // activity!!.showToast("User account deleted.")
                    }
                }
    }
}