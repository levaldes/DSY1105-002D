package com.example.actividad

/**
 * Clase base abstracta para representan máquinas de lavandería.
 */
abstract class Maquina(
    val codigoMaquina: String,
    val marcaModelo: String,
    val tipoUsuario: TipoUsuario
) {
    init {
        // Validación R1: El código debe tener 2 letras, 2 números y 2 letras (Ej: AB12CD)
        val regex = Regex("^[A-Za-z]{2}\\d{2}[A-Za-z]{2}\$")
        if (!regex.matches(codigoMaquina)) {
            throw CodigoInvalidoException("El código '$codigoMaquina' no cumple con el formato requerido (2 letras, 2 números, 2 letras).")
        }
    }

    abstract val tarifaPorMinuto: Double

    /**
     * Calcula el monto total aplicando IVA (19%) y descuento de EMPRESA (50%).
     */
    fun calcularMontoFinal(minutosUso: Int): Double {
        val montoBase = tarifaPorMinuto * minutosUso
        var montoConIva = montoBase * 1.19 // Aplicación de IVA 19%

        // Si el usuario pertenece a una Empresa, obtiene 50% de descuento sobre el total con IVA
        if (tipoUsuario == TipoUsuario.EMPRESA) {
            montoConIva *= 0.5
        }
        return montoConIva
    }
}

// Subclase Lavadora: tarifa $1.200/min
class Lavadora(
    codigoMaquina: String,
    marcaModelo: String,
    tipoUsuario: TipoUsuario
) : Maquina(codigoMaquina, marcaModelo, tipoUsuario) {
    override val tarifaPorMinuto: Double = 1200.0
}

// Subclase Secadora: tarifa $1.000/min
class Secadora(
    codigoMaquina: String,
    marcaModelo: String,
    tipoUsuario: TipoUsuario
) : Maquina(codigoMaquina, marcaModelo, tipoUsuario) {
    override val tarifaPorMinuto: Double = 1000.0
}

// Subclase LavasecaIndustrial: tarifa $1.800/min + $3.000 si incluye vapor
class LavasecaIndustrial(
    codigoMaquina: String,
    marcaModelo: String,
    tipoUsuario: TipoUsuario,
    val conVapor: Boolean
) : Maquina(codigoMaquina, marcaModelo, tipoUsuario) {
    override val tarifaPorMinuto: Double = 1800.0 + (if (conVapor) 3000.0 else 0.0)
}

/**
 * Modelo para representar un Slot físico de la lavandería.
 */
data class Slot(
    val numeroSlot: Int,
    var estado: EstadoSlot = EstadoSlot.LIBRE,
    var maquina: Maquina? = null,
    var motivoEstado: String = ""
)

/**
 * Modelo para el ticket emitido al retirar una máquina.
 */
data class Ticket(
    val numeroTicket: Int,
    val codigoMaquina: String,
    val tipoMaquina: String,
    val minutosUso: Int,
    val montoPagado: Double
)