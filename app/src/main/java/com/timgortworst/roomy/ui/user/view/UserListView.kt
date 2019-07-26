package com.timgortworst.roomy.ui.user.view

import com.timgortworst.roomy.model.User

interface UserListView {
    fun showOrHideFab(condition: Boolean)
    fun showContextMenuFor(user: User)
    fun share(householdId: String)
    fun presentEditedUser(user: User)
    fun presentDeletedUser(user: User)
    fun presentAddedUser(user: User)
    fun setLoading(loading: Boolean)
}
