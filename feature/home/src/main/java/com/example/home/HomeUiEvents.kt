package com.example.home

sealed class HomeUiEvents {
    data class onRefresh(val category:String): HomeUiEvents()
    data class onPageChange(val page:String, val category:String): HomeUiEvents()
}