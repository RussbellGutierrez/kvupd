package com.upd.kvupd.domain

import android.content.Context
import com.upd.kvupd.data.local.OperationSource
import com.upd.kvupd.data.model.TableConfiguracion
import com.upd.kvupd.ui.fragment.enumClass.TipoUsuario
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

    override fun syncInitial() {
        operationSource.syncInicial()
    }

    override fun reprogramBeforeConfig() {
        operationSource.reprogramarPorNuevaConfig()
    }

    override fun initBootWorker() {
        operationSource.lanzarWorkerReinicio()
    }

    override fun checkTodaySesion(config: TableConfiguracion): Boolean =
        operationSource.sesionActual(config)
}