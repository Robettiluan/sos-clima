package com.sosclima.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sosclima.app.domain.Cidades
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

data class Preferencias(
    val temaEscuro: Boolean,
    val cidadeId: String,
)

/** Preferências chave-valor do usuário, salvas com DataStore. */
class PreferenciasRepository(private val dataStore: DataStore<Preferences>) {

    val preferencias: Flow<Preferencias> = dataStore.data
        .catch { erro -> if (erro is IOException) emit(emptyPreferences()) else throw erro }
        .map { prefs ->
            Preferencias(
                temaEscuro = prefs[TEMA_ESCURO] ?: false,
                cidadeId = prefs[CIDADE_ID] ?: Cidades.padrao.id,
            )
        }

    suspend fun salvarTemaEscuro(ativo: Boolean) {
        dataStore.edit { it[TEMA_ESCURO] = ativo }
    }

    suspend fun salvarCidade(cidadeId: String) {
        dataStore.edit { it[CIDADE_ID] = cidadeId }
    }

    private companion object {
        val TEMA_ESCURO = booleanPreferencesKey("tema_escuro")
        val CIDADE_ID = stringPreferencesKey("cidade_id")
    }
}
