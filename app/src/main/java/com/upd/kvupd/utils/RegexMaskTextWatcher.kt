package com.upd.kvupd.utils

import android.text.Editable
import android.text.TextWatcher
import java.util.regex.Pattern

class RegexMaskTextWatcher(regexForInputToMatch: String) : TextWatcher {

    private val regex = Pattern.compile(regexForInputToMatch)
    private var previousText: String = ""

    override fun afterTextChanged(s: Editable) {
        if (regex.matcher(s).matches()) {
            previousText = s.toString()
        } else {
            s.replace(0, s.length, previousText)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

}