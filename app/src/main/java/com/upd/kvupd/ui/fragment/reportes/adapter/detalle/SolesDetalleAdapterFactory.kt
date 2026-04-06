package com.upd.kvupd.ui.fragment.reportes.adapter.detalle

import dagger.assisted.AssistedFactory

@AssistedFactory
interface SolesDetalleAdapterFactory {
    fun create(
        listener: SolesDetalleAdapter.Listener
    ): SolesDetalleAdapter
}