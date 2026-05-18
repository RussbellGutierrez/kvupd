package com.upd.kvupd.viewmodel.state

import com.upd.kvupd.data.model.core.TableAltaDatos
import com.upd.kvupd.ui.fragment.altas.modelUI.DistritoUI
import com.upd.kvupd.ui.fragment.altas.modelUI.GiroUI
import com.upd.kvupd.ui.fragment.altas.modelUI.RutaUI
import com.upd.kvupd.ui.fragment.altas.modelUI.SubGiroUI

data class AltaFormState(
    val giros: List<GiroUI>,
    val subgiros: Map<String, List<SubGiroUI>>,
    val distritos: List<DistritoUI>,
    val rutas: List<RutaUI>,
    val alta: TableAltaDatos?
)