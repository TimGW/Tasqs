package com.timgortworst.roomy.ui.main.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.timgortworst.roomy.R
import com.timgortworst.roomy.ui.agenda.ui.AgendaFragment
import com.timgortworst.roomy.ui.eventcategory.view.EventCategoryFragment
import com.timgortworst.roomy.ui.main.presenter.MainPresenter
import com.timgortworst.roomy.ui.users.view.UsersActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : AppCompatActivity(), HasSupportFragmentInjector, MainView {
    @Inject
    lateinit var presenter: MainPresenter

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_appbar.replaceMenu(R.menu.bottom_appbar_menu)

        bottom_appbar.setOnMenuItemClickListener { item ->
            presenter.handleMenuItemClick(item.itemId)
            true
        }

        presentAgendaFragment()
    }

    override fun presentText(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun fragmentToReplace(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit()
    }

    override fun presentAgendaFragment() {
        fragmentToReplace(AgendaFragment.newInstance())
    }

    override fun presentTasksFragment() {
        fragmentToReplace(EventCategoryFragment.newInstance())
    }

    override fun presentProfileActivity() {
        UsersActivity.start(this)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }
}
