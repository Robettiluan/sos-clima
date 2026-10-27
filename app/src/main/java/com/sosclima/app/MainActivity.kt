package com.sosclima.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sosclima.app.di.AppViewModelProvider
import com.sosclima.app.ui.ajustes.AjustesViewModel
import com.sosclima.app.ui.navigation.SosClimaApp
import com.sosclima.app.ui.theme.SosClimaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // O tema escuro vem do DataStore, então a escolha do usuário persiste entre aberturas.
            val ajustesViewModel: AjustesViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val ajustes by ajustesViewModel.uiState.collectAsStateWithLifecycle()

            SosClimaTheme(temaEscuro = ajustes.temaEscuro) {
                SosClimaApp()
            }
        }
    }
}
