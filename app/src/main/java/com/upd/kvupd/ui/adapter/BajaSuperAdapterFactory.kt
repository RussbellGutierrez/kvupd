package com.upd.kvupd.ui.adapter

import dagger.assisted.AssistedFactory

@AssistedFactory
interface BajaSuperAdapterFactory {
    fun create(
        listener: BajaSuperAdapter.Listener
    ): BajaSuperAdapter
}