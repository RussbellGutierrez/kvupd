package com.upd.kvupd.data.local

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.upd.kvupd.application.work.ClientesWorker
import com.upd.kvupd.application.work.ConfiguracionWorker
import com.upd.kvupd.application.work.DistritosWorker
import com.upd.kvupd.application.work.EmpleadosWorker
import com.upd.kvupd.application.work.EncuestasWorker
import com.upd.kvupd.application.work.NegociosWorker
import com.upd.kvupd.application.work.RutasWorker
import com.upd.kvupd.ui.sealed.TipoUsuario
import com.upd.kvupd.utils.WorkerTags.WORK_CLIENTE
import com.upd.kvupd.utils.WorkerTags.WORK_CONFIGURACION
import com.upd.kvupd.utils.WorkerTags.WORK_DISTRITO
import com.upd.kvupd.utils.WorkerTags.WORK_EMPLEADO
import com.upd.kvupd.utils.WorkerTags.WORK_ENCUESTA
import com.upd.kvupd.utils.WorkerTags.WORK_NEGOCIO
import com.upd.kvupd.utils.WorkerTags.WORK_RUTA
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
            TipoUsuario.Vendedor -> listOf(workerClientes(),workerDistritos(),workerNegocios(),workerRutas(),workerEncuestas())
            TipoUsuario.Supervisor -> listOf(workerEmpleados(),workerDistritos(),workerNegocios(),workerRutas(),workerEncuestas())
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

    private fun workerDistritos() =
        OneTimeWorkRequestBuilder<DistritosWorker>()
            .addTag(WORK_DISTRITO)
            .setConstraints(constraints)
            .build()

    private fun workerNegocios() =
        OneTimeWorkRequestBuilder<NegociosWorker>()
            .addTag(WORK_NEGOCIO)
            .setConstraints(constraints)
            .build()

    private fun workerRutas() =
        OneTimeWorkRequestBuilder<RutasWorker>()
            .addTag(WORK_RUTA)
            .setConstraints(constraints)
            .build()

    private fun workerEncuestas() =
        OneTimeWorkRequestBuilder<EncuestasWorker>()
            .addTag(WORK_ENCUESTA)
            .setConstraints(constraints)
            .build()
}