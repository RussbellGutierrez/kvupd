package com.upd.kvupd.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import com.upd.kvupd.databinding.OldFragmentFAjusteBinding
import com.upd.kvupd.service.ServiceSetup
import com.upd.kvupd.utils.OldConstant.IP_AUX
import com.upd.kvupd.utils.OldConstant.IP_FILTER
import com.upd.kvupd.utils.OldConstant.OPTURL
import com.upd.kvupd.utils.OldHostSelectionInterceptor
import com.upd.kvupd.utils.OldRegexMaskTextWatcher
import com.upd.kvupd.utils.consume
import com.upd.kvupd.utils.isServiceRunning
import com.upd.kvupd.utils.progress
import com.upd.kvupd.utils.setUI
import com.upd.kvupd.utils.showDialog
import com.upd.kvupd.utils.snack
import com.upd.kvupd.utils.toReqBody
import com.upd.kvupd.utils.toast
import com.upd.kvupd.utils.validateIP
import com.upd.kvupd.utils.validateImei
import com.upd.kvupd.viewmodel.OldAppViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class OldFAjuste : Fragment(), MenuProvider {

    @Inject
    lateinit var host: OldHostSelectionInterceptor

    private val viewmodel by activityViewModels<OldAppViewModel>()
    private var _bind: OldFragmentFAjusteBinding? = null
    private val bind get() = _bind!!
    private var empresa = 0
    private var reinsert = false
    private val _tag by lazy { OldFAjuste::class.java.simpleName }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = OldFragmentFAjusteBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        cleaningData()

        bind.edtIp.addTextChangedListener(OldRegexMaskTextWatcher(IP_FILTER))

        bind.edtIp.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                bind.chkImei.isChecked = false
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })

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
                showDialog(
                    "advertencia",
                    "¿Está seguro de querer cambiar la empresa del celular?"
                ) {
                    sendParams()
                }
            } else {
                sendParams()
            }
        }

        viewmodel.sessionObserver().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                reinsert = true
                showDialog(
                    "espere",
                    "El celular ya se encuentra registrado, si desea cambiar la empresa para el celular, primero debe eliminar el registro en la página de PEDIMAP"
                ) {}
                bind.txtEmpresa.setUI("v", true)
                setParamButton(result.empresa)
                getIMEIandIP()
            }
        }

        /*viewmodel.register.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is OldNetworkRetrofit.Success -> {
                        y.data?.jobl?.forEach { i ->
                            if (i.has("message")) {
                                showDialog("Advertencia", i.getString("message")) {}
                            } else {
                                showDialog("Correcto", "Usuario registrado en el servidor") {}
                            }
                        }
                    }
                    is OldNetworkRetrofit.Error -> {
                        showDialog("Error", y.message!!) {}
                    }
                }
            }
        }*/

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

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) = Unit

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        android.R.id.home -> consume {
            showDialog(
                "Advertencia",
                "Cerraremos kventas para aplicar los cambios"
            ) { requireActivity().finishAndRemoveTask() }
        }
        else -> false
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
        val ip = bind.edtIp.text.toString().trim()
        val modelo = "${Build.MANUFACTURER} ${Build.MODEL}"

        if (imei != "" && empresa != 0 && bind.chkImei.isChecked) {

            OPTURL = "aux"
            IP_AUX = "http://$ip/api/"
            host.setHostBaseUrl()

            val p = JSONObject()
            p.put("imei", "$imei-V")
            p.put("modelo", modelo.uppercase())
            p.put("version", viewmodel.appSo())
            p.put("empresa", empresa)
            Log.d(_tag, "Config-> $p")
            progress("Registrando usuario en servidor")
            viewmodel.fetchRegisterDevice(p.toReqBody())
        } else {
            snack("Seleccione una empresa, ingrese una ip y complete el IMEI, no olvide marcar la casilla de IMEI")
        }
    }

    private fun setParamButton(o: Int) {
        when (o) {
            1 -> {
                bind.btnOriunda.setTextColor(Color.WHITE)
                bind.btnOriunda.setBackgroundColor(Color.parseColor("#1E90FF"))
                bind.btnTerranorte.setTextColor(Color.parseColor("#DF3E5F"))
                bind.btnTerranorte.setBackgroundColor(Color.WHITE)
                bind.txtEmpresa.text =
                    "Recuerde, para cambiar el registro a TERRANORTE primero debe eliminar el registro en la página PEDIMAP"
            }
            2 -> {
                bind.btnTerranorte.setTextColor(Color.WHITE)
                bind.btnTerranorte.setBackgroundColor(Color.parseColor("#DF3E5F"))
                bind.btnOriunda.setTextColor(Color.parseColor("#1E90FF"))
                bind.btnOriunda.setBackgroundColor(Color.WHITE)
                bind.txtEmpresa.text =
                    "Recuerde, para cambiar el registro a ORIUNDA primero debe eliminar el registro en la página PEDIMAP"
            }
        }
    }

    private fun setParamImei(check: Boolean) {
        if (check) {
            if (bind.edtImei.text.validateImei()) {
                if (bind.edtIp.text.validateIP()) {
                    val qr = "${bind.edtIp.text}-${bind.edtImei.text}"
                    val bm = viewmodel.generateAndSaveQR(qr)
                    bind.imgQr.setImageBitmap(bm)
                    bind.edtImei.setUI("e", false)
                } else {
                    toast("Ingrese una IP valida")
                    bind.chkImei.isChecked = false
                }
            } else {
                toast("IMEI debe contener 15 digitos")
                bind.chkImei.isChecked = false
            }
        } else {
            bind.edtImei.setUI("e", true)
        }
    }

    private fun getIMEIandIP() {
        val ip = viewmodel.getIP()
        val imei = viewmodel.getIMEI()
        val qr = "$ip-$imei"
        bind.imgQr.setImageBitmap(viewmodel.getQR(qr))
        bind.edtIp.setText(ip)
        bind.edtImei.setText(imei)
        bind.chkImei.isChecked = true
    }
}