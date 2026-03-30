package com.upd.kvupd.ui.fragment.reportes.adapter

import com.upd.kvupd.domain.enumFile.TipoUsuario
import dagger.assisted.AssistedFactory

@AssistedFactory
interface LineasAdapterFactory {
    fun create(
        listener: LineasAdapter.Listener,
        tipoUsuario: TipoUsuario
    ): LineasAdapter
}