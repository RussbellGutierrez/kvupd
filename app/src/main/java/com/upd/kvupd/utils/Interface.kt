package com.upd.kvupd.utils

import com.upd.kvupd.domain.OnClosingApp
import com.upd.kvupd.domain.OnGpsState
import com.upd.kvupd.domain.OnInterSetup
import com.upd.kvupd.ui.adapter.AltaAdapter
import com.upd.kvupd.ui.adapter.BajaAdapter
import com.upd.kvupd.ui.adapter.BajaSupervisorAdapter
import com.upd.kvupd.ui.adapter.ClienteAdapter
import com.upd.kvupd.ui.adapter.GenericoAdapter
import com.upd.kvupd.ui.adapter.SolesAdapter
import com.upd.kvupd.ui.adapter.UmesAdapter
import com.upd.kvupd.ui.adapter.VisisuperAdapter

object Interface {
    lateinit var clienteListener: ClienteAdapter.OnClienteListener
    lateinit var umesListener: UmesAdapter.OnUmesListener
    lateinit var solesListener: SolesAdapter.OnSolesListener
    lateinit var generListener: GenericoAdapter.OnGenericoListener
    lateinit var visisuListener: VisisuperAdapter.OnVisisuperListener
    lateinit var altaListener: AltaAdapter.OnAltaListener
    lateinit var bajaListener: BajaAdapter.OnBajaListener
    lateinit var bajaSuperListener: BajaSupervisorAdapter.OnBajaSuperListener
    var closeListener: OnClosingApp? = null
    var interListener: OnInterSetup? = null
    var gpsListener: OnGpsState? = null
}