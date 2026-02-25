package com.upd.kvupd.ui.adapter

import dagger.assisted.AssistedFactory

@AssistedFactory
interface BajaAdapterFactory {
    fun create(
        listener: BajaAdapter.Listener
    ): BajaAdapter
}