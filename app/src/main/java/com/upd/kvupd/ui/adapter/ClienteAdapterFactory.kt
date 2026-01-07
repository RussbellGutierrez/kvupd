package com.upd.kvupd.ui.adapter

import dagger.assisted.AssistedFactory

@AssistedFactory
interface ClienteAdapterFactory {
    fun create(
        listener: ClienteAdapter.Listener,
        hoy: String
    ): ClienteAdapter
}