package com.app.gerenciadorcartoes.ui.feature.cadastrousuario.state


data class CadastroUsuarioUiState(
    val nome           : String  = "",
    val erroNome       : String? = null,
    val cpf            : String  = "",
    val erroCpf        : String? = null,
    val cep            : String  = "",
    val erroCep        : String? = null,
    val endereco       : String  = "",
    val erroEndereco   : String? = null,
    val number         : String  = "",
    val erroNumber     : String? = null,
    val bairro         : String  = "",
    val erroBairro     : String? = null,
    val estado         : String  = "",
    val erroEstado     : String? = null,
    val email          : String  = "",
    val erroEmail      : String? = null,
    val senha          : String  = "",
    val erroSenha      : String? = null,
    val confirmarSenha : String  = "",
    val erroConfirmarSenha : String? = null,
    val carregando     : Boolean = false,
    val buscandoCep     : Boolean = false
)
