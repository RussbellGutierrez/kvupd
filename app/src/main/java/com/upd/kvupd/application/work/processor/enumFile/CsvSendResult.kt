package com.upd.kvupd.application.work.processor.enumFile

enum class CsvSendResult {
    SUCCESS,   // enviado OK
    RETRY,     // sin respuesta / timeout / red
    DISCARD    // servidor respondió error definitivo
}