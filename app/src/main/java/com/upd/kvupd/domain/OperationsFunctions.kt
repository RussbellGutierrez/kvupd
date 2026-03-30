package com.upd.kvupd.domain

import com.upd.kvupd.data.model.TableConfiguracion
import com.upd.kvupd.domain.enumFile.TipoUsuario
import java.util.UUID

interface OperationsFunctions {

    fun initWorker(): UUID
    fun remainingWorkers(usuarioTipo: TipoUsuario): List<UUID>
    fun syncInitial()
    fun reprogramBeforeConfig()
    fun initBootWorker()
    fun checkTodaySesion(config: TableConfiguracion): Boolean
}