package com.upd.kvupd.di

import android.content.Context
import com.upd.kvupd.domain.OldFunctions
import com.upd.kvupd.domain.OldRepository
import com.upd.kvupd.ui.adapter.AltaAdapter
import com.upd.kvupd.ui.adapter.BajaAdapter
import com.upd.kvupd.ui.adapter.BajaSupervisorAdapter
import com.upd.kvupd.ui.adapter.BajaVendedorAdapter
import com.upd.kvupd.ui.adapter.BuscarAdapter
import com.upd.kvupd.ui.adapter.ClienteAdapter
import com.upd.kvupd.ui.adapter.GenericoAdapter
import com.upd.kvupd.ui.adapter.IncidenciaAdapter
import com.upd.kvupd.ui.adapter.SolesAdapter
import com.upd.kvupd.ui.adapter.OldUmesAdapter
import com.upd.kvupd.ui.adapter.VisisuperAdapter
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
    ) = ClienteAdapter(ctx, repository)

    @Provides
    fun prodiverUmeAdapter() = OldUmesAdapter()

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
        functions: OldFunctions
    ) = BajaVendedorAdapter(functions)

    @Provides
    fun providerIncidenciaAdapter() = IncidenciaAdapter()

    @Provides
    fun providerBuscarAdapter() = BuscarAdapter()

}