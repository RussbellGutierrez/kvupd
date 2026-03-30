package com.upd.kvupd.ui.fragment.baja.adapter.supervisor

import dagger.assisted.AssistedFactory

@AssistedFactory
interface BajaSuperAdapterFactory {
    fun create(
        listener: BajaSuperAdapter.Listener
    ): BajaSuperAdapter
}