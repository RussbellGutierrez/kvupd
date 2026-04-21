package com.upd.kvupd.domain

import com.upd.kvupd.data.model.core.TableConfiguracion
import com.upd.kvupd.domain.enumFile.TipoUsuario
import java.util.UUID

interface OperationsFunctions {

    fun initWorker(): UUID
    fun remainingWorkers(usuarioTipo: TipoUsuario): List<UUID>
    fun startServerWorker()
    fun runCleanupNow()
    fun startCleanupWorker()
    fun syncInitial(config: TableConfiguracion)
    fun reprogramBeforeConfig()
    fun initBootWorker()
    fun checkTodaySesion(config: TableConfiguracion): Boolean
    fun validateAndRecreateAlarms()
    fun programNextAlarm(modo: String)
}