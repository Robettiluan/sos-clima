package com.sosclima.app.ui.alertas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sosclima.app.di.AppViewModelProvider
import com.sosclima.app.domain.PrevisaoDia
import com.sosclima.app.domain.descricaoTempo
import com.sosclima.app.domain.formatarGraus
import com.sosclima.app.domain.formatarLongo
import com.sosclima.app.domain.nivelAlerta
import com.sosclima.app.domain.recomendacoes
import com.sosclima.app.ui.components.EstadoVazio
import com.sosclima.app.ui.components.Etiqueta
import com.sosclima.app.ui.theme.cor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheAlertaRoute(
    onVoltar: () -> Unit,
    viewModel: DetalheAlertaViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val dia by viewModel.dia.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do dia") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
    ) { padding ->
        val atual = dia
        if (atual == null) {
            EstadoVazio(
                "Previsão indisponível",
                "Volte e atualize a lista de alertas.",
                Modifier.padding(padding),
            )
        } else {
            ConteudoDetalhe(atual, Modifier.padding(padding))
        }
    }
}

@Composable
private fun ConteudoDetalhe(dia: PrevisaoDia, modifier: Modifier = Modifier) {
    val nivel = dia.nivelAlerta()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(dia.data.formatarLongo(), style = MaterialTheme.typography.headlineSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Etiqueta(nivel.rotulo, nivel.cor())
            Etiqueta(dia.codigoTempo.descricaoTempo(), MaterialTheme.colorScheme.primary)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            Metrica("Mínima", dia.tempMin.formatarGraus(), Modifier.weight(1f))
            Metrica("Máxima", dia.tempMax.formatarGraus(), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            Metrica("Chuva", "%.1f mm".format(dia.chuvaMm), Modifier.weight(1f))
            Metrica("Chance", "${dia.probabilidadeChuva}%", Modifier.weight(1f))
            Metrica("Vento", "%.0f km/h".format(dia.ventoKmh), Modifier.weight(1f))
        }

        Text("O que fazer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        dia.recomendacoes().forEach { Text("• $it", style = MaterialTheme.typography.bodyLarge) }

        Text(
            "Classificação calculada pelo app a partir da previsão da Open-Meteo. " +
                "Siga sempre os avisos oficiais da Defesa Civil.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun Metrica(rotulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(rotulo, style = MaterialTheme.typography.labelMedium)
            Text(valor, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}
