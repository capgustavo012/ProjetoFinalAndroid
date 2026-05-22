package com.app.gerenciadorcartoes.ui.feature.cadastrousuario

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.gerenciadorcartoes.R
import com.app.gerenciadorcartoes.ui.components.AppLoading
import com.app.gerenciadorcartoes.ui.components.AppScaffold
import com.app.gerenciadorcartoes.ui.components.AppTopAppBar
import com.app.gerenciadorcartoes.ui.feature.cadastrousuario.state.CadastroUsuarioUiState
import com.app.gerenciadorcartoes.ui.theme.GerenciadorCartoesTheme
import com.app.gerenciadorcartoes.ui.theme.LocalIconSize
import com.app.gerenciadorcartoes.ui.theme.LocalSpacing
import com.app.gerenciadorcartoes.viewmodel.CadastroUsuarioViewModel

private enum class CadastroUsuarioTab(val titleRes: Int) {
    DadosPessoais(R.string.cadastro_usuario_aba_dados_pessoais),
    Endereco(R.string.cadastro_usuario_aba_endereco),
    Seguranca(R.string.cadastro_usuario_aba_seguranca),
}

@Composable
fun CadastroUsuarioScreen(
    navigateBack: () -> Unit,
    viewModel: CadastroUsuarioViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                CadastroUsuarioUiEvent.NavigateBack -> navigateBack()
                is CadastroUsuarioUiEvent.MostrarErro -> snackbarHostState.showSnackbar(event.mensagem)
                is CadastroUsuarioUiEvent.MostrarMensagem -> snackbarHostState.showSnackbar(event.mensagem)
            }
        }
    }

    CadastroUsuarioContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroUsuarioContent(
    uiState: CadastroUsuarioUiState = CadastroUsuarioUiState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onEvent: (CadastroUsuarioEvent) -> Unit = {},
) {
    val spacing = LocalSpacing.current
    val iconSize = LocalIconSize.current
    val focusManager = LocalFocusManager.current
    val tabs = CadastroUsuarioTab.entries

    var abaSelecionada by rememberSaveable { mutableIntStateOf(0) }
    var senhaVisivel by remember { mutableStateOf(false) }
    var confirmarSenhaVisivel by remember { mutableStateOf(false) }
    var estavaBuscandoCep by remember { mutableStateOf(false) }

    val nomeFocusRequester = remember { FocusRequester() }
    val cpfFocusRequester = remember { FocusRequester() }
    val emailFocusRequester = remember { FocusRequester() }
    val cepFocusRequester = remember { FocusRequester() }
    val enderecoFocusRequester = remember { FocusRequester() }
    val numberFocusRequester = remember { FocusRequester() }
    val bairroFocusRequester = remember { FocusRequester() }
    val estadoFocusRequester = remember { FocusRequester() }
    val senhaFocusRequester = remember { FocusRequester() }
    val confirmarSenhaFocusRequester = remember { FocusRequester() }

    LaunchedEffect(abaSelecionada) {
        when (tabs[abaSelecionada]) {
            CadastroUsuarioTab.DadosPessoais -> nomeFocusRequester.requestFocus()
            CadastroUsuarioTab.Endereco -> cepFocusRequester.requestFocus()
            CadastroUsuarioTab.Seguranca -> senhaFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(uiState.buscandoCep) {
        if (estavaBuscandoCep && !uiState.buscandoCep && uiState.erroCep == null) {
            numberFocusRequester.requestFocus()
        }
        estavaBuscandoCep = uiState.buscandoCep
    }

    AppScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            AppTopAppBar(
                title = stringResource(R.string.app_name),
                subtitle = stringResource(R.string.cadastro_usuario_subtitulo),
                large = true,
                onNavigateBack = { onEvent(CadastroUsuarioEvent.Voltar) },
            )
        },
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding(),
        ) {
            val contentWidth = if (maxWidth >= 600.dp) 560.dp else maxWidth

            if (uiState.carregando) {
                AppLoading()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f),
                                    MaterialTheme.colorScheme.background,
                                ),
                            ),
                        )
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = spacing.medium, vertical = spacing.medium),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacing.medium),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = contentWidth),
                        verticalArrangement = Arrangement.spacedBy(spacing.medium),
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.large,
                            tonalElevation = spacing.small,
                            shadowElevation = spacing.extraSmall,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(
                                modifier = Modifier.padding(spacing.medium),
                                verticalArrangement = Arrangement.spacedBy(spacing.small),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(iconSize.extraLarge * 2)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(iconSize.large),
                                    )
                                }

                                Text(
                                    text = stringResource(R.string.cadastro_usuario_header_titulo),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )

                                Text(
                                    text = stringResource(R.string.cadastro_usuario_header_subtitulo),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }

                        Surface(
                            shape = MaterialTheme.shapes.large,
                            tonalElevation = spacing.small,
                            shadowElevation = spacing.extraSmall,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column {
                                PrimaryScrollableTabRow(
                                    selectedTabIndex = abaSelecionada,
                                    edgePadding = spacing.medium,
                                    divider = { },
                                ) {
                                    tabs.forEachIndexed { index, tab ->
                                        Tab(
                                            selected = abaSelecionada == index,
                                            onClick = { abaSelecionada = index },
                                            text = { Text(stringResource(tab.titleRes)) },
                                        )
                                    }
                                }

                                HorizontalDivider()

                                AnimatedContent(
                                    targetState = tabs[abaSelecionada],
                                    label = "CadastroUsuarioTabContent",
                                ) { tab ->
                                    when (tab) {
                                        CadastroUsuarioTab.DadosPessoais -> CadastroUsuarioSectionCard(
                                            title = stringResource(R.string.cadastro_usuario_secao_dados_pessoais),
                                            subtitle = stringResource(R.string.cadastro_usuario_header_subtitulo),
                                        ) {
                                            CadastroUsuarioField(
                                                value = uiState.nome,
                                                onValueChange = { onEvent(CadastroUsuarioEvent.NomeAlterado(it)) },
                                                label = stringResource(R.string.cadastro_usuario_nome),
                                                errorMessage = uiState.erroNome,
                                                focusRequester = nomeFocusRequester,
                                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                                keyboardActions = KeyboardActions(onNext = { cpfFocusRequester.requestFocus() }),
                                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                            )

                                            CadastroUsuarioField(
                                                value = uiState.cpf,
                                                onValueChange = { onEvent(CadastroUsuarioEvent.CpfAlterado(it)) },
                                                label = stringResource(R.string.cadastro_usuario_cpf),
                                                errorMessage = uiState.erroCpf,
                                                focusRequester = cpfFocusRequester,
                                                keyboardActions = KeyboardActions(onNext = { emailFocusRequester.requestFocus() }),
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Number,
                                                    imeAction = ImeAction.Next,
                                                ),
                                            )

                                            CadastroUsuarioField(
                                                value = uiState.email,
                                                onValueChange = { onEvent(CadastroUsuarioEvent.EmailAlterado(it)) },
                                                label = stringResource(R.string.cadastro_usuario_email),
                                                errorMessage = uiState.erroEmail,
                                                focusRequester = emailFocusRequester,
                                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                                keyboardActions = KeyboardActions(onNext = { abaSelecionada = CadastroUsuarioTab.Endereco.ordinal }),
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Email,
                                                    imeAction = ImeAction.Next,
                                                ),
                                            )
                                        }

                                        CadastroUsuarioTab.Endereco -> CadastroUsuarioSectionCard(
                                            title = stringResource(R.string.cadastro_usuario_secao_endereco),
                                            subtitle = stringResource(R.string.cadastro_usuario_cep_dica),
                                        ) {
                                            CadastroUsuarioField(
                                                value = uiState.cep,
                                                onValueChange = { onEvent(CadastroUsuarioEvent.CepAlterado(it)) },
                                                label = stringResource(R.string.cadastro_usuario_cep),
                                                errorMessage = uiState.erroCep,
                                                focusRequester = cepFocusRequester,
                                                showValidationIcon = !uiState.buscandoCep,
                                                trailingIcon = {
                                                    if (uiState.buscandoCep) {
                                                        CircularProgressIndicator(
                                                            modifier = Modifier
                                                                .padding(spacing.extraSmall)
                                                                .size(iconSize.small),
                                                            strokeWidth = 2.dp,
                                                            color = MaterialTheme.colorScheme.primary,
                                                        )
                                                    }
                                                },
                                                keyboardActions = KeyboardActions(onNext = { enderecoFocusRequester.requestFocus() }),
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Number,
                                                    imeAction = ImeAction.Next,
                                                ),
                                            )

                                            if (uiState.buscandoCep) {
                                                Text(
                                                    text = stringResource(R.string.cadastro_usuario_buscando_cep),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.primary,
                                                )
                                            }

                                            CadastroUsuarioField(
                                                value = uiState.endereco,
                                                onValueChange = { onEvent(CadastroUsuarioEvent.EnderecoAlterado(it)) },
                                                label = stringResource(R.string.cadastro_usuario_endereco),
                                                errorMessage = uiState.erroEndereco,
                                                focusRequester = enderecoFocusRequester,
                                                readOnly = uiState.buscandoCep,
                                                keyboardActions = KeyboardActions(onNext = { numberFocusRequester.requestFocus() }),
                                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                            )

                                            CadastroUsuarioField(
                                                value = uiState.number,
                                                onValueChange = { onEvent(CadastroUsuarioEvent.NumberAlterado(it)) },
                                                label = stringResource(R.string.cadastro_usuario_numero),
                                                errorMessage = uiState.erroNumber,
                                                focusRequester = numberFocusRequester,
                                                keyboardActions = KeyboardActions(onNext = { bairroFocusRequester.requestFocus() }),
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Number,
                                                    imeAction = ImeAction.Next,
                                                ),
                                            )

                                            CadastroUsuarioField(
                                                value = uiState.bairro,
                                                onValueChange = { onEvent(CadastroUsuarioEvent.BairroAlterado(it)) },
                                                label = stringResource(R.string.cadastro_usuario_bairro),
                                                errorMessage = uiState.erroBairro,
                                                focusRequester = bairroFocusRequester,
                                                readOnly = uiState.buscandoCep,
                                                keyboardActions = KeyboardActions(onNext = { estadoFocusRequester.requestFocus() }),
                                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                            )

                                            CadastroUsuarioField(
                                                value = uiState.estado,
                                                onValueChange = { onEvent(CadastroUsuarioEvent.EstadoAlterado(it)) },
                                                label = stringResource(R.string.cadastro_usuario_estado),
                                                errorMessage = uiState.erroEstado,
                                                focusRequester = estadoFocusRequester,
                                                readOnly = uiState.buscandoCep,
                                                keyboardActions = KeyboardActions(onNext = { abaSelecionada = CadastroUsuarioTab.Seguranca.ordinal }),
                                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                            )
                                        }

                                        CadastroUsuarioTab.Seguranca -> CadastroUsuarioSectionCard(
                                            title = stringResource(R.string.cadastro_usuario_secao_seguranca),
                                            subtitle = stringResource(R.string.cadastro_usuario_header_subtitulo),
                                        ) {
                                            CadastroUsuarioField(
                                                value = uiState.senha,
                                                onValueChange = { onEvent(CadastroUsuarioEvent.SenhaAlterada(it)) },
                                                label = stringResource(R.string.cadastro_usuario_senha),
                                                errorMessage = uiState.erroSenha,
                                                focusRequester = senhaFocusRequester,
                                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                                trailingIcon = {
                                                    IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                                                        Icon(
                                                            imageVector = if (senhaVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                            contentDescription = if (senhaVisivel) {
                                                                stringResource(R.string.cadastro_usuario_cd_ocultar_senha)
                                                            } else {
                                                                stringResource(R.string.cadastro_usuario_cd_mostrar_senha)
                                                            },
                                                        )
                                                    }
                                                },
                                                visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                                                keyboardActions = KeyboardActions(onNext = { confirmarSenhaFocusRequester.requestFocus() }),
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Password,
                                                    imeAction = ImeAction.Next,
                                                ),
                                            )

                                            CadastroUsuarioField(
                                                value = uiState.confirmarSenha,
                                                onValueChange = { onEvent(CadastroUsuarioEvent.ConfirmarSenhaAlterada(it)) },
                                                label = stringResource(R.string.cadastro_usuario_confirmar_senha),
                                                errorMessage = uiState.erroConfirmarSenha,
                                                focusRequester = confirmarSenhaFocusRequester,
                                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                                trailingIcon = {
                                                    IconButton(onClick = { confirmarSenhaVisivel = !confirmarSenhaVisivel }) {
                                                        Icon(
                                                            imageVector = if (confirmarSenhaVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                            contentDescription = if (confirmarSenhaVisivel) {
                                                                stringResource(R.string.cadastro_usuario_cd_ocultar_senha)
                                                            } else {
                                                                stringResource(R.string.cadastro_usuario_cd_mostrar_senha)
                                                            },
                                                        )
                                                    }
                                                },
                                                visualTransformation = if (confirmarSenhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                                                keyboardActions = KeyboardActions(onDone = {
                                                    focusManager.clearFocus()
                                                    onEvent(CadastroUsuarioEvent.Cadastrar)
                                                }),
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Password,
                                                    imeAction = ImeAction.Done,
                                                ),
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = { onEvent(CadastroUsuarioEvent.Cadastrar) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(stringResource(R.string.cadastro_usuario_btn_cadastrar))
                        }

                        Spacer(modifier = Modifier.height(spacing.small))
                    }
                }
            }
        }
    }
}

