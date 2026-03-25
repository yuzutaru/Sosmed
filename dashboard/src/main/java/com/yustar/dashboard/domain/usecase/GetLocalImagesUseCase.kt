package com.yustar.dashboard.domain.usecase

import com.yustar.dashboard.domain.model.LocalMedia
import com.yustar.dashboard.domain.repository.FeedsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocalImagesUseCase @Inject constructor(
    private val repository: FeedsRepository
) {
    operator fun invoke(): Flow<List<LocalMedia>> = repository.getLocalImages()
}
