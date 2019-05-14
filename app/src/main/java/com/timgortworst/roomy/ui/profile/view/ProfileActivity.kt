package com.timgortworst.roomy.ui.profile.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.timgortworst.roomy.R
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.ui.base.view.BaseAuthActivity
import com.timgortworst.roomy.ui.customview.CircleTransform
import com.timgortworst.roomy.ui.users.view.UsersActivity
import com.timgortworst.roomy.ui.profile.presenter.ProfilePresenter
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject


class ProfileActivity : BaseAuthActivity(), ProfileView {
    @Inject
    lateinit var presenter: ProfilePresenter

    private lateinit var user: User

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, ProfileActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        presenter.getCurrentUser()

        profile_task_history.setOnClickListener { Toast.makeText(this, "Show history", Toast.LENGTH_LONG).show() }
        profile_roommates.setOnClickListener {
            UsersActivity.start(this)
        }
        profile_logout_button.setOnClickListener {
            logout()
        }

        Picasso.get().load(getProfileImage()).transform(CircleTransform()).into(profile_image)
    }


    override fun presentUser(user: User) {
        this.user = user

        profile_name.text = user.name
        profile_email.text = user.email
        profile_points.setStatValue(user.totalPoints.toString())
    }
}
