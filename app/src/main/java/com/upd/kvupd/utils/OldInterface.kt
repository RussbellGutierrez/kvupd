package com.upd.kvupd.utils

import com.upd.kvupd.domain.OldOnClosingApp
import com.upd.kvupd.domain.OldOnGpsState
import com.upd.kvupd.domain.OldOnInterSetup
import com.upd.kvupd.ui.adapter.AltaAdapter
import com.upd.kvupd.ui.adapter.BajaAdapter
import com.upd.kvupd.ui.adapter.BajaSupervisorAdapter
import com.upd.kvupd.ui.adapter.BuscarAdapter
import com.upd.kvupd.ui.adapter.ClienteAdapter
import com.upd.kvupd.ui.adapter.GenericoAdapter
import com.upd.kvupd.ui.adapter.SolesAdapter

object OldInterface {
    lateinit var clienteListener: ClienteAdapter.OnClienteListener
    //lateinit var umesListener: OldUmesAdapter.OnUmesListener
    lateinit var solesListener: SolesAdapter.OnSolesListener
    lateinit var generListener: GenericoAdapter.OnGenericoListener
    //lateinit var visisuListener: VisisuperAdapter.OnVisisuperListener
    lateinit var altaListener: AltaAdapter.OnAltaListener
    lateinit var bajaListener: BajaAdapter.OnBajaListener
    lateinit var bajaSuperListener: BajaSupervisorAdapter.OnBajaSuperListener
    lateinit var buscarListener: BuscarAdapter.OnBuscarListener
    var closeListener: OldOnClosingApp? = null
    var interListener: OldOnInterSetup? = null
    var gpsListener: OldOnGpsState? = null
}