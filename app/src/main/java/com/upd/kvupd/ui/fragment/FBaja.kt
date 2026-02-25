package com.upd.kvupd.ui.fragment

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.upd.kvupd.R
import com.upd.kvupd.data.model.BajaVendedor
import com.upd.kvupd.data.model.FlowBajaSupervisor
import com.upd.kvupd.data.model.JsonBajaSupervisor
import com.upd.kvupd.data.model.JsonBajaVendedor
import com.upd.kvupd.data.model.TableBaja
import com.upd.kvupd.databinding.FragmentFBajaBinding
import com.upd.kvupd.ui.adapter.BajaAdapter
import com.upd.kvupd.ui.adapter.BajaAdapterFactory
import com.upd.kvupd.ui.adapter.BajaSuperAdapter
import com.upd.kvupd.ui.adapter.BajaSuperAdapterFactory
import com.upd.kvupd.ui.fragment.enumClass.VistaBaja
import com.upd.kvupd.ui.sealed.AppDialogType
import com.upd.kvupd.ui.sealed.ResultadoApi
import com.upd.kvupd.ui.fragment.enumClass.TipoUsuario
import com.upd.kvupd.utils.GpsTracker
import com.upd.kvupd.utils.InstanciaDialog
import com.upd.kvupd.utils.MaterialDialogTexto.T_ERROR
import com.upd.kvupd.utils.MaterialDialogTexto.T_SUCCESS
import com.upd.kvupd.utils.MaterialDialogTexto.T_WARNING
import com.upd.kvupd.utils.buildMaterialDialog
import com.upd.kvupd.utils.collectFlow
import com.upd.kvupd.utils.consume
import com.upd.kvupd.utils.setUI
import com.upd.kvupd.utils.viewBinding
import com.upd.kvupd.viewmodel.ALLViewModel
import com.upd.kvupd.viewmodel.APIViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class FBaja : Fragment(), MenuProvider, OnQueryTextListener,
    BajaAdapter.Listener, BajaSuperAdapter.Listener {

    private val apiViewModel by activityViewModels<APIViewModel>()
    private val localViewmodel by activityViewModels<ALLViewModel>()
    private val binding by viewBinding(FragmentFBajaBinding::bind)

    private lateinit var bajaAdapter: BajaAdapter
    private lateinit var bajasuperAdapter: BajaSuperAdapter
    private var vistaActual: VistaBaja = VistaBaja.GENERADO
    private var bajaCache: List<TableBaja> = emptyList()
    private var bajaSuperCache: List<FlowBajaSupervisor> = emptyList()
    private var getLocation: Location? = null
    private val _tag by lazy { FBaja::class.java.simpleName }

    @Inject
    lateinit var adapterBFactory: BajaAdapterFactory

    @Inject
    lateinit var adapterBSFactory: BajaSuperAdapterFactory

    @Inject
    lateinit var gpsTracker: GpsTracker

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentFBajaBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        bajaAdapter = adapterBFactory.create(listener = this)
        bajasuperAdapter = adapterBSFactory.create(listener = this)
        binding.rcvBaja.layoutManager = LinearLayoutManager(requireContext())
        binding.searchview.setOnQueryTextListener(this)

        collectFlows()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.n_baja_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.lista -> consume { toggleVista() }
        R.id.descargar -> consume { launchApiDownload() }
        else -> false
    }

    override fun onLongClick(baja: TableBaja) {
        mostrarDialog(
            AppDialogType.Informativo(
                titulo = T_WARNING,
                mensaje = "Desea anular la baja del cliente ${baja.nombre}?",
                onPositive = {
                    val item = baja.copy(anulado = 1)
                    apiViewModel.retrySendBaja(item)
                }
            )
        )
    }

    override fun onLongClick(bajaSupervisor: FlowBajaSupervisor) {
        TODO("Not yet implemented")
    }

    override fun onQueryTextSubmit(p0: String) = false
    override fun onQueryTextChange(p0: String): Boolean {
        localViewmodel.setQuery(p0)
        return true
    }

    override fun onStop() {
        localViewmodel.clearQuery()
        super.onStop()
    }

    private fun collectFlows() {
        val flowBajas = apiViewModel.flowBajasFiltrados(localViewmodel.query)
        val flowBajaSupervisor = apiViewModel.flowBajasSupervisorFiltrados(localViewmodel.query)

        collectFlow(flowBajas) { bajas ->
            bajaCache = bajas
            renderVistaActual()
        }

        collectFlow(flowBajaSupervisor) { bajasSupervisor ->
            bajaSuperCache = bajasSupervisor
            renderVistaActual()
        }

        collectFlow(apiViewModel.bajasuperEvent) { resultado ->
            handleResultadoApi(
                resultado
            ) { data ->
                stateSuccessBajaSuper(data)
            }
        }

        collectFlow(apiViewModel.bajaestadoEvent) { resultado ->
            handleResultadoApi(
                resultado
            ) { data ->
                stateSuccessBajaEstado(data)
            }
        }
    }

    private fun toggleVista() {
        val tipo = localViewmodel.tipoUsuario.value ?: return
        when (tipo) {
            TipoUsuario.SUPERVISOR -> {
                val siguiente =
                    if (vistaActual == VistaBaja.GENERADO)
                        VistaBaja.PROCESAR
                    else
                        VistaBaja.GENERADO

                cambiarVista(siguiente)
            }

            TipoUsuario.VENDEDOR,
            TipoUsuario.JEFE_VENTAS -> {
                if (vistaActual != VistaBaja.GENERADO) {
                    cambiarVista(VistaBaja.GENERADO)
                }
            }
        }
    }

    private fun cambiarVista(nuevaVista: VistaBaja) {
        if (vistaActual == nuevaVista) return
        vistaActual = nuevaVista

        binding.searchview.setQuery("", false)
        localViewmodel.clearQuery()

        renderVistaActual()
    }

    private fun renderVistaActual() {
        when (vistaActual) {
            VistaBaja.GENERADO -> renderGeneradas()
            VistaBaja.PROCESAR -> renderSupervisor()
        }
    }

    private fun launchApiDownload() {
        val tipo = localViewmodel.tipoUsuario.value ?: return
        when (tipo) {
            TipoUsuario.VENDEDOR -> apiViewModel.downloadAndShowBajas()
            TipoUsuario.SUPERVISOR -> apiViewModel.downloadBajasSupervisor()
            TipoUsuario.JEFE_VENTAS -> Unit
        }
    }

    private fun renderGeneradas() {
        val hayDatos = bajaCache.isNotEmpty()
        toggleEmpty(hayDatos)

        binding.rcvBaja.adapter = bajaAdapter
        bajaAdapter.submitList(bajaCache)
    }

    private fun renderSupervisor() {
        val hayDatos = bajaSuperCache.isNotEmpty()
        toggleEmpty(hayDatos)

        binding.rcvBaja.adapter = bajasuperAdapter
        bajasuperAdapter.submitList(bajaSuperCache)
    }

    private fun toggleEmpty(hayDatos: Boolean) {
        binding.rcvBaja.setUI("v", hayDatos)
        binding.emptyContainer.root.setUI("v", !hayDatos)
    }

    private fun stateSuccessBajaSuper(bajaSupervisores: JsonBajaSupervisor?) {
        when {
            bajaSupervisores == null -> mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_ERROR,
                    mensaje = "No se obtuvo respuesta del servidor"
                )
            )

            bajaSupervisores.jobl.isEmpty() -> mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_ERROR,
                    mensaje = "No se encontraron bajas pendientes"
                )
            )

            else -> mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_SUCCESS,
                    mensaje = "Se descargaron ${bajaSupervisores.jobl.size} bajas correctamente"
                )
            )
        }
    }

    private fun stateSuccessBajaEstado(bajaVendedores: JsonBajaVendedor?) {
        when {
            bajaVendedores == null -> mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_ERROR,
                    mensaje = "No se obtuvo respuesta del servidor"
                )
            )

            bajaVendedores.jobl.isEmpty() -> mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_ERROR,
                    mensaje = "Todas las bajas revisadas"
                )
            )

            else -> {
                mostrarDialog(
                    AppDialogType.Informativo(
                        titulo = T_SUCCESS,
                        mensaje = "Se descargaron ${bajaVendedores.jobl.size} bajas correctamente"
                    )
                )

                renderBajasPendientes(bajaVendedores.jobl)
            }
        }
    }

    private fun renderBajasPendientes(lista: List<BajaVendedor>) {
        //
    }

    private fun <T> handleResultadoApi(
        resultado: ResultadoApi<T>,
        onSuccess: (T?) -> Unit
    ) {
        when (resultado) {
            is ResultadoApi.Loading -> mostrarDialog(
                AppDialogType.Progreso(
                    mensaje = "Obteniendo lista de bajas"
                )
            )

            is ResultadoApi.Exito -> onSuccess(resultado.data)

            is ResultadoApi.ErrorHttp -> mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_ERROR,
                    mensaje = "Error HTTP ${resultado.code}: ${resultado.mensaje}"
                )
            )

            is ResultadoApi.Fallo -> mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_ERROR,
                    mensaje = "Fallo: ${resultado.mensaje}"
                )
            )
        }
    }

    private fun mostrarDialog(dialogType: AppDialogType) {
        lifecycleScope.launch(Dispatchers.Main) {
            InstanciaDialog.cerrarDialogActual()
            val dialog = buildMaterialDialog(requireContext(), dialogType)
            dialog.show()
            InstanciaDialog.REFERENCIA_DIALOG = WeakReference(dialog)
        }
    }
}