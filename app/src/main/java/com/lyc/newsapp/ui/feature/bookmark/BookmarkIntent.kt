package com.lyc.newsapp.ui.feature.bookmark

/**
 * 收藏列表用户意图（MVI）。
 */
sealed class BookmarkIntent {
    /** 拉取或刷新收藏列表 */
    object Refresh : BookmarkIntent()

    /** 页面重新可见时刷新（与 Refresh 行为一致，语义区分便于埋点/单测） */
    object ScreenBecameVisible : BookmarkIntent()

    data class DeleteFavorite(val id: String) : BookmarkIntent()
    object ClearAll : BookmarkIntent()
}
