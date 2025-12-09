package com.upd.kvupd.di

import android.content.Context
import com.upd.kvupd.domain.OldFunctions
import com.upd.kvupd.domain.OldRepository
import com.upd.kvupd.ui.adapter.OldAltaAdapter
import com.upd.kvupd.ui.adapter.OldBajaAdapter
import com.upd.kvupd.ui.adapter.OldBajaSupervisorAdapter
import com.upd.kvupd.ui.adapter.OldBajaVendedorAdapter
import com.upd.kvupd.ui.adapter.OldBuscarAdapter
import com.upd.kvupd.ui.adapter.OldClienteAdapter
import com.upd.kvupd.ui.adapter.OldGenericoAdapter
import com.upd.kvupd.ui.adapter.OldIncidenciaAdapter
import com.upd.kvupd.ui.adapter.OldSolesAdapter
import com.upd.kvupd.ui.adapter.OldUmesAdapter
import com.upd.kvupd.ui.adapter.OldVisisuperAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AdapterModule {

    @Provides
    fun prodiverClienteAdapter(
        @ApplicationContext ctx: Context,
        repository: OldRepository
    ) = OldClienteAdapter(ctx, repository)

    @Provides
    fun prodiverUmeAdapter() = OldUmesAdapter()

    @Provides
    fun prodiverSolesAdapter() = OldSolesAdapter()

    @Provides
    fun prodiverGenericoAdapter() = OldGenericoAdapter()

    @Provides
    fun prodiverVisisuperAdapter() = OldVisisuperAdapter()

    @Provides
    fun prodiverAltaAdapter() = OldAltaAdapter()

    @Provides
    fun providerBajaAdapter() = OldBajaAdapter()

    @Provides
    fun providerBajaSupervisorAdapter() = OldBajaSupervisorAdapter()

    @Provides
    fun providerBajaVendedorAdapter(
        functions: OldFunctions
    ) = OldBajaVendedorAdapter(functions)

    @Provides
    fun providerIncidenciaAdapter() = OldIncidenciaAdapter()

    @Provides
    fun providerBuscarAdapter() = OldBuscarAdapter()

}