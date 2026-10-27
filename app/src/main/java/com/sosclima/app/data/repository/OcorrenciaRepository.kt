package com.sosclima.app.data.repository

import com.sosclima.app.data.local.Ocorrencia
import com.sosclima.app.data.local.OcorrenciaDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/** Única porta de acesso às ocorrências. O ViewModel não conhece o DAO. */
class OcorrenciaRepository(
    private val dao: OcorrenciaDao,
    private val io: CoroutineDispatcher = Dispatchers.IO,
) {
    fun observarTodas(): Flow<List<Ocorrencia>> = dao.observarTodas()

    suspend fun buscarPorId(id: Long): Ocorrencia? = withContext(io) { dao.buscarPorId(id) }

    /** Cria se o id for 0, senão atualiza. */
    suspend fun salvar(ocorrencia: Ocorrencia) = withContext(io) {
        if (ocorrencia.id == 0L) dao.inserir(ocorrencia) else dao.atualizar(ocorrencia)
    }

    suspend fun alternarResolvida(ocorrencia: Ocorrencia) = withContext(io) {
        dao.atualizar(ocorrencia.copy(resolvida = !ocorrencia.resolvida))
    }

    suspend fun excluir(ocorrencia: Ocorrencia) = withContext(io) { dao.excluir(ocorrencia) }

    suspend fun excluirResolvidas(): Int = withContext(io) { dao.excluirResolvidas() }
}
