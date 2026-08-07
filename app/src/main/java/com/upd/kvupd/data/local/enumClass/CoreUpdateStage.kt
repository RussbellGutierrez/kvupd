package com.upd.kvupd.data.local.enumClass

enum class CoreUpdateStage {
    READ_VERSION,
    VALIDATE_VERSION,
    BACKUP_CONFIGURATION,
    EXPORT_PENDING,
    DELETE_OLD_DATABASE,
    CREATE_NEW_DATABASE,
    RESTORE_CONFIGURATION
}