package com.upd.kvupd.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.upd.kvupd.BuildConfig
import com.upd.kvupd.databinding.FragmentFAjusteBinding
import com.upd.kvupd.service.ServiceSetup
import com.upd.kvupd.utils.*
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class FAjuste : Fragment() {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: FragmentFAjusteBinding? = null
    private val bind get() = _bind!!
    private var empresa = 0
    private var reinsert = false
    private val _tag by lazy { FAjuste::class.java.simpleName }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentFAjusteBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cleaningData()

        bind.btnOriunda.setOnClickListener {
            empresa = 1
            setParamButton(1)
        }

        bind.btnTerranorte.setOnClickListener {
            empresa = 2
            setParamButton(2)
        }

        bind.chkImei.setOnCheckedChangeListener { _, isChecked ->
            setParamImei(isChecked)
        }

        bind.btnRegistrar.setOnClickListener {
            if (reinsert) {
                showDialog("advertencia","¿Está seguro de querer cambiar la empresa del celular?") {
                    sendParams()
                }
            } else {
                sendParams()
            }
        }

        viewmodel.sessionObserver().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                reinsert = true
                showDialog("espere","El celular ya se encuentra registrado, si desea cambiar la empresa para el celular, primero debe eliminar el registro en la página de PEDIMAP") {}
                bind.txtEmpresa.setUI("v",true)
                setParamButton(result.empresa)
                getImei()
            }
        }

        viewmodel.register.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is Network.Success -> {
                        y.data?.jobl?.forEach { i ->
                            if (i.has("message")) {
                                showDialog("Advertencia", i.getString("message")) {}
                            } else {
                                showDialog("Correcto", "Usuario registrado en el servidor") {}
                            }
                        }
                    }
                    is Network.Error -> {
                        showDialog("Error", y.message!!) {}
                    }
                }
            }
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showDialog(
                        "Advertencia",
                        "Cerraremos kventas para aplicar los cambios"
                    ) { requireActivity().finishAndRemoveTask() }
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> consume {
            showDialog(
                "Advertencia",
                "Cerraremos kventas para aplicar los cambios"
            ) { requireActivity().finishAndRemoveTask() }
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun cleaningData() {
        viewmodel.deleteTables()

        requireActivity().let {
            if (it.isServiceRunning(ServiceSetup::class.java))
                it.stopService(Intent(it, ServiceSetup::class.java))
        }
    }

    private fun sendParams() {
        val imei = bind.edtImei.text.toString().trim()
        val modelo = "${Build.MANUFACTURER} ${Build.MODEL}"
        if (imei != "" && empresa != 0 && bind.chkImei.isChecked) {
            val p = JSONObject()
            p.put("imei", "$imei-V")
            p.put("modelo", modelo.uppercase())
            p.put("version", viewmodel.appSo())
            p.put("empresa", empresa)
            Log.d(_tag,"Config-> $p")
            progress("Registrando usuario en servidor")
            viewmodel.fetchRegisterDevice(p.toReqBody())
        } else {
            snack("Seleccione una empresa y complete el IMEI, no olvide marcar la casilla de IMEI")
        }
    }

    private fun setParamButton(o: Int) {
        when (o) {
            1 -> {
                bind.btnOriunda.setTextColor(Color.WHITE)
                bind.btnOriunda.setBackgroundColor(Color.parseColor("#1E90FF"))
                bind.btnTerranorte.setTextColor(Color.parseColor("#DF3E5F"))
                bind.btnTerranorte.setBackgroundColor(Color.WHITE)
                bind.txtEmpresa.text = "Recuerde, para cambiar el registro a TERRANORTE primero debe eliminar el registro en la página PEDIMAP"
            }
            2 -> {
                bind.btnTerranorte.setTextColor(Color.WHITE)
                bind.btnTerranorte.setBackgroundColor(Color.parseColor("#DF3E5F"))
                bind.btnOriunda.setTextColor(Color.parseColor("#1E90FF"))
                bind.btnOriunda.setBackgroundColor(Color.WHITE)
                bind.txtEmpresa.text = "Recuerde, para cambiar el registro a ORIUNDA primero debe eliminar el registro en la página PEDIMAP"
            }
        }
    }

    private fun setParamImei(check: Boolean) {
        if (check) {
            if (bind.edtImei.text.validateImei()) {
                val bm = viewmodel.generateAndSaveQR(bind.edtImei.text.toString())
                bind.imgQr.setImageBitmap(bm)
                bind.edtImei.setUI("e", false)
            } else {
                toast("IMEI debe contener 15 digitos")
                bind.chkImei.isChecked = false
            }
        } else {
            bind.edtImei.setUI("e", true)
        }
    }

    private fun getImei() {
        val imei = viewmodel.getIMEI()
        bind.imgQr.setImageBitmap(viewmodel.getQR(imei))
        bind.edtImei.setText(imei)
        bind.chkImei.isChecked = true
    }
}