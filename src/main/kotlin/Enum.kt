package com.example.actividad

/**
 * Enumeraciones para los estados del slot y tipos de usuario.
 */
enum class EstadoSlot {
    LIBRE,
    EN_USO,
    EN_CICLO_FINAL
}

enum class TipoUsuario {
    REGULAR,
    SUSCRIPTOR,
    EMPRESA
}