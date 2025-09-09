package com.upd.kvupd.domain

interface OldRepository {
    /*//  Room Functions
    fun getFlowConfig(): Flow<List<TConfiguracion>>
    fun getFlowSession(): Flow<TSesion?>
    fun getFlowRowCliente(): Flow<List<RowCliente>>
    fun getFlowLocation(): Flow<List<TSeguimiento>?>
    fun getFlowMarker(observacion: String): Flow<List<MarkerMap>>
    fun getFlowAltas(): Flow<List<TAlta>>
    fun getFlowDistritos(): Flow<List<TDistrito>>
    fun getFlowNegocios(): Flow<List<TNegocio>>
    fun getFlowBajas(): Flow<List<TBaja>>
    fun getFlowRowBaja(): Flow<List<RowBaja>>
    fun getFlowRutas(): Flow<List<TRutas>>
    fun getFlowIncidencias(): Flow<List<TIncidencia>>

    suspend fun getSesion(): TSesion?
    suspend fun getConfig(): TConfiguracion?
    suspend fun getClientes(): List<Cliente>
    suspend fun getEmpleados(): List<Vendedor>
    suspend fun getDistritos(): List<Distrito>
    suspend fun getNegocios(): List<Negocio>
    suspend fun getRutas(): List<Ruta>
    suspend fun getListEncuestas(): List<Cabecera>
    suspend fun getPreguntas(): List<TEncuesta>
    suspend fun getConsultaCliente(numero: String, nombre: String): List<TConsulta>
    suspend fun getClienteDetail(cliente: String, observacion: String): List<DataCliente>
    suspend fun getDataAlta(alta: String): DataAlta
    suspend fun getAltaDatoSpecific(alta: String): TADatos?
    suspend fun getBajaSuperSpecific(codigo: String, fecha: String): TBajaSuper
    suspend fun isClienteBaja(cliente: String): Boolean
    suspend fun getLastAux(): Int?
    suspend fun processAlta(fecha: String, location: Location)
    suspend fun processAAux(codigo: Int)
    suspend fun isDataToday(): Int
    suspend fun getStarterTime(): Long
    suspend fun getFinishTime(): Long
    suspend fun getIntoHours(): Boolean
    suspend fun getSeleccionado(): TEncuestaSeleccionado?
    suspend fun clienteRespondioActual(cliente: String): Boolean
    suspend fun clienteRespondioAntes(cliente: String): String

    suspend fun saveSesion(config: Config)
    suspend fun saveConfiguracion(config: List<Config>)
    suspend fun saveClientes(cliente: List<Cliente>)
    suspend fun saveEmpleados(empleado: List<Vendedor>)
    suspend fun saveDistritos(distrito: List<Distrito>)
    suspend fun saveRutas(ruta: List<Ruta>)
    suspend fun saveNegocios(negocio: List<Negocio>)
    suspend fun saveEncuesta(encuesta: List<Encuesta>)
    suspend fun saveConsulta(consulta: List<Consulta>)
    suspend fun saveSeguimiento(seguimiento: TSeguimiento)
    suspend fun saveVisita(visita: TVisita)
    suspend fun saveEstado(estado: TEstado)
    suspend fun saveBaja(baja: TBaja)
    suspend fun saveAlta(alta: TAlta)
    suspend fun saveAltaDatos(da: TADatos)
    suspend fun saveAAux(aux: TAAux)
    suspend fun saveBajaSuper(baja: List<BajaSupervisor>)
    suspend fun saveBajaEstado(estado: TBEstado)
    suspend fun saveSeleccionado(selec: TEncuestaSeleccionado)
    suspend fun saveRespuesta(respuesta: List<TRespuesta>)
    suspend fun saveIncidencia(respuesta: TIncidencia)
    suspend fun saveAltaFoto(respuesta: TAFoto)

    suspend fun getServerSeguimiento(estado: String): List<TSeguimiento>
    suspend fun getServerVisita(estado: String): List<TVisita>
    suspend fun getServerAlta(estado: String): List<TAlta>
    suspend fun getServerAltadatos(estado: String): List<TADatos>
    suspend fun getServerBaja(estado: String): List<TBaja>
    suspend fun getServerBajaestado(estado: String): List<TBEstado>
    suspend fun getServerRespuesta(estado: String): List<TRespuesta>
    suspend fun getServerFoto(estado: String): List<TRespuesta>
    suspend fun getServerAltaFoto(estado: String): List<TAFoto>

    suspend fun updateSeguimiento(coordenada: TSeguimiento)
    suspend fun updateVisita(visita: TVisita)
    suspend fun updateAlta(alta: TAlta)
    suspend fun updateBaja(baja: TBaja)
    suspend fun updateBajaEstado(be: TBEstado)
    suspend fun updateLocationAlta(locationAlta: LocationAlta)
    suspend fun updateMiniAlta(miniUpdAlta: MiniUpdAlta)
    suspend fun updateAltaDatos(upd: TADatos)
    suspend fun updateAltaFoto(foto: TAFoto)
    suspend fun updateMiniBaja(miniUpdBaja: MiniUpdBaja)
    suspend fun updateRespuesta(respuesta: TRespuesta)

    suspend fun deleteConfig()
    suspend fun deleteClientes()
    suspend fun deleteEmpleados()
    suspend fun deleteDistritos()
    suspend fun deleteNegocios()
    suspend fun deleteRutas()
    suspend fun deleteEncuesta()
    suspend fun deleteConsulta()
    suspend fun deleteSeguimiento()
    suspend fun deleteVisita()
    suspend fun deleteEstado()
    suspend fun deleteBaja()
    suspend fun deleteAlta()
    suspend fun deleteAltaDatos()
    suspend fun deleteBajaSuper()
    suspend fun deleteBajaEstado()
    suspend fun deleteEncuestaSeleccionado()
    suspend fun deleteRespuesta()
    suspend fun deleteIncidencia()
    suspend fun deleteAFoto()
    suspend fun deleteAAux()

    //  Retrofit Functions
    /*suspend fun loginAdministrator(body: RequestBody): Flow<OldNetworkRetrofit<Login>>
    suspend fun registerWebDevice(body: RequestBody): Flow<OldNetworkRetrofit<JObj>>
    suspend fun getWebConfiguracion(body: RequestBody): Flow<OldNetworkRetrofit<JConfig>>
    suspend fun getWebClientes(body: RequestBody): Flow<OldNetworkRetrofit<JCliente>>
    suspend fun getWebEmpleados(body: RequestBody): Flow<OldNetworkRetrofit<JVendedores>>
    suspend fun getWebDistritos(body: RequestBody): Flow<OldNetworkRetrofit<JDistrito>>
    suspend fun getWebNegocios(body: RequestBody): Flow<OldNetworkRetrofit<JNegocio>>
    suspend fun getWebRutas(body: RequestBody): Flow<OldNetworkRetrofit<JRuta>>
    suspend fun getWebEncuesta(body: RequestBody): Flow<OldNetworkRetrofit<JEncuesta>>
    suspend fun getWebConsulta(body: RequestBody): Flow<OldNetworkRetrofit<JConsulta>>

    suspend fun getWebPreventa(body: RequestBody): Flow<OldNetworkRetrofit<JVolumen>>
    suspend fun getWebCobertura(body: RequestBody): Flow<OldNetworkRetrofit<JCobCart>>
    suspend fun getWebCartera(body: RequestBody): Flow<OldNetworkRetrofit<JCobCart>>
    suspend fun getWebPedidos(body: RequestBody): Flow<OldNetworkRetrofit<JPedido>>
    suspend fun getWebCambiosCli(body: RequestBody): Flow<OldNetworkRetrofit<JCambio>>
    suspend fun getWebCambiosEmp(body: RequestBody): Flow<OldNetworkRetrofit<JCambio>>
    suspend fun getWebVisicooler(body: RequestBody): Flow<OldNetworkRetrofit<JVisicooler>>
    suspend fun getWebVisisuper(body: RequestBody): Flow<OldNetworkRetrofit<JVisisuper>>
    suspend fun getWebUmes(body: RequestBody): Flow<OldNetworkRetrofit<JUmes>>
    suspend fun getWebSoles(body: RequestBody): Flow<OldNetworkRetrofit<JSoles>>

    suspend fun getWebUmesGenerico(body: RequestBody): Flow<OldNetworkRetrofit<JGenerico>>
    suspend fun getWebSolesGenerico(body: RequestBody): Flow<OldNetworkRetrofit<JGenerico>>
    suspend fun getWebUmesDetalle(body: RequestBody): Flow<OldNetworkRetrofit<JGenerico>>
    suspend fun getWebCoberturaDetalle(body: RequestBody): Flow<OldNetworkRetrofit<JDetCob>>
    suspend fun getWebSolesDetalle(body: RequestBody): Flow<OldNetworkRetrofit<JGenerico>>
    suspend fun getWebCoberturaPendiente(body: RequestBody): Flow<OldNetworkRetrofit<JCoberturados>>
    suspend fun getWebPedidosRealizados(body: RequestBody): Flow<OldNetworkRetrofit<JPediGen>>

    suspend fun getWebPedimap(body: RequestBody): Flow<OldNetworkRetrofit<JPedimap>>
    suspend fun getWebBajaVendedor(body: RequestBody): Flow<OldNetworkRetrofit<JBajaVendedor>>
    suspend fun getWebBajaSupervisor(body: RequestBody): Flow<OldNetworkRetrofit<JBajaSupervisor>>

    //  Send Server
    suspend fun setWebSeguimiento(body: RequestBody): Flow<OldNetworkRetrofit<JObj>>
    suspend fun setWebVisita(body: RequestBody): Flow<OldNetworkRetrofit<JObj>>
    suspend fun setWebAlta(body: RequestBody): Flow<OldNetworkRetrofit<JObj>>
    suspend fun setWebAltaDatos(body: RequestBody): Flow<OldNetworkRetrofit<JObj>>
    suspend fun setWebBaja(body: RequestBody): Flow<OldNetworkRetrofit<JObj>>
    suspend fun setWebBajaEstados(body: RequestBody): Flow<OldNetworkRetrofit<JObj>>
    suspend fun setWebRespuestas(body: RequestBody): Flow<OldNetworkRetrofit<JObj>>
    suspend fun setWebFotos(body: RequestBody): Flow<OldNetworkRetrofit<JFoto>>
    suspend fun setWebAltaFotos(body: RequestBody): Flow<OldNetworkRetrofit<JFoto>>*/
    */
}