package com.upd.kvupd.ui.fragment.encuesta.enumFile

enum class EstadoEncuesta {
    INIT,           // aún no hay datos reales
    LOADING,
    RENDER,         // mostrando formulario
    SIN_DATOS,      // no hay encuestas
    SELECCION       // elegir encuesta
}