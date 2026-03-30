package com.upd.kvupd.ui.fragment.altas.adapter

import dagger.assisted.AssistedFactory

@AssistedFactory
interface AltaAdapterFactory {
    fun create(
        listener: AltaAdapter.Listener
    ): AltaAdapter
}