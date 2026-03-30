package com.upd.kvupd.ui.fragment.reportes.adapter

import com.upd.kvupd.domain.enumFile.TipoUsuario
import dagger.assisted.AssistedFactory

@AssistedFactory
interface KpiAdapterFactory {
    fun create(
        listener: KpiAdapter.Listener,
        tipoUsuario: TipoUsuario
    ): KpiAdapter
}