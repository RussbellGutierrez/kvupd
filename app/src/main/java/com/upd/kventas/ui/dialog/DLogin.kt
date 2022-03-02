package com.upd.kventas.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.upd.kventas.R
import com.upd.kventas.databinding.DialogLoginBinding
import com.upd.kventas.utils.*
import com.upd.kventas.utils.Constant.CONF
import com.upd.kventas.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject

@AndroidEntryPoint
class DLogin : BottomSheetDialogFragment() {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: DialogLoginBinding? = null
    private val bind get() = _bind!!
    private var configEmpty = false
    private val _tag by lazy { DLogin::class.java.simpleName }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = DialogLoginBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind.btnLogin.setOnClickListener {
            login()
        }

        viewmodel.login.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is Network.Success -> {
                        controlUI(false)
                        y.data?.let {
                            when (it.data.tipo.descripcion) {
                                "ADMINISTRADOR" -> findNavController().navigate(R.id.action_DLogin_to_FAjuste)
                                else -> {
                                    controlUI(false)
                                    snack("Administradores solamente")
                                }
                            }
                        }
                    }
                    is Network.Error -> {
                        controlUI(false)
                        bind.txtMensaje.setUI("v", true)
                        bind.txtMensaje.text = y.message
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            configEmpty = viewmodel.isConfigEmpty()
        }
    }

    private fun editEmpty(): Boolean {
        val user = bind.edtUser.text.toString().trim()
        val pass = bind.edtPass.text.toString().trim()
        return (user == "" || pass == "")
    }

    private fun login() {
        if (editEmpty()) {
            toast("Complete los campos")
        }else {
            if (!configEmpty) {
                val p = JSONObject()
                p.put("usuario",bind.edtUser.text.toString())
                p.put("clave",bind.edtPass.text.toString())
                p.put("empresa",CONF.empresa)
                controlUI(true)
                viewmodel.fetchLoginAdmin(p.toReqBody())
            }
        }
    }

    private fun controlUI(launch: Boolean) {
        bind.edtUser.setUI("e", !launch)
        bind.edtPass.setUI("e", !launch)
        bind.pgrbCargando.setUI("v", launch)
        bind.btnLogin.setUI("e", !launch)
    }
}