package com.yustar.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.yustar.dashboard.domain.model.Post
import com.yustar.dashboard.domain.usecase.GetFeedsUseCase
import kotlinx.coroutines.flow.Flow

/**
 * Created by Yustar Pramudana on 22/03/26.
 */

class FeedsViewModel(
    getFeedsUseCase: GetFeedsUseCase
) : ViewModel() {

    val feeds: Flow<PagingData<Post>> = getFeedsUseCase()
        .cachedIn(viewModelScope)
}
