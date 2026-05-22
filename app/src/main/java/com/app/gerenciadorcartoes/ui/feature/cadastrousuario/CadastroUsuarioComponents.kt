package com.app.gerenciadorcartoes.ui.feature.cadastrousuario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.gerenciadorcartoes.R
import com.app.gerenciadorcartoes.ui.theme.LocalIconSize
import com.app.gerenciadorcartoes.ui.theme.LocalSpacing

// ── Enum de etapas do wizard ──────────────────────────────────────────────────

/** Representa as etapas do wizard de cadastro de usuário. */
internal enum class CadastroUsuarioTab(val titleRes: Int) {
    DadosPessoais(R.string.cadastro_usuario_aba_dados_pessoais),
    Endereco(R.string.cadastro_usuario_aba_endereco),
    Seguranca(R.string.cadastro_usuario_aba_seguranca),
}

// ── Stepper ───────────────────────────────────────────────────────────────────

/**
 * Cabeçalho do wizard com barra de progresso linear, indicadores de etapa circulares e
 * rótulos de cada passo.
 *
 * @param steps Lista de etapas na ordem de exibição.
 * @param etapaAtual Índice (0-based) da etapa corrente.
 * @param temErroNaEtapa Retorna `true` se a etapa informada possui algum campo com erro.
 * @param onStepClick Chamado ao clicar em um indicador de etapa já visitada.
 */
@Composable
internal fun CadastroUsuarioStepperHeader(
    steps: List<CadastroUsuarioTab>,
    etapaAtual: Int,
    temErroNaEtapa: (CadastroUsuarioTab) -> Boolean,
    onStepClick: (Int) -> Unit,
) {
    val spacing = LocalSpacing.current
    val iconSize = LocalIconSize.current
    val totalSteps = steps.size
    val progressoLinear = (etapaAtual + 1).toFloat() / totalSteps.toFloat()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        Text(
            text = stringResource(
                R.string.cadastro_usuario_progresso,
                etapaAtual + 1,
                totalSteps,
            ),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        LinearProgressIndicator(
            progress = { progressoLinear },
            modifier = Modifier.fillMaxWidth(),
        )

        // Indicadores circulares
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            steps.forEachIndexed { index, step ->
                val ativo      = index == etapaAtual
                val concluido  = index < etapaAtual && !temErroNaEtapa(step)
                val erro       = temErroNaEtapa(step)
                val podeClicar = index <= etapaAtual

                Surface(
                    shape = CircleShape,
                    color = when {
                        erro               -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                        ativo || concluido -> MaterialTheme.colorScheme.primary
                        else               -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    modifier = Modifier
                        .size(iconSize.medium + spacing.extraSmall)
                        .alpha(if (podeClicar) 1f else 0.45f)
                        .clickable(enabled = podeClicar) { onStepClick(index) },
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        when {
                            concluido -> Icon(
                                imageVector        = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint               = MaterialTheme.colorScheme.onPrimary,
                                modifier           = Modifier.size(iconSize.small),
                            )
                            erro -> Icon(
                                imageVector        = Icons.Default.Error,
                                contentDescription = null,
                                tint               = MaterialTheme.colorScheme.error,
                                modifier           = Modifier.size(iconSize.small),
                            )
                            else -> Text(
                                text  = "${index + 1}",
                                style = MaterialTheme.typography.labelMedium,
                                color = when {
                                    ativo       -> MaterialTheme.colorScheme.onPrimary
                                    !podeClicar -> MaterialTheme.colorScheme.outline
                                    else        -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            )
                        }
                    }
                }

                if (index < steps.lastIndex) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = spacing.extraSmall)
                            .height(2.dp)
                            .background(
                                if (index < etapaAtual) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                                CircleShape,
                            ),
                    )
                }
            }
        }

        // Rótulos de etapa
        Row(modifier = Modifier.fillMaxWidth()) {
            steps.forEachIndexed { index, step ->
                Text(
                    text      = stringResource(step.titleRes),
                    style     = MaterialTheme.typography.labelSmall,
                    color     = if (index <= etapaAtual) MaterialTheme.colorScheme.onSurfaceVariant
                                else MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier
                        .weight(1f)
                        .alpha(if (index <= etapaAtual) 1f else 0.6f),
                )
            }
        }
    }
}
