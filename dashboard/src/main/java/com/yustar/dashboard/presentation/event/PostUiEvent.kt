package com.yustar.dashboard.presentation.event

import com.yustar.dashboard.domain.model.AlbumItem
import com.yustar.dashboard.domain.model.LocalMedia

/**
 * Created by Yustar Pramudana on 26/03/26.
 */

sealed class PostUiEvent {
    data class ShowAlbumSelection(val show: Boolean) : PostUiEvent()
    data class OnImageSelected(val image: LocalMedia) : PostUiEvent()
    data class OnAlbumSelected(val album: AlbumItem) : PostUiEvent()
    data class OnTabSelected(val tab: Int) : PostUiEvent()
}