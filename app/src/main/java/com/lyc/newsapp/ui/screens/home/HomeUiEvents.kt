package com.lyc.newsapp.ui.screens.home

import com.lyc.newsapp.domain.model.Category

sealed class HomeUiEvents {
    data class onRefresh(val category:String): HomeUiEvents()
    data class onPageChange(val page:String, val category:String): HomeUiEvents()
}