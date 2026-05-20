package com.app.gerenciadorcartoes.repository

import com.app.gerenciadorcartoes.model.Cartao
import com.app.gerenciadorcartoes.model.CartaoDetalhe
import com.app.gerenciadorcartoes.model.InstituicaoFinanceira
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartaoDetalheRepositoryImpl @Inject constructor(
    private val cartaoRepository: CartaoRepository,
) : CartaoDetalheRepository {

    override fun observarDetalhePorId(id: Long): Flow<CartaoDetalhe?> =
        cartaoRepository.observarPorId(id).map { cartao ->
            cartao?.let(::montarDetalheMockado)
        }

    // Ponto de troca futura: substituir este mock por dados reais de fatura/limite.
    private fun montarDetalheMockado(cartao: Cartao): CartaoDetalhe {
        val percentualMockado = percentualMockado(cartao)
        return CartaoDetalhe(
            cartao          = cartao,
            instituicao     = InstituicaoFinanceira(nome = instituicaoPorTemplate(cartao.template)),
            limiteUtilizado = cartao.limite * percentualMockado,
        )
    }

    private fun percentualMockado(cartao: Cartao): Double {
        val referencia = cartao.id.takeIf { it > 0L }
            ?: cartao.finalNumero.toLongOrNull()
            ?: 0L

        return when ((referencia % 3L).toInt()) {
            0    -> 0.34
            1    -> 0.67
            else -> 0.86
        }
    }

    private fun instituicaoPorTemplate(template: String): String =
        when (template) {
            "bradesco" -> "Bradesco"
            "itau"     -> "Itaú"
            "nubank"   -> "Nubank"
            "inter"    -> "Inter"
            "c6bank"   -> "C6 Bank"
            else       -> "Cartão"
        }
}
