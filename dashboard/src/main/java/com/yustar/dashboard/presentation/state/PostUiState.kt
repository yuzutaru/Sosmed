package com.yustar.dashboard.presentation.state

import com.yustar.dashboard.domain.model.AlbumItem
import com.yustar.dashboard.domain.model.LocalMedia

/**
 * Created by Yustar Pramudana on 26/03/26.
 */

data class PostUiState(
    val isLoading: Boolean = false,
    val error: String = "",
    val localImages: List<LocalMedia> = emptyList(),
    val selectedImage: LocalMedia? = null,
    val selectedAlbum: AlbumItem? = null,
    val selectedTab: Int = 0,
    val tabs: List<String> = emptyList(),
    val showAlbumSelection: Boolean = false,
    val albums: List<AlbumItem> = emptyList()
)