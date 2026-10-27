package com.sosclima.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Ocorrência registrada pelo usuário (tabela principal do Room). */
@Entity(tableName = "ocorrencias")
data class Ocorrencia(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val descricao: String,
    val endereco: String,
    val tipo: TipoOcorrencia,
    val gravidade: Gravidade,
    val resolvida: Boolean = false,
    val criadaEm: Long = System.currentTimeMillis(),
)

enum class TipoOcorrencia(val rotulo: String) {
    ALAGAMENTO("Alagamento"),
    ARVORE_CAIDA("Árvore caída"),
    DESLIZAMENTO("Deslizamento"),
    FALTA_ENERGIA("Falta de energia"),
    DESTELHAMENTO("Destelhamento"),
    OUTRO("Outro"),
}

enum class Gravidade(val rotulo: String) {
    BAIXA("Baixa"),
    MEDIA("Média"),
    ALTA("Alta"),
}
