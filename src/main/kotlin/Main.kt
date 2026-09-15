package com.example.actividad

import kotlinx.coroutines.runBlocking
import java.util.Scanner

fun main() = runBlocking {
    val gestor = GestorLavanderia()
    val scanner = Scanner(System.`in`)
    var continuar = true

    println("   SISTEMA DE GESTION LAVEXPRESS SpA     ")

    while (continuar) {
        println("\n--- MENU PRINCIPAL ---")
        println("1. Registrar Entrada de Maquina")
        println("2. Registrar Salida de Maquina")
        println("3. Consultas de Negocio")
        println("4. Cierre de Turno y Salir")
        print("Seleccione una opcion: ")

        when (scanner.nextLine().trim()) {
            "1" -> {
                println("\n--- REGISTRO DE ENTRADA ---")
                println("Tipo de maquina: 1) Lavadora  2) Secadora  3) Lavaseca Industrial")
                print("Opcion: ")
                val tipo = scanner.nextLine().trim()

                print("Ingrese Codigo de Maquina (Ej: AB12CD): ")
                val codigo = scanner.nextLine().trim()

                print("Ingrese Marca y Modelo: ")
                val marca = scanner.nextLine().trim()

                println("Tipo de usuario: 1) Regular  2) Suscriptor  3) Empresa")
                print("Opcion: ")
                val tipoUserInt = scanner.nextLine().trim()
                val tipoUsuario = when (tipoUserInt) {
                    "2" -> TipoUsuario.SUSCRIPTOR
                    "3" -> TipoUsuario.EMPRESA
                    else -> TipoUsuario.REGULAR
                }

                try {
                    val maquina: Maquina = when (tipo) {
                        "1" -> Lavadora(codigo, marca, tipoUsuario)
                        "2" -> Secadora(codigo, marca, tipoUsuario)
                        "3" -> {
                            print("¿Incluye ciclo con vapor? (si/no): ")
                            val conVapor = scanner.nextLine().trim().lowercase() == "si"
                            LavasecaIndustrial(codigo, marca, tipoUsuario, conVapor)
                        }
                        else -> {
                            println("Opción de máquina no válida.")
                            continue
                        }
                    }
                    gestor.registrarEntrada(maquina)
                } catch (e: LavExpressException) {
                    println("\n[ERROR CAPTURADO]: ${e.message}")
                }
            }

            "2" -> {
                println("\n--- REGISTRO DE SALIDA ---")
                print("Ingrese Codigo de Maquina a retirar: ")
                val codigo = scanner.nextLine().trim()

                print("Ingrese tiempo de uso en minutos: ")
                val minutos = scanner.nextLine().trim().toIntOrNull() ?: 0

                try {
                    gestor.registrarSalida(codigo, minutos)
                } catch (e: LavExpressException) {
                    println("\n[ERROR CAPTURADO]: ${e.message}")
                }
            }

            "3" -> {
                println("\n--- CONSULTAS DE NEGOCIO ---")
                println("* Slots disponibles: ${gestor.slotsDisponibles()}")
                println("* Ingreso promedio: $${String.format("%.0f", gestor.ingresoPromedio())}")
                println("* Maquinas finalizadas: ${gestor.maquinasFinalizadas()}")
                val mayor = gestor.maquinaMayorTiempo()
                println("* Mayor tiempo de uso: ${mayor?.codigoMaquina ?: "N/A"} (${mayor?.minutosUso ?: 0} min)")
            }

            "4" -> {
                println("\nGenerando informe...")
                gestor.imprimirReporteCierre()
                continuar = false
            }

            else -> println("Opcion invalida. Intente nuevamente.")
        }
    }
}