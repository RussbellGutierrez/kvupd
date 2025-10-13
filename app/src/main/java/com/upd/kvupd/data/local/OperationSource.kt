package com.upd.kvupd.data.local

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.upd.kvupd.application.work.ClientesWorker
import com.upd.kvupd.application.work.ConfiguracionWorker
import com.upd.kvupd.application.work.EmpleadosWorker
import com.upd.kvupd.ui.sealed.TipoUsuario
import com.upd.kvupd.utils.WorkerTags.WORK_CLIENTE
import com.upd.kvupd.utils.WorkerTags.WORK_CONFIGURACION
import com.upd.kvupd.utils.WorkerTags.WORK_EMPLEADO
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class OperationSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager
) {

    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun launchWorkers(usuarioTipo: String) {
        val tipo = TipoUsuario.inicialTipo(usuarioTipo)

        val lista: List<OneTimeWorkRequest> = when(tipo){
            TipoUsuario.Vendedor -> listOf(workerClientes())
            TipoUsuario.Supervisor -> listOf(workerEmpleados())
        }
        workManager
            .beginWith(workerConfiguracion())
            .then(lista)
            .enqueue()
    }

    private fun workerConfiguracion() =
        OneTimeWorkRequestBuilder<ConfiguracionWorker>()
            .addTag(WORK_CONFIGURACION)
            .setConstraints(constraints)
            .build()

    private fun workerClientes() =
        OneTimeWorkRequestBuilder<ClientesWorker>()
            .addTag(WORK_CLIENTE)
            .setConstraints(constraints)
            .build()

    private fun workerEmpleados() =
        OneTimeWorkRequestBuilder<EmpleadosWorker>()
            .addTag(WORK_EMPLEADO)
            .setConstraints(constraints)
            .build()

    /*fun workerDistritos() =
        OneTimeWorkRequestBuilder<DistritosWork>()
            .addTag(WORK_DISTRITO)
            .setConstraints(constraints)
            .build()

    fun workerNegocios() =
        OneTimeWorkRequestBuilder<NegociosWork>()
            .addTag(WORK_NEGOCIO)
            .setConstraints(constraints)
            .build()

    fun workerRutas() =
        OneTimeWorkRequestBuilder<RutasWork>()
            .addTag(WORK_RUTA)
            .setConstraints(constraints)
            .build()

    fun workerEncuestas() =
        OneTimeWorkRequestBuilder<EncuestaWork>()
            .addTag(WORK_ENCUESTA)
            .setConstraints(constraints)
            .build()*/
}