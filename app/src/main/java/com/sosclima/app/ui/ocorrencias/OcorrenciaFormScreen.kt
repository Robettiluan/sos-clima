package com.sosclima.app.ui.ocorrencias

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sosclima.app.data.local.Gravidade
import com.sosclima.app.data.local.TipoOcorrencia
import com.sosclima.app.di.AppViewModelProvider
import com.sosclima.app.ui.components.DialogoConfirmacao

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OcorrenciaFormRoute(
    onConcluido: () -> Unit,
    viewModel: OcorrenciaFormViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val form = estado.formulario
    val erroTitulo = estado.erroTitulo
    val erroEndereco = estado.erroEndereco

    // Navega de volta uma única vez quando o ViewModel sinaliza que terminou.
    LaunchedEffect(estado.concluido) {
        if (estado.concluido) onConcluido()
    }

    // Intercepta o botão voltar do sistema enquanto houver alterações não salvas.
    BackHandler(enabled = estado.temAlteracoes && !estado.concluido) {
        viewModel.solicitarSaida()
    }

    if (estado.mostrarDialogoDescartar) {
        DialogoConfirmacao(
            titulo = "Descartar alterações?",
            mensagem = "Você preencheu dados que ainda não foram salvos. Se sair agora, eles serão perdidos.",
            textoConfirmar = "Descartar",
            textoCancelar = "Continuar editando",
            onConfirmar = viewModel::confirmarDescarte,
            onCancelar = viewModel::cancelarDescarte,
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (estado.editando) "Editar ocorrência" else "Nova ocorrência") },
                navigationIcon = {
                    IconButton(onClick = viewModel::solicitarSaida) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::salvar) {
                        Icon(Icons.Default.Check, contentDescription = "Salvar")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = form.titulo,
                onValueChange = viewModel::onTituloChange,
                label = { Text("Título *") },
                placeholder = { Text("Ex.: Rua alagada em frente à escola") },
                isError = erroTitulo != null,
                supportingText = if (erroTitulo != null) {
                    { Text(erroTitulo) }
                } else {
                    null
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = form.endereco,
                onValueChange = viewModel::onEnderecoChange,
                label = { Text("Local / endereço *") },
                isError = erroEndereco != null,
                supportingText = if (erroEndereco != null) {
                    { Text(erroEndereco) }
                } else {
                    null
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = form.descricao,
                onValueChange = viewModel::onDescricaoChange,
                label = { Text("Descrição") },
                minLines = 3,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth(),
            )

            Text("Tipo", style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TipoOcorrencia.entries.forEach { tipo ->
                    FilterChip(
                        selected = form.tipo == tipo,
                        onClick = { viewModel.onTipoChange(tipo) },
                        label = { Text(tipo.rotulo) },
                    )
                }
            }

            Text("Gravidade", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Gravidade.entries.forEach { gravidade ->
                    FilterChip(
                        selected = form.gravidade == gravidade,
                        onClick = { viewModel.onGravidadeChange(gravidade) },
                        label = { Text(gravidade.rotulo) },
                    )
                }
            }

            if (estado.editando) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Ocorrência resolvida", modifier = Modifier.weight(1f))
                    Switch(checked = form.resolvida, onCheckedChange = viewModel::onResolvidaChange)
                }
            }

            Button(onClick = viewModel::salvar, modifier = Modifier.fillMaxWidth()) {
                Text("Salvar")
            }
        }
    }
}
