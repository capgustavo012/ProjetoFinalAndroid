package com.app.gerenciadorcartoes.ui.feature.cadastrousuario

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CadastroUsuarioMaskTest {

    @Test
    fun `formatCpf should apply cpf mask and cap digits`() {
        assertEquals("123.456.789-01", formatCpf("12345678901"))
        assertEquals("123.456.789-01", formatCpf("123.456.789-01xx"))
    }

    @Test
    fun `formatCep should apply cep mask and cap digits`() {
        assertEquals("01310-100", formatCep("01310100"))
        assertEquals("01310-100", formatCep("01310-100-extra"))
    }

    @Test
    fun `formatNumero should keep only digits as string`() {
        assertEquals("1234", formatNumero("12A-34"))
        assertEquals("12345678", formatNumero("1234567890"))
    }

    @Test
    fun `formatUf should keep two uppercase letters`() {
        assertEquals("SP", formatUf("sp"))
        assertEquals("RJ", formatUf("rj9"))
        assertEquals("AC", formatUf("acr"))
    }

    // ── emailEstruturalmenteValido ─────────────────────────────────────────────

    @Test
    fun `emailEstruturalmenteValido should return true for valid emails`() {
        assertTrue(emailEstruturalmenteValido("user@example.com"))
        assertTrue(emailEstruturalmenteValido("user.name+tag@sub.domain.org"))
        assertTrue(emailEstruturalmenteValido("a@b.io"))
    }

    @Test
    fun `emailEstruturalmenteValido should return false for missing at-sign`() {
        assertFalse(emailEstruturalmenteValido("gsgf"))
        assertFalse(emailEstruturalmenteValido("userexample.com"))
    }

    @Test
    fun `emailEstruturalmenteValido should return false for missing domain or TLD`() {
        assertFalse(emailEstruturalmenteValido("user@"))
        assertFalse(emailEstruturalmenteValido("user@domain"))
        assertFalse(emailEstruturalmenteValido("user@domain."))
    }

    @Test
    fun `emailEstruturalmenteValido should return false for blank input`() {
        assertFalse(emailEstruturalmenteValido(""))
        assertFalse(emailEstruturalmenteValido("   "))
    }
}

