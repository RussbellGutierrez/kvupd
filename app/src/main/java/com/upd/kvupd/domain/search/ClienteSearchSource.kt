package com.upd.kvupd.domain.search

import com.upd.kvupd.data.model.FlowCliente
import javax.inject.Inject

class ClienteSearchSource @Inject constructor() {

    fun filtrar(
        lista: List<FlowCliente>,
        query: String
    ): List<FlowCliente> {

        if (query.isBlank()) return lista

        val q = query.trim()

        return lista.filter {

            it.cliente.contains(q, true) ||
                    it.nomcli.contains(q, true) ||
                    it.negocio.contains(q, true)
        }
    }
}