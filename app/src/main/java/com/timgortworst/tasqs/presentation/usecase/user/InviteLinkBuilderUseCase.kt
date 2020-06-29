package com.timgortworst.tasqs.presentation.usecase.user

import android.net.Uri
import com.timgortworst.tasqs.domain.usecase.UseCase

interface InviteLinkBuilderUseCase : UseCase<Uri, Unit>