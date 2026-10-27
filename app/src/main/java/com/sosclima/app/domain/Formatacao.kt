package com.sosclima.app.domain

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val localeBr: Locale = Locale.forLanguageTag("pt-BR")
private val formatoDia = DateTimeFormatter.ofPattern("EEE, dd/MM", localeBr)
private val formatoDiaLongo = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM", localeBr)
private val formatoDataHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", localeBr)

fun LocalDate.formatarCurto(): String = format(formatoDia).replaceFirstChar { it.uppercase() }

fun LocalDate.formatarLongo(): String = format(formatoDiaLongo).replaceFirstChar { it.uppercase() }

fun Long.formatarDataHora(): String =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).format(formatoDataHora)

fun Double.formatarGraus(): String = "%.0f°".format(localeBr, this)
