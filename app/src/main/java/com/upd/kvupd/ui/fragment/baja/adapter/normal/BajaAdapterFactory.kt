package com.upd.kvupd.ui.fragment.baja.adapter.normal

import dagger.assisted.AssistedFactory

@AssistedFactory
interface BajaAdapterFactory {
    fun create(
        listener: BajaAdapter.Listener
    ): BajaAdapter
}