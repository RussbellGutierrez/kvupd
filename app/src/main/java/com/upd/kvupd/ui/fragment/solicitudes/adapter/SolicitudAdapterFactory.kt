package com.upd.kvupd.ui.fragment.solicitudes.adapter

import dagger.assisted.AssistedFactory

@AssistedFactory
interface SolicitudAdapterFactory {
    fun create(
        listener: SolicitudAdapter.Listener
    ): SolicitudAdapter
}