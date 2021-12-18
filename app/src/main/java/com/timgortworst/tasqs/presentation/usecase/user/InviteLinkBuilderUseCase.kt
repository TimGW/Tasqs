package com.timgortworst.tasqs.presentation.usecase.user

import android.net.Uri
import com.timgortworst.tasqs.domain.usecase.None
import com.timgortworst.tasqs.domain.usecase.FlowUseCase

interface InviteLinkBuilderUseCase : FlowUseCase<Uri, None>