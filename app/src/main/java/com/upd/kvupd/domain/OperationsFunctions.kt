package com.upd.kvupd.domain

import java.util.UUID

interface OperationsFunctions {

    fun initWorker(): UUID
    fun remainingWorkers(usuarioTipo: String): List<UUID>
    fun syncModeAlarms()
}