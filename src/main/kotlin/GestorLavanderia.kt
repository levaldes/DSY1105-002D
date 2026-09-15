package com.example.actividad

import kotlinx.coroutines.delay

/**
 * Gestor encargado de la administración de los 10 slots,
 * simulación de retardos asíncronos y generación de informes.
 */
class GestorLavanderia {
    val slots = List(10) { index -> Slot(numeroSlot = index + 1) }
    val historialTickets = mutableListOf<Ticket>()
    private var contadorTickets = 1

    /**
     * Registra el ingreso de una máquina a un slot disponible.
     */
    suspend fun registrarEntrada(maquina: Maquina) {
        val slotDisponible = slots.firstOrNull { it.estado == EstadoSlot.LIBRE }
            ?: throw SistemaSinCapacidadException("Error: Sistema sin capacidad. No hay slots libres.")

        // Estado EN_CICLO_FINAL mientras simula el retardo asíncrono
        slotDisponible.estado = EstadoSlot.EN_CICLO_FINAL
        slotDisponible.motivoEstado = "Registrando entrada ante sensor"
        println("[SENSOR] Esperando confirmacion de entrada para maquina ${maquina.codigoMaquina} en Slot ${slotDisponible.numeroSlot}...")

        delay(3000) // Retardo asíncrono de 3 segundos (R3)

        slotDisponible.maquina = maquina
        slotDisponible.estado = EstadoSlot.EN_USO
        slotDisponible.motivoEstado = ""
        println("-> EXITO: Maquina ${maquina.codigoMaquina} asignada al Slot ${slotDisponible.numeroSlot}.")
    }

    /**
     * Registra la salida de una máquina y calcula la tarifa.
     */
    suspend fun registrarSalida(codigoMaquina: String, minutosUso: Int) {
        val slot = slots.firstOrNull { it.maquina?.codigoMaquina == codigoMaquina && it.estado == EstadoSlot.EN_USO }
            ?: throw MaquinaNoEncontradaException("Error: La maquina con codigo $codigoMaquina no esta en el sistema o en uso.")

        val maquina = slot.maquina!!

        // Estado EN_CICLO_FINAL mientras calcula tarifa
        slot.estado = EstadoSlot.EN_CICLO_FINAL
        slot.motivoEstado = "Calculando tarifa con sensor"
        println("[SENSOR] Procesando salida y calculo de tarifa para maquina $codigoMaquina en Slot ${slot.numeroSlot}...")

        delay(6500) // Retardo asíncrono de 6.5 segundos (R3)

        val montoFinal = maquina.calcularMontoFinal(minutosUso)
        val ticket = Ticket(
            numeroTicket = contadorTickets++,
            codigoMaquina = maquina.codigoMaquina,
            tipoMaquina = maquina::class.simpleName ?: "Desconocido",
            minutosUso = minutosUso,
            montoPagado = montoFinal
        )

        historialTickets.add(ticket)
        slot.maquina = null
        slot.estado = EstadoSlot.LIBRE
        slot.motivoEstado = ""

        println("-> EXITO: Salida procesada para $codigoMaquina. Ticket #${ticket.numeroTicket} emitido por $${String.format("%.0f", montoFinal)}.")
    }

    // --- Consultas de Negocio (R5) ---

    fun slotsDisponibles(): Int = slots.count { it.estado == EstadoSlot.LIBRE }

    fun ingresoPromedio(): Double = if (historialTickets.isEmpty()) 0.0 else historialTickets.sumOf { it.montoPagado } / historialTickets.size

    fun maquinasFinalizadas(): List<String> = historialTickets.map { it.codigoMaquina }

    fun maquinaMayorTiempo(): Ticket? = historialTickets.maxByOrNull { it.minutosUso }

    fun tipoMaquinaMasIngresos(): String {
        return historialTickets.groupBy { it.tipoMaquina }
            .maxByOrNull { entry -> entry.value.sumOf { it.montoPagado } }?.key ?: "N/A"
    }

    /**
     * Imprime el informe al cierre de turno (R4).
     */
    fun imprimirReporteCierre() {
        println("            REPORTE DE CIERRE DE TURNO - LAVEXPRESS     ")
        println("TICKET | TIPO                 | CODIGO | TIEMPO  | MONTO")
        historialTickets.forEach { t ->
            println("#${t.numeroTicket.toString().padEnd(5)} | ${t.tipoMaquina.padEnd(20)} | ${t.codigoMaquina} | ${t.minutosUso} min  | $${String.format("%.0f", t.montoPagado)}")
        }
        println("Total Recaudado:          $${String.format("%.0f", historialTickets.sumOf { it.montoPagado })}")
        println("Cantidad de Atenciones:   ${historialTickets.size}")
        println("Ingreso Promedio:         $${String.format("%.0f", ingresoPromedio())}")
        println("Tipo con Mayor Ingreso:   ${tipoMaquinaMasIngresos()}")
        println("Slots Libres al Cierre:   ${slotsDisponibles()}")
    }
}