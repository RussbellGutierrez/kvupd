package com.upd.kvupd.domain.upload

import com.upd.kvupd.data.model.core.TableAlta
import com.upd.kvupd.data.model.core.TableAltaDatos
import com.upd.kvupd.data.model.core.TableBaja
import com.upd.kvupd.data.model.core.TableBajaProcesada
import com.upd.kvupd.data.model.core.TableConfiguracion
import com.upd.kvupd.data.model.core.TableFoto
import com.upd.kvupd.data.model.core.TableRespuesta
import com.upd.kvupd.data.model.core.TableSeguimiento
import com.upd.kvupd.domain.RoomFunctions
import com.upd.kvupd.domain.send.SendServerFunctions
import com.upd.kvupd.ui.fragment.servidor.enumFile.ApiServerStatus
import com.upd.kvupd.ui.fragment.servidor.enumFile.UploadType
import com.upd.kvupd.ui.fragment.servidor.modelUI.UploadConfig
import com.upd.kvupd.ui.sealed.ResultadoApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

class UploadManager @Inject constructor(
    private val roomFunctions: RoomFunctions,
    private val sendServerFunctions: SendServerFunctions
) {

    suspend fun uploadAll(
        extraParam: String?,
        onStatus: (UploadType, ApiServerStatus) -> Unit = { _, _ -> },
        onProgress: (UploadType, Int, Int) -> Unit = { _, _, _ -> },
        onError: (UploadType, List<String>) -> Unit = { _, _ -> }
    ) {

        val configGlobal = roomFunctions.queryConfiguracion()

        val uploadConfigs = listOf(

            UploadConfig(
                type = UploadType.GPS,
                getData = { roomFunctions.apiServerSeguimiento(false) },
                send = { item ->
                    val param = extraParam ?: return@UploadConfig ResultadoApi.Fallo(
                        IllegalStateException("Parametro requerido")
                    )
                    sendServerFunctions.enviarSeguimiento(item as TableSeguimiento, param)
                }
            ),

            UploadConfig(
                type = UploadType.ALTAS,
                getData = { roomFunctions.apiServerAltas(false) },
                send = { sendServerFunctions.enviarAlta(it as TableAlta) }
            ),

            UploadConfig(
                type = UploadType.ALTA_DATOS,
                getData = { roomFunctions.apiServerAltaDatos(false) },
                send = { sendServerFunctions.enviarAltaDatos(it as TableAltaDatos) }
            ),

            UploadConfig(
                type = UploadType.BAJAS,
                getData = { roomFunctions.apiServerBajas(false) },
                send = { sendServerFunctions.enviarBaja(it as TableBaja) }
            ),

            UploadConfig(
                type = UploadType.BAJA_REVISADA,
                getData = { roomFunctions.apiServerBajasProcesadas(false) },
                send = { sendServerFunctions.enviarBajaProcesada(it as TableBajaProcesada) }
            ),

            UploadConfig(
                type = UploadType.ENCUESTAS,
                getData = { roomFunctions.apiServerRespuestas(false) },
                send = { item ->
                    sendServerFunctions.enviarRespuesta(
                        listOf(item as TableRespuesta)
                    )
                }
            ),

            UploadConfig(
                type = UploadType.FOTOS,
                getData = { roomFunctions.apiServerFotos(false) },
                send = { sendServerFunctions.enviarFoto(it as TableFoto) }
            )
        )

        coroutineScope {
            uploadConfigs.map { config ->
                launch {
                    processUpload(config, configGlobal, onStatus, onProgress, onError)
                }
            }.joinAll()
        }
    }

    private suspend fun processUpload(
        config: UploadConfig,
        configGlobal: TableConfiguracion?,
        onStatus: (UploadType, ApiServerStatus) -> Unit,
        onProgress: (UploadType, Int, Int) -> Unit,
        onError: (UploadType, List<String>) -> Unit
    ) {

        if (config.type == UploadType.GPS && configGlobal?.seguimiento != 1) {
            onStatus(config.type, ApiServerStatus.SUCCESS)
            return
        }

        val data = config.getData()

        if (data.isEmpty()) {
            onStatus(config.type, ApiServerStatus.SUCCESS)
            return
        }

        onStatus(config.type, ApiServerStatus.LOADING)

        val errores = mutableListOf<String>()

        data.forEachIndexed { index, item ->

            val result = config.send(item)

            when (result) {
                is ResultadoApi.Exito -> {
                    onProgress(
                        config.type,
                        index + 1,
                        data.size - (index + 1)
                    )
                }

                is ResultadoApi.ErrorHttp,
                is ResultadoApi.Fallo -> {
                    result.mensajeUsuario()?.let { errores.add(it) }
                }

                else -> Unit
            }
        }

        val finalStatus =
            if (errores.isEmpty()) ApiServerStatus.SUCCESS
            else ApiServerStatus.ERROR

        onStatus(config.type, finalStatus)

        if (errores.isNotEmpty()) {
            onError(config.type, errores)
        }
    }
}