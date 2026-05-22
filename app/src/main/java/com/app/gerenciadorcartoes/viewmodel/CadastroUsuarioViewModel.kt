package com.app.gerenciadorcartoes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gerenciadorcartoes.network.service.BuscaCep
import com.app.gerenciadorcartoes.ui.feature.cadastrousuario.CadastroUsuarioEvent
import com.app.gerenciadorcartoes.ui.feature.cadastrousuario.CadastroUsuarioUiEvent
import com.app.gerenciadorcartoes.ui.feature.cadastrousuario.state.CadastroUsuarioUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CadastroUsuarioViewModel @Inject constructor(
    private val buscaCep: BuscaCep,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CadastroUsuarioUiState())
   
    val uiState: StateFlow<CadastroUsuarioUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<CadastroUsuarioUiEvent>(Channel.BUFFERED)

    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: CadastroUsuarioEvent) {
        when (event) {
            is CadastroUsuarioEvent.NomeAlterado ->
                _uiState.update { it.copy(nome = event.valor, erroNome = null) }

            is CadastroUsuarioEvent.CpfAlterado ->
                _uiState.update { it.copy(cpf = event.valor, erroCpf = null) }

            is CadastroUsuarioEvent.CepAlterado -> {
                val cepLimpo = event.valor.filter { it.isDigit() }
                _uiState.update { it.copy(cep = event.valor, erroCep = null) }
                if (cepLimpo.length == 8) buscarCep(cepLimpo)
            }

            is CadastroUsuarioEvent.EnderecoAlterado ->
                _uiState.update { it.copy(endereco = event.valor, erroEndereco = null) }

            is CadastroUsuarioEvent.NumberAlterado ->
                _uiState.update { it.copy(number = event.valor, erroNumber = null) }

            is CadastroUsuarioEvent.BairroAlterado ->
                _uiState.update { it.copy(bairro = event.valor, erroBairro = null) }

            is CadastroUsuarioEvent.EstadoAlterado ->
                _uiState.update { it.copy(estado = event.valor, erroEstado = null) }

            is CadastroUsuarioEvent.EmailAlterado ->
                _uiState.update { it.copy(email = event.valor, erroEmail = null) }

            is CadastroUsuarioEvent.SenhaAlterada ->
                _uiState.update { it.copy(senha = event.valor, erroSenha = null) }

            is CadastroUsuarioEvent.ConfirmarSenhaAlterada ->
                _uiState.update { it.copy(confirmarSenha = event.valor, erroConfirmarSenha = null) }

            CadastroUsuarioEvent.Cadastrar -> cadastrar()

            CadastroUsuarioEvent.Voltar ->
                viewModelScope.launch { _uiEvent.send(CadastroUsuarioUiEvent.NavigateBack) }
        }
    }

    private fun buscarCep(cep: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(buscandoCep = true) }
            runCatching {
                val resposta = buscaCep.getCep(cep)
                if (resposta.isSuccessful) {
                    val body = resposta.body()
                    if (body != null ) {
                        _uiState.update {
                            it.copy(
                                endereco = body.logradouro,
                                bairro   = body.bairro,
                                estado   = body.uf,
                                erroCep  = null,
                                erroEndereco = null,
                                erroBairro = null,
                                erroEstado = null,
                            )
                        }
                    } else {
                        _uiState.update { it.copy(erroCep = "CEP não encontrado") }
                    }
                } else {
                    _uiState.update { it.copy(erroCep = "CEP não encontrado") }
                }
            }.onFailure { erro ->
                _uiState.update { it.copy(erroCep = erro.message ?: "Erro ao buscar CEP") }
            }
            _uiState.update { it.copy(buscandoCep = false) }
        }
    }

    private fun cadastrar() {
        viewModelScope.launch {
            runCatching {
                val s = _uiState.value
                if (!validar(s)) return@runCatching

                _uiEvent.send(CadastroUsuarioUiEvent.MostrarMensagem("Cadastro realizado com sucesso!"))
                _uiEvent.send(CadastroUsuarioUiEvent.NavigateBack)
            }.onFailure { erro ->
                _uiState.update { it.copy(carregando = false) }
                _uiEvent.send(CadastroUsuarioUiEvent.MostrarErro(erro.message ?: "Erro ao cadastrar"))
            }
        }
    }

    private fun validar(s: CadastroUsuarioUiState): Boolean {
        var valido = true

        fun invalidar(campo: (CadastroUsuarioUiState) -> String?, atualizador: (CadastroUsuarioUiState) -> CadastroUsuarioUiState) {
            if (campo(s).isNullOrBlank()) {
                _uiState.update { atualizador(it) }
                valido = false
            }
        }

        invalidar({ it.nome }) { it.copy(erroNome = "Nome é obrigatório") }
        invalidar({ it.cpf }) { it.copy(erroCpf = "CPF é obrigatório") }
        invalidar({ it.cep }) { it.copy(erroCep = "CEP é obrigatório") }
        invalidar({ it.endereco }) { it.copy(erroEndereco = "Endereço é obrigatório") }
        invalidar({ it.number }) { it.copy(erroNumber = "Número é obrigatório") }
        invalidar({ it.bairro }) { it.copy(erroBairro = "Bairro é obrigatório") }
        invalidar({ it.estado }) { it.copy(erroEstado = "Estado é obrigatório") }
        invalidar({ it.email }) { it.copy(erroEmail = "E-mail é obrigatório") }
        invalidar({ it.senha }) { it.copy(erroSenha = "Senha é obrigatória") }
        invalidar({ it.confirmarSenha }) { it.copy(erroConfirmarSenha = "Confirme a senha") }

        if (s.cep.filter { it.isDigit() }.length != 8) {
            _uiState.update { it.copy(erroCep = "CEP deve ter 8 números") }
            valido = false
        }

        if (s.senha.isNotBlank() && s.confirmarSenha.isNotBlank() && s.senha != s.confirmarSenha) {
            _uiState.update { it.copy(erroConfirmarSenha = "As senhas não coincidem") }
            valido = false
        }

        return valido
    }
}

