package com.app.gerenciadorcartoes.ui.feature.ajustarlimite

sealed interface AjustarLimiteEvent {
    data object Voltar                                  : AjustarLimiteEvent
    data object Salvar                                  : AjustarLimiteEvent
    data object ConfirmarSalvar                         : AjustarLimiteEvent
    data object CancelarConfirmacao                     : AjustarLimiteEvent
    data class  NovoLimiteAlterado(val valor: Double)   : AjustarLimiteEvent
}
