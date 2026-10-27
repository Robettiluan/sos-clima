package com.sosclima.app.di

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sosclima.app.SosClimaApplication
import com.sosclima.app.ui.ajustes.AjustesViewModel
import com.sosclima.app.ui.alertas.AlertasViewModel
import com.sosclima.app.ui.alertas.DetalheAlertaViewModel
import com.sosclima.app.ui.ocorrencias.OcorrenciaFormViewModel
import com.sosclima.app.ui.ocorrencias.OcorrenciasViewModel

/** Fábrica única de ViewModels: entrega só os repositórios, nunca o Context. */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            AlertasViewModel(container().previsaoRepository, container().preferenciasRepository)
        }
        initializer {
            DetalheAlertaViewModel(createSavedStateHandle(), container().previsaoRepository)
        }
        initializer {
            OcorrenciasViewModel(container().ocorrenciaRepository)
        }
        initializer {
            OcorrenciaFormViewModel(createSavedStateHandle(), container().ocorrenciaRepository)
        }
        initializer {
            AjustesViewModel(container().preferenciasRepository)
        }
    }
}

private fun CreationExtras.container(): AppContainer =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as SosClimaApplication).container
