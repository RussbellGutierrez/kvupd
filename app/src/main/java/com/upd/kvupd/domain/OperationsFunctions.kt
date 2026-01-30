package com.upd.kvupd.domain

import com.upd.kvupd.data.model.TableConfiguracion
import java.util.UUID

interface OperationsFunctions {

    fun initWorker(): UUID
    fun remainingWorkers(usuarioTipo: String): List<UUID>
    fun syncInitial()
    fun reprogramBeforeConfig()
    fun initBootWorker()
    fun checkTodaySesion(config: TableConfiguracion): Boolean
}