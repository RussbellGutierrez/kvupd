package com.upd.kvupd.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.DialogFragment.STYLE_NO_FRAME
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.upd.kvupd.R
import com.upd.kvupd.ui.sealed.AppDialogType
import com.upd.kvupd.utils.DimensionesDialog.DIALOG_ALTO
import com.upd.kvupd.utils.DimensionesDialog.DIALOG_ANCHO
import com.upd.kvupd.utils.DimensionesDialog.DIALOG_ANCHOTODO
import com.upd.kvupd.utils.MaterialDialogTexto.TEXT_CANCELAR
import com.upd.kvupd.utils.MaterialDialogTexto.TEXT_POSITIVO_A
import com.upd.kvupd.utils.MaterialDialogTexto.TEXT_POSITIVO_B
import com.upd.kvupd.utils.MaterialDialogTexto.T_ERROR
import com.upd.kvupd.utils.MaterialDialogTexto.T_SUCCESS
import com.upd.kvupd.utils.MaterialDialogTexto.T_WARNING
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}

fun <T> Fragment.collectFlow(
    flow: Flow<T>,
    collector: suspend (T) -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect { collector(it) }
        }
    }
}

fun <T> ComponentActivity.collectFlow(
    flow: Flow<T>,
    collector: suspend (T) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect { collector(it) }
        }
    }
}

// Función de extensión para Fragment que devuelve un delegado de sólo lectura
// Te permite usar: val binding by viewBinding(Binding::bind)
fun <T : ViewBinding> Fragment.viewBinding(bind: (View) -> T): ReadOnlyProperty<Fragment, T> =
    object : ReadOnlyProperty<Fragment, T>, DefaultLifecycleObserver {

        // Referencia interna al binding (nullable para poder limpiarlo en onDestroyView)
        private var binding: T? = null

        // Se llama cuando el LifecycleOwner (el Fragment) se crea
        // Registramos este delegado como observador del ciclo de vida
        override fun onCreate(owner: LifecycleOwner) {
            owner.lifecycle.addObserver(this)
        }

        // Se llama automáticamente cuando el Fragment entra en onDestroyView()
        // Aquí limpiamos la referencia al binding para evitar memory leaks
        override fun onDestroy(owner: LifecycleOwner) {
            binding = null
        }

        // Devuelve el binding cuando se accede a la propiedad (ej: binding.txtTitulo.text = "...")
        override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
            // Si la vista del fragment aún no está creada, lanzamos error
            val view = thisRef.view ?: error("View no inicializada")
            // Si binding ya está creado lo devolvemos, si no, lo creamos con bind(view)
            return binding ?: bind(view).also { binding = it }
        }
    }

fun View.setUI(ui: String, toggle: Boolean) {
    when (ui) {
        "v" -> visibility = if (toggle) View.VISIBLE else View.GONE
        "e" -> isEnabled = toggle
        "c" -> isClickable = toggle
        "s" -> isSelected = toggle
    }
}

fun Fragment.toast(text: String, duration: Int = 0) {
    Toast.makeText(this.requireContext(), text, duration).show()
}

fun Context.toast(text: String, duration: Int = 0) {
    Toast.makeText(this, text, duration).show()
}

fun DialogFragment.setResume(isCompact: Boolean = true) {
    dialog?.window?.let { window ->
        val width = if (isCompact) DIALOG_ANCHO else DIALOG_ANCHOTODO
        window.setLayout(width, DIALOG_ALTO)

        if (isCompact) {
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setStyle(STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar)
        }
    }
}

fun buildMaterialDialog(context: Context, dialogType: AppDialogType): MaterialDialog {
    return MaterialDialog(context).apply {
        cancelable(false)
        cancelOnTouchOutside(false)

        when (dialogType) {
            is AppDialogType.Informativo -> {
                val iconRes = when (dialogType.titulo) {
                    T_WARNING -> R.drawable.advertencia
                    T_SUCCESS -> R.drawable.correcto
                    T_ERROR -> R.drawable.error
                    else -> R.drawable.informacion
                }

                icon(iconRes)
                title(null, dialogType.titulo.uppercase())
                message(null, dialogType.mensaje)

                val positiveText = if (dialogType.mostrarNegativo) TEXT_POSITIVO_B else TEXT_POSITIVO_A
                positiveButton(null, positiveText) {
                    dismiss()
                    dialogType.onPositive()
                }

                if (dialogType.mostrarNegativo) {
                    negativeButton(null, TEXT_CANCELAR) {
                        dismiss()
                        dialogType.onNegative?.invoke()
                    }
                }
            }

            is AppDialogType.Progreso -> {
                customView(R.layout.custom_dialog_loading, scrollable = false)

                // Inyectar mensaje dinámico
                val view = getCustomView()
                val txtMensaje = view.findViewById<TextView>(R.id.txt_dialog)
                txtMensaje.text = dialogType.mensaje
            }
        }
    }
}