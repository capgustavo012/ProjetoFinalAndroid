package com.app.gerenciadorcartoes.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.app.gerenciadorcartoes.ui.theme.GerenciadorCartoesTheme
import com.app.gerenciadorcartoes.ui.theme.LocalSpacing

/**
 * Card de seção genérico com título, subtítulo opcional e slot de conteúdo.
 *
 * Aplica espaçamento e tipografia do design system. Use para agrupar campos
 * relacionados em formulários e telas de detalhe.
 *
 * @param title    Título da seção (ex: "Dados Pessoais").
 * @param subtitle Texto explicativo exibido abaixo do título; omita se desnecessário.
 * @param modifier Modifier aplicado ao [Column] raiz.
 * @param content  Conteúdo da seção (campos, textos, etc.).
 */
@Composable
fun AppSectionCard(
    title    : String,
    modifier : Modifier                         = Modifier,
    subtitle : String?                          = null,
    content  : @Composable ColumnScope.() -> Unit,
) {
    val spacing = LocalSpacing.current

    Column(
        modifier            = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.medium, vertical = spacing.small),
        verticalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        Text(
            text  = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (subtitle != null) {
            Text(
                text  = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(spacing.extraSmall))
        content()
    }
}

// ── Previews ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "AppSectionCard – Com subtítulo")
@Preview(showBackground = true, name = "AppSectionCard – Com subtítulo Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AppSectionCardComSubtituloPreview() {
    GerenciadorCartoesTheme {
        AppSectionCard(
            title    = "Dados Pessoais",
            subtitle = "Informe seus dados para criar a sua conta.",
        ) {
            Text("Campo 1")
            Text("Campo 2")
        }
    }
}

@Preview(showBackground = true, name = "AppSectionCard – Sem subtítulo")
@Composable
private fun AppSectionCardSemSubtituloPreview() {
    GerenciadorCartoesTheme {
        AppSectionCard(title = "Endereço") {
            Text("Campo de endereço")
        }
    }
}

