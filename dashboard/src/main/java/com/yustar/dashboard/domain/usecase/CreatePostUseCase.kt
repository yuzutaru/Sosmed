package com.yustar.dashboard.domain.usecase

import com.yustar.core.data.remote.model.Resource
import com.yustar.dashboard.data.remote.model.MediaDto
import com.yustar.dashboard.data.repository.FeedsRepository
import com.yustar.dashboard.domain.model.PostMedia
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
        return try {
            val mediaDto = media.mapIndexed { index, postMedia ->
                MediaDto(
                    media_url = postMedia.url,
                    media_type = postMedia.mediaType ?: "",
                    position = index
                )
            }
            repository.createPost(caption, location, mediaDto)
            Resource.success(Unit)
        } catch (e: Exception) {
            Resource.error(null, e.localizedMessage)
        }
    }
}
