package com.yustar.dashboard.domain.usecase

import com.yustar.dashboard.domain.model.AlbumItem
import com.yustar.dashboard.domain.repository.FeedsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocalAlbumsUseCase @Inject constructor(
    private val repository: FeedsRepository
) {
    operator fun invoke(): Flow<List<AlbumItem>> = repository.getLocalAlbums()
}
