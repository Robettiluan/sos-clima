package com.sosclima.app.domain

import java.time.LocalDate

/** Previsão de um dia, já convertida do JSON da API para o modelo do app. */
data class PrevisaoDia(
    val data: LocalDate,
    val codigoTempo: Int,
    val tempMax: Double,
    val tempMin: Double,
    val chuvaMm: Double,
    val probabilidadeChuva: Int,
    val ventoKmh: Double,
)

enum class NivelAlerta(val rotulo: String) {
    BAIXO("Sem alerta"),
    MEDIO("Atenção"),
    ALTO("Alerta alto"),
}

/**
 * Classifica o dia em um nível de alerta a partir dos dados da previsão.
 * Os limites são uma regra do próprio app (não são alertas oficiais da Defesa Civil).
 */
fun PrevisaoDia.nivelAlerta(): NivelAlerta = when {
    chuvaMm >= 50.0 || ventoKmh >= 60.0 -> NivelAlerta.ALTO
    chuvaMm >= 20.0 || ventoKmh >= 40.0 || probabilidadeChuva >= 80 -> NivelAlerta.MEDIO
    else -> NivelAlerta.BAIXO
}

/** Orientações exibidas na tela de detalhes, de acordo com o risco do dia. */
fun PrevisaoDia.recomendacoes(): List<String> = buildList {
    when (nivelAlerta()) {
        NivelAlerta.ALTO -> {
            add("Evite sair de casa se não for necessário.")
            add("Não atravesse ruas ou pontes alagadas, a pé ou de carro.")
            add("Se mora em área de risco, deixe o local e procure um abrigo ou a casa de parentes.")
            add("Ligue 199 (Defesa Civil) em caso de alagamento, deslizamento ou desabamento.")
        }
        NivelAlerta.MEDIO -> {
            add("Fique atento aos avisos da Defesa Civil da sua cidade.")
            add("Evite áreas com histórico de alagamento.")
            add("Deixe documentos e remédios em um saco plástico, em local alto.")
        }
        NivelAlerta.BAIXO -> add("Sem risco relevante previsto para o dia.")
    }
    if (ventoKmh >= 40.0) add("Ventos fortes: afaste-se de árvores, placas e redes elétricas.")
}

/** Descrição em português dos códigos de tempo da OMM usados pela Open-Meteo. */
fun Int.descricaoTempo(): String = when (this) {
    0 -> "Céu limpo"
    1 -> "Predomínio de sol"
    2 -> "Parcialmente nublado"
    3 -> "Nublado"
    45, 48 -> "Neblina"
    51, 53, 55 -> "Garoa"
    56, 57 -> "Garoa congelante"
    61 -> "Chuva fraca"
    63 -> "Chuva moderada"
    65 -> "Chuva forte"
    66, 67 -> "Chuva congelante"
    71, 73, 75, 77 -> "Neve"
    80 -> "Pancadas de chuva fracas"
    81 -> "Pancadas de chuva moderadas"
    82 -> "Pancadas de chuva fortes"
    85, 86 -> "Pancadas de neve"
    95 -> "Tempestade com trovoadas"
    96, 99 -> "Tempestade com granizo"
    else -> "Condição desconhecida"
}
