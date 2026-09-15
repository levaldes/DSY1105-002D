package com.example.actividad

/**
 * Jerarquía de excepciones personalizadas del sistema LavExpress.
 */
open class LavExpressException(mensaje: String) : Exception(mensaje)

// Excepción lanzada cuando el formato del código de la máquina es inválido (R1)
class CodigoInvalidoException(mensaje: String) : LavExpressException(mensaje)

// Excepción lanzada cuando los 10 slots están ocupados (R3)
class SistemaSinCapacidadException(mensaje: String) : LavExpressException(mensaje)

// Excepción lanzada cuando la máquina no existe o no está en uso (R3)
class MaquinaNoEncontradaException(mensaje: String) : LavExpressException(mensaje)