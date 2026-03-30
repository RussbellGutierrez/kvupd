package com.upd.kvupd.utils

import com.upd.kvupd.domain.OldOnClosingApp
import com.upd.kvupd.domain.OldOnGpsState
import com.upd.kvupd.domain.OldOnInterSetup
import com.upd.kvupd.ui.adapter.OldBajaSupervisorAdapter
import com.upd.kvupd.ui.adapter.OldBuscarAdapter
import com.upd.kvupd.ui.adapter.OldClienteAdapter

object OldInterface {
    lateinit var clienteListener: OldClienteAdapter.OnClienteListener
    //lateinit var umesListener: OldUmesAdapter.OnUmesListener
    //lateinit var solesListener: OldSolesAdapter.OnSolesListener
    //lateinit var generListener: OldGenericoAdapter.OnGenericoListener
    //lateinit var visisuListener: VisisuperAdapter.OnVisisuperListener
    //lateinit var altaListener: AltaAdapter.OnAltaListener
    //lateinit var bajaListener: BajaAdapter.OnBajaListener
    lateinit var bajaSuperListener: OldBajaSupervisorAdapter.OnBajaSuperListener
    lateinit var buscarListener: OldBuscarAdapter.OnBuscarListener
    var closeListener: OldOnClosingApp? = null
    var interListener: OldOnInterSetup? = null
    var gpsListener: OldOnGpsState? = null
}