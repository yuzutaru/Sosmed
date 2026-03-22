package com.yustar.dashboard.domain.usecase

import androidx.paging.PagingData
import com.yustar.dashboard.domain.model.Post
import com.yustar.dashboard.domain.repository.FeedsRepository
import kotlinx.coroutines.flow.Flow

/**
 * Created by Yustar Pramudana on 22/03/26.
 */

class GetFeedsUseCase(
    private val repository: FeedsRepository
) {
    operator fun invoke(): Flow<PagingData<Post>> {
        return repository.getFeedsPaged()
    }
}
