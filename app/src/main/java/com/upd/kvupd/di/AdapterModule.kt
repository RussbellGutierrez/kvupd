package com.upd.kvupd.di

import android.content.Context
import com.upd.kvupd.domain.Functions
import com.upd.kvupd.domain.Repository
import com.upd.kvupd.ui.adapter.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AdapterModule {

    @Provides
    fun prodiverClienteAdapter(
        @ApplicationContext ctx: Context,
        functions: Functions,
        repository: Repository
    ) = ClienteAdapter(ctx, functions, repository)

    @Provides
    fun prodiverUmeAdapter() = UmesAdapter()

    @Provides
    fun prodiverSolesAdapter() = SolesAdapter()

    @Provides
    fun prodiverGenericoAdapter() = GenericoAdapter()

    @Provides
    fun prodiverVisisuperAdapter() = VisisuperAdapter()

    @Provides
    fun prodiverAltaAdapter() = AltaAdapter()

    @Provides
    fun providerBajaAdapter() = BajaAdapter()

    @Provides
    fun providerBajaSupervisorAdapter() = BajaSupervisorAdapter()

    @Provides
    fun providerBajaVendedorAdapter(
        functions: Functions
    ) = BajaVendedorAdapter(functions)

    @Provides
    fun providerIncidenciaAdapter() = IncidenciaAdapter()

}