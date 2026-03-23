package com.yustar.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.yustar.dashboard.domain.model.Post
import com.yustar.dashboard.domain.usecase.GetFeedsUseCase
import com.yustar.dashboard.presentation.state.FeedsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Created by Yustar Pramudana on 22/03/26.
 */

@HiltViewModel
class FeedsViewModel @Inject constructor(
    getFeedsUseCase: GetFeedsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedsUiState())
    val uiState: StateFlow<FeedsUiState> = _uiState.asStateFlow()

    val feeds: Flow<PagingData<Post>> = getFeedsUseCase()
        .cachedIn(viewModelScope)

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = "")
    }
}
