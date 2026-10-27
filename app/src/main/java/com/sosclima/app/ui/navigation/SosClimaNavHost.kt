package com.sosclima.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.compose.navigation
import com.sosclima.app.ui.ajustes.AjustesRoute
import com.sosclima.app.ui.alertas.AlertasRoute
import com.sosclima.app.ui.alertas.DetalheAlertaRoute
import com.sosclima.app.ui.alertas.DetalheAlertaViewModel
import com.sosclima.app.ui.emergencia.EmergenciaScreen
import com.sosclima.app.ui.ocorrencias.OcorrenciaFormRoute
import com.sosclima.app.ui.ocorrencias.OcorrenciaFormViewModel
import com.sosclima.app.ui.ocorrencias.OcorrenciasRoute

/** Rotas do app. Cada módulo do menu é um grafo aninhado com suas telas. */
object Rotas {
    const val ALERTAS = "alertas"
    const val ALERTAS_LISTA = "alertas/lista"
    const val ALERTAS_DETALHE = "alertas/detalhe/{${DetalheAlertaViewModel.ARG_DATA}}"

    const val OCORRENCIAS = "ocorrencias"
    const val OCORRENCIAS_LISTA = "ocorrencias/lista"
    const val OCORRENCIAS_FORM = "ocorrencias/form?${OcorrenciaFormViewModel.ARG_ID}={${OcorrenciaFormViewModel.ARG_ID}}"

    const val EMERGENCIA = "emergencia"
    const val AJUSTES = "ajustes"

    fun detalheAlerta(data: String) = "alertas/detalhe/$data"
    fun formOcorrencia(id: Long? = null) = if (id == null) "ocorrencias/form" else "ocorrencias/form?id=$id"
}

enum class ItemMenu(val rota: String, val rotulo: String, val icone: ImageVector) {
    ALERTAS(Rotas.ALERTAS, "Alertas", Icons.Default.Warning),
    OCORRENCIAS(Rotas.OCORRENCIAS, "Ocorrências", Icons.Default.LocationOn),
    EMERGENCIA(Rotas.EMERGENCIA, "Emergência", Icons.Default.Call),
    AJUSTES(Rotas.AJUSTES, "Ajustes", Icons.Default.Settings),
}

@Composable
fun SosClimaApp() {
    val navController = rememberNavController()
    val entradaAtual by navController.currentBackStackEntryAsState()
    val destinoAtual = entradaAtual?.destination

    // No formulário a barra some, para o usuário não sair sem passar pelo diálogo de descarte.
    val mostrarBarra = destinoAtual?.route != Rotas.OCORRENCIAS_FORM

    Scaffold(
        bottomBar = {
            if (mostrarBarra) {
                NavigationBar {
                    ItemMenu.entries.forEach { item ->
                        NavigationBarItem(
                            selected = destinoAtual?.hierarchy?.any { it.route == item.rota } == true,
                            onClick = {
                                navController.navigate(item.rota) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icone, contentDescription = null) },
                            label = { Text(item.rotulo) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rotas.ALERTAS,
            modifier = Modifier.padding(padding),
        ) {
            // Módulo B: painel de alertas (API) -> detalhes do dia
            navigation(startDestination = Rotas.ALERTAS_LISTA, route = Rotas.ALERTAS) {
                composable(Rotas.ALERTAS_LISTA) {
                    AlertasRoute(onAbrirDetalhe = { dia ->
                        navController.navigate(Rotas.detalheAlerta(dia.data.toString()))
                    })
                }
                composable(
                    Rotas.ALERTAS_DETALHE,
                    arguments = listOf(navArgument(DetalheAlertaViewModel.ARG_DATA) { type = NavType.StringType }),
                ) {
                    DetalheAlertaRoute(onVoltar = { navController.navigateUp() })
                }
            }

            // Módulo A: gestão de ocorrências (Room) -> formulário de cadastro/edição
            navigation(startDestination = Rotas.OCORRENCIAS_LISTA, route = Rotas.OCORRENCIAS) {
                composable(Rotas.OCORRENCIAS_LISTA) {
                    OcorrenciasRoute(
                        onNova = { navController.navigate(Rotas.formOcorrencia()) },
                        onEditar = { id -> navController.navigate(Rotas.formOcorrencia(id)) },
                    )
                }
                composable(
                    Rotas.OCORRENCIAS_FORM,
                    arguments = listOf(
                        navArgument(OcorrenciaFormViewModel.ARG_ID) {
                            type = NavType.LongType
                            defaultValue = OcorrenciaFormViewModel.SEM_ID
                        },
                    ),
                ) {
                    OcorrenciaFormRoute(onConcluido = { navController.popBackStack() })
                }
            }

            composable(Rotas.EMERGENCIA) { EmergenciaScreen() }
            composable(Rotas.AJUSTES) { AjustesRoute() }
        }
    }
}
