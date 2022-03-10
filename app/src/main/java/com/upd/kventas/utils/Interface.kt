package com.upd.kventas.utils

import com.upd.kventas.service.ServiceSetup
import com.upd.kventas.ui.adapter.*

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
}