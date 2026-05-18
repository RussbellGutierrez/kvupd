package com.upd.kvupd.ui.fragment.reportes.adapter.lineas

import com.upd.kvupd.domain.enumFile.TipoUsuario
import com.upd.kvupd.ui.fragment.reportes.adapter.lineas.LineasAdapter
import dagger.assisted.AssistedFactory

@AssistedFactory
interface LineasAdapterFactory {
    fun create(
        listener: LineasAdapter.Listener,
        tipoUsuario: TipoUsuario
    ): LineasAdapter
}