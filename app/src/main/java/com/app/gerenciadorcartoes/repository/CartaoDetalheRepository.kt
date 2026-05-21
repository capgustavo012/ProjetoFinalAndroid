package com.app.gerenciadorcartoes.repository

import com.app.gerenciadorcartoes.model.CartaoDetalhe
import kotlinx.coroutines.flow.Flow

interface CartaoDetalheRepository {
    fun observarDetalhePorId(id: Long): Flow<CartaoDetalhe?>
}
