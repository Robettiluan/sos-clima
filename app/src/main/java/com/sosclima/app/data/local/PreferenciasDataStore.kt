package com.sosclima.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/** Arquivo único de preferências do app (DataStore). */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferencias")
