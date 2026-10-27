package com.sosclima.app.ui.ajustes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sosclima.app.di.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjustesRoute(
    viewModel: AjustesViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar(title = { Text("Ajustes") }) }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Tema escuro", style = MaterialTheme.typography.titleMedium)
                    Text("Salvo no aparelho com DataStore", style = MaterialTheme.typography.bodySmall)
                }
                Switch(checked = estado.temaEscuro, onCheckedChange = viewModel::alterarTemaEscuro)
            }
            HorizontalDivider()

            Text(
                "Cidade monitorada",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp),
            )
            estado.cidadesDisponiveis.forEach { cidade ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = cidade.id == estado.cidade.id,
                            onClick = { viewModel.selecionarCidade(cidade) },
                            role = Role.RadioButton,
                        )
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(selected = cidade.id == estado.cidade.id, onClick = null)
                    Text(cidade.nome, modifier = Modifier.padding(start = 12.dp))
                }
            }
            HorizontalDivider(Modifier.padding(top = 8.dp))

            Text(
                "Previsão do tempo: Open-Meteo (open-meteo.com), API pública e gratuita.\n" +
                    "Os níveis de alerta são calculados pelo app e não substituem os avisos oficiais da Defesa Civil.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
