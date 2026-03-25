package com.yustar.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yustar.dashboard.domain.model.LocalMedia
import com.yustar.dashboard.domain.usecase.GetLocalImagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val getLocalImagesUseCase: GetLocalImagesUseCase
) : ViewModel() {

    private val _localImages = MutableStateFlow<List<LocalMedia>>(emptyList())
    val localImages: StateFlow<List<LocalMedia>> = _localImages.asStateFlow()

    private val _selectedImage = MutableStateFlow<LocalMedia?>(null)
    val selectedImage: StateFlow<LocalMedia?> = _selectedImage.asStateFlow()

    init {
        loadLocalImages()
    }

    fun loadLocalImages() {
        viewModelScope.launch {
            getLocalImagesUseCase().collect { images ->
                _localImages.value = images
                if (images.isNotEmpty() && _selectedImage.value == null) {
                    _selectedImage.value = images.first()
                }
            }
        }
    }

    fun onImageSelected(media: LocalMedia) {
        _selectedImage.value = media
    }
}
