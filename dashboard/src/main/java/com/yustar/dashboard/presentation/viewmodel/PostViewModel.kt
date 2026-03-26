package com.yustar.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yustar.dashboard.domain.usecase.GetLocalAlbumsUseCase
import com.yustar.dashboard.domain.usecase.GetLocalImagesUseCase
import com.yustar.dashboard.presentation.event.PostUiEvent
import com.yustar.dashboard.presentation.state.PostUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val getLocalImagesUseCase: GetLocalImagesUseCase,
    private val getLocalAlbumsUseCase: GetLocalAlbumsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PostUiState())
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()

    init {
        loadLocalImages()
        loadLocalAlbums()
    }

    fun setTabs(tabs: List<String>) {
        _uiState.value = _uiState.value.copy(tabs = tabs)
    }

    fun loadLocalImages(bucketId: String? = null) {
        viewModelScope.launch {
            getLocalImagesUseCase(bucketId).collect { images ->
                _uiState.update { it.copy(localImages = images) }
                if (images.isNotEmpty() && _uiState.value.selectedImage == null) {
                    _uiState.update { it.copy(selectedImage = images.first()) }
                }
            }
        }
    }

    fun loadLocalAlbums() {
        viewModelScope.launch {
            getLocalAlbumsUseCase().collect { albums ->
                _uiState.update { it.copy(albums = albums) }
            }
        }
    }

    fun onEvent(event: PostUiEvent) {
        when (event) {
            is PostUiEvent.ShowAlbumSelection -> _uiState.update { it.copy(showAlbumSelection = event.show) }
            is PostUiEvent.OnAlbumSelected -> _uiState.update { it.copy(selectedAlbum = event.album) }
            is PostUiEvent.OnImageSelected -> _uiState.update { it.copy(selectedImage = event.image) }
            is PostUiEvent.OnTabSelected -> _uiState.update { it.copy(selectedTab = event.tab) }
        }
    }
}
