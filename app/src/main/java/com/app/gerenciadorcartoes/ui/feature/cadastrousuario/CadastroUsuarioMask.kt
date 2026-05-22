package com.app.gerenciadorcartoes.ui.feature.cadastrousuario

private const val CPF_MAX_DIGITOS = 11
private const val CEP_MAX_DIGITOS = 8
private const val NUMERO_MAX_DIGITOS = 8
private const val UF_MAX_CHARS = 2

// Regex estrutural mínima para bloqueio de avanço na UI.
// Validação completa (TLD, domínio real) fica no ViewModel.
private val EMAIL_ESTRUTURAL = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}$")

fun onlyDigits(text: String): String = text.filter { it.isDigit() }

/**
 * Verificação estrutural de e-mail para uso na UI (gate de avanço de etapa).
 * Retorna true somente quando o valor possui formato user@domain.tld.
 * A validação completa de formato permanece no ViewModel.
 */
fun emailEstruturalmenteValido(email: String): Boolean =
    email.isNotBlank() && EMAIL_ESTRUTURAL.matches(email)

fun formatCpf(text: String): String {
    val digits = onlyDigits(text).take(CPF_MAX_DIGITOS)
    return buildString {
        digits.forEachIndexed { index, c ->
            append(c)
            if (index == 2 || index == 5) append('.')
            if (index == 8) append('-')
        }
    }
}

fun formatCep(text: String): String {
    val digits = onlyDigits(text).take(CEP_MAX_DIGITOS)
    return buildString {
        digits.forEachIndexed { index, c ->
            append(c)
            if (index == 4) append('-')
        }
    }
}

fun formatNumero(text: String): String = onlyDigits(text).take(NUMERO_MAX_DIGITOS)

fun formatUf(text: String): String = text
    .filter { it.isLetter() }
    .uppercase()
    .take(UF_MAX_CHARS)

