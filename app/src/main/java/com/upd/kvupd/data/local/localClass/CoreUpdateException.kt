package com.upd.kvupd.data.local.localClass

import com.upd.kvupd.data.local.enumClass.CoreUpdateStage

class CoreUpdateException(
    val stage: CoreUpdateStage,
    val installedVersion: Int,
    val targetVersion: Int,
    cause: Throwable
) : IllegalStateException(
    "Falló CoreRoom en $stage: $installedVersion -> $targetVersion",
    cause
)