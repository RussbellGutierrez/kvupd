package com.upd.kvupd.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class EventFlow<T> {
    private val _events = MutableSharedFlow<T>(replay = 0, extraBufferCapacity = 1)
    val events: SharedFlow<T> = _events

    suspend fun emit(value: T) {
        _events.emit(value)
    }

    fun tryEmit(value: T) {
        _events.tryEmit(value)
    }
}