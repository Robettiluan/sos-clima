package com.sosclima.app.ui.emergencia

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sosclima.app.data.local.ContatoEmergencia
import com.sosclima.app.data.local.GuiaEmergencia
import com.sosclima.app.data.local.OrientacaoEmergencia

/** Guia offline: telefones e orientações ficam no app e funcionam sem internet. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergenciaScreen() {
    val context = LocalContext.current
    val ligar: (String) -> Unit = { numero ->
        // ACTION_DIAL só abre o discador com o número; não precisa de permissão.
        try {
            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$numero")))
        } catch (e: ActivityNotFoundException) {
            // Aparelho sem discador (ex.: tablet): não há o que fazer.
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Emergência") }) }) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text("Telefones úteis", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            items(GuiaEmergencia.contatos, key = { it.numero }) { contato ->
                CartaoContato(contato, onLigar = { ligar(contato.numero) })
            }
            item {
                Text(
                    "O que fazer em cada situação",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            items(GuiaEmergencia.orientacoes, key = { it.titulo }) { orientacao ->
                CartaoOrientacao(orientacao)
            }
        }
    }
}

@Composable
private fun CartaoContato(contato: ContatoEmergencia, onLigar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(contato.nome, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(contato.descricao, style = MaterialTheme.typography.bodySmall)
            }
            FilledTonalButton(onClick = onLigar) {
                Icon(Icons.Default.Phone, contentDescription = null)
                Text(" ${contato.numero}", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CartaoOrientacao(orientacao: OrientacaoEmergencia) {
    // rememberSaveable: o cartão continua aberto/fechado mesmo se a tela girar.
    var expandido by rememberSaveable(orientacao.titulo) { mutableStateOf(false) }

    Card(
        onClick = { expandido = !expandido },
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    orientacao.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    if (expandido) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expandido) "Recolher" else "Expandir",
                )
            }
            if (expandido) {
                orientacao.passos.forEach { passo ->
                    Text("• $passo", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 6.dp))
                }
            }
        }
    }
}
