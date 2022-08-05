package com.upd.kvupd.utils

import com.upd.kvupd.domain.ServiceWork
import com.upd.kvupd.service.ServiceSetup
import com.upd.kvupd.ui.activity.MainActivity
import com.upd.kvupd.ui.adapter.*

object Interface {

    lateinit var clienteListener: ClienteAdapter.OnClienteListener
    lateinit var umesListener: UmesAdapter.OnUmesListener
    lateinit var solesListener: SolesAdapter.OnSolesListener
    lateinit var generListener: GenericoAdapter.OnGenericoListener
    lateinit var visisuListener: VisisuperAdapter.OnVisisuperListener
    lateinit var altaListener: AltaAdapter.OnAltaListener
    lateinit var bajaListener: BajaAdapter.OnBajaListener
    lateinit var bajaSuperListener: BajaSupervisorAdapter.OnBajaSuperListener
    var serviceListener: ServiceSetup.OnServiceListener? = null
    var servworkListener: ServiceWork? = null
    var mainListener: MainActivity.OnMainListener? = null
}