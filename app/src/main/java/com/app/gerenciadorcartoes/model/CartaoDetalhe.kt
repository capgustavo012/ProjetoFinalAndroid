package com.app.gerenciadorcartoes.model

data class CartaoDetalhe(
    val cartao          : Cartao = Cartao(),
    val instituicao     : InstituicaoFinanceira = InstituicaoFinanceira(),
    val limiteUtilizado : Double = 0.0,
) {
    val limiteTotal: Double
        get() = cartao.limite.takeIf { it > 0.0 } ?: LIMITE_PADRAO

    val limiteDisponivel: Double
        get() = (limiteTotal - limiteUtilizado).coerceAtLeast(0.0)

    val percentualUso: Float
        get() = when {
            limiteTotal <= 0.0 -> 0f
            else               -> (limiteUtilizado / limiteTotal).coerceIn(0.0, 1.0).toFloat()
        }

    private companion object {
        const val LIMITE_PADRAO = 1_000.0
    }
}
