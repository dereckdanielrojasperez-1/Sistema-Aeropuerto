package com.aeropuerto.vista;

import com.aeropuerto.controlador.ControladorAeropuerto;
import com.aeropuerto.modelo.Avion;
import com.aeropuerto.vista.tema.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// Ficha técnica del avión Airbus A320neo
public class DialogoFichaAvion extends JDialog {
    private static final long serialVersionUID = 1L;

    public DialogoFichaAvion(JFrame ventanaPadre, ControladorAeropuerto controlador) {
        super(ventanaPadre, "Ficha Técnica y Especificaciones de Aeronave - CORPAC / Airbus A320neo", false);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets insets = null;
        try {
            insets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        } catch (Exception ignored) {}
        int maxH = (insets != null) ? screenSize.height - insets.top - insets.bottom : screenSize.height - 60;
        int maxW = (insets != null) ? screenSize.width - insets.left - insets.right : screenSize.width - 40;
        setSize(Math.min(860, maxW - 40), Math.min(620, maxH - 40));
        setLocationRelativeTo(ventanaPadre);
        setResizable(true);

        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        configurarIcono();
        inicializarComponentes(controlador.obtenerAvion());
    }

    // Configuración del icono de ventana
    private void configurarIcono() {
        try {
            java.net.URL urlLogo = getClass().getResource("/com/aeropuerto/vista/logo_corpac.png");
            if (urlLogo == null) {
                urlLogo = getClass().getResource("logo_corpac.png");
            }
            if (urlLogo != null) {
                setIconImage(new ImageIcon(urlLogo).getImage());
            } else {
                java.io.File archivoLogo = new java.io.File("src/com/aeropuerto/vista/logo_corpac.png");
                if (archivoLogo.exists()) {
                    setIconImage(new ImageIcon(archivoLogo.getAbsolutePath()).getImage());
                }
            }
        } catch (Exception error) {}
    }

    // Inicialización de componentes gráficos
    private void inicializarComponentes(Avion avion) {
        Color fondoOscuro = Theme.getBg();
        Color fondoTarjeta = Theme.getSurface();
        Color fondoHeader = Theme.getElevated();
        Color bordeTarjeta = Theme.getBorder();
        Color doradoSmart = Theme.getWarning();
        Color azulCyan = Theme.getPasajeroAccent();
        Color textoBlanco = Theme.getTexto();
        Color textoGris = Theme.getSecundario();

        JPanel panelPrincipal = new JPanel(new BorderLayout(12, 12));
        panelPrincipal.setBackground(fondoOscuro);
        panelPrincipal.setBorder(new EmptyBorder(12, 15, 12, 15));
        setContentPane(panelPrincipal);

        // Encabezado
        JPanel panelEncabezado = new JPanel(new BorderLayout(12, 0));
        panelEncabezado.setBackground(fondoHeader);
        panelEncabezado.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bordeTarjeta, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));

        JPanel panelTitulos = new JPanel(new GridLayout(2, 1, 0, 3));
        panelTitulos.setOpaque(false);

        JLabel lblTitulo = new JLabel("FICHA TÉCNICA OFICIAL: AIRBUS A320neo - MODELO A320-271N", JLabel.LEFT);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitulo.setForeground(doradoSmart);

        JLabel lblSubtitulo = new JLabel("Aeronave comercial para operaciones nacionales de CORPAC S.A.", JLabel.LEFT);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(textoGris);

        panelTitulos.add(lblTitulo);
        panelTitulos.add(lblSubtitulo);
        panelEncabezado.add(panelTitulos, BorderLayout.CENTER);

        // Logo en encabezado
        try {
            java.net.URL urlLogo = getClass().getResource("/com/aeropuerto/vista/logo_corpac.png");
            if (urlLogo == null) {
                urlLogo = getClass().getResource("logo_corpac.png");
            }
            Image imagenLogo = null;
            if (urlLogo != null) {
                imagenLogo = new ImageIcon(urlLogo).getImage();
            } else {
                java.io.File archivoLogo = new java.io.File("src/com/aeropuerto/vista/logo_corpac.png");
                if (archivoLogo.exists()) {
                    imagenLogo = new ImageIcon(archivoLogo.getAbsolutePath()).getImage();
                }
            }
            if (imagenLogo != null) {
                JLabel lblLogoEncabezado = new JLabel(new ImageIcon(imagenLogo.getScaledInstance(165, 44, Image.SCALE_SMOOTH)));
                panelEncabezado.add(lblLogoEncabezado, BorderLayout.EAST);
            }
        } catch (Exception error) {}

        panelPrincipal.add(panelEncabezado, BorderLayout.NORTH);

        // Panel central con imagen y datos
        JPanel panelCentro = new JPanel(new BorderLayout(10, 10));
        panelCentro.setOpaque(false);

        // Contenedor del Modelo 3D Interactivo
        JPanel panelContenedor3D = new JPanel(new BorderLayout(0, 4));
        panelContenedor3D.setOpaque(false);

        PanelVisorAvion3D visor3D = new PanelVisorAvion3D();
        visor3D.setPreferredSize(new Dimension(820, 270));
        visor3D.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bordeTarjeta, 1, true),
            new EmptyBorder(2, 2, 2, 2)
        ));

        // Barra de herramientas del visor 3D
        JPanel barraBotones3D = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        barraBotones3D.setOpaque(false);

        JButton btnGiro = new JButton("Pausar / Rotar Auto");
        btnGiro.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnGiro.setBackground(fondoHeader);
        btnGiro.setForeground(azulCyan);
        btnGiro.setFocusPainted(false);
        btnGiro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGiro.setBorder(BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(bordeTarjeta, 1), new EmptyBorder(3, 8, 3, 8)));
        btnGiro.addActionListener(e -> visor3D.alternarAnimacion());

        JButton btnMalla = new JButton("Malla 3D / Solido");
        btnMalla.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        btnMalla.setBackground(fondoHeader);
        btnMalla.setForeground(textoBlanco);
        btnMalla.setFocusPainted(false);
        btnMalla.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMalla.setBorder(BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(bordeTarjeta, 1), new EmptyBorder(3, 8, 3, 8)));
        btnMalla.addActionListener(e -> visor3D.alternarModoAlambre());

        JButton btnReset = new JButton("Centrar Camara");
        btnReset.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        btnReset.setBackground(fondoHeader);
        btnReset.setForeground(doradoSmart);
        btnReset.setFocusPainted(false);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.setBorder(BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(bordeTarjeta, 1), new EmptyBorder(3, 8, 3, 8)));
        btnReset.addActionListener(e -> visor3D.reiniciarCamara());

        barraBotones3D.add(btnGiro);
        barraBotones3D.add(btnMalla);
        barraBotones3D.add(btnReset);

        panelContenedor3D.add(visor3D, BorderLayout.CENTER);
        panelContenedor3D.add(barraBotones3D, BorderLayout.SOUTH);

        panelCentro.add(panelContenedor3D, BorderLayout.NORTH);

        // Especificaciones en 2 columnas
        JPanel panelCuadriculaEspecificaciones = new JPanel(new GridLayout(1, 2, 12, 10));
        panelCuadriculaEspecificaciones.setOpaque(false);

        // Columna 1: Rendimiento
        JPanel panelColumna1 = new JPanel(new GridLayout(6, 1, 4, 4));
        panelColumna1.setBackground(fondoTarjeta);
        panelColumna1.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(bordeTarjeta, 1),
                "Rendimiento Operativo y Dimensiones",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 11),
                doradoSmart
            ),
            new EmptyBorder(8, 10, 8, 10)
        ));

        panelColumna1.add(crearFila("Velocidad Crucero:", "828 km/h - Mach 0.78", textoGris, azulCyan));
        panelColumna1.add(crearFila("Alcance Máximo:", "6,500 km - Rutas en Perú", textoGris, azulCyan));
        panelColumna1.add(crearFila("Techo de Operación:", "12,100 m - 39,800 pies", textoGris, azulCyan));
        panelColumna1.add(crearFila("Envergadura:", "35.80 metros", textoGris, azulCyan));
        panelColumna1.add(crearFila("Longitud:", "37.57 metros", textoGris, azulCyan));
        panelColumna1.add(crearFila("Altura:", "11.76 metros", textoGris, azulCyan));
        panelCuadriculaEspecificaciones.add(panelColumna1);

        // Columna 2: Propulsión
        JPanel panelColumna2 = new JPanel(new GridLayout(6, 1, 4, 4));
        panelColumna2.setBackground(fondoTarjeta);
        panelColumna2.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(bordeTarjeta, 1),
                "Propulsión y Configuración",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 11),
                doradoSmart
            ),
            new EmptyBorder(8, 10, 8, 10)
        ));

        panelColumna2.add(crearFila("Fabricante:", "Airbus SAS", textoGris, textoBlanco));
        panelColumna2.add(crearFila("Matrícula:", "CC-BHB", textoGris, textoBlanco));
        panelColumna2.add(crearFila("Planta de Poder:", "2x CFM LEAP-1A26 Turbofán", textoGris, textoBlanco));
        panelColumna2.add(crearFila("Capacidad:", "180 plazas - 50 en simulación", textoGris, textoBlanco));
        panelColumna2.add(crearFila("Eficiencia:", "-20% consumo de combustible", textoGris, new Color(52, 211, 153)));
        panelColumna2.add(crearFila("Certificación:", "DGAC Perú / OACI", textoGris, textoBlanco));
        panelCuadriculaEspecificaciones.add(panelColumna2);

        panelCentro.add(panelCuadriculaEspecificaciones, BorderLayout.CENTER);

        // Reseña del modelo
        JTextArea txtDescripcion = new JTextArea();
        txtDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setEditable(false);
        txtDescripcion.setBackground(fondoTarjeta);
        txtDescripcion.setForeground(textoBlanco);
        txtDescripcion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bordeTarjeta, 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        txtDescripcion.setText(
            "Reseña del Modelo:\n" +
            "El Airbus A320neo es una aeronave comercial eficiente para rutas nacionales en el Perú. " +
            "Sus dispositivos Sharklets y motores LEAP-1A permiten aterrizajes seguros en aeropuertos de altura."
        );
        panelCentro.add(txtDescripcion, BorderLayout.SOUTH);

        panelPrincipal.add(panelCentro, BorderLayout.CENTER);

        // Botón de cierre
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelBotones.setOpaque(false);

        JButton botonCerrar = new JButton("Cerrar Ficha Técnica");
        botonCerrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        botonCerrar.setBackground(fondoHeader);
        botonCerrar.setForeground(textoBlanco);
        botonCerrar.setBorder(BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(bordeTarjeta, 1), new EmptyBorder(6, 14, 6, 14)));
        botonCerrar.setFocusPainted(false);
        botonCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonCerrar.addActionListener(evento -> dispose());
        panelBotones.add(botonCerrar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
    }

    // Fila informativa
    private JPanel crearFila(String etiqueta, String valor, Color colorEtiqueta, Color colorValor) {
        JPanel panelFila = new JPanel(new BorderLayout(5, 0));
        panelFila.setOpaque(false);
        JLabel etiquetaNombre = new JLabel(etiqueta);
        etiquetaNombre.setFont(new Font("Segoe UI", Font.BOLD, 11));
        etiquetaNombre.setForeground(colorEtiqueta);
        JLabel etiquetaValor = new JLabel(valor, JLabel.RIGHT);
        etiquetaValor.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        etiquetaValor.setForeground(colorValor);
        panelFila.add(etiquetaNombre, BorderLayout.WEST);
        panelFila.add(etiquetaValor, BorderLayout.EAST);
        return panelFila;
    }
}