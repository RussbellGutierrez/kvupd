package com.upd.kvupd.domain.search

import com.upd.kvupd.data.model.FlowBajaSupervisor
import com.upd.kvupd.data.model.core.TableBaja
import javax.inject.Inject

class BajaSearchSource @Inject constructor() {

    fun filtrarGeneradas(
        lista: List<TableBaja>,
        query: String
    ): List<TableBaja> {
        if (query.isBlank()) return lista

        val q = query.trim().lowercase()

        return lista.filter {
            it.cliente.contains(q) ||
                    it.nombre.lowercase().contains(q)
        }
    }

    fun filtrarSupervisor(
        lista: List<FlowBajaSupervisor>,
        query: String
    ): List<FlowBajaSupervisor> {
        if (query.isBlank()) return lista

        val q = query.trim().lowercase()

        return lista.filter {
            it.cliente.contains(q) ||
                    it.nombre.lowercase().contains(q) ||
                    it.vendedor.lowercase().contains(q)
        }
    }
}