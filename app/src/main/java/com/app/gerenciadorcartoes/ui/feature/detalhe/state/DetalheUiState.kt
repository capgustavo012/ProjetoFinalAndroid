package com.app.gerenciadorcartoes.ui.feature.detalhe.state

import com.app.gerenciadorcartoes.model.CartaoDetalhe

data class DetalheUiState(
    val detalhe    : CartaoDetalhe = CartaoDetalhe(),
    val carregando : Boolean       = false,
    val erro       : String?       = null,
)
