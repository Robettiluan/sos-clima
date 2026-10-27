package com.sosclima.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OcorrenciaDao {

    /** Read reativo: a lista é emitida de novo sempre que a tabela muda. */
    @Query("SELECT * FROM ocorrencias ORDER BY resolvida ASC, criadaEm DESC")
    fun observarTodas(): Flow<List<Ocorrencia>>

    @Query("SELECT * FROM ocorrencias WHERE id = :id")
    suspend fun buscarPorId(id: Long): Ocorrencia?

    @Insert
    suspend fun inserir(ocorrencia: Ocorrencia): Long

    @Update
    suspend fun atualizar(ocorrencia: Ocorrencia)

    @Delete
    suspend fun excluir(ocorrencia: Ocorrencia)

    @Query("DELETE FROM ocorrencias WHERE resolvida = 1")
    suspend fun excluirResolvidas(): Int
}
