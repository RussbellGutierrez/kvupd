package com.upd.kvupd.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
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
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.upd.kvupd.R
import com.upd.kvupd.ui.fragment.servidor.enumFile.DrawablePosition
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
import java.util.UUID
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@Suppress("DEPRECATION")
inline fun <reified T : Parcelable> Bundle.getParcelableCompat(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, T::class.java)
    } else {
        getParcelable(key)
    }
}

inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}

inline fun <reified T> Spinner.selectedItemTyped(): T? {
    return selectedItem as? T
}

@Suppress("UNCHECKED_CAST")
fun <T> Spinner.onItemSelectedItem(block: (item: T) -> Unit) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>,
            view: View?,
            position: Int,
            id: Long
        ) {
            block(parent.getItemAtPosition(position) as T)
        }

        override fun onNothingSelected(parent: AdapterView<*>) = Unit
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> Spinner.selectItem(predicate: (T) -> Boolean) {
    val adapter = adapter ?: return

    for (i in 0 until adapter.count) {
        val item = adapter.getItem(i) as? T ?: continue

        if (predicate(item)) {
            setSelection(i)
            return
        }
    }
}

fun <T> Spinner.setAdapterList(items: List<T>) {
    adapter = ArrayAdapter(
        context,
        android.R.layout.simple_spinner_dropdown_item,
        items
    )
}

fun Fragment.snack(text: String) {
    val view = this.requireActivity().window.decorView.findViewById<View>(android.R.id.content)
    Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show()
}

fun Activity.snack(text: String) {
    val view = this.window.decorView.findViewById<View>(android.R.id.content)
    Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show()
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

fun <T : ViewBinding> Fragment.viewBinding(bind: (View) -> T): ReadOnlyProperty<Fragment, T> =
    object : ReadOnlyProperty<Fragment, T> {

        private var binding: T? = null

        override fun getValue(thisRef: Fragment, property: KProperty<*>): T {

            binding?.let { return it }

            val view = thisRef.view
                ?: throw IllegalStateException("View no inicializada o ya destruida")

            return bind(view).also { newBinding ->
                binding = newBinding
                thisRef.viewLifecycleOwnerLiveData.observe(thisRef) { owner ->
                    owner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                        override fun onDestroy(owner: LifecycleOwner) {
                            binding = null
                        }
                    })
                }
            }
        }
    }

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.visibleIf(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.GONE
}

fun TextView.setDrawableTint(position: DrawablePosition, color: Int) {
    compoundDrawablesRelative[position.index]?.setTint(color)
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

fun BottomSheetDialogFragment.expandFullHeight(
    skipCollapsed: Boolean = true,
    fullHeight: Boolean = false
) {
    val dialog = dialog as? BottomSheetDialog ?: return
    val bottomSheet = dialog.findViewById<View>(
        com.google.android.material.R.id.design_bottom_sheet
    ) ?: return

    val behavior = BottomSheetBehavior.from(bottomSheet)

    behavior.state = BottomSheetBehavior.STATE_EXPANDED
    behavior.skipCollapsed = skipCollapsed

    if (fullHeight) {
        bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
    }
}

fun DialogFragment.observeWorkersById(
    workManager: WorkManager,
    lifecycleOwner: LifecycleOwner,
    ids: List<UUID>,
    onUpdate: (id: UUID, workInfo: WorkInfo, progreso: Int, mensaje: String) -> Unit
) {
    ids.forEach { id ->
        workManager.getWorkInfoByIdLiveData(id)
            .observe(lifecycleOwner) { info ->
                if (info != null) {
                    val progreso = info.progress.getInt("progreso", 0)
                    val mensaje = info.progress.getString("estado") ?: ""
                    onUpdate(id, info, progreso, mensaje)
                }
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

                val positiveText =
                    if (dialogType.mostrarNegativo) TEXT_POSITIVO_B else TEXT_POSITIVO_A
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

fun Fragment.popWithResult(key: String, value: Any) {
    val nav = findNavController()
    nav.previousBackStackEntry
        ?.savedStateHandle
        ?.set(key, value)

    nav.popBackStack()
}

inline fun <reified T> Fragment.observeResult(
    key: String,
    crossinline onResult: (T) -> Unit
) {
    val nav = findNavController()
    val handle = nav.currentBackStackEntry?.savedStateHandle ?: return

    handle.getLiveData<T>(key).observe(viewLifecycleOwner) { value ->
        onResult(value)
        handle.remove<T>(key)
    }
}