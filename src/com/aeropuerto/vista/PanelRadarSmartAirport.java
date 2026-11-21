package com.aeropuerto.vista;

import com.aeropuerto.controlador.ControladorAeropuerto;
import com.aeropuerto.estructuras.ListaCircular;
import com.aeropuerto.modelo.PuertaEmbarque;
import com.aeropuerto.modelo.Usuario;
import com.aeropuerto.modelo.Vuelo;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

// HUMANIZACION UI
import com.aeropuerto.vista.tema.Theme;
import com.aeropuerto.vista.tema.AppIcon;
import com.aeropuerto.vista.tema.FontManager;
import com.aeropuerto.vista.tema.BotonPrimario;
import com.aeropuerto.vista.tema.PanelTelemetriaConsola;
import com.aeropuerto.vista.tema.GlassPanel;

// Tablero de control futurista de aeropuerto inteligente con gemelo digital 3D interactivo y control RBAC
public class PanelRadarSmartAirport extends JPanel {
    private static final long serialVersionUID = 1L;

    private final ControladorAeropuerto controlador;
    private final VentanaPrincipal ventanaPrincipal;
    private PanelTelemetriaConsola panelTelemetriaConsola;
    private JTabbedPane tabsHUD;

    // Paleta de colores Dark Mode y acentos neon
    private final Color fondoOscuro = new Color(11, 14, 20);
    private final Color fondoTarjeta = new Color(17, 24, 39);
    private final Color fondoEntrada = new Color(13, 18, 28);
    private final Color bordeTarjeta = new Color(31, 41, 55);
    private final Color rojoNeon = new Color(239, 68, 68);
    private final Color doradoNeon = new Color(245, 158, 11);
    private final Color amarilloMarcador = new Color(251, 191, 36);
    private final Color verdeNeon = new Color(16, 185, 129);
    private final Color cyanNeon = new Color(56, 189, 248);
    private final Color azulAcento = new Color(37, 99, 235);
    private final Color textoBlanco = new Color(248, 250, 252);
    private final Color textoGris = new Color(156, 163, 175);

    // Estado operacional del gemelo digital
    private boolean conflictoActivo = true;
    private final String puertaConflicto = "Gate B12 (Puerta 02)";
    private final String vueloConflicto = "CP-2024";
    private final String puertaRecomendada = "Gate 01 (C-646)";
    private String standSeleccionado = "Gate B12";

    // Modos de visualizacion: 0 = Gemelo Digital 3D Vectorial Interactivo, 1 = Gemelo Hibrido (Render + HUD), 2 = Radar Tactico AOC
    private int modoVisualizacion = 0;

    // Componentes de la cabecera y permisos RBAC
    private JLabel lblModoPermiso;
    private JButton btnAccesoAdminRapido;
    private JButton btnAlternarVista;
    private JButton btnRotar;

    // Componentes del HUD inferior (Flight Information)
    private JLabel lblConflictTitulo;
    private JLabel lblConflictDetalle;
    private JButton btnResolverConflicto;
    private JTable tablaArribos;
    private DefaultTableModel modeloArribos;
    private JTable tablaSalidas;
    private DefaultTableModel modeloSalidas;

    // Lienzo visual del gemelo digital 3D interactivo
    private LienzoGemeloDigital lienzoVisual;

    public PanelRadarSmartAirport(ControladorAeropuerto controlador, VentanaPrincipal ventanaPrincipal) {
        this.controlador = controlador;
        this.ventanaPrincipal = ventanaPrincipal;
        setLayout(new BorderLayout(0, 0));
        setBackground(Theme.getBg());
        inicializarComponentes();
        actualizarEstadoPermisos();
        refrescarDatos();
        Theme.addThemeListener(this::actualizarTema);
    }

    private void inicializarComponentes() {
        // 1. Lienzo interactivo del Gemelo Digital
        lienzoVisual = new LienzoGemeloDigital();
        lienzoVisual.setMinimumSize(new Dimension(600, 160));
        lienzoVisual.setPreferredSize(new Dimension(1100, 260));

        // 2. Panel HUD inferior con telemetria y tablas de vuelos
        JPanel panelHUD = crearPanelFlightInformation();
        panelHUD.setMinimumSize(new Dimension(600, 200));
        panelHUD.setPreferredSize(new Dimension(1100, 250));

        // Divisor vertical que mantiene proporciones equilibradas para el Avance 2
        JSplitPane splitPrincipal = new JSplitPane(JSplitPane.VERTICAL_SPLIT, lienzoVisual, panelHUD);
        splitPrincipal.setDividerLocation(230);
        splitPrincipal.setResizeWeight(0.40);
        splitPrincipal.setBorder(null);
        splitPrincipal.setDividerSize(4);
        splitPrincipal.setBackground(Theme.getBg());

        add(splitPrincipal, BorderLayout.CENTER);
    }

    public void alternarModoVisualizacion() {
        modoVisualizacion = (modoVisualizacion + 1) % 3;
        if (btnAlternarVista != null) {
            switch (modoVisualizacion) {
                case 0:
                    btnAlternarVista.setText("Modo: Gemelo 3D Vectorial");
                    break;
                case 1:
                    btnAlternarVista.setText("Modo: Gemelo Híbrido (3D)");
                    break;
                case 2:
                    btnAlternarVista.setText("Modo: Radar Táctico AOC");
                    break;
            }
        }
        if (lienzoVisual != null) lienzoVisual.repaint();
    }

    public PanelTelemetriaConsola getPanelTelemetriaConsola() {
        return panelTelemetriaConsola;
    }

    public JTabbedPane getTabsHUD() {
        return tabsHUD;
    }

    // Panel de datos inferior con tablas ARRIVING, DEPARTING y telemetria con contenedor GlassPanel
    private JPanel crearPanelFlightInformation() {
        GlassPanel panelHUD = new GlassPanel(new BorderLayout(0, 0), 16);
        panelHUD.setColorAcento(Theme.getAdminAccent());
        panelHUD.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.getBorder()));

        tabsHUD = new JTabbedPane();
        tabsHUD.setFont(FontManager.getBarlowCondensed(Font.BOLD, 11f));
        tabsHUD.setBackground(Theme.getSurface());
        tabsHUD.setForeground(Theme.getTexto());
        tabsHUD.setBorder(null);

        // Pestaña 1: Tráfico en Plataforma, Llegadas/Salidas y Conflicto
        JPanel panelTrafico = new JPanel(new BorderLayout(0, 6));
        panelTrafico.setBackground(Theme.getSurface());
        panelTrafico.setBorder(new EmptyBorder(6, 12, 6, 12));

        // Cabecera del panel de telemetria
        JPanel panelCabecera = new JPanel(new BorderLayout(10, 0));
        panelCabecera.setOpaque(false);

        JPanel pTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pTitulo.setOpaque(false);

        JLabel lblIcono = new JLabel(AppIcon.radar(14, Theme.getAdminAccent()));

        JLabel lblTitulo = new JLabel("FLIGHT INFORMATION & DIGITAL TWIN TELEMETRY");
        lblTitulo.setFont(FontManager.getBarlowCondensed(Font.BOLD, 12f));
        lblTitulo.setForeground(Theme.getTexto());

        JLabel lblSub = new JLabel("| Plataforma y Asignación de Puertas (AOC)");
        lblSub.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
        lblSub.setForeground(Theme.getSecundario());

        lblModoPermiso = new JLabel(" ROL: MODO CONSULTA / REGISTRO ");
        lblModoPermiso.setFont(FontManager.getIBMPlexSans(Font.BOLD, 10f));
        lblModoPermiso.setOpaque(true);
        lblModoPermiso.setBackground(Theme.getInput());
        lblModoPermiso.setForeground(Theme.getInfo());
        lblModoPermiso.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getBorder(), 1),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));

        pTitulo.add(lblIcono);
        pTitulo.add(lblTitulo);
        pTitulo.add(lblSub);
        pTitulo.add(lblModoPermiso);
        panelCabecera.add(pTitulo, BorderLayout.WEST);

        // Inicializar botones de control para el panel vertical de acciones ATC
        btnAccesoAdminRapido = PanelTelemetriaConsola.crearBotonOutline("Identificarse como Admin", Theme.getAdminAccent(), e -> alternarSesionAdminRapida());
        btnRotar = PanelTelemetriaConsola.crearBotonOutline("Rotar Puerta en Turno", Theme.getInfo(), e -> accionAvanzarTurno());
        btnAlternarVista = PanelTelemetriaConsola.crearBotonOutline("Modo: Gemelo 3D Vectorial", Theme.getTexto(), e -> alternarModoVisualizacion());

        panelTrafico.add(panelCabecera, BorderLayout.NORTH);

        // Bloque central con 3 columnas: ARRIVING, DEPARTING y Apartado Vertical de Control ATC
        JPanel panelContenido = new JPanel(new GridLayout(1, 3, 10, 0));
        panelContenido.setOpaque(false);

        panelContenido.add(crearSubpanelArribos());
        panelContenido.add(crearSubpanelSalidas());
        panelContenido.add(crearTarjetaControlConflicto());

        panelTrafico.add(panelContenido, BorderLayout.CENTER);

        // Pestaña 2: Telemetria y Logs con JTextPane + StyledDocument y chips TAD
        panelTelemetriaConsola = new PanelTelemetriaConsola(
                e -> {
                    if (ventanaPrincipal != null) ventanaPrincipal.accionSincronizarMySQLPublica();
                    else controlador.sincronizarConBD();
                },
                e -> {
                    if (ventanaPrincipal != null) ventanaPrincipal.accionCargarDesdeMySQLPublica();
                },
                e -> alternarModoVisualizacion(),
                e -> accionAvanzarTurno()
        );

        tabsHUD.addTab("Tráfico Aéreo y Asignación de Puertas", panelTrafico);
        tabsHUD.addTab("Telemetría y Registro de Operaciones", panelTelemetriaConsola);

        panelHUD.add(tabsHUD, BorderLayout.CENTER);
        return panelHUD;
    }

    private JPanel crearSubpanelArribos() {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(fondoTarjeta);
        p.setBorder(new CompoundBorder(
                new LineBorder(bordeTarjeta, 1),
                new EmptyBorder(6, 8, 6, 8)));

        JLabel lbl = new JLabel("Llegadas y Aproximación");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(cyanNeon);
        p.add(lbl, BorderLayout.NORTH);

        String[] cols = {"PUERTA", "VUELO", "ORIGEN", "HORA", "ESTADO"};
        modeloArribos = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tablaArribos = new JTable(modeloArribos);
        estilizarTablaHUD(tablaArribos);
        tablaArribos.getColumnModel().getColumn(4).setCellRenderer(new RenderEstadoVuelo());

        JScrollPane sc = new JScrollPane(tablaArribos);
        sc.getViewport().setBackground(fondoEntrada);
        sc.setBorder(BorderFactory.createLineBorder(bordeTarjeta));
        p.add(sc, BorderLayout.CENTER);
        return p;
    }

    private JPanel crearSubpanelSalidas() {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(fondoTarjeta);
        p.setBorder(new CompoundBorder(
                new LineBorder(bordeTarjeta, 1),
                new EmptyBorder(6, 8, 6, 8)));

        JLabel lbl = new JLabel("Salidas y Plataforma");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(doradoNeon);
        p.add(lbl, BorderLayout.NORTH);

        String[] cols = {"PUERTA", "VUELO", "DESTINO", "HORA", "ESTADO"};
        modeloSalidas = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tablaSalidas = new JTable(modeloSalidas);
        estilizarTablaHUD(tablaSalidas);
        tablaSalidas.getColumnModel().getColumn(4).setCellRenderer(new RenderEstadoVuelo());

        JScrollPane sc = new JScrollPane(tablaSalidas);
        sc.getViewport().setBackground(fondoEntrada);
        sc.setBorder(BorderFactory.createLineBorder(bordeTarjeta));
        p.add(sc, BorderLayout.CENTER);
        return p;
    }

    private JPanel crearTarjetaControlConflicto() {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(fondoTarjeta);
        p.setBorder(new CompoundBorder(
                new LineBorder(conflictoActivo ? rojoNeon : verdeNeon, 1),
                new EmptyBorder(6, 8, 6, 8)));

        JPanel pNorte = new JPanel(new BorderLayout(0, 4));
        pNorte.setOpaque(false);

        JPanel pTitulos = new JPanel(new GridLayout(2, 1, 0, 1));
        pTitulos.setOpaque(false);

        lblConflictTitulo = new JLabel(conflictoActivo ? "ALERTA: CONFLICTO EN PUERTA B12" : "PUERTA B12 - CONFLICTO RESUELTO");
        lblConflictTitulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblConflictTitulo.setForeground(conflictoActivo ? rojoNeon : verdeNeon);

        lblConflictDetalle = new JLabel(conflictoActivo
                ? "Reasignar CP-2024 a Gate 01 libre (Lista Circular)"
                : "Stand Gate 01 asignado · Plataforma despejada");
        lblConflictDetalle.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblConflictDetalle.setForeground(new Color(148, 163, 184));

        pTitulos.add(lblConflictTitulo);
        pTitulos.add(lblConflictDetalle);
        pNorte.add(pTitulos, BorderLayout.CENTER);

        btnResolverConflicto = new JButton(conflictoActivo ? "Resolver Conflicto con Lista Circular" : "Conflicto Resuelto");
        btnResolverConflicto.setFont(FontManager.getIBMPlexSans(Font.BOLD, 10.5f));
        btnResolverConflicto.setBackground(conflictoActivo ? Theme.getDanger() : Theme.getSuccess());
        btnResolverConflicto.setForeground(Color.WHITE);
        btnResolverConflicto.setFocusPainted(false);
        btnResolverConflicto.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnResolverConflicto.setEnabled(conflictoActivo);
        btnResolverConflicto.setPreferredSize(new Dimension(0, 26));
        btnResolverConflicto.addActionListener(e -> accionResolverConflicto());
        pNorte.add(btnResolverConflicto, BorderLayout.SOUTH);

        p.add(pNorte, BorderLayout.NORTH);

        // Cuadrícula 2x2 para Comandos de Control ATC (Nunca se aplastan)
        JPanel pGridBotones = new JPanel(new GridLayout(2, 2, 6, 4));
        pGridBotones.setOpaque(false);
        pGridBotones.setBorder(new EmptyBorder(4, 0, 0, 0));

        if (btnRotar != null) pGridBotones.add(btnRotar);
        if (btnAlternarVista != null) pGridBotones.add(btnAlternarVista);
        JButton btnInspeccion3D = PanelTelemetriaConsola.crearBotonOutline("Modelo 3D Interactivo", Theme.getPasajeroAccent(), e -> abrirVisorAeronave3D());
        pGridBotones.add(btnInspeccion3D);
        if (btnAccesoAdminRapido != null) pGridBotones.add(btnAccesoAdminRapido);

        p.add(pGridBotones, BorderLayout.CENTER);

        return p;
    }

    private void estilizarTablaHUD(JTable t) {
        t.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 10f));
        t.setRowHeight(20);
        t.setBackground(Theme.getInput());
        t.setForeground(Theme.getTexto());
        t.setGridColor(Theme.getBorder());
        t.setSelectionBackground(Theme.getElevated());
        t.setSelectionForeground(Theme.getAdminAccent());

        JTableHeader th = t.getTableHeader();
        th.setBackground(Theme.getElevated());
        th.setForeground(Theme.getSecundario());
        th.setFont(FontManager.getBarlowCondensed(Font.BOLD, 10f));

        DefaultTableCellRenderer c = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value == null || value.toString().trim().isEmpty()) {
                    setText("-");
                }
                return comp;
            }
        };
        c.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < t.getColumnCount(); i++) {
            if (i != 4) {
                t.getColumnModel().getColumn(i).setCellRenderer(c);
            }
        }
    }

    // Gestion de control de permisos RBAC
    public void actualizarEstadoPermisos() {
        boolean esAdmin = controlador != null && controlador.esSesionAdmin();

        if (lblModoPermiso != null) {
            if (esAdmin) {
                Usuario u = controlador.obtenerUsuarioActual();
                String nombre = (u != null) ? u.obtenerUsuario() : "admin";
                lblModoPermiso.setText(" [ ROL: ADMINISTRADOR (" + nombre + ") - ACCESO TOTAL ] ");
                lblModoPermiso.setBackground(Theme.getSuccessSoft());
                lblModoPermiso.setForeground(Theme.getSuccess());
                lblModoPermiso.setBorder(new CompoundBorder(new LineBorder(Theme.getSuccess(), 1), new EmptyBorder(2, 6, 2, 6)));
            } else {
                lblModoPermiso.setText(" [ ROL: MODO CONSULTA / REGISTRO ] ");
                lblModoPermiso.setBackground(Theme.getInput());
                lblModoPermiso.setForeground(Theme.getInfo());
                lblModoPermiso.setBorder(new CompoundBorder(new LineBorder(Theme.getBorder(), 1), new EmptyBorder(2, 6, 2, 6)));
            }
        }

        if (btnAccesoAdminRapido != null) {
            if (esAdmin) {
                btnAccesoAdminRapido.setText("Cerrar Sesión Admin");
                btnAccesoAdminRapido.setForeground(Theme.getDanger());
            } else {
                btnAccesoAdminRapido.setText("Identificarse como Admin");
                btnAccesoAdminRapido.setForeground(Theme.getAdminAccent());
            }
        }

        if (btnResolverConflicto != null && conflictoActivo) {
            if (esAdmin) {
                btnResolverConflicto.setText("Resolver Conflicto con Lista Circular");
                btnResolverConflicto.setToolTipText("Ejecutar reasignación de aeronave en memoria RAM y persistir en MySQL");
            } else {
                btnResolverConflicto.setText("Resolver Conflicto [Requiere Admin]");
                btnResolverConflicto.setToolTipText("Inicie sesión como Administrador para modificar la asignación de plataforma");
            }
        }

        if (lienzoVisual != null) {
            lienzoVisual.repaint();
        }
    }

    private void alternarSesionAdminRapida() {
        if (controlador.esSesionAdmin()) {
            int opc = JOptionPane.showConfirmDialog(this,
                    "¿Desea cerrar la sesión de Administrador actual?",
                    "Cerrar Sesión", JOptionPane.YES_NO_OPTION);
            if (opc == JOptionPane.YES_OPTION) {
                controlador.cerrarSesion();
                actualizarEstadoPermisos();
                if (ventanaPrincipal != null) {
                    ventanaPrincipal.actualizarVistaPuertasExterna();
                }
            }
        } else {
            if (ventanaPrincipal != null) {
                ventanaPrincipal.abrirDialogoLoginAdminPublico();
                actualizarEstadoPermisos();
            }
        }
    }

    private boolean validarPermisoAdmin(String accion) {
        if (controlador != null && controlador.esSesionAdmin()) {
            return true;
        }

        int resp = JOptionPane.showConfirmDialog(this,
                "Acceso Restringido (Modo Consulta):\n\n" +
                "La acción operacional '" + accion + "' modifica el estado de la plataforma\n" +
                "y requiere privilegios de Administrador ATC.\n\n" +
                "¿Desea iniciar sesión como Administrador para ejecutar esta orden?",
                "Permiso de Administrador Requerido",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (resp == JOptionPane.YES_OPTION) {
            if (ventanaPrincipal != null) {
                ventanaPrincipal.abrirDialogoLoginAdminPublico();
                actualizarEstadoPermisos();
                return controlador.esSesionAdmin();
            }
        }
        return false;
    }

    // Resolucion del conflicto aplicando la lista circular y persistiendo en MySQL
    public void accionResolverConflicto() {
        if (!conflictoActivo) return;

        if (!validarPermisoAdmin("Reasignación de aeronave y mitigación de conflicto en plataforma")) {
            return;
        }

        // 1. Asignar vuelo al stand recomendado (Puerta 01)
        controlador.asignarVueloAPuertaEspecifica("Puerta 01", vueloConflicto);

        // 2. Liberar y actualizar el estado en memoria y persistencia
        conflictoActivo = false;
        standSeleccionado = "Gate 01 (C-646)";

        lblConflictTitulo.setText("PUERTA B12 - CONFLICTO RESUELTO");
        lblConflictTitulo.setForeground(verdeNeon);
        lblConflictDetalle.setText("Vuelo " + vueloConflicto + " reasignado a Puerta 01 · Plataforma despejada");
        lblConflictDetalle.setForeground(new Color(16, 185, 129));

        btnResolverConflicto.setText("Conflicto Resuelto");
        btnResolverConflicto.setBackground(Theme.getSuccess());
        btnResolverConflicto.setEnabled(false);

        if (panelTelemetriaConsola != null) {
            panelTelemetriaConsola.logOk("Conflicto mitigado: Vuelo " + vueloConflicto + " reasignado a Puerta 01 vía Lista Circular.");
        }

        refrescarDatos();
        lienzoVisual.repaint();

        if (ventanaPrincipal != null) {
            ventanaPrincipal.actualizarVistaPuertasExterna();
        }

        if (!GraphicsEnvironment.isHeadless()) {
            JOptionPane.showMessageDialog(this,
                    "Resolución Operacional Completada:\n" +
                    "- Conflicto en Gate B12 mitigado exitosamente.\n" +
                    "- Vuelo " + vueloConflicto + " reasignado a Puerta 01 (C-646) mediante rotación de puertas.\n" +
                    "- Estado sincronizado en tiempo real con MySQL.",
                    "Gemelo Digital - Resolución de Puerta", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void accionAvanzarTurno() {
        if (!validarPermisoAdmin("Rotar turno de puertas de embarque")) {
            return;
        }

        ListaCircular lc = controlador.obtenerListaPuertas();
        if (lc != null && !lc.estaVacia()) {
            PuertaEmbarque sig = lc.avanzarSiguiente();
            if (sig != null) {
                standSeleccionado = sig.obtenerNumeroPuerta();
                if (panelTelemetriaConsola != null) {
                    panelTelemetriaConsola.logInfo("Turno avanzado en Lista Circular a puerta " + sig.obtenerNumeroPuerta());
                }
            }
        }
        refrescarDatos();
        lienzoVisual.repaint();
        if (ventanaPrincipal != null) {
            ventanaPrincipal.actualizarVistaPuertasExterna();
        }
    }

    public void abrirVisorAeronave3D() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog diag3D = new JDialog(parent, "Inspeccion 3D Interactiva - Airbus A320neo / Flota CORPAC S.A.", true);
        diag3D.setSize(920, 700);
        diag3D.setLocationRelativeTo(this);

        JPanel pPrincipal = new JPanel(new BorderLayout(0, 8));
        pPrincipal.setBackground(Theme.getBg());
        pPrincipal.setBorder(new EmptyBorder(10, 12, 10, 12));

        PanelVisorAvion3D visor = new PanelVisorAvion3D();
        visor.setBorder(BorderFactory.createLineBorder(Theme.getBorder(), 1));

        JPanel pBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        pBotones.setOpaque(false);

        JButton bGiro = new JButton("Pausar / Rotar Auto");
        bGiro.setFont(new Font("Segoe UI", Font.BOLD, 11));
        bGiro.setBackground(Theme.getElevated());
        bGiro.setForeground(Theme.getPasajeroAccent());
        bGiro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bGiro.setFocusPainted(false);
        bGiro.addActionListener(ev -> visor.alternarAnimacion());

        JButton bMalla = new JButton("Malla Vectorial / Solido");
        bMalla.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        bMalla.setBackground(Theme.getElevated());
        bMalla.setForeground(Theme.getTexto());
        bMalla.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bMalla.setFocusPainted(false);
        bMalla.addActionListener(ev -> visor.alternarModoAlambre());

        JButton bReset = new JButton("Reiniciar Camara");
        bReset.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        bReset.setBackground(Theme.getElevated());
        bReset.setForeground(Theme.getWarning());
        bReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bReset.setFocusPainted(false);
        bReset.addActionListener(ev -> visor.reiniciarCamara());

        JButton bCerrar = new JButton("Cerrar Visor 3D");
        bCerrar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        bCerrar.setBackground(Theme.getElevated());
        bCerrar.setForeground(Color.WHITE);
        bCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bCerrar.setFocusPainted(false);
        bCerrar.addActionListener(ev -> diag3D.dispose());

        pBotones.add(bGiro);
        pBotones.add(bMalla);
        pBotones.add(bReset);
        pBotones.add(bCerrar);

        pPrincipal.add(visor, BorderLayout.CENTER);
        pPrincipal.add(pBotones, BorderLayout.SOUTH);

        diag3D.setContentPane(pPrincipal);
        diag3D.setVisible(true);
    }

    // Sincronización con el tema global Claro / Oscuro
    public void actualizarTema() {
        setBackground(Theme.getBg());
        if (tabsHUD != null) {
            tabsHUD.setBackground(Theme.getSurface());
            tabsHUD.setForeground(Theme.getTexto());
        }
        if (tablaArribos != null) {
            tablaArribos.setBackground(Theme.getInput());
            tablaArribos.setForeground(Theme.getTexto());
            tablaArribos.setGridColor(Theme.getBorder());
            tablaArribos.setSelectionBackground(Theme.getElevated());
            tablaArribos.setSelectionForeground(Theme.getAdminAccent());
            if (tablaArribos.getTableHeader() != null) {
                tablaArribos.getTableHeader().setBackground(Theme.getElevated());
                tablaArribos.getTableHeader().setForeground(Theme.getSecundario());
            }
        }
        if (tablaSalidas != null) {
            tablaSalidas.setBackground(Theme.getInput());
            tablaSalidas.setForeground(Theme.getTexto());
            tablaSalidas.setGridColor(Theme.getBorder());
            tablaSalidas.setSelectionBackground(Theme.getElevated());
            tablaSalidas.setSelectionForeground(Theme.getAdminAccent());
            if (tablaSalidas.getTableHeader() != null) {
                tablaSalidas.getTableHeader().setBackground(Theme.getElevated());
                tablaSalidas.getTableHeader().setForeground(Theme.getSecundario());
            }
        }
        actualizarEstadoPermisos();
        if (lienzoVisual != null) {
            lienzoVisual.repaint();
        }
        repaint();
    }

    // Actualiza tablas de arribos y salidas desde las estructuras reales en RAM y MySQL
    public void refrescarDatos() {
        if (modeloArribos == null || modeloSalidas == null) return;
        modeloArribos.setRowCount(0);
        modeloSalidas.setRowCount(0);

        Vuelo[] arrVuelos = controlador.obtenerVuelos();
        int total = controlador.obtenerContadorVuelos();

        if (total == 0) {
            if (conflictoActivo) {
                modeloArribos.addRow(new Object[]{"B12", "CP-2024", "Arequipa", "19:55", "CONFLICTO"});
                modeloArribos.addRow(new Object[]{"04", "CP-2026", "Iquitos", "20:30", "APROXIMACIÓN"});
                modeloArribos.addRow(new Object[]{"05", "CP-2028", "Trujillo", "21:10", "A TIEMPO"});
                modeloSalidas.addRow(new Object[]{"B12", "CP-2023", "Cusco", "19:40", "DEMORA"});
                modeloSalidas.addRow(new Object[]{"01", "CP-2025", "Piura", "20:15", "ABORDANDO"});
                modeloSalidas.addRow(new Object[]{"03", "CP-2027", "Tarapoto", "20:45", "A TIEMPO"});
            } else {
                modeloArribos.addRow(new Object[]{"01", "CP-2024", "Arequipa", "19:55", "ASIGNADO"});
                modeloArribos.addRow(new Object[]{"04", "CP-2026", "Iquitos", "20:30", "APROXIMACIÓN"});
                modeloArribos.addRow(new Object[]{"05", "CP-2028", "Trujillo", "21:10", "A TIEMPO"});
                modeloSalidas.addRow(new Object[]{"B12", "CP-2023", "Cusco", "19:40", "DESPACHO"});
                modeloSalidas.addRow(new Object[]{"01", "CP-2025", "Piura", "20:15", "ABORDANDO"});
                modeloSalidas.addRow(new Object[]{"03", "CP-2027", "Tarapoto", "20:45", "A TIEMPO"});
            }
        } else {
            for (int i = 0; i < total; i++) {
                Vuelo v = arrVuelos[i];
                if (v != null) {
                    String puerta = "0" + ((i % 5) + 1);
                    if (v.obtenerCodigo().equalsIgnoreCase("CP-2024")) {
                        puerta = conflictoActivo ? "B12" : "01";
                    }
                    if (i % 2 == 0) {
                        String est = (puerta.equals("B12") && conflictoActivo) ? "DEMORA" : "ABORDANDO";
                        modeloSalidas.addRow(new Object[]{puerta, v.obtenerCodigo(), v.obtenerDestino(), v.obtenerHoraSalida(), est});
                    } else {
                        String est = (puerta.equals("B12") && conflictoActivo) ? "CONFLICTO" : "A TIEMPO";
                        modeloArribos.addRow(new Object[]{puerta, v.obtenerCodigo(), v.obtenerOrigen(), v.obtenerHoraLlegada(), est});
                    }
                }
            }
        }

        if (lienzoVisual != null) {
            lienzoVisual.repaint();
        }
    }

    // Lienzo visual que renderiza el Gemelo Digital 3D Isométrico Interactivo
    private class LienzoGemeloDigital extends JPanel {
        private static final long serialVersionUID = 1L;

        private BufferedImage imagenGemelo;
        private final Timer timerAnimacion;
        private double fasePulso = 0.0;
        private float desplazamientoGuia = 0.0f;

        // Marcador actualmente bajo el cursor (Hover)
        private MarcadorPuerta marcadorHover = null;
        private Point posicionRaton = new Point(-1, -1);

        // Posiciones de los 8 stands en el espacio base 1376x768
        private final MarcadorPuerta[] marcadores = {
                new MarcadorPuerta("02", "Stand C-638 (Gate 02)", 635, 75, 635, 135, false, "CP-2022", "Trujillo", "A320neo", "EN PLATAFORMA"),
                new MarcadorPuerta("04", "Stand C-640 (Gate 04)", 380, 160, 380, 215, false, "CP-2026", "Iquitos", "A320neo", "APROXIMACIÓN"),
                new MarcadorPuerta("03", "Stand C-641 (Gate 03 Norte)", 265, 260, 265, 315, false, "CP-2027", "Tarapoto", "A320neo", "A TIEMPO"),
                new MarcadorPuerta("B12", "Stand C-642 (Gate B12)", 735, 335, 695, 385, true, "CP-2024", "Arequipa", "A320neo", "CONFLICTO PLATAFORMA"),
                new MarcadorPuerta("01", "Stand C-646 (Gate 01)", 490, 420, 490, 475, false, "CP-2025", "Piura", "A320neo", "STAND RECOMENDADO"),
                new MarcadorPuerta("11", "Stand C-648 (Gate 11)", 920, 475, 920, 525, false, "CP-2029", "Tacna", "A320neo", "EN EMBARQUE"),
                new MarcadorPuerta("12", "Stand C-650 (Gate 12)", 770, 580, 770, 625, false, "CP-2030", "Juliaca", "A320neo", "EN ESPERA"),
                new MarcadorPuerta("05", "Stand C-652 (Gate 05 Sur)", 1160, 350, 1160, 395, false, "CP-2028", "Chiclayo", "A320neo", "EN MANTENIMIENTO")
        };

        public LienzoGemeloDigital() {
            setBackground(fondoOscuro);
            cargarImagenRender();

            // Temporizador continuo para animacion de balizas aeronave y trayectorias (solo si es visible)
            timerAnimacion = new Timer(50, e -> {
                if (!isShowing()) return;
                fasePulso = (fasePulso + 0.08) % (Math.PI * 2);
                desplazamientoGuia = (desplazamientoGuia + 0.8f) % 16.0f;
                repaint();
            });
            timerAnimacion.start();

            // Deteccion de hover para telemetria flotante interactiva
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    posicionRaton = e.getPoint();
                    double escalaX = (double) getWidth() / 1376.0;
                    double escalaY = (double) getHeight() / 768.0;

                    MarcadorPuerta previoHover = marcadorHover;
                    marcadorHover = null;

                    for (MarcadorPuerta m : marcadores) {
                        int mx = (int) (m.xInsignia * escalaX);
                        int my = (int) (m.yInsignia * escalaY);
                        int ax = (int) (m.xAvion * escalaX);
                        int ay = (int) (m.yAvion * escalaY);

                        // Area de deteccion combinada de insignia y aeronave
                        Rectangle areaInsignia = new Rectangle(mx - 22, my - 16, 44, 32);
                        Rectangle areaAvion = new Rectangle(ax - 35, ay - 30, 70, 60);

                        if (areaInsignia.contains(e.getPoint()) || areaAvion.contains(e.getPoint())) {
                            marcadorHover = m;
                            break;
                        }
                    }

                    if (previoHover != marcadorHover) {
                        setCursor(marcadorHover != null ? new Cursor(Cursor.HAND_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));
                        repaint();
                    } else if (marcadorHover != null) {
                        repaint();
                    }
                }
            });

            // Interaccion al hacer clic: seleccion de stand y resolucion de conflicto
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    double escalaX = (double) getWidth() / 1376.0;
                    double escalaY = (double) getHeight() / 768.0;

                    for (MarcadorPuerta m : marcadores) {
                        int mx = (int) (m.xInsignia * escalaX);
                        int my = (int) (m.yInsignia * escalaY);
                        int ax = (int) (m.xAvion * escalaX);
                        int ay = (int) (m.yAvion * escalaY);

                        Rectangle areaInsignia = new Rectangle(mx - 22, my - 16, 44, 32);
                        Rectangle areaAvion = new Rectangle(ax - 35, ay - 30, 70, 60);

                        if (areaInsignia.contains(e.getPoint()) || areaAvion.contains(e.getPoint())) {
                            standSeleccionado = m.nombreCompleto;
                            if (m.esConflicto && conflictoActivo) {
                                accionResolverConflicto();
                            } else {
                                JOptionPane.showMessageDialog(PanelRadarSmartAirport.this,
                                        "Telemetría de Plataforma - " + m.nombreCompleto + "\n" +
                                        "- Vuelo Asignado: " + m.vuelo + " (" + m.modeloAvion + ")\n" +
                                        "- Ruta: " + m.destino + "\n" +
                                        "- Estado: " + (m.esConflicto && conflictoActivo ? "CONFLICTO ACTIVO" : m.estado) + "\n" +
                                        "- Nivel de Acceso: " + (controlador.esSesionAdmin() ? "Administrador (Control Total)" : "Modo Consulta / Registro"),
                                        "Telemetría Stand Digital Twin", JOptionPane.INFORMATION_MESSAGE);
                            }
                            repaint();
                            break;
                        }
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    marcadorHover = null;
                    posicionRaton = new Point(-1, -1);
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    repaint();
                }
            });
        }

        private void cargarImagenRender() {
            try {
                URL url = getClass().getResource("/com/aeropuerto/vista/gemelo_digital_3d.jpg");
                if (url == null) url = getClass().getResource("gemelo_digital_3d.jpg");
                if (url != null) {
                    imagenGemelo = ImageIO.read(url);
                } else {
                    File f = new File("src/com/aeropuerto/vista/gemelo_digital_3d.jpg");
                    if (f.exists()) imagenGemelo = ImageIO.read(f);
                }
            } catch (Exception ex) {
                // Modo vectorial garantizado si no existe la imagen
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int w = getWidth();
            int h = getHeight();

            double escalaX = (double) w / 1376.0;
            double escalaY = (double) h / 768.0;

            if (modoVisualizacion == 0) {
                // Modo 100% Vectorial Interactivo: renderizado Java2D de alta fidelidad
                dibujarFondoPlataformaIsometrico(g2, w, h);
                dibujarTerminalFingersIsometricos(g2, escalaX, escalaY);
                dibujarAvionesEnStands(g2, escalaX, escalaY);
                dibujarZonaAlertaNeon(g2, escalaX, escalaY);
                dibujarLineaConexionVectorial(g2, escalaX, escalaY);
                dibujarMarcadoresNumerados(g2, escalaX, escalaY);
                dibujarTarjetaTooltipHover(g2, w, h);
                dibujarLeyendaOperacional(g2);
                dibujarBannerAlerta(g2, w);
            } else if (modoVisualizacion == 1 && imagenGemelo != null) {
                // Modo Hibrido: Render fotorrealista + capas vectoriales de telemetria
                g2.drawImage(imagenGemelo, 0, 0, w, h, null);
                dibujarZonaAlertaNeon(g2, escalaX, escalaY);
                dibujarLineaConexionVectorial(g2, escalaX, escalaY);
                dibujarMarcadoresNumerados(g2, escalaX, escalaY);
                dibujarTarjetaTooltipHover(g2, w, h);
                dibujarLeyendaOperacional(g2);
                dibujarBannerAlerta(g2, w);
            } else {
                // Modo Radar Tactico AOC
                dibujarRadarVectorialAOC(g2, w, h);
                dibujarAvionesEnStands(g2, escalaX, escalaY);
                dibujarZonaAlertaNeon(g2, escalaX, escalaY);
                dibujarLineaConexionVectorial(g2, escalaX, escalaY);
                dibujarMarcadoresNumerados(g2, escalaX, escalaY);
                dibujarTarjetaTooltipHover(g2, w, h);
                dibujarLeyendaOperacional(g2);
                dibujarBannerAlerta(g2, w);
            }

            g2.dispose();
        }

        // Renderizado del asfalto de plataforma aeroportuaria, rejilla tenue y taxiways continuas/discontinuas
        private void dibujarFondoPlataformaIsometrico(Graphics2D g2, int w, int h) {
            // Fondo asfalto plataforma aeroportuaria
            g2.setColor(Theme.getBg());
            g2.fillRect(0, 0, w, h);

            // Rejilla tenue AlphaComposite ~0.10 (no negro vacio)
            Composite compOriginal = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f));
            g2.setColor(Theme.getBorder());
            int cx = w / 2;
            int cy = h / 2;
            for (int r = 60; r < Math.max(w, h); r += 65) {
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);
            }
            for (int x = 0; x < w; x += 55) {
                g2.drawLine(x, 0, x, h);
            }
            for (int y = 0; y < h; y += 45) {
                g2.drawLine(0, y, w, y);
            }
            g2.setComposite(compOriginal);

            // Taxiways BasicStroke(2f, CAP_BUTT, JOIN_MITER, 1f, new float[]{10, 8}, 0f) en ambar (discontinuas REALES)
            BasicStroke strokeTaxiway = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{10.0f, 8.0f}, 0.0f);
            g2.setColor(Theme.getAdminAccent());
            g2.setStroke(strokeTaxiway);

            int taxiY1 = (int) (h * 0.36);
            int taxiY2 = (int) (h * 0.70);
            g2.drawLine(0, taxiY1, w, taxiY1);
            g2.drawLine(0, taxiY2, w, taxiY2);

            // Lineas de guia a los stands
            double sx = (double) w / 1376.0;
            double sy = (double) h / 768.0;
            for (MarcadorPuerta m : marcadores) {
                int ax = (int) (m.xAvion * sx);
                int ay = (int) (m.yAvion * sy);
                int targetY = (ay < h / 2) ? taxiY1 : taxiY2;
                g2.drawLine(ax, ay + (ay < h / 2 ? 22 : -22), ax, targetY);
            }
        }

        // Renderizado del edificio terminal y pasarelas telescopicas (fingers)
        private void dibujarTerminalFingersIsometricos(Graphics2D g2, double sx, double sy) {
            // Edificio concourse superior
            GeneralPath terminal = new GeneralPath();
            terminal.moveTo(100 * sx, 0);
            terminal.lineTo(1300 * sx, 0);
            terminal.lineTo(1220 * sx, 130 * sy);
            terminal.lineTo(180 * sx, 130 * sy);
            terminal.closePath();

            // Relleno de la estructura del terminal
            GradientPaint gradTerminal = new GradientPaint(
                    (float)(200 * sx), 0, Theme.getElevated(),
                    (float)(600 * sx), (float)(130 * sy), Theme.getSurface());
            g2.setPaint(gradTerminal);
            g2.fill(terminal);

            // Borde estructural y cristalera
            g2.setColor(Theme.getBorder());
            g2.setStroke(new BasicStroke(1.8f));
            g2.draw(terminal);

            // Fachada con ventanales iluminados
            g2.setColor(new Color(56, 189, 248, 60));
            for (double x = 240 * sx; x < 1180 * sx; x += 55 * sx) {
                g2.fillRect((int)x, (int)(40 * sy), (int)(32 * sx), (int)(65 * sy));
            }

            // Pasarelas telescopicas (Fingers) conectando a los stands principales
            g2.setColor(Theme.getSecundario());
            g2.setStroke(new BasicStroke(3.0f));
            for (MarcadorPuerta m : marcadores) {
                int ax = (int) (m.xAvion * sx);
                int ay = (int) (m.yAvion * sy);
                if (ay < 500 * sy) {
                    g2.drawLine(ax - 20, (int)(120 * sy), ax - 12, ay - 14);
                    // Cabina de acople
                    g2.setColor(Theme.getBorder());
                    g2.fillRect(ax - 16, ay - 18, 10, 8);
                    g2.setColor(Theme.getSecundario());
                }
            }
        }

        // Renderizado de las aeronaves cenitales en cada stand
        private void dibujarAvionesEnStands(Graphics2D g2, double sx, double sy) {
            for (MarcadorPuerta m : marcadores) {
                int ax = (int) (m.xAvion * sx);
                int ay = (int) (m.yAvion * sy);
                boolean esConflicto = m.esConflicto && conflictoActivo;
                boolean esHover = (marcadorHover == m);

                dibujarAvionCenitalProporcional(g2, ax, ay, 36 * sx, m, esConflicto, esHover);
            }
        }

        // Aviones Path2D cenital PROPORCIONAL (fuselaje + alas + cola desde arriba) con estela de rumbo
        private void dibujarAvionCenitalProporcional(Graphics2D g2, int cx, int cy, double escala,
                                                    MarcadorPuerta m, boolean enConflicto, boolean esHover) {
            // Semántica de colores de avión:
            // puerta = success / rodando = gris-azulado (#58A6FF) / conflicto = danger (#F85149)
            Color colorSemantico;
            if (enConflicto) {
                colorSemantico = Theme.getDanger();
            } else if (m.numero.equals("04") || m.estado.contains("APROXIMACIÓN") || m.estado.contains("MANTENIMIENTO")) {
                colorSemantico = new Color(0x58, 0xA6, 0xFF); // Gris-azulado aeronáutico
            } else {
                colorSemantico = Theme.getSuccess();
            }

            // 1. Estela de rumbo hacia donde se desplaza / rumbo de la aeronave
            g2.setColor(new Color(colorSemantico.getRed(), colorSemantico.getGreen(), colorSemantico.getBlue(), 140));
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{4f, 4f}, desplazamientoGuia));
            int dxRumbo = (m.xAvion > 700) ? 28 : -28;
            int dyRumbo = (m.yAvion < 350) ? 20 : -20;
            g2.drawLine(cx, cy, cx + dxRumbo, cy + dyRumbo);
            g2.fillOval(cx + dxRumbo - 2, cy + dyRumbo - 2, 5, 5);

            // Resaltado de escaneo si el mouse esta en hover
            if (esHover) {
                g2.setColor(colorSemantico);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{4f, 4f}, desplazamientoGuia));
                g2.drawOval(cx - (int)(escala * 1.1), cy - (int)(escala * 1.1), (int)(escala * 2.2), (int)(escala * 2.2));
            }

            double L = Math.max(38.0, escala * 1.45); // Longitud de fuselaje
            double W = L * 0.18;                    // Ancho de fuselaje
            double S = L * 0.95;                    // Envergadura alar

            // Sombra cenital en el asfalto
            g2.setColor(new Color(0, 0, 0, 75));
            g2.fillOval(cx - (int)(S * 0.5), cy - (int)(L * 0.45), (int)S, (int)(L * 0.9));

            // 2. Alas simetricas con flecha cenital (Path2D.Double)
            Path2D.Double alas = new Path2D.Double();
            alas.moveTo(cx, cy - L * 0.10);
            alas.lineTo(cx + S * 0.50, cy + L * 0.12); // Wingtip derecho
            alas.lineTo(cx + S * 0.47, cy + L * 0.16);
            alas.lineTo(cx + W * 0.48, cy + L * 0.18);
            alas.lineTo(cx - W * 0.48, cy + L * 0.18);
            alas.lineTo(cx - S * 0.47, cy + L * 0.16);
            alas.lineTo(cx - S * 0.50, cy + L * 0.12); // Wingtip izquierdo
            alas.closePath();

            g2.setColor(Theme.getElevated());
            g2.fill(alas);
            g2.setColor(colorSemantico);
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(alas);

            // Motores turbofan bajo las alas
            g2.setColor(Theme.getBorder());
            g2.fill(new RoundRectangle2D.Double(cx - S * 0.24, cy - L * 0.02, W * 0.8, L * 0.22, 3, 3));
            g2.fill(new RoundRectangle2D.Double(cx + S * 0.16, cy - L * 0.02, W * 0.8, L * 0.22, 3, 3));

            // 3. Fuselaje cenital proporcional (Path2D.Double)
            Path2D.Double fuselaje = new Path2D.Double();
            fuselaje.moveTo(cx, cy - L * 0.50); // Nariz/Radomo
            fuselaje.curveTo(cx + W * 0.5, cy - L * 0.45, cx + W * 0.5, cy - L * 0.30, cx + W * 0.5, cy - L * 0.10);
            fuselaje.lineTo(cx + W * 0.5, cy + L * 0.28);
            fuselaje.curveTo(cx + W * 0.4, cy + L * 0.40, cx + W * 0.15, cy + L * 0.48, cx, cy + L * 0.52); // Cola APU
            fuselaje.curveTo(cx - W * 0.15, cy + L * 0.48, cx - W * 0.4, cy + L * 0.40, cx - W * 0.5, cy + L * 0.28);
            fuselaje.lineTo(cx - W * 0.5, cy - L * 0.10);
            fuselaje.curveTo(cx - W * 0.5, cy - L * 0.30, cx - W * 0.5, cy - L * 0.45, cx, cy - L * 0.50);
            fuselaje.closePath();

            GradientPaint gradFuselaje = new GradientPaint(
                    (float)(cx - W), (float)cy, Theme.getTexto(),
                    (float)(cx + W), (float)cy, Theme.getSurface());
            g2.setPaint(gradFuselaje);
            g2.fill(fuselaje);
            g2.setColor(Theme.getBorder());
            g2.setStroke(new BasicStroke(1.0f));
            g2.draw(fuselaje);

            // Cockpit
            g2.setColor(Theme.getBg());
            g2.fillArc(cx - (int)(W * 0.35), cy - (int)(L * 0.44), (int)(W * 0.7), (int)(L * 0.10), 0, 180);

            // 4. Estabilizadores de cola cenitales
            Path2D.Double estabilizador = new Path2D.Double();
            estabilizador.moveTo(cx, cy + L * 0.38);
            estabilizador.lineTo(cx + S * 0.22, cy + L * 0.48);
            estabilizador.lineTo(cx + S * 0.20, cy + L * 0.51);
            estabilizador.lineTo(cx - S * 0.20, cy + L * 0.51);
            estabilizador.lineTo(cx - S * 0.22, cy + L * 0.48);
            estabilizador.closePath();
            g2.setColor(colorSemantico);
            g2.fill(estabilizador);

            // 5. Luces y Balizas
            int alphaBeacon = (int) (120 + Math.sin(fasePulso) * 120);
            alphaBeacon = Math.max(20, Math.min(255, alphaBeacon));
            g2.setColor(new Color(239, 68, 68, alphaBeacon));
            g2.fillOval(cx - 2, cy - 2, 4, 4);

            if (Math.sin(fasePulso * 2.5) > 0.6) {
                g2.setColor(Color.WHITE);
                g2.fillOval(cx + (int)(S * 0.50) - 2, cy + (int)(L * 0.12) - 2, 4, 4);
                g2.fillOval(cx - (int)(S * 0.50) - 2, cy + (int)(L * 0.12) - 2, 4, 4);
            }
        }

        // Anillo de conflicto RadialGradientPaint + pulsacion animada con javax.swing.Timer (interpola radio/opacidad)
        private void dibujarZonaAlertaNeon(Graphics2D g2, double sx, double sy) {
            int cx = (int) (695 * sx);
            int cy = (int) (385 * sy);

            if (conflictoActivo) {
                float radioPulso = (float) (46.0 + Math.sin(fasePulso) * 18.0);
                float alphaCentro = (float) (0.42 + Math.sin(fasePulso) * 0.18);
                alphaCentro = Math.max(0.15f, Math.min(0.65f, alphaCentro));

                Point2D center = new Point2D.Float(cx, cy);
                float[] fractions = {0.0f, 0.55f, 1.0f};
                Color[] colors = {
                        new Color(248, 81, 73, (int) (alphaCentro * 255)),
                        new Color(248, 81, 73, (int) (alphaCentro * 0.35f * 255)),
                        new Color(248, 81, 73, 0)
                };
                RadialGradientPaint rgp = new RadialGradientPaint(center, Math.max(10f, radioPulso), fractions, colors);
                g2.setPaint(rgp);
                g2.fill(new Ellipse2D.Float(cx - radioPulso, cy - radioPulso, radioPulso * 2, radioPulso * 2));

                // Anillo perimetral discontinuo en expansion
                g2.setColor(new Color(248, 81, 73, (int)(alphaCentro * 255)));
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{6f, 4f}, desplazamientoGuia));
                g2.draw(new Ellipse2D.Float(cx - radioPulso, cy - radioPulso, radioPulso * 2, radioPulso * 2));
            } else {
                g2.setColor(Theme.getSuccessSoft());
                g2.fillOval(cx - 28, cy - 28, 56, 56);
                g2.setColor(Theme.getSuccess());
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawOval(cx - 28, cy - 28, 56, 56);
            }
        }

        // Linea de conexion vectorial con flujo animado de particulas
        private void dibujarLineaConexionVectorial(Graphics2D g2, double sx, double sy) {
            int xOrigen = (int) (695 * sx);
            int yOrigen = (int) (385 * sy);
            int xDestino = (int) (490 * sx);
            int yDestino = (int) (475 * sy);

            Color colorGuia = conflictoActivo ? Theme.getAdminAccent() : Theme.getSuccess();

            QuadCurve2D curva = new QuadCurve2D.Float(
                    xOrigen, yOrigen,
                    (float) ((xOrigen + xDestino) / 2.0f), (float) (yOrigen - 45 * sy),
                    xDestino, yDestino);

            g2.setColor(new Color(colorGuia.getRed(), colorGuia.getGreen(), colorGuia.getBlue(), 40));
            g2.setStroke(new BasicStroke(4.0f));
            g2.draw(curva);

            g2.setColor(colorGuia);
            g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{8, 6}, desplazamientoGuia));
            g2.draw(curva);

            g2.setColor(colorGuia);
            g2.fillOval(xDestino - 4, yDestino - 4, 8, 8);
        }

        // Gates RoundRectangle2D con etiqueta (01..12, B12) en MONO sobre chip oscuro
        private void dibujarMarcadoresNumerados(Graphics2D g2, double sx, double sy) {
            for (MarcadorPuerta m : marcadores) {
                int bx = (int) (m.xInsignia * sx);
                int by = (int) (m.yInsignia * sy);
                int ax = (int) (m.xAvion * sx);
                int ay = (int) (m.yAvion * sy);

                boolean esConflicto = m.esConflicto && conflictoActivo;
                Color colBadge = esConflicto ? Theme.getDanger() : (m.numero.equals("01") ? Theme.getSuccess() : Theme.getAdminAccent());

                // Linea de conexion
                g2.setColor(new Color(colBadge.getRed(), colBadge.getGreen(), colBadge.getBlue(), 120));
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawLine(bx, by + 12, ax, ay);

                // Gates RoundRectangle2D sobre chip oscuro
                RoundRectangle2D chipGate = new RoundRectangle2D.Float(bx - 18, by - 12, 36, 24, 8, 8);
                g2.setColor(Theme.getElevated());
                g2.fill(chipGate);
                g2.setColor(colBadge);
                g2.setStroke(new BasicStroke(1.4f));
                g2.draw(chipGate);

                // Etiqueta en IBM Plex Mono
                g2.setFont(FontManager.getIBMPlexMono(Font.BOLD, 10f));
                g2.setColor(Theme.getTexto());
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(m.numero);
                g2.drawString(m.numero, bx - (tw / 2), by + 5);
            }
        }

        // Tarjeta flotante interactiva de telemetria al pasar el raton (Hover HUD Tooltip)
        private void dibujarTarjetaTooltipHover(Graphics2D g2, int w, int h) {
            if (marcadorHover == null || posicionRaton.x < 0) return;

            int tx = posicionRaton.x + 18;
            int ty = posicionRaton.y + 12;
            int cardW = 270;
            int cardH = 110;

            if (tx + cardW > w - 10) tx = posicionRaton.x - cardW - 14;
            if (ty + cardH > h - 10) ty = posicionRaton.y - cardH - 10;

            boolean esAdmin = controlador.esSesionAdmin();
            boolean esConflicto = marcadorHover.esConflicto && conflictoActivo;
            Color colorBorde = esConflicto ? Theme.getDanger() : (marcadorHover.numero.equals("01") ? Theme.getSuccess() : Theme.getAdminAccent());

            // Sombra y fondo de tarjeta
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(tx + 4, ty + 4, cardW, cardH, 8, 8);
            g2.setColor(Theme.getSurface());
            g2.fillRoundRect(tx, ty, cardW, cardH, 8, 8);
            g2.setColor(colorBorde);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(tx, ty, cardW, cardH, 8, 8);

            // Encabezado
            g2.setFont(FontManager.getBarlowCondensed(Font.BOLD, 12f));
            g2.setColor(colorBorde);
            g2.drawString("[STAND] " + marcadorHover.nombreCompleto.toUpperCase(), tx + 10, ty + 18);

            // Datos de vuelo y estado
            g2.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 10f));
            g2.setColor(Theme.getTexto());
            g2.drawString("- Vuelo: " + marcadorHover.vuelo + " | Destino: " + marcadorHover.destino, tx + 12, ty + 38);
            g2.drawString("- Modelo: " + marcadorHover.modeloAvion + " - Comercial", tx + 12, ty + 54);

            g2.setColor(esConflicto ? Theme.getDanger() : Theme.getAdminAccent());
            g2.drawString("- Estado: " + (esConflicto ? "CONFLICTO DEMORA 45 MIN" : marcadorHover.estado), tx + 12, ty + 70);

            // Linea de control segun rol
            g2.setFont(FontManager.getIBMPlexSans(Font.BOLD, 9f));
            if (esAdmin) {
                g2.setColor(Theme.getSuccess());
                g2.drawString("ADMIN: Clic para gestionar stand o resolver conflicto", tx + 12, ty + 94);
            } else {
                g2.setColor(Theme.getInfo());
                g2.drawString("CONSULTA: Inspeccion en vivo - Admin para cambios", tx + 12, ty + 94);
            }
        }

        // Banner de alerta en esquina superior-derecha (SVG warning + danger-soft + texto danger legible)
        private void dibujarBannerAlerta(Graphics2D g2, int w) {
            int bannerW = 340;
            int bannerH = 34;
            int bannerX = w - bannerW - 14;
            int bannerY = 8;

            if (conflictoActivo) {
                g2.setColor(Theme.getDangerSoft());
                g2.fillRoundRect(bannerX, bannerY, bannerW, bannerH, 8, 8);
                g2.setColor(Theme.getDanger());
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(bannerX, bannerY, bannerW, bannerH, 8, 8);

                Icon icWarn = AppIcon.warning(16, Theme.getDanger());
                icWarn.paintIcon(PanelRadarSmartAirport.this, g2, bannerX + 10, bannerY + 9);

                g2.setFont(FontManager.getBarlowCondensed(Font.BOLD, 12f));
                g2.setColor(Theme.getDanger());
                g2.drawString("ALERTA ATC · CONFLICTO EN GATE B12", bannerX + 32, bannerY + 16);

                g2.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 10f));
                g2.setColor(Theme.getTexto());
                g2.drawString("Vuelo CP-2024 · Reasignar a Gate 01 vía Lista Circular", bannerX + 32, bannerY + 28);
            } else {
                g2.setColor(Theme.getSuccessSoft());
                g2.fillRoundRect(bannerX, bannerY, bannerW, bannerH, 8, 8);
                g2.setColor(Theme.getSuccess());
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(bannerX, bannerY, bannerW, bannerH, 8, 8);

                Icon icOk = AppIcon.check(16, Theme.getSuccess());
                icOk.paintIcon(PanelRadarSmartAirport.this, g2, bannerX + 10, bannerY + 9);

                g2.setFont(FontManager.getBarlowCondensed(Font.BOLD, 12f));
                g2.setColor(Theme.getSuccess());
                g2.drawString("OPERACIONES ATC DESPEJADAS · B12 RESUELTO", bannerX + 32, bannerY + 16);

                g2.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 10f));
                g2.setColor(Theme.getTexto());
                g2.drawString("CP-2024 reasignado a Stand Gate 01 · Flujo normal", bannerX + 32, bannerY + 28);
            }
        }

        // Leyenda (En puerta / Rodando / Conflicto) con colores claros
        private void dibujarLeyendaOperacional(Graphics2D g2) {
            int lx = 14;
            int ly = 8;
            int lw = 360;
            int lh = 34;

            g2.setColor(Theme.getSurface());
            g2.fillRoundRect(lx, ly, lw, lh, 8, 8);
            g2.setColor(Theme.getBorder());
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(lx, ly, lw, lh, 8, 8);

            // 1. En puerta (success)
            g2.setColor(Theme.getSuccess());
            g2.fillOval(lx + 10, ly + 12, 10, 10);
            g2.setFont(FontManager.getBarlowCondensed(Font.BOLD, 11f));
            g2.setColor(Theme.getTexto());
            g2.drawString("EN PUERTA", lx + 25, ly + 21);

            // 2. Rodando (gris-azulado)
            g2.setColor(new Color(0x58, 0xA6, 0xFF));
            g2.fillOval(lx + 105, ly + 12, 10, 10);
            g2.drawString("RODANDO / TAXI", lx + 120, ly + 21);

            // 3. Conflicto (danger)
            g2.setColor(Theme.getDanger());
            g2.fillOval(lx + 230, ly + 12, 10, 10);
            g2.drawString("CONFLICTO ATC", lx + 245, ly + 21);
        }

        private void dibujarRadarVectorialAOC(Graphics2D g2, int w, int h) {
            g2.setColor(new Color(15, 19, 27));
            g2.fillRect(0, 0, w, h);

            g2.setColor(new Color(25, 34, 48));
            for (int x = 0; x < w; x += 40) g2.drawLine(x, 0, x, h);
            for (int y = 0; y < h; y += 40) g2.drawLine(0, y, w, y);

            GeneralPath terminal = new GeneralPath();
            terminal.moveTo(w * 0.25, h * 0.15);
            terminal.lineTo(w * 0.75, h * 0.15);
            terminal.lineTo(w * 0.65, h * 0.45);
            terminal.lineTo(w * 0.35, h * 0.45);
            terminal.closePath();

            g2.setColor(new Color(20, 27, 39));
            g2.fill(terminal);
            g2.setColor(cyanNeon);
            g2.setStroke(new BasicStroke(1.8f));
            g2.draw(terminal);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.setColor(textoBlanco);
            g2.drawString("TERMINAL PRINCIPAL - RADAR VECTORIAL AOC", (int)(w * 0.32), (int)(h * 0.30));
        }
    }

    // Estructura auxiliar con datos y telemetria de cada stand
    private static class MarcadorPuerta {
        String numero;
        String nombreCompleto;
        int xInsignia, yInsignia;
        int xAvion, yAvion;
        boolean esConflicto;
        String vuelo;
        String destino;
        String modeloAvion;
        String estado;

        MarcadorPuerta(String numero, String nombreCompleto, int xInsignia, int yInsignia, int xAvion, int yAvion,
                       boolean esConflicto, String vuelo, String destino, String modeloAvion, String estado) {
            this.numero = numero;
            this.nombreCompleto = nombreCompleto;
            this.xInsignia = xInsignia;
            this.yInsignia = yInsignia;
            this.xAvion = xAvion;
            this.yAvion = yAvion;
            this.esConflicto = esConflicto;
            this.vuelo = vuelo;
            this.destino = destino;
            this.modeloAvion = modeloAvion;
            this.estado = estado;
        }
    }

    // Renderizador de estados de vuelo con colores neon
    private class RenderEstadoVuelo extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
            String texto = (value != null) ? value.toString().trim() : "";
            if (texto.isEmpty()) {
                lbl.setText("-");
            }

            if (texto.contains("CONFLICTO") || texto.contains("DEMORA")) {
                lbl.setForeground(rojoNeon);
            } else if (texto.contains("APROXIMACIÓN") || texto.contains("ABORDANDO")) {
                lbl.setForeground(doradoNeon);
            } else if (texto.contains("ASIGNADO") || texto.contains("DESPACHO")) {
                lbl.setForeground(verdeNeon);
            } else {
                lbl.setForeground(cyanNeon);
            }

            if (!isSelected) {
                lbl.setBackground(fondoEntrada);
            }
            return lbl;
        }
    }
}
