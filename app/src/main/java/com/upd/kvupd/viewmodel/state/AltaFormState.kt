package com.upd.kvupd.viewmodel.state

import com.upd.kvupd.data.model.TableAltaDatos
import com.upd.kvupd.ui.fragment.encuesta.modelUI.DistritoUI
import com.upd.kvupd.ui.fragment.encuesta.modelUI.GiroUI
import com.upd.kvupd.ui.fragment.encuesta.modelUI.RutaUI
import com.upd.kvupd.ui.fragment.encuesta.modelUI.SubGiroUI

data class AltaFormState(
    val giros: List<GiroUI>,
    val subgiros: Map<String, List<SubGiroUI>>,
    val distritos: List<DistritoUI>,
    val rutas: List<RutaUI>,
    val alta: TableAltaDatos?
)