package com.sosclima.app.domain

/** Cidade monitorada. As coordenadas são enviadas para a API Open-Meteo. */
data class Cidade(
    val id: String,
    val nome: String,
    val latitude: Double,
    val longitude: Double,
)

object Cidades {
    val todas: List<Cidade> = listOf(
        Cidade("sao-paulo", "São Paulo - SP", -23.5505, -46.6333),
        Cidade("pato-branco", "Pato Branco - PR", -26.2289, -52.6708),
        Cidade("curitiba", "Curitiba - PR", -25.4284, -49.2733),
        Cidade("porto-alegre", "Porto Alegre - RS", -30.0346, -51.2177),
        Cidade("rio-de-janeiro", "Rio de Janeiro - RJ", -22.9068, -43.1729),
        Cidade("belo-horizonte", "Belo Horizonte - MG", -19.9167, -43.9345),
        Cidade("recife", "Recife - PE", -8.0476, -34.8770),
    )

    val padrao: Cidade = todas.first()

    fun porId(id: String?): Cidade = todas.firstOrNull { it.id == id } ?: padrao
}