@Composable
private fun CadastroUsuarioSectionCard(
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val spacing = LocalSpacing.current

    Surface(
        shape = MaterialTheme.shapes.large,
        tonalElevation = spacing.small,
        shadowElevation = spacing.extraSmall,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(spacing.extraSmall))
            content()
        }
    }
}

@Composable
private fun CadastroUsuarioField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    readOnly: Boolean = false,
    focusRequester: FocusRequester? = null,
    showValidationIcon: Boolean = true,
) {
    val fieldModifier = if (focusRequester != null) {
        modifier.fillMaxWidth().focusRequester(focusRequester)
    } else {
        modifier.fillMaxWidth()
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = leadingIcon,
        trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showValidationIcon) {
                    CadastroUsuarioValidationIcon(
                        hasValue = value.isNotBlank(),
                        isError = errorMessage != null,
                    )
                }
                trailingIcon?.invoke()
            }
        },
        isError = errorMessage != null,
        supportingText = errorMessage?.let { { Text(it) } },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        readOnly = readOnly,
        singleLine = true,
        modifier = fieldModifier,
    )
}

@Composable
private fun CadastroUsuarioValidationIcon(
    hasValue: Boolean,
    isError: Boolean,
) {
    if (!hasValue && !isError) return

    Icon(
        imageVector = if (isError) Icons.Default.Error else Icons.Default.CheckCircle,
        contentDescription = null,
        tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(LocalIconSize.current.small),
    )
}

@Preview(showBackground = true, name = "CadastroUsuario – Carregando")
@Preview(showBackground = true, name = "CadastroUsuario – Carregando Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CadastroUsuarioCarregandoPreview() {
    GerenciadorCartoesTheme {
        CadastroUsuarioContent(uiState = CadastroUsuarioUiState(carregando = true))
    }
}

@Preview(showBackground = true, name = "CadastroUsuario – Vazio")
@Composable
private fun CadastroUsuarioVazioPreview() {
    GerenciadorCartoesTheme { CadastroUsuarioContent() }
}

@Preview(showBackground = true, name = "CadastroUsuario – Preenchido")
@Composable
private fun CadastroUsuarioPreenchidoPreview() {
    GerenciadorCartoesTheme {
        CadastroUsuarioContent(
            uiState = CadastroUsuarioUiState(
                nome = "João Silva",
                cpf = "123.456.789-00",
                cep = "01310-100",
                endereco = "Av. Paulista",
                number = "1000",
                bairro = "Bela Vista",
                estado = "SP",
                email = "joao@email.com",
                senha = "senha123",
                confirmarSenha = "senha123",
            ),
        )
    }
}
