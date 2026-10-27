package com.sosclima.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.sosclima.app.data.local.Gravidade
import com.sosclima.app.domain.NivelAlerta

private val Azul = Color(0xFF0B5CAD)
private val AzulClaro = Color(0xFFA6C8FF)
private val Laranja = Color(0xFFE8710A)
private val LaranjaClaro = Color(0xFFFFB68A)

private val CoresClaras = lightColorScheme(
    primary = Azul,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD5E3FF),
    onPrimaryContainer = Color(0xFF001B3C),
    secondary = Laranja,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDBC8),
    onSecondaryContainer = Color(0xFF331200),
)

private val CoresEscuras = darkColorScheme(
    primary = AzulClaro,
    onPrimary = Color(0xFF003062),
    primaryContainer = Color(0xFF00468A),
    onPrimaryContainer = Color(0xFFD5E3FF),
    secondary = LaranjaClaro,
    onSecondary = Color(0xFF532200),
    secondaryContainer = Color(0xFF763300),
    onSecondaryContainer = Color(0xFFFFDBC8),
)

@Composable
fun SosClimaTheme(
    temaEscuro: Boolean,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (temaEscuro) CoresEscuras else CoresClaras,
        content = content,
    )
}

/** Cores semânticas de risco (iguais nos dois temas, para manter o significado). */
fun NivelAlerta.cor(): Color = when (this) {
    NivelAlerta.BAIXO -> Color(0xFF2E7D32)
    NivelAlerta.MEDIO -> Color(0xFFE6A100)
    NivelAlerta.ALTO -> Color(0xFFC62828)
}

fun Gravidade.cor(): Color = when (this) {
    Gravidade.BAIXA -> Color(0xFF2E7D32)
    Gravidade.MEDIA -> Color(0xFFE6A100)
    Gravidade.ALTA -> Color(0xFFC62828)
}
