package com.upd.kventas.di

import android.content.Context
import com.upd.kventas.domain.Functions
import com.upd.kventas.ui.adapter.*
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

    @Provides
    fun prodiverUmeAdapter() = UmesAdapter()

    @Provides
    fun prodiverSolesAdapter() = SolesAdapter()

    @Provides
    fun prodiverGenericoAdapter() = GenericoAdapter()

    @Provides
    fun prodiverVisisuperAdapter() = VisisuperAdapter()
}