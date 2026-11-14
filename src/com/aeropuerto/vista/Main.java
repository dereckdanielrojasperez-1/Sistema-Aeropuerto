package com.aeropuerto.vista;

import javax.swing.SwingUtilities;

// Clase principal que inicializa la aplicacion del Sistema de Gestion Aeroportuaria
public class Main {
    public static void main(String[] args) {
        // Arranque instantaneo y silencioso en JDK 21+ sin advertencias de carga nativa
        System.setProperty("flatlaf.useNativeLibrary", "false");

        // HUMANIZACION UI: Inicializacion de FlatLaf y fuentes institucionales
        com.aeropuerto.vista.tema.Theme.inicializar();

        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
            ventana.solicitarRolInicial();
        });
    }
}