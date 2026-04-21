package com.upd.kvupd.domain

import android.content.Context
import com.upd.kvupd.data.local.OperationSource
import com.upd.kvupd.data.model.core.TableConfiguracion
import com.upd.kvupd.domain.enumFile.TipoUsuario
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject

class OperationsImplementation @Inject constructor(
    @ApplicationContext private val context: Context,
    private val operationSource: OperationSource
): OperationsFunctions {

    override fun initWorker(): UUID {
        return operationSource.lanzarWorkerInicial()
    }

    override fun remainingWorkers(usuarioTipo: TipoUsuario): List<UUID> {
        return operationSource.lanzarWorkersRestantes(usuarioTipo)
    }

    override fun startServerWorker() {
        operationSource.lanzarServidorWorker()
    }

    override fun runCleanupNow() {
        operationSource.lanzarCleanupNow()
    }

    override fun startCleanupWorker() {
        operationSource.lanzarCleanupWorker()
    }

    override fun syncInitial(config: TableConfiguracion) {
        operationSource.syncInicial(config)
    }

    override fun reprogramBeforeConfig() {
        operationSource.reprogramarPorNuevaConfig()
    }

    override fun initBootWorker() {
        operationSource.lanzarWorkerReinicio()
    }

    override fun checkTodaySesion(config: TableConfiguracion): Boolean =
        operationSource.sesionActual(config)

    override fun validateAndRecreateAlarms() {
        operationSource.validarYRecrearAlarmasSiFaltan()
    }

    override fun programNextAlarm(modo: String) {
        operationSource.programarSiguienteAlarma(modo)
    }
}