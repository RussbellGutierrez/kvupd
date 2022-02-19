package com.upd.kv.di

import android.content.Context
import com.upd.kv.domain.Functions
import com.upd.kv.ui.adapter.ClienteAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ActivityComponent::class)
object AdapterModule {

    @Provides
    fun prodiverClienteAdapter(
        @ApplicationContext ctx: Context,
        functions: Functions
    ) = ClienteAdapter(ctx, functions)
}