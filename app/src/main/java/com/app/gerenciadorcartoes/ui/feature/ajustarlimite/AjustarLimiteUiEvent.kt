package com.app.gerenciadorcartoes.ui.feature.ajustarlimite

sealed interface AjustarLimiteUiEvent {
    data object NavigateBack                          : AjustarLimiteUiEvent
    data class  MostrarMensagem(val mensagem: String) : AjustarLimiteUiEvent
}
