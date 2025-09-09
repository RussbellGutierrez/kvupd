package com.upd.kvupd.ui.sealed

sealed class ModificarParaFragments {
    data class uiToast(val message: String) : ModificarParaFragments()
    data class uiDialog(val dialogType: AppDialogType) : ModificarParaFragments()
    data class uiNavigate(val destination: String) : ModificarParaFragments()
}