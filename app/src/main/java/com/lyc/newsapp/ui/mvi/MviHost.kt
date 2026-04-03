package com.lyc.newsapp.ui.mvi

import kotlinx.coroutines.flow.StateFlow

/**
 * MVI基础框架
 */
interface MviHost<UiState : Any, Intent : Any> {
    val uiState: StateFlow<UiState>
    fun dispatch(intent: Intent)
}
