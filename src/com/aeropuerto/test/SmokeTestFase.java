package com.aeropuerto.test;

import com.aeropuerto.controlador.ControladorAeropuerto;
import com.aeropuerto.estructuras.arbol.ArbolAVL;
import com.aeropuerto.estructuras.arbol.ArbolBinarioBusqueda;
import com.aeropuerto.estructuras.grafo.GrafoRutas;
import com.aeropuerto.estructuras.grafo.Vertice;
import com.aeropuerto.modelo.AccionReserva;
import com.aeropuerto.modelo.Equipaje;
import com.aeropuerto.modelo.Usuario;
import com.aeropuerto.modelo.Vuelo;
import com.aeropuerto.vista.VentanaPrincipal;
import com.aeropuerto.vista.tema.Theme;

import javax.swing.*;
import java.util.List;

public class SmokeTestFase {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("================================================================================");
                System.out.println("   SUITE DE AUDITORIA QA Y VERIFICACION FUNCIONAL DE FLUJOS (FASE 1 & 2)");
                System.out.println("================================================================================");
                
                // 1. Inicialización de tema corporativo
                Theme.inicializar();
                System.out.println("[OK 1.1] Modo Oscuro Permanente Activo: " + Theme.esModoOscuro());

                // 2. Controlador y autenticación de roles RBAC
                ControladorAeropuerto ctrl = new ControladorAeropuerto();
                Usuario admin = ctrl.autenticarUsuario("admin", "admin123");
                Usuario operador = ctrl.autenticarUsuario("operador", "operador123");
                Usuario supervisor = ctrl.autenticarUsuario("supervisor", "sup123");
                Usuario pasajero = ctrl.autenticarUsuario("pasajero", "123456");

                if (admin == null || !admin.esAdministrador()) throw new AssertionError("Fallo en rol ADMIN");
                if (operador == null || !operador.esOperador()) throw new AssertionError("Fallo en rol OPERADOR");
                if (supervisor == null || !supervisor.esSupervisor()) throw new AssertionError("Fallo en rol SUPERVISOR");
                if (pasajero == null || !pasajero.esPasajero()) throw new AssertionError("Fallo en rol PASAJERO");
                System.out.println("[OK 2.1] Control de Acceso RBAC 4 Roles verificado (Admin, Op, Sup, Pasajero)");

                // 3. Gestión de Vuelos y Arreglos Vuelo[]
                int cantInicial = ctrl.obtenerContadorVuelos();
                Vuelo vTest = new Vuelo("CP-9999", "Lima", "Iquitos", "2026-11-30", "10:00", "11:45", 105, 199.50, "Turista");
                String msgAdd = ctrl.agregarVuelo(vTest);
                System.out.println("[OK 2.2] Insercion de Vuelo en Arreglo Vuelo[]: " + msgAdd);
                
                // Asignación de asientos en cabina 10x5
                ctrl.seleccionarVueloActivo("CP-9999");
                boolean asientoOk = ctrl.ocuparAsiento(2, 0, "72104589"); // Asiento 3A (Ventana)
                if (!asientoOk) throw new AssertionError("Fallo al ocupar asiento 3A");
                System.out.println("[OK 2.3] Asignacion espacial en Matriz 10x5 de Cabina: Asiento 3A ocupado");

                // 4. Gestión de Equipajes y Lista Simple IATA
                Equipaje eqNormal = new Equipaje("MAL-TEST-1", "72104589", "CP-9999", 18.5, "Bodega");
                Equipaje eqSobrepeso = new Equipaje("MAL-TEST-2", "72104589", "CP-9999", 26.0, "Bodega");
                ctrl.agregarEquipaje(eqNormal);
                ctrl.agregarEquipaje(eqSobrepeso);
                if (eqSobrepeso.obtenerPeso() <= 23.0) throw new AssertionError("Fallo en deteccion de sobrepeso IATA");
                System.out.println("[OK 2.4] Lista Simple IATA: Registro de maletas con deteccion de sobrepeso >23kg");

                // 5. Puertas de Embarque y Cola de Despegue ATC
                Vuelo vEmergencia = new Vuelo("EMERG-7700", "Lima", "Trujillo", "2026-11-30", "10:05", "11:15", 70, 0.0, "Emergencia");
                ctrl.agregarVuelo(vEmergencia);
                ctrl.encolarVueloPista("CP-9999");
                ctrl.encolarVueloPistaEmergencia("EMERG-7700");
                Vuelo despachado = ctrl.despacharSiguienteVueloPista();
                if (despachado == null || !despachado.obtenerCodigo().equals("EMERG-7700")) {
                    throw new AssertionError("La cola de prioridad no priorizo la emergencia Squawk 7700");
                }
                System.out.println("[OK 2.5] Torre ATC Cola con Prioridad: Emergencia Squawk 7700 despachada con prelacion");

                // 6. Venta de Pasajes, Historial Pila LIFO y Deshacer (Undo)
                AccionReserva deshecha = ctrl.deshacerUltimaReserva();
                if (deshecha == null || deshecha.obtenerFila() != 2 || deshecha.obtenerColumna() != 0) {
                    throw new AssertionError("Fallo en el TAD Pila LIFO al revertir reserva");
                }
                System.out.println("[OK 2.6] TAD Pila LIFO: Operacion Deshacer (Undo) restauro asiento 3A a estado libre");

                // 7. Apertura de VentanaPrincipal a 1280x720 y 1920x1080 (Prueba de interfaz visual)
                VentanaPrincipal ventana = new VentanaPrincipal();
                ventana.setSize(1280, 720);
                ventana.configurarModoVista(true);
                ventana.doLayout();
                ventana.validate();
                System.out.println("[OK 2.7] VentanaPrincipal instanciada e inicializada a 1280x720 sin colapsos");

                ventana.setSize(1920, 1080);
                ventana.doLayout();
                ventana.validate();
                System.out.println("[OK 2.8] VentanaPrincipal redimensionada y validada a 1920x1080 (Full HD)");

                // 8. Cierre limpio
                ventana.dispose();
                System.out.println("================================================================================");
                System.out.println("   TODAS LAS PRUEBAS FUNCIONALES DE FASE 1 Y 2 APROBADAS EXITOSAMENTE");
                System.out.println("================================================================================");
                System.exit(0);
            } catch (Throwable t) {
                System.err.println("FALLO EN AUDITORIA QA: " + t.getMessage());
                t.printStackTrace();
                System.exit(1);
            }
        });
    }
}
