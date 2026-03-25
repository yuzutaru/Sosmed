package com.yustar.dashboard.domain.usecase

import com.yustar.core.data.remote.model.Resource
import com.yustar.dashboard.domain.model.PostMedia
import com.yustar.dashboard.domain.repository.FeedsRepository
import javax.inject.Inject

/**
 * Created by Yustar Pramudana on 22/03/26.
 */

class CreatePostUseCase @Inject constructor(
    private val repository: FeedsRepository
) {
    suspend operator fun invoke(
        caption: String,
        location: String,
        media: List<PostMedia>
    ): Resource<Unit> {
        return repository.createPost(caption, location, media)
    }
}
