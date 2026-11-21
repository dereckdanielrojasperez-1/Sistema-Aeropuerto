package com.aeropuerto.vista;

import com.aeropuerto.controlador.ControladorAeropuerto;
import com.aeropuerto.estructuras.ListaCircular;
import com.aeropuerto.estructuras.ListaDoble;
import com.aeropuerto.estructuras.ListaSimple;
import com.aeropuerto.estructuras.NodoCircular;
import com.aeropuerto.estructuras.NodoDoble;
import com.aeropuerto.estructuras.NodoSimple;
import com.aeropuerto.modelo.Avion;
import com.aeropuerto.modelo.Equipaje;
import com.aeropuerto.modelo.PuertaEmbarque;
import com.aeropuerto.modelo.Ruta;
import com.aeropuerto.modelo.Usuario;
import com.aeropuerto.modelo.Vuelo;
import com.aeropuerto.util.ConexionBD;
import com.aeropuerto.estructuras.grafo.Vertice;
import com.aeropuerto.estructuras.grafo.Arista;
import com.aeropuerto.estructuras.grafo.ResultadoMST;
import com.aeropuerto.estructuras.grafo.ResultadoFloydWarshall;
import com.aeropuerto.estructuras.arbol.ArbolAVL;
import com.aeropuerto.estructuras.arbol.ArbolBinarioBusqueda;
import com.aeropuerto.estructuras.hash.TablaHash;
import com.aeropuerto.estructuras.heap.MonticuloBinario;
import com.aeropuerto.estructuras.ordenamiento.AlgoritmosOrdenamientoAvanzado;
import com.aeropuerto.servicio.ServicioAeropuerto;
import com.aeropuerto.servicio.ServicioCorreo;
import java.io.File;
import java.util.List;

// HUMANIZACION UI - Cimientos y componentes modulares
import com.aeropuerto.vista.tema.Theme;
import com.aeropuerto.vista.tema.FontManager;
import com.aeropuerto.vista.tema.BotonPrimario;
import com.aeropuerto.vista.tema.AppIcon;
import com.aeropuerto.vista.tema.StepperPasajero;
import com.aeropuerto.vista.tema.PanelDashboardKpi;
import com.aeropuerto.vista.tema.RenderersTablaVuelos;
import com.aeropuerto.vista.tema.BarraEstadoFooter;
import com.aeropuerto.vista.tema.TarjetaTurnoVuelo;
import com.aeropuerto.vista.tema.StripVueloSeleccionado;
import com.aeropuerto.vista.tema.GlassPanel;
import com.aeropuerto.vista.tema.BotonAsientoCabina;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

// Interfaz interactiva y dinamica para control aeroportuario
public class VentanaPrincipal extends JFrame {
    private static final long serialVersionUID = 1L;

    // Controlador y estructuras de datos
    private ControladorAeropuerto controlador;
    private ListaSimple listaEquipaje;
    private ListaDoble listaRutas;
    private ListaCircular listaPuertas;

    // Paleta de colores Smart Airport (Aeronáutica Dinámica)
    private Color azulHeader = new Color(10, 13, 18);
    private Color azulSecundario = new Color(14, 18, 26);
    private Color azulAccion = new Color(245, 158, 11); // Dorado Smart Airport
    private Color verdeLibre = new Color(16, 185, 129); // Verde Esmeralda
    private Color rojoOcupado = new Color(239, 68, 68); // Rojo Alerta
    private Color rojoBoton = new Color(220, 38, 38);
    private Color fondoGris = new Color(11, 14, 20); // Fondo general oscuro
    private Color doradoAlerta = new Color(251, 191, 36);
    private Color fondoTarjeta = new Color(19, 25, 36);
    private Color fondoEntrada = new Color(15, 20, 29);
    private Color bordeTarjeta = new Color(34, 44, 63);
    private Color textoBlanco = new Color(248, 250, 252);
    private Color textoGris = new Color(148, 163, 184);
    private Color azulCyan = new Color(56, 189, 248);

    private void refrescarPaletaLocal() {
        boolean oscuro = Theme.esModoOscuro();
        fondoGris = Theme.getBg();
        fondoTarjeta = Theme.getSurface();
        fondoEntrada = Theme.getInput();
        bordeTarjeta = Theme.getBorder();
        textoBlanco = Theme.getTexto();
        textoGris = Theme.getSecundario();
        azulHeader = Theme.getSurface();
        azulSecundario = Theme.getElevated();
        azulCyan = oscuro ? new Color(56, 189, 248) : new Color(9, 105, 218);
        doradoAlerta = oscuro ? new Color(251, 191, 36) : new Color(180, 83, 9);
    }

    // Componentes Radar y Control de Supervisión
    private PanelRadarSmartAirport panelRadarSmartAirport;
    private PanelControlSupervisoresAeropuertos panelSupervisoresAeropuertos;
    private JComponent panelLaboratorioAvanzado;

    // Componentes del Dashboard de Metricas (KPIs)
    private JLabel lblKpiVuelos;
    private JLabel lblKpiOcupacion;
    private JProgressBar barraKpiOcupacion;
    private JLabel lblKpiEquipaje;
    private JLabel lblKpiPuerta;

    // Componentes de vuelos (Arreglos)
    private JTextField txtCodigo;
    private JComboBox<String> cbOrigen;
    private JComboBox<String> cbDestino;
    private JSpinner spinnerFecha;
    private JComboBox<String> cbHoraSalida;
    private JTextField txtHoraLlegada;
    private JTextField txtDuracion;
    private JTextField txtPrecioBase;
    private JComboBox<String> cbClase;
    private JTable tablaVuelos;
    private DefaultTableModel modeloTablaVuelos;
    private JLabel lblContadorArreglo;
    private JTextField txtFiltroVuelos;

    // Simulador dinámico de radar/vuelos
    private Timer temporizadorSimulador;
    private boolean simuladorActivo = false;
    private JButton btnAlternarSimulador;
    private String[] estadosVueloRotativos = {"A Tiempo", "Abordando", "En Pista", "En Vuelo", "Aterrizado"};

    // Componentes de cabina (Matriz 10x5)
    private JButton[][] botonesAsientos;
    private JTextField txtFilaSeleccionada;
    private JTextField txtColSeleccionada;
    private JTextField txtDniAsiento;
    private JLabel lblCostoCalculado;
    private JLabel lblZonaAsiento;
    private JLabel lblResumenOcupacion;
    private JLabel lblVueloActivoCabina;
    private JTabbedPane panelPestanas;

    // Pase de abordar interactivo (Boarding Pass Preview)
    private JLabel lblPasePasajero;
    private JLabel lblPaseVueloRuta;
    private JLabel lblPaseAsientoZona;
    private JLabel lblPaseTarifaClase;

    // Componentes de equipaje (Lista Simple)
    private JTextField txtListaEquipajeId;
    private JTextField txtListaEquipajeDni;
    private JComboBox<String> cbListaEquipajeVuelo;
    private JTextField txtListaEquipajePeso;
    private JComboBox<String> cbListaEquipajeTipo;
    private JTextField txtListaEquipajeNuevoPeso;
    private JComboBox<String> cbListaEquipajeNuevoTipo;
    private JTextArea txtResultadoEquipaje;
    private JLabel lblTamanoMaletas;
    private JProgressBar barraPesoEquipaje;
    private JLabel lblAlertaSobrepeso;

    // Componentes de rutas (Lista Doble)
    private JTextField txtListaRutaId;
    private JComboBox<String> cbListaRutaOrigen;
    private JComboBox<String> cbListaRutaDestino;
    private JTextField txtListaRutaDistancia;
    private JTextArea txtResultadoRutas;
    private JLabel lblTamanoRutas;

    // Navegador interactivo visual de Lista Doble
    private NodoDoble cursorRutaNavegacion;
    private JLabel lblRutaIataOrigen;
    private JLabel lblRutaIataDestino;
    private JLabel lblRutaDistanciaKm;
    private JLabel lblRutaPunteros;

    // Componentes de puertas de embarque (Lista Circular)
    private JTextArea txtResultadoPuertas;
    private JLabel lblPuertaActualInfo;
    private JButton btnAvanzarTurnoPuerta;
    private JButton btnAsignarVueloPuerta;
    private JComboBox<String> cbVueloAsignarPuerta;
    private JPanel panelVisualGates;

    // Consola y estado general
    private JTextArea txtConsola;
    private JLabel lblEstadoBD;
    private JLabel lblReloj;

    // Componentes de sesion y control de acceso
    private JLabel lblEstadoSesion;
    private JButton btnSesionAdmin;
    private JLabel lblAdminUsuarioActivo;
    private JLabel lblAdminRolActivo;
    private JLabel lblAdminNombreActivo;
    private JLabel lblSqlVuelos;
    private JLabel lblSqlAsientos;
    private JLabel lblSqlEquipaje;
    private JLabel lblSqlRutas;
    private JLabel lblSqlPuertas;
    private JLabel lblSqlUsuarios;
    private JLabel lblSqlAeropuertos;
    private JLabel lblSqlSupervisores;
    private JLabel lblSqlEstadoServidor;

    // Modulos de la interfaz de Administrador
    private JPanel panelModuloVuelos;
    private JPanel panelModuloCabina;
    private JSplitPane panelDivisionPrincipal;
    private JPanel panelGestionEquipaje;
    private JPanel panelGestionRutas;
    private JSplitPane panelDivisionListas;
    private JPanel panelModuloPuertas;
    private JPanel panelModuloAdministracion;
    private JLabel lblFrenteColaPista;

    // Modulos de la interfaz de Pasajero
    private JPanel panelPasajeroVuelos;
    private JPanel panelPasajeroCabinaContenedor;
    private JPanel panelPasajeroEquipajeContenedor;
    private JPanel panelPasajeroPaseAbordar;
    private JTable tablaVuelosPasajero;
    private DefaultTableModel modeloTablaVuelosPasajero;
    private JComboBox<String> cbFiltroDestinoPasajero;
    private JComboBox<String> cbFiltroTurnoPasajero;
    private JComboBox<String> cbFiltroClasePasajero;
    private JPanel panelTarjetasVariaciones;
    private JLabel lblBannerVueloSeleccionado;
    private JButton[] btnsDestinosLaterales;

    // HUMANIZACION UI - Componentes modulares Bloque 2
    private StepperPasajero stepperPasajero;
    private PanelDashboardKpi panelDashboardKpi;
    private StripVueloSeleccionado stripVueloSeleccionado;
    private BarraEstadoFooter barraEstadoFooter;
    private JButton btnSincronizarTodo;
    private JButton btnRecargarTodo;

    // Componentes interactivos de Cabina Pasajero
    private JButton[][] botonesAsientosPasajero = new JButton[10][5];
    private JLabel lblCabinaVueloActivoInfo;
    private JLabel lblCabinaResumenLibres;
    private JLabel lblDetalleAsientoPasajero;
    private JLabel lblDetalleClasePasajero;
    private JLabel lblDetalleBasePasajero;
    private JLabel lblDetalleSuplementoPasajero;
    private JLabel lblDetalleTotalPasajero;
    private JTextField txtDniPasajeroReserva;
    private JTextField txtNombrePasajeroReserva;
    private JTextField txtEmailPasajeroReserva;
    private int pasajeroFilaSel = -1;
    private int pasajeroColSel = -1;
    private int pasajeroFilaConfirmada = -1;
    private int pasajeroColConfirmada = -1;
    private JButton btnConfirmarAsientoPasajero;

    // Componentes de Modo Limpio / Tecnico y validacion inline
    private boolean vistaTecnicaActiva = false;
    private JButton btnToggleVistaTecnica;
    private JButton btnToggleTablaDetallada;
    private JLabel lblValidacionDni;
    private Timer toastTimer;
    private JLabel lblToast;

    // Paneles integrados de Estructuras Avanzadas (Semana 18)
    private JPanel panelArbolesTab;
    private JPanel panelGrafoTab;
    private JPanel panelHashHeapTab;
    private JPanel panelBenchTab;

    // Componentes de Equipaje Pasajero
    private JComboBox<String> cbTipoEquipajePasajero;
    private JTextField txtPesoEquipajePasajero;
    private JLabel lblAlertaIataEquipaje;
    private JLabel lblInfoPasajeroVueloEquipaje;
    private JTable tablaEquipajePasajero;
    private DefaultTableModel modeloTablaEquipajePasajero;

    // Elementos del pase de abordar del pasajero
    private JLabel lblPaseNombrePasajero;
    private JLabel lblPaseCodigoVuelo;
    private JLabel lblPaseRutaVuelo;
    private JLabel lblPaseHorario;
    private JLabel lblPaseAsientoDetalle;
    private JLabel lblPasePuertaDetalle;
    private JLabel lblPaseEquipajeDetalle;
    private JLabel lblPaseLocalizador;
    private JTextField txtEmailConfirmacionPase;
    private JLabel lblEstadoEnvioCorreoPase;
    private JButton btnAbrirVoucherHtml;
    private String ultimaRutaVoucherHtml;

    // Elementos de la cabecera
    private JLabel lblInsigniaHeader;
    private JLabel lblTituloPrincipalHeader;
    private JLabel lblTagAocHeader;
    private JLabel lblSubtituloHeader;
    private JPanel panelRaiz;
    private JPanel panelNorte;
    private JPanel panelEncabezado;
    private JButton btnAlternarTemaHeader;
    private JPanel badgeNorm;
    private JPanel badgeRain;
    private JLabel dotNorm;
    private JLabel txtNorm;
    private JLabel dotRain;
    private JLabel txtRain;

    // Componentes de Vuelos Pasajero
    private JPanel pNortePasajeroVuelos;
    private JLabel lblTitPasajeroVuelos;
    private JLabel lblSubPasajeroVuelos;
    private JPanel pFiltrosPasajero;
    private JLabel lblFiltroDestPasajero;
    private JLabel lblFiltroTurnoPasajero;
    private JLabel lblFiltroClasePasajero;
    private JScrollPane scrollTablaPasajero;
    private JButton btnFichaAvionPasajeroVuelos;
    private JButton btnVerRutaPasajeroVuelos;
    private JButton btnContinuarAsientoPasajeroVuelos;
    private JLabel lblResumenCodigoVuelo;
    private JLabel lblResumenRuta;
    private JLabel lblResumenHorario;
    private JLabel lblResumenTarifa;
    private JLabel lblResumenAsientos;
    private boolean modoVistaTablaVuelos = false;

    // Componentes de Cabina Pasajero
    private JPanel panelCabinaPasajeroRaiz;
    private JPanel pEncabezadoCabinaPasajero;
    private JPanel pFuselajeContenedor;
    private JScrollPane scrollFuselaje;
    private JPanel pLateralCabinaPasajero;
    private JLabel lblTitLatCabina;
    private JLabel lblCockpitCabina;
    private JLabel lblVipBannerCabina;
    private JLabel lblTuristaBannerCabina;
    private JComboBox<String> cbClaseCabinaPasajero;
    private JButton btnAutoAsignarPasajero;
    private JButton btnFichaAvionPasajero;
    private JButton btnVolverVuelosCabina;
    private JButton btnContinuarEquipajeCabina;

    // Componentes de Equipaje Pasajero
    private JLabel lblTitEquipajePasajero;
    private JLabel lblSubEquipajePasajero;
    private JPanel pFormularioEquipajePasajero;
    private JScrollPane scrollTablaEquipajePasajero;
    private JButton btnVolverCabinaEquipaje;
    private JButton btnContinuarPaseEquipaje;

    // Componentes de Pase de Abordar Pasajero
    private JLabel lblTitPasePasajero;
    private JLabel lblSubPasePasajero;
    private JPanel pTarjetaBoleto;
    private JPanel pDatosBoleto;
    private JPanel pMonitorSalas;
    private JLabel lblTitMonitorSalas;
    private JLabel lblDescMonitorSalas;
    private JButton btnVolverEquipajePase;
    private JButton btnNuevaReservaPase;

    // Scroll panes adicionales para actualización de tema
    private JScrollPane scrollTablaVuelos;

    public VentanaPrincipal() {
        this.controlador = new ControladorAeropuerto();
        this.botonesAsientos = new JButton[10][5];
        this.listaEquipaje = controlador.obtenerListaEquipaje();
        this.listaRutas = controlador.obtenerListaRutas();
        this.listaPuertas = controlador.obtenerListaPuertas();

        refrescarPaletaLocal();
        configurarIconoVentana();
        inicializarVentana();
        actualizarTablaVuelos();
        actualizarCombosVuelos();
        actualizarMatrizCabina();
        actualizarEtiquetaVueloActivo();
        actualizarContadorMaletas();
        actualizarContadorRutas();
        actualizarNavegadorRutas();
        actualizarVistaPuertas();
        actualizarMetricasDashboard();
        actualizarEstadoSesionUI();
        actualizarMetricasSQLTab();
        iniciarReloj();
        iniciarSimuladorDinamico();
        recalcularHorarioYTarifa();
        setLocationRelativeTo(null);
    }

    public ControladorAeropuerto obtenerControlador() {
        return controlador;
    }

    private void configurarIconoVentana() {
        try {
            java.net.URL urlLogo = getClass().getResource("/com/aeropuerto/vista/logo_corpac.png");
            if (urlLogo == null) urlLogo = getClass().getResource("logo_corpac.png");
            if (urlLogo != null) {
                setIconImage(new ImageIcon(urlLogo).getImage());
            } else {
                java.io.File f = new java.io.File("src/com/aeropuerto/vista/logo_corpac.png");
                if (f.exists()) setIconImage(new ImageIcon(f.getAbsolutePath()).getImage());
            }
        } catch (Exception e) {}
    }

    private ImageIcon obtenerIconoEmpresa(int ancho, int alto) {
        try {
            java.net.URL urlLogo = getClass().getResource("/com/aeropuerto/vista/logo_corpac.png");
            if (urlLogo == null) urlLogo = getClass().getResource("logo_corpac.png");
            Image img = null;
            if (urlLogo != null) {
                img = new ImageIcon(urlLogo).getImage();
            } else {
                java.io.File f = new java.io.File("src/com/aeropuerto/vista/logo_corpac.png");
                if (f.exists()) {
                    img = new ImageIcon(f.getAbsolutePath()).getImage();
                }
            }
            if (img != null) {
                return new ImageIcon(img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH));
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void inicializarVentana() {
        setTitle("Sistema de Aeropuerto - Centro de Control CORPAC (AOC)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int anchoVentana = Math.min(1280, Math.max(1024, (int) (screenSize.width * 0.94)));
        int altoVentana = Math.min(740, Math.max(580, (int) (screenSize.height * 0.88)));
        setSize(anchoVentana, altoVentana);
        setMinimumSize(new Dimension(980, 560));
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        this.panelRaiz = new JPanel(new BorderLayout(0, 0)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                int w = getWidth();
                int h = getHeight();
                if (w > 0 && h > 0) {
                    if (Theme.esModoOscuro()) {
                        // Gradiente sutil slate #0D1117 -> #161B22
                        Color c1 = new Color(0x0D, 0x11, 0x17);
                        Color c2 = new Color(0x16, 0x1B, 0x22);
                        java.awt.GradientPaint gp = new java.awt.GradientPaint(0, 0, c1, 0, h, c2);
                        g2.setPaint(gp);
                        g2.fillRect(0, 0, w, h);

                        // Glow suave en la esquina con el acento del rol activo
                        Color glowColor = Theme.esRolAdmin()
                                ? new Color(0xE3, 0xB3, 0x41, 14)
                                : new Color(0x58, 0xA6, 0xFF, 14);
                        java.awt.RadialGradientPaint rgp = new java.awt.RadialGradientPaint(
                                new java.awt.geom.Point2D.Float(w * 0.85f, 25f),
                                Math.max(w, h) * 0.65f,
                                new float[]{0.0f, 1.0f},
                                new Color[]{glowColor, new Color(0, 0, 0, 0)}
                        );
                        g2.setPaint(rgp);
                        g2.fillRect(0, 0, w, h);
                    } else {
                        Color c1 = new Color(0xF4, 0xF6, 0xF8);
                        Color c2 = new Color(0xEE, 0xF1, 0xF4);
                        java.awt.GradientPaint gp = new java.awt.GradientPaint(0, 0, c1, 0, h, c2);
                        g2.setPaint(gp);
                        g2.fillRect(0, 0, w, h);
                    }
                }
                g2.dispose();
            }
        };
        panelRaiz.setBackground(Theme.getBg());

        this.panelPestanas = new JTabbedPane();
        panelPestanas.setUI(new UIEstiloPestanasSmartAirport());
        panelPestanas.setFont(FontManager.getBarlowCondensed(Font.BOLD, 12f));
        panelPestanas.setBackground(Theme.getBg());
        panelPestanas.setForeground(Theme.getTexto());
        panelPestanas.setBorder(null);

        Theme.addThemeListener(this::actualizarTemaGlobal);

        // HUMANIZACION UI - Bloque Superior: Encabezado institucional + Dashboard 4 KPIs + Stepper 4 pasos
        this.panelNorte = new JPanel(new BorderLayout(0, 0));
        panelNorte.setBackground(Theme.getBg());
        panelNorte.add(crearPanelEncabezado(), BorderLayout.NORTH);

        this.panelDashboardKpi = new PanelDashboardKpi();
        panelNorte.add(this.panelDashboardKpi, BorderLayout.CENTER);

        this.stepperPasajero = new StepperPasajero();
        this.stepperPasajero.conectarConTabbedPane(panelPestanas);
        panelPestanas.addChangeListener(e -> {
            int idx = panelPestanas.getSelectedIndex();
            if (!Theme.esRolAdmin()) {
                // Guarda 1: Una vez confirmada la reserva, el pasajero no puede regresar a cambiar de vuelo (Paso 1)
                if (pasajeroFilaConfirmada >= 0 && idx == 0) {
                    SwingUtilities.invokeLater(() -> {
                        panelPestanas.setSelectedIndex(2);
                        mostrarToast("Su reserva ya está confirmada. No puede cambiar de vuelo en esta etapa.", Theme.getWarning());
                    });
                    return;
                }

                // Guarda 2: tabs 2 (Equipaje) y 3 (Pase) exigen asiento confirmado y datos de pasajero
                if (idx >= 2) {
                    String dni = (txtDniPasajeroReserva != null) ? txtDniPasajeroReserva.getText().trim() : "";
                    String nom = (txtNombrePasajeroReserva != null) ? txtNombrePasajeroReserva.getText().trim() : "";
                    if (pasajeroFilaConfirmada < 0 || dni.length() != 8 || nom.isEmpty()) {
                        SwingUtilities.invokeLater(() -> {
                            panelPestanas.setSelectedIndex(1);
                            mostrarToast("Complete la selección de asiento y datos de pasajero antes de avanzar", Theme.getWarning());
                            if (txtDniPasajeroReserva != null && (dni.isEmpty() || dni.length() != 8)) {
                                txtDniPasajeroReserva.requestFocus();
                            } else if (txtNombrePasajeroReserva != null) {
                                txtNombrePasajeroReserva.requestFocus();
                            }
                        });
                        return;
                    }
                }
                if (stepperPasajero != null) {
                    stepperPasajero.setPasoActivo(idx);
                }
                if (idx == 0) {
                    actualizarTablaVuelosPasajero();
                } else if (idx == 1) {
                    actualizarCabinaPasajero();
                } else if (idx == 2) {
                    actualizarPanelEquipajePasajero();
                } else if (idx == 3) {
                    actualizarPaseAbordarPasajero();
                }
            }
        });
        panelNorte.add(this.stepperPasajero, BorderLayout.SOUTH);

        panelRaiz.add(panelNorte, BorderLayout.NORTH);

        // Pestaña 1: Radar de Plataforma Smart Airport (AOC)
        this.panelRadarSmartAirport = new PanelRadarSmartAirport(controlador, this);

        // Pestaña 2: Supervisión y Control de Aeropuertos Libres
        this.panelSupervisoresAeropuertos = new PanelControlSupervisoresAeropuertos(controlador, this);

        // Inicializar modulos compartidos y de administracion
        this.panelModuloVuelos = crearPanelModuloVuelos();
        this.panelModuloCabina = crearPanelModuloCabina();
        this.panelModuloVuelos.setMinimumSize(new Dimension(480, 360));
        this.panelModuloCabina.setMinimumSize(new Dimension(440, 360));
        this.panelDivisionPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelModuloVuelos, panelModuloCabina);
        this.panelDivisionPrincipal.setDividerLocation(640);
        this.panelDivisionPrincipal.setResizeWeight(0.5);
        this.panelDivisionPrincipal.setBorder(null);

        this.panelGestionEquipaje = crearPanelGestionEquipaje();
        this.panelGestionRutas = crearPanelGestionRutas();
        this.panelDivisionListas = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelGestionEquipaje, panelGestionRutas);
        this.panelDivisionListas.setDividerLocation(660);
        this.panelDivisionListas.setResizeWeight(0.5);
        this.panelDivisionListas.setBorder(null);

        this.panelModuloPuertas = crearPanelModuloPuertas();
        this.panelModuloAdministracion = crearPanelModuloAdministracion();

        // Inicializar modulos especificos de pasajero
        this.panelPasajeroVuelos = crearPanelPasajeroVuelos();
        this.panelPasajeroCabinaContenedor = crearPanelPasajeroCabinaContenedor();
        this.panelPasajeroEquipajeContenedor = crearPanelPasajeroEquipajeContenedor();
        this.panelPasajeroPaseAbordar = crearPanelPasajeroPaseAbordar();

        // Configurar vista inicial segun rol
        configurarModoVista(controlador.esSesionAdmin());

        panelRaiz.add(panelPestanas, BorderLayout.CENTER);
        panelRaiz.add(crearPanelInferior(), BorderLayout.SOUTH);

        setContentPane(panelRaiz);
    }

    // Clase de estilo visual para las pestanas de navegacion (JTabbedPane)
    private static class UIEstiloPestanasSmartAirport extends BasicTabbedPaneUI {
        private int indicePestanaHover = -1;
        private java.awt.event.MouseMotionListener oyenteMovimientoRaton;
        private java.awt.event.MouseListener oyenteRaton;

        @Override
        protected void installDefaults() {
            super.installDefaults();
            tabInsets = new Insets(8, 14, 8, 14);
            selectedTabPadInsets = new Insets(0, 0, 0, 0);
            contentBorderInsets = new Insets(0, 0, 0, 0);
        }

        @Override
        protected void installListeners() {
            super.installListeners();
            if (tabPane == null) return;
            oyenteMovimientoRaton = new java.awt.event.MouseMotionAdapter() {
                @Override
                public void mouseMoved(java.awt.event.MouseEvent e) {
                    if (tabPane == null || !tabPane.isDisplayable()) return;
                    int previo = indicePestanaHover;
                    int encontrado = -1;
                    try {
                        Point pt = e.getPoint();
                        int cantidad = tabPane.getTabCount();
                        for (int i = 0; i < cantidad; i++) {
                            Rectangle r = getTabBounds(tabPane, i);
                            if (r != null && r.contains(pt)) {
                                encontrado = i;
                                break;
                            }
                        }
                    } catch (Throwable ignored) {
                        encontrado = -1;
                    }
                    indicePestanaHover = encontrado;
                    if (previo != indicePestanaHover && tabPane != null) {
                        tabPane.repaint();
                    }
                }
            };
            oyenteRaton = new java.awt.event.MouseAdapter() {
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    indicePestanaHover = -1;
                    if (tabPane != null) {
                        tabPane.repaint();
                    }
                }
            };
            tabPane.addMouseMotionListener(oyenteMovimientoRaton);
            tabPane.addMouseListener(oyenteRaton);
        }

        @Override
        protected void uninstallListeners() {
            if (tabPane != null) {
                if (oyenteMovimientoRaton != null) {
                    tabPane.removeMouseMotionListener(oyenteMovimientoRaton);
                }
                if (oyenteRaton != null) {
                    tabPane.removeMouseListener(oyenteRaton);
                }
            }
            oyenteMovimientoRaton = null;
            oyenteRaton = null;
            super.uninstallListeners();
        }

        @Override
        protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int maxTabHeight) {
            if (!Theme.esRolAdmin()) {
                // En modo Pasajero, el Stepper 4 pasos gestiona visualmente el flujo sin cajas naranjas uniformes
                return 0;
            }
            return super.calculateTabAreaHeight(tabPlacement, horizRunCount, maxTabHeight);
        }

        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
            if (!Theme.esRolAdmin()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            boolean estaHover = (indicePestanaHover == tabIndex);
            int radioTab = 10;
            int insetY = 2;
            int tabH = h - (insetY * 2);

            if (isSelected) {
                // Píldora de vidrio activo con borde dorado CORPAC
                Color softGold = Theme.getAdminSoft();
                g2.setColor(new Color(softGold.getRed(), softGold.getGreen(), softGold.getBlue(), 215));
                g2.fillRoundRect(x + 2, y + insetY, w - 4, tabH, radioTab, radioTab);
                g2.setColor(Theme.getAdminAccent());
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(x + 2, y + insetY, w - 4, tabH, radioTab, radioTab);
                // Highlight de vidrio superior
                g2.setColor(new Color(255, 255, 255, 45));
                g2.drawLine(x + 6, y + insetY + 1, x + w - 6, y + insetY + 1);
            } else if (estaHover) {
                Color elev = Theme.getElevated();
                g2.setColor(new Color(elev.getRed(), elev.getGreen(), elev.getBlue(), 195));
                g2.fillRoundRect(x + 2, y + insetY, w - 4, tabH, radioTab, radioTab);
                g2.setColor(Theme.getBorder());
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(x + 2, y + insetY, w - 4, tabH, radioTab, radioTab);
            } else {
                Color surf = Theme.getSurface();
                g2.setColor(new Color(surf.getRed(), surf.getGreen(), surf.getBlue(), 170));
                g2.fillRoundRect(x + 2, y + insetY, w - 4, tabH, radioTab, radioTab);
                g2.setColor(Theme.getBorder());
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(x + 2, y + insetY, w - 4, tabH, radioTab, radioTab);
            }
            g2.dispose();
        }

        @Override
        protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
            // El borde ya se dibuja estilizado con radio redondeado en paintTabBackground
        }

        @Override
        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            // Sin bordes toscos de contenido
        }

        @Override
        protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
            // Sin bordes punteados de foco
        }

        @Override
        protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
            if (!Theme.esRolAdmin()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g2.setFont(FontManager.getBarlowCondensed(Font.BOLD, 12f));

            boolean estaHover = (indicePestanaHover == tabIndex);
            if (isSelected) {
                g2.setColor(Theme.getAdminAccent());
            } else if (estaHover) {
                g2.setColor(Theme.getTexto());
            } else {
                g2.setColor(Theme.getSecundario());
            }
            g2.drawString(title, textRect.x, textRect.y + metrics.getAscent());
            g2.dispose();
        }
    }

    // Métodos utilitarios de estilización modo oscuro para campos, botones y bordes
    private void estilizarCampoTexto(JTextField tf) {
        tf.setBackground(fondoEntrada);
        tf.setForeground(textoBlanco);
        tf.setCaretColor(azulCyan);
        tf.setBorder(new CompoundBorder(new LineBorder(bordeTarjeta, 1), new EmptyBorder(3, 6, 3, 6)));
    }

    private void estilizarCombo(JComboBox<?> cb) {
        cb.setBackground(fondoEntrada);
        cb.setForeground(textoBlanco);
        cb.setBorder(new LineBorder(bordeTarjeta, 1));
    }

    private void estilizarBotonAccion(JButton btn, Color fondo, Color texto) {
        btn.setBackground(fondo);
        btn.setForeground(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(new LineBorder(bordeTarjeta, 1), new EmptyBorder(4, 8, 4, 8)));
    }

    private JLabel crearEtiquetaFormulario(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(textoGris);
        Dimension pref = l.getPreferredSize();
        l.setMinimumSize(new Dimension(pref.width, Math.max(pref.height, 20)));
        return l;
    }

    private TitledBorder crearBordeTituloDark(String titulo) {
        return BorderFactory.createTitledBorder(
                new LineBorder(bordeTarjeta, 1),
                titulo, TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 11), azulAccion);
    }

    private JPanel crearPanelEncabezado() {
        this.panelEncabezado = new JPanel(new BorderLayout(12, 0));
        this.panelEncabezado.setBackground(Theme.getSurface());
        this.panelEncabezado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.getBorder()),
                BorderFactory.createEmptyBorder(4, 14, 4, 14)));

        JPanel panelIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelIzquierdo.setOpaque(false);

        // Insignia y titulos dinamicos segun rol (Logo CORPAC preservado)
        lblInsigniaHeader = new JLabel();
        lblInsigniaHeader.setIcon(obtenerIconoEmpresa(95, 24));

        lblTituloPrincipalHeader = new JLabel("PORTAL DEL PASAJERO");
        lblTituloPrincipalHeader.setFont(FontManager.getBarlowCondensed(Font.BOLD, 17f));
        lblTituloPrincipalHeader.setForeground(Theme.getTexto());

        lblTagAocHeader = new JLabel(" CHECK-IN ");
        lblTagAocHeader.setFont(FontManager.getBarlowCondensed(Font.BOLD, 10f));
        lblTagAocHeader.setOpaque(true);
        lblTagAocHeader.setBackground(Theme.getPasajeroSoft());
        lblTagAocHeader.setForeground(Theme.getPasajeroAccent());
        lblTagAocHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getPasajeroAccent(), 1),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));

        lblSubtituloHeader = new JLabel(" | CORPAC S.A.");
        lblSubtituloHeader.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
        lblSubtituloHeader.setForeground(Theme.getSecundario());

        panelIzquierdo.add(lblInsigniaHeader);
        panelIzquierdo.add(lblTituloPrincipalHeader);
        panelIzquierdo.add(lblTagAocHeader);
        panelIzquierdo.add(lblSubtituloHeader);
        panelEncabezado.add(panelIzquierdo, BorderLayout.WEST);

        JPanel panelSesionCentro = new JPanel();
        panelSesionCentro.setLayout(new BoxLayout(panelSesionCentro, BoxLayout.X_AXIS));
        panelSesionCentro.setOpaque(false);

        lblEstadoSesion = new JLabel(" Modo: Pasajero / Cliente ");
        lblEstadoSesion.setFont(FontManager.getIBMPlexSans(Font.BOLD, 11f));
        lblEstadoSesion.setForeground(Theme.getTexto());
        lblEstadoSesion.setOpaque(true);
        lblEstadoSesion.setBackground(Theme.getInput());
        lblEstadoSesion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getBorder(), 1),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)));

        btnSesionAdmin = new JButton("Acceso Administrador");
        btnSesionAdmin.setFont(FontManager.getIBMPlexSans(Font.BOLD, 11f));
        btnSesionAdmin.setBackground(Theme.getPasajeroSoft());
        btnSesionAdmin.setForeground(Theme.getPasajeroAccent());
        btnSesionAdmin.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getPasajeroAccent(), 1),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)));
        btnSesionAdmin.setFocusPainted(false);
        btnSesionAdmin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSesionAdmin.addActionListener(e -> alternarSesionAdmin());

        btnAlternarTemaHeader = new JButton(Theme.esModoOscuro() ? "Modo Claro" : "Modo Oscuro");
        btnAlternarTemaHeader.setFont(FontManager.getIBMPlexSans(Font.BOLD, 11f));
        btnAlternarTemaHeader.setBackground(Theme.getElevated());
        btnAlternarTemaHeader.setForeground(Theme.getTexto());
        btnAlternarTemaHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getBorder(), 1),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        btnAlternarTemaHeader.setFocusPainted(false);
        btnAlternarTemaHeader.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAlternarTemaHeader.setIcon(AppIcon.of(Theme.esModoOscuro() ? "sun" : "moon", 12, Theme.getTexto()));
        btnAlternarTemaHeader.addActionListener(e -> Theme.toggleTheme());

        btnToggleVistaTecnica = new JButton("Vista Técnica");
        btnToggleVistaTecnica.setFont(FontManager.getIBMPlexSans(Font.BOLD, 11f));
        btnToggleVistaTecnica.setBackground(Theme.getElevated());
        btnToggleVistaTecnica.setForeground(Theme.getTexto());
        btnToggleVistaTecnica.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getBorder(), 1),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        btnToggleVistaTecnica.setFocusPainted(false);
        btnToggleVistaTecnica.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToggleVistaTecnica.setIcon(AppIcon.of("settings", 12, Theme.getTexto()));
        btnToggleVistaTecnica.setToolTipText("Alternar entre Modo Limpio (pasajero) y Modo Técnico (TADs, métricas y consola)");
        btnToggleVistaTecnica.addActionListener(e -> alternarVistaTecnica());

        panelSesionCentro.add(Box.createHorizontalGlue());
        panelSesionCentro.add(lblEstadoSesion);
        panelSesionCentro.add(Box.createHorizontalStrut(8));
        panelSesionCentro.add(btnToggleVistaTecnica);
        panelSesionCentro.add(Box.createHorizontalStrut(8));
        panelSesionCentro.add(btnSesionAdmin);
        panelSesionCentro.add(Box.createHorizontalStrut(8));
        panelSesionCentro.add(btnAlternarTemaHeader);
        panelSesionCentro.add(Box.createHorizontalGlue());
        panelEncabezado.add(panelSesionCentro, BorderLayout.CENTER);

        // Panel Derecho: Reloj/Fecha + Badges semanticos de clima operacional
        JPanel panelDerecho = new JPanel(new GridLayout(2, 1, 0, 3));
        panelDerecho.setOpaque(false);

        lblReloj = new JLabel("Hora: --:--:--");
        lblReloj.setFont(FontManager.getIBMPlexMono(Font.BOLD, 12f));
        lblReloj.setForeground(Theme.getTexto());
        lblReloj.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel panelBadgesClima = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        panelBadgesClima.setOpaque(false);

        // Badge 1: Operaciones Normales (success) con punto verde
        badgeNorm = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.getSuccessSoft());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
            }
        };
        badgeNorm.setOpaque(false);
        dotNorm = new JLabel(AppIcon.dot(8, Theme.getSuccess()));
        txtNorm = new JLabel("Operaciones Normales");
        txtNorm.setFont(FontManager.getIBMPlexSans(Font.BOLD, 10f));
        txtNorm.setForeground(Theme.getSuccess());
        badgeNorm.add(dotNorm);
        badgeNorm.add(txtNorm);

        // Badge 2: Heavy Rain (warning) con punto ambar
        badgeRain = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.getWarningSoft());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
            }
        };
        badgeRain.setOpaque(false);
        dotRain = new JLabel(AppIcon.dot(8, Theme.getWarning()));
        // Badge 2: Lluvia Intensa (alerta meteorológica traducida según política de idioma; tooltip METAR técnico)
        txtRain = new JLabel("Lluvia Intensa");
        txtRain.setToolTipText("Reporte METAR: +RA (Heavy Rain / Lluvia Intensa en zona terminal)");
        txtRain.setFont(FontManager.getIBMPlexSans(Font.BOLD, 10f));
        txtRain.setForeground(Theme.getWarning());
        badgeRain.add(dotRain);
        badgeRain.add(txtRain);

        panelBadgesClima.add(badgeNorm);
        panelBadgesClima.add(badgeRain);

        panelDerecho.add(lblReloj);
        panelDerecho.add(panelBadgesClima);
        panelEncabezado.add(panelDerecho, BorderLayout.EAST);

        this.panelEncabezado = panelEncabezado;
        return panelEncabezado;
    }

    // HUMANIZACION UI - Dashboard visual de 4 metricas con entrada stagger 60ms
    private JPanel crearPanelDashboardMetricas() {
        if (panelDashboardKpi == null) {
            panelDashboardKpi = new PanelDashboardKpi();
        }
        return panelDashboardKpi;
    }

    private JPanel crearTarjetaMetrica(String titulo, JLabel etiquetaValor, String subtitulo) {
        JPanel p = new JPanel(new GridLayout(3, 1, 0, 1));
        p.setBackground(fondoTarjeta);
        p.setBorder(new CompoundBorder(
                new LineBorder(bordeTarjeta, 1),
                new EmptyBorder(6, 12, 6, 12)));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 10));
        t.setForeground(textoGris);

        etiquetaValor.setFont(new Font("Segoe UI", Font.BOLD, 14));
        etiquetaValor.setForeground(textoBlanco);

        JLabel s = new JLabel(subtitulo);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        s.setForeground(azulCyan);

        p.add(t);
        p.add(etiquetaValor);
        p.add(s);
        return p;
    }

    private void actualizarMetricasDashboard() {
        if (panelDashboardKpi != null) {
            panelDashboardKpi.actualizarVuelos(controlador.obtenerContadorVuelos());
            Vuelo v = controlador.obtenerVueloActivo();
            int ocup = (v != null) ? v.contarAsientosOcupados() : 0;
            panelDashboardKpi.actualizarOcupacion(ocup, 50);
            if (listaEquipaje != null) {
                double peso = listaEquipaje.calcularPesoTotal();
                int sobre = listaEquipaje.contarSobrepeso(23.0);
                panelDashboardKpi.actualizarEquipaje(peso, listaEquipaje.obtenerTamano(), sobre);
            }
            if (listaPuertas != null) {
                PuertaEmbarque act = listaPuertas.obtenerPuertaActual();
                if (act != null) {
                    panelDashboardKpi.actualizarPuerta("Puerta " + act.obtenerNumeroPuerta() + " (" + act.obtenerEstado() + ")");
                }
            }
        }
        if (lblKpiVuelos != null) {
            lblKpiVuelos.setText(controlador.obtenerContadorVuelos() + " vuelos registrados");
        }
        if (lblKpiOcupacion != null && barraKpiOcupacion != null) {
            Vuelo v = controlador.obtenerVueloActivo();
            int ocup = (v != null) ? v.contarAsientosOcupados() : 0;
            lblKpiOcupacion.setText(ocup + " / 50 (" + ((ocup * 100) / 50) + "%)");
            barraKpiOcupacion.setValue(ocup);
            if (ocup > 35) barraKpiOcupacion.setForeground(rojoOcupado);
            else barraKpiOcupacion.setForeground(verdeLibre);
        }
        if (lblKpiEquipaje != null && listaEquipaje != null) {
            double peso = listaEquipaje.calcularPesoTotal();
            int sobre = listaEquipaje.contarSobrepeso(23.0);
            lblKpiEquipaje.setText(String.format(java.util.Locale.US, "%.1f kg (%d maletas)", peso, listaEquipaje.obtenerTamano()));
            if (sobre > 0) lblKpiEquipaje.setForeground(new Color(255, 180, 80));
            else lblKpiEquipaje.setForeground(Color.WHITE);
        }
        if (lblKpiPuerta != null && listaPuertas != null) {
            PuertaEmbarque act = listaPuertas.obtenerPuertaActual();
            if (act != null) lblKpiPuerta.setText(act.obtenerNumeroPuerta() + " (" + act.obtenerEstado() + ")");
        }
    }

    // Panel de Catalogo de Vuelos (Arreglos)
    private JPanel crearPanelModuloVuelos() {
        JPanel panelVuelos = new JPanel(new BorderLayout(0, 8));
        panelVuelos.setBackground(fondoGris);
        panelVuelos.setBorder(new EmptyBorder(10, 12, 10, 8));

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setOpaque(false);

        JLabel lblTituloSeccion = new JLabel("Catálogo de Vuelos (Arreglo)");
        lblTituloSeccion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloSeccion.setForeground(textoBlanco);

        lblContadorArreglo = new JLabel("Vuelos registrados: 0");
        lblContadorArreglo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblContadorArreglo.setForeground(textoGris);

        panelSuperior.add(lblTituloSeccion, BorderLayout.WEST);
        panelSuperior.add(lblContadorArreglo, BorderLayout.EAST);
        panelVuelos.add(panelSuperior, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setOpaque(false);
        panelFormulario.setBorder(crearBordeTituloDark("Programación de Vuelos Comerciales"));

        GridBagConstraints r = new GridBagConstraints();
        r.insets = new Insets(3, 4, 3, 4);
        r.fill = GridBagConstraints.HORIZONTAL;

        JPanel panelCodigo = new JPanel(new BorderLayout(2, 0));
        panelCodigo.setOpaque(false);
        txtCodigo = new JTextField("CP-2028", 5);
        txtCodigo.setPreferredSize(new Dimension(65, 24));
        txtCodigo.setMinimumSize(new Dimension(55, 22));
        estilizarCampoTexto(txtCodigo);

        JPanel pAccionesCodigo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        pAccionesCodigo.setOpaque(false);

        JButton botonGenCod = new JButton("Gen");
        estilizarBotonAccion(botonGenCod, new Color(24, 34, 50), azulCyan);
        botonGenCod.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonGenCod.setMargin(new Insets(1, 4, 1, 4));
        botonGenCod.setToolTipText("Generar nuevo código de vuelo correlativo O(1)");
        botonGenCod.addActionListener(e -> generarCodigoVuelo());

        JButton botonAutoFillVuelo = new JButton("Auto");
        estilizarBotonAccion(botonAutoFillVuelo, new Color(20, 40, 70), doradoAlerta);
        botonAutoFillVuelo.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonAutoFillVuelo.setMargin(new Insets(1, 4, 1, 4));
        botonAutoFillVuelo.setToolTipText("Autocompletar formulario con datos comerciales para registro rápido sin complicaciones");
        botonAutoFillVuelo.addActionListener(e -> autocompletarFormularioVueloAdmin());

        pAccionesCodigo.add(botonGenCod);
        pAccionesCodigo.add(botonAutoFillVuelo);

        panelCodigo.add(txtCodigo, BorderLayout.CENTER);
        panelCodigo.add(pAccionesCodigo, BorderLayout.EAST);

        cbOrigen = new JComboBox<>(new String[]{"Lima", "Cusco", "Arequipa", "Piura", "Iquitos", "Tarapoto", "Trujillo"});
        estilizarCombo(cbOrigen);
        cbDestino = new JComboBox<>(new String[]{"Trujillo", "Cusco", "Arequipa", "Piura", "Iquitos", "Tarapoto", "Chiclayo"});
        estilizarCombo(cbDestino);
        cbOrigen.addActionListener(e -> recalcularHorarioYTarifa());
        cbDestino.addActionListener(e -> recalcularHorarioYTarifa());

        spinnerFecha = new JSpinner(new SpinnerDateModel());
        spinnerFecha.setEditor(new JSpinner.DateEditor(spinnerFecha, "yyyy-MM-dd"));
        spinnerFecha.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        spinnerFecha.setBackground(fondoEntrada);
        if (spinnerFecha.getEditor().getComponentCount() > 0) {
            spinnerFecha.getEditor().getComponent(0).setBackground(fondoEntrada);
            spinnerFecha.getEditor().getComponent(0).setForeground(textoBlanco);
        }
        spinnerFecha.setBorder(new LineBorder(bordeTarjeta, 1));

        String[] horarios = {"06:00", "07:30", "08:30", "09:45", "11:15", "13:00", "14:15", "16:00", "17:30", "19:00", "21:15"};
        cbHoraSalida = new JComboBox<>(horarios);
        cbHoraSalida.setEditable(true);
        cbHoraSalida.setSelectedItem("07:30");
        estilizarCombo(cbHoraSalida);
        cbHoraSalida.addActionListener(e -> recalcularHorarioYTarifa());

        txtHoraLlegada = new JTextField("08:40", 5);
        txtHoraLlegada.setEditable(false);
        estilizarCampoTexto(txtHoraLlegada);
        txtHoraLlegada.setBackground(new Color(20, 26, 38));

        txtDuracion = new JTextField("70", 4);
        txtDuracion.setEditable(false);
        estilizarCampoTexto(txtDuracion);
        txtDuracion.setBackground(new Color(20, 26, 38));

        txtPrecioBase = new JTextField("120.00", 5);
        estilizarCampoTexto(txtPrecioBase);
        cbClase = new JComboBox<>(new String[]{"Turista", "Ejecutiva", "Primera"});
        estilizarCombo(cbClase);
        cbClase.addActionListener(e -> recalcularHorarioYTarifa());

        // Fila 0
        r.weightx = 0.0; r.fill = GridBagConstraints.NONE; r.anchor = GridBagConstraints.LINE_START;
        JLabel lblCod = crearEtiquetaFormulario("Código:");
        lblCod.setPreferredSize(new Dimension(58, 22));
        lblCod.setMinimumSize(new Dimension(55, 22));
        r.gridx = 0; r.gridy = 0; panelFormulario.add(lblCod, r);
        r.weightx = 1.0; r.fill = GridBagConstraints.HORIZONTAL;
        r.gridx = 1; r.gridy = 0; panelFormulario.add(panelCodigo, r);
        r.weightx = 0.0; r.fill = GridBagConstraints.NONE;
        JLabel lblOri = crearEtiquetaFormulario("Origen:");
        lblOri.setPreferredSize(new Dimension(52, 22));
        lblOri.setMinimumSize(new Dimension(50, 22));
        r.gridx = 2; r.gridy = 0; panelFormulario.add(lblOri, r);
        r.weightx = 1.0; r.fill = GridBagConstraints.HORIZONTAL;
        r.gridx = 3; r.gridy = 0; panelFormulario.add(cbOrigen, r);
        r.weightx = 0.0; r.fill = GridBagConstraints.NONE;
        JLabel lblDes = crearEtiquetaFormulario("Destino:");
        lblDes.setPreferredSize(new Dimension(55, 22));
        lblDes.setMinimumSize(new Dimension(52, 22));
        r.gridx = 4; r.gridy = 0; panelFormulario.add(lblDes, r);
        r.weightx = 1.0; r.fill = GridBagConstraints.HORIZONTAL;
        r.gridx = 5; r.gridy = 0; panelFormulario.add(cbDestino, r);

        // Fila 1
        r.weightx = 0.0; r.fill = GridBagConstraints.NONE; r.anchor = GridBagConstraints.LINE_START;
        JLabel lblFec = crearEtiquetaFormulario("Fecha:");
        lblFec.setPreferredSize(new Dimension(58, 22));
        lblFec.setMinimumSize(new Dimension(55, 22));
        r.gridx = 0; r.gridy = 1; panelFormulario.add(lblFec, r);
        r.weightx = 1.0; r.fill = GridBagConstraints.HORIZONTAL;
        r.gridx = 1; r.gridy = 1; panelFormulario.add(spinnerFecha, r);
        r.weightx = 0.0; r.fill = GridBagConstraints.NONE;
        JLabel lblSal = crearEtiquetaFormulario("Salida:");
        lblSal.setPreferredSize(new Dimension(52, 22));
        lblSal.setMinimumSize(new Dimension(50, 22));
        r.gridx = 2; r.gridy = 1; panelFormulario.add(lblSal, r);
        r.weightx = 1.0; r.fill = GridBagConstraints.HORIZONTAL;
        r.gridx = 3; r.gridy = 1; panelFormulario.add(cbHoraSalida, r);
        r.weightx = 0.0; r.fill = GridBagConstraints.NONE;
        JLabel lblLleg = crearEtiquetaFormulario("Llegada:");
        lblLleg.setPreferredSize(new Dimension(55, 22));
        lblLleg.setMinimumSize(new Dimension(52, 22));
        r.gridx = 4; r.gridy = 1; panelFormulario.add(lblLleg, r);
        r.weightx = 1.0; r.fill = GridBagConstraints.HORIZONTAL;
        r.gridx = 5; r.gridy = 1; panelFormulario.add(txtHoraLlegada, r);

        // Fila 2
        r.weightx = 0.0; r.fill = GridBagConstraints.NONE; r.anchor = GridBagConstraints.LINE_START;
        JLabel lblDur = crearEtiquetaFormulario("Duración:");
        lblDur.setPreferredSize(new Dimension(58, 22));
        lblDur.setMinimumSize(new Dimension(55, 22));
        r.gridx = 0; r.gridy = 2; panelFormulario.add(lblDur, r);
        r.weightx = 1.0; r.fill = GridBagConstraints.HORIZONTAL;
        r.gridx = 1; r.gridy = 2; panelFormulario.add(txtDuracion, r);
        r.weightx = 0.0; r.fill = GridBagConstraints.NONE;
        JLabel lblTar = crearEtiquetaFormulario("Tarifa S/.:");
        lblTar.setPreferredSize(new Dimension(62, 22));
        lblTar.setMinimumSize(new Dimension(60, 22));
        r.gridx = 2; r.gridy = 2; panelFormulario.add(lblTar, r);
        r.weightx = 1.0; r.fill = GridBagConstraints.HORIZONTAL;
        r.gridx = 3; r.gridy = 2; panelFormulario.add(txtPrecioBase, r);
        r.weightx = 0.0; r.fill = GridBagConstraints.NONE;
        JLabel lblCla = crearEtiquetaFormulario("Clase:");
        lblCla.setPreferredSize(new Dimension(55, 22));
        lblCla.setMinimumSize(new Dimension(50, 22));
        r.gridx = 4; r.gridy = 2; panelFormulario.add(lblCla, r);
        r.weightx = 1.0; r.fill = GridBagConstraints.HORIZONTAL;
        r.gridx = 5; r.gridy = 2; panelFormulario.add(cbClase, r);

        // Panel de Botones CRUD y Algoritmos Organizado en 3 Apartados de Vidrio Esmerilado (GlassPanel)
        JPanel panelBotonesCRUD = new JPanel(new GridLayout(1, 3, 6, 0));
        panelBotonesCRUD.setOpaque(false);
        panelBotonesCRUD.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));

        JButton botonAgregar = new JButton("Agregar Vuelo");
        estilizarBotonAccion(botonAgregar, azulAccion, new Color(15, 20, 29));
        botonAgregar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonAgregar.setMargin(new Insets(2, 6, 2, 6));
        botonAgregar.setToolTipText("Inserción secuencial O(1) en arreglo unidimensional");
        botonAgregar.addActionListener(e -> accionAgregarVuelo());

        JButton botonActualizar = new JButton("Actualizar");
        estilizarBotonAccion(botonActualizar, new Color(24, 34, 50), textoBlanco);
        botonActualizar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonActualizar.setMargin(new Insets(2, 6, 2, 6));
        botonActualizar.setToolTipText("Actualización por índice/búsqueda O(n)");
        botonActualizar.addActionListener(e -> accionActualizarVuelo());

        JButton botonEliminar = new JButton("Eliminar Vuelo");
        estilizarBotonAccion(botonEliminar, rojoBoton, Color.WHITE);
        botonEliminar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonEliminar.setMargin(new Insets(2, 6, 2, 6));
        botonEliminar.setToolTipText("Eliminación lógica/compactación O(n)");
        botonEliminar.addActionListener(e -> accionEliminarVuelo());

        JButton botonLimpiar = new JButton("Limpiar");
        estilizarBotonAccion(botonLimpiar, new Color(24, 34, 50), textoGris);
        botonLimpiar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonLimpiar.setMargin(new Insets(2, 6, 2, 6));
        botonLimpiar.setToolTipText("Limpieza de formulario O(1)");
        botonLimpiar.addActionListener(e -> limpiarCamposVuelo());

        JButton botonBuscar = new JButton("Buscar Código");
        estilizarBotonAccion(botonBuscar, new Color(24, 34, 50), textoBlanco);
        botonBuscar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonBuscar.setMargin(new Insets(2, 6, 2, 6));
        botonBuscar.setToolTipText("Búsqueda Lineal O(n) sobre arreglo");
        botonBuscar.addActionListener(e -> accionBuscarVuelo());

        JButton botonBuscarBinario = new JButton("Búsqueda Binaria");
        estilizarBotonAccion(botonBuscarBinario, new Color(24, 34, 50), doradoAlerta);
        botonBuscarBinario.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonBuscarBinario.setMargin(new Insets(2, 6, 2, 6));
        botonBuscarBinario.setToolTipText("Búsqueda Binaria O(log n) previa ordenación");
        botonBuscarBinario.addActionListener(e -> accionBuscarVueloBinario());

        JButton botonBuscarAVL = new JButton("Búsqueda AVL O(log n)");
        estilizarBotonAccion(botonBuscarAVL, new Color(24, 34, 50), Theme.getSuccess());
        botonBuscarAVL.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonBuscarAVL.setMargin(new Insets(2, 6, 2, 6));
        botonBuscarAVL.setToolTipText("Búsqueda logarítmica O(log n) en Árbol AVL balanceado");
        botonBuscarAVL.addActionListener(e -> accionBuscarVueloAVL());

        JButton botonOrdenarPrecio = new JButton("Por Precio O(n²)");
        estilizarBotonAccion(botonOrdenarPrecio, new Color(24, 34, 50), textoBlanco);
        botonOrdenarPrecio.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonOrdenarPrecio.setMargin(new Insets(2, 4, 2, 4));
        botonOrdenarPrecio.setToolTipText("Ordenamiento cuadrático O(n²) por tarifa");
        botonOrdenarPrecio.addActionListener(e -> accionOrdenarVuelosPorPrecio());

        JButton botonQuicksort = new JButton("Quicksort O(n log n)");
        estilizarBotonAccion(botonQuicksort, new Color(24, 34, 50), Theme.getSuccess());
        botonQuicksort.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonQuicksort.setMargin(new Insets(2, 4, 2, 4));
        botonQuicksort.setToolTipText("Ordenamiento avanzado Quicksort O(n log n) por tarifa");
        botonQuicksort.addActionListener(e -> accionOrdenarVuelosQuicksort());

        JButton botonOrdenarCodigo = new JButton("Por Código O(n²)");
        estilizarBotonAccion(botonOrdenarCodigo, new Color(24, 34, 50), textoBlanco);
        botonOrdenarCodigo.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonOrdenarCodigo.setMargin(new Insets(2, 4, 2, 4));
        botonOrdenarCodigo.setToolTipText("Ordenamiento cuadrático O(n²) por código");
        botonOrdenarCodigo.addActionListener(e -> accionOrdenarVuelosPorCodigo());

        JButton botonRecorrerVuelos = new JButton("Recorrer Arreglo");
        estilizarBotonAccion(botonRecorrerVuelos, new Color(24, 34, 50), azulCyan);
        botonRecorrerVuelos.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonRecorrerVuelos.setMargin(new Insets(2, 4, 2, 4));
        botonRecorrerVuelos.setToolTipText("Recorrido secuencial iterativo O(n)");
        botonRecorrerVuelos.addActionListener(e -> accionRecorrerArregloVuelos());

        JButton botonCopiarVuelos = new JButton("Copiar Arreglo");
        estilizarBotonAccion(botonCopiarVuelos, new Color(24, 34, 50), textoBlanco);
        botonCopiarVuelos.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonCopiarVuelos.setMargin(new Insets(2, 4, 2, 4));
        botonCopiarVuelos.setToolTipText("Clonación en nuevo bloque O(n)");
        botonCopiarVuelos.addActionListener(e -> accionCopiarArregloVuelos());

        JButton botonEstadisticas = new JButton("Estadísticas");
        estilizarBotonAccion(botonEstadisticas, new Color(24, 34, 50), azulCyan);
        botonEstadisticas.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonEstadisticas.setMargin(new Insets(2, 4, 2, 4));
        botonEstadisticas.setToolTipText("Cálculo de métricas de catálogo O(n)");
        botonEstadisticas.addActionListener(e -> accionMostrarEstadisticasCatalogo());

        // Apartado 1: Operaciones del Catálogo (CRUD) - 2x2 grid
        JPanel contenidoCrud = new JPanel(new GridLayout(2, 2, 4, 3));
        contenidoCrud.setOpaque(false);
        contenidoCrud.add(botonAgregar);
        contenidoCrud.add(botonActualizar);
        contenidoCrud.add(botonEliminar);
        contenidoCrud.add(botonLimpiar);
        GlassPanel cardCrud = GlassPanel.crearApartado("Operaciones Catálogo", "Inserción O(1) y edición", azulAccion, contenidoCrud);

        // Apartado 2: Algoritmos de Búsqueda
        JPanel contenidoBusqueda = new JPanel(new GridLayout(0, 1, 0, 3));
        contenidoBusqueda.setOpaque(false);
        contenidoBusqueda.add(botonBuscar);
        contenidoBusqueda.add(botonBuscarBinario);
        contenidoBusqueda.add(botonBuscarAVL);
        GlassPanel cardBusqueda = GlassPanel.crearApartado("Algoritmos Búsqueda", "Lineal, Binaria y AVL", doradoAlerta, contenidoBusqueda);

        // Apartado 3: Ordenamiento y Recorridos Algorítmicos - 3x2 grid
        JPanel contenidoOrden = new JPanel(new GridLayout(3, 2, 4, 3));
        contenidoOrden.setOpaque(false);
        contenidoOrden.add(botonQuicksort);
        contenidoOrden.add(botonOrdenarPrecio);
        contenidoOrden.add(botonOrdenarCodigo);
        contenidoOrden.add(botonRecorrerVuelos);
        contenidoOrden.add(botonCopiarVuelos);
        contenidoOrden.add(botonEstadisticas);
        GlassPanel cardOrden = GlassPanel.crearApartado("Ordenamiento y Métricas", "Quicksort O(n log n) y reporte", azulCyan, contenidoOrden);

        panelBotonesCRUD.add(cardCrud);
        panelBotonesCRUD.add(cardBusqueda);
        panelBotonesCRUD.add(cardOrden);

        r.gridx = 0; r.gridy = 3; r.gridwidth = 6; r.weightx = 1.0; r.fill = GridBagConstraints.HORIZONTAL;
        panelFormulario.add(panelBotonesCRUD, r);

        // Tabla de vuelos con renderizado dinámico de estado
        String[] columnas = {"Índice", "Código", "Ruta", "Fecha", "Salida", "Llegada", "Tarifa", "Clase", "Libres", "Estado Operativo"};
        modeloTablaVuelos = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int f, int c) { return false; }
        };
        tablaVuelos = new JTable(modeloTablaVuelos);
        tablaVuelos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaVuelos.setRowHeight(24);
        tablaVuelos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaVuelos.setBackground(fondoEntrada);
        tablaVuelos.setForeground(textoBlanco);
        tablaVuelos.setGridColor(new Color(28, 36, 52));
        tablaVuelos.setSelectionBackground(new Color(36, 48, 70));
        tablaVuelos.setSelectionForeground(doradoAlerta);

        JTableHeader enc = tablaVuelos.getTableHeader();
        enc.setFont(new Font("Segoe UI", Font.BOLD, 10));
        enc.setBackground(new Color(20, 26, 38));
        enc.setForeground(doradoAlerta);
        enc.setPreferredSize(new Dimension(0, 26));
        enc.setReorderingAllowed(false);

        // Renderizador de colores para estados operativos
        tablaVuelos.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(new Font("Segoe UI", Font.BOLD, 10));
                String est = (value != null) ? value.toString() : "";
                if (est.contains("En Vuelo")) {
                    l.setForeground(azulCyan);
                } else if (est.contains("Abordando") || est.contains("En Pista")) {
                    l.setForeground(doradoAlerta);
                } else if (est.contains("A Tiempo")) {
                    l.setForeground(verdeLibre);
                } else {
                    l.setForeground(textoGris);
                }
                if (!isSelected) {
                    l.setBackground(fondoEntrada);
                }
                return l;
            }
        });

        tablaVuelos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent ev) {
                int filaSel = tablaVuelos.getSelectedRow();
                if (filaSel >= 0) {
                    String codigo = (String) modeloTablaVuelos.getValueAt(filaSel, 1);
                    Vuelo vuelo = controlador.buscarVueloPorCodigo(codigo);
                    if (vuelo != null) {
                        cargarVueloEnFormulario(vuelo);
                        controlador.seleccionarVueloActivo(vuelo.obtenerCodigo());
                        actualizarMatrizCabina();
                        actualizarEtiquetaVueloActivo();
                        actualizarMetricasDashboard();
                    }
                }
            }
        });

        JPanel panelFiltroYTabla = new JPanel(new BorderLayout(0, 4));
        panelFiltroYTabla.setOpaque(false);
        panelFiltroYTabla.setBorder(crearBordeTituloDark("Itinerario de Vuelos (Seleccione un vuelo para gestionar su cabina)"));

        JPanel panelBarraFiltro = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        panelBarraFiltro.setOpaque(false);

        btnAlternarSimulador = new JButton("Iniciar Simulacion Radar");
        btnAlternarSimulador.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnAlternarSimulador.setBackground(new Color(20, 36, 28));
        btnAlternarSimulador.setForeground(verdeLibre);
        btnAlternarSimulador.setBorder(new CompoundBorder(new LineBorder(verdeLibre, 1), new EmptyBorder(3, 8, 3, 8)));
        btnAlternarSimulador.setFocusPainted(false);
        btnAlternarSimulador.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAlternarSimulador.addActionListener(e -> alternarSimuladorDinamico());
        panelBarraFiltro.add(btnAlternarSimulador);

        JLabel lblFiltro = crearEtiquetaFormulario("Filtrar:");
        panelBarraFiltro.add(lblFiltro);
        txtFiltroVuelos = new JTextField(12);
        estilizarCampoTexto(txtFiltroVuelos);
        txtFiltroVuelos.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarTablaVuelos(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarTablaVuelos(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarTablaVuelos(); }
        });
        panelBarraFiltro.add(txtFiltroVuelos);

        this.scrollTablaVuelos = new JScrollPane(tablaVuelos);
        scrollTablaVuelos.getViewport().setBackground(fondoEntrada);
        scrollTablaVuelos.setBorder(new LineBorder(bordeTarjeta, 1));
        scrollTablaVuelos.getVerticalScrollBar().setUnitIncrement(16);

        panelFiltroYTabla.add(panelBarraFiltro, BorderLayout.NORTH);
        panelFiltroYTabla.add(scrollTablaVuelos, BorderLayout.CENTER);

        JPanel panelCentroVuelos = new JPanel(new BorderLayout(0, 6));
        panelCentroVuelos.setOpaque(false);
        panelCentroVuelos.add(panelFormulario, BorderLayout.NORTH);
        panelCentroVuelos.add(panelFiltroYTabla, BorderLayout.CENTER);

        panelVuelos.add(panelCentroVuelos, BorderLayout.CENTER);
        return panelVuelos;
    }

    // Panel de Cabina de Pasajeros (Matriz 10x5)
    private JPanel crearPanelModuloCabina() {
        JPanel panelCabina = new JPanel(new BorderLayout(0, 6));
        panelCabina.setBackground(fondoGris);
        panelCabina.setBorder(new EmptyBorder(10, 8, 10, 12));

        JPanel panelSuperior = new JPanel(new GridLayout(2, 1, 0, 4));
        panelSuperior.setOpaque(false);

        JPanel filaSup1 = new JPanel(new BorderLayout(8, 0));
        filaSup1.setOpaque(false);

        JLabel lblTituloCabina = new JLabel("Distribución de Cabina de Pasajeros");
        lblTituloCabina.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloCabina.setForeground(textoBlanco);

        lblResumenOcupacion = new JLabel("Total: 50 | Libres: 47 | Ocupados: 3");
        lblResumenOcupacion.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblResumenOcupacion.setForeground(doradoAlerta);

        filaSup1.add(lblTituloCabina, BorderLayout.WEST);
        filaSup1.add(lblResumenOcupacion, BorderLayout.EAST);

        JPanel filaSup2 = new JPanel(new BorderLayout());
        filaSup2.setOpaque(false);

        lblVueloActivoCabina = new JLabel("Vuelo Activo: CP-2023 (Lima -> Cusco)");
        lblVueloActivoCabina.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblVueloActivoCabina.setForeground(verdeLibre);

        filaSup2.add(lblVueloActivoCabina, BorderLayout.WEST);

        panelSuperior.add(filaSup1);
        panelSuperior.add(filaSup2);
        panelCabina.add(panelSuperior, BorderLayout.NORTH);

        // Simulacion del fuselaje del avion
        JPanel panelFuselaje = new JPanel(new BorderLayout(0, 4));
        panelFuselaje.setOpaque(false);

        JPanel panelFuselajeTecho = new JPanel(new BorderLayout(0, 2));
        panelFuselajeTecho.setOpaque(false);

        JLabel lblCockpit = new JLabel("[ CABINA DE MANDO / PILOTOS ]", SwingConstants.CENTER);
        lblCockpit.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblCockpit.setForeground(azulCyan);
        panelFuselajeTecho.add(lblCockpit, BorderLayout.NORTH);

        JPanel panelCabeceraColumnas = new JPanel(new GridLayout(1, 5, 4, 0));
        panelCabeceraColumnas.setOpaque(false);
        panelCabeceraColumnas.setBorder(new EmptyBorder(2, 4, 2, 4));
        String[] titulosCols = {"A (Ventana)", "B (Pasillo)", "C (Centro)", "D (Pasillo)", "E (Ventana)"};
        for (String titCol : titulosCols) {
            JLabel lblCol = new JLabel(titCol, SwingConstants.CENTER);
            lblCol.setFont(new Font("Segoe UI", Font.BOLD, 9));
            lblCol.setForeground(doradoAlerta);
            panelCabeceraColumnas.add(lblCol);
        }
        panelFuselajeTecho.add(panelCabeceraColumnas, BorderLayout.SOUTH);
        panelFuselaje.add(panelFuselajeTecho, BorderLayout.NORTH);

        JPanel panelCuadriculaMatriz = new JPanel(new GridLayout(10, 5, 4, 3));
        panelCuadriculaMatriz.setOpaque(false);
        panelCuadriculaMatriz.setBorder(crearBordeTituloDark("Mapa Físico de Asientos (50 Plazas Distribuidas)"));
        panelCuadriculaMatriz.setPreferredSize(new Dimension(380, 275));
        panelCuadriculaMatriz.setMinimumSize(new Dimension(340, 255));

        for (int f = 0; f < 10; f++) {
            for (int c = 0; c < 5; c++) {
                final int fila = f;
                final int columna = c;
                JButton botonAsiento = new JButton();
                botonAsiento.setFont(new Font("Segoe UI", Font.BOLD, 10));
                botonAsiento.setFocusPainted(false);
                botonAsiento.setMargin(new Insets(1, 1, 1, 1));
                botonAsiento.setPreferredSize(new Dimension(56, 24));
                botonAsiento.setMinimumSize(new Dimension(48, 22));
                botonAsiento.setBackground(new Color(20, 28, 42));
                botonAsiento.setForeground(textoBlanco);
                botonAsiento.setBorder(new LineBorder(bordeTarjeta, 1));
                botonAsiento.addActionListener(e -> seleccionarAsientoEnMatriz(fila, columna));
                botonesAsientos[f][c] = botonAsiento;
                panelCuadriculaMatriz.add(botonAsiento);
            }
        }
        panelFuselaje.add(panelCuadriculaMatriz, BorderLayout.CENTER);

        // Pase de abordar dinamico (Tarjeta de previsualizacion compacta de 2 filas)
        JPanel panelPaseAbordar = new JPanel(new GridLayout(2, 1, 0, 3));
        panelPaseAbordar.setBackground(fondoTarjeta);
        panelPaseAbordar.setBorder(new CompoundBorder(
                new LineBorder(bordeTarjeta, 1),
                new EmptyBorder(5, 10, 5, 10)));

        JLabel lblTitPase = new JLabel("TARJETA DE EMBARQUE EN VIVO (PREVISUALIZACIÓN)", SwingConstants.LEFT);
        lblTitPase.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTitPase.setForeground(azulAccion);

        lblPasePasajero = new JLabel("Pasajero: -- (DNI: --)", SwingConstants.RIGHT);
        lblPasePasajero.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPasePasajero.setForeground(textoBlanco);

        lblPaseVueloRuta = new JLabel("Vuelo: CP-2024 | Ruta: Lima -> Arequipa", SwingConstants.LEFT);
        lblPaseVueloRuta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblPaseVueloRuta.setForeground(textoGris);

        lblPaseAsientoZona = new JLabel("Asiento: 1A [Ventana (+30%)] | Tarifa: S/. 180.70", SwingConstants.RIGHT);
        lblPaseAsientoZona.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPaseAsientoZona.setForeground(verdeLibre);

        JPanel filaPase1 = new JPanel(new BorderLayout(8, 0));
        filaPase1.setOpaque(false);
        filaPase1.add(lblTitPase, BorderLayout.WEST);
        filaPase1.add(lblPasePasajero, BorderLayout.EAST);

        JPanel filaPase2 = new JPanel(new BorderLayout(8, 0));
        filaPase2.setOpaque(false);
        filaPase2.add(lblPaseVueloRuta, BorderLayout.WEST);
        filaPase2.add(lblPaseAsientoZona, BorderLayout.EAST);

        panelPaseAbordar.add(filaPase1);
        panelPaseAbordar.add(filaPase2);

        JPanel panelFormularioAsiento = new JPanel(new BorderLayout(0, 4));
        panelFormularioAsiento.setOpaque(false);
        panelFormularioAsiento.setBorder(crearBordeTituloDark("Gestión de Asignación"));

        txtFilaSeleccionada = new JTextField("0", 3);
        estilizarCampoTexto(txtFilaSeleccionada);
        txtColSeleccionada = new JTextField("0", 3);
        estilizarCampoTexto(txtColSeleccionada);
        txtDniAsiento = new JTextField("", 9);
        txtDniAsiento.setToolTipText("DNI del pasajero");
        estilizarCampoTexto(txtDniAsiento);

        lblZonaAsiento = new JLabel("Zona: Ventana (Factor x1.3)");
        lblZonaAsiento.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblZonaAsiento.setForeground(azulCyan);

        lblCostoCalculado = new JLabel("Tarifa Asiento: S/. 180.70");
        lblCostoCalculado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblCostoCalculado.setForeground(doradoAlerta);

        JPanel filaInputsAsiento = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        filaInputsAsiento.setOpaque(false);
        filaInputsAsiento.add(crearEtiquetaFormulario("Fila:"));
        filaInputsAsiento.add(txtFilaSeleccionada);
        filaInputsAsiento.add(crearEtiquetaFormulario("Col:"));
        filaInputsAsiento.add(txtColSeleccionada);
        filaInputsAsiento.add(crearEtiquetaFormulario("DNI Pasajero:"));
        filaInputsAsiento.add(txtDniAsiento);

        JPanel filaInfoAsiento = new JPanel(new BorderLayout(8, 0));
        filaInfoAsiento.setOpaque(false);
        filaInfoAsiento.setBorder(new EmptyBorder(0, 4, 4, 4));
        filaInfoAsiento.add(lblZonaAsiento, BorderLayout.WEST);
        filaInfoAsiento.add(lblCostoCalculado, BorderLayout.EAST);

        JPanel pNorteAsiento = new JPanel(new GridLayout(2, 1, 0, 2));
        pNorteAsiento.setOpaque(false);
        pNorteAsiento.add(filaInputsAsiento);
        pNorteAsiento.add(filaInfoAsiento);

        panelFormularioAsiento.add(pNorteAsiento, BorderLayout.NORTH);

        // Panel de Botones de Cabina Organizado en 3 Apartados de Vidrio Esmerilado (GlassPanel)
        JPanel panelBotonesAsientos = new JPanel(new GridLayout(1, 3, 6, 0));
        panelBotonesAsientos.setOpaque(false);

        JButton botonOcupar = new JButton("Ocupar Asiento");
        estilizarBotonAccion(botonOcupar, verdeLibre, Color.WHITE);
        botonOcupar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonOcupar.setMargin(new Insets(2, 4, 2, 4));
        botonOcupar.addActionListener(e -> accionOcuparAsiento());

        JButton botonLiberar = new JButton("Liberar Asiento");
        estilizarBotonAccion(botonLiberar, rojoBoton, Color.WHITE);
        botonLiberar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonLiberar.setMargin(new Insets(2, 4, 2, 4));
        botonLiberar.addActionListener(e -> accionLiberarAsiento());

        JButton botonAutoAsignar = new JButton("Auto-Asignar");
        estilizarBotonAccion(botonAutoAsignar, azulAccion, new Color(15, 20, 29));
        botonAutoAsignar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonAutoAsignar.setMargin(new Insets(2, 4, 2, 4));
        botonAutoAsignar.addActionListener(e -> accionAutoAsignarAsiento());

        JButton botonDeshacer = new JButton("Deshacer Reserva");
        estilizarBotonAccion(botonDeshacer, new Color(24, 34, 50), doradoAlerta);
        botonDeshacer.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonDeshacer.setMargin(new Insets(2, 4, 2, 4));
        botonDeshacer.addActionListener(e -> accionDeshacerPila());

        JButton botonVerPila = new JButton("Ver Pila (LIFO)");
        estilizarBotonAccion(botonVerPila, new Color(24, 34, 50), doradoAlerta);
        botonVerPila.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonVerPila.setMargin(new Insets(2, 4, 2, 4));
        botonVerPila.addActionListener(e -> accionVerPilaLIFO());

        JButton botonRecorrerMatriz = new JButton("Recorrer Matriz");
        estilizarBotonAccion(botonRecorrerMatriz, new Color(24, 34, 50), azulCyan);
        botonRecorrerMatriz.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonRecorrerMatriz.setMargin(new Insets(2, 4, 2, 4));
        botonRecorrerMatriz.addActionListener(e -> accionRecorrerMatrizCabina());

        JButton botonSimularLlenado = new JButton("Simular Llenado");
        estilizarBotonAccion(botonSimularLlenado, new Color(24, 34, 50), textoBlanco);
        botonSimularLlenado.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonSimularLlenado.setMargin(new Insets(2, 4, 2, 4));
        botonSimularLlenado.addActionListener(e -> accionSimularLlenadoRapido());

        JButton botonFichaAvion = new JButton("Ficha Técnica");
        estilizarBotonAccion(botonFichaAvion, new Color(24, 34, 50), azulCyan);
        botonFichaAvion.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonFichaAvion.setMargin(new Insets(2, 4, 2, 4));
        botonFichaAvion.addActionListener(e -> mostrarFichaAvion());

        // Apartado 1: Asignación
        JPanel pContenidoAsig = new JPanel(new GridLayout(0, 1, 0, 3));
        pContenidoAsig.setOpaque(false);
        pContenidoAsig.add(botonOcupar);
        pContenidoAsig.add(botonLiberar);
        pContenidoAsig.add(botonAutoAsignar);
        GlassPanel cardAsignar = GlassPanel.crearApartado("Asignación Cabina", "Gestión plazas", verdeLibre, pContenidoAsig);

        // Apartado 2: Historial LIFO
        JPanel pContenidoPila = new JPanel(new GridLayout(0, 1, 0, 3));
        pContenidoPila.setOpaque(false);
        pContenidoPila.add(botonDeshacer);
        pContenidoPila.add(botonVerPila);
        GlassPanel cardPila = GlassPanel.crearApartado("Historial LIFO", "Reversión reservas", doradoAlerta, pContenidoPila);

        // Apartado 3: Algoritmos y Flota
        JPanel pContenidoSim = new JPanel(new GridLayout(0, 1, 0, 3));
        pContenidoSim.setOpaque(false);
        pContenidoSim.add(botonRecorrerMatriz);
        pContenidoSim.add(botonSimularLlenado);
        pContenidoSim.add(botonFichaAvion);
        GlassPanel cardSim = GlassPanel.crearApartado("Algoritmos y Flota", "Recorrido y flota", azulCyan, pContenidoSim);

        panelBotonesAsientos.add(cardAsignar);
        panelBotonesAsientos.add(cardPila);
        panelBotonesAsientos.add(cardSim);

        panelFormularioAsiento.add(panelBotonesAsientos, BorderLayout.CENTER);

        JPanel panelContenedorCabina = new JPanel();
        panelContenedorCabina.setLayout(new BoxLayout(panelContenedorCabina, BoxLayout.Y_AXIS));
        panelContenedorCabina.setOpaque(false);

        panelFuselaje.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPaseAbordar.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelFormularioAsiento.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelContenedorCabina.add(panelFuselaje);
        panelContenedorCabina.add(Box.createVerticalStrut(6));
        panelContenedorCabina.add(panelPaseAbordar);
        panelContenedorCabina.add(Box.createVerticalStrut(6));
        panelContenedorCabina.add(panelFormularioAsiento);

        JScrollPane scrollCabina = new JScrollPane(panelContenedorCabina);
        scrollCabina.setOpaque(false);
        scrollCabina.getViewport().setOpaque(false);
        scrollCabina.setBorder(null);
        scrollCabina.getVerticalScrollBar().setUnitIncrement(16);
        scrollCabina.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panelCabina.add(scrollCabina, BorderLayout.CENTER);
        return panelCabina;
    }

    // Modulo de Listas Dinámicas: Equipaje (Simple) y Rutas (Doble)
    private Component crearPanelModuloListasDinamicas() {
        if (panelDivisionListas != null) {
            return panelDivisionListas;
        }
        panelDivisionListas = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                crearPanelGestionEquipaje(), crearPanelGestionRutas());
        panelDivisionListas.setDividerLocation(660);
        panelDivisionListas.setResizeWeight(0.5);
        panelDivisionListas.setBorder(null);
        return panelDivisionListas;
    }

    // Gestion de Equipaje (Lista Enlazada Simple)
    private JPanel crearPanelGestionEquipaje() {
        JPanel panelEquipaje = new JPanel(new BorderLayout(0, 8));
        panelEquipaje.setBackground(fondoGris);
        panelEquipaje.setBorder(new EmptyBorder(10, 12, 10, 8));

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setOpaque(false);

        JLabel lblTitulo = new JLabel("Gestión de Equipaje y Bodega");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(textoBlanco);

        lblTamanoMaletas = new JLabel("Maletas: 0");
        lblTamanoMaletas.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTamanoMaletas.setForeground(textoGris);

        panelSuperior.add(lblTitulo, BorderLayout.WEST);
        panelSuperior.add(lblTamanoMaletas, BorderLayout.EAST);
        panelEquipaje.add(panelSuperior, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setOpaque(false);
        panelFormulario.setBorder(crearBordeTituloDark("Registro de Maletas y Control de Carga"));

        GridBagConstraints r = new GridBagConstraints();
        r.insets = new Insets(3, 4, 3, 4);
        r.fill = GridBagConstraints.HORIZONTAL;

        JPanel panelIdEquipaje = new JPanel(new BorderLayout(2, 0));
        panelIdEquipaje.setOpaque(false);
        txtListaEquipajeId = new JTextField("EQ104", 5);
        estilizarCampoTexto(txtListaEquipajeId);

        JPanel pAccionesIdEq = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        pAccionesIdEq.setOpaque(false);

        JButton botonGenIdEq = new JButton("Gen");
        estilizarBotonAccion(botonGenIdEq, new Color(24, 34, 50), azulCyan);
        botonGenIdEq.setMargin(new Insets(1, 4, 1, 4));
        botonGenIdEq.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonGenIdEq.addActionListener(e -> generarIdEquipaje());

        JButton botonAutoEq = new JButton("Auto");
        estilizarBotonAccion(botonAutoEq, new Color(20, 40, 70), doradoAlerta);
        botonAutoEq.setMargin(new Insets(1, 4, 1, 4));
        botonAutoEq.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonAutoEq.setToolTipText("Autocompletar datos de maleta para registro rápido");
        botonAutoEq.addActionListener(e -> autocompletarEquipajeAdmin());

        pAccionesIdEq.add(botonGenIdEq);
        pAccionesIdEq.add(botonAutoEq);

        panelIdEquipaje.add(txtListaEquipajeId, BorderLayout.CENTER);
        panelIdEquipaje.add(pAccionesIdEq, BorderLayout.EAST);

        txtListaEquipajeDni = new JTextField("", 8);
        txtListaEquipajeDni.setToolTipText("DNI del pasajero");
        estilizarCampoTexto(txtListaEquipajeDni);
        cbListaEquipajeVuelo = new JComboBox<>(new String[]{"CP-2023", "CP-2024", "CP-2025", "CP-2026", "CP-2027"});
        estilizarCombo(cbListaEquipajeVuelo);
        txtListaEquipajePeso = new JTextField("18.5", 5);
        estilizarCampoTexto(txtListaEquipajePeso);
        cbListaEquipajeTipo = new JComboBox<>(new String[]{"Bodega", "Cabina", "Especial"});
        estilizarCombo(cbListaEquipajeTipo);

        txtListaEquipajeNuevoPeso = new JTextField("22.0", 5);
        estilizarCampoTexto(txtListaEquipajeNuevoPeso);
        cbListaEquipajeNuevoTipo = new JComboBox<>(new String[]{"Bodega", "Cabina", "Especial"});
        estilizarCombo(cbListaEquipajeNuevoTipo);

        // Fila 0
        r.gridx = 0; r.gridy = 0; panelFormulario.add(crearEtiquetaFormulario("ID Maleta:"), r);
        r.gridx = 1; r.gridy = 0; panelFormulario.add(panelIdEquipaje, r);
        r.gridx = 2; r.gridy = 0; panelFormulario.add(crearEtiquetaFormulario("DNI Pasajero:"), r);
        r.gridx = 3; r.gridy = 0; panelFormulario.add(txtListaEquipajeDni, r);
        r.gridx = 4; r.gridy = 0; panelFormulario.add(crearEtiquetaFormulario("Vuelo:"), r);
        r.gridx = 5; r.gridy = 0; panelFormulario.add(cbListaEquipajeVuelo, r);

        // Fila 1
        r.gridx = 0; r.gridy = 1; panelFormulario.add(crearEtiquetaFormulario("Peso (kg):"), r);
        r.gridx = 1; r.gridy = 1; panelFormulario.add(txtListaEquipajePeso, r);
        r.gridx = 2; r.gridy = 1; panelFormulario.add(crearEtiquetaFormulario("Tipo:"), r);
        r.gridx = 3; r.gridy = 1; panelFormulario.add(cbListaEquipajeTipo, r);
        r.gridx = 4; r.gridy = 1; panelFormulario.add(crearEtiquetaFormulario("Nuevo peso:"), r);
        r.gridx = 5; r.gridy = 1; panelFormulario.add(txtListaEquipajeNuevoPeso, r);

        // Medidor de peso dinámico interactivo
        JPanel panelMedidorPeso = new JPanel(new BorderLayout(4, 2));
        panelMedidorPeso.setOpaque(false);
        barraPesoEquipaje = new JProgressBar(0, 32);
        barraPesoEquipaje.setValue(18);
        barraPesoEquipaje.setStringPainted(true);
        barraPesoEquipaje.setString("Peso de maleta: 18.5 kg (Dentro del límite IATA)");
        barraPesoEquipaje.setForeground(verdeLibre);
        barraPesoEquipaje.setBackground(fondoEntrada);
        barraPesoEquipaje.setBorder(new LineBorder(bordeTarjeta, 1));

        lblAlertaSobrepeso = new JLabel("Límite IATA: 23.0 kg máximo sin recargo", SwingConstants.CENTER);
        lblAlertaSobrepeso.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblAlertaSobrepeso.setForeground(textoGris);
        panelMedidorPeso.add(barraPesoEquipaje, BorderLayout.CENTER);
        panelMedidorPeso.add(lblAlertaSobrepeso, BorderLayout.SOUTH);

        r.gridx = 0; r.gridy = 2; r.gridwidth = 6;
        panelFormulario.add(panelMedidorPeso, r);

        // Panel de Botones de Equipaje Organizado en 2 Apartados GlassPanel
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 8, 0));
        panelBotones.setOpaque(false);

        JButton botonAgregar = new JButton("Agregar");
        estilizarBotonAccion(botonAgregar, azulAccion, new Color(15, 20, 29));
        botonAgregar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonAgregar.setMargin(new Insets(3, 7, 3, 7));
        botonAgregar.addActionListener(e -> accionListaAgregarMaleta());

        JButton botonBuscar = new JButton("Buscar");
        estilizarBotonAccion(botonBuscar, new Color(24, 34, 50), textoBlanco);
        botonBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        botonBuscar.setMargin(new Insets(3, 7, 3, 7));
        botonBuscar.addActionListener(e -> accionListaBuscarMaleta());

        JButton botonEliminar = new JButton("Eliminar");
        estilizarBotonAccion(botonEliminar, rojoBoton, Color.WHITE);
        botonEliminar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonEliminar.setMargin(new Insets(3, 7, 3, 7));
        botonEliminar.addActionListener(e -> accionListaEliminarMaleta());

        JButton botonActualizar = new JButton("Actualizar");
        estilizarBotonAccion(botonActualizar, new Color(24, 34, 50), textoBlanco);
        botonActualizar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        botonActualizar.setMargin(new Insets(3, 7, 3, 7));
        botonActualizar.addActionListener(e -> accionListaActualizarMaleta());

        JButton botonListar = new JButton("Listar Nodos");
        estilizarBotonAccion(botonListar, new Color(24, 34, 50), azulCyan);
        botonListar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        botonListar.setMargin(new Insets(3, 7, 3, 7));
        botonListar.addActionListener(e -> accionListaListarMaletas());

        JButton botonOrdenar = new JButton("Ordenar por Peso");
        estilizarBotonAccion(botonOrdenar, new Color(24, 34, 50), textoBlanco);
        botonOrdenar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        botonOrdenar.setMargin(new Insets(3, 7, 3, 7));
        botonOrdenar.addActionListener(e -> accionListaOrdenarPorPeso());

        JButton botonSobrepeso = new JButton("Control Sobrepeso");
        estilizarBotonAccion(botonSobrepeso, new Color(24, 34, 50), doradoAlerta);
        botonSobrepeso.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        botonSobrepeso.setMargin(new Insets(3, 7, 3, 7));
        botonSobrepeso.addActionListener(e -> accionControlSobrepeso());

        // Apartado 1: Operaciones CRUD
        JPanel pContenidoCrudEq = new JPanel(new GridLayout(0, 1, 0, 4));
        pContenidoCrudEq.setOpaque(false);
        pContenidoCrudEq.add(botonAgregar);
        pContenidoCrudEq.add(botonActualizar);
        pContenidoCrudEq.add(botonEliminar);
        GlassPanel cardCrudEq = GlassPanel.crearApartado("Operaciones Lista Simple", "Inserción y mutación", azulAccion, pContenidoCrudEq);

        // Apartado 2: Búsqueda y Algoritmos
        JPanel pContenidoAlgEq = new JPanel(new GridLayout(0, 1, 0, 4));
        pContenidoAlgEq.setOpaque(false);
        pContenidoAlgEq.add(botonBuscar);
        pContenidoAlgEq.add(botonListar);
        pContenidoAlgEq.add(botonOrdenar);
        pContenidoAlgEq.add(botonSobrepeso);
        GlassPanel cardAlgEq = GlassPanel.crearApartado("Búsqueda y Algoritmos", "Inspección y control IATA", doradoAlerta, pContenidoAlgEq);

        panelBotones.add(cardCrudEq);
        panelBotones.add(cardAlgEq);

        r.gridx = 0; r.gridy = 3; r.gridwidth = 6;
        panelFormulario.add(panelBotones, r);

        txtResultadoEquipaje = new JTextArea(10, 40);
        txtResultadoEquipaje.setFont(new Font("Consolas", Font.PLAIN, 11));
        txtResultadoEquipaje.setEditable(false);
        txtResultadoEquipaje.setBackground(fondoEntrada);
        txtResultadoEquipaje.setForeground(new Color(203, 213, 225));
        txtResultadoEquipaje.setText("Área de resultados para Lista Enlazada Simple de Equipaje.\n");

        JScrollPane panelScrollEq = new JScrollPane(txtResultadoEquipaje);
        panelScrollEq.setBackground(fondoEntrada);
        panelScrollEq.getViewport().setBackground(fondoEntrada);
        panelScrollEq.setBorder(crearBordeTituloDark("Registro Detallado de Equipaje"));

        JPanel panelCentro = new JPanel(new BorderLayout(0, 6));
        panelCentro.setOpaque(false);
        panelCentro.add(panelFormulario, BorderLayout.NORTH);
        panelCentro.add(panelScrollEq, BorderLayout.CENTER);

        panelEquipaje.add(panelCentro, BorderLayout.CENTER);
        return panelEquipaje;
    }

    // Gestion de Rutas (Lista Doblemente Enlazada)
    private JPanel crearPanelGestionRutas() {
        JPanel panelRutas = new JPanel(new BorderLayout(0, 8));
        panelRutas.setBackground(fondoGris);
        panelRutas.setBorder(new EmptyBorder(10, 8, 10, 12));

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setOpaque(false);

        JLabel lblTitulo = new JLabel("Gestión de Rutas Comerciales");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(textoBlanco);

        lblTamanoRutas = new JLabel("Rutas: 0");
        lblTamanoRutas.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTamanoRutas.setForeground(textoGris);

        panelSuperior.add(lblTitulo, BorderLayout.WEST);
        panelSuperior.add(lblTamanoRutas, BorderLayout.EAST);
        panelRutas.add(panelSuperior, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setOpaque(false);
        panelFormulario.setBorder(crearBordeTituloDark("Registro y Modificación de Rutas"));

        GridBagConstraints r = new GridBagConstraints();
        r.insets = new Insets(3, 4, 3, 4);
        r.fill = GridBagConstraints.HORIZONTAL;

        JPanel panelIdRuta = new JPanel(new BorderLayout(2, 0));
        panelIdRuta.setOpaque(false);
        txtListaRutaId = new JTextField("RT305", 5);
        estilizarCampoTexto(txtListaRutaId);

        JPanel pAccionesIdRuta = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        pAccionesIdRuta.setOpaque(false);

        JButton botonGenIdRuta = new JButton("Gen");
        estilizarBotonAccion(botonGenIdRuta, new Color(24, 34, 50), azulCyan);
        botonGenIdRuta.setMargin(new Insets(1, 4, 1, 4));
        botonGenIdRuta.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonGenIdRuta.addActionListener(e -> generarIdRuta());

        JButton botonAutoRuta = new JButton("Auto");
        estilizarBotonAccion(botonAutoRuta, new Color(20, 40, 70), doradoAlerta);
        botonAutoRuta.setMargin(new Insets(1, 4, 1, 4));
        botonAutoRuta.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonAutoRuta.setToolTipText("Autocompletar datos de ruta para registro rápido");
        botonAutoRuta.addActionListener(e -> autocompletarRutaAdmin());

        pAccionesIdRuta.add(botonGenIdRuta);
        pAccionesIdRuta.add(botonAutoRuta);

        panelIdRuta.add(txtListaRutaId, BorderLayout.CENTER);
        panelIdRuta.add(pAccionesIdRuta, BorderLayout.EAST);

        cbListaRutaOrigen = new JComboBox<>(new String[]{"Lima", "Cusco", "Arequipa", "Piura", "Iquitos", "Tarapoto", "Trujillo"});
        estilizarCombo(cbListaRutaOrigen);
        cbListaRutaDestino = new JComboBox<>(new String[]{"Trujillo", "Cusco", "Arequipa", "Piura", "Iquitos", "Tarapoto", "Chiclayo"});
        estilizarCombo(cbListaRutaDestino);
        txtListaRutaDistancia = new JTextField("580.0", 5);
        estilizarCampoTexto(txtListaRutaDistancia);

        r.gridx = 0; r.gridy = 0; panelFormulario.add(crearEtiquetaFormulario("ID Ruta:"), r);
        r.gridx = 1; r.gridy = 0; panelFormulario.add(panelIdRuta, r);
        r.gridx = 2; r.gridy = 0; panelFormulario.add(crearEtiquetaFormulario("Origen:"), r);
        r.gridx = 3; r.gridy = 0; panelFormulario.add(cbListaRutaOrigen, r);

        r.gridx = 0; r.gridy = 1; panelFormulario.add(crearEtiquetaFormulario("Destino:"), r);
        r.gridx = 1; r.gridy = 1; panelFormulario.add(cbListaRutaDestino, r);
        r.gridx = 2; r.gridy = 1; panelFormulario.add(crearEtiquetaFormulario("Distancia (km):"), r);
        r.gridx = 3; r.gridy = 1; panelFormulario.add(txtListaRutaDistancia, r);

        // Panel de Botones de Rutas Organizado en 2 Apartados GlassPanel
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 8, 0));
        panelBotones.setOpaque(false);

        JButton botonAgregar = new JButton("Agregar");
        estilizarBotonAccion(botonAgregar, azulAccion, new Color(15, 20, 29));
        botonAgregar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonAgregar.setMargin(new Insets(3, 7, 3, 7));
        botonAgregar.addActionListener(e -> accionListaAgregarRuta());

        JButton botonBuscar = new JButton("Buscar");
        estilizarBotonAccion(botonBuscar, new Color(24, 34, 50), textoBlanco);
        botonBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        botonBuscar.setMargin(new Insets(3, 7, 3, 7));
        botonBuscar.addActionListener(e -> accionListaBuscarRuta());

        JButton botonEliminar = new JButton("Eliminar");
        estilizarBotonAccion(botonEliminar, rojoBoton, Color.WHITE);
        botonEliminar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        botonEliminar.setMargin(new Insets(3, 7, 3, 7));
        botonEliminar.addActionListener(e -> accionListaEliminarRuta());

        JButton botonActualizar = new JButton("Actualizar");
        estilizarBotonAccion(botonActualizar, new Color(24, 34, 50), textoBlanco);
        botonActualizar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        botonActualizar.setMargin(new Insets(3, 7, 3, 7));
        botonActualizar.addActionListener(e -> accionListaActualizarRuta());

        JButton botonRecAdelante = new JButton("Recorrer Adelante");
        estilizarBotonAccion(botonRecAdelante, new Color(24, 34, 50), azulCyan);
        botonRecAdelante.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        botonRecAdelante.setMargin(new Insets(3, 7, 3, 7));
        botonRecAdelante.addActionListener(e -> accionListaRecorrerRutasAdelante());

        JButton botonRecAtras = new JButton("Recorrer Atrás");
        estilizarBotonAccion(botonRecAtras, new Color(24, 34, 50), azulCyan);
        botonRecAtras.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        botonRecAtras.setMargin(new Insets(3, 7, 3, 7));
        botonRecAtras.addActionListener(e -> accionListaRecorrerRutasAtras());

        // Apartado 1: Operaciones CRUD - Botones en columna vertical
        JPanel pContenidoCrudRt = new JPanel(new GridLayout(0, 1, 0, 4));
        pContenidoCrudRt.setOpaque(false);
        pContenidoCrudRt.add(botonAgregar);
        pContenidoCrudRt.add(botonActualizar);
        pContenidoCrudRt.add(botonEliminar);
        GlassPanel cardCrudRt = GlassPanel.crearApartado("Operaciones Lista Doble", "Inserción y mutación", azulAccion, pContenidoCrudRt);

        // Apartado 2: Recorrido Bidireccional - Botones en columna vertical
        JPanel pContenidoRecRt = new JPanel(new GridLayout(0, 1, 0, 4));
        pContenidoRecRt.setOpaque(false);
        pContenidoRecRt.add(botonBuscar);
        pContenidoRecRt.add(botonRecAdelante);
        pContenidoRecRt.add(botonRecAtras);
        GlassPanel cardRecRt = GlassPanel.crearApartado("Búsqueda y Recorrido", "Punteros previo y siguiente", azulCyan, pContenidoRecRt);

        panelBotones.add(cardCrudRt);
        panelBotones.add(cardRecRt);

        r.gridx = 0; r.gridy = 2; r.gridwidth = 4;
        panelFormulario.add(panelBotones, r);

        // Subpanel Navegador Interactivo de Lista Doble
        JPanel panelNavegador = new JPanel(new BorderLayout(8, 4));
        panelNavegador.setBackground(fondoTarjeta);
        panelNavegador.setBorder(crearBordeTituloDark("Navegación Bidireccional Paso a Paso - Punteros Anterior y Siguiente"));

        // Panel Izquierdo: Retroceso y Salto a Cabeza
        JPanel pControlesOeste = new JPanel(new GridLayout(2, 1, 0, 4));
        pControlesOeste.setOpaque(false);

        JButton btnAnt = new JButton("<< Ruta Previa");
        estilizarBotonAccion(btnAnt, new Color(24, 34, 50), azulCyan);
        btnAnt.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnAnt.setToolTipText("Retroceder al nodo anterior (puntero nodo.anterior)");
        btnAnt.addActionListener(e -> accionNavegarRutaAnterior());

        JButton btnPrimero = new JButton("|<< Primero (Cabeza)");
        estilizarBotonAccion(btnPrimero, new Color(20, 30, 45), textoBlanco);
        btnPrimero.setFont(new Font("Segoe UI", Font.BOLD, 9));
        btnPrimero.setToolTipText("Saltar directamente a la cabeza de la Lista Doble");
        btnPrimero.addActionListener(e -> accionNavegarRutaPrimero());

        pControlesOeste.add(btnAnt);
        pControlesOeste.add(btnPrimero);

        // Panel Derecho: Avance y Salto a Cola
        JPanel pControlesEste = new JPanel(new GridLayout(2, 1, 0, 4));
        pControlesEste.setOpaque(false);

        JButton btnSig = new JButton("Ruta Siguiente >>");
        estilizarBotonAccion(btnSig, new Color(24, 34, 50), azulCyan);
        btnSig.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnSig.setToolTipText("Avanzar al nodo siguiente (puntero nodo.siguiente)");
        btnSig.addActionListener(e -> accionNavegarRutaSiguiente());

        JButton btnUltimo = new JButton("Ultimo (Cola) >>|");
        estilizarBotonAccion(btnUltimo, new Color(20, 30, 45), textoBlanco);
        btnUltimo.setFont(new Font("Segoe UI", Font.BOLD, 9));
        btnUltimo.setToolTipText("Saltar directamente a la cola de la Lista Doble");
        btnUltimo.addActionListener(e -> accionNavegarRutaUltimo());

        pControlesEste.add(btnSig);
        pControlesEste.add(btnUltimo);

        JPanel panelInfoNav = new JPanel(new GridLayout(4, 1, 0, 2));
        panelInfoNav.setOpaque(false);

        JPanel panelItinerarioVisual = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        panelItinerarioVisual.setOpaque(false);
        lblRutaIataOrigen = new JLabel("LIM - Lima");
        lblRutaIataOrigen.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRutaIataOrigen.setForeground(azulCyan);

        JLabel flechaRuta = new JLabel(" =======> ");
        flechaRuta.setFont(new Font("Consolas", Font.BOLD, 12));
        flechaRuta.setForeground(doradoAlerta);

        lblRutaIataDestino = new JLabel("CUZ - Cusco");
        lblRutaIataDestino.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRutaIataDestino.setForeground(azulCyan);

        panelItinerarioVisual.add(lblRutaIataOrigen);
        panelItinerarioVisual.add(flechaRuta);
        panelItinerarioVisual.add(lblRutaIataDestino);

        lblRutaDistanciaKm = new JLabel("Distancia: 580.0 km | Duracion: 80 min aprox.", SwingConstants.CENTER);
        lblRutaDistanciaKm.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRutaDistanciaKm.setForeground(textoBlanco);

        lblRutaPunteros = new JLabel("[ Anterior ] <=== [ ACTUAL ] ===> [ Siguiente ]", SwingConstants.CENTER);
        lblRutaPunteros.setFont(new Font("Consolas", Font.BOLD, 10));
        lblRutaPunteros.setForeground(new Color(148, 163, 184));

        JPanel pAccionDemoRutas = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        pAccionDemoRutas.setOpaque(false);
        JButton btnCargarRutasDemo = new JButton("Cargar Rutas Troncales (Demo)");
        estilizarBotonAccion(btnCargarRutasDemo, new Color(20, 40, 30), verdeLibre);
        btnCargarRutasDemo.setFont(new Font("Segoe UI", Font.BOLD, 9));
        btnCargarRutasDemo.addActionListener(e -> accionCargarRutasTroncalesDemo());
        pAccionDemoRutas.add(btnCargarRutasDemo);

        panelInfoNav.add(panelItinerarioVisual);
        panelInfoNav.add(lblRutaDistanciaKm);
        panelInfoNav.add(lblRutaPunteros);
        panelInfoNav.add(pAccionDemoRutas);

        panelNavegador.add(pControlesOeste, BorderLayout.WEST);
        panelNavegador.add(panelInfoNav, BorderLayout.CENTER);
        panelNavegador.add(pControlesEste, BorderLayout.EAST);

        txtResultadoRutas = new JTextArea(8, 40);
        txtResultadoRutas.setFont(new Font("Consolas", Font.PLAIN, 11));
        txtResultadoRutas.setEditable(false);
        txtResultadoRutas.setBackground(fondoEntrada);
        txtResultadoRutas.setForeground(new Color(203, 213, 225));
        txtResultadoRutas.setText("Área de resultados para Lista Doblemente Enlazada de Rutas.\n");

        JScrollPane panelScrollRutas = new JScrollPane(txtResultadoRutas);
        panelScrollRutas.setBackground(fondoEntrada);
        panelScrollRutas.getViewport().setBackground(fondoEntrada);
        panelScrollRutas.setBorder(crearBordeTituloDark("Nodos y Recorridos de la Lista Doble"));

        JPanel panelCentro = new JPanel(new BorderLayout(0, 4));
        panelCentro.setOpaque(false);
        panelCentro.add(panelFormulario, BorderLayout.NORTH);
        panelCentro.add(panelNavegador, BorderLayout.CENTER);
        panelCentro.add(panelScrollRutas, BorderLayout.SOUTH);

        panelRutas.add(panelCentro, BorderLayout.CENTER);
        return panelRutas;
    }

    // Modulo de Puertas de Embarque (Lista Circular)
    private JPanel crearPanelModuloPuertas() {
        JPanel panelPuertas = new JPanel(new BorderLayout(0, 10));
        panelPuertas.setBackground(fondoGris);
        panelPuertas.setBorder(new EmptyBorder(14, 16, 14, 16));

        JPanel panelSup = new JPanel(new BorderLayout());
        panelSup.setOpaque(false);

        JLabel lblTit = new JLabel("Rotación de Puertas de Embarque");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTit.setForeground(textoBlanco);

        JLabel lblDesc = new JLabel("Asignación continua y rotativa de puertas de salida (Round-Robin donde último nodo apunta a la cabeza)");
        lblDesc.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblDesc.setForeground(textoGris);

        panelSup.add(lblTit, BorderLayout.NORTH);
        panelSup.add(lblDesc, BorderLayout.SOUTH);
        panelPuertas.add(panelSup, BorderLayout.NORTH);

        JPanel panelControl = new JPanel(new GridBagLayout());
        panelControl.setOpaque(false);
        panelControl.setBorder(crearBordeTituloDark("Control de Rotación Circular de Turnos"));

        GridBagConstraints r = new GridBagConstraints();
        r.insets = new Insets(6, 8, 6, 8);
        r.fill = GridBagConstraints.HORIZONTAL;

        lblPuertaActualInfo = new JLabel("Sin puertas cargadas. Presione 'Cargar 5 Puertas Estándar'");
        lblPuertaActualInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPuertaActualInfo.setForeground(new Color(245, 158, 11));

        btnAvanzarTurnoPuerta = new JButton("Avanzar al Siguiente Turno");
        estilizarBotonAccion(btnAvanzarTurnoPuerta, azulAccion, new Color(15, 20, 29));
        btnAvanzarTurnoPuerta.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnAvanzarTurnoPuerta.setToolTipText("Rotación circular O(1) hacia la siguiente puerta");
        btnAvanzarTurnoPuerta.setEnabled(false);
        btnAvanzarTurnoPuerta.addActionListener(e -> accionAvanzarPuertaCircular());

        cbVueloAsignarPuerta = new JComboBox<>();
        estilizarCombo(cbVueloAsignarPuerta);
        btnAsignarVueloPuerta = new JButton("Asignar Vuelo a Puerta Activa");
        estilizarBotonAccion(btnAsignarVueloPuerta, new Color(24, 34, 50), textoBlanco);
        btnAsignarVueloPuerta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnAsignarVueloPuerta.setToolTipText("Asignación directa O(1) sobre la puerta activa en turno");
        btnAsignarVueloPuerta.setEnabled(false);
        btnAsignarVueloPuerta.addActionListener(e -> accionAsignarVueloAPuerta());

        JButton btnCargarPuertas = new JButton("Cargar 5 Puertas Estándar");
        estilizarBotonAccion(btnCargarPuertas, new Color(24, 34, 50), azulCyan);
        btnCargarPuertas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnCargarPuertas.setToolTipText("Inicialización O(1) de lista circular con 5 puertas estándar");
        btnCargarPuertas.addActionListener(e -> {
            controlador.cargarPuertasEstandar();
            this.listaPuertas = controlador.obtenerListaPuertas();
            actualizarVistaPuertas();
            actualizarMetricasDashboard();
            actualizarMetricasSQLTab();
            log("[PUERTAS] 5 puertas de embarque estándar inicializadas en Lista Circular.");
            JOptionPane.showMessageDialog(this, "5 puertas estándar cargadas (Puerta 01 a Puerta 05).", "Puertas de Embarque", JOptionPane.INFORMATION_MESSAGE);
        });

        // Apartados de GlassPanel para Rotación y Asignación - Botones en columna vertical
        JPanel pContenidoRot = new JPanel(new GridLayout(0, 1, 0, 4));
        pContenidoRot.setOpaque(false);
        pContenidoRot.add(btnAvanzarTurnoPuerta);
        pContenidoRot.add(btnCargarPuertas);
        GlassPanel cardRotacion = GlassPanel.crearApartado("Rotación Circular O(1)", "Round-Robin de turnos", azulAccion, pContenidoRot);

        JPanel pContenidoAsigPta = new JPanel(new GridLayout(0, 1, 0, 4));
        pContenidoAsigPta.setOpaque(false);
        JPanel filaComboPuerta = new JPanel(new BorderLayout(6, 0));
        filaComboPuerta.setOpaque(false);
        filaComboPuerta.add(crearEtiquetaFormulario("Vuelo:"), BorderLayout.WEST);
        filaComboPuerta.add(cbVueloAsignarPuerta, BorderLayout.CENTER);
        pContenidoAsigPta.add(filaComboPuerta);
        pContenidoAsigPta.add(btnAsignarVueloPuerta);
        GlassPanel cardAsignarPta = GlassPanel.crearApartado("Asignación a Puerta", "Vinculación operativa", Theme.getSuccess(), pContenidoAsigPta);

        JPanel panelApartadosPuertas = new JPanel(new GridLayout(1, 2, 10, 0));
        panelApartadosPuertas.setOpaque(false);
        panelApartadosPuertas.add(cardRotacion);
        panelApartadosPuertas.add(cardAsignarPta);

        r.gridx = 0; r.gridy = 0; r.gridwidth = 4; panelControl.add(lblPuertaActualInfo, r);
        r.gridx = 0; r.gridy = 1; r.gridwidth = 4; panelControl.add(panelApartadosPuertas, r);

        // Visualizador tipo radar de puertas de embarque
        panelVisualGates = new JPanel(new GridLayout(1, 5, 8, 0));
        panelVisualGates.setOpaque(false);
        panelVisualGates.setBorder(new EmptyBorder(6, 0, 6, 0));
        actualizarTarjetasVisualesGates();

        txtResultadoPuertas = new JTextArea(12, 60);
        txtResultadoPuertas.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtResultadoPuertas.setEditable(false);
        txtResultadoPuertas.setBackground(fondoEntrada);
        txtResultadoPuertas.setForeground(new Color(203, 213, 225));

        JScrollPane scrollPuertas = new JScrollPane(txtResultadoPuertas);
        scrollPuertas.setBackground(fondoEntrada);
        scrollPuertas.getViewport().setBackground(fondoEntrada);
        scrollPuertas.setBorder(crearBordeTituloDark("Ciclo de Puertas en Memoria (Último nodo apunta a Cabeza)"));

        JPanel panelCentroPuertas = new JPanel(new BorderLayout(0, 8));
        panelCentroPuertas.setOpaque(false);

        JPanel panelNorteCentro = new JPanel(new BorderLayout(0, 6));
        panelNorteCentro.setOpaque(false);
        panelNorteCentro.add(panelControl, BorderLayout.NORTH);
        panelNorteCentro.add(panelVisualGates, BorderLayout.SOUTH);

        panelCentroPuertas.add(panelNorteCentro, BorderLayout.NORTH);
        panelCentroPuertas.add(scrollPuertas, BorderLayout.CENTER);

        panelPuertas.add(panelCentroPuertas, BorderLayout.CENTER);
        panelPuertas.add(crearPanelColaPistaDespegue(), BorderLayout.SOUTH);
        return panelPuertas;
    }

    private JPanel crearPanelColaPistaDespegue() {
        JPanel pCola = new JPanel(new BorderLayout(8, 6));
        pCola.setOpaque(false);
        pCola.setBorder(crearBordeTituloDark("Torre de Control: Secuencia de Despegue en Pista"));

        JPanel pInfo = new JPanel(new BorderLayout(8, 0));
        pInfo.setOpaque(false);

        lblFrenteColaPista = new JLabel("Próxima aeronave en pista: Sin aeronaves en espera");
        lblFrenteColaPista.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFrenteColaPista.setForeground(doradoAlerta);
        pInfo.add(lblFrenteColaPista, BorderLayout.CENTER);

        // Panel de Acciones de Pista Organizado en 2 Apartados GlassPanel
        JPanel pAcciones = new JPanel(new GridLayout(1, 2, 8, 0));
        pAcciones.setOpaque(false);

        JButton btnEncolar = new JButton("Encolar Regular");
        estilizarBotonAccion(btnEncolar, new Color(24, 34, 50), azulCyan);
        btnEncolar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnEncolar.addActionListener(e -> accionEncolarVueloPista());

        JButton btnEncolarEmergencia = new JButton("Encolar Emergencia (Prioridad 1)");
        estilizarBotonAccion(btnEncolarEmergencia, new Color(54, 24, 28), new Color(255, 95, 105));
        btnEncolarEmergencia.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnEncolarEmergencia.addActionListener(e -> accionEncolarVueloPistaEmergencia());

        JButton btnDespachar = new JButton("Autorizar Despegue");
        estilizarBotonAccion(btnDespachar, verdeLibre, Color.WHITE);
        btnDespachar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnDespachar.addActionListener(e -> accionDespacharVueloPista());

        JButton btnVerCola = new JButton("Inspeccionar Cola");
        estilizarBotonAccion(btnVerCola, new Color(24, 34, 50), textoBlanco);
        btnVerCola.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        btnVerCola.addActionListener(e -> accionInspeccionarColaPista());

        // Apartado 1: Ingreso a Pista - Botones en columna vertical
        JPanel pContenidoIngreso = new JPanel(new GridLayout(0, 1, 0, 4));
        pContenidoIngreso.setOpaque(false);
        pContenidoIngreso.add(btnEncolar);
        pContenidoIngreso.add(btnEncolarEmergencia);
        GlassPanel cardIngreso = GlassPanel.crearApartado("Ingreso a Pista", "Encolado estándar y emergencia", azulCyan, pContenidoIngreso);

        // Apartado 2: Despacho y Monitoreo ATC - Botones en columna vertical
        JPanel pContenidoDespacho = new JPanel(new GridLayout(0, 1, 0, 4));
        pContenidoDespacho.setOpaque(false);
        pContenidoDespacho.add(btnDespachar);
        pContenidoDespacho.add(btnVerCola);
        GlassPanel cardDespacho = GlassPanel.crearApartado("Torre ATC - Despacho", "Prelación de despegue", verdeLibre, pContenidoDespacho);

        pAcciones.add(cardIngreso);
        pAcciones.add(cardDespacho);

        pCola.add(pInfo, BorderLayout.CENTER);
        pCola.add(pAcciones, BorderLayout.SOUTH);

        actualizarVistaColaPista();
        return pCola;
    }

    private void accionEncolarVueloPista() {
        if (!verificarPermisoAdmin("Encolar aeronave a pista de despegue")) return;
        String cod = (cbVueloAsignarPuerta != null) ? (String) cbVueloAsignarPuerta.getSelectedItem() : null;
        if (cod == null || cod.trim().isEmpty()) {
            if (controlador.obtenerContadorVuelos() > 0) {
                cod = controlador.obtenerVuelos()[0].obtenerCodigo();
            } else {
                JOptionPane.showMessageDialog(this, "No hay vuelos disponibles para encolar.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        boolean ok = controlador.encolarVueloPista(cod);
        if (ok) {
            actualizarVistaColaPista();
            log("[COLA FIFO] Aeronave del vuelo " + cod + " encolada en cabecera de pista para despegue.");
            JOptionPane.showMessageDialog(this,
                    "Vuelo " + cod + " agregado a la Cola de Espera.\n" +
                    "Disciplina de atención: Orden cronológico de llegada a pista.",
                    "Encolado Exitoso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void accionEncolarVueloPistaEmergencia() {
        if (!verificarPermisoAdmin("Encolar aeronave con prioridad de emergencia")) return;
        String cod = (cbVueloAsignarPuerta != null) ? (String) cbVueloAsignarPuerta.getSelectedItem() : null;
        if (cod == null || cod.trim().isEmpty()) {
            if (controlador.obtenerContadorVuelos() > 0) {
                cod = controlador.obtenerVuelos()[0].obtenerCodigo();
            } else {
                JOptionPane.showMessageDialog(this, "No hay vuelos disponibles para encolar.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        boolean ok = controlador.encolarVueloPistaEmergencia(cod);
        if (ok) {
            actualizarVistaColaPista();
            log("[COLA PRIORIDAD ATC] ¡ALERTA! Vuelo " + cod + " encolado con MÁXIMA PRIORIDAD (Emergencia) en pista.");
            JOptionPane.showMessageDialog(this,
                    "===============================================================\n" +
                    "   ALERTA ATC: ENCOLADO CON MAXIMA PRIORIDAD (EMERGENCIA)      \n" +
                    "===============================================================\n\n" +
                    "- Vuelo de Emergencia: " + cod + "\n" +
                    "- Nivel de Prioridad: PRIORIDAD 1 (Maxima Prelacion ATC / Auxilio Medico)\n" +
                    "- Comportamiento Algoritmico: Se posiciona al frente de la cola de pista,\n" +
                    "  desplazando a las aeronaves con prioridad regular (FIFO estandar).\n\n" +
                    "La aeronave ha tomado turno preferencial de despegue inmediato.",
                    "Encolado con Prioridad de Emergencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void accionDespacharVueloPista() {
        if (!verificarPermisoAdmin("Autorizar despegue y despachar aeronave")) return;
        Vuelo v = controlador.despacharSiguienteVueloPista();
        if (v != null) {
            actualizarVistaColaPista();
            log("[COLA] Torre de Control: Vuelo " + v.obtenerCodigo() + " (" + v.obtenerOrigen() + " -> " + v.obtenerDestino() + ") autorizado para despegue.");
            JOptionPane.showMessageDialog(this,
                    "================================================================\n" +
                    "   TORRE DE CONTROL - DESPACHO EXITOSO EN PISTA              \n" +
                    "================================================================\n\n" +
                    "- Vuelo Despachado: " + v.obtenerCodigo() + "\n" +
                    "- Ruta Autorizada: " + v.obtenerOrigen() + " -> " + v.obtenerDestino() + "\n" +
                    "- Operación: Despegue en pista activo (Despacho prioritario)\n\n" +
                    "La aeronave ha sido liberada de la cola y despega según normativa CORPAC.",
                    "Autorización de Despegue", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "La Cola de espera en pista se encuentra vacía.\nNo hay aeronaves esperando turno de despegue.",
                    "Cola Vacía", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void accionInspeccionarColaPista() {
        com.aeropuerto.estructuras.Cola cola = controlador.obtenerColaPista();
        String listado = (cola != null) ? cola.listar() : "Cola no disponible.";
        mostrarDialogoTextoLargo("Inspección del TAD Cola con Prioridad ATC", listado, 620, 380);
    }

    private void actualizarVistaColaPista() {
        if (lblFrenteColaPista == null) return;
        com.aeropuerto.estructuras.Cola cola = controlador.obtenerColaPista();
        if (cola != null && !cola.estaVacia()) {
            Vuelo frente = (Vuelo) cola.frente();
            if (frente != null) {
                int prio = cola.obtenerPrioridadFrente();
                String etiquetaPrio = (prio == 1) ? " [¡MÁXIMA PRIORIDAD - EMERGENCIA!]" :
                                      (prio == 2) ? " [PRIORITARIO]" : " [REGULAR FIFO]";
                lblFrenteColaPista.setText("Próxima aeronave en pista" + etiquetaPrio + ": " + frente.obtenerCodigo() +
                        " (" + frente.obtenerOrigen() + " -> " + frente.obtenerDestino() + ") | Espera: " + cola.obtenerTamano() + " vuelos");
                lblFrenteColaPista.setForeground((prio == 1) ? new Color(255, 95, 105) : verdeLibre);
                return;
            }
        }
        lblFrenteColaPista.setText("Próxima aeronave en pista [Frente]: Sin aeronaves en espera (Pista despejada)");
        lblFrenteColaPista.setForeground(doradoAlerta);
    }

    private void actualizarTarjetasVisualesGates() {
        if (panelVisualGates == null || listaPuertas == null) return;
        panelVisualGates.removeAll();

        if (listaPuertas.estaVacia()) {
            JLabel lblVacio = new JLabel("Sin puertas de embarque registradas. Presione 'Cargar 5 Puertas Estándar' para inicializarlas.", SwingConstants.CENTER);
            lblVacio.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            lblVacio.setForeground(textoGris);
            panelVisualGates.add(lblVacio);
            panelVisualGates.revalidate();
            panelVisualGates.repaint();
            return;
        }

        String[] puertas = {"Puerta 01", "Puerta 02", "Puerta 03", "Puerta 04", "Puerta 05"};
        PuertaEmbarque actual = listaPuertas.obtenerPuertaActual();
        String nomAct = (actual != null) ? actual.obtenerNumeroPuerta() : "";

        for (String p : puertas) {
            PuertaEmbarque pe = listaPuertas.buscarPuerta(p);
            if (pe != null) {
                boolean esActiva = p.equalsIgnoreCase(nomAct);
                JPanel tarjeta = new JPanel(new GridLayout(3, 1));
                tarjeta.setBackground(esActiva ? new Color(20, 36, 56) : fondoTarjeta);
                tarjeta.setBorder(new CompoundBorder(
                        new LineBorder(esActiva ? azulAccion : bordeTarjeta, esActiva ? 2 : 1),
                        new EmptyBorder(6, 6, 6, 6)));

                JLabel l1 = new JLabel(p, SwingConstants.CENTER);
                l1.setFont(new Font("Segoe UI", Font.BOLD, 12));
                l1.setForeground(esActiva ? doradoAlerta : textoBlanco);

                String est = pe.obtenerEstado();
                String vue = pe.obtenerCodigoVuelo();

                JLabel l2 = new JLabel(est, SwingConstants.CENTER);
                l2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                l2.setForeground(est.equalsIgnoreCase("Ocupada") ? rojoOcupado : verdeLibre);

                JLabel l3 = new JLabel("Vuelo: " + vue, SwingConstants.CENTER);
                l3.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                l3.setForeground(azulCyan);

                tarjeta.add(l1);
                tarjeta.add(l2);
                tarjeta.add(l3);
                panelVisualGates.add(tarjeta);
            }
        }
        panelVisualGates.revalidate();
        panelVisualGates.repaint();
    }

    // Modulo 4: Control de Acceso y MySQL
    private JPanel crearPanelModuloAdministracion() {
        JPanel panelAdmin = new JPanel(new BorderLayout(12, 12));
        panelAdmin.setBackground(fondoGris);
        panelAdmin.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Subpanel Superior: Perfil y Sesion
        JPanel panelSesion = new JPanel(new BorderLayout(15, 0));
        panelSesion.setBackground(fondoTarjeta);
        panelSesion.setBorder(crearBordeTituloDark("Estado de Sesión y Control de Privilegios"));

        JPanel panelDatosUsuario = new JPanel(new GridLayout(3, 1, 0, 4));
        panelDatosUsuario.setOpaque(false);

        lblAdminUsuarioActivo = new JLabel("Usuario: Invitado");
        lblAdminUsuarioActivo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblAdminUsuarioActivo.setForeground(textoBlanco);

        lblAdminRolActivo = new JLabel("Rol: Modo Consulta (Sin privilegios de edición)");
        lblAdminRolActivo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAdminRolActivo.setForeground(textoGris);

        lblAdminNombreActivo = new JLabel("Nombre: Operador de Consulta / Pasajero");
        lblAdminNombreActivo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAdminNombreActivo.setForeground(textoGris);

        panelDatosUsuario.add(lblAdminUsuarioActivo);
        panelDatosUsuario.add(lblAdminRolActivo);
        panelDatosUsuario.add(lblAdminNombreActivo);
        panelSesion.add(panelDatosUsuario, BorderLayout.CENTER);

        JPanel panelBotoneraSesion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelBotoneraSesion.setOpaque(false);

        JButton btnLoginAdminTab = new JButton("Iniciar Sesión como Administrador");
        btnLoginAdminTab.setFont(new Font("Segoe UI", Font.BOLD, 12));
        estilizarBotonAccion(btnLoginAdminTab, azulAccion, new Color(15, 20, 29));
        btnLoginAdminTab.addActionListener(e -> abrirDialogoLoginAdmin());

        JButton btnLogoutAdminTab = new JButton("Cerrar Sesión");
        btnLogoutAdminTab.setFont(new Font("Segoe UI", Font.BOLD, 12));
        estilizarBotonAccion(btnLogoutAdminTab, rojoBoton, Color.WHITE);
        btnLogoutAdminTab.addActionListener(e -> {
            if (controlador.esSesionAdmin()) {
                controlador.cerrarSesion();
                actualizarEstadoSesionUI();
                log("[SEGURIDAD] Sesión cerrada desde el panel de administración.");
                JOptionPane.showMessageDialog(this, "Sesión de Administrador cerrada.", "Sesión Finalizada", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Actualmente se encuentra en modo Consulta.", "Atención", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        panelBotoneraSesion.add(btnLoginAdminTab);
        panelBotoneraSesion.add(btnLogoutAdminTab);
        panelSesion.add(panelBotoneraSesion, BorderLayout.EAST);

        panelAdmin.add(panelSesion, BorderLayout.NORTH);

        // Subpanel Central: Monitoreo de MySQL y Conteo en Tiempo Real
        JPanel panelCentro = new JPanel(new BorderLayout(0, 12));
        panelCentro.setOpaque(false);

        JPanel panelEstadoServidor = new JPanel(new BorderLayout());
        panelEstadoServidor.setBackground(fondoTarjeta);
        panelEstadoServidor.setBorder(crearBordeTituloDark("Servidor de Base de Datos MySQL (localhost:3306 / aeropuerto_db)"));

        lblSqlEstadoServidor = new JLabel("Estado: Verificando conexión...");
        lblSqlEstadoServidor.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelEstadoServidor.add(lblSqlEstadoServidor, BorderLayout.WEST);

        JButton btnRefrescarConteo = new JButton("Actualizar Métricas SQL");
        estilizarBotonAccion(btnRefrescarConteo, new Color(24, 34, 50), azulCyan);
        btnRefrescarConteo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnRefrescarConteo.addActionListener(e -> actualizarMetricasSQLTab());
        panelEstadoServidor.add(btnRefrescarConteo, BorderLayout.EAST);
        panelCentro.add(panelEstadoServidor, BorderLayout.NORTH);

        // Cuadricula con los conteos de las tablas
        JPanel panelCuadriculaTablas = new JPanel(new GridLayout(2, 4, 12, 12));
        panelCuadriculaTablas.setOpaque(false);

        panelCuadriculaTablas.add(crearTarjetaTablaSQL("TABLA: vuelos", lblSqlVuelos = new JLabel("0 registros"), "Arreglo Unidimensional"));
        panelCuadriculaTablas.add(crearTarjetaTablaSQL("TABLA: asientos", lblSqlAsientos = new JLabel("0 ocupados"), "Matriz 10x5 Cabina"));
        panelCuadriculaTablas.add(crearTarjetaTablaSQL("TABLA: equipaje", lblSqlEquipaje = new JLabel("0 maletas"), "TAD Lista Simple"));
        panelCuadriculaTablas.add(crearTarjetaTablaSQL("TABLA: rutas", lblSqlRutas = new JLabel("0 rutas"), "TAD Lista Doble"));
        panelCuadriculaTablas.add(crearTarjetaTablaSQL("TABLA: puertas_embarque", lblSqlPuertas = new JLabel("0 puertas"), "TAD Lista Circular"));
        panelCuadriculaTablas.add(crearTarjetaTablaSQL("TABLA: aeropuertos", lblSqlAeropuertos = new JLabel("0 aeropuertos"), "Control Pistas y Stands"));
        panelCuadriculaTablas.add(crearTarjetaTablaSQL("TABLA: supervisores", lblSqlSupervisores = new JLabel("0 supervisores"), "Control Operaciones ATC"));
        panelCuadriculaTablas.add(crearTarjetaTablaSQL("TABLA: usuarios", lblSqlUsuarios = new JLabel("0 usuarios"), "Seguridad y Roles"));

        panelCentro.add(panelCuadriculaTablas, BorderLayout.CENTER);
        panelAdmin.add(panelCentro, BorderLayout.CENTER);

        // Subpanel Inferior: Botones de Operación Organizados en 3 Apartados GlassPanel Verticales
        JPanel panelAccionesAdmin = new JPanel(new GridLayout(1, 3, 10, 0));
        panelAccionesAdmin.setOpaque(false);
        panelAccionesAdmin.setBorder(new EmptyBorder(6, 0, 4, 0));

        JPanel filaAdmin1 = new JPanel(new GridLayout(0, 1, 0, 4));
        filaAdmin1.setOpaque(false);

        JPanel filaAdmin2 = new JPanel(new GridLayout(0, 1, 0, 4));
        filaAdmin2.setOpaque(false);

        JPanel filaAdmin3 = new JPanel(new GridLayout(0, 1, 0, 4));
        filaAdmin3.setOpaque(false);

        JButton btnProbarMySQL = new JButton("Probar Conexión MySQL");
        estilizarBotonAccion(btnProbarMySQL, new Color(24, 34, 50), azulCyan);
        btnProbarMySQL.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnProbarMySQL.setMargin(new Insets(4, 10, 4, 10));
        btnProbarMySQL.addActionListener(e -> {
            boolean ok = ConexionBD.probarConexionManual();
            actualizarEstadoMySQL();
            actualizarMetricasSQLTab();
            if (ok) {
                JOptionPane.showMessageDialog(this, "Conexión exitosa a MySQL (localhost:3306 / aeropuerto_db).", "Conexión MySQL", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo conectar a MySQL. Verifique que el servicio esté iniciado.", "Sin Conexión", JOptionPane.WARNING_MESSAGE);
            }
        });

        this.btnSincronizarTodo = new JButton("Sincronizar Todo a MySQL");
        this.btnSincronizarTodo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        this.btnSincronizarTodo.setMargin(new Insets(4, 10, 4, 10));
        estilizarBotonAccion(this.btnSincronizarTodo, azulAccion, new Color(15, 20, 29));
        this.btnSincronizarTodo.addActionListener(e -> {
            if (!verificarPermisoAdmin("Sincronizar a MySQL")) return;
            accionSincronizarMySQL();
            actualizarMetricasSQLTab();
        });

        this.btnRecargarTodo = new JButton("Recargar Todo desde MySQL");
        estilizarBotonAccion(this.btnRecargarTodo, new Color(24, 34, 50), textoBlanco);
        this.btnRecargarTodo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        this.btnRecargarTodo.setMargin(new Insets(4, 10, 4, 10));
        this.btnRecargarTodo.addActionListener(e -> {
            accionCargarDesdeMySQL();
            actualizarMetricasSQLTab();
        });

        JButton btnCargarPuertasAdmin = new JButton("Cargar 5 Puertas Estándar");
        estilizarBotonAccion(btnCargarPuertasAdmin, new Color(24, 34, 50), textoBlanco);
        btnCargarPuertasAdmin.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnCargarPuertasAdmin.setMargin(new Insets(4, 10, 4, 10));
        btnCargarPuertasAdmin.addActionListener(e -> {
            controlador.cargarPuertasEstandar();
            this.listaPuertas = controlador.obtenerListaPuertas();
            actualizarVistaPuertas();
            actualizarMetricasDashboard();
            actualizarMetricasSQLTab();
            log("[PUERTAS] 5 puertas de embarque estándar inicializadas en Lista Circular y MySQL.");
            JOptionPane.showMessageDialog(this, "5 puertas estándar cargadas (Puerta 01 a Puerta 05).", "Puertas de Embarque", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnCargarDemo = new JButton("Cargar Datos de Demostración");
        estilizarBotonAccion(btnCargarDemo, new Color(24, 34, 50), doradoAlerta);
        btnCargarDemo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnCargarDemo.setMargin(new Insets(4, 10, 4, 10));
        btnCargarDemo.addActionListener(e -> accionCargarDatosDemostracion());

        JButton btnVaciarTodo = new JButton("Vaciar Todo");
        btnVaciarTodo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnVaciarTodo.setMargin(new Insets(4, 10, 4, 10));
        estilizarBotonAccion(btnVaciarTodo, rojoBoton, Color.WHITE);
        btnVaciarTodo.addActionListener(e -> accionVaciarTodo());

        filaAdmin1.add(btnProbarMySQL);
        filaAdmin1.add(btnSincronizarTodo);
        filaAdmin1.add(btnRecargarTodo);

        filaAdmin2.add(btnCargarPuertasAdmin);
        filaAdmin2.add(btnCargarDemo);
        filaAdmin2.add(btnVaciarTodo);

        JButton btnOptimizarRed = new JButton("Optimización de Red Troncal Nacional (MST Kruskal)");
        estilizarBotonAccion(btnOptimizarRed, new Color(20, 40, 70), new Color(147, 197, 253));
        btnOptimizarRed.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnOptimizarRed.setMargin(new Insets(4, 10, 4, 10));
        btnOptimizarRed.addActionListener(e -> accionOptimizarRedTroncal());

        filaAdmin3.add(btnOptimizarRed);

        GlassPanel cardPersistencia = GlassPanel.crearApartado("Persistencia MySQL", "Sincronizacion y Recarga", azulAccion, filaAdmin1);
        GlassPanel cardMantenimiento = GlassPanel.crearApartado("Inicializacion y Datos", "Carga demo y mantenimiento", doradoAlerta, filaAdmin2);
        GlassPanel cardDiagnostico = GlassPanel.crearApartado("Optimización de Rutas", "Red Troncal de Grafo Kruskal", new Color(52, 211, 153), filaAdmin3);

        panelAccionesAdmin.add(cardPersistencia);
        panelAccionesAdmin.add(cardMantenimiento);
        panelAccionesAdmin.add(cardDiagnostico);

        panelAdmin.add(panelAccionesAdmin, BorderLayout.SOUTH);

        return panelAdmin;
    }

    private JPanel crearTarjetaTablaSQL(String titulo, JLabel etiquetaValor, String tipoEstructura) {
        JPanel p = new JPanel(new GridLayout(3, 1, 0, 3));
        p.setBackground(fondoTarjeta);
        p.setBorder(new CompoundBorder(
                new LineBorder(bordeTarjeta, 1),
                new EmptyBorder(10, 14, 10, 14)));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setForeground(textoGris);

        etiquetaValor.setFont(new Font("Segoe UI", Font.BOLD, 15));
        etiquetaValor.setForeground(textoBlanco);

        JLabel subtitulo = new JLabel(tipoEstructura);
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subtitulo.setForeground(azulCyan);

        p.add(t);
        p.add(etiquetaValor);
        p.add(subtitulo);
        return p;
    }

    public void solicitarRolInicial() {
        DialogoSeleccionRol dialogo = new DialogoSeleccionRol(this, controlador);
        dialogo.setVisible(true);
        configurarModoVista(controlador.esSesionAdmin());
    }

    private void alternarSesionAdmin() {
        solicitarRolInicial();
    }

    public void abrirDialogoLoginAdminPublico() {
        solicitarRolInicial();
    }

    public boolean esModoAdministrador() {
        return controlador != null && controlador.esSesionAdmin();
    }

    private void abrirDialogoLoginAdmin() {
        solicitarRolInicial();
    }

    public void configurarModoVista(boolean esAdmin) {
        if (panelPestanas == null) return;
        panelPestanas.removeAll();
        this.vistaTecnicaActiva = esAdmin;

        if (esAdmin) {
            Theme.setRolActivo("ADMIN");
            if (controlador != null && !controlador.esSesionAdmin()) {
                controlador.autenticarUsuario("admin", "admin123");
            }
            if (stepperPasajero != null) stepperPasajero.setVisible(false);

            // Pestañas integrales de Administrador (Centro de Control AOC - Semana 18)
            panelPestanas.addTab("Radar de Plataforma CORPAC", panelRadarSmartAirport);
            panelPestanas.addTab("Supervisión y Aeropuertos Libres", panelSupervisoresAeropuertos);
            panelPestanas.addTab("Arreglos y Matrices", panelDivisionPrincipal);
            panelPestanas.addTab("Listas Dinámicas", panelDivisionListas);
            panelPestanas.addTab("Puertas de Embarque", panelModuloPuertas);
            panelPestanas.addTab("Árboles (ABB y AVL)", obtenerPanelArboles());
            panelPestanas.addTab("Red de Rutas y Grafos", obtenerPanelGrafo());
            panelPestanas.addTab("Directorio de Pasajeros (Hash y Heap)", obtenerPanelHashHeap());
            panelPestanas.addTab("Benchmarking de Ordenamiento", obtenerPanelBenchmarking());
            panelPestanas.addTab("Control Administrativo y SQL", panelModuloAdministracion);

            // Ajustes visuales de cabecera (Mantener logo CORPAC)
            if (lblInsigniaHeader != null) {
                lblInsigniaHeader.setText("");
                lblInsigniaHeader.setIcon(obtenerIconoEmpresa(95, 24));
            }
            if (lblTituloPrincipalHeader != null) {
                lblTituloPrincipalHeader.setText("CENTRO DE CONTROL ATC");
                lblTituloPrincipalHeader.setFont(FontManager.getBarlowCondensed(Font.BOLD, 17f));
                lblTituloPrincipalHeader.setForeground(Theme.getTexto());
            }
            if (lblTagAocHeader != null) {
                lblTagAocHeader.setText(" AOC OPERATIONS ");
                lblTagAocHeader.setFont(FontManager.getBarlowCondensed(Font.BOLD, 10f));
                lblTagAocHeader.setBackground(Theme.getAdminSoft());
                lblTagAocHeader.setForeground(Theme.getAdminAccent());
                lblTagAocHeader.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.getAdminAccent(), 1),
                        BorderFactory.createEmptyBorder(2, 6, 2, 6)));
            }
            if (lblSubtituloHeader != null) {
                lblSubtituloHeader.setText(" | CORPAC S.A.");
                lblSubtituloHeader.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
                lblSubtituloHeader.setForeground(Theme.getSecundario());
            }
            setTitle("Sistema de Aeropuerto - Centro de Control CORPAC (AOC)");
        } else {
            Theme.setRolActivo("PASAJERO");
            if (stepperPasajero != null) {
                stepperPasajero.setVisible(true);
                stepperPasajero.setPasoActivo(panelPestanas.getSelectedIndex());
            }

            // Modo Pasajero: Garantizar disponibilidad de vuelos comerciales
            controlador.asegurarVuelosComerciales();
            if (controlador.obtenerVueloActivo() == null && controlador.obtenerContadorVuelos() > 0) {
                controlador.seleccionarVueloActivo(controlador.obtenerVuelos()[0].obtenerCodigo());
            }

            actualizarTablaVuelosPasajero();
            actualizarCabinaPasajero();
            actualizarPaseAbordarPasajero();
            actualizarMetricasDashboard();

            // Pestañas exclusivas de Pasajero (Itinerario, Cabina, Equipaje, Pase)
            panelPestanas.addTab("1. Vuelos y Destinos Disponibles", panelPasajeroVuelos);
            panelPestanas.addTab("2. Selección de Asiento y Clase", panelPasajeroCabinaContenedor);
            panelPestanas.addTab("3. Registro de Pasajero y Equipaje", panelPasajeroEquipajeContenedor);
            panelPestanas.addTab("4. Mi Pase de Abordar y Puerta", panelPasajeroPaseAbordar);

            // Ajustes visuales de cabecera (Logo CORPAC y tipografia institucional)
            if (lblInsigniaHeader != null) {
                lblInsigniaHeader.setText("");
                lblInsigniaHeader.setIcon(obtenerIconoEmpresa(95, 24));
            }
            if (lblTituloPrincipalHeader != null) {
                lblTituloPrincipalHeader.setText("PORTAL DEL PASAJERO");
                lblTituloPrincipalHeader.setFont(FontManager.getBarlowCondensed(Font.BOLD, 17f));
                lblTituloPrincipalHeader.setForeground(Theme.getTexto());
            }
            if (lblTagAocHeader != null) {
                lblTagAocHeader.setText(" CHECK-IN ");
                lblTagAocHeader.setFont(FontManager.getBarlowCondensed(Font.BOLD, 10f));
                lblTagAocHeader.setBackground(Theme.getPasajeroSoft());
                lblTagAocHeader.setForeground(Theme.getPasajeroAccent());
                lblTagAocHeader.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.getPasajeroAccent(), 1),
                        BorderFactory.createEmptyBorder(2, 6, 2, 6)));
            }
            if (lblSubtituloHeader != null) {
                lblSubtituloHeader.setText(" | CORPAC S.A.");
                lblSubtituloHeader.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
                lblSubtituloHeader.setForeground(Theme.getSecundario());
            }
            setTitle("Sistema de Aeropuerto - Portal del Pasajero");
        }

        actualizarEstadoSesionUI();
        aplicarModoVistaTecnica();
        actualizarEstadoPestanasPasajero();
        panelPestanas.revalidate();
        panelPestanas.repaint();
    }

    private void actualizarEstadoSesionUI() {
        if (lblEstadoSesion == null || btnSesionAdmin == null) return;
        if (controlador.esSesionAdmin()) {
            Usuario u = controlador.obtenerUsuarioActual();
            String nombre = (u != null) ? u.obtenerUsuario() : "admin";
            lblEstadoSesion.setText(" Administrador: " + nombre + " · Acceso Total ");
            lblEstadoSesion.setIcon(AppIcon.dot(8, Theme.getSuccess()));
            lblEstadoSesion.setBackground(Theme.getSuccessSoft());
            lblEstadoSesion.setForeground(Theme.getSuccess());
            lblEstadoSesion.setPreferredSize(new Dimension(240, 26));
            lblEstadoSesion.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getSuccess(), 1),
                    BorderFactory.createEmptyBorder(3, 8, 3, 8)));

            btnSesionAdmin.setText("Cerrar Sesión / Cambiar Rol");
            btnSesionAdmin.setContentAreaFilled(false);
            btnSesionAdmin.setOpaque(false);
            btnSesionAdmin.setBackground(Theme.getSurface());
            btnSesionAdmin.setForeground(Theme.getDanger());
            btnSesionAdmin.setPreferredSize(new Dimension(175, 26));
            btnSesionAdmin.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getDanger(), 1),
                    BorderFactory.createEmptyBorder(3, 10, 3, 10)));
        } else {
            lblEstadoSesion.setText(vistaTecnicaActiva ? " Modo: Pasajero · Vista Técnica " : " Modo: Pasajero · Vista Simple ");
            lblEstadoSesion.setIcon(null);
            lblEstadoSesion.setBackground(Theme.getInput());
            lblEstadoSesion.setForeground(Theme.getTexto());
            lblEstadoSesion.setPreferredSize(new Dimension(210, 26));
            lblEstadoSesion.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getBorder(), 1),
                    BorderFactory.createEmptyBorder(4, 10, 4, 10)));

            btnSesionAdmin.setText("Acceso Administrador");
            btnSesionAdmin.setContentAreaFilled(true);
            btnSesionAdmin.setOpaque(true);
            btnSesionAdmin.setBackground(Theme.getPasajeroSoft());
            btnSesionAdmin.setForeground(Theme.getPasajeroAccent());
            btnSesionAdmin.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getPasajeroAccent(), 1),
                    BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        }

        actualizarPanelAdminTab();
        if (panelRadarSmartAirport != null) {
            panelRadarSmartAirport.actualizarEstadoPermisos();
        }
        if (panelSupervisoresAeropuertos != null) {
            panelSupervisoresAeropuertos.actualizarEstadoPermisos();
        }
    }

    public void alternarVistaTecnica() {
        this.vistaTecnicaActiva = !this.vistaTecnicaActiva;
        aplicarModoVistaTecnica();
        mostrarToast(vistaTecnicaActiva ? "Vista Técnica activada (TADs y métricas visibles)" : "Modo Limpio activado (Interfaz simplificada)", Theme.getPasajeroAccent());
    }

    public void aplicarModoVistaTecnica() {
        if (panelDashboardKpi != null) {
            panelDashboardKpi.setModoTecnico(vistaTecnicaActiva);
        }
        if (barraEstadoFooter != null) {
            barraEstadoFooter.setVisible(vistaTecnicaActiva || Theme.esRolAdmin());
        }
        if (btnToggleVistaTecnica != null) {
            btnToggleVistaTecnica.setText(vistaTecnicaActiva ? "Vista Simple" : "Vista Técnica");
            btnToggleVistaTecnica.setForeground(vistaTecnicaActiva ? Theme.getPasajeroAccent() : Theme.getTexto());
        }
        if (lblTagAocHeader != null && !Theme.esRolAdmin()) {
            lblTagAocHeader.setText(vistaTecnicaActiva ? " CHECK-IN TÉCNICO " : " REGISTRO DE VUELO ");
        }
        if (scrollTablaPasajero != null && !Theme.esRolAdmin()) {
            scrollTablaPasajero.setVisible(vistaTecnicaActiva);
            if (btnToggleTablaDetallada != null) {
                btnToggleTablaDetallada.setText(vistaTecnicaActiva ? "Ocultar comparativa detallada de vuelos" : "Ver comparativa detallada de vuelos");
            }
        }
        if (lblTitEquipajePasajero != null) {
            lblTitEquipajePasajero.setToolTipText(vistaTecnicaActiva
                    ? "TAD Lista Enlazada Simple (ListaSimple) de maletas por pasajero."
                    : "Normativa internacional estándar de equipaje IATA.");
        }
        actualizarEstadoSesionUI();
        if (panelRaiz != null) {
            panelRaiz.revalidate();
            panelRaiz.repaint();
        }
    }

    private void actualizarEstadoPestanasPasajero() {
        if (panelPestanas == null || Theme.esRolAdmin()) return;
        boolean prereqs = (pasajeroFilaConfirmada >= 0 &&
                txtDniPasajeroReserva != null && txtDniPasajeroReserva.getText().trim().length() == 8 &&
                txtNombrePasajeroReserva != null && !txtNombrePasajeroReserva.getText().trim().isEmpty());
        if (panelPestanas.getTabCount() >= 4) {
            panelPestanas.setEnabledAt(0, !prereqs); // Una vez confirmada la reserva, se bloquea el regreso a Vuelos
            panelPestanas.setEnabledAt(1, true);     // Pestaña de cabina en modo lectura / bloqueo de asientos
            panelPestanas.setEnabledAt(2, prereqs);
            panelPestanas.setEnabledAt(3, prereqs);
        }
        if (btnVolverCabinaEquipaje != null) {
            btnVolverCabinaEquipaje.setEnabled(!prereqs);
            btnVolverCabinaEquipaje.setToolTipText(prereqs ? "Reserva asegurada: su asiento no requiere modificaciones." : "Regresar a la cabina");
        }
    }

    private void validarDniEnVivo() {
        String dni = (txtDniPasajeroReserva != null) ? txtDniPasajeroReserva.getText().trim() : "";
        String nom = (txtNombrePasajeroReserva != null) ? txtNombrePasajeroReserva.getText().trim() : "";
        boolean esValido = dni.length() == 8 && dni.matches("\\d{8}");

        if (lblValidacionDni != null) {
            if (dni.isEmpty()) {
                lblValidacionDni.setText("8 dígitos numéricos requeridos");
                lblValidacionDni.setForeground(Theme.getMuted());
            } else if (esValido) {
                lblValidacionDni.setText("DNI valido (8 digitos)");
                lblValidacionDni.setForeground(Theme.getSuccess());
            } else {
                int faltan = 8 - dni.length();
                String msg = (faltan > 0) ? ("Faltan " + faltan + " digitos") : (dni.length() > 8 ? "Maximo 8 digitos" : "Solo numeros");
                lblValidacionDni.setText("Aviso: " + msg);
                lblValidacionDni.setForeground(Theme.getDanger());
            }
        }

        boolean puedeConfirmar = esValido && !nom.isEmpty() && (pasajeroFilaSel >= 0 && pasajeroColSel >= 0);
        if (btnConfirmarAsientoPasajero != null) {
            if (pasajeroFilaConfirmada >= 0) {
                char letConf = (char) ('A' + pasajeroColConfirmada);
                btnConfirmarAsientoPasajero.setText("Asiento " + (pasajeroFilaConfirmada + 1) + letConf + " Confirmado (Bloqueado)");
                btnConfirmarAsientoPasajero.setEnabled(false);
            } else {
                btnConfirmarAsientoPasajero.setText("Confirmar y Reservar Asiento");
                btnConfirmarAsientoPasajero.setEnabled(puedeConfirmar);
            }
        }
    }

    public void mostrarToast(String mensaje, Color acento) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (lblToast == null) {
                    lblToast = new JLabel("", SwingConstants.CENTER);
                    lblToast.setFont(FontManager.getIBMPlexSans(Font.BOLD, 12f));
                    lblToast.setOpaque(true);
                    getLayeredPane().add(lblToast, JLayeredPane.POPUP_LAYER);
                }
                Color bgToast = (acento != null) ? acento : Theme.getPasajeroAccent();
                lblToast.setText("  " + mensaje + "  ");
                lblToast.setBackground(new Color(bgToast.getRed(), bgToast.getGreen(), bgToast.getBlue(), 235));
                lblToast.setForeground(Color.WHITE);
                lblToast.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.WHITE, 1),
                        BorderFactory.createEmptyBorder(6, 16, 6, 16)
                ));
                lblToast.setSize(lblToast.getPreferredSize());
                int x = (getWidth() - lblToast.getWidth()) / 2;
                int y = Math.max(50, getHeight() - 110);
                lblToast.setLocation(x, y);
                lblToast.setVisible(true);

                if (toastTimer != null && toastTimer.isRunning()) {
                    toastTimer.stop();
                }
                toastTimer = new Timer(2400, e -> {
                    if (lblToast != null) lblToast.setVisible(false);
                });
                toastTimer.setRepeats(false);
                toastTimer.start();
            } catch (Exception ignored) {}
        });
    }



    private void accionReprogramarHorarioVuelo() {
        Vuelo vAct = controlador.obtenerVueloActivo();
        if (vAct == null) {
            JOptionPane.showMessageDialog(this, "No hay un vuelo seleccionado.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String[] opciones = new String[]{
            "08:30 (Mañana - Itinerario oficial)",
            "10:15 (Media Mañana - Conexión)",
            "14:15 (Tarde - Turno Regular)",
            "17:45 (Tarde - Hora Punta)",
            "20:00 (Noche - Servicio Ejecutivo)",
            "22:15 (Noche - Vuelo Nocturno)",
            "Personalizado..."
        };
        String sel = (String) JOptionPane.showInputDialog(
                this,
                "Vuelo Activo: " + vAct.obtenerCodigo() + " (" + vAct.obtenerOrigen() + " -> " + vAct.obtenerDestino() + ")\n"
                        + "Duración del trayecto: " + vAct.obtenerDuracion() + " minutos.\n\n"
                        + "Seleccione el nuevo horario de salida:",
                "Reprogramar / Ajustar Horario de Vuelo",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );
        if (sel == null) return;

        String nuevaSalida = "08:30";
        if (sel.startsWith("Personalizado")) {
            String custom = JOptionPane.showInputDialog(this, "Ingrese la nueva hora de salida (formato HH:mm):", vAct.obtenerHoraSalida());
            if (custom == null || !custom.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                JOptionPane.showMessageDialog(this, "Formato de hora inválido. Use HH:mm (ej. 09:30).", "Formato Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            nuevaSalida = custom.trim();
        } else {
            nuevaSalida = sel.substring(0, 5);
        }

        try {
            String[] p = nuevaSalida.split(":");
            int h = Integer.parseInt(p[0].trim());
            int m = Integer.parseInt(p[1].trim());
            int dur = vAct.obtenerDuracion();
            int totalMin = (h * 60 + m + dur) % 1440;
            int hllegada = totalMin / 60;
            int mllegada = totalMin % 60;
            String nuevaLlegada = String.format(java.util.Locale.US, "%02d:%02d", hllegada, mllegada);

            vAct.establecerHoraSalida(nuevaSalida);
            vAct.establecerHoraLlegada(nuevaLlegada);

            actualizarTablaVuelosPasajero();
            actualizarTablaVuelos();
            actualizarCabinaPasajero();
            actualizarPaseAbordarPasajero();
            actualizarBannerVueloSeleccionado();

            mostrarToast("Horario actualizado: " + nuevaSalida + " -> " + nuevaLlegada + " (" + dur + " min)", Theme.getSuccess());
            log("[OPERACIONES] Horario reprogramado para vuelo " + vAct.obtenerCodigo() + ": Salida " + nuevaSalida + " -> Llegada " + nuevaLlegada);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al calcular el horario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public PanelRadarSmartAirport getPanelRadarSmartAirport() {
        return panelRadarSmartAirport;
    }

    // HUMANIZACION UI - Creación del panel de Vuelos y Destinos para el Pasajero
    private JPanel crearPanelPasajeroVuelos() {
        panelPasajeroVuelos = new JPanel(new BorderLayout(0, 6));
        panelPasajeroVuelos.setBackground(Theme.getBg());
        panelPasajeroVuelos.setBorder(new EmptyBorder(4, 12, 4, 12));

        pNortePasajeroVuelos = new JPanel(new BorderLayout(0, 2));
        pNortePasajeroVuelos.setOpaque(false);

        lblTitPasajeroVuelos = new JLabel("Itinerario Comercial de Vuelos y Selección de Destino");
        lblTitPasajeroVuelos.setFont(FontManager.getBarlowCondensed(Font.BOLD, 15f));
        lblTitPasajeroVuelos.setForeground(Theme.getTexto());

        lblSubPasajeroVuelos = new JLabel("Elige tu vuelo: las tarjetas muestran horario y precio.");
        lblSubPasajeroVuelos.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 10.5f));
        lblSubPasajeroVuelos.setForeground(Theme.getSecundario());

        JPanel pTitulosVuelos = new JPanel(new GridLayout(2, 1, 0, 2));
        pTitulosVuelos.setOpaque(false);
        pTitulosVuelos.add(lblTitPasajeroVuelos);
        pTitulosVuelos.add(lblSubPasajeroVuelos);

        pNortePasajeroVuelos.add(pTitulosVuelos, BorderLayout.CENTER);
        panelPasajeroVuelos.add(pNortePasajeroVuelos, BorderLayout.NORTH);

        // Barra de filtros por Destino, Turno y Clase con estilo real y labels inline secundarios
        pFiltrosPasajero = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        pFiltrosPasajero.setOpaque(false);
        pFiltrosPasajero.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getBorder(), 1),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)));

        lblFiltroDestPasajero = new JLabel("Ciudad de Destino:");
        lblFiltroDestPasajero.setFont(FontManager.getBarlowCondensed(Font.BOLD, 11.5f));
        lblFiltroDestPasajero.setForeground(Theme.getSecundario());

        cbFiltroDestinoPasajero = new JComboBox<>(new String[]{
                "Arequipa", "Cusco", "Piura", "Iquitos", "Tarapoto", "Trujillo", "Chiclayo", "Todos"
        });
        estilizarComboModerno(cbFiltroDestinoPasajero);
        cbFiltroDestinoPasajero.addActionListener(e -> {
            String dest = (String) cbFiltroDestinoPasajero.getSelectedItem();
            if (dest != null && !"Todos".equalsIgnoreCase(dest)) {
                controlador.asegurarVariacionesDestino(dest);
            }
            actualizarTablaVuelosPasajero();
        });

        lblFiltroTurnoPasajero = new JLabel("Turno / Horario:");
        lblFiltroTurnoPasajero.setFont(FontManager.getBarlowCondensed(Font.BOLD, 11.5f));
        lblFiltroTurnoPasajero.setForeground(Theme.getSecundario());

        cbFiltroTurnoPasajero = new JComboBox<>(new String[]{
                "Todos los Horarios", "Mañana (06:00 - 12:00)", "Tarde (12:00 - 18:00)", "Noche (18:00 - 23:59)"
        });
        estilizarComboModerno(cbFiltroTurnoPasajero);
        cbFiltroTurnoPasajero.addActionListener(e -> actualizarTablaVuelosPasajero());

        lblFiltroClasePasajero = new JLabel("Clase:");
        lblFiltroClasePasajero.setFont(FontManager.getBarlowCondensed(Font.BOLD, 11.5f));
        lblFiltroClasePasajero.setForeground(Theme.getSecundario());

        cbFiltroClasePasajero = new JComboBox<>(new String[]{
                "Todas las Clases", "Turista", "Ejecutiva"
        });
        estilizarComboModerno(cbFiltroClasePasajero);
        cbFiltroClasePasajero.addActionListener(e -> actualizarTablaVuelosPasajero());

        pFiltrosPasajero.add(lblFiltroDestPasajero);
        pFiltrosPasajero.add(cbFiltroDestinoPasajero);
        pFiltrosPasajero.add(lblFiltroTurnoPasajero);
        pFiltrosPasajero.add(cbFiltroTurnoPasajero);
        pFiltrosPasajero.add(lblFiltroClasePasajero);
        pFiltrosPasajero.add(cbFiltroClasePasajero);

        // Panel de variaciones destacadas (Tarjetas de vuelo modernas verticales)
        panelTarjetasVariaciones = new JPanel();
        panelTarjetasVariaciones.setLayout(new BoxLayout(panelTarjetasVariaciones, BoxLayout.Y_AXIS));
        panelTarjetasVariaciones.setOpaque(false);
        panelTarjetasVariaciones.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JScrollPane scrollTarjetas = new JScrollPane(panelTarjetasVariaciones);
        scrollTarjetas.setOpaque(false);
        scrollTarjetas.getViewport().setOpaque(false);
        scrollTarjetas.setBorder(BorderFactory.createEmptyBorder());
        scrollTarjetas.getVerticalScrollBar().setUnitIncrement(16);

        // Tabla de Vuelos detallada con renderers avanzados (para modo vista tabular)
        String[] columnas = {"Código", "Ruta", "Fecha", "Salida", "Llegada", "Duración", "Tarifa Base", "Clase", "Asientos Libres", "Estado Operativo"};
        modeloTablaVuelosPasajero = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int f, int c) { return false; }
        };
        tablaVuelosPasajero = new JTable(modeloTablaVuelosPasajero);
        tablaVuelosPasajero.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        RenderersTablaVuelos.aplicarEstilosTabla(tablaVuelosPasajero);

        tablaVuelosPasajero.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent ev) {
                int filaSel = tablaVuelosPasajero.getSelectedRow();
                if (filaSel >= 0) {
                    String codigo = (String) modeloTablaVuelosPasajero.getValueAt(filaSel, 0);
                    seleccionarVueloPasajeroPorCodigo(codigo);
                }
            }
        });

        scrollTablaPasajero = new JScrollPane(tablaVuelosPasajero);
        scrollTablaPasajero.getViewport().setBackground(Theme.getSurface());
        scrollTablaPasajero.setBorder(BorderFactory.createLineBorder(Theme.getBorder(), 1));

        // Contenedor conmutador de Vistas (Tarjetas vs Tabla)
        CardLayout cardLayoutVuelos = new CardLayout();
        JPanel pContenedorVistasVuelos = new JPanel(cardLayoutVuelos);
        pContenedorVistasVuelos.setOpaque(false);
        pContenedorVistasVuelos.add(scrollTarjetas, "TARJETAS");
        pContenedorVistasVuelos.add(scrollTablaPasajero, "TABLA");

        btnToggleTablaDetallada = new JButton("Ver comparativa en tabla");
        btnToggleTablaDetallada.setFont(FontManager.getIBMPlexSans(Font.BOLD, 10.5f));
        btnToggleTablaDetallada.setBackground(Theme.getElevated());
        btnToggleTablaDetallada.setForeground(Theme.getPasajeroAccent());
        btnToggleTablaDetallada.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getBorder(), 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        btnToggleTablaDetallada.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToggleTablaDetallada.setFocusPainted(false);
        btnToggleTablaDetallada.addActionListener(e -> {
            modoVistaTablaVuelos = !modoVistaTablaVuelos;
            cardLayoutVuelos.show(pContenedorVistasVuelos, modoVistaTablaVuelos ? "TABLA" : "TARJETAS");
            btnToggleTablaDetallada.setText(modoVistaTablaVuelos ? "Volver a Tarjetas de Vuelo" : "Ver comparativa en tabla");
        });

        // HUMANIZACION UI - Strip informativo de vuelo seleccionado (accent-soft, legible)
        stripVueloSeleccionado = new StripVueloSeleccionado();

        // Agrupar centro
        JPanel pCentro = new JPanel(new BorderLayout(0, 6));
        pCentro.setOpaque(false);
        pCentro.add(pFiltrosPasajero, BorderLayout.NORTH);
        pCentro.add(pContenedorVistasVuelos, BorderLayout.CENTER);

        JPanel pSurCentro = new JPanel(new BorderLayout(8, 0));
        pSurCentro.setOpaque(false);
        pSurCentro.add(stripVueloSeleccionado, BorderLayout.CENTER);
        pSurCentro.add(btnToggleTablaDetallada, BorderLayout.EAST);
        pCentro.add(pSurCentro, BorderLayout.SOUTH);

        panelPasajeroVuelos.add(pCentro, BorderLayout.CENTER);

        // Apartado Lateral Izquierdo: Selección Rápida de Destinos y Ventanas del Flujo
        JPanel pLateralIzquierdo = crearPanelLateralIzquierdoDestinos();
        panelPasajeroVuelos.add(pLateralIzquierdo, BorderLayout.WEST);

        // Apartado Lateral Derecho: Resumen de Reserva (Modo Pasajero - Paso 1)
        JPanel pLateralAccionesVuelos = new JPanel(new BorderLayout(0, 8));
        pLateralAccionesVuelos.setOpaque(false);
        pLateralAccionesVuelos.setPreferredSize(new Dimension(260, 0));
        pLateralAccionesVuelos.setBorder(new EmptyBorder(0, 8, 0, 0));

        JPanel pResumenCard = new JPanel(new BorderLayout(0, 10));
        pResumenCard.setOpaque(false);

        // Header interno del resumen
        JPanel pInfoVuelo = new JPanel(new GridLayout(0, 1, 0, 5));
        pInfoVuelo.setOpaque(false);

        lblResumenCodigoVuelo = new JLabel("CP-2028 · Airbus A320neo");
        lblResumenCodigoVuelo.setFont(FontManager.getBarlowCondensed(Font.BOLD, 15f));
        lblResumenCodigoVuelo.setForeground(Theme.getPasajeroAccent());

        lblResumenRuta = new JLabel("Lima [LIM] -> Trujillo [TRU]");
        lblResumenRuta.setFont(FontManager.getIBMPlexSans(Font.BOLD, 12f));
        lblResumenRuta.setForeground(Theme.getTexto());

        lblResumenHorario = new JLabel("Salida: 07:45 | Llegada: 08:55");
        lblResumenHorario.setFont(FontManager.getIBMPlexMono(Font.PLAIN, 10.5f));
        lblResumenHorario.setForeground(Theme.getSecundario());

        lblResumenTarifa = new JLabel("Tarifa Base: S/. 119.00");
        lblResumenTarifa.setFont(FontManager.getBarlowCondensed(Font.BOLD, 16f));
        lblResumenTarifa.setForeground(Theme.getWarning());

        lblResumenAsientos = new JLabel("50 Asientos Libres (Turista)");
        lblResumenAsientos.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 10.5f));
        lblResumenAsientos.setForeground(Theme.getSuccess());

        pInfoVuelo.add(lblResumenCodigoVuelo);
        pInfoVuelo.add(lblResumenRuta);
        pInfoVuelo.add(lblResumenHorario);
        pInfoVuelo.add(lblResumenTarifa);
        pInfoVuelo.add(lblResumenAsientos);

        pResumenCard.add(pInfoVuelo, BorderLayout.NORTH);

        btnContinuarAsientoPasajeroVuelos = new BotonPrimario("Continuar a Asiento ->", Theme.getPasajeroAccent());
        btnContinuarAsientoPasajeroVuelos.setPreferredSize(new Dimension(230, 42));
        btnContinuarAsientoPasajeroVuelos.setFont(FontManager.getBarlowCondensed(Font.BOLD, 13.5f));
        btnContinuarAsientoPasajeroVuelos.addActionListener(e -> {
            actualizarCabinaPasajero();
            actualizarPaseAbordarPasajero();
            panelPestanas.setSelectedIndex(1);
        });

        btnFichaAvionPasajeroVuelos = new JButton("Ver Ficha Técnica");
        estilizarBotonAccion(btnFichaAvionPasajeroVuelos, Theme.getElevated(), Theme.getPasajeroAccent());
        btnFichaAvionPasajeroVuelos.setFont(FontManager.getIBMPlexSans(Font.BOLD, 10.5f));
        btnFichaAvionPasajeroVuelos.addActionListener(e -> mostrarFichaAvion());

        btnVerRutaPasajeroVuelos = new JButton("Consultar Ruta Aérea");
        estilizarBotonAccion(btnVerRutaPasajeroVuelos, Theme.getElevated(), Theme.getTexto());
        btnVerRutaPasajeroVuelos.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 10.5f));
        btnVerRutaPasajeroVuelos.addActionListener(e -> {
            Vuelo act = controlador.obtenerVueloActivo();
            if (act != null) {
                JOptionPane.showMessageDialog(this,
                        "Información de Ruta Comercial:\n" +
                        "- Vuelo: " + act.obtenerCodigo() + "\n" +
                        "- Origen: " + act.obtenerOrigen() + " (LIM)\n" +
                        "- Destino: " + act.obtenerDestino() + " (" + obtenerIata(act.obtenerDestino()) + ")\n" +
                        "- Duración estimada: " + act.obtenerDuracion() + " minutos\n" +
                        "- Aeronave: Airbus A320neo (50 Asientos)\n" +
                        "- Operador: CORPAC Airways / Servicio Nacional",
                        "Detalle de Ruta", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton btnActualizarCatalogo = new JButton("Actualizar Horarios");
        estilizarBotonAccion(btnActualizarCatalogo, Theme.getElevated(), Theme.getTexto());
        btnActualizarCatalogo.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 10.5f));
        btnActualizarCatalogo.setToolTipText("Reprogramar y ajustar horario de vuelo con recálculo dinámico");
        btnActualizarCatalogo.addActionListener(e -> accionReprogramarHorarioVuelo());

        JPanel pBotonesSecundarios = new JPanel(new GridLayout(3, 1, 0, 5));
        pBotonesSecundarios.setOpaque(false);
        pBotonesSecundarios.add(btnFichaAvionPasajeroVuelos);
        pBotonesSecundarios.add(btnVerRutaPasajeroVuelos);
        pBotonesSecundarios.add(btnActualizarCatalogo);

        JPanel pAccionesColumna = new JPanel(new BorderLayout(0, 10));
        pAccionesColumna.setOpaque(false);
        pAccionesColumna.add(btnContinuarAsientoPasajeroVuelos, BorderLayout.NORTH);
        pAccionesColumna.add(pBotonesSecundarios, BorderLayout.CENTER);

        pResumenCard.add(pAccionesColumna, BorderLayout.SOUTH);

        GlassPanel cardAccionesVuelo = GlassPanel.crearApartado(
                "Resumen de Reserva", "Tu Vuelo Elegido", Theme.getPasajeroAccent(), pResumenCard);

        pLateralAccionesVuelos.add(cardAccionesVuelo, BorderLayout.NORTH);
        panelPasajeroVuelos.add(pLateralAccionesVuelos, BorderLayout.EAST);

        return panelPasajeroVuelos;
    }

    private JPanel crearPanelLateralIzquierdoDestinos() {
        JPanel pLateral = new JPanel(new BorderLayout(0, 6));
        pLateral.setOpaque(false);
        pLateral.setPreferredSize(new Dimension(205, 0));
        pLateral.setBorder(new EmptyBorder(0, 0, 0, 8));

        // Subpanel de Destinos Rápidos con botones seleccionables
        JPanel pDestinosBotones = new JPanel(new GridLayout(0, 1, 0, 4));
        pDestinosBotones.setOpaque(false);

        String[] destinos = {"Trujillo", "Cusco", "Arequipa", "Piura", "Iquitos", "Tarapoto", "Chiclayo", "Todos"};
        String[] subtitulos = {"TRU - La Libertad", "CUZ - Imperial", "AQP - Sur", "PIU - Costa Norte", "IQT - Selva", "TPP - Amazonia", "CIX - Lambayeque", "Todos los Vuelos"};
        String[] precios = {"S/. 119", "S/. 149", "S/. 139", "S/. 129", "S/. 159", "S/. 145", "S/. 125", "Catálogo"};

        btnsDestinosLaterales = new JButton[destinos.length];
        for (int i = 0; i < destinos.length; i++) {
            final String dest = destinos[i];
            final String sub = subtitulos[i];
            final String precio = precios[i];
            final String iataCode = sub.split(" - ")[0];
            JButton btn = new JButton("<html><b>" + dest + "</b> <span style='font-size: 8.5px; color: #38BDF8;'>[" + iataCode + "]</span> <span style='font-size: 8.5px; color: #F59E0B;'>· " + precio + "</span></html>");
            btn.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 10.5f));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setBackground(Theme.getElevated());
            btn.setForeground(Theme.getTexto());
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getBorder(), 1, true),
                    BorderFactory.createEmptyBorder(3, 8, 3, 8)
            ));
            btn.setPreferredSize(new Dimension(195, 27));
            btn.addActionListener(e -> {
                if (cbFiltroDestinoPasajero != null) {
                    cbFiltroDestinoPasajero.setSelectedItem(dest);
                }
                for (JButton b : btnsDestinosLaterales) {
                    if (b != null) {
                        b.setBackground(Theme.getElevated());
                        b.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(Theme.getBorder(), 1, true),
                                BorderFactory.createEmptyBorder(3, 8, 3, 8)
                        ));
                    }
                }
                btn.setBackground(Theme.getPasajeroSoft());
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.getPasajeroAccent(), 2, true),
                        BorderFactory.createEmptyBorder(3, 8, 3, 8)
                ));
                mostrarToast("Destino: " + dest + " (" + sub + ")", Theme.getPasajeroAccent());
            });
            btnsDestinosLaterales[i] = btn;
            pDestinosBotones.add(btn);
        }

        if (btnsDestinosLaterales.length > 0 && btnsDestinosLaterales[0] != null) {
            btnsDestinosLaterales[0].setBackground(Theme.getPasajeroSoft());
            btnsDestinosLaterales[0].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getPasajeroAccent(), 2, true),
                    BorderFactory.createEmptyBorder(3, 8, 3, 8)
            ));
        }

        JPanel pContenedorDestinosFijo = new JPanel(new BorderLayout());
        pContenedorDestinosFijo.setOpaque(false);
        pContenedorDestinosFijo.add(pDestinosBotones, BorderLayout.NORTH);

        GlassPanel cardDestinos = GlassPanel.crearApartado(
                "Destinos Nacionales", "Selección rápida", Theme.getPasajeroAccent(), pContenedorDestinosFijo);

        // Subpanel de Navegación Rápida de Ventanas del Pasajero (Guía de Pasos)
        JPanel pNavegacionVentanas = new JPanel(new GridLayout(0, 1, 0, 4));
        pNavegacionVentanas.setOpaque(false);

        String[] pasosTitulos = {
            "1. Vuelos y Destinos  [*]",
            "2. Asiento y Cabina   [ ]",
            "3. Equipaje IATA      [ ]",
            "4. Pase de Abordar    [ ]"
        };
        for (int pIdx = 0; pIdx < pasosTitulos.length; pIdx++) {
            final int pasoDestino = pIdx;
            JButton btnPaso = new JButton(pasosTitulos[pIdx]);
            btnPaso.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 10.5f));
            btnPaso.setHorizontalAlignment(SwingConstants.LEFT);
            btnPaso.setPreferredSize(new Dimension(195, 26));
            btnPaso.setBackground(Theme.getSurface());
            btnPaso.setForeground(pIdx == 0 ? Theme.getPasajeroAccent() : Theme.getSecundario());
            btnPaso.setFocusPainted(false);
            btnPaso.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnPaso.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getBorder(), 1),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));
            btnPaso.addActionListener(e -> {
                if (pasoDestino > 1) {
                    boolean prereqs = (pasajeroFilaConfirmada >= 0 &&
                            txtDniPasajeroReserva != null && txtDniPasajeroReserva.getText().trim().length() == 8 &&
                            txtNombrePasajeroReserva != null && !txtNombrePasajeroReserva.getText().trim().isEmpty());
                    if (!prereqs) {
                        mostrarToast("Confirme su asiento en el Paso 2 antes de avanzar", Theme.getWarning());
                        panelPestanas.setSelectedIndex(1);
                        return;
                    }
                }
                if (pasoDestino == 1) {
                    actualizarCabinaPasajero();
                } else if (pasoDestino == 3) {
                    actualizarPaseAbordarPasajero();
                }
                panelPestanas.setSelectedIndex(pasoDestino);
            });
            pNavegacionVentanas.add(btnPaso);
        }

        JPanel pContenedorVentanasFijo = new JPanel(new BorderLayout());
        pContenedorVentanasFijo.setOpaque(false);
        pContenedorVentanasFijo.add(pNavegacionVentanas, BorderLayout.NORTH);

        GlassPanel cardVentanas = GlassPanel.crearApartado(
                "Flujo del Pasajero", "Guía de 4 pasos", Theme.getSecundario(), pContenedorVentanasFijo);

        pLateral.add(cardDestinos, BorderLayout.CENTER);
        pLateral.add(cardVentanas, BorderLayout.SOUTH);

        return pLateral;
    }

    private static String obtenerIata(String ciudad) {
        if (ciudad == null) return "PER";
        String c = ciudad.toUpperCase().trim();
        if (c.contains("LIMA")) return "LIM";
        if (c.contains("TRUJILLO")) return "TRU";
        if (c.contains("AREQUIPA")) return "AQP";
        if (c.contains("CUSCO") || c.contains("CUZCO")) return "CUZ";
        if (c.contains("PIURA")) return "PIU";
        if (c.contains("IQUITOS")) return "IQT";
        if (c.contains("TARAPOTO")) return "TPP";
        if (c.contains("CHICLAYO")) return "CIX";
        return c.length() >= 3 ? c.substring(0, 3) : c;
    }

    private void estilizarComboModerno(JComboBox<?> cb) {
        cb.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 12f));
        cb.setBackground(Theme.getInput());
        cb.setForeground(Theme.getTexto());
        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getBorder(), 1),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                if (isSelected) {
                    setBackground(Theme.getPasajeroSoft());
                    setForeground(Theme.getPasajeroAccent());
                } else {
                    setBackground(Theme.getSurface());
                    setForeground(Theme.getTexto());
                }
                return this;
            }
        });
    }

    // Creación del panel de Cabina exclusivo para Pasajeros
    private JPanel crearPanelPasajeroCabinaContenedor() {
        panelPasajeroCabinaContenedor = new JPanel(new BorderLayout(0, 6));
        panelPasajeroCabinaContenedor.setBackground(Theme.getBg());

        JPanel panelCabinaPasajero = crearPanelCabinaPasajeroNuevo();
        panelPasajeroCabinaContenedor.add(panelCabinaPasajero, BorderLayout.CENTER);

        return panelPasajeroCabinaContenedor;
    }

    // Componente visual de cabina espacioso, con pasillo central y zonas diferenciadas
    private JPanel crearPanelCabinaPasajeroNuevo() {
        panelCabinaPasajeroRaiz = new JPanel(new BorderLayout(0, 8));
        panelCabinaPasajeroRaiz.setBackground(Theme.getBg());
        panelCabinaPasajeroRaiz.setBorder(new EmptyBorder(8, 14, 8, 14));

        // 1. Encabezado del Vuelo Activo y Leyenda
        pEncabezadoCabinaPasajero = new JPanel(new BorderLayout(0, 4));
        pEncabezadoCabinaPasajero.setBackground(Theme.getSurface());
        pEncabezadoCabinaPasajero.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getBorder(), 1),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));

        lblCabinaVueloActivoInfo = new JLabel("CABINA COMERCIAL AIRBUS A320neo | Vuelo Activo: CP-2024 (Lima -> Arequipa)");
        lblCabinaVueloActivoInfo.setFont(FontManager.getBarlowCondensed(Font.BOLD, 15f));
        lblCabinaVueloActivoInfo.setForeground(Theme.getTexto());

        lblCabinaResumenLibres = new JLabel("Toca un asiento verde para elegirlo; los rojos están ocupados. (47 Libres de 50)");
        lblCabinaResumenLibres.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
        lblCabinaResumenLibres.setForeground(Theme.getSecundario());

        JPanel pLeyenda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pLeyenda.setOpaque(false);

        JLabel itemTurista = new JLabel("[Libre] Turista (S/. Base)");
        itemTurista.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
        itemTurista.setForeground(Theme.getSuccess());

        JLabel itemVip = new JLabel("[Libre] VIP (+30%)");
        itemVip.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
        itemVip.setForeground(Theme.getWarning());

        JLabel itemOcupado = new JLabel("[X] Ocupado");
        itemOcupado.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
        itemOcupado.setForeground(Theme.getMuted());

        JLabel itemSeleccionado = new JLabel("[*] Su Asiento");
        itemSeleccionado.setFont(FontManager.getIBMPlexSans(Font.BOLD, 11f));
        itemSeleccionado.setForeground(Theme.getPasajeroAccent());

        JButton btnVisor3D = new JButton("Vista 3D de Aeronave");
        btnVisor3D.setFont(FontManager.getIBMPlexSans(Font.BOLD, 11f));
        btnVisor3D.setBackground(Theme.getElevated());
        btnVisor3D.setForeground(Theme.getPasajeroAccent());
        btnVisor3D.setIcon(AppIcon.of("plane", 12, Theme.getPasajeroAccent()));
        btnVisor3D.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getPasajeroAccent(), 1),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        btnVisor3D.setFocusPainted(false);
        btnVisor3D.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVisor3D.setToolTipText("Telemetría complementaria: Inspección tridimensional del Airbus A320neo");
        btnVisor3D.addActionListener(e -> mostrarDialogoVisor3DAeronave());

        pLeyenda.add(itemTurista);
        pLeyenda.add(itemVip);
        pLeyenda.add(itemOcupado);
        pLeyenda.add(itemSeleccionado);
        pLeyenda.add(btnVisor3D);

        JPanel pSubCabina = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pSubCabina.setOpaque(false);

        JLabel lblSelClaseTxt = new JLabel("Clase:");
        lblSelClaseTxt.setFont(FontManager.getBarlowCondensed(Font.BOLD, 12f));
        lblSelClaseTxt.setForeground(Theme.getWarning());

        cbClaseCabinaPasajero = new JComboBox<>(new String[]{
            "Clase Ejecutiva (VIP)", "Clase Turista"
        });
        estilizarComboModerno(cbClaseCabinaPasajero);
        cbClaseCabinaPasajero.setPreferredSize(new Dimension(175, 26));
        cbClaseCabinaPasajero.addActionListener(e -> {
            if (cbFiltroClasePasajero != null) {
                String sel = (String) cbClaseCabinaPasajero.getSelectedItem();
                if (sel != null && sel.contains("Ejecutiva")) {
                    cbFiltroClasePasajero.setSelectedItem("Ejecutiva");
                } else if (sel != null && sel.contains("Turista")) {
                    cbFiltroClasePasajero.setSelectedItem("Turista");
                }
            }
            actualizarCabinaPasajero();
        });

        pSubCabina.add(lblSelClaseTxt);
        pSubCabina.add(cbClaseCabinaPasajero);
        pSubCabina.add(lblCabinaResumenLibres);

        JPanel pTitulos = new JPanel(new GridLayout(2, 1, 0, 3));
        pTitulos.setOpaque(false);
        pTitulos.add(lblCabinaVueloActivoInfo);
        pTitulos.add(pSubCabina);

        pEncabezadoCabinaPasajero.add(pTitulos, BorderLayout.WEST);
        pEncabezadoCabinaPasajero.add(pLeyenda, BorderLayout.EAST);
        panelCabinaPasajeroRaiz.add(pEncabezadoCabinaPasajero, BorderLayout.NORTH);

        // 2. Fuselaje de la Aeronave (Centro) en contenedor GlassPanel
        pFuselajeContenedor = new GlassPanel(new BorderLayout(0, 6), 18);
        pFuselajeContenedor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getBorder(), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        lblCockpitCabina = new JLabel("[ CABINA DE MANDO / PILOTOS - AIRBUS A320neo ]", SwingConstants.CENTER);
        lblCockpitCabina.setFont(FontManager.getBarlowCondensed(Font.BOLD, 12f));
        lblCockpitCabina.setForeground(Theme.getPasajeroAccent());
        lblCockpitCabina.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        pFuselajeContenedor.add(lblCockpitCabina, BorderLayout.NORTH);

        // Cabecera de columnas con pasillo
        JPanel pCabeceraColumnas = new JPanel(new GridBagLayout());
        pCabeceraColumnas.setOpaque(false);
        GridBagConstraints gbcH = new GridBagConstraints();
        gbcH.insets = new Insets(2, 3, 2, 3);
        gbcH.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblColA = new JLabel("VENTANA A", SwingConstants.CENTER);
        lblColA.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblColA.setForeground(textoGris);
        lblColA.setPreferredSize(new Dimension(50, 16));

        JLabel lblColB = new JLabel("PASILLO B", SwingConstants.CENTER);
        lblColB.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblColB.setForeground(textoGris);
        lblColB.setPreferredSize(new Dimension(50, 16));

        JLabel lblAisleHead = new JLabel("== PASILLO ==", SwingConstants.CENTER);
        lblAisleHead.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblAisleHead.setForeground(new Color(71, 85, 105));
        lblAisleHead.setPreferredSize(new Dimension(50, 16));

        JLabel lblColC = new JLabel("PASILLO C", SwingConstants.CENTER);
        lblColC.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblColC.setForeground(textoGris);
        lblColC.setPreferredSize(new Dimension(50, 16));

        JLabel lblColD = new JLabel("CENTRO D", SwingConstants.CENTER);
        lblColD.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblColD.setForeground(textoGris);
        lblColD.setPreferredSize(new Dimension(50, 16));

        JLabel lblColE = new JLabel("VENTANA E", SwingConstants.CENTER);
        lblColE.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblColE.setForeground(textoGris);
        lblColE.setPreferredSize(new Dimension(50, 16));

        gbcH.gridx = 0; pCabeceraColumnas.add(lblColA, gbcH);
        gbcH.gridx = 1; pCabeceraColumnas.add(lblColB, gbcH);
        gbcH.gridx = 2; pCabeceraColumnas.add(lblAisleHead, gbcH);
        gbcH.gridx = 3; pCabeceraColumnas.add(lblColC, gbcH);
        gbcH.gridx = 4; pCabeceraColumnas.add(lblColD, gbcH);
        gbcH.gridx = 5; pCabeceraColumnas.add(lblColE, gbcH);

        // Cuadricula completa con pasillo y divisiones de clase
        JPanel pFilasAsientos = new JPanel(new GridBagLayout());
        pFilasAsientos.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 3, 2, 3);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int gridY = 0;

        // Banner VIP
        lblVipBannerCabina = new JLabel("CLASE EJECUTIVA VIP - Filas 1 a 3 · Máximo confort, catering de cortesía (+30%)", SwingConstants.CENTER);
        lblVipBannerCabina.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblVipBannerCabina.setForeground(Theme.getWarning());
        lblVipBannerCabina.setOpaque(true);
        lblVipBannerCabina.setBackground(Theme.getWarningSoft());
        lblVipBannerCabina.setBorder(new LineBorder(Theme.getWarning(), 1));
        gbc.gridx = 0; gbc.gridy = gridY++; gbc.gridwidth = 6;
        pFilasAsientos.add(lblVipBannerCabina, gbc);
        gbc.gridwidth = 1;

        // Filas VIP 1 a 3
        for (int f = 0; f < 3; f++) {
            final int fila = f;
            for (int c = 0; c < 5; c++) {
                final int col = c;
                BotonAsientoCabina btn = new BotonAsientoCabina();
                btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
                btn.setPreferredSize(new Dimension(50, 26));
                btn.setFocusPainted(false);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.addActionListener(e -> seleccionarAsientoPasajero(fila, col));
                botonesAsientosPasajero[f][c] = btn;

                int posX = (c < 2) ? c : c + 1;
                gbc.gridx = posX; gbc.gridy = gridY;
                pFilasAsientos.add(btn, gbc);
            }
            JLabel lblPasilloVip = new JLabel("|", SwingConstants.CENTER);
            lblPasilloVip.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            lblPasilloVip.setForeground(new Color(51, 65, 85));
            lblPasilloVip.setPreferredSize(new Dimension(50, 26));
            gbc.gridx = 2; gbc.gridy = gridY;
            pFilasAsientos.add(lblPasilloVip, gbc);

            gridY++;
        }

        // Banner Turista
        lblTuristaBannerCabina = new JLabel("CLASE TURISTA CONFORT - Filas 4 a 10 · Asientos ergonómicos con USB · Tarifa estándar", SwingConstants.CENTER);
        lblTuristaBannerCabina.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTuristaBannerCabina.setForeground(Theme.getSuccess());
        lblTuristaBannerCabina.setOpaque(true);
        lblTuristaBannerCabina.setBackground(Theme.getSuccessSoft());
        lblTuristaBannerCabina.setBorder(new LineBorder(Theme.getSuccess(), 1));
        gbc.gridx = 0; gbc.gridy = gridY++; gbc.gridwidth = 6;
        gbc.insets = new Insets(6, 3, 2, 3);
        pFilasAsientos.add(lblTuristaBannerCabina, gbc);
        gbc.insets = new Insets(2, 3, 2, 3);
        gbc.gridwidth = 1;

        // Filas Turista 4 a 10
        for (int f = 3; f < 10; f++) {
            final int fila = f;
            for (int c = 0; c < 5; c++) {
                final int col = c;
                BotonAsientoCabina btn = new BotonAsientoCabina();
                btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
                btn.setPreferredSize(new Dimension(50, 24));
                btn.setFocusPainted(false);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.addActionListener(e -> seleccionarAsientoPasajero(fila, col));
                botonesAsientosPasajero[f][c] = btn;

                int posX = (c < 2) ? c : c + 1;
                gbc.gridx = posX; gbc.gridy = gridY;
                pFilasAsientos.add(btn, gbc);
            }
            JLabel lblPasilloTur = new JLabel("|", SwingConstants.CENTER);
            lblPasilloTur.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            lblPasilloTur.setForeground(new Color(40, 50, 70));
            lblPasilloTur.setPreferredSize(new Dimension(50, 24));
            gbc.gridx = 2; gbc.gridy = gridY;
            pFilasAsientos.add(lblPasilloTur, gbc);

            gridY++;
        }

        JPanel pCuerpoFuselaje = new JPanel(new BorderLayout(0, 4));
        pCuerpoFuselaje.setOpaque(false);
        pCuerpoFuselaje.add(pCabeceraColumnas, BorderLayout.NORTH);
        pCuerpoFuselaje.add(pFilasAsientos, BorderLayout.CENTER);

        JPanel pFuselajeCentrado = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
        pFuselajeCentrado.setOpaque(false);
        pFuselajeCentrado.add(pCuerpoFuselaje);

        scrollFuselaje = new JScrollPane(pFuselajeCentrado);
        scrollFuselaje.getViewport().setBackground(Theme.getInput());
        scrollFuselaje.setBorder(null);
        scrollFuselaje.getVerticalScrollBar().setUnitIncrement(16);
        pFuselajeContenedor.add(scrollFuselaje, BorderLayout.CENTER);

        // 3. Panel Lateral de Cotización y Reserva (Derecha) en contenedor GlassPanel
        pLateralCabinaPasajero = new GlassPanel(new BorderLayout(0, 6), 18);
        pLateralCabinaPasajero.setPreferredSize(new Dimension(365, 0));
        pLateralCabinaPasajero.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getBorder(), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        lblTitLatCabina = new JLabel("DETALLE DE SU ASIENTO Y TARIFA", SwingConstants.CENTER);
        lblTitLatCabina.setFont(FontManager.getBarlowCondensed(Font.BOLD, 13f));
        lblTitLatCabina.setForeground(Theme.getTexto());

        lblDetalleAsientoPasajero = new JLabel("Asiento: Seleccione un asiento en el mapa");
        lblDetalleAsientoPasajero.setFont(FontManager.getIBMPlexMono(Font.BOLD, 12f));
        lblDetalleAsientoPasajero.setForeground(Theme.getWarning());

        lblDetalleClasePasajero = new JLabel("Zona: -");
        lblDetalleClasePasajero.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
        lblDetalleClasePasajero.setForeground(Theme.getTexto());

        lblDetalleBasePasajero = new JLabel("Tarifa Base del Vuelo: S/. 139.00");
        lblDetalleBasePasajero.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
        lblDetalleBasePasajero.setForeground(Theme.getSecundario());

        lblDetalleSuplementoPasajero = new JLabel("Suplemento de Clase: S/. 0.00");
        lblDetalleSuplementoPasajero.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
        lblDetalleSuplementoPasajero.setForeground(Theme.getSecundario());

        lblDetalleTotalPasajero = new JLabel("TOTAL A PAGAR: S/. 0.00");
        lblDetalleTotalPasajero.setFont(FontManager.getIBMPlexSans(Font.BOLD, 13f));
        lblDetalleTotalPasajero.setForeground(Theme.getSuccess());

        JPanel pNorteCabina = new JPanel(new BorderLayout(0, 4));
        pNorteCabina.setOpaque(false);
        pNorteCabina.add(lblTitLatCabina, BorderLayout.NORTH);

        JPanel pCardResumen = new JPanel(new GridLayout(0, 1, 0, 2));
        pCardResumen.setOpaque(false);
        pCardResumen.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getBorder(), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        pCardResumen.add(lblDetalleAsientoPasajero);
        pCardResumen.add(lblDetalleClasePasajero);
        pCardResumen.add(lblDetalleTotalPasajero);
        pNorteCabina.add(pCardResumen, BorderLayout.CENTER);

        pLateralCabinaPasajero.add(pNorteCabina, BorderLayout.NORTH);

        JPanel pCuerpoLat = new JPanel(new GridBagLayout());
        pCuerpoLat.setOpaque(false);
        GridBagConstraints gLat = new GridBagConstraints();
        gLat.insets = new Insets(4, 4, 4, 4);
        gLat.fill = GridBagConstraints.HORIZONTAL;

        txtDniPasajeroReserva = new JTextField("", 10);
        txtDniPasajeroReserva.setToolTipText("Ingrese su número de DNI (8 dígitos)");
        estilizarCampoTexto(txtDniPasajeroReserva);
        txtDniPasajeroReserva.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validarDniEnVivo(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validarDniEnVivo(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validarDniEnVivo(); }
        });

        lblValidacionDni = new JLabel("Ingrese 8 dígitos numéricos");
        lblValidacionDni.setFont(FontManager.getIBMPlexSans(Font.BOLD, 10f));
        lblValidacionDni.setForeground(Theme.getMuted());

        txtNombrePasajeroReserva = new JTextField("", 14);
        txtNombrePasajeroReserva.setToolTipText("Ingrese sus nombres y apellidos completos");
        estilizarCampoTexto(txtNombrePasajeroReserva);
        txtNombrePasajeroReserva.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validarDniEnVivo(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validarDniEnVivo(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validarDniEnVivo(); }
        });

        txtEmailPasajeroReserva = new JTextField("", 14);
        txtEmailPasajeroReserva.setToolTipText("Ingrese su correo para envío automático de Boleta SUNAT y Pase");
        estilizarCampoTexto(txtEmailPasajeroReserva);

        JButton btnCargaRapidaDemo = new JButton("Carga Rapida (Demo)");
        btnCargaRapidaDemo.setFont(FontManager.getIBMPlexSans(Font.BOLD, 11f));
        btnCargaRapidaDemo.setBackground(Theme.getElevated());
        btnCargaRapidaDemo.setForeground(Theme.getSuccess());
        btnCargaRapidaDemo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getSuccess(), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        btnCargaRapidaDemo.setFocusPainted(false);
        btnCargaRapidaDemo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCargaRapidaDemo.setToolTipText("Auto-completa datos de prueba (DNI, Nombre y Correo) para agilizar la prueba.");
        btnCargaRapidaDemo.addActionListener(e -> {
            txtDniPasajeroReserva.setText("74859612");
            txtNombrePasajeroReserva.setText("Juan Pérez Quispe");
            if (txtEmailPasajeroReserva != null) txtEmailPasajeroReserva.setText("juan.perez@corpac.pe");
            if (txtDniAsiento != null) txtDniAsiento.setText("74859612");
            if (txtPesoEquipajePasajero != null) txtPesoEquipajePasajero.setText("12.5");
            if (pasajeroFilaSel < 0 || pasajeroColSel < 0) {
                Vuelo vDemo = controlador.obtenerVueloActivo();
                if (vDemo != null) {
                    int[][] matD = vDemo.obtenerMatrizAsientos();
                    String filtroClase = (cbClaseCabinaPasajero != null && cbClaseCabinaPasajero.getSelectedItem() != null)
                            ? (String) cbClaseCabinaPasajero.getSelectedItem()
                            : ((cbFiltroClasePasajero != null) ? (String) cbFiltroClasePasajero.getSelectedItem() : "Todas las Clases");
                    int fInicio = (filtroClase != null && filtroClase.contains("Turista")) ? 3 : 0;
                    int fFin = (filtroClase != null && (filtroClase.contains("Ejecutiva") || filtroClase.contains("VIP"))) ? 3 : 10;
                    for (int f = fInicio; f < fFin && (pasajeroFilaSel < 0); f++) {
                        for (int c = 0; c < 5; c++) {
                            if (matD[f][c] == 0) {
                                pasajeroFilaSel = f;
                                pasajeroColSel = c;
                                break;
                            }
                        }
                    }
                }
                actualizarCabinaPasajero();
            }
            validarDniEnVivo();
            log("[DEMO] Datos de prueba cargados: DNI 74859612 - Juan Pérez Quispe (juan.perez@corpac.pe).");
            mostrarToast("Datos demo cargados con éxito (DNI 74859612)", Theme.getSuccess());
        });

        gLat.gridx = 0; gLat.gridy = 0; gLat.gridwidth = 2; pCuerpoLat.add(lblDetalleBasePasajero, gLat);
        gLat.gridx = 0; gLat.gridy = 1; gLat.gridwidth = 2; pCuerpoLat.add(lblDetalleSuplementoPasajero, gLat);
        gLat.gridx = 0; gLat.gridy = 2; gLat.gridwidth = 2; pCuerpoLat.add(new JSeparator(), gLat);

        // B4.3: Carga Rapida ubicada prominentemente arriba de los campos de formulario
        gLat.gridx = 0; gLat.gridy = 3; gLat.gridwidth = 2; pCuerpoLat.add(btnCargaRapidaDemo, gLat);
        gLat.gridwidth = 1;

        gLat.gridx = 0; gLat.gridy = 4; pCuerpoLat.add(crearEtiquetaFormulario("DNI Pasajero:"), gLat);
        gLat.gridx = 1; gLat.gridy = 4; pCuerpoLat.add(txtDniPasajeroReserva, gLat);

        gLat.gridx = 1; gLat.gridy = 5; pCuerpoLat.add(lblValidacionDni, gLat);

        gLat.gridx = 0; gLat.gridy = 6; pCuerpoLat.add(crearEtiquetaFormulario("Nombres:"), gLat);
        gLat.gridx = 1; gLat.gridy = 6; pCuerpoLat.add(txtNombrePasajeroReserva, gLat);

        gLat.gridx = 0; gLat.gridy = 7; pCuerpoLat.add(crearEtiquetaFormulario("Correo Electrónico:"), gLat);
        gLat.gridx = 1; gLat.gridy = 7; pCuerpoLat.add(txtEmailPasajeroReserva, gLat);

        // Botones de acción organizados verticalmente en GlassPanel
        JPanel pBotonesColumnaCabina = new JPanel(new GridLayout(0, 1, 0, 6));
        pBotonesColumnaCabina.setOpaque(false);

        btnConfirmarAsientoPasajero = new BotonPrimario("Confirmar y Reservar Asiento", Theme.getPasajeroAccent());
        btnConfirmarAsientoPasajero.setPreferredSize(new Dimension(240, 36));
        btnConfirmarAsientoPasajero.setEnabled(false);
        btnConfirmarAsientoPasajero.addActionListener(e -> accionConfirmarAsientoPasajero());

        btnAutoAsignarPasajero = new JButton("Asignar Mejor Asiento Automático");
        btnAutoAsignarPasajero.setFont(FontManager.getIBMPlexSans(Font.BOLD, 11f));
        btnAutoAsignarPasajero.setBackground(Theme.getElevated());
        btnAutoAsignarPasajero.setForeground(Theme.getWarning());
        btnAutoAsignarPasajero.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getWarning(), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        btnAutoAsignarPasajero.setFocusPainted(false);
        btnAutoAsignarPasajero.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAutoAsignarPasajero.addActionListener(e -> accionAutoAsignarPasajero());

        btnFichaAvionPasajero = new JButton("Especificaciones Airbus A320neo");
        btnFichaAvionPasajero.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
        btnFichaAvionPasajero.setBackground(Theme.getElevated());
        btnFichaAvionPasajero.setForeground(Theme.getTexto());
        btnFichaAvionPasajero.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getBorder(), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        btnFichaAvionPasajero.setFocusPainted(false);
        btnFichaAvionPasajero.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFichaAvionPasajero.addActionListener(e -> mostrarFichaAvion());

        btnContinuarEquipajeCabina = new JButton("Continuar a Registro de Equipaje ->");
        estilizarBotonAccion(btnContinuarEquipajeCabina, Theme.getElevated(), Theme.getPasajeroAccent());
        btnContinuarEquipajeCabina.setFont(FontManager.getIBMPlexSans(Font.BOLD, 11f));
        btnContinuarEquipajeCabina.setFocusPainted(false);
        btnContinuarEquipajeCabina.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnContinuarEquipajeCabina.addActionListener(e -> {
            if (pasajeroFilaConfirmada < 0) {
                accionConfirmarAsientoPasajero();
            } else {
                actualizarPanelEquipajePasajero();
                actualizarPaseAbordarPasajero();
                panelPestanas.setSelectedIndex(2);
            }
        });

        btnVolverVuelosCabina = new JButton("<- Cambiar Vuelo / Destino");
        btnVolverVuelosCabina.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
        btnVolverVuelosCabina.setBackground(Theme.getElevated());
        btnVolverVuelosCabina.setForeground(Theme.getTexto());
        btnVolverVuelosCabina.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getBorder(), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        btnVolverVuelosCabina.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolverVuelosCabina.setFocusPainted(false);
        btnVolverVuelosCabina.addActionListener(e -> {
            if (pasajeroFilaConfirmada >= 0) {
                mostrarToast("Su reserva ya está confirmada. No puede cambiar el vuelo.", Theme.getWarning());
                return;
            }
            panelPestanas.setSelectedIndex(0);
        });

        pBotonesColumnaCabina.add(btnConfirmarAsientoPasajero);
        pBotonesColumnaCabina.add(btnContinuarEquipajeCabina);
        pBotonesColumnaCabina.add(btnAutoAsignarPasajero);
        pBotonesColumnaCabina.add(btnFichaAvionPasajero);
        pBotonesColumnaCabina.add(btnVolverVuelosCabina);

        GlassPanel cardAccionesCabina = GlassPanel.crearApartado("Acciones de Reserva", "Asignación y Validación", Theme.getPasajeroAccent(), pBotonesColumnaCabina);

        gLat.gridx = 0; gLat.gridy = 8; gLat.gridwidth = 2;
        pCuerpoLat.add(cardAccionesCabina, gLat);

        JScrollPane scrollLatCabina = new JScrollPane(pCuerpoLat);
        scrollLatCabina.setBorder(null);
        scrollLatCabina.setOpaque(false);
        scrollLatCabina.getViewport().setOpaque(false);
        scrollLatCabina.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollLatCabina.getVerticalScrollBar().setUnitIncrement(16);
        pLateralCabinaPasajero.add(scrollLatCabina, BorderLayout.CENTER);

        // Armar centro
        JPanel pCentroSplit = new JPanel(new BorderLayout(8, 0));
        pCentroSplit.setOpaque(false);
        pCentroSplit.add(pFuselajeContenedor, BorderLayout.CENTER);
        pCentroSplit.add(pLateralCabinaPasajero, BorderLayout.EAST);
        panelCabinaPasajeroRaiz.add(pCentroSplit, BorderLayout.CENTER);

        return panelCabinaPasajeroRaiz;
    }

    // Creación del panel de Equipaje para el Pasajero
    private JPanel crearPanelPasajeroEquipajeContenedor() {
        this.panelPasajeroEquipajeContenedor = new JPanel(new BorderLayout(0, 6));
        this.panelPasajeroEquipajeContenedor.setBackground(Theme.getBg());
        this.panelPasajeroEquipajeContenedor.setBorder(new EmptyBorder(4, 12, 4, 12));

        // Encabezado
        JPanel pNorte = new JPanel(new BorderLayout());
        pNorte.setOpaque(false);

        this.lblTitEquipajePasajero = new JLabel("Facturación y Despacho de Equipaje Comercial - Normas IATA");
        this.lblTitEquipajePasajero.setFont(FontManager.getBarlowCondensed(Font.BOLD, 15f));
        this.lblTitEquipajePasajero.setForeground(Theme.getTexto());
        this.lblTitEquipajePasajero.setToolTipText("Normativa internacional de equipaje IATA.");

        this.lblSubEquipajePasajero = new JLabel("Registra tus maletas: hasta 10 kg en cabina sin costo.");
        this.lblSubEquipajePasajero.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 10.5f));
        this.lblSubEquipajePasajero.setForeground(Theme.getSecundario());

        JPanel pTitulosEq = new JPanel(new GridLayout(2, 1, 0, 2));
        pTitulosEq.setOpaque(false);
        pTitulosEq.add(this.lblTitEquipajePasajero);
        pTitulosEq.add(this.lblSubEquipajePasajero);

        pNorte.add(pTitulosEq, BorderLayout.CENTER);
        this.panelPasajeroEquipajeContenedor.add(pNorte, BorderLayout.NORTH);

        // Formulario de equipaje en contenedor GlassPanel
        this.pFormularioEquipajePasajero = new GlassPanel(new GridBagLayout(), 16);
        this.pFormularioEquipajePasajero.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getBorder(), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(3, 4, 3, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        lblInfoPasajeroVueloEquipaje = new JLabel("Pasajero: No registrado | Vuelo Activo: CP-2024 (Lima -> Arequipa)");
        lblInfoPasajeroVueloEquipaje.setFont(FontManager.getBarlowCondensed(Font.BOLD, 12f));
        lblInfoPasajeroVueloEquipaje.setForeground(Theme.getPasajeroAccent());
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2; this.pFormularioEquipajePasajero.add(lblInfoPasajeroVueloEquipaje, g);
        g.gridwidth = 1;

        cbTipoEquipajePasajero = new JComboBox<>(new String[]{
                "Equipaje de Mano / Cabina (Hasta 10 kg - Sin costo)",
                "Equipaje en Bodega / Facturado (Hasta 23 kg - Estándar)",
                "Equipaje Especial Sobredimensionado (Hasta 32 kg)"
        });
        estilizarComboModerno(cbTipoEquipajePasajero);

        txtPesoEquipajePasajero = new JTextField("8.5", 8);
        estilizarCampoTexto(txtPesoEquipajePasajero);
        txtPesoEquipajePasajero.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { recalcularAlertaIata(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { recalcularAlertaIata(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { recalcularAlertaIata(); }
        });

        lblAlertaIataEquipaje = new JLabel("[OK] Peso estándar permitido por normativa IATA.");
        lblAlertaIataEquipaje.setFont(FontManager.getIBMPlexSans(Font.BOLD, 10.5f));
        lblAlertaIataEquipaje.setForeground(Theme.getSuccess());

        JButton btnRegistrarMaleta = new JButton("Registrar Maleta en Vuelo");
        estilizarBotonAccion(btnRegistrarMaleta, Theme.getElevated(), Theme.getPasajeroAccent());
        btnRegistrarMaleta.setFont(FontManager.getIBMPlexSans(Font.BOLD, 11f));
        btnRegistrarMaleta.setPreferredSize(new Dimension(230, 36));
        btnRegistrarMaleta.addActionListener(e -> accionRegistrarEquipajePasajero());

        g.gridx = 0; g.gridy = 1; this.pFormularioEquipajePasajero.add(crearEtiquetaFormulario("Tipo de Maleta:"), g);
        g.gridx = 1; g.gridy = 1; this.pFormularioEquipajePasajero.add(cbTipoEquipajePasajero, g);

        g.gridx = 0; g.gridy = 2; this.pFormularioEquipajePasajero.add(crearEtiquetaFormulario("Peso Estimado (kg):"), g);
        g.gridx = 1; g.gridy = 2; this.pFormularioEquipajePasajero.add(txtPesoEquipajePasajero, g);

        g.gridx = 0; g.gridy = 3; g.gridwidth = 2; this.pFormularioEquipajePasajero.add(lblAlertaIataEquipaje, g);

        // Tabla de maletas registradas
        String[] colEq = {"Código Maleta", "DNI Pasajero", "Vuelo Asignado", "Peso (kg)", "Tipo de Equipaje", "Estado de Carga"};
        modeloTablaEquipajePasajero = new DefaultTableModel(colEq, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int f, int c) { return false; }
        };
        tablaEquipajePasajero = new JTable(modeloTablaEquipajePasajero) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getRowCount() == 0) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setFont(FontManager.getIBMPlexSans(Font.ITALIC, 12f));
                    g2.setColor(Theme.getMuted());
                    String msg = "Aun no ha registrado maletas para este vuelo. Puede viajar sin equipaje adicional o ingresar el peso arriba.";
                    FontMetrics fm = g2.getFontMetrics();
                    int x = Math.max(16, (getWidth() - fm.stringWidth(msg)) / 2);
                    int y = Math.max(35, getHeight() / 2);
                    g2.drawString(msg, x, y);
                    g2.dispose();
                }
            }
        };
        RenderersTablaVuelos.aplicarEstilosTabla(tablaEquipajePasajero);

        this.scrollTablaEquipajePasajero = new JScrollPane(tablaEquipajePasajero);
        this.scrollTablaEquipajePasajero.getViewport().setBackground(Theme.getSurface());
        this.scrollTablaEquipajePasajero.setBorder(BorderFactory.createLineBorder(Theme.getBorder(), 1));
        this.scrollTablaEquipajePasajero.getVerticalScrollBar().setUnitIncrement(16);

        JPanel pCentroEq = new JPanel(new BorderLayout(0, 6));
        pCentroEq.setOpaque(false);
        pCentroEq.add(this.pFormularioEquipajePasajero, BorderLayout.NORTH);
        pCentroEq.add(this.scrollTablaEquipajePasajero, BorderLayout.CENTER);
        this.panelPasajeroEquipajeContenedor.add(pCentroEq, BorderLayout.CENTER);

        // Botones de Navegación y Acciones
        this.btnVolverCabinaEquipaje = new JButton("<< Volver a Asiento");
        this.btnVolverCabinaEquipaje.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
        this.btnVolverCabinaEquipaje.setBackground(Theme.getElevated());
        this.btnVolverCabinaEquipaje.setForeground(Theme.getTexto());
        this.btnVolverCabinaEquipaje.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getBorder(), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        this.btnVolverCabinaEquipaje.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.btnVolverCabinaEquipaje.setFocusPainted(false);
        this.btnVolverCabinaEquipaje.addActionListener(e -> {
            if (pasajeroFilaConfirmada >= 0) {
                mostrarToast("Su reserva ya está confirmada. La cabina es de sólo lectura.", Theme.getInfo());
            }
            panelPestanas.setSelectedIndex(1);
        });

        this.btnContinuarPaseEquipaje = new BotonPrimario("Generar Mi Pase de Abordar ->", Theme.getPasajeroAccent());
        this.btnContinuarPaseEquipaje.setPreferredSize(new Dimension(230, 40));
        this.btnContinuarPaseEquipaje.addActionListener(e -> {
            String dni = (txtDniPasajeroReserva != null) ? txtDniPasajeroReserva.getText().trim() : "";
            String nombre = (txtNombrePasajeroReserva != null) ? txtNombrePasajeroReserva.getText().trim() : "";

            if (nombre.isEmpty() || dni.length() != 8 || !dni.matches("\\d+")) {
                JOptionPane.showMessageDialog(this,
                        "Complete los datos del pasajero antes de continuar.",
                        "Datos del Pasajero Requeridos", JOptionPane.WARNING_MESSAGE);
                panelPestanas.setSelectedIndex(1);
                if (txtDniPasajeroReserva != null) txtDniPasajeroReserva.requestFocus();
                return;
            }
            stepperPasajero.setPasoCompletado(2, true);
            actualizarEstadoPestanasPasajero();
            actualizarPaseAbordarPasajero();
            mostrarToast("Equipaje verificado. Generando pase...", Theme.getSuccess());
            panelPestanas.setSelectedIndex(3);
        });

        // Apartado Lateral Derecho: Botones ordenados verticalmente (Modo Pasajero - Paso 3)
        JPanel pLateralAccionesEquipaje = new JPanel(new BorderLayout(0, 8));
        pLateralAccionesEquipaje.setOpaque(false);
        pLateralAccionesEquipaje.setPreferredSize(new Dimension(250, 0));
        pLateralAccionesEquipaje.setBorder(new EmptyBorder(0, 8, 0, 0));

        JPanel pBotonesColumnaEquipaje = new JPanel(new GridLayout(0, 1, 0, 8));
        pBotonesColumnaEquipaje.setOpaque(false);
        pBotonesColumnaEquipaje.add(this.btnContinuarPaseEquipaje);
        pBotonesColumnaEquipaje.add(btnRegistrarMaleta);
        pBotonesColumnaEquipaje.add(this.btnVolverCabinaEquipaje);

        GlassPanel cardAccionesEq = GlassPanel.crearApartado(
                "Acciones de Equipaje", "Despacho y Control IATA", Theme.getPasajeroAccent(), pBotonesColumnaEquipaje);

        pLateralAccionesEquipaje.add(cardAccionesEq, BorderLayout.NORTH);
        this.panelPasajeroEquipajeContenedor.add(pLateralAccionesEquipaje, BorderLayout.EAST);

        return this.panelPasajeroEquipajeContenedor;
    }

    // Creación del panel de Pase de Abordar
    private JPanel crearPanelPasajeroPaseAbordar() {
        this.panelPasajeroPaseAbordar = new JPanel(new BorderLayout(0, 6));
        this.panelPasajeroPaseAbordar.setBackground(Theme.getBg());
        this.panelPasajeroPaseAbordar.setBorder(new EmptyBorder(4, 12, 4, 12));

        JPanel pNorte = new JPanel(new BorderLayout());
        pNorte.setOpaque(false);

        this.lblTitPasePasajero = new JLabel("Pase de Abordar Electrónico y Sala de Embarque");
        this.lblTitPasePasajero.setFont(FontManager.getBarlowCondensed(Font.BOLD, 15f));
        this.lblTitPasePasajero.setForeground(Theme.getTexto());

        this.lblSubPasePasajero = new JLabel("Revisa tus datos y confirma para emitir el voucher.");
        this.lblSubPasePasajero.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 10.5f));
        this.lblSubPasePasajero.setForeground(Theme.getSecundario());

        JPanel pTitulosPase = new JPanel(new GridLayout(2, 1, 0, 2));
        pTitulosPase.setOpaque(false);
        pTitulosPase.add(this.lblTitPasePasajero);
        pTitulosPase.add(this.lblSubPasePasajero);

        pNorte.add(pTitulosPase, BorderLayout.CENTER);
        this.panelPasajeroPaseAbordar.add(pNorte, BorderLayout.NORTH);

        // Tarjeta de boleto en contenedor GlassPanel
        GlassPanel gpBoleto = new GlassPanel(new BorderLayout(0, 8), 18);
        gpBoleto.setColorAcento(Theme.getPasajeroAccent());
        this.pTarjetaBoleto = gpBoleto;
        this.pTarjetaBoleto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getPasajeroAccent(), 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        JPanel pEncabezadoBoleto = new JPanel(new BorderLayout());
        pEncabezadoBoleto.setOpaque(false);

        JLabel lblLogoBoleto = new JLabel("CORPAC AIRWAYS | AEROPUERTOS DEL PERÚ");
        ImageIcon icoBoleto = obtenerIconoEmpresa(75, 20);
        if (icoBoleto != null) {
            lblLogoBoleto.setIcon(icoBoleto);
            lblLogoBoleto.setText(" CORPAC AIRWAYS | AEROPUERTOS DEL PERÚ");
        }
        lblLogoBoleto.setFont(FontManager.getBarlowCondensed(Font.BOLD, 14f));
        lblLogoBoleto.setForeground(Theme.getPasajeroAccent());

        JLabel lblTipoBoleto = new JLabel("BOARDING PASS / PASE ELECTRÓNICO");
        lblTipoBoleto.setFont(FontManager.getIBMPlexMono(Font.BOLD, 11f));
        lblTipoBoleto.setForeground(Theme.getWarning());

        pEncabezadoBoleto.add(lblLogoBoleto, BorderLayout.WEST);
        pEncabezadoBoleto.add(lblTipoBoleto, BorderLayout.EAST);
        this.pTarjetaBoleto.add(pEncabezadoBoleto, BorderLayout.NORTH);

        this.pDatosBoleto = new JPanel(new GridLayout(4, 2, 12, 6));
        this.pDatosBoleto.setOpaque(false);
        this.pDatosBoleto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, Theme.getBorder()),
                BorderFactory.createEmptyBorder(8, 0, 8, 0)));

        lblPaseNombrePasajero = new JLabel("Pasajero: -- | DNI: --");
        lblPaseNombrePasajero.setFont(FontManager.getIBMPlexSans(Font.BOLD, 12f));
        lblPaseNombrePasajero.setForeground(Theme.getTexto());

        lblPaseCodigoVuelo = new JLabel("Vuelo: -- | Aeronave: Airbus A320neo");
        lblPaseCodigoVuelo.setFont(FontManager.getIBMPlexMono(Font.BOLD, 12f));
        lblPaseCodigoVuelo.setForeground(Theme.getPasajeroAccent());

        lblPaseRutaVuelo = new JLabel("Ruta: -- -> --");
        lblPaseRutaVuelo.setFont(FontManager.getBarlowCondensed(Font.BOLD, 12f));
        lblPaseRutaVuelo.setForeground(Theme.getTexto());

        lblPaseHorario = new JLabel("Horario: --:-- | Abordaje: --:--");
        lblPaseHorario.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
        lblPaseHorario.setForeground(Theme.getSecundario());

        lblPaseAsientoDetalle = new JLabel("Asiento: -- | Clase: --");
        lblPaseAsientoDetalle.setFont(FontManager.getIBMPlexMono(Font.BOLD, 12f));
        lblPaseAsientoDetalle.setForeground(Theme.getSuccess());

        lblPasePuertaDetalle = new JLabel("PUERTA DE EMBARQUE: GATE 01");
        lblPasePuertaDetalle.setFont(FontManager.getIBMPlexMono(Font.BOLD, 12f));
        lblPasePuertaDetalle.setForeground(Theme.getWarning());

        lblPaseEquipajeDetalle = new JLabel("Equipaje: --");
        lblPaseEquipajeDetalle.setFont(FontManager.getIBMPlexSans(Font.BOLD, 11.5f));
        lblPaseEquipajeDetalle.setForeground(Theme.getTexto());

        lblPaseLocalizador = new JLabel("LOCALIZADOR: LIM-84920 [CONFIRMADO]");
        lblPaseLocalizador.setFont(FontManager.getIBMPlexMono(Font.BOLD, 11.5f));
        lblPaseLocalizador.setForeground(Theme.getInfo());

        this.pDatosBoleto.add(lblPaseNombrePasajero);
        this.pDatosBoleto.add(lblPaseCodigoVuelo);
        this.pDatosBoleto.add(lblPaseRutaVuelo);
        this.pDatosBoleto.add(lblPaseHorario);
        this.pDatosBoleto.add(lblPaseAsientoDetalle);
        this.pDatosBoleto.add(lblPasePuertaDetalle);
        this.pDatosBoleto.add(lblPaseEquipajeDetalle);
        this.pDatosBoleto.add(lblPaseLocalizador);
        this.pTarjetaBoleto.add(this.pDatosBoleto, BorderLayout.CENTER);

        JPanel pPieBoleto = new JPanel(new BorderLayout());
        pPieBoleto.setOpaque(false);

        JLabel lblBarcode = new JLabel("|||||| | |||| ||| ||||||| |||| | ||| |||||| | |||||| ||| | |||||", SwingConstants.CENTER);
        lblBarcode.setFont(FontManager.getIBMPlexMono(Font.BOLD, 16f));
        lblBarcode.setForeground(Theme.getSecundario());

        JLabel lblInstrucciones = new JLabel("Presente este pase electrónico o físico junto a su DNI en la puerta de embarque señalada.", SwingConstants.CENTER);
        lblInstrucciones.setFont(FontManager.getIBMPlexSans(Font.ITALIC, 10.5f));
        lblInstrucciones.setForeground(Theme.getMuted());

        pPieBoleto.add(lblBarcode, BorderLayout.NORTH);
        pPieBoleto.add(lblInstrucciones, BorderLayout.SOUTH);
        this.pTarjetaBoleto.add(pPieBoleto, BorderLayout.SOUTH);

        this.pMonitorSalas = new GlassPanel(new BorderLayout(0, 4), 16);
        this.pMonitorSalas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getBorder(), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        this.lblTitMonitorSalas = new JLabel("MONITOR DE SALAS DE EMBARQUE EN VIVO");
        this.lblTitMonitorSalas.setFont(FontManager.getBarlowCondensed(Font.BOLD, 11.5f));
        this.lblTitMonitorSalas.setForeground(Theme.getPasajeroAccent());

        this.lblDescMonitorSalas = new JLabel("<html><body style='color:#94A3B8; font-size:10px;'>" +
                "- Gate 01: Sala Nacional (Puerta Principal) &nbsp;|&nbsp; " +
                "- Gate 02: Sala Regional &nbsp;|&nbsp; " +
                "- Gate 03: Sala Norte &nbsp;|&nbsp; " +
                "- Gate 04: Sala Centro &nbsp;|&nbsp; " +
                "- Gate 05: Sala Sur &nbsp;|&nbsp; " +
                "- Gate B12: Sala Internacional" +
                "</body></html>");

        this.pMonitorSalas.add(this.lblTitMonitorSalas, BorderLayout.NORTH);
        this.pMonitorSalas.add(this.lblDescMonitorSalas, BorderLayout.CENTER);

        JPanel pCentroCompleto = new JPanel(new BorderLayout(0, 8));
        pCentroCompleto.setOpaque(false);
        pCentroCompleto.add(this.pTarjetaBoleto, BorderLayout.CENTER);
        pCentroCompleto.add(this.pMonitorSalas, BorderLayout.SOUTH);

        JScrollPane scrollBoleto = new JScrollPane(pCentroCompleto);
        scrollBoleto.setBorder(null);
        scrollBoleto.setOpaque(false);
        scrollBoleto.getViewport().setOpaque(false);
        scrollBoleto.getVerticalScrollBar().setUnitIncrement(16);
        this.panelPasajeroPaseAbordar.add(scrollBoleto, BorderLayout.CENTER);


        this.btnVolverEquipajePase = new JButton("<- Modificar Equipaje");
        this.btnVolverEquipajePase.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
        this.btnVolverEquipajePase.setBackground(Theme.getElevated());
        this.btnVolverEquipajePase.setForeground(Theme.getTexto());
        this.btnVolverEquipajePase.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getBorder(), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        this.btnVolverEquipajePase.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.btnVolverEquipajePase.setFocusPainted(false);
        this.btnVolverEquipajePase.addActionListener(e -> panelPestanas.setSelectedIndex(2));

        this.btnNuevaReservaPase = new JButton("Nueva Reserva / Cambiar Destino");
        this.btnNuevaReservaPase.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
        this.btnNuevaReservaPase.setBackground(Theme.getElevated());
        this.btnNuevaReservaPase.setForeground(Theme.getPasajeroAccent());
        this.btnNuevaReservaPase.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getPasajeroAccent(), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        this.btnNuevaReservaPase.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.btnNuevaReservaPase.setFocusPainted(false);
        this.btnNuevaReservaPase.addActionListener(e -> {
            pasajeroFilaConfirmada = -1;
            pasajeroColConfirmada = -1;
            pasajeroFilaSel = -1;
            pasajeroColSel = -1;
            if (txtNombrePasajeroReserva != null) {
                txtNombrePasajeroReserva.setText("");
                txtNombrePasajeroReserva.setEditable(true);
            }
            if (txtDniPasajeroReserva != null) {
                txtDniPasajeroReserva.setText("");
                txtDniPasajeroReserva.setEditable(true);
            }
            if (txtEmailPasajeroReserva != null) {
                txtEmailPasajeroReserva.setText("");
                txtEmailPasajeroReserva.setEditable(true);
            }
            if (cbClaseCabinaPasajero != null) cbClaseCabinaPasajero.setEnabled(true);
            if (btnAutoAsignarPasajero != null) btnAutoAsignarPasajero.setEnabled(true);
            if (btnVolverVuelosCabina != null) btnVolverVuelosCabina.setEnabled(true);
            if (btnVolverCabinaEquipaje != null) btnVolverCabinaEquipaje.setEnabled(true);
            if (stepperPasajero != null) {
                stepperPasajero.setPasoCompletado(1, false);
                stepperPasajero.setPasoCompletado(2, false);
                stepperPasajero.setPasoCompletado(3, false);
            }
            actualizarEstadoPestanasPasajero();
            actualizarCabinaPasajero();
            panelPestanas.setSelectedIndex(0);
        });

        // Apartado Lateral Derecho: Organizado en secciones claras
        JPanel pLateralAccionesPase = new JPanel(new BorderLayout(0, 8));
        pLateralAccionesPase.setOpaque(false);
        pLateralAccionesPase.setPreferredSize(new Dimension(265, 0));
        pLateralAccionesPase.setBorder(new EmptyBorder(0, 8, 0, 0));

        // 1. Apartado: Voucher Electrónico (SUNAT / Email)
        JPanel pVoucherEmail = new JPanel();
        pVoucherEmail.setLayout(new BoxLayout(pVoucherEmail, BoxLayout.Y_AXIS));
        pVoucherEmail.setOpaque(false);

        JLabel lblEmailDestino = new JLabel("Correo para Comprobante Digital:");
        lblEmailDestino.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 10.5f));
        lblEmailDestino.setForeground(Theme.getSecundario());
        lblEmailDestino.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.txtEmailConfirmacionPase = new JTextField(15);
        estilizarCampoTexto(this.txtEmailConfirmacionPase);
        this.txtEmailConfirmacionPase.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        this.txtEmailConfirmacionPase.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.txtEmailConfirmacionPase.setToolTipText("Correo al que se enviará la Boleta y Pase Electrónico.");

        BotonPrimario btnEnviarCorreo = new BotonPrimario("Enviar Voucher al Correo", Theme.getPasajeroAccent());
        btnEnviarCorreo.setFont(FontManager.getIBMPlexSans(Font.BOLD, 11f));
        btnEnviarCorreo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        btnEnviarCorreo.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnEnviarCorreo.addActionListener(e -> accionEnviarVoucherElectronico());

        this.btnAbrirVoucherHtml = new JButton("Ver Voucher Digital (HTML)");
        estilizarBotonAccion(this.btnAbrirVoucherHtml, Theme.getElevated(), Theme.getPasajeroAccent());
        this.btnAbrirVoucherHtml.setFont(FontManager.getIBMPlexSans(Font.BOLD, 10.5f));
        this.btnAbrirVoucherHtml.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        this.btnAbrirVoucherHtml.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.btnAbrirVoucherHtml.addActionListener(e -> accionAbrirVoucherHtml());

        JButton btnGmailWeb = new JButton("Abrir en Gmail Web ↗");
        estilizarBotonAccion(btnGmailWeb, Theme.getElevated(), Theme.getWarning());
        btnGmailWeb.setFont(FontManager.getIBMPlexSans(Font.BOLD, 10.5f));
        btnGmailWeb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        btnGmailWeb.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnGmailWeb.setToolTipText("Abre directamente su cuenta Gmail en el navegador con el voucher pre-cargado listo para enviar.");
        btnGmailWeb.addActionListener(e -> accionAbrirGmailWeb());

        JButton btnConfigSmtp = new JButton("Configurar Servidor SMTP");
        estilizarBotonAccion(btnConfigSmtp, Theme.getElevated(), Theme.getSecundario());
        btnConfigSmtp.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 10f));
        btnConfigSmtp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        btnConfigSmtp.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnConfigSmtp.setToolTipText("Configurar remitente y Contraseña de Aplicación de Google para envíos automáticos por SMTP SSL.");
        btnConfigSmtp.addActionListener(e -> accionAbrirConfiguracionSmtp());

        this.lblEstadoEnvioCorreoPase = new JLabel("Estado: Pendiente de despacho");
        this.lblEstadoEnvioCorreoPase.setFont(FontManager.getIBMPlexMono(Font.PLAIN, 9.5f));
        this.lblEstadoEnvioCorreoPase.setForeground(Theme.getMuted());
        this.lblEstadoEnvioCorreoPase.setAlignmentX(Component.LEFT_ALIGNMENT);

        pVoucherEmail.add(lblEmailDestino);
        pVoucherEmail.add(Box.createVerticalStrut(4));
        pVoucherEmail.add(this.txtEmailConfirmacionPase);
        pVoucherEmail.add(Box.createVerticalStrut(6));
        pVoucherEmail.add(btnEnviarCorreo);
        pVoucherEmail.add(Box.createVerticalStrut(4));
        pVoucherEmail.add(btnGmailWeb);
        pVoucherEmail.add(Box.createVerticalStrut(4));
        pVoucherEmail.add(this.btnAbrirVoucherHtml);
        pVoucherEmail.add(Box.createVerticalStrut(4));
        pVoucherEmail.add(btnConfigSmtp);
        pVoucherEmail.add(Box.createVerticalStrut(6));
        pVoucherEmail.add(this.lblEstadoEnvioCorreoPase);

        GlassPanel cardVoucherEmail = GlassPanel.crearApartado(
                "Voucher Electrónico", "Despacho digital SUNAT", Theme.getPasajeroAccent(), pVoucherEmail);
        cardVoucherEmail.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 2. Apartado: Impresión y Pase Físico
        JPanel pPaseFisico = new JPanel();
        pPaseFisico.setLayout(new BoxLayout(pPaseFisico, BoxLayout.Y_AXIS));
        pPaseFisico.setOpaque(false);

        JButton btnVoucherCorpac = new JButton("Emitir Pase con QR (Modal)");
        estilizarBotonAccion(btnVoucherCorpac, Theme.getElevated(), Theme.getSuccess());
        btnVoucherCorpac.setFont(FontManager.getIBMPlexSans(Font.BOLD, 11f));
        btnVoucherCorpac.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        btnVoucherCorpac.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnVoucherCorpac.addActionListener(e -> {
            actualizarPaseAbordarPasajero();
            abrirDialogoVoucherCorpac();
        });

        JButton btnImprimir = new JButton("Imprimir / Guardar Físico");
        estilizarBotonAccion(btnImprimir, Theme.getElevated(), Theme.getTexto());
        btnImprimir.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 10.5f));
        btnImprimir.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        btnImprimir.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnImprimir.addActionListener(e -> {
            actualizarPaseAbordarPasajero();
            abrirDialogoVoucherCorpac();
        });

        JButton btnActualizarPase = new JButton("Sincronizar Pase con BD");
        estilizarBotonAccion(btnActualizarPase, Theme.getElevated(), Theme.getTexto());
        btnActualizarPase.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 10.5f));
        btnActualizarPase.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        btnActualizarPase.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnActualizarPase.addActionListener(e -> {
            actualizarPaseAbordarPasajero();
            mostrarToast("Pase de abordar actualizado en pantalla.", Theme.getInfo());
        });

        pPaseFisico.add(btnVoucherCorpac);
        pPaseFisico.add(Box.createVerticalStrut(6));
        pPaseFisico.add(btnImprimir);
        pPaseFisico.add(Box.createVerticalStrut(6));
        pPaseFisico.add(btnActualizarPase);

        GlassPanel cardPaseFisico = GlassPanel.crearApartado(
                "Pase Físico e Impresión", "Boleto tradicional para sala", Theme.getSuccess(), pPaseFisico);
        cardPaseFisico.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 3. Apartado: Opciones y Navegación
        JPanel pNavegacion = new JPanel();
        pNavegacion.setLayout(new BoxLayout(pNavegacion, BoxLayout.Y_AXIS));
        pNavegacion.setOpaque(false);

        pNavegacion.add(this.btnNuevaReservaPase);
        pNavegacion.add(Box.createVerticalStrut(6));
        pNavegacion.add(this.btnVolverEquipajePase);

        GlassPanel cardNavegacion = GlassPanel.crearApartado(
                "Navegación", "Opciones del itinerario", Theme.getBorder(), pNavegacion);
        cardNavegacion.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pContenedorCards = new JPanel();
        pContenedorCards.setLayout(new BoxLayout(pContenedorCards, BoxLayout.Y_AXIS));
        pContenedorCards.setOpaque(false);
        pContenedorCards.add(cardVoucherEmail);
        pContenedorCards.add(Box.createVerticalStrut(8));
        pContenedorCards.add(cardPaseFisico);
        pContenedorCards.add(Box.createVerticalStrut(8));
        pContenedorCards.add(cardNavegacion);

        JScrollPane scrollLateralPase = new JScrollPane(pContenedorCards);
        scrollLateralPase.setBorder(null);
        scrollLateralPase.setOpaque(false);
        scrollLateralPase.getViewport().setOpaque(false);
        scrollLateralPase.getVerticalScrollBar().setUnitIncrement(14);
        scrollLateralPase.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        pLateralAccionesPase.add(scrollLateralPase, BorderLayout.CENTER);
        this.panelPasajeroPaseAbordar.add(pLateralAccionesPase, BorderLayout.EAST);

        return this.panelPasajeroPaseAbordar;
    }

    public void abrirDialogoVoucherSunat() {
        abrirDialogoVoucherCorpac();
    }

    public void abrirDialogoVoucherCorpac() {
        String dni = (txtDniPasajeroReserva != null) ? txtDniPasajeroReserva.getText().trim() : "";
        String nombre = (txtNombrePasajeroReserva != null) ? txtNombrePasajeroReserva.getText().trim() : "";

        if (nombre.isEmpty() || dni.length() != 8 || !dni.matches("\\d+")) {
            JOptionPane.showMessageDialog(this,
                    "Para emitir su Voucher oficial CORPAC, por favor ingrese sus datos personales (Nombres y DNI) en el Paso 2 (Selección de Asiento).",
                    "Datos Requeridos del Pasajero", JOptionPane.WARNING_MESSAGE);
            panelPestanas.setSelectedIndex(1);
            if (nombre.isEmpty() && txtNombrePasajeroReserva != null) txtNombrePasajeroReserva.requestFocus();
            else if (txtDniPasajeroReserva != null) txtDniPasajeroReserva.requestFocus();
            return;
        }

        if (pasajeroFilaSel < 0 || pasajeroColSel < 0) {
            JOptionPane.showMessageDialog(this,
                    "Para emitir su Voucher oficial CORPAC, debe seleccionar y confirmar su asiento en el Paso 2.",
                    "Asiento Requerido", JOptionPane.WARNING_MESSAGE);
            panelPestanas.setSelectedIndex(1);
            return;
        }

        Vuelo vAct = controlador.obtenerVueloActivo();
        String codigoVuelo = (vAct != null) ? vAct.obtenerCodigo() : "CP-2024";
        String origenVuelo = (vAct != null) ? vAct.obtenerOrigen() : "Lima";
        String destinoVuelo = (vAct != null) ? vAct.obtenerDestino() : "Arequipa";
        String horaSalida = (vAct != null) ? vAct.obtenerHoraSalida() : "08:30";
        String fechaVuelo = (vAct != null) ? vAct.obtenerFecha() : "2026-10-15";

        char letraSel = (char) ('A' + pasajeroColSel);
        String asientoCod = (pasajeroFilaSel + 1) + "" + letraSel;
        String claseNombre = (pasajeroFilaSel < 3) ? "Clase Ejecutiva VIP" : "Clase Turista Confort";

        double base = (vAct != null) ? vAct.obtenerPrecioBase() : 139.0;
        double suplemento = (pasajeroFilaSel < 3) ? (base * 0.30) : 0.0;
        double total = base + suplemento;

        String puerta = "Gate 01";
        ListaCircular lc = controlador.obtenerListaPuertas();
        if (lc != null && !lc.estaVacia()) {
            PuertaEmbarque pAct = lc.obtenerPuertaActual();
            if (pAct != null) puerta = pAct.obtenerNumeroPuerta();
        }

        String detEquipaje = "Equipaje de mano (10 kg incluido)";
        if (listaEquipaje != null && !listaEquipaje.estaVacia()) {
            NodoSimple n = listaEquipaje.obtenerCabeza();
            while (n != null) {
                if (n.dato != null && (n.dato.obtenerDniPasajero().equals(dni) || n.dato.obtenerCodigoVuelo().equalsIgnoreCase(codigoVuelo))) {
                    detEquipaje = n.dato.obtenerCodigo() + " (" + n.dato.obtenerPeso() + " kg - " + n.dato.obtenerTipo() + ")";
                    break;
                }
                n = n.siguiente;
            }
        }

        String localizador = "LIM-" + (Math.abs(codigoVuelo.hashCode()) % 90000 + 10000);
        String correo = (txtEmailPasajeroReserva != null && !txtEmailPasajeroReserva.getText().trim().isEmpty())
                ? txtEmailPasajeroReserva.getText().trim()
                : "pasajero.corpac@aeropuerto.gob.pe";

        DialogoVoucherSunat dlg = new DialogoVoucherSunat(this, nombre, dni, correo, codigoVuelo, origenVuelo, destinoVuelo,
                horaSalida, fechaVuelo, asientoCod, claseNombre, puerta, detEquipaje, base, suplemento, total, localizador);
        if (stepperPasajero != null) {
            stepperPasajero.setPasoCompletado(3, true);
        }
        mostrarToast("Voucher emitido y enviado por correo a " + correo, Theme.getSuccess());
        dlg.setVisible(true);
    }

    private void accionEnviarVoucherElectronico() {
        String dni = (txtDniPasajeroReserva != null) ? txtDniPasajeroReserva.getText().trim() : "";
        String nombre = (txtNombrePasajeroReserva != null) ? txtNombrePasajeroReserva.getText().trim() : "";
        String email = (txtEmailConfirmacionPase != null) ? txtEmailConfirmacionPase.getText().trim() : "";
        if (email.isEmpty() && txtEmailPasajeroReserva != null) {
            email = txtEmailPasajeroReserva.getText().trim();
        }

        if (nombre.isEmpty() || dni.length() != 8 || !dni.matches("\\d+")) {
            JOptionPane.showMessageDialog(this,
                    "Para despachar el Voucher Electrónico a su correo, ingrese sus datos personales (Nombre y DNI) en el Paso 2.",
                    "Datos Requeridos del Pasajero", JOptionPane.WARNING_MESSAGE);
            panelPestanas.setSelectedIndex(1);
            if (nombre.isEmpty() && txtNombrePasajeroReserva != null) txtNombrePasajeroReserva.requestFocus();
            else if (txtDniPasajeroReserva != null) txtDniPasajeroReserva.requestFocus();
            return;
        }

        if (pasajeroFilaSel < 0 || pasajeroColSel < 0) {
            JOptionPane.showMessageDialog(this,
                    "Para despachar el Voucher Electrónico, debe seleccionar y confirmar su asiento en el Paso 2.",
                    "Asiento Requerido", JOptionPane.WARNING_MESSAGE);
            panelPestanas.setSelectedIndex(1);
            return;
        }

        if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingrese un correo electrónico válido para recibir el Voucher Electrónico SUNAT.",
                    "Correo Electrónico Inválido", JOptionPane.WARNING_MESSAGE);
            if (txtEmailConfirmacionPase != null) txtEmailConfirmacionPase.requestFocus();
            return;
        }

        Vuelo vAct = controlador.obtenerVueloActivo();
        String codigoVuelo = (vAct != null) ? vAct.obtenerCodigo() : "CP-2024";
        String origenVuelo = (vAct != null) ? vAct.obtenerOrigen() : "Lima";
        String destinoVuelo = (vAct != null) ? vAct.obtenerDestino() : "Arequipa";
        String horaSalida = (vAct != null) ? vAct.obtenerHoraSalida() : "08:30";
        String fechaVuelo = (vAct != null) ? vAct.obtenerFecha() : "2026-10-15";

        char letraSel = (char) ('A' + pasajeroColSel);
        String asientoCod = (pasajeroFilaSel + 1) + "" + letraSel;
        String claseNombre = (pasajeroFilaSel < 3) ? "Clase Ejecutiva VIP" : "Clase Turista Confort";

        double base = (vAct != null) ? vAct.obtenerPrecioBase() : 139.0;
        double suplemento = (pasajeroFilaSel < 3) ? (base * 0.30) : 0.0;
        double total = base + suplemento;

        String puerta = "Gate 01";
        ListaCircular lc = controlador.obtenerListaPuertas();
        if (lc != null && !lc.estaVacia()) {
            PuertaEmbarque pAct = lc.obtenerPuertaActual();
            if (pAct != null) puerta = pAct.obtenerNumeroPuerta();
        }

        String localizador = "LIM-" + (Math.abs((codigoVuelo + dni).hashCode()) % 90000 + 10000);
        String numeroSerie = "B001-" + String.format(java.util.Locale.US, "%08d", Math.abs((codigoVuelo + dni).hashCode()) % 1000000);

        ServicioCorreo srvCorreo = ServicioCorreo.obtenerInstancia();
        String timestamp = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date());
        String textoPlano = srvCorreo.generarTextoPlanoBoleta(
                email, nombre, dni, numeroSerie, codigoVuelo, origenVuelo, destinoVuelo,
                fechaVuelo, horaSalida, asientoCod, claseNombre, puerta, total, localizador, timestamp);
        String htmlContenido = srvCorreo.generarPlantillaHtmlBoleta(
                email, nombre, dni, numeroSerie, codigoVuelo, origenVuelo, destinoVuelo,
                fechaVuelo, horaSalida, asientoCod, claseNombre, puerta, total, localizador, timestamp);

        ServicioCorreo.RegistroEnvioCorreo reg = srvCorreo.enviarBoletaPorCorreo(
                email, nombre, dni, numeroSerie, codigoVuelo, origenVuelo, destinoVuelo,
                fechaVuelo, horaSalida, asientoCod, claseNombre, puerta, total, localizador, textoPlano);

        this.ultimaRutaVoucherHtml = (reg != null) ? reg.getRutaArchivoHtml() : srvCorreo.obtenerUltimaRutaArchivoGenerado();

        if (btnAbrirVoucherHtml != null) {
            btnAbrirVoucherHtml.setEnabled(true);
        }
        if (stepperPasajero != null) {
            stepperPasajero.setPasoCompletado(3, true);
        }

        final String asuntoCorreo = "CORPAC AIRWAYS - Voucher y Boleta Electrónica " + numeroSerie + " [Reserva " + localizador + "]";
        final String finalEmail = email;

        if (srvCorreo.tieneCredencialesSmtpConfiguradas()) {
            if (lblEstadoEnvioCorreoPase != null) {
                lblEstadoEnvioCorreoPase.setText("Conectando con smtp.gmail.com:465...");
                lblEstadoEnvioCorreoPase.setForeground(Theme.getWarning());
            }
            mostrarToast("Conectando con servidor Gmail SMTP...", Theme.getWarning());

            SwingWorker<ServicioCorreo.ResultadoSmtp, Void> worker = new SwingWorker<ServicioCorreo.ResultadoSmtp, Void>() {
                @Override
                protected ServicioCorreo.ResultadoSmtp doInBackground() {
                    return srvCorreo.enviarSmtpDirecto(finalEmail, asuntoCorreo, htmlContenido, textoPlano);
                }

                @Override
                protected void done() {
                    try {
                        ServicioCorreo.ResultadoSmtp res = get();
                        if (res.isExitoso()) {
                            if (lblEstadoEnvioCorreoPase != null) {
                                lblEstadoEnvioCorreoPase.setText("[OK] Despachado por SMTP a: " + finalEmail);
                                lblEstadoEnvioCorreoPase.setForeground(Theme.getSuccess());
                            }
                            mostrarToast("Voucher enviado a su Gmail exitosamente", Theme.getSuccess());
                            JOptionPane.showMessageDialog(VentanaPrincipal.this,
                                    "¡Voucher Electrónico enviado exitosamente vía SMTP directo a su bandeja de entrada!\n\n" +
                                    "- Destinatario: " + finalEmail + "\n" +
                                    "- Servidor SMTP: " + srvCorreo.getServidorConfigurado() + ":" + srvCorreo.getPuertoConfigurado() + " (SSL)\n" +
                                    "- Serie y Número: " + numeroSerie + "\n" +
                                    "- Localizador: " + localizador + "\n\n" +
                                    "¡El comprobante oficial fue transferido a los servidores de correo!\n" +
                                    "Revise su bandeja de entrada o buzón principal de Gmail.",
                                    "Voucher Despachado por SMTP", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            if (lblEstadoEnvioCorreoPase != null) {
                                lblEstadoEnvioCorreoPase.setText("[!] Aviso SMTP (" + res.getCodigoSmtp() + ")");
                                lblEstadoEnvioCorreoPase.setForeground(Theme.getWarning());
                            }
                            int opt = JOptionPane.showConfirmDialog(VentanaPrincipal.this,
                                    "El servidor SMTP reportó: " + res.getMensaje() + "\n\n" +
                                    "¿Desea abrir Gmail Web directamente en su navegador para enviar el voucher a " + finalEmail + " con 1 solo clic?",
                                    "Aviso de Transmisión SMTP", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                            if (opt == JOptionPane.YES_OPTION) {
                                srvCorreo.abrirGmailWeb(finalEmail, asuntoCorreo, textoPlano);
                                if (lblEstadoEnvioCorreoPase != null) {
                                    lblEstadoEnvioCorreoPase.setText("[OK] Abierto en Gmail Web para envío");
                                    lblEstadoEnvioCorreoPase.setForeground(Theme.getSuccess());
                                }
                            }
                        }
                    } catch (Exception ex) {
                        if (lblEstadoEnvioCorreoPase != null) {
                            lblEstadoEnvioCorreoPase.setText("[!] Error de conexión SMTP");
                            lblEstadoEnvioCorreoPase.setForeground(Theme.getDanger());
                        }
                    }
                }
            };
            worker.execute();

        } else {
            String[] opciones = {"Abrir en Gmail Web (Recomendado)", "Configurar Servidor SMTP", "Ver Archivo HTML"};
            int sel = JOptionPane.showOptionDialog(this,
                    "¡Comprobante digital SUNAT generado con éxito!\n\n" +
                    "- Destinatario: " + email + "\n" +
                    "- Comprobante: " + numeroSerie + "\n" +
                    "- Localizador de Reserva: " + localizador + "\n\n" +
                    "¿Cómo desea enviar el Voucher a su correo (" + email + ")?\n\n" +
                    "1. 'Abrir en Gmail Web' (Recomendado): Abre su navegador con Gmail y el voucher precargado listo para enviar en 1 clic.\n" +
                    "2. 'Configurar Servidor SMTP': Ingrese su usuario y Contraseña de Aplicación para envíos automáticos 100% en segundo plano.\n" +
                    "3. 'Ver Archivo HTML': Abre el comprobante digital interactivo guardado en su disco local.",
                    "Despacho Electrónico a Gmail",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                    opciones, opciones[0]);

            if (sel == 0) {
                boolean ok = srvCorreo.abrirGmailWeb(email, asuntoCorreo, textoPlano);
                if (ok) {
                    if (lblEstadoEnvioCorreoPase != null) {
                        lblEstadoEnvioCorreoPase.setText("[OK] Abierto en Gmail Web -> Listo para enviar");
                        lblEstadoEnvioCorreoPase.setForeground(Theme.getSuccess());
                    }
                    mostrarToast("Abriendo Gmail Web con el voucher cargado...", Theme.getSuccess());
                }
            } else if (sel == 1) {
                accionAbrirConfiguracionSmtp();
            } else if (sel == 2) {
                accionAbrirVoucherHtml();
            }
        }
    }

    private void accionAbrirGmailWeb() {
        String dni = (txtDniPasajeroReserva != null) ? txtDniPasajeroReserva.getText().trim() : "";
        String nombre = (txtNombrePasajeroReserva != null) ? txtNombrePasajeroReserva.getText().trim() : "";
        String email = (txtEmailConfirmacionPase != null) ? txtEmailConfirmacionPase.getText().trim() : "";
        if (email.isEmpty() && txtEmailPasajeroReserva != null) {
            email = txtEmailPasajeroReserva.getText().trim();
        }
        if (email.isEmpty() || !email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Ingrese un correo válido en el campo 'Correo Electrónico para Voucher Digital'.", "Correo Requerido", JOptionPane.WARNING_MESSAGE);
            if (txtEmailConfirmacionPase != null) txtEmailConfirmacionPase.requestFocus();
            return;
        }

        Vuelo vAct = controlador.obtenerVueloActivo();
        String codigoVuelo = (vAct != null) ? vAct.obtenerCodigo() : "CP-2024";
        String origenVuelo = (vAct != null) ? vAct.obtenerOrigen() : "Lima";
        String destinoVuelo = (vAct != null) ? vAct.obtenerDestino() : "Arequipa";
        String horaSalida = (vAct != null) ? vAct.obtenerHoraSalida() : "08:30";
        String fechaVuelo = (vAct != null) ? vAct.obtenerFecha() : "2026-10-15";

        char letraSel = (char) ('A' + (pasajeroColSel >= 0 ? pasajeroColSel : 0));
        String asientoCod = (pasajeroFilaSel >= 0 ? (pasajeroFilaSel + 1) : 1) + "" + letraSel;
        String claseNombre = (pasajeroFilaSel < 3 && pasajeroFilaSel >= 0) ? "Clase Ejecutiva VIP" : "Clase Turista Confort";

        double base = (vAct != null) ? vAct.obtenerPrecioBase() : 139.0;
        double suplemento = (pasajeroFilaSel < 3 && pasajeroFilaSel >= 0) ? (base * 0.30) : 0.0;
        double total = base + suplemento;

        String puerta = "Gate 01";
        ListaCircular lc = controlador.obtenerListaPuertas();
        if (lc != null && !lc.estaVacia()) {
            PuertaEmbarque pAct = lc.obtenerPuertaActual();
            if (pAct != null) puerta = pAct.obtenerNumeroPuerta();
        }

        String localizador = "LIM-" + (Math.abs((codigoVuelo + dni).hashCode()) % 90000 + 10000);
        String numeroSerie = "B001-" + String.format(java.util.Locale.US, "%08d", Math.abs((codigoVuelo + dni).hashCode()) % 1000000);
        String timestamp = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date());

        ServicioCorreo srv = ServicioCorreo.obtenerInstancia();
        String textoPlano = srv.generarTextoPlanoBoleta(
                email, nombre.isEmpty() ? "PASAJERO CORPAC" : nombre, dni.isEmpty() ? "00000000" : dni,
                numeroSerie, codigoVuelo, origenVuelo, destinoVuelo, fechaVuelo, horaSalida,
                asientoCod, claseNombre, puerta, total, localizador, timestamp);

        String asunto = "CORPAC AIRWAYS - Voucher y Boleta Electrónica " + numeroSerie + " [Reserva " + localizador + "]";
        boolean ok = srv.abrirGmailWeb(email, asunto, textoPlano);
        if (ok) {
            if (lblEstadoEnvioCorreoPase != null) {
                lblEstadoEnvioCorreoPase.setText("[OK] Abierto en Gmail Web -> Listo para enviar");
                lblEstadoEnvioCorreoPase.setForeground(Theme.getSuccess());
            }
            mostrarToast("Abriendo Gmail Web con su voucher cargado...", Theme.getSuccess());
        }
    }

    private void accionAbrirConfiguracionSmtp() {
        DialogoConfiguracionCorreo dlg = new DialogoConfiguracionCorreo(this);
        dlg.setVisible(true);
    }

    private void accionAbrirVoucherHtml() {
        try {
            String ruta = this.ultimaRutaVoucherHtml;
            if (ruta == null || ruta.isEmpty()) {
                ruta = ServicioCorreo.obtenerInstancia().obtenerUltimaRutaArchivoGenerado();
            }
            if (ruta != null) {
                File archivo = new File(ruta);
                if (archivo.exists() && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(archivo);
                    mostrarToast("Abriendo comprobante electrónico en navegador...", Theme.getInfo());
                    return;
                }
            }
            JOptionPane.showMessageDialog(this,
                    "No se encontró el archivo HTML del voucher o aún no ha sido despachado.",
                    "Archivo no Encontrado", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo abrir automáticamente el voucher: " + ex.getMessage(),
                    "Error al Abrir", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarVueloPasajeroPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) return;
        Vuelo v = controlador.buscarVueloPorCodigo(codigo);
        if (v != null) {
            String codAnterior = controlador.obtenerCodigoVueloActivo();
            if (codAnterior == null || !codAnterior.equalsIgnoreCase(v.obtenerCodigo())) {
                this.pasajeroFilaSel = -1;
                this.pasajeroColSel = -1;
                this.pasajeroFilaConfirmada = -1;
                this.pasajeroColConfirmada = -1;
            }
            controlador.seleccionarVueloActivo(v.obtenerCodigo());
            if (cbClaseCabinaPasajero != null) {
                String cv = v.obtenerClase();
                if (cv != null && cv.contains("Turista")) {
                    cbClaseCabinaPasajero.setSelectedItem("Clase Turista");
                } else {
                    cbClaseCabinaPasajero.setSelectedItem("Clase Ejecutiva (VIP)");
                }
            }
            actualizarCabinaPasajero();
            actualizarPaseAbordarPasajero();
            actualizarBannerVueloSeleccionado();
            actualizarMatrizCabina();
            actualizarEtiquetaVueloActivo();
            actualizarMetricasDashboard();

            if (modeloTablaVuelosPasajero != null) {
                for (int r = 0; r < modeloTablaVuelosPasajero.getRowCount(); r++) {
                    if (codigo.equalsIgnoreCase((String) modeloTablaVuelosPasajero.getValueAt(r, 0))) {
                        tablaVuelosPasajero.setRowSelectionInterval(r, r);
                        break;
                    }
                }
            }
            if (cbFiltroDestinoPasajero != null) {
                actualizarTablaVuelosPasajero();
            }
        }
    }

    private void actualizarTablaVuelosPasajero() {
        if (modeloTablaVuelosPasajero == null) return;
        modeloTablaVuelosPasajero.setRowCount(0);
        Vuelo[] vuelos = controlador.obtenerVuelos();
        int total = controlador.obtenerContadorVuelos();
        String filtroDestino = (cbFiltroDestinoPasajero != null) ? (String) cbFiltroDestinoPasajero.getSelectedItem() : "Todos";
        String filtroTurno = (cbFiltroTurnoPasajero != null) ? (String) cbFiltroTurnoPasajero.getSelectedItem() : "Todos los Horarios";
        String filtroClase = (cbFiltroClasePasajero != null) ? (String) cbFiltroClasePasajero.getSelectedItem() : "Todas las Clases";

        java.util.List<Vuelo> listaCoincidentes = new java.util.ArrayList<>();

        for (int i = 0; i < total; i++) {
            Vuelo v = vuelos[i];
            if (v != null) {
                if (filtroDestino != null && !"Todos".equalsIgnoreCase(filtroDestino)) {
                    String dVuelo = v.obtenerDestino().toLowerCase();
                    String dFiltro = filtroDestino.toLowerCase();
                    if (!dVuelo.contains(dFiltro) && !dFiltro.contains(dVuelo)) {
                        continue;
                    }
                }
                if (filtroTurno != null && !"Todos los Horarios".equalsIgnoreCase(filtroTurno)) {
                    int hora = 0;
                    try {
                        String hStr = v.obtenerHoraSalida().split(":")[0];
                        hora = Integer.parseInt(hStr.trim());
                    } catch (Exception ignored) {}
                    if (filtroTurno.startsWith("Mañana") && (hora < 6 || hora >= 12)) continue;
                    if (filtroTurno.startsWith("Tarde") && (hora < 12 || hora >= 18)) continue;
                    if (filtroTurno.startsWith("Noche") && (hora < 18 || hora >= 24)) continue;
                }
                if (filtroClase != null && !"Todas las Clases".equalsIgnoreCase(filtroClase)) {
                    String cVuelo = v.obtenerClaseDisponible().toLowerCase();
                    String cFiltro = filtroClase.toLowerCase();
                    boolean coincideClase = cVuelo.contains(cFiltro) ||
                            ("turista".equalsIgnoreCase(cFiltro) && cVuelo.contains("comercial"));
                    if (!coincideClase) {
                        continue;
                    }
                }

                listaCoincidentes.add(v);

                String estadoDefecto = (i == 0) ? "En Vuelo" : (i == 1) ? "Abordando" : (i % 3 == 0) ? "Retrasado" : "A Tiempo";
                modeloTablaVuelosPasajero.addRow(new Object[]{
                        v.obtenerCodigo(),
                        v.obtenerOrigen() + " -> " + v.obtenerDestino(),
                        v.obtenerFecha(),
                        v.obtenerHoraSalida(),
                        v.obtenerHoraLlegada(),
                        v.obtenerDuracion() + " min",
                        "S/. " + String.format(java.util.Locale.US, "%.2f", v.obtenerPrecioBase()),
                        v.obtenerClaseDisponible(),
                        v.contarAsientosLibres() + " / 50",
                        estadoDefecto
                });
            }
        }

        // Sincronización automática de selección:
        // Si el vuelo activo no está en la lista de resultados del filtro (o es nulo),
        // seleccionar automáticamente el primer vuelo coincidente para garantizar coherencia en la UI
        Vuelo activoActual = controlador.obtenerVueloActivo();
        boolean activoEnLista = false;
        if (activoActual != null && !listaCoincidentes.isEmpty()) {
            for (Vuelo vc : listaCoincidentes) {
                if (vc.obtenerCodigo().equalsIgnoreCase(activoActual.obtenerCodigo())) {
                    activoEnLista = true;
                    break;
                }
            }
        }
        if (!activoEnLista && !listaCoincidentes.isEmpty()) {
            controlador.seleccionarVueloActivo(listaCoincidentes.get(0).obtenerCodigo());
        }

        actualizarTarjetasVariaciones(listaCoincidentes);
        actualizarBannerVueloSeleccionado();
    }

    // HUMANIZACION UI - Tarjetas de vuelo ejecutivas modernas (scroll vertical fluido, estilo LATAM / Google Flights)
    private void actualizarTarjetasVariaciones(java.util.List<Vuelo> lista) {
        if (panelTarjetasVariaciones == null) return;
        panelTarjetasVariaciones.removeAll();

        if (lista == null || lista.isEmpty()) {
            JPanel pVacio = new JPanel(new BorderLayout());
            pVacio.setOpaque(false);
            JLabel lblVacio = new JLabel("No se encontraron variaciones para el filtro seleccionado. Seleccione otro turno o destino.", SwingConstants.CENTER);
            lblVacio.setForeground(Theme.getMuted());
            lblVacio.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 12f));
            pVacio.add(lblVacio, BorderLayout.CENTER);
            panelTarjetasVariaciones.add(pVacio);
            panelTarjetasVariaciones.revalidate();
            panelTarjetasVariaciones.repaint();
            return;
        }

        Vuelo activo = controlador.obtenerVueloActivo();
        String codActivo = (activo != null) ? activo.obtenerCodigo() : "";

        for (int i = 0; i < lista.size(); i++) {
            Vuelo v = lista.get(i);
            boolean esSeleccionado = v.obtenerCodigo().equalsIgnoreCase(codActivo);

            TarjetaTurnoVuelo tarjeta = new TarjetaTurnoVuelo(v, i, esSeleccionado, e -> {
                seleccionarVueloPasajeroPorCodigo(v.obtenerCodigo());
            }, e -> {
                seleccionarVueloPasajeroPorCodigo(v.obtenerCodigo());
                actualizarCabinaPasajero();
                actualizarPaseAbordarPasajero();
                panelPestanas.setSelectedIndex(1);
            });
            panelTarjetasVariaciones.add(tarjeta);
            panelTarjetasVariaciones.add(Box.createVerticalStrut(8));
        }

        panelTarjetasVariaciones.revalidate();
        panelTarjetasVariaciones.repaint();
    }

    // HUMANIZACION UI - Strip informativo de vuelo seleccionado y Resumen de Reserva
    private void actualizarBannerVueloSeleccionado() {
        Vuelo v = controlador.obtenerVueloActivo();
        if (stripVueloSeleccionado != null) {
            stripVueloSeleccionado.actualizarVuelo(v);
        }
        if (lblBannerVueloSeleccionado != null) {
            if (v != null) {
                lblBannerVueloSeleccionado.setText("Vuelo Seleccionado: " + v.obtenerCodigo() + " (" +
                        v.obtenerOrigen() + " -> " + v.obtenerDestino() + ") | Salida: " + v.obtenerHoraSalida() +
                        " | Base: S/. " + String.format(java.util.Locale.US, "%.2f", v.obtenerPrecioBase()) +
                        " | Libres: " + v.contarAsientosLibres() + "/50");
            } else {
                lblBannerVueloSeleccionado.setText("Sin vuelo seleccionado. Elija un destino y horario para comenzar.");
            }
        }
        if (lblResumenCodigoVuelo != null) {
            if (v != null) {
                lblResumenCodigoVuelo.setText(v.obtenerCodigo() + " · Airbus A320neo");
                if (lblResumenRuta != null) lblResumenRuta.setText(v.obtenerOrigen() + " [LIM] -> " + v.obtenerDestino() + " [" + obtenerIata(v.obtenerDestino()) + "]");
                if (lblResumenHorario != null) lblResumenHorario.setText("Salida: " + v.obtenerHoraSalida() + " | Llegada: " + v.obtenerHoraLlegada() + " (" + v.obtenerDuracion() + " min)");
                if (lblResumenTarifa != null) lblResumenTarifa.setText("Tarifa Base: S/. " + String.format(java.util.Locale.US, "%.2f", v.obtenerPrecioBase()));
                if (lblResumenAsientos != null) {
                    int libres = v.contarAsientosLibres();
                    lblResumenAsientos.setText(libres + " Asientos Libres (Clase " + v.obtenerClaseDisponible() + ")");
                    lblResumenAsientos.setForeground(libres > 15 ? Theme.getSuccess() : Theme.getWarning());
                }
            } else {
                lblResumenCodigoVuelo.setText("Seleccione un vuelo");
                if (lblResumenRuta != null) lblResumenRuta.setText("Lima [LIM] -> ---");
                if (lblResumenHorario != null) lblResumenHorario.setText("Horario por definir");
                if (lblResumenTarifa != null) lblResumenTarifa.setText("Tarifa: S/. 0.00");
                if (lblResumenAsientos != null) lblResumenAsientos.setText("0 Asientos");
            }
        }
    }

    private void seleccionarAsientoPasajero(int fila, int col) {
        if (pasajeroFilaConfirmada >= 0) {
            char letConf = (char) ('A' + pasajeroColConfirmada);
            JOptionPane.showMessageDialog(this,
                    "Su reserva ya ha sido confirmada con el asiento " + (pasajeroFilaConfirmada + 1) + letConf + ".\nNo es posible modificar ni seleccionar otros asientos.",
                    "Reserva Bloqueada", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Vuelo v = controlador.obtenerVueloActivo();
        if (v == null) return;
        int[][] mat = v.obtenerMatrizAsientos();
        String dniActual = (txtDniPasajeroReserva != null) ? txtDniPasajeroReserva.getText().trim() : "";
        String[][] matPas = v.obtenerMatrizPasajeros();

        boolean esMiPropioAsiento = (fila == pasajeroFilaConfirmada && col == pasajeroColConfirmada)
                || (!dniActual.isEmpty() && matPas != null && dniActual.equals(matPas[fila][col]));

        if (mat[fila][col] == 1 && !esMiPropioAsiento) {
            char letra = (char) ('A' + col);
            JOptionPane.showMessageDialog(this,
                    "El asiento " + (fila + 1) + letra + " ya está reservado por otro pasajero.\nPor favor seleccione uno disponible.",
                    "Asiento No Disponible", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Restricción de clase según la clase elegida (combo en Cabina o filtro Paso 1)
        String claseElegida = null;
        if (cbClaseCabinaPasajero != null && cbClaseCabinaPasajero.getSelectedItem() != null) {
            claseElegida = (String) cbClaseCabinaPasajero.getSelectedItem();
        } else if (cbFiltroClasePasajero != null && cbFiltroClasePasajero.getSelectedItem() != null) {
            String f = (String) cbFiltroClasePasajero.getSelectedItem();
            if (!"Todas las Clases".equalsIgnoreCase(f)) {
                claseElegida = f;
            }
        }
        if (claseElegida == null || claseElegida.isEmpty() || claseElegida.contains("Todas")) {
            claseElegida = (v.obtenerClase() != null) ? v.obtenerClase() : "Ejecutiva";
        }

        boolean esEjecutiva = claseElegida.contains("Ejecutiva") || claseElegida.contains("VIP");
        boolean esTurista = !esEjecutiva;
        boolean esFilaVip = (fila < 3);

        char letra = (char) ('A' + col);
        if (esEjecutiva && !esFilaVip) {
            JOptionPane.showMessageDialog(this,
                    "No es de esta clase. Asiento bloqueado.\n\n" +
                    "- Su clase seleccionada es: Clase Ejecutiva (VIP).\n" +
                    "- El asiento " + (fila + 1) + letra + " pertenece a la Clase Turista.\n\n" +
                    "Para seleccionar este asiento, cambie la clase a 'Clase Turista' en el selector superior.",
                    "Restricción de Categoría", JOptionPane.WARNING_MESSAGE);
            return;
        } else if (esTurista && esFilaVip) {
            JOptionPane.showMessageDialog(this,
                    "No es de esta clase. Asiento bloqueado.\n\n" +
                    "- Su clase seleccionada es: Clase Turista Confort.\n" +
                    "- El asiento " + (fila + 1) + letra + " pertenece a la Clase Ejecutiva VIP.\n\n" +
                    "Para seleccionar este asiento, cambie la clase a 'Clase Ejecutiva (VIP)' en el selector superior.",
                    "Restricción de Categoría", JOptionPane.WARNING_MESSAGE);
            return;
        }

        this.pasajeroFilaSel = fila;
        this.pasajeroColSel = col;
        actualizarCabinaPasajero();
        validarDniEnVivo();
    }

    private void actualizarCabinaPasajero() {
        Vuelo v = controlador.obtenerVueloActivo();
        if (v == null) {
            if (controlador.obtenerContadorVuelos() > 0) {
                controlador.seleccionarVueloActivo(controlador.obtenerVuelos()[0].obtenerCodigo());
                v = controlador.obtenerVueloActivo();
            }
        }
        if (v == null) return;

        int[][] mat = v.obtenerMatrizAsientos();
        if (mat == null) return;

        // Limpiar asientos duplicados en este vuelo para el mismo DNI (regla: 1 pasajero = 1 asiento)
        String dniActual = (txtDniPasajeroReserva != null) ? txtDniPasajeroReserva.getText().trim() : "";
        if (!dniActual.isEmpty() && dniActual.length() == 8) {
            String[][] matPas = v.obtenerMatrizPasajeros();
            if (matPas != null) {
                for (int f = 0; f < 10; f++) {
                    for (int c = 0; c < 5; c++) {
                        if (dniActual.equals(matPas[f][c]) && (f != pasajeroFilaConfirmada || c != pasajeroColConfirmada)) {
                            controlador.liberarAsiento(f, c);
                        }
                    }
                }
            }
        }

        int ocupados = v.contarAsientosOcupados();
        int libres = 50 - ocupados;

        // Auto-seleccionar primer asiento libre disponible solo si el asiento seleccionado por el usuario fue ocupado por otro
        if (pasajeroFilaSel >= 0 && pasajeroFilaSel < 10 && pasajeroColSel >= 0 && pasajeroColSel < 5) {
            if (mat[pasajeroFilaSel][pasajeroColSel] == 1 && (pasajeroFilaSel != pasajeroFilaConfirmada || pasajeroColSel != pasajeroColConfirmada)) {
                boolean encontrado = false;
                for (int f = 0; f < 10 && !encontrado; f++) {
                    for (int c = 0; c < 5 && !encontrado; c++) {
                        if (mat[f][c] == 0) {
                            this.pasajeroFilaSel = f;
                            this.pasajeroColSel = c;
                            encontrado = true;
                        }
                    }
                }
                if (!encontrado) {
                    pasajeroFilaSel = -1;
                    pasajeroColSel = -1;
                }
            }
        }

        if (lblCabinaVueloActivoInfo != null) {
            lblCabinaVueloActivoInfo.setText("CABINA COMERCIAL AIRBUS A320neo | Vuelo Activo: " +
                    v.obtenerCodigo() + " (" + v.obtenerOrigen() + " -> " + v.obtenerDestino() + ") | Salida: " + v.obtenerHoraSalida());
        }
        String claseElegida = null;
        if (cbClaseCabinaPasajero != null && cbClaseCabinaPasajero.getSelectedItem() != null) {
            claseElegida = (String) cbClaseCabinaPasajero.getSelectedItem();
        } else if (cbFiltroClasePasajero != null && cbFiltroClasePasajero.getSelectedItem() != null) {
            String f = (String) cbFiltroClasePasajero.getSelectedItem();
            if (!"Todas las Clases".equalsIgnoreCase(f)) {
                claseElegida = f;
            }
        }
        if (claseElegida == null || claseElegida.isEmpty() || claseElegida.contains("Todas")) {
            claseElegida = (v.obtenerClase() != null) ? v.obtenerClase() : "Ejecutiva";
        }

        boolean esEjecutiva = claseElegida.contains("Ejecutiva") || claseElegida.contains("VIP");
        boolean esTurista = !esEjecutiva;

        // Mantener sincronizado el combo de cabina si no coincidía
        if (cbClaseCabinaPasajero != null) {
            String optDeseada = esEjecutiva ? "Clase Ejecutiva (VIP)" : "Clase Turista";
            if (!optDeseada.equals(cbClaseCabinaPasajero.getSelectedItem())) {
                cbClaseCabinaPasajero.setSelectedItem(optDeseada);
            }
        }

        boolean yaConfirmado = (pasajeroFilaConfirmada >= 0);

        if (txtDniPasajeroReserva != null) txtDniPasajeroReserva.setEditable(!yaConfirmado);
        if (txtNombrePasajeroReserva != null) txtNombrePasajeroReserva.setEditable(!yaConfirmado);
        if (txtEmailPasajeroReserva != null) txtEmailPasajeroReserva.setEditable(!yaConfirmado);
        if (cbClaseCabinaPasajero != null) cbClaseCabinaPasajero.setEnabled(!yaConfirmado);
        if (btnAutoAsignarPasajero != null) btnAutoAsignarPasajero.setEnabled(!yaConfirmado);
        if (btnVolverVuelosCabina != null) btnVolverVuelosCabina.setEnabled(!yaConfirmado);
        if (btnVolverCabinaEquipaje != null) btnVolverCabinaEquipaje.setEnabled(!yaConfirmado);

        if (lblVipBannerCabina != null) {
            if (esEjecutiva) {
                lblVipBannerCabina.setText("* CLASE EJECUTIVA VIP - Filas 1 a 3 (HABILITADA · Selección Activa)");
                lblVipBannerCabina.setForeground(Theme.getWarning());
            } else {
                lblVipBannerCabina.setText("🔒 CLASE EJECUTIVA VIP - Filas 1 a 3 (BLOQUEADA · Exclusivo Clase Ejecutiva)");
                lblVipBannerCabina.setForeground(new Color(100, 116, 139));
            }
        }
        if (lblTuristaBannerCabina != null) {
            if (esTurista) {
                lblTuristaBannerCabina.setText("* CLASE TURISTA CONFORT - Filas 4 a 10 (HABILITADA · Selección Activa)");
                lblTuristaBannerCabina.setForeground(Theme.getSuccess());
            } else {
                lblTuristaBannerCabina.setText("🔒 CLASE TURISTA CONFORT - Filas 4 a 10 (BLOQUEADA · Exclusivo Clase Turista)");
                lblTuristaBannerCabina.setForeground(new Color(100, 116, 139));
            }
        }

        if (lblCabinaResumenLibres != null) {
            if (yaConfirmado) {
                char letC = (char) ('A' + pasajeroColConfirmada);
                String codC = (pasajeroFilaConfirmada + 1) + "" + letC;
                lblCabinaResumenLibres.setText("Reserva de asiento " + codC + " confirmada. Cabina asegurada contra modificaciones.");
                lblCabinaResumenLibres.setForeground(Theme.getSuccess());
            } else if (esEjecutiva) {
                lblCabinaResumenLibres.setText("Modo Clase Ejecutiva: Solo Filas 1 a 3 (VIP) habilitadas. Filas 4-10 bloqueadas (🔒).");
                lblCabinaResumenLibres.setForeground(Theme.getSecundario());
            } else {
                lblCabinaResumenLibres.setText("Modo Clase Turista: Solo Filas 4 a 10 habilitadas. Filas 1-3 VIP bloqueadas (🔒).");
                lblCabinaResumenLibres.setForeground(Theme.getSecundario());
            }
        }

        // Si la selección tentativa quedó en una fila bloqueada por clase, reubicarla automáticamente
        if (!yaConfirmado && pasajeroFilaSel >= 0 && pasajeroColSel >= 0) {
            boolean filaVipSel = (pasajeroFilaSel < 3);
            if ((esEjecutiva && !filaVipSel) || (esTurista && filaVipSel)) {
                int fIni = esTurista ? 3 : 0;
                int fFin = esEjecutiva ? 3 : 10;
                boolean reasignado = false;
                for (int f = fIni; f < fFin && !reasignado; f++) {
                    for (int c = 0; c < 5; c++) {
                        if (mat[f][c] == 0) {
                            pasajeroFilaSel = f;
                            pasajeroColSel = c;
                            reasignado = true;
                            break;
                        }
                    }
                }
                if (!reasignado) {
                    pasajeroFilaSel = -1;
                    pasajeroColSel = -1;
                }
            }
        }

        for (int f = 0; f < 10; f++) {
            for (int c = 0; c < 5; c++) {
                JButton btn = botonesAsientosPasajero[f][c];
                if (btn == null) continue;

                char letra = (char) ('A' + c);
                String codigoAsiento = (f + 1) + "" + letra;
                String tipoUbic = (c == 0 || c == 4) ? "Ventana" : (c == 1 || c == 3) ? "Pasillo" : "Centro";
                boolean esVip = (f < 3);
                boolean bloqueadoPorClase = (esEjecutiva && !esVip) || (esTurista && esVip);

                if (yaConfirmado) {
                    btn.setEnabled(false);
                    btn.setCursor(Cursor.getDefaultCursor());
                    if (f == pasajeroFilaConfirmada && c == pasajeroColConfirmada) {
                        if (btn instanceof BotonAsientoCabina) {
                            ((BotonAsientoCabina) btn).setEstadoAsiento(BotonAsientoCabina.EstadoAsiento.CONFIRMADO);
                        } else {
                            btn.setBackground(new Color(2, 132, 199));
                            btn.setForeground(Color.WHITE);
                            btn.setBorder(new LineBorder(new Color(56, 189, 248), 2));
                        }
                        btn.setText(codigoAsiento + " *");
                        btn.setToolTipText("Asiento " + codigoAsiento + " (" + tipoUbic + ") | SU ASIENTO CONFIRMADO (Bloqueado)");
                    } else if (mat[f][c] == 1) {
                        if (btn instanceof BotonAsientoCabina) {
                            ((BotonAsientoCabina) btn).setEstadoAsiento(BotonAsientoCabina.EstadoAsiento.OCUPADO);
                        }
                        btn.setText(codigoAsiento + " X");
                        btn.setToolTipText("Asiento " + codigoAsiento + " (" + tipoUbic + ") | OCUPADO");
                    } else if (bloqueadoPorClase) {
                        if (btn instanceof BotonAsientoCabina) {
                            ((BotonAsientoCabina) btn).setEstadoAsiento(BotonAsientoCabina.EstadoAsiento.BLOQUEADO_CLASE);
                        }
                        btn.setText(codigoAsiento + " 🔒");
                        btn.setToolTipText("Asiento bloqueado por categoría de clase");
                    } else {
                        if (btn instanceof BotonAsientoCabina) {
                            ((BotonAsientoCabina) btn).setEstadoAsiento(esVip ? BotonAsientoCabina.EstadoAsiento.LIBRE_VIP : BotonAsientoCabina.EstadoAsiento.LIBRE_TURISTA);
                        }
                        btn.setText(codigoAsiento);
                        btn.setToolTipText("Asiento " + codigoAsiento + " | Libre (No modificable: reserva ya confirmada)");
                    }
                } else if (f == pasajeroFilaSel && c == pasajeroColSel && !bloqueadoPorClase) {
                    btn.setEnabled(true);
                    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    if (btn instanceof BotonAsientoCabina) {
                        ((BotonAsientoCabina) btn).setEstadoAsiento(BotonAsientoCabina.EstadoAsiento.SELECCIONADO);
                    } else {
                        btn.setBackground(esVip ? new Color(217, 119, 6) : new Color(14, 116, 144));
                        btn.setForeground(Color.WHITE);
                        btn.setBorder(new LineBorder(Theme.esModoOscuro() ? Color.WHITE : new Color(15, 23, 42), 2));
                    }
                    btn.setText("[" + codigoAsiento + "]");
                    btn.setToolTipText("¡Su asiento seleccionado actualmente!");
                } else if (mat[f][c] == 1) {
                    btn.setEnabled(false);
                    btn.setCursor(Cursor.getDefaultCursor());
                    if (btn instanceof BotonAsientoCabina) {
                        ((BotonAsientoCabina) btn).setEstadoAsiento(BotonAsientoCabina.EstadoAsiento.OCUPADO);
                    } else {
                        if (Theme.esModoOscuro()) {
                            btn.setBackground(new Color(24, 32, 47));
                            btn.setForeground(new Color(100, 116, 139));
                            btn.setBorder(new LineBorder(new Color(45, 55, 72), 1));
                        } else {
                            btn.setBackground(new Color(225, 230, 238));
                            btn.setForeground(new Color(100, 116, 139));
                            btn.setBorder(new LineBorder(new Color(203, 213, 225), 1));
                        }
                    }
                    btn.setText(codigoAsiento + " X");
                    btn.setToolTipText("Asiento " + codigoAsiento + " (" + tipoUbic + ") | OCUPADO");
                } else if (bloqueadoPorClase) {
                    btn.setEnabled(true);
                    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    if (btn instanceof BotonAsientoCabina) {
                        ((BotonAsientoCabina) btn).setEstadoAsiento(BotonAsientoCabina.EstadoAsiento.BLOQUEADO_CLASE);
                    } else {
                        btn.setBackground(new Color(20, 24, 33));
                        btn.setForeground(new Color(75, 85, 99));
                        btn.setBorder(new LineBorder(new Color(38, 44, 55), 1));
                    }
                    btn.setText(codigoAsiento + " 🔒");
                    btn.setToolTipText("No es de esta clase: " + (esEjecutiva ? "Pertenece a Turista. Cambie de clase para elegirlo." : "Pertenece a Ejecutiva VIP. Cambie de clase para elegirlo."));
                } else {
                    btn.setEnabled(true);
                    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    if (esVip) {
                        if (btn instanceof BotonAsientoCabina) {
                            ((BotonAsientoCabina) btn).setEstadoAsiento(BotonAsientoCabina.EstadoAsiento.LIBRE_VIP);
                        } else {
                            if (Theme.esModoOscuro()) {
                                btn.setBackground(new Color(36, 28, 14));
                                btn.setForeground(new Color(251, 191, 36));
                                btn.setBorder(new LineBorder(new Color(180, 110, 20), 1));
                            } else {
                                btn.setBackground(new Color(254, 243, 199));
                                btn.setForeground(new Color(180, 83, 9));
                                btn.setBorder(new LineBorder(new Color(245, 158, 11), 1));
                            }
                        }
                    } else {
                        if (btn instanceof BotonAsientoCabina) {
                            ((BotonAsientoCabina) btn).setEstadoAsiento(BotonAsientoCabina.EstadoAsiento.LIBRE_TURISTA);
                        } else {
                            if (Theme.esModoOscuro()) {
                                btn.setBackground(new Color(16, 38, 30));
                                btn.setForeground(new Color(52, 211, 153));
                                btn.setBorder(new LineBorder(new Color(20, 80, 60), 1));
                            } else {
                                btn.setBackground(new Color(220, 252, 231));
                                btn.setForeground(new Color(4, 120, 87));
                                btn.setBorder(new LineBorder(new Color(16, 185, 129), 1));
                            }
                        }
                    }
                    btn.setText(codigoAsiento);
                    double precioAsiento = v.obtenerPrecioBase() * (esVip ? 1.30 : 1.0);
                    btn.setToolTipText("Asiento " + codigoAsiento + " (" + tipoUbic + ") | " +
                            (esVip ? "Clase VIP" : "Clase Turista") + " | Tarifa: S/. " + String.format(java.util.Locale.US, "%.2f", precioAsiento));
                }
            }
        }

        // Estado inicial o reset cuando no hay asiento seleccionado (-1)
        if (pasajeroFilaSel < 0 || pasajeroColSel < 0) {
            if (lblDetalleAsientoPasajero != null) {
                lblDetalleAsientoPasajero.setText("Asiento: Seleccione un asiento en el mapa");
            }
            if (lblDetalleClasePasajero != null) {
                lblDetalleClasePasajero.setText("Zona: -");
            }
            if (lblDetalleBasePasajero != null) {
                lblDetalleBasePasajero.setText("Tarifa Base del Vuelo: S/. " + String.format(java.util.Locale.US, "%.2f", v.obtenerPrecioBase()));
            }
            if (lblDetalleSuplementoPasajero != null) {
                lblDetalleSuplementoPasajero.setText("Suplemento de Clase: S/. 0.00");
            }
            if (lblDetalleTotalPasajero != null) {
                lblDetalleTotalPasajero.setText("TOTAL A PAGAR: S/. 0.00");
            }
            if (btnConfirmarAsientoPasajero != null) {
                btnConfirmarAsientoPasajero.setEnabled(false);
            }
            return;
        }

        if (btnConfirmarAsientoPasajero != null) {
            btnConfirmarAsientoPasajero.setEnabled(true);
        }

        // Recalcular cotización en vivo
        char letraSel = (char) ('A' + pasajeroColSel);
        String ubicSel = (pasajeroColSel == 0 || pasajeroColSel == 4) ? "Ventana" : (pasajeroColSel == 1 || pasajeroColSel == 3) ? "Pasillo" : "Centro";
        boolean esVipSel = (pasajeroFilaSel < 3);
        double base = v.obtenerPrecioBase();
        double suplemento = esVipSel ? (base * 0.30) : 0.0;
        double total = base + suplemento;

        if (lblDetalleAsientoPasajero != null) {
            lblDetalleAsientoPasajero.setText("Asiento: Fila " + (pasajeroFilaSel + 1) + " - " + (pasajeroFilaSel + 1) + letraSel + " [" + ubicSel + "]");
        }
        if (lblDetalleClasePasajero != null) {
            lblDetalleClasePasajero.setText("Zona: " + (esVipSel ? "Clase Ejecutiva VIP" : "Clase Turista Confort"));
        }
        if (lblDetalleBasePasajero != null) {
            lblDetalleBasePasajero.setText("Tarifa Base del Vuelo: S/. " + String.format(java.util.Locale.US, "%.2f", base));
        }
        if (lblDetalleSuplementoPasajero != null) {
            lblDetalleSuplementoPasajero.setText(esVipSel ? "Suplemento VIP (+30%): + S/. " + String.format(java.util.Locale.US, "%.2f", suplemento) : "Suplemento de Clase: S/. 0.00 (Estándar)");
        }
        if (lblDetalleTotalPasajero != null) {
            lblDetalleTotalPasajero.setText("TOTAL A PAGAR: S/. " + String.format(java.util.Locale.US, "%.2f", total));
        }
    }

    private void accionConfirmarAsientoPasajero() {
        if (pasajeroFilaConfirmada >= 0) {
            JOptionPane.showMessageDialog(this,
                    "Su reserva ya está confirmada. No se permiten modificaciones.",
                    "Reserva Bloqueada", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Vuelo v = controlador.obtenerVueloActivo();
        if (v == null) {
            JOptionPane.showMessageDialog(this, "No hay un vuelo seleccionado.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (pasajeroFilaSel < 0 || pasajeroColSel < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un asiento en el mapa antes de confirmar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dni = (txtDniPasajeroReserva != null) ? txtDniPasajeroReserva.getText().trim() : "";
        String nombre = (txtNombrePasajeroReserva != null) ? txtNombrePasajeroReserva.getText().trim() : "";

        if (nombre.isEmpty() || dni.isEmpty() || dni.length() < 8) {
            JOptionPane.showMessageDialog(this,
                    "Para confirmar su asiento y emitir su pase oficial, ingrese su DNI y Nombre o presione 'Carga Rapida (Demo)'.",
                    "Datos de Pasajero Requeridos", JOptionPane.WARNING_MESSAGE);
            if (txtDniPasajeroReserva != null && (dni.isEmpty() || dni.length() < 8)) {
                txtDniPasajeroReserva.requestFocus();
            } else if (txtNombrePasajeroReserva != null) {
                txtNombrePasajeroReserva.requestFocus();
            }
            return;
        }

        boolean esCambio = (pasajeroFilaConfirmada >= 0 && (pasajeroFilaConfirmada != pasajeroFilaSel || pasajeroColConfirmada != pasajeroColSel));
        int filaAnterior = pasajeroFilaConfirmada;
        int colAnterior = pasajeroColConfirmada;

        int[][] mat = v.obtenerMatrizAsientos();
        if (mat[pasajeroFilaSel][pasajeroColSel] == 1 && (!esCambio || pasajeroFilaSel != filaAnterior || pasajeroColSel != colAnterior)) {
            JOptionPane.showMessageDialog(this, "El asiento seleccionado ya está ocupado.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (esCambio) {
            controlador.liberarAsiento(filaAnterior, colAnterior);
        }

        boolean exito = controlador.ocuparAsiento(pasajeroFilaSel, pasajeroColSel, dni);
        if (exito) {
            pasajeroFilaConfirmada = pasajeroFilaSel;
            pasajeroColConfirmada = pasajeroColSel;

            // Destello suave de confirmación sin bloqueo de hilo (B8)
            if (btnConfirmarAsientoPasajero != null) {
                Color colorOriginal = btnConfirmarAsientoPasajero.getBackground();
                btnConfirmarAsientoPasajero.setBackground(new Color(16, 185, 129));
                Timer timerToast = new Timer(1200, evTimer -> {
                    if (btnConfirmarAsientoPasajero != null) {
                        btnConfirmarAsientoPasajero.setBackground(colorOriginal);
                    }
                });
                timerToast.setRepeats(false);
                timerToast.start();
            }
            // Sincronizar en tabla pasajeros de MySQL en segundo plano (cero cuello de botella)
            if (ConexionBD.estaConectado()) {
                final String fDni = dni;
                final String fNom = nombre;
                final String fClase = (pasajeroFilaSel < 3 ? "VIP" : "Turista");
                final String fCodVuelo = v.obtenerCodigo();
                final int fFila = pasajeroFilaSel;
                final int fCol = pasajeroColSel;
                Thread tSyncPasajero = new Thread(() -> {
                    try (java.sql.Connection con = ConexionBD.obtenerConexion()) {
                        if (con != null) {
                            try (java.sql.PreparedStatement ps = con.prepareStatement(
                                "INSERT INTO pasajeros (dni, nombre, clase, codigo_vuelo, asiento_fila, asiento_columna) VALUES (?, ?, ?, ?, ?, ?) " +
                                "ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), clase=VALUES(clase), codigo_vuelo=VALUES(codigo_vuelo), asiento_fila=VALUES(asiento_fila), asiento_columna=VALUES(asiento_columna)")) {
                                ps.setString(1, fDni);
                                ps.setString(2, fNom);
                                ps.setString(3, fClase);
                                ps.setString(4, fCodVuelo);
                                ps.setInt(5, fFila);
                                ps.setInt(6, fCol);
                                ps.executeUpdate();
                            }
                        }
                    } catch (Exception ignored) {}
                }, "CORPAC-AsyncPasajero");
                tSyncPasajero.setDaemon(true);
                tSyncPasajero.start();
            }

            char letra = (char) ('A' + pasajeroColSel);
            String asiento = (pasajeroFilaSel + 1) + "" + letra;
            stepperPasajero.setPasoCompletado(1, true);
            actualizarEstadoPestanasPasajero();
            actualizarCabinaPasajero();
            actualizarMatrizCabina();
            actualizarPaseAbordarPasajero();
            actualizarTablaVuelos();
            actualizarTablaVuelosPasajero();

            if (esCambio) {
                char letAnt = (char) ('A' + colAnterior);
                String asientoAnt = (filaAnterior + 1) + "" + letAnt;
                mostrarToast("Asiento cambiado de " + asientoAnt + " a " + asiento + " para " + nombre, Theme.getSuccess());
                JOptionPane.showMessageDialog(this,
                        "¡CAMBIO DE ASIENTO CONFIRMADO CON ÉXITO!\n\n" +
                        "- Pasajero: " + nombre + "\n" +
                        "- Documento: DNI " + dni + "\n" +
                        "- Asiento Anterior Liberado: " + asientoAnt + "\n" +
                        "- Nuevo Asiento Asignado: " + asiento + " (" + (pasajeroFilaSel < 3 ? "Clase VIP" : "Clase Turista") + ")\n\n" +
                        "Avanzando al registro de equipaje...",
                        "Cambio de Asiento Exitoso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                mostrarToast("¡Asiento " + asiento + " confirmado para " + nombre + "!", Theme.getSuccess());
                JOptionPane.showMessageDialog(this,
                        "¡RESERVA DE ASIENTO CONFIRMADA CON ÉXITO!\n\n" +
                        "- Pasajero: " + nombre + "\n" +
                        "- Documento: DNI " + dni + "\n" +
                        "- Vuelo: " + v.obtenerCodigo() + " (" + v.obtenerOrigen() + " -> " + v.obtenerDestino() + ")\n" +
                        "- Asiento Asignado: " + asiento + " (" + (pasajeroFilaSel < 3 ? "Clase VIP" : "Clase Turista") + ")\n\n" +
                        "Avanzando al registro de equipaje...",
                        "Reserva Exitosa", JOptionPane.INFORMATION_MESSAGE);
            }
            actualizarPanelEquipajePasajero();
            actualizarPaseAbordarPasajero();
            panelPestanas.setSelectedIndex(2);
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo reservar el asiento.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionAutoAsignarPasajero() {
        if (pasajeroFilaConfirmada >= 0) {
            JOptionPane.showMessageDialog(this,
                    "Su reserva ya está confirmada. No se puede reasignar el asiento.",
                    "Reserva Bloqueada", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Vuelo v = controlador.obtenerVueloActivo();
        if (v == null) return;
        int[][] mat = v.obtenerMatrizAsientos();

        String claseElegida = null;
        if (cbClaseCabinaPasajero != null && cbClaseCabinaPasajero.getSelectedItem() != null) {
            claseElegida = (String) cbClaseCabinaPasajero.getSelectedItem();
        } else if (cbFiltroClasePasajero != null && cbFiltroClasePasajero.getSelectedItem() != null) {
            String f = (String) cbFiltroClasePasajero.getSelectedItem();
            if (!"Todas las Clases".equalsIgnoreCase(f)) {
                claseElegida = f;
            }
        }
        if (claseElegida == null || claseElegida.isEmpty() || claseElegida.contains("Todas")) {
            claseElegida = (v.obtenerClase() != null) ? v.obtenerClase() : "Ejecutiva";
        }
        boolean esTurista = claseElegida.contains("Turista");
        int filaInicio = esTurista ? 3 : 0;
        int filaFin = esTurista ? 10 : 3;

        int mejorF = -1, mejorC = -1;
        // Prioridad 1: Ventana libre dentro del rango de clase
        for (int f = filaInicio; f < filaFin; f++) {
            if (mat[f][0] == 0) { mejorF = f; mejorC = 0; break; }
            if (mat[f][4] == 0) { mejorF = f; mejorC = 4; break; }
        }
        // Prioridad 2: Pasillo libre dentro del rango de clase
        if (mejorF == -1) {
            for (int f = filaInicio; f < filaFin; f++) {
                if (mat[f][1] == 0) { mejorF = f; mejorC = 1; break; }
                if (mat[f][3] == 0) { mejorF = f; mejorC = 3; break; }
            }
        }
        // Prioridad 3: Cualquier libre dentro del rango de clase
        if (mejorF == -1) {
            for (int f = filaInicio; f < filaFin; f++) {
                for (int c = 0; c < 5; c++) {
                    if (mat[f][c] == 0) { mejorF = f; mejorC = c; break; }
                }
                if (mejorF != -1) break;
            }
        }

        if (mejorF != -1) {
            pasajeroFilaSel = mejorF;
            pasajeroColSel = mejorC;
            actualizarCabinaPasajero();
            char letra = (char) ('A' + mejorC);
            JOptionPane.showMessageDialog(this,
                    "Se ha seleccionado automáticamente el mejor asiento disponible: " + (mejorF + 1) + letra +
                    "\nPulse 'Confirmar y Reservar Asiento' para fijar su reserva.",
                    "Asignación Inteligente", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Todos los asientos de este vuelo se encuentran ocupados.", "Vuelo Completo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void recalcularAlertaIata() {
        if (lblAlertaIataEquipaje == null || txtPesoEquipajePasajero == null) return;
        try {
            double peso = Double.parseDouble(txtPesoEquipajePasajero.getText().trim());
            if (peso <= 0) {
                lblAlertaIataEquipaje.setText("[!] Ingrese un peso mayor a 0 kg.");
                lblAlertaIataEquipaje.setForeground(Theme.getMuted());
            } else if (peso <= 10.0) {
                lblAlertaIataEquipaje.setText("[OK] Equipaje de cabina (hasta 10 kg) dentro del estándar IATA sin costo.");
                lblAlertaIataEquipaje.setForeground(Theme.getSuccess());
            } else if (peso <= 23.0) {
                lblAlertaIataEquipaje.setText("[OK] Equipaje facturado en bodega (hasta 23 kg) dentro del límite regular IATA.");
                lblAlertaIataEquipaje.setForeground(Theme.getInfo());
            } else if (peso <= 32.0) {
                lblAlertaIataEquipaje.setText("[ALERTA SOBREPESO] Excede 23 kg. Aplica recargo de sobrepeso comercial.");
                lblAlertaIataEquipaje.setForeground(Theme.getWarning());
            } else {
                lblAlertaIataEquipaje.setText("[PELIGRO EXCESO] Excede el peso máximo permitido (32 kg). Requiere carga especial.");
                lblAlertaIataEquipaje.setForeground(Theme.getDanger());
            }
        } catch (NumberFormatException ex) {
            lblAlertaIataEquipaje.setText("[!] Ingrese un valor numérico válido (ej. 8.5, 23.0)");
            lblAlertaIataEquipaje.setForeground(Theme.getMuted());
        }
    }

    private void actualizarPanelEquipajePasajero() {
        Vuelo v = controlador.obtenerVueloActivo();
        String codVuelo = (v != null) ? v.obtenerCodigo() + " (" + v.obtenerOrigen() + " -> " + v.obtenerDestino() + ")" : "CP-2024 (Lima -> Arequipa)";
        String nombre = (txtNombrePasajeroReserva != null && !txtNombrePasajeroReserva.getText().trim().isEmpty())
                ? txtNombrePasajeroReserva.getText().trim() : "No registrado";
        String dni = (txtDniPasajeroReserva != null && !txtDniPasajeroReserva.getText().trim().isEmpty())
                ? txtDniPasajeroReserva.getText().trim() : "No registrado";
        String email = (txtEmailPasajeroReserva != null && !txtEmailPasajeroReserva.getText().trim().isEmpty())
                ? txtEmailPasajeroReserva.getText().trim() : "";
        if (lblInfoPasajeroVueloEquipaje != null) {
            String txtEmail = email.isEmpty() ? "" : " | Correo: " + email;
            lblInfoPasajeroVueloEquipaje.setText("Pasajero: " + nombre + " | DNI: " + dni + txtEmail + " | Vuelo Activo: " + codVuelo);
        }
        actualizarTablaEquipajePasajero();
    }

    private void actualizarTablaEquipajePasajero() {
        if (modeloTablaEquipajePasajero == null) return;
        modeloTablaEquipajePasajero.setRowCount(0);
        if (listaEquipaje != null) {
            NodoSimple act = listaEquipaje.obtenerCabeza();
            while (act != null) {
                Equipaje eq = act.dato;
                if (eq != null) {
                    modeloTablaEquipajePasajero.addRow(new Object[]{
                            eq.obtenerCodigo(),
                            eq.obtenerDniPasajero(),
                            eq.obtenerCodigoVuelo(),
                            eq.obtenerPeso() + " kg",
                            eq.obtenerTipo(),
                            "Listo para Embarque"
                    });
                }
                act = act.siguiente;
            }
        }
    }

    private void accionRegistrarEquipajePasajero() {
        Vuelo v = controlador.obtenerVueloActivo();
        if (v == null) {
            JOptionPane.showMessageDialog(this, "Debe tener un vuelo activo para registrar equipaje.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dni = (txtDniPasajeroReserva != null) ? txtDniPasajeroReserva.getText().trim() : "";
        String nombre = (txtNombrePasajeroReserva != null) ? txtNombrePasajeroReserva.getText().trim() : "";
        if (nombre.isEmpty() || dni.length() != 8 || !dni.matches("\\d+")) {
            JOptionPane.showMessageDialog(this,
                    "Complete los datos del pasajero antes de registrar equipaje.",
                    "Datos del Pasajero Requeridos", JOptionPane.WARNING_MESSAGE);
            panelPestanas.setSelectedIndex(1);
            if (txtDniPasajeroReserva != null) txtDniPasajeroReserva.requestFocus();
            return;
        }

        double peso = 8.5;
        try {
            peso = Double.parseDouble(txtPesoEquipajePasajero.getText().trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un peso numérico válido en kg.", "Peso Inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tipoCombo = (String) cbTipoEquipajePasajero.getSelectedItem();
        String tipo = "Cabina";
        if (tipoCombo != null && tipoCombo.contains("Bodega")) tipo = "Bodega";
        if (tipoCombo != null && tipoCombo.contains("Especial")) tipo = "Sobredimensionado";

        String cod = "EQ-" + (100 + (int)(Math.random() * 900));
        Equipaje eq = new Equipaje(cod, dni, v.obtenerCodigo(), peso, tipo);
        controlador.agregarEquipaje(eq);
        actualizarContadorMaletas();
        actualizarTablaEquipajePasajero();
        actualizarMetricasDashboard();
        actualizarPaseAbordarPasajero();

        int opc = JOptionPane.showConfirmDialog(this,
                "Equipaje registrado exitosamente en el sistema:\n" +
                "- Código de Etiqueta: " + cod + "\n" +
                "- DNI Pasajero: " + dni + "\n" +
                "- Peso: " + peso + " kg\n" +
                "- Modalidad: " + tipo + "\n\n" +
                "¿Desea avanzar a la emisión de su Pase de Abordar?",
                "Equipaje Confirmado", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (opc == JOptionPane.YES_OPTION) {
            panelPestanas.setSelectedIndex(3);
        }
    }

    private void actualizarPaseAbordarPasajero() {
        if (lblPaseNombrePasajero == null) return;
        Vuelo vAct = controlador.obtenerVueloActivo();
        String codigoVuelo = (vAct != null) ? vAct.obtenerCodigo() : "CP-2024";
        String rutaVuelo = (vAct != null) ? (vAct.obtenerOrigen() + " (LIM) -> " + vAct.obtenerDestino().toUpperCase()) : "LIMA (LIM) -> AREQUIPA (AQP)";
        String horaVuelo = (vAct != null) ? (vAct.obtenerFecha() + " | Salida: " + vAct.obtenerHoraSalida()) : "2026-10-06 | Salida: 08:30";

        String dni = (txtDniPasajeroReserva != null && !txtDniPasajeroReserva.getText().trim().isEmpty())
                ? txtDniPasajeroReserva.getText().trim() : "";
        String nombre = (txtNombrePasajeroReserva != null && !txtNombrePasajeroReserva.getText().trim().isEmpty())
                ? txtNombrePasajeroReserva.getText().trim() : "";
        String email = (txtEmailPasajeroReserva != null && !txtEmailPasajeroReserva.getText().trim().isEmpty())
                ? txtEmailPasajeroReserva.getText().trim() : "";

        lblPaseCodigoVuelo.setText(codigoVuelo + " | Airbus A320neo | CORPAC Airways");
        lblPaseRutaVuelo.setText("Ruta: " + rutaVuelo);
        lblPaseHorario.setText("Horario: " + horaVuelo + " (Presentación: 40 min antes)");

        boolean prereqsValidos = (pasajeroFilaConfirmada >= 0 && dni.length() == 8 && !nombre.isEmpty());

        if (prereqsValidos) {
            char letraSel = (pasajeroColConfirmada >= 0 && pasajeroColConfirmada < 5) ? (char) ('A' + pasajeroColConfirmada) : (char) ('A' + Math.max(0, pasajeroColSel));
            int filaValida = (pasajeroFilaConfirmada >= 0) ? pasajeroFilaConfirmada : pasajeroFilaSel;
            String asientoCod = (filaValida + 1) + "" + letraSel;
            String claseNombre = (filaValida < 3) ? "Clase Ejecutiva VIP" : "Clase Turista Confort";
            int colValida = (pasajeroColConfirmada >= 0) ? pasajeroColConfirmada : Math.max(0, pasajeroColSel);
            String ubicNombre = (colValida == 0 || colValida == 4) ? "Ventana" : (colValida == 1 || colValida == 3) ? "Pasillo" : "Centro";

            double base = (vAct != null) ? vAct.obtenerPrecioBase() : 139.0;
            double suplemento = (filaValida < 3) ? (base * 0.30) : 0.0;
            double total = base + suplemento;

            String txtEmail = email.isEmpty() ? "" : " | " + email;
            lblPaseNombrePasajero.setText("Pasajero: " + nombre.toUpperCase() + " | DNI: " + dni + txtEmail);
            lblPaseAsientoDetalle.setText("Asiento: " + asientoCod + " [" + ubicNombre + "] | " + claseNombre + " | Tarifa: S/. " + String.format(java.util.Locale.US, "%.2f", total));
            lblPaseLocalizador.setText("LOCALIZADOR: LIM-" + (Math.abs((codigoVuelo + dni).hashCode()) % 90000 + 10000) + " [CONFIRMADO]");
            lblPaseLocalizador.setForeground(Theme.getSuccess());

            String puerta = "Gate 01 (Sala 1)";
            ListaCircular lc = controlador.obtenerListaPuertas();
            if (lc != null && !lc.estaVacia()) {
                PuertaEmbarque pAct = lc.obtenerPuertaActual();
                if (pAct != null) {
                    puerta = pAct.obtenerNumeroPuerta() + " (" + pAct.obtenerEstado() + ")";
                }
            }
            lblPasePuertaDetalle.setText("PUERTA DE EMBARQUE: " + puerta.toUpperCase());
        } else {
            lblPaseNombrePasajero.setText("Pasajero: - (SIN EMITIR) | DNI: -");
            lblPaseAsientoDetalle.setText("Asiento: - (SIN SELECCIONAR) | Clase: - | Tarifa: S/. 0.00");
            lblPaseLocalizador.setText("LOCALIZADOR: SIN EMITIR");
            lblPaseLocalizador.setForeground(Theme.getMuted());
            lblPasePuertaDetalle.setText("PUERTA DE EMBARQUE: - (SIN ASIGNAR)");
        }

        String detEquipaje = "Equipaje de mano (10 kg incluido)";
        if (listaEquipaje != null && !listaEquipaje.estaVacia()) {
            NodoSimple n = listaEquipaje.obtenerCabeza();
            while (n != null) {
                if (n.dato != null && (n.dato.obtenerDniPasajero().equals(dni) || n.dato.obtenerCodigoVuelo().equalsIgnoreCase(codigoVuelo))) {
                    detEquipaje = n.dato.obtenerCodigo() + " (" + n.dato.obtenerPeso() + " kg - " + n.dato.obtenerTipo() + ")";
                    break;
                }
                n = n.siguiente;
            }
        }
        if (lblPaseEquipajeDetalle != null) {
            lblPaseEquipajeDetalle.setText("Equipaje: " + detEquipaje);
        }

        if (txtEmailConfirmacionPase != null) {
            String act = txtEmailConfirmacionPase.getText().trim();
            if ((act.isEmpty() || act.contains("@corpac.pe") || act.contains("aeropuerto.gob.pe")) && !email.isEmpty()) {
                txtEmailConfirmacionPase.setText(email);
            }
        }
    }

    private boolean verificarPermisoAdmin(String accion) {
        if (controlador.esSesionAdmin()) {
            return true;
        }
        int resp = JOptionPane.showConfirmDialog(this,
                "La acción '" + accion + "' requiere privilegios de Administrador.\n" +
                "¿Desea iniciar sesión como Administrador ahora?",
                "Permiso Requerido", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (resp == JOptionPane.YES_OPTION) {
            abrirDialogoLoginAdmin();
            return controlador.esSesionAdmin();
        }
        return false;
    }

    private void accionVaciarTodo() {
        if (!verificarPermisoAdmin("Vaciar base de datos y memoria")) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de vaciar completamente todas las estructuras de datos?\n" +
                "Esto dejará el catálogo de vuelos, cabina, equipaje, rutas y puertas en 0 registros\n" +
                "tanto en memoria RAM como en las tablas operativas de MySQL.",
                "Confirmar Vacío Total", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            controlador.vaciarTodo(true);
            this.listaEquipaje = controlador.obtenerListaEquipaje();
            this.listaRutas = controlador.obtenerListaRutas();
            this.listaPuertas = controlador.obtenerListaPuertas();

            actualizarTablaVuelos();
            actualizarCombosVuelos();
            actualizarMatrizCabina();
            actualizarEtiquetaVueloActivo();
            actualizarContadorMaletas();
            actualizarContadorRutas();
            actualizarNavegadorRutas();
            actualizarVistaPuertas();
            actualizarMetricasDashboard();
            actualizarMetricasSQLTab();
            if (panelSupervisoresAeropuertos != null) {
                panelSupervisoresAeropuertos.actualizarTablas();
            }
            if (panelRadarSmartAirport != null) {
                panelRadarSmartAirport.refrescarDatos();
            }
            limpiarCamposVuelo();

            log("[SISTEMA] Vaciado total completado. Estructuras y MySQL limpias (0 registros).");
            JOptionPane.showMessageDialog(this, "Todas las estructuras y tablas han sido vaciadas con éxito.\nEl sistema se encuentra 100% limpio listo para ingresar datos.", "Reinicio Completo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void accionCargarDatosDemostracion() {
        if (!verificarPermisoAdmin("Cargar datos de demostración")) return;
        controlador.cargarDatosDemostracion();
        this.listaEquipaje = controlador.obtenerListaEquipaje();
        this.listaRutas = controlador.obtenerListaRutas();
        this.listaPuertas = controlador.obtenerListaPuertas();

        actualizarTablaVuelos();
        actualizarCombosVuelos();
        actualizarMatrizCabina();
        actualizarEtiquetaVueloActivo();
        actualizarContadorMaletas();
        actualizarContadorRutas();
        actualizarNavegadorRutas();
        actualizarVistaPuertas();
        actualizarMetricasDashboard();
        actualizarMetricasSQLTab();
        if (panelSupervisoresAeropuertos != null) {
            panelSupervisoresAeropuertos.actualizarTablas();
        }
        if (panelRadarSmartAirport != null) {
            panelRadarSmartAirport.refrescarDatos();
        }

        log("[SISTEMA] Datos de prueba cargados en Arreglos, Matriz, Lista Simple, Doble y Circular.");
        JOptionPane.showMessageDialog(this, "Datos de prueba cargados con éxito en memoria y MySQL.", "Datos de Demostración", JOptionPane.INFORMATION_MESSAGE);
    }

    private void actualizarMetricasSQLTab() {
        if (lblSqlEstadoServidor == null) return;
        boolean conectado = ConexionBD.estaConectado();
        if (conectado) {
            lblSqlEstadoServidor.setText("MySQL: CONECTADO | Servicio activo en localhost:3306 (aeropuerto_db)");
            lblSqlEstadoServidor.setForeground(verdeLibre);
            int[] c = controlador.obtenerConteoRegistrosBD();
            if (lblSqlVuelos != null) lblSqlVuelos.setText(c[0] + " vuelos en BD");
            if (lblSqlAsientos != null) lblSqlAsientos.setText(c[1] + " asientos ocupados");
            if (lblSqlEquipaje != null) lblSqlEquipaje.setText(c[2] + " maletas en BD");
            if (lblSqlRutas != null) lblSqlRutas.setText(c[3] + " rutas en BD");
            if (lblSqlPuertas != null) lblSqlPuertas.setText(c[4] + " puertas en BD");
            if (lblSqlUsuarios != null) lblSqlUsuarios.setText(c[5] + " usuarios registrados");
            if (lblSqlAeropuertos != null) lblSqlAeropuertos.setText(c[6] + " aeropuertos en BD");
            if (lblSqlSupervisores != null) lblSqlSupervisores.setText(c[7] + " supervisores en BD");
        } else {
            lblSqlEstadoServidor.setText("Modo Local RAM: MySQL no disponible en localhost:3306");
            lblSqlEstadoServidor.setForeground(new Color(220, 100, 0));
            if (lblSqlVuelos != null) lblSqlVuelos.setText(controlador.obtenerContadorVuelos() + " en memoria");
            if (lblSqlAsientos != null) {
                Vuelo v = controlador.obtenerVueloActivo();
                lblSqlAsientos.setText((v != null ? v.contarAsientosOcupados() : 0) + " en memoria");
            }
            if (lblSqlEquipaje != null) lblSqlEquipaje.setText(listaEquipaje.obtenerTamano() + " en memoria");
            if (lblSqlRutas != null) lblSqlRutas.setText(listaRutas.obtenerTamano() + " en memoria");
            if (lblSqlPuertas != null) lblSqlPuertas.setText(listaPuertas.obtenerTamano() + " en memoria");
            if (lblSqlAeropuertos != null) lblSqlAeropuertos.setText(controlador.obtenerContadorAeropuertos() + " en memoria");
            if (lblSqlSupervisores != null) lblSqlSupervisores.setText(controlador.obtenerContadorSupervisores() + " en memoria");
            if (lblSqlUsuarios != null) lblSqlUsuarios.setText("2 en memoria");
        }
    }

    private void actualizarPanelAdminTab() {
        if (lblAdminUsuarioActivo == null) return;
        if (controlador.esSesionAdmin()) {
            Usuario usuario = controlador.obtenerUsuarioActual();
            String nombreUsuario = (usuario != null) ? usuario.obtenerUsuario() : "admin";
            String nombreReal = (usuario != null) ? usuario.obtenerNombre() : "Administrador";
            lblAdminUsuarioActivo.setText("Usuario: " + nombreUsuario);
            lblAdminRolActivo.setText("Rol: ADMINISTRADOR - Acceso Total Concedido");
            lblAdminRolActivo.setForeground(verdeLibre);
            lblAdminNombreActivo.setText("Nombre: " + nombreReal);
        } else {
            lblAdminUsuarioActivo.setText("Usuario: Invitado / Pasajero");
            lblAdminRolActivo.setText("Rol: Modo Consulta - Sin privilegios de edición");
            lblAdminRolActivo.setForeground(new Color(110, 110, 110));
            lblAdminNombreActivo.setText("Nombre: Operador de Consulta");
        }
    }

    // HUMANIZACION UI - Panel inferior: Barra de estado con chips semanticos (MySQL / RAM / TADs)
    private JPanel crearPanelInferior() {
        if (txtConsola == null) {
            txtConsola = new JTextArea();
        }
        barraEstadoFooter = new BarraEstadoFooter(
                e -> accionSincronizarMySQL(),
                e -> accionCargarDesdeMySQL()
        );
        actualizarEstadoMySQL();
        barraEstadoFooter.setVisible(vistaTecnicaActiva || Theme.esRolAdmin());
        return barraEstadoFooter;
    }

    // Simulador Dinámico de Radar / Operaciones Aéreas
    private void iniciarSimuladorDinamico() {
        temporizadorSimulador = new Timer(3500, e -> {
            if (simuladorActivo && modeloTablaVuelos != null) {
                // Rotar aleatoriamente estados de vuelo para dar dinamismo en vivo
                int total = modeloTablaVuelos.getRowCount();
                if (total > 0 && modeloTablaVuelos.getColumnCount() >= 10) {
                    int filaAlAzar = (int) (Math.random() * total);
                    if (filaAlAzar >= 0 && filaAlAzar < modeloTablaVuelos.getRowCount()) {
                        int estadoIdx = (int) (Math.random() * estadosVueloRotativos.length);
                        modeloTablaVuelos.setValueAt(estadosVueloRotativos[estadoIdx], filaAlAzar, 9);
                        Object codObj = modeloTablaVuelos.getValueAt(filaAlAzar, 1);
                        String cod = codObj != null ? codObj.toString() : "N/A";
                        log("[RADAR] Vuelo " + cod + " actualizó estado a: " + estadosVueloRotativos[estadoIdx]);
                    }
                }
            }
        });
        temporizadorSimulador.start();
    }

    private void alternarSimuladorDinamico() {
        simuladorActivo = !simuladorActivo;
        if (simuladorActivo) {
            btnAlternarSimulador.setText("Pausar Simulacion");
            btnAlternarSimulador.setBackground(new Color(45, 20, 25));
            btnAlternarSimulador.setForeground(new Color(248, 113, 113));
            btnAlternarSimulador.setBorder(new CompoundBorder(new LineBorder(new Color(239, 68, 68), 1), new EmptyBorder(3, 8, 3, 8)));
            log("[SIMULADOR] Simulación dinámica de operaciones en pista y vuelo ACTIVADA.");
        } else {
            btnAlternarSimulador.setText("Iniciar Simulacion Radar");
            btnAlternarSimulador.setBackground(new Color(20, 36, 28));
            btnAlternarSimulador.setForeground(verdeLibre);
            btnAlternarSimulador.setBorder(new CompoundBorder(new LineBorder(verdeLibre, 1), new EmptyBorder(3, 8, 3, 8)));
            log("[SIMULADOR] Simulación de radar en pausa.");
        }
    }

    // Eventos de Vuelos
    private void accionAgregarVuelo() {
        try {
            String codigo = txtCodigo.getText().trim();
            String origen = (String) cbOrigen.getSelectedItem();
            String destino = (String) cbDestino.getSelectedItem();
            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
            String fecha = formatoFecha.format((Date) spinnerFecha.getValue());
            String salida = (String) cbHoraSalida.getSelectedItem();
            if (salida != null) salida = salida.trim();
            String llegada = txtHoraLlegada.getText().trim();
            int duracion = Integer.parseInt(txtDuracion.getText().trim());
            double precio = Double.parseDouble(txtPrecioBase.getText().trim().replace(',', '.'));
            String clase = (String) cbClase.getSelectedItem();

            if (codigo.isEmpty() || origen.equalsIgnoreCase(destino)) {
                JOptionPane.showMessageDialog(this, "El código es obligatorio y el destino debe ser distinto al origen.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Vuelo nuevoVuelo = new Vuelo(codigo, origen, destino, fecha, salida, llegada, duracion, precio, clase);
            String mensaje = controlador.agregarVuelo(nuevoVuelo);
            log(mensaje);

            if (controlador.obtenerCodigoVueloActivo() == null || controlador.obtenerCodigoVueloActivo().isEmpty()) {
                controlador.seleccionarVueloActivo(codigo);
            }

            actualizarTablaVuelos();
            actualizarCombosVuelos();
            actualizarMatrizCabina();
            actualizarEtiquetaVueloActivo();
            actualizarMetricasDashboard();
            actualizarMetricasSQLTab();

            if (mensaje.startsWith("Vuelo")) {
                JOptionPane.showMessageDialog(this, mensaje, "Éxito en Catálogo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, mensaje, "Atención", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException error) {
            JOptionPane.showMessageDialog(this, "Duración y precio deben ser valores numéricos válidos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionActualizarVuelo() {
        if (!verificarPermisoAdmin("Actualizar Datos del Vuelo")) return;
        int filaSel = tablaVuelos.getSelectedRow();
        if (filaSel < 0 || filaSel >= controlador.obtenerContadorVuelos()) {
            JOptionPane.showMessageDialog(this, "Seleccione un vuelo de la tabla para actualizar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String codigo = txtCodigo.getText().trim();
            String origen = (String) cbOrigen.getSelectedItem();
            String destino = (String) cbDestino.getSelectedItem();
            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
            String fecha = formatoFecha.format((Date) spinnerFecha.getValue());
            String salida = (String) cbHoraSalida.getSelectedItem();
            if (salida != null) salida = salida.trim();
            String llegada = txtHoraLlegada.getText().trim();
            int duracion = Integer.parseInt(txtDuracion.getText().trim());
            double precio = Double.parseDouble(txtPrecioBase.getText().trim().replace(',', '.'));
            String clase = (String) cbClase.getSelectedItem();

            Vuelo vAct = new Vuelo(codigo, origen, destino, fecha, salida, llegada, duracion, precio, clase);
            boolean exito = controlador.actualizarVuelo(filaSel, vAct);
            if (exito) {
                log("Vuelo en índice [" + filaSel + "] actualizado en Arreglo y MySQL.");
                actualizarTablaVuelos();
                actualizarEtiquetaVueloActivo();
                actualizarMetricasDashboard();
                actualizarMetricasSQLTab();
                JOptionPane.showMessageDialog(this, "Vuelo actualizado correctamente.", "Actualización", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception error) {
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionEliminarVuelo() {
        if (!verificarPermisoAdmin("Eliminar Vuelo del Catálogo")) return;
        int filaSel = tablaVuelos.getSelectedRow();
        if (filaSel < 0 || filaSel >= controlador.obtenerContadorVuelos()) {
            JOptionPane.showMessageDialog(this, "Seleccione un vuelo del arreglo para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Vuelo vuelo = controlador.obtenerVuelos()[filaSel];
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Desea eliminar el vuelo " + vuelo.obtenerCodigo() + " en índice [" + filaSel + "]?\n" +
                "Se aplicará corrimiento hacia la izquierda para mantener la contigüidad física.",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean exito = controlador.eliminarVuelo(filaSel);
            if (exito) {
                log("Vuelo " + vuelo.obtenerCodigo() + " eliminado del Arreglo con corrimiento a la izquierda.");
                if (controlador.obtenerContadorVuelos() > 0) {
                    controlador.seleccionarVueloActivo(controlador.obtenerVuelos()[0].obtenerCodigo());
                } else {
                    controlador.seleccionarVueloActivo("");
                }
                actualizarTablaVuelos();
                actualizarCombosVuelos();
                actualizarMatrizCabina();
                actualizarEtiquetaVueloActivo();
                actualizarMetricasDashboard();
                actualizarMetricasSQLTab();
                limpiarCamposVuelo();
                JOptionPane.showMessageDialog(this, "Vuelo eliminado con éxito mediante corrimiento físico.", "Eliminación", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void accionBuscarVuelo() {
        String codigoBuscado = JOptionPane.showInputDialog(this, "Ingrese el código de vuelo a buscar en el arreglo:");
        if (codigoBuscado != null && !codigoBuscado.trim().isEmpty()) {
            Vuelo encontrado = controlador.buscarVueloPorCodigo(codigoBuscado.trim());
            if (encontrado != null) {
                cargarVueloEnFormulario(encontrado);
                controlador.seleccionarVueloActivo(encontrado.obtenerCodigo());
                actualizarMatrizCabina();
                actualizarEtiquetaVueloActivo();
                actualizarMetricasDashboard();
                log("Búsqueda Lineal O(n) exitosa: Vuelo " + encontrado.obtenerCodigo() + " localizado.");
                JOptionPane.showMessageDialog(this, "Vuelo Encontrado:\n" +
                        "Código: " + encontrado.obtenerCodigo() + "\n" +
                        "Ruta: " + encontrado.obtenerOrigen() + " -> " + encontrado.obtenerDestino() + "\n" +
                        "Tarifa: S/." + encontrado.obtenerPrecioBase() + "\n" +
                        "Asientos Libres: " + encontrado.contarAsientosLibres() + "/50", "Búsqueda Secuencial", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró ningún vuelo con el código " + codigoBuscado, "No Encontrado", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void accionBuscarVueloBinario() {
        String codigoBuscado = JOptionPane.showInputDialog(this, "Ingrese el código de vuelo para Búsqueda Binaria:");
        if (codigoBuscado != null && !codigoBuscado.trim().isEmpty()) {
            Vuelo encontrado = controlador.buscarVueloBinario(codigoBuscado.trim());
            if (encontrado != null) {
                cargarVueloEnFormulario(encontrado);
                controlador.seleccionarVueloActivo(encontrado.obtenerCodigo());
                actualizarMatrizCabina();
                actualizarCabinaPasajero();
                actualizarEtiquetaVueloActivo();
                actualizarMetricasDashboard();
                log("[BÚSQUEDA BINARIA] Vuelo " + encontrado.obtenerCodigo() + " localizado.");
                JOptionPane.showMessageDialog(this,
                        "===============================================================\n" +
                        "   BUSQUEDA BINARIA EXITOSA                                  \n" +
                        "===============================================================\n\n" +
                        "- Codigo Localizado: " + encontrado.obtenerCodigo() + "\n" +
                        "- Ruta: " + encontrado.obtenerOrigen() + " -> " + encontrado.obtenerDestino() + "\n" +
                        "- Tarifa Base: S/. " + encontrado.obtenerPrecioBase() + "\n" +
                        "- Asientos Libres: " + encontrado.contarAsientosLibres() + " / 50\n",
                        "Busqueda Binaria", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró ningún vuelo con el código: " + codigoBuscado, "No Encontrado", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void accionOrdenarVuelosPorPrecio() {
        controlador.ordenarVuelosPorPrecio(true);
        actualizarTablaVuelos();
        log("Catálogo de vuelos ordenado por precio ascendente.");
        JOptionPane.showMessageDialog(this, "Vuelos ordenados por precio de menor a mayor.", "Ordenamiento", JOptionPane.INFORMATION_MESSAGE);
    }

    private void accionBuscarVueloAVL() {
        String codigoBuscado = JOptionPane.showInputDialog(this, "Ingrese el código de vuelo para Búsqueda Indexada en Árbol AVL:");
        if (codigoBuscado != null && !codigoBuscado.trim().isEmpty()) {
            controlador.sincronizarIndiceArbol();
            Vuelo encontrado = controlador.buscarVueloPorCodigo(codigoBuscado.trim().toUpperCase());
            if (encontrado != null) {
                cargarVueloEnFormulario(encontrado);
                controlador.seleccionarVueloActivo(encontrado.obtenerCodigo());
                actualizarMatrizCabina();
                actualizarCabinaPasajero();
                actualizarEtiquetaVueloActivo();
                actualizarMetricasDashboard();

                if (modeloTablaVuelos != null && tablaVuelos != null) {
                    for (int r = 0; r < modeloTablaVuelos.getRowCount(); r++) {
                        if (encontrado.obtenerCodigo().equalsIgnoreCase((String) modeloTablaVuelos.getValueAt(r, 1))) {
                            tablaVuelos.setRowSelectionInterval(r, r);
                            tablaVuelos.scrollRectToVisible(tablaVuelos.getCellRect(r, 0, true));
                            break;
                        }
                    }
                }

                mostrarToast("Vuelo " + encontrado.obtenerCodigo() + " localizado en Árbol AVL O(log n)", Theme.getSuccess());
                JOptionPane.showMessageDialog(this,
                        "BÚSQUEDA INDEXADA EN ÁRBOL AVL EXITOSA:\n\n" +
                        "- Código: " + encontrado.obtenerCodigo() + "\n" +
                        "- Ruta: " + encontrado.obtenerOrigen() + " -> " + encontrado.obtenerDestino() + "\n" +
                        "- Tarifa: S/. " + String.format(java.util.Locale.US, "%.2f", encontrado.obtenerPrecioBase()) + "\n" +
                        "- Complejidad: O(log n) garantizado por auto-balanceo de factores de equilibrio",
                        "Árbol AVL - Vuelo Localizado", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró ningún vuelo en el árbol con el código: " + codigoBuscado, "No Encontrado", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void accionOrdenarVuelosQuicksort() {
        controlador.ordenarVuelosQuicksort(true);
        actualizarTablaVuelos();
        mostrarToast("Catálogo ordenado por Tarifa mediante Quicksort O(n log n)", Theme.getSuccess());
        JOptionPane.showMessageDialog(this,
                "Catálogo de vuelos ordenado exitosamente por Tarifa (menor a mayor)\n" +
                "utilizando el Algoritmo Quicksort O(n log n).",
                "Quicksort Completado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void accionOrdenarVuelosPorCodigo() {
        controlador.ordenarVuelosPorCodigo();
        actualizarTablaVuelos();
        log("Catálogo de vuelos ordenado alfabéticamente por código.");
        JOptionPane.showMessageDialog(this, "Vuelos ordenados por código.", "Ordenamiento", JOptionPane.INFORMATION_MESSAGE);
    }

    private void accionMostrarEstadisticasCatalogo() {
        String stats = controlador.obtenerEstadisticasCatalogo();
        log("Consulta de estadísticas generales del catálogo de vuelos.");
        JOptionPane.showMessageDialog(this, stats, "Estadísticas del Catálogo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void accionRecorrerArregloVuelos() {
        String reporte = controlador.recorrerArregloVuelos();
        log("[ARREGLO VUELOS] Recorrido secuencial del arreglo Vuelo[] ejecutado exitosamente.");
        mostrarDialogoTextoLargo("Recorrido Secuencial del Arreglo Vuelo[]", reporte, 680, 420);
    }

    private void accionCopiarArregloVuelos() {
        String reporte = controlador.copiarArregloVuelosConReporte();
        log("[ARREGLO VUELOS] Copia física e independiente del arreglo Vuelo[] realizada con éxito.");
        mostrarDialogoTextoLargo("Copia Física Independiente del Arreglo Vuelo[]", reporte, 680, 420);
    }

    // Eventos de Cabina y Pase de Abordar
    private void seleccionarAsientoEnMatriz(int fila, int columna) {
        if (controlador.obtenerContadorVuelos() == 0 || controlador.obtenerVueloActivo() == null) {
            JOptionPane.showMessageDialog(this, "Debe registrar un vuelo antes de gestionar asientos.", "Sin Vuelo Activo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        txtFilaSeleccionada.setText(String.valueOf(fila));
        txtColSeleccionada.setText(String.valueOf(columna));

        String tipoZona = (columna == 0 || columna == 4) ? "Ventana (+30%)" :
                          (columna == 1 || columna == 3) ? "Pasillo (+10%)" : "Centro (Base)";
        lblZonaAsiento.setText("Zona: " + tipoZona);

        Vuelo vActivo = controlador.obtenerVueloActivo();
        double precioBase = (vActivo != null) ? vActivo.obtenerPrecioBase() : 139.0;
        double costo = controlador.calcularCostoAsiento(fila, columna, precioBase);
        lblCostoCalculado.setText(String.format(java.util.Locale.US, "Tarifa Asiento: S/. %.2f", costo));

        String pasajero = controlador.obtenerMatrizPasajeros()[fila][columna];
        if (pasajero != null) {
            txtDniAsiento.setText(pasajero);
        }

        // Actualizar pase de abordar en vivo
        char letra = (char) ('A' + columna);
        String codAsiento = (fila + 1) + "" + letra;
        lblPasePasajero.setText("Pasajero: " + (pasajero != null ? "Registrado (DNI: " + pasajero + ")" : "Por asignar (DNI: " + txtDniAsiento.getText() + ")"));
        if (vActivo != null) {
            lblPaseVueloRuta.setText("Vuelo: " + vActivo.obtenerCodigo() + " | Ruta: " + vActivo.obtenerOrigen() + " -> " + vActivo.obtenerDestino() + " (" + vActivo.obtenerClaseDisponible() + ")");
        }
        lblPaseAsientoZona.setText(String.format(java.util.Locale.US, "Asiento: %s [%s] | Tarifa: S/. %.2f | Estado: %s",
                codAsiento, tipoZona, costo, pasajero != null ? "OCUPADO" : "DISPONIBLE"));
        actualizarPaseAbordarPasajero();
    }

    private void accionOcuparAsiento() {
        if (controlador.obtenerContadorVuelos() == 0 || controlador.obtenerVueloActivo() == null) {
            JOptionPane.showMessageDialog(this, "Debe registrar un vuelo antes de asignar asientos.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int fila = Integer.parseInt(txtFilaSeleccionada.getText().trim());
            int columna = Integer.parseInt(txtColSeleccionada.getText().trim());
            String dni = txtDniAsiento.getText().trim();

            if (fila < 0 || fila >= 10 || columna < 0 || columna >= 5) {
                JOptionPane.showMessageDialog(this, "Coordenadas fuera de rango. Fila: 0-9, Columna: 0-4.", "Límites de Matriz", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean exito = controlador.ocuparAsiento(fila, columna, dni);
            if (exito) {
                log("[CABINA] Vuelo " + controlador.obtenerCodigoVueloActivo() + ": Asiento [" + fila + "," + columna + "] asignado a DNI " + dni);
                actualizarMatrizCabina();
                actualizarTablaVuelos();
                actualizarMetricasDashboard();
                actualizarMetricasSQLTab();
                seleccionarAsientoEnMatriz(fila, columna);
                JOptionPane.showMessageDialog(this, "Asiento asignado con éxito en el vuelo " + controlador.obtenerCodigoVueloActivo(), "Cabina de Pasajeros", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "El asiento ya se encuentra ocupado.", "Asiento Ocupado", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException error) {
            JOptionPane.showMessageDialog(this, "Fila y columna deben ser valores numéricos enteros.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionLiberarAsiento() {
        if (!verificarPermisoAdmin("Liberar y anular asiento de pasajero")) return;
        if (controlador.obtenerContadorVuelos() == 0 || controlador.obtenerVueloActivo() == null) {
            JOptionPane.showMessageDialog(this, "Debe registrar un vuelo antes de liberar asientos.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int fila = Integer.parseInt(txtFilaSeleccionada.getText().trim());
            int columna = Integer.parseInt(txtColSeleccionada.getText().trim());

            boolean exito = controlador.liberarAsiento(fila, columna);
            if (exito) {
                log("[CABINA] Vuelo " + controlador.obtenerCodigoVueloActivo() + ": Asiento [" + fila + "," + columna + "] liberado.");
                actualizarMatrizCabina();
                actualizarTablaVuelos();
                actualizarMetricasDashboard();
                actualizarMetricasSQLTab();
                seleccionarAsientoEnMatriz(fila, columna);
                JOptionPane.showMessageDialog(this, "Asiento liberado y disponible en el vuelo " + controlador.obtenerCodigoVueloActivo(), "Cabina de Pasajeros", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "El asiento ya estaba libre.", "Atención", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException error) {
            JOptionPane.showMessageDialog(this, "Fila y columna deben ser valores numéricos enteros.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionAutoAsignarAsiento() {
        if (controlador.obtenerContadorVuelos() == 0 || controlador.obtenerVueloActivo() == null) {
            JOptionPane.showMessageDialog(this, "Debe registrar un vuelo antes de auto-asignar asientos.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String dni = txtDniAsiento.getText().trim();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el DNI del pasajero para auto-asignar asiento.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int[][] matriz = controlador.obtenerMatrizAsientos();
        int filaElegida = -1;
        int colElegida = -1;

        // Prioridad 1: Ventana (Col 0 y 4)
        for (int f = 0; f < 10 && filaElegida == -1; f++) {
            if (matriz[f][0] == 0) { filaElegida = f; colElegida = 0; break; }
            if (matriz[f][4] == 0) { filaElegida = f; colElegida = 4; break; }
        }
        // Prioridad 2: Pasillo (Col 1 y 3)
        if (filaElegida == -1) {
            for (int f = 0; f < 10 && filaElegida == -1; f++) {
                if (matriz[f][1] == 0) { filaElegida = f; colElegida = 1; break; }
                if (matriz[f][3] == 0) { filaElegida = f; colElegida = 3; break; }
            }
        }
        // Prioridad 3: Centro (Col 2)
        if (filaElegida == -1) {
            for (int f = 0; f < 10; f++) {
                if (matriz[f][2] == 0) { filaElegida = f; colElegida = 2; break; }
            }
        }

        if (filaElegida != -1) {
            seleccionarAsientoEnMatriz(filaElegida, colElegida);
            boolean exito = controlador.ocuparAsiento(filaElegida, colElegida, dni);
            if (exito) {
                log("[CABINA] Asignación inteligente: Asiento [" + filaElegida + "," + colElegida + "] asignado a " + dni);
                actualizarMatrizCabina();
                actualizarTablaVuelos();
                actualizarMetricasDashboard();
                actualizarMetricasSQLTab();
                char letra = (char) ('A' + colElegida);
                JOptionPane.showMessageDialog(this, "Asiento " + (filaElegida + 1) + letra + " asignado automáticamente.", "Auto-Asignación", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "La cabina de este vuelo está completamente ocupada (50/50).", "Capacidad Completa", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Simulacion de llenado rapido de 5 asientos para demostracion en vivo
    private void accionSimularLlenadoRapido() {
        if (controlador.obtenerContadorVuelos() == 0 || controlador.obtenerVueloActivo() == null) {
            JOptionPane.showMessageDialog(this, "Debe registrar un vuelo antes de simular llenado.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int asignados = 0;
        int[][] mat = controlador.obtenerMatrizAsientos();
        for (int f = 0; f < 10 && asignados < 5; f++) {
            for (int c = 0; c < 5 && asignados < 5; c++) {
                if (mat[f][c] == 0) {
                    String dniSim = "4" + (1000000 + (int)(Math.random() * 8999999));
                    controlador.ocuparAsiento(f, c, dniSim);
                    asignados++;
                }
            }
        }
        actualizarMatrizCabina();
        actualizarTablaVuelos();
        actualizarMetricasDashboard();
        actualizarMetricasSQLTab();
        log("[CABINA] Simulación rápida: " + asignados + " asientos asignados automáticamente en " + controlador.obtenerCodigoVueloActivo());
        JOptionPane.showMessageDialog(this, "Se asignaron " + asignados + " asientos automáticamente para demostración.", "Simulación Rápida", JOptionPane.INFORMATION_MESSAGE);
    }

    private void accionDeshacerPila() {
        if (!verificarPermisoAdmin("Deshacer última reserva mediante Pila LIFO")) return;
        com.aeropuerto.modelo.AccionReserva accion = controlador.deshacerUltimaReserva();
        if (accion != null) {
            actualizarMatrizCabina();
            actualizarCabinaPasajero();
            actualizarTablaVuelos();
            actualizarMetricasDashboard();
            actualizarMetricasSQLTab();
            log("[PILA LIFO] Deshecha última reserva: " + accion.toString());
            JOptionPane.showMessageDialog(this,
                    "===============================================================\n" +
                    "   OPERACION DESHACER RESERVA COMPLETADA                     \n" +
                    "===============================================================\n\n" +
                    "- Vuelo afectado: " + accion.obtenerCodigoVuelo() + "\n" +
                    "- Asiento liberado: " + accion.obtenerEtiquetaAsiento() + " [Fila " + accion.obtenerFila() + ", Col " + accion.obtenerColumna() + "]\n" +
                    "- DNI del Pasajero: " + accion.obtenerDniPasajero() + "\n" +
                    "- Registro original: " + accion.obtenerHoraAccion() + "\n\n" +
                    "El asiento ha retornado al estado LIBRE tanto en memoria RAM como en MySQL.",
                    "Deshacer Reserva", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "El historial de reservas se encuentra vacío.\nNo hay operaciones pendientes de revertir.",
                    "Historial Vacío", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void accionRecorrerMatrizCabina() {
        String reporte = controlador.recorrerMatrizCabina();
        log("[MATRIZ 10x5] Recorrido bidimensional de cabina Airbus A320neo ejecutado exitosamente.");
        mostrarDialogoTextoLargo("Recorrido Bidimensional de Cabina (10 Filas x 5 Columnas)", reporte, 720, 460);
    }

    private void accionVerPilaLIFO() {
        String listado = controlador.obtenerHistorialPilaDeshacer();
        log("[PILA LIFO] Inspección de acciones reversibles apiladas en el tope.");
        mostrarDialogoTextoLargo("Inspección del TAD Pila LIFO (Historial de Reservas)", listado, 640, 400);
    }

    private void mostrarDialogoTextoLargo(String titulo, String contenido, int ancho, int alto) {
        JDialog dlg = new JDialog(this, titulo, true);
        dlg.setLayout(new BorderLayout(8, 8));
        dlg.getContentPane().setBackground(fondoGris);

        JTextArea ta = new JTextArea(contenido);
        ta.setEditable(false);
        ta.setFont(new Font("Consolas", Font.PLAIN, 12));
        ta.setBackground(fondoEntrada);
        ta.setForeground(textoBlanco);
        ta.setCaretPosition(0);
        ta.setBorder(new EmptyBorder(10, 12, 10, 12));

        JScrollPane sp = new JScrollPane(ta);
        sp.setBorder(new LineBorder(bordeTarjeta, 1));
        sp.setBackground(fondoEntrada);

        JPanel pSur = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        pSur.setOpaque(false);
        JButton btnCerrar = new JButton("Cerrar");
        estilizarBotonAccion(btnCerrar, azulAccion, new Color(15, 20, 29));
        btnCerrar.addActionListener(e -> dlg.dispose());
        pSur.add(btnCerrar);

        dlg.add(sp, BorderLayout.CENTER);
        dlg.add(pSur, BorderLayout.SOUTH);
        dlg.setSize(ancho, alto);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private void accionEnviarDniAEquipaje() {
        String dni = txtDniAsiento.getText().trim();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un asiento con pasajero para transferir el DNI.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        txtListaEquipajeDni.setText(dni);
        if (cbListaEquipajeVuelo != null && controlador.obtenerCodigoVueloActivo() != null) {
            cbListaEquipajeVuelo.setSelectedItem(controlador.obtenerCodigoVueloActivo());
        }
        if (panelPestanas != null) {
            panelPestanas.setSelectedIndex(1);
        }
        log("[CABINA] DNI " + dni + " transferido al módulo de Gestión de Equipaje.");
        JOptionPane.showMessageDialog(this, "DNI " + dni + " cargado en el formulario de equipaje.", "Transferencia Exitosa", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarFichaAvion() {
        try {
            DialogoFichaAvion dialogo = new DialogoFichaAvion(this, controlador);
            dialogo.setVisible(true);
        } catch (Exception error) {
            Avion avion = controlador.obtenerAvion();
            JOptionPane.showMessageDialog(this, avion.obtenerFichaTecnica(), "Ficha Técnica Airbus A320neo", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    public void accionOptimizarRedTroncal() {
        try {
            ResultadoMST mst = controlador.calcularMSTKruskal();
            if (mst == null) {
                mostrarToast("No hay rutas registradas en el grafo para optimizar.", Theme.getWarning());
                return;
            }
            JDialog diag = new JDialog(this, "Optimización de Red Troncal Nacional - Algoritmo Kruskal (MST)", true);
            diag.setSize(800, 520);
            diag.setLocationRelativeTo(this);

            JPanel p = new JPanel(new BorderLayout(12, 12));
            p.setBackground(fondoGris);
            p.setBorder(new EmptyBorder(16, 16, 16, 16));

            JLabel lblTitulo = new JLabel("RED TRONCAL ÓPTIMA AERONÁUTICA (Árbol de Expansión Mínima)");
            lblTitulo.setFont(FontManager.getBarlowCondensed(Font.BOLD, 18f));
            lblTitulo.setForeground(doradoAlerta);

            JTextArea area = new JTextArea();
            area.setFont(new Font("Consolas", Font.PLAIN, 12));
            area.setBackground(fondoEntrada);
            area.setForeground(new Color(226, 232, 240));
            area.setEditable(false);
            area.setMargin(new Insets(10, 10, 10, 10));

            StringBuilder sb = new StringBuilder();
            sb.append("========================================================================================\n");
            sb.append("  CORPAC S.A. - CÁLCULO DE INTERCONEXIÓN EFICIENTE DE RUTAS NACIONALES\n");
            sb.append("  Algoritmo: Kruskal con Conjuntos Disjuntos (Disjoint-Set / Union-Find)\n");
            sb.append("  Complejidad Temporal: O(E log E) | Espacial: O(V + E)\n");
            sb.append("========================================================================================\n\n");
            sb.append(String.format("  Tramos Troncales Seleccionados: %d aristas mínimas\n", mst.obtenerCantidadAristas()));
            sb.append(String.format("  Distancia Total Interconectada: %.1f km\n", mst.obtenerDistanciaTotalKm()));
            sb.append(String.format("  Costo Base Total de Operación: S/. %.2f\n\n", mst.obtenerCostoTotal()));
            sb.append("DETALLE DE INTERCONEXIONES TRONCALES DIRECTAS:\n");
            sb.append(String.format("  %-5s  %-18s  %-18s  %12s  %12s\n", "N°", "ORIGEN", "DESTINO", "DISTANCIA", "DURACIÓN"));
            sb.append("  ------------------------------------------------------------------------------------\n");

            if (mst.obtenerAristas() != null) {
                int n = 1;
                for (Arista a : mst.obtenerAristas()) {
                    if (a != null && a.obtenerOrigen() != null && a.obtenerDestino() != null) {
                        String orig = a.obtenerOrigen().obtenerCodigoIATA() + " (" + a.obtenerOrigen().obtenerCiudad() + ")";
                        String dest = a.obtenerDestino().obtenerCodigoIATA() + " (" + a.obtenerDestino().obtenerCiudad() + ")";
                        sb.append(String.format("  [%02d]  %-18s -> %-18s  %9.1f km  %8d min\n",
                                n++, orig, dest, a.obtenerDistanciaKm(), a.obtenerDuracionMinutos()));
                    }
                }
            }
            sb.append("  ------------------------------------------------------------------------------------\n\n");
            sb.append("IMPACTO Y VALOR OPERATIVO:\n");
            sb.append("- Garantiza la cobertura de todas las ciudades con la menor distancia de vuelo acumulada.\n");
            sb.append("- Elimina ciclos y redundancias en el corredor aéreo nacional de CORPAC.\n");
            sb.append("- Optimiza el despacho de combustible y reduce costos logísticos aeroportuarios.\n");

            area.setText(sb.toString());
            area.setCaretPosition(0);

            JScrollPane scroll = new JScrollPane(area);
            scroll.setBorder(BorderFactory.createLineBorder(Theme.getBorder()));

            JPanel pBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            pBotones.setOpaque(false);
            JButton btnCerrar = new JButton("Cerrar");
            estilizarBotonAccion(btnCerrar, azulAccion, new Color(15, 20, 29));
            btnCerrar.addActionListener(ev -> diag.dispose());
            pBotones.add(btnCerrar);

            p.add(lblTitulo, BorderLayout.NORTH);
            p.add(scroll, BorderLayout.CENTER);
            p.add(pBotones, BorderLayout.SOUTH);

            diag.setContentPane(p);
            diag.setVisible(true);
        } catch (Exception ex) {
            mostrarToast("Error calculando red troncal: " + ex.getMessage(), Theme.getDanger());
        }
    }

    public void mostrarDialogoArbolesYGrafos() {
        if (panelPestanas != null && obtenerPanelArboles() != null) {
            panelPestanas.setSelectedComponent(obtenerPanelArboles());
        }
    }

    public void mostrarDialogoArbolesYGrafos(boolean soloLectura) {
        mostrarDialogoArbolesYGrafos();
    }

    public void mostrarDialogoVistaTecnicaLaboratorio(boolean soloLectura) {
        mostrarDialogoArbolesYGrafos();
    }

    public void mostrarDialogoVisor3DAeronave() {
        JDialog dialogo = new JDialog(this, "Telemetría de Aeronave - Vista 3D Airbus A320neo (CORPAC S.A.)", false);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialogo.setSize(840, 620);
        dialogo.setLocationRelativeTo(this);
        dialogo.setResizable(true);

        JPanel panelContenedor = new JPanel(new BorderLayout(0, 8));
        panelContenedor.setBackground(Theme.getBg());
        panelContenedor.setBorder(new EmptyBorder(10, 14, 10, 14));

        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setOpaque(false);

        JLabel lblTitulo = new JLabel("Telemetría Física y Visualizador 3D Vectorial (Airbus A320neo)");
        lblTitulo.setFont(FontManager.getBarlowCondensed(Font.BOLD, 16f));
        lblTitulo.setForeground(Theme.getTexto());
        lblTitulo.setIcon(AppIcon.of("plane", 14, Theme.getPasajeroAccent()));

        JLabel lblSub = new JLabel("Herramienta complementaria de inspección externa · Arrastre para orbitar / Rueda para zoom");
        lblSub.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
        lblSub.setForeground(Theme.getSecundario());

        JPanel pTit = new JPanel(new GridLayout(2, 1, 0, 2));
        pTit.setOpaque(false);
        pTit.add(lblTitulo);
        pTit.add(lblSub);
        panelInfo.add(pTit, BorderLayout.WEST);

        panelContenedor.add(panelInfo, BorderLayout.NORTH);

        PanelVisorAvion3D visor3D = new PanelVisorAvion3D();
        panelContenedor.add(visor3D, BorderLayout.CENTER);

        JPanel panelBarra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        panelBarra.setOpaque(false);

        JButton btnReset = new JButton("Reiniciar Cámara");
        btnReset.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
        btnReset.setBackground(Theme.getElevated());
        btnReset.setForeground(Theme.getTexto());
        btnReset.addActionListener(e -> visor3D.reiniciarCamara());

        JButton btnWireframe = new JButton("Modo Alambre / Sólido");
        btnWireframe.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
        btnWireframe.setBackground(Theme.getElevated());
        btnWireframe.setForeground(Theme.getTexto());
        btnWireframe.addActionListener(e -> visor3D.alternarModoAlambre());

        JButton btnAnim = new JButton("Pausar / Reanudar Giro");
        btnAnim.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
        btnAnim.setBackground(Theme.getElevated());
        btnAnim.setForeground(Theme.getTexto());
        btnAnim.addActionListener(e -> visor3D.alternarAnimacion());

        JButton btnCerrar = new JButton("Volver a Matriz 2D");
        btnCerrar.setFont(FontManager.getIBMPlexSans(Font.BOLD, 11f));
        btnCerrar.setBackground(Theme.getPasajeroSoft());
        btnCerrar.setForeground(Theme.getPasajeroAccent());
        btnCerrar.addActionListener(e -> dialogo.dispose());

        panelBarra.add(btnReset);
        panelBarra.add(btnWireframe);
        panelBarra.add(btnAnim);
        panelBarra.add(btnCerrar);
        panelContenedor.add(panelBarra, BorderLayout.SOUTH);

        dialogo.setContentPane(panelContenedor);
        dialogo.setVisible(true);
    }

    public JPanel obtenerPanelArboles() {
        if (panelArbolesTab == null) {
            panelArbolesTab = crearPanelArboles(false);
        }
        return panelArbolesTab;
    }

    private JPanel crearPanelArboles(boolean soloLectura) {
        controlador.sincronizarIndiceArbol();
        JPanel pArboles = new JPanel(new BorderLayout(10, 10));
        pArboles.setBackground(fondoTarjeta);
        pArboles.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel pArbolBotones = new JPanel(new GridLayout(1, 2, 8, 0));
        pArbolBotones.setOpaque(false);

        JButton btnInOrden = new JButton("In-Order (Ordenado)");
        JButton btnPreOrden = new JButton("Pre-Order");
        JButton btnPostOrden = new JButton("Post-Order");
        JButton btnNiveles = new JButton("Por Niveles (BFS)");
        JButton btnDemoAVL = new JButton("Auto-Balance AVL (4 Rotaciones)");

        estilizarBotonAccion(btnInOrden, azulAccion, new Color(15, 20, 29));
        estilizarBotonAccion(btnPreOrden, new Color(30, 41, 59), textoBlanco);
        estilizarBotonAccion(btnPostOrden, new Color(30, 41, 59), textoBlanco);
        estilizarBotonAccion(btnNiveles, new Color(30, 41, 59), textoBlanco);
        estilizarBotonAccion(btnDemoAVL, new Color(16, 185, 129), new Color(15, 20, 29));

        JPanel pContenidoDFS = new JPanel(new GridLayout(0, 1, 0, 4));
        pContenidoDFS.setOpaque(false);
        pContenidoDFS.add(btnInOrden);
        pContenidoDFS.add(btnPreOrden);
        pContenidoDFS.add(btnPostOrden);
        GlassPanel cardDFS = GlassPanel.crearApartado("Recorridos en Profundidad (DFS)", "In-Order, Pre-Order, Post-Order", azulAccion, pContenidoDFS);

        JPanel pContenidoBFS = new JPanel(new GridLayout(0, 1, 0, 4));
        pContenidoBFS.setOpaque(false);
        pContenidoBFS.add(btnNiveles);
        pContenidoBFS.add(btnDemoAVL);
        GlassPanel cardBFS = GlassPanel.crearApartado("Amplitud (BFS) y AVL", "Cola FIFO y rotaciones automáticas", new Color(16, 185, 129), pContenidoBFS);

        pArbolBotones.add(cardDFS);
        pArbolBotones.add(cardBFS);
        pArboles.add(pArbolBotones, BorderLayout.NORTH);

        JTextArea txtArbolResultado = new JTextArea();
        txtArbolResultado.setEditable(false);
        txtArbolResultado.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtArbolResultado.setBackground(new Color(15, 23, 42));
        txtArbolResultado.setForeground(new Color(248, 250, 252));
        txtArbolResultado.setCaretPosition(0);
        pArboles.add(new JScrollPane(txtArbolResultado), BorderLayout.CENTER);

        JLabel lblMetricasArbol = new JLabel("Seleccione una operación para visualizar los recorridos en el ABB o el auto-balanceo en el Árbol AVL.");
        lblMetricasArbol.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblMetricasArbol.setForeground(azulCyan);
        pArboles.add(lblMetricasArbol, BorderLayout.SOUTH);

        btnInOrden.addActionListener(ev -> {
            controlador.sincronizarIndiceArbol();
            ArbolBinarioBusqueda<String, Vuelo> abb = controlador.obtenerServicioAeropuerto().obtenerArbolVuelos();
            List<Vuelo> lista = abb.recorridoInOrden();
            StringBuilder sb = new StringBuilder();
            sb.append("=== RECORRIDO IN-ORDER EN ÁRBOL BINARIO DE BÚSQUEDA (ABB) ===\n");
            sb.append("Propiedad: Visita subárbol izquierdo -> Raíz -> Subárbol derecho (Orden ascendente por clave)\n");
            sb.append("Total de Vuelos Indexados: ").append(abb.tamano()).append("\n");
            sb.append("Altura del Árbol (h): ").append(abb.obtenerAltura()).append("\n");
            sb.append("Cantidad de Hojas: ").append(abb.contarHojas()).append("\n\n");
            for (int i = 0; i < lista.size(); i++) {
                Vuelo v = lista.get(i);
                sb.append(String.format("  [%02d] Clave: %-8s | %s -> %-15s | Tarifa: S/. %.2f | Salida: %s\n",
                        i + 1, v.obtenerCodigo(), v.obtenerOrigen(), v.obtenerDestino(), v.obtenerPrecioBase(), v.obtenerHoraSalida()));
            }
            txtArbolResultado.setText(sb.toString());
            txtArbolResultado.setCaretPosition(0);
            lblMetricasArbol.setText("ABB In-Order completado: " + lista.size() + " vuelos ordenados por código | Altura: " + abb.obtenerAltura());
        });

        btnPreOrden.addActionListener(ev -> {
            controlador.sincronizarIndiceArbol();
            ArbolBinarioBusqueda<String, Vuelo> abb = controlador.obtenerServicioAeropuerto().obtenerArbolVuelos();
            List<Vuelo> lista = abb.recorridoPreOrden();
            StringBuilder sb = new StringBuilder();
            sb.append("=== RECORRIDO PRE-ORDER EN ÁRBOL BINARIO DE BÚSQUEDA (ABB) ===\n");
            sb.append("Propiedad: Visita Raíz -> Subárbol izquierdo -> Subárbol derecho\n");
            sb.append("Utilidad: Serialización, clonación y reconstrucción exacta de la topología del árbol\n\n");
            for (int i = 0; i < lista.size(); i++) {
                Vuelo v = lista.get(i);
                sb.append(String.format("  [%02d] Raíz/Nodo: %-8s | %s -> %s\n", i + 1, v.obtenerCodigo(), v.obtenerOrigen(), v.obtenerDestino()));
            }
            txtArbolResultado.setText(sb.toString());
            txtArbolResultado.setCaretPosition(0);
            lblMetricasArbol.setText("ABB Pre-Order completado: " + lista.size() + " nodos visitados en secuencia jerárquica.");
        });

        btnPostOrden.addActionListener(ev -> {
            controlador.sincronizarIndiceArbol();
            ArbolBinarioBusqueda<String, Vuelo> abb = controlador.obtenerServicioAeropuerto().obtenerArbolVuelos();
            List<Vuelo> lista = abb.recorridoPostOrden();
            StringBuilder sb = new StringBuilder();
            sb.append("=== RECORRIDO POST-ORDER EN ÁRBOL BINARIO DE BÚSQUEDA (ABB) ===\n");
            sb.append("Propiedad: Visita Subárbol izquierdo -> Subárbol derecho -> Raíz\n");
            sb.append("Utilidad: Liberación de memoria de abajo hacia arriba, evaluación de expresiones\n\n");
            for (int i = 0; i < lista.size(); i++) {
                Vuelo v = lista.get(i);
                sb.append(String.format("  [%02d] Nodo: %-8s | %s -> %s\n", i + 1, v.obtenerCodigo(), v.obtenerOrigen(), v.obtenerDestino()));
            }
            txtArbolResultado.setText(sb.toString());
            txtArbolResultado.setCaretPosition(0);
            lblMetricasArbol.setText("ABB Post-Order completado: " + lista.size() + " nodos procesados de hojas a raíz.");
        });

        btnNiveles.addActionListener(ev -> {
            controlador.sincronizarIndiceArbol();
            ArbolBinarioBusqueda<String, Vuelo> abb = controlador.obtenerServicioAeropuerto().obtenerArbolVuelos();
            List<Vuelo> lista = abb.recorridoPorNiveles();
            StringBuilder sb = new StringBuilder();
            sb.append("=== RECORRIDO POR NIVELES (BFS - AMPLITUD) EN ABB ===\n");
            sb.append("Propiedad: Recorrido horizontal nivel por nivel utilizando Cola FIFO iterativa\n");
            sb.append("Total de Nodos: ").append(lista.size()).append(" | Altura máxima: ").append(abb.obtenerAltura()).append("\n\n");
            for (int i = 0; i < lista.size(); i++) {
                Vuelo v = lista.get(i);
                sb.append(String.format("  [%02d] Nivel/Pos: %-8s | %s -> %s\n", i + 1, v.obtenerCodigo(), v.obtenerOrigen(), v.obtenerDestino()));
            }
            txtArbolResultado.setText(sb.toString());
            txtArbolResultado.setCaretPosition(0);
            lblMetricasArbol.setText("ABB Recorrido por Niveles completado mediante cola FIFO iterativa.");
        });

        btnDemoAVL.addActionListener(ev -> {
            ArbolAVL<String, String> demoAVL = new ArbolAVL<>();
            StringBuilder sb = new StringBuilder();
            sb.append("=== DEMOSTRACIÓN EN VIVO: ÁRBOL AVL AUTO-BALANCEADO ===\n");
            sb.append("Mecanismo: Cálculo del Factor de Balance (FE = Altura_Izq - Altura_Der)\n");
            sb.append("Condición de Balance Estricta: |FE| <= 1 en todos los nodos del árbol\n\n");

            sb.append("1. Inserción de claves para verificar rotaciones canónicas:\n");
            String[] clavesPrueba = {"LIM", "AQP", "CUZ", "JUL", "TRU", "PIU", "IQT", "TCQ", "CIX", "AYP"};
            for (String c : clavesPrueba) {
                demoAVL.insertar(c, "Vuelo-" + c);
                sb.append(String.format("   + Insertado '%s' -> Altura actual: %d | Total nodos: %d | Balance válido: %s\n",
                        c, demoAVL.obtenerAltura(), demoAVL.tamano(), demoAVL.verificarBalance() ? "SÍ (CUMPLE |FE|<=1)" : "NO"));
            }

            sb.append("\n2. Registro de Rotaciones Canónicas Disponibles en el TAD ArbolAVL:\n");
            sb.append("   - Rotación Simple Derecha (RSD / LL): Cuando FE > 1 y FE(hijo_izq) >= 0\n");
            sb.append("   - Rotación Simple Izquierda (RSI / RR): Cuando FE < -1 y FE(hijo_der) <= 0\n");
            sb.append("   - Rotación Doble Izquierda-Derecha (RDID / LR): Cuando FE > 1 y FE(hijo_izq) < 0\n");
            sb.append("   - Rotación Doble Derecha-Izquierda (RDDI / RL): Cuando FE < -1 y FE(hijo_der) > 0\n\n");

            sb.append("3. Recorrido In-Order del Árbol AVL (Garantizado O(log n) en búsqueda):\n");
            List<String> inOrder = demoAVL.recorridoInOrden();
            sb.append("   Secuencia: ").append(inOrder).append("\n\n");

            sb.append("4. Recorrido por Niveles (Topología Equilibrada):\n");
            List<String> niveles = demoAVL.recorridoPorNiveles();
            sb.append("   Por Niveles: ").append(niveles).append("\n\n");

            sb.append("Estado Final del AVL: Altura = ").append(demoAVL.obtenerAltura())
              .append(" (En árbol no balanceado sería hasta ").append(demoAVL.tamano()).append(")\n")
              .append("Garantía de Búsqueda: O(log n) en el peor de los casos.\n");

            txtArbolResultado.setText(sb.toString());
            txtArbolResultado.setCaretPosition(0);
            lblMetricasArbol.setText("Demostración AVL ejecutada: 10 nodos balanceados con éxito. Altura = " + demoAVL.obtenerAltura());
        });

        return pArboles;
    }

    public JPanel obtenerPanelGrafo() {
        if (panelGrafoTab == null) {
            panelGrafoTab = crearPanelGrafo(false);
        }
        return panelGrafoTab;
    }

    private JPanel crearPanelGrafo(boolean soloLectura) {
        JPanel pGrafo = new JPanel(new BorderLayout(10, 10));
        pGrafo.setBackground(fondoTarjeta);
        pGrafo.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel pFormGrafo = new JPanel(new GridLayout(1, 3, 8, 0));
        pFormGrafo.setOpaque(false);

        String[] aeropuertos = {"LIM", "CUZ", "AQP", "TRU", "IQT", "PIU", "TCQ", "JUL"};
        JComboBox<String> cbOrigen = new JComboBox<>(aeropuertos);
        JComboBox<String> cbDestino = new JComboBox<>(aeropuertos);
        cbDestino.setSelectedItem("CUZ");
        estilizarComboModerno(cbOrigen);
        estilizarComboModerno(cbDestino);

        JButton btnCalcularDijkstra = new JButton("Dijkstra (Ruta Mínima)");
        JButton btnCalcularPrim = new JButton("Prim (MST Troncal)");
        JButton btnCalcularKruskal = new JButton("Kruskal (MST - DSU)");
        JButton btnCalcularFloyd = new JButton("Floyd-Warshall (Matriz O(V^3))");

        estilizarBotonAccion(btnCalcularDijkstra, azulAccion, new Color(15, 20, 29));
        estilizarBotonAccion(btnCalcularPrim, new Color(16, 185, 129), new Color(15, 20, 29));
        estilizarBotonAccion(btnCalcularKruskal, new Color(245, 158, 11), new Color(15, 20, 29));
        estilizarBotonAccion(btnCalcularFloyd, new Color(139, 92, 246), new Color(15, 20, 29));

        JPanel pContenidoDijkstra = new JPanel(new GridLayout(0, 1, 0, 4));
        pContenidoDijkstra.setOpaque(false);
        JPanel pSelRuta = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        pSelRuta.setOpaque(false);
        pSelRuta.add(cbOrigen);
        JLabel lblFlecha = new JLabel("->");
        lblFlecha.setForeground(azulCyan);
        pSelRuta.add(lblFlecha);
        pSelRuta.add(cbDestino);
        pContenidoDijkstra.add(pSelRuta);
        pContenidoDijkstra.add(btnCalcularDijkstra);
        GlassPanel cardDijkstra = GlassPanel.crearApartado("Ruta Óptima Punto a Punto", "Dijkstra O((V+E) log V)", azulAccion, pContenidoDijkstra);

        JPanel pContenidoMST = new JPanel(new GridLayout(0, 1, 0, 4));
        pContenidoMST.setOpaque(false);
        pContenidoMST.add(btnCalcularPrim);
        pContenidoMST.add(btnCalcularKruskal);
        GlassPanel cardMST = GlassPanel.crearApartado("Árboles de Expansión Mínima", "Prim y Kruskal (DSU)", new Color(16, 185, 129), pContenidoMST);

        JPanel pContenidoFloyd = new JPanel(new GridLayout(0, 1, 0, 4));
        pContenidoFloyd.setOpaque(false);
        pContenidoFloyd.add(btnCalcularFloyd);
        GlassPanel cardFloyd = GlassPanel.crearApartado("Todas las Parejas", "Floyd-Warshall O(V^3)", new Color(139, 92, 246), pContenidoFloyd);

        pFormGrafo.add(cardDijkstra);
        pFormGrafo.add(cardMST);
        pFormGrafo.add(cardFloyd);
        pGrafo.add(pFormGrafo, BorderLayout.NORTH);

        JTextArea txtGrafoResultado = new JTextArea();
        txtGrafoResultado.setEditable(false);
        txtGrafoResultado.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtGrafoResultado.setBackground(new Color(15, 23, 42));
        txtGrafoResultado.setForeground(new Color(248, 250, 252));
        txtGrafoResultado.setText("Seleccione una operación para ejecutar algoritmos sobre el Grafo Ponderado de Rutas CORPAC.");
        pGrafo.add(new JScrollPane(txtGrafoResultado), BorderLayout.CENTER);

        btnCalcularDijkstra.addActionListener(ev -> {
            String orig = (String) cbOrigen.getSelectedItem();
            String dest = (String) cbDestino.getSelectedItem();
            List<Vertice> ruta = controlador.calcularRutaOptima(orig, dest);
            if (ruta.isEmpty()) {
                txtGrafoResultado.setText("No existe una ruta disponible entre " + orig + " y " + dest + ".");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("=== RUTA ÓPTIMA CALCULADA MEDIANTE ALGORITMO DE DIJKSTRA ===\n\n");
                sb.append("Origen: ").append(orig).append("\n");
                sb.append("Destino: ").append(dest).append("\n");
                sb.append("Tramos calculados: ").append(ruta.size() - 1).append(" tramos directos\n\n");
                sb.append("Secuencia de Vuelo:\n");
                for (int i = 0; i < ruta.size(); i++) {
                    Vertice vert = ruta.get(i);
                    sb.append("  [").append(i + 1).append("] ").append(vert.obtenerCodigoIATA())
                      .append(" - ").append(vert.obtenerCiudad()).append(" (").append(vert.obtenerNombreAeropuerto()).append(")\n");
                }
                sb.append("\nComplejidad Algorítmica: O((V + E) log V)\n");
                sb.append("Estructura: Grafo Ponderado con Lista y Matriz de Adyacencia.\n");
                txtGrafoResultado.setText(sb.toString());
                txtGrafoResultado.setCaretPosition(0);
            }
        });

        btnCalcularPrim.addActionListener(ev -> {
            String orig = (String) cbOrigen.getSelectedItem();
            ResultadoMST mst = controlador.calcularMSTPrim(orig);
            txtGrafoResultado.setText(mst.toString());
            txtGrafoResultado.setCaretPosition(0);
        });

        btnCalcularKruskal.addActionListener(ev -> {
            ResultadoMST mst = controlador.calcularMSTKruskal();
            txtGrafoResultado.setText(mst.toString());
            txtGrafoResultado.setCaretPosition(0);
        });

        btnCalcularFloyd.addActionListener(ev -> {
            ResultadoFloydWarshall rfw = controlador.calcularFloydWarshall();
            txtGrafoResultado.setText(rfw.generarReporteMatriz());
            txtGrafoResultado.setCaretPosition(0);
        });

        return pGrafo;
    }

    public JPanel obtenerPanelHashHeap() {
        if (panelHashHeapTab == null) {
            panelHashHeapTab = crearPanelHashHeap(false);
        }
        return panelHashHeapTab;
    }

    private JPanel crearPanelHashHeap(boolean soloLectura) {
        JPanel pHashHeap = new JPanel(new BorderLayout(10, 10));
        pHashHeap.setBackground(fondoTarjeta);
        pHashHeap.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel pHashControles = new JPanel(new GridLayout(1, 2, 8, 0));
        pHashControles.setOpaque(false);

        JPanel filaHash = new JPanel(new GridLayout(0, 1, 0, 4));
        filaHash.setOpaque(false);
        JPanel pDniInput = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        pDniInput.setOpaque(false);
        pDniInput.add(crearEtiquetaFormulario("DNI Pasajero:"));
        JTextField txtDniHash = new JTextField("72104589", 9);
        txtDniHash.setBackground(new Color(15, 23, 42));
        txtDniHash.setForeground(textoBlanco);
        txtDniHash.setCaretColor(textoBlanco);
        pDniInput.add(txtDniHash);
        JButton btnBuscarHash = new JButton("Buscar O(1)");
        JButton btnEstadoHash = new JButton("Auditar Carga");
        estilizarBotonAccion(btnBuscarHash, azulAccion, new Color(15, 20, 29));
        estilizarBotonAccion(btnEstadoHash, new Color(30, 41, 59), textoBlanco);
        filaHash.add(pDniInput);
        filaHash.add(btnBuscarHash);
        filaHash.add(btnEstadoHash);
        GlassPanel cardHash = GlassPanel.crearApartado("Tabla Hash (Separate Chaining)", "Acceso O(1) promedio y dispersión", azulAccion, filaHash);

        JPanel filaHeap = new JPanel(new GridLayout(0, 1, 0, 4));
        filaHeap.setOpaque(false);
        JButton btnPoblarHeap = new JButton("Encolar 5 Aeronaves");
        JButton btnDespacharHeap = new JButton("Despachar Mínimo O(log n)");
        JButton btnVaciarHeap = new JButton("Vaciar");
        estilizarBotonAccion(btnPoblarHeap, new Color(16, 185, 129), new Color(15, 20, 29));
        estilizarBotonAccion(btnDespacharHeap, new Color(245, 158, 11), new Color(15, 20, 29));
        estilizarBotonAccion(btnVaciarHeap, new Color(239, 68, 68), textoBlanco);
        filaHeap.add(btnPoblarHeap);
        filaHeap.add(btnDespacharHeap);
        filaHeap.add(btnVaciarHeap);
        GlassPanel cardHeap = GlassPanel.crearApartado("Montículo Binario (Min-Heap)", "Prioridad O(log n) para emergencias", new Color(245, 158, 11), filaHeap);

        pHashControles.add(cardHash);
        pHashControles.add(cardHeap);
        pHashHeap.add(pHashControles, BorderLayout.NORTH);

        JTextArea txtHashHeapLog = new JTextArea();
        txtHashHeapLog.setEditable(false);
        txtHashHeapLog.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtHashHeapLog.setBackground(new Color(15, 23, 42));
        txtHashHeapLog.setForeground(new Color(248, 250, 252));
        txtHashHeapLog.setText("=== ESTRUCTURAS HASH Y MONTÍCULO BINARIO ===\n\n" +
                "1. TABLA HASH: Acceso O(1) promedio por dispersión bit-shift, factor de carga <= 0.75 y encadenamiento.\n" +
                "2. TAD HEAP (Min-Heap): Inserción y extracción O(log n) garantizado para control de pista ATC.");
        pHashHeap.add(new JScrollPane(txtHashHeapLog), BorderLayout.CENTER);

        btnBuscarHash.addActionListener(ev -> {
            String dni = txtDniHash.getText().trim();
            if (dni.isEmpty()) {
                txtHashHeapLog.setText("Por favor ingrese un DNI a consultar en la Tabla Hash.");
                return;
            }
            TablaHash<String, String> tabla = controlador.obtenerTablaHashPasajeros();
            String datos = tabla.buscar(dni);
            StringBuilder sb = new StringBuilder();
            sb.append("=== CONSULTA EN TABLA HASH (COMPLEJIDAD O(1)) ===\n\n");
            sb.append("Clave consultada (DNI): ").append(dni).append("\n");
            if (datos != null) {
                sb.append("Estado: ENCONTRADO EN LA TABLA HASH\n");
                sb.append("Valor asociado: ").append(datos).append("\n");
            } else {
                sb.append("Estado: NO ENCONTRADO (Pasajero aún no registrado en la tabla hash actual)\n");
            }
            sb.append("\nMétricas de la Tabla Hash:\n");
            sb.append("  - Elementos almacenados: ").append(tabla.obtenerTamano()).append("\n");
            sb.append("  - Capacidad de cubetas: ").append(tabla.obtenerCapacidad()).append("\n");
            sb.append(String.format("  - Factor de carga actual: %.4f (Límite máximo: 0.75)\n", tabla.obtenerFactorCargaActual()));
            sb.append("  - Colisiones resueltas por encadenamiento: ").append(tabla.obtenerTotalColisiones()).append("\n");
            txtHashHeapLog.setText(sb.toString());
            txtHashHeapLog.setCaretPosition(0);
        });

        btnEstadoHash.addActionListener(ev -> {
            TablaHash<String, String> tabla = controlador.obtenerTablaHashPasajeros();
            StringBuilder sb = new StringBuilder();
            sb.append("=== AUDITORÍA Y ESTADO TÉCNICO DE LA TABLA HASH ===\n\n");
            sb.append("Función de Dispersión: ((h ^ (h >>> 16)) & 0x7FFFFFFF) % capacidad\n");
            sb.append("Resolución de Colisiones: Encadenamiento Separado (Separate Chaining)\n");
            sb.append("Capacidad actual: ").append(tabla.obtenerCapacidad()).append(" cubetas primas\n");
            sb.append("Total de registros almacenados: ").append(tabla.obtenerTamano()).append("\n");
            sb.append(String.format("Factor de Carga (alpha): %.4f\n", tabla.obtenerFactorCargaActual()));
            sb.append("Total de Colisiones Históricas: ").append(tabla.obtenerTotalColisiones()).append("\n\n");
            sb.append("Claves indexadas actualmente (primeras 15):\n");
            List<String> claves = tabla.listarClaves();
            for (int i = 0; i < Math.min(claves.size(), 15); i++) {
                String c = claves.get(i);
                sb.append(String.format("  [%02d] DNI: %-12s -> %s\n", i + 1, c, tabla.buscar(c)));
            }
            if (claves.size() > 15) {
                sb.append("  ... y ").append(claves.size() - 15).append(" registros más.\n");
            }
            txtHashHeapLog.setText(sb.toString());
            txtHashHeapLog.setCaretPosition(0);
        });

        final MonticuloBinario<String> heapDemoLocal = new MonticuloBinario<>();

        btnPoblarHeap.addActionListener(ev -> {
            MonticuloBinario<String> heap = soloLectura ? heapDemoLocal : controlador.obtenerHeapATC();
            heap.insertar("LAN-2041 (Emergencia Médica)", 1);
            heap.insertar("SKU-3012 (Combustible Crítico)", 2);
            heap.insertar("JAT-4015 (Vuelo Comercial Regular)", 5);
            heap.insertar("AMZ-1102 (Carga Prioritaria)", 4);
            heap.insertar("VIP-0001 (Vuelo Gubernamental)", 3);

            StringBuilder sb = new StringBuilder();
            sb.append("=== AERONAVES ENCOLADAS EN EL MONTÍCULO BINARIO (MIN-HEAP)").append(soloLectura ? " [COPIA LOCAL DEMO] ===" : " ===").append("\n\n");
            sb.append("Total de aeronaves en espera: ").append(heap.obtenerTamano()).append("\n");
            sb.append("Próxima aeronave con máxima prioridad de pista:\n");
            sb.append("  -> CIMA DEL HEAP: ").append(heap.cima()).append(" | Prioridad: ").append(heap.prioridadCima()).append("\n\n");
            sb.append("Elementos en el arreglo del montículo:\n");
            List<String> elem = heap.listarElementos();
            for (int i = 0; i < elem.size(); i++) {
                sb.append(String.format("  [Pos %d] %s\n", i, elem.get(i)));
            }
            sb.append("\nComplejidad de Inserción (flotar): O(log n)\n");
            txtHashHeapLog.setText(sb.toString());
            txtHashHeapLog.setCaretPosition(0);
        });

        btnDespacharHeap.addActionListener(ev -> {
            MonticuloBinario<String> heap = soloLectura ? heapDemoLocal : controlador.obtenerHeapATC();
            if (heap.estaVacio()) {
                txtHashHeapLog.setText("El Montículo Binario se encuentra vacío actualmente. Encole aeronaves primero.");
                return;
            }
            int prioridad = heap.prioridadCima();
            String nave = heap.extraerMinimo();
            StringBuilder sb = new StringBuilder();
            sb.append("=== DESPACHO DE PISTA REALIZADO MEDIANTE MIN-HEAP O(log n)").append(soloLectura ? " [COPIA LOCAL DEMO] ===" : " ===").append("\n\n");
            sb.append("Aeronave autorizada para despegue / aterrizaje inmediato:\n");
            sb.append("  [VUELO] ").append(nave).append(" (Nivel de Prioridad: ").append(prioridad).append(")\n\n");
            sb.append("Aeronaves restantes en espera: ").append(heap.obtenerTamano()).append("\n");
            if (!heap.estaVacio()) {
                sb.append("Siguiente en la cima: ").append(heap.cima()).append(" (Prioridad: ").append(heap.prioridadCima()).append(")\n");
            } else {
                sb.append("Pista despejada: No quedan aeronaves en cola.\n");
            }
            txtHashHeapLog.setText(sb.toString());
            txtHashHeapLog.setCaretPosition(0);
        });

        if (soloLectura) {
            btnVaciarHeap.setEnabled(false);
            btnVaciarHeap.setToolTipText("Deshabilitado en modo solo lectura");
        }

        btnVaciarHeap.addActionListener(ev -> {
            controlador.obtenerHeapATC().vaciar();
            txtHashHeapLog.setText("Montículo Binario (Min-Heap) vaciado correctamente. Capacidad reiniciada.");
        });

        return pHashHeap;
    }

    public JPanel obtenerPanelBenchmarking() {
        if (panelBenchTab == null) {
            panelBenchTab = crearPanelBenchmarking(false);
        }
        return panelBenchTab;
    }

    private JPanel crearPanelBenchmarking(boolean soloLectura) {
        JPanel pBench = new JPanel(new BorderLayout(10, 10));
        pBench.setBackground(fondoTarjeta);
        pBench.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel pBenchAcciones = new JPanel(new GridLayout(1, 2, 8, 0));
        pBenchAcciones.setOpaque(false);

        JButton btnEjecutarBench = new JButton("Ejecutar Benchmark Completo");
        JButton btnSortQuick = new JButton("Quicksort (Precio Asc)");
        JButton btnSortMerge = new JButton("Mergesort (Destino)");
        JButton btnSortHeap = new JButton("Heapsort (Precio Desc)");

        estilizarBotonAccion(btnEjecutarBench, new Color(16, 185, 129), new Color(15, 20, 29));
        estilizarBotonAccion(btnSortQuick, azulAccion, new Color(15, 20, 29));
        estilizarBotonAccion(btnSortMerge, new Color(30, 41, 59), textoBlanco);
        estilizarBotonAccion(btnSortHeap, new Color(30, 41, 59), textoBlanco);

        JPanel pContenidoBench = new JPanel(new GridLayout(0, 1, 0, 4));
        pContenidoBench.setOpaque(false);
        pContenidoBench.add(btnEjecutarBench);
        GlassPanel cardBench = GlassPanel.crearApartado("Evaluación Empírica", "Medición estricta en nanosegundos", new Color(16, 185, 129), pContenidoBench);

        JPanel pContenidoSort = new JPanel(new GridLayout(0, 1, 0, 4));
        pContenidoSort.setOpaque(false);
        pContenidoSort.add(btnSortQuick);
        pContenidoSort.add(btnSortMerge);
        pContenidoSort.add(btnSortHeap);
        GlassPanel cardSort = GlassPanel.crearApartado("Algoritmos Avanzados O(n log n)", "Ordenamiento óptimo", azulAccion, pContenidoSort);

        pBenchAcciones.add(cardBench);
        pBenchAcciones.add(cardSort);
        pBench.add(pBenchAcciones, BorderLayout.NORTH);

        JTextArea txtBenchResultado = new JTextArea();
        txtBenchResultado.setEditable(false);
        txtBenchResultado.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtBenchResultado.setBackground(new Color(15, 23, 42));
        txtBenchResultado.setForeground(new Color(248, 250, 252));
        txtBenchResultado.setText("Presione 'Ejecutar Benchmark Completo' para medir tiempos en nanosegundos y microsegundos\n" +
                "entre algoritmos cuadráticos O(n^2) y algoritmos avanzados O(n log n).");
        pBench.add(new JScrollPane(txtBenchResultado), BorderLayout.CENTER);

        btnEjecutarBench.addActionListener(ev -> {
            String reporte = controlador.ejecutarBenchmarkOrdenamientos();
            txtBenchResultado.setText(reporte);
            txtBenchResultado.setCaretPosition(0);
        });

        btnSortQuick.addActionListener(ev -> {
            if (soloLectura) {
                int n = controlador.obtenerContadorVuelos();
                Vuelo[] copia = new Vuelo[n];
                System.arraycopy(controlador.obtenerVuelos(), 0, copia, 0, n);
                AlgoritmosOrdenamientoAvanzado.quicksortPorPrecio(copia, true);
                StringBuilder sb = new StringBuilder();
                sb.append("=== QUICKSORT O(n log n) [COPIA LOCAL - VISTA DE DEMOSTRACIÓN (SOLO LECTURA)] ===\n\n");
                sb.append("Tarifas ordenadas ascendentemente sin modificar el catálogo activo del aeropuerto:\n\n");
                for (int i = 0; i < copia.length; i++) {
                    sb.append(String.format("  [%02d] Vuelo: %-8s | %s -> %-15s | Tarifa: S/. %.2f\n",
                            i + 1, copia[i].obtenerCodigo(), copia[i].obtenerOrigen(), copia[i].obtenerDestino(), copia[i].obtenerPrecioBase()));
                }
                txtBenchResultado.setText(sb.toString());
                txtBenchResultado.setCaretPosition(0);
                return;
            }
            controlador.ordenarVuelosQuicksort(true);
            actualizarTablaVuelos();
            txtBenchResultado.setText("Catálogo ordenado exitosamente mediante QUICKSORT (O(n log n)) por Tarifa Ascendente.\n" +
                    "La tabla de vuelos en la vista principal y panel de administración ha sido actualizada.");
        });

        btnSortMerge.addActionListener(ev -> {
            if (soloLectura) {
                int n = controlador.obtenerContadorVuelos();
                Vuelo[] copia = new Vuelo[n];
                System.arraycopy(controlador.obtenerVuelos(), 0, copia, 0, n);
                AlgoritmosOrdenamientoAvanzado.mergesortPorDestino(copia, true);
                StringBuilder sb = new StringBuilder();
                sb.append("=== MERGESORT O(n log n) ESTABLE [COPIA LOCAL - VISTA DE DEMOSTRACIÓN (SOLO LECTURA)] ===\n\n");
                sb.append("Destinos ordenados alfabéticamente de forma estable sin mutar las estructuras globales:\n\n");
                for (int i = 0; i < copia.length; i++) {
                    sb.append(String.format("  [%02d] Destino: %-15s | Vuelo: %-8s | Tarifa: S/. %.2f\n",
                            i + 1, copia[i].obtenerDestino(), copia[i].obtenerCodigo(), copia[i].obtenerPrecioBase()));
                }
                txtBenchResultado.setText(sb.toString());
                txtBenchResultado.setCaretPosition(0);
                return;
            }
            controlador.ordenarVuelosMergesort(true);
            actualizarTablaVuelos();
            txtBenchResultado.setText("Catálogo ordenado exitosamente mediante MERGESORT (O(n log n) Estable) por Destino.\n" +
                    "La tabla de vuelos en la vista principal y panel de administración ha sido actualizada.");
        });

        btnSortHeap.addActionListener(ev -> {
            if (soloLectura) {
                int n = controlador.obtenerContadorVuelos();
                Vuelo[] copia = new Vuelo[n];
                System.arraycopy(controlador.obtenerVuelos(), 0, copia, 0, n);
                AlgoritmosOrdenamientoAvanzado.heapsortPorPrecio(copia, false);
                StringBuilder sb = new StringBuilder();
                sb.append("=== HEAPSORT O(n log n) [COPIA LOCAL - VISTA DE DEMOSTRACIÓN (SOLO LECTURA)] ===\n\n");
                sb.append("Tarifas ordenadas descendentemente sobre montículo sin mutar las estructuras globales:\n\n");
                for (int i = 0; i < copia.length; i++) {
                    sb.append(String.format("  [%02d] Vuelo: %-8s | %s -> %-15s | Tarifa: S/. %.2f\n",
                            i + 1, copia[i].obtenerCodigo(), copia[i].obtenerOrigen(), copia[i].obtenerDestino(), copia[i].obtenerPrecioBase()));
                }
                txtBenchResultado.setText(sb.toString());
                txtBenchResultado.setCaretPosition(0);
                return;
            }
            controlador.ordenarVuelosHeapsort(false);
            actualizarTablaVuelos();
            txtBenchResultado.setText("Catálogo ordenado exitosamente mediante HEAPSORT (O(n log n) sobre Montículo) por Tarifa Descendente.\n" +
                    "La tabla de vuelos en la vista principal y panel de administración ha sido actualizada.");
        });

        return pBench;
    }

    private JComponent crearPanelLaboratorioAvanzado() {
        return crearPanelLaboratorioAvanzado(false);
    }

    private JComponent crearPanelLaboratorioAvanzado(boolean soloLectura) {
        JTabbedPane pestañas = new JTabbedPane();
        pestañas.addTab("Árboles (ABB y AVL)", obtenerPanelArboles());
        pestañas.addTab("Grafo de Rutas (Dijkstra / MST / Floyd)", obtenerPanelGrafo());
        pestañas.addTab("Tablas Hash y Montículo (Heap)", obtenerPanelHashHeap());
        pestañas.addTab("Benchmarking Ordenamiento O(n log n)", obtenerPanelBenchmarking());
        return pestañas;
    }

    // Eventos de Equipaje (Lista Simple)
    private void accionListaAgregarMaleta() {
        String id = txtListaEquipajeId.getText().trim();
        String dni = txtListaEquipajeDni.getText().trim();
        String vuelo = (String) cbListaEquipajeVuelo.getSelectedItem();
        String strPeso = txtListaEquipajePeso.getText().trim();
        String tipo = (String) cbListaEquipajeTipo.getSelectedItem();

        if (id.isEmpty() || dni.isEmpty() || strPeso.isEmpty() || tipo == null) {
            mostrarErrorEquipaje("Complete todos los campos del equipaje.");
            return;
        }

        if (vuelo == null || vuelo.trim().isEmpty()) {
            mostrarErrorEquipaje("Debe seleccionar un vuelo. Si no existen vuelos registrados, registre uno primero en el Catálogo.");
            return;
        }

        double peso;
        try {
            peso = Double.parseDouble(strPeso.replace(',', '.'));
        } catch (NumberFormatException e) {
            mostrarErrorEquipaje("El peso debe ser un número válido.");
            return;
        }

        if (peso <= 0.0) {
            mostrarErrorEquipaje("El peso debe ser mayor a 0 kg.");
            return;
        }

        if (listaEquipaje.existeMaleta(id)) {
            mostrarErrorEquipaje("El ID de equipaje " + id + " ya se encuentra registrado.");
            return;
        }

        // Actualizar barra de peso dinámico
        actualizarMedidorPeso(peso);

        Equipaje nuevoEquipaje = new Equipaje(id, dni, vuelo, peso, tipo);
        if (controlador.agregarEquipaje(nuevoEquipaje)) {
            actualizarContadorMaletas();
            actualizarMetricasDashboard();
            actualizarMetricasSQLTab();
            txtResultadoEquipaje.setText("Maleta registrada en Lista Simple y MySQL:\n" + nuevoEquipaje.toString() + "\n\nREPRESENTACIÓN DE NODOS EN MEMORIA:\n" + listaEquipaje.obtenerRepresentacionNodal());
            log("[EQUIPAJE] Maleta " + id + " (DNI " + dni + ", " + peso + " kg) registrada en Lista Simple.");
            JOptionPane.showMessageDialog(this, "Maleta registrada con éxito.", "Gestión de Equipaje", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void actualizarMedidorPeso(double peso) {
        if (barraPesoEquipaje == null) return;
        int valorEntero = (int) Math.min(peso, 32.0);
        barraPesoEquipaje.setValue(valorEntero);
        if (peso > 23.0) {
            barraPesoEquipaje.setForeground(rojoOcupado);
            barraPesoEquipaje.setString(String.format("Peso: %.1f kg [ALERTA: SOBREPESO +S/. 50.00]", peso));
            lblAlertaSobrepeso.setText("Exceso de peso detectado (> 23.0 kg) - Aplicar recargo aeroportuario.");
            lblAlertaSobrepeso.setForeground(rojoOcupado);
        } else {
            barraPesoEquipaje.setForeground(verdeLibre);
            barraPesoEquipaje.setString(String.format("Peso: %.1f kg [DENTRO DEL LÍMITE PERMITIDO]", peso));
            lblAlertaSobrepeso.setText("Equipaje estándar cumple especificaciones de cabina/bodega.");
            lblAlertaSobrepeso.setForeground(new Color(60, 60, 60));
        }
    }

    private void accionListaBuscarMaleta() {
        String id = txtListaEquipajeId.getText().trim();
        if (id.isEmpty()) {
            mostrarErrorEquipaje("Ingrese el ID de la maleta a buscar.");
            return;
        }
        Equipaje encontrada = listaEquipaje.buscarMaleta(id);
        if (encontrada == null) {
            mostrarErrorEquipaje("No se encontró ninguna maleta con ID " + id);
            return;
        }
        txtListaEquipajeDni.setText(encontrada.obtenerDniPasajero());
        txtListaEquipajePeso.setText(String.valueOf(encontrada.obtenerPeso()));
        cbListaEquipajeTipo.setSelectedItem(encontrada.obtenerTipo());
        cbListaEquipajeVuelo.setSelectedItem(encontrada.obtenerCodigoVuelo());
        txtListaEquipajeNuevoPeso.setText(String.valueOf(encontrada.obtenerPeso()));
        cbListaEquipajeNuevoTipo.setSelectedItem(encontrada.obtenerTipo());
        actualizarMedidorPeso(encontrada.obtenerPeso());

        txtResultadoEquipaje.setText("Maleta localizada en Lista Simple:\n" + encontrada.toString());
        log("[EQUIPAJE] Búsqueda lineal en Lista Simple: Maleta " + id + " encontrada.");
    }

    private void accionListaEliminarMaleta() {
        if (!verificarPermisoAdmin("Eliminar Equipaje")) return;
        String id = txtListaEquipajeId.getText().trim();
        if (id.isEmpty()) {
            mostrarErrorEquipaje("Ingrese el ID de la maleta a eliminar.");
            return;
        }
        if (controlador.eliminarEquipaje(id)) {
            actualizarContadorMaletas();
            actualizarMetricasDashboard();
            actualizarMetricasSQLTab();
            txtResultadoEquipaje.setText("Maleta con ID " + id + " eliminada de Lista Simple y MySQL.\n\n" + listaEquipaje.obtenerRepresentacionNodal());
            log("[EQUIPAJE] Maleta " + id + " eliminada con reconexión de punteros simples.");
            JOptionPane.showMessageDialog(this, "Maleta eliminada correctamente.", "Gestión de Equipaje", JOptionPane.INFORMATION_MESSAGE);
        } else {
            mostrarErrorEquipaje("No se pudo eliminar la maleta con ID " + id);
        }
    }

    private void accionListaActualizarMaleta() {
        if (!verificarPermisoAdmin("Actualizar Equipaje")) return;
        String id = txtListaEquipajeId.getText().trim();
        String strNuevoPeso = txtListaEquipajeNuevoPeso.getText().trim();
        String nuevoTipo = (String) cbListaEquipajeNuevoTipo.getSelectedItem();

        if (id.isEmpty() || strNuevoPeso.isEmpty() || nuevoTipo == null) {
            mostrarErrorEquipaje("Complete el ID, nuevo peso y nuevo tipo.");
            return;
        }

        try {
            double nuevoPeso = Double.parseDouble(strNuevoPeso.replace(',', '.'));
            if (nuevoPeso <= 0.0) {
                mostrarErrorEquipaje("El peso debe ser mayor a 0.");
                return;
            }
            if (controlador.actualizarEquipaje(id, nuevoPeso, nuevoTipo)) {
                actualizarContadorMaletas();
                actualizarMetricasDashboard();
                actualizarMetricasSQLTab();
                actualizarMedidorPeso(nuevoPeso);
                Equipaje act = listaEquipaje.buscarMaleta(id);
                txtResultadoEquipaje.setText("Maleta actualizada en Lista Simple y MySQL:\n" + (act != null ? act.toString() : id));
                log("[EQUIPAJE] Maleta " + id + " actualizada (Peso: " + nuevoPeso + " kg, Tipo: " + nuevoTipo + ").");
                JOptionPane.showMessageDialog(this, "Maleta actualizada con éxito.", "Gestión de Equipaje", JOptionPane.INFORMATION_MESSAGE);
            } else {
                mostrarErrorEquipaje("No se encontró la maleta con ID " + id);
            }
        } catch (NumberFormatException e) {
            mostrarErrorEquipaje("El nuevo peso debe ser numérico.");
        }
    }

    private void accionListaListarMaletas() {
        txtResultadoEquipaje.setText("=== LISTADO DE EQUIPAJE - LISTA ENLAZADA SIMPLE ===\n\n" +
                listaEquipaje.listarMaletas() + "\n\nREPRESENTACIÓN DE NODOS EN MEMORIA:\n" +
                listaEquipaje.obtenerRepresentacionNodal());
        log("[EQUIPAJE] Listado completo de maletas en memoria (" + listaEquipaje.obtenerTamano() + " elementos).");
    }

    private void accionListaOrdenarPorPeso() {
        if (listaEquipaje.estaVacia()) {
            mostrarErrorEquipaje("La lista de equipaje está vacía.");
            return;
        }
        listaEquipaje.ordenarPorPeso();
        txtResultadoEquipaje.setText("=== EQUIPAJES ORDENADOS POR PESO - BURBUJA ASCENDENTE ===\n\n" +
                listaEquipaje.listarMaletas());
        log("[EQUIPAJE] Lista Simple ordenada por peso mediante intercambio entre nodos.");
        JOptionPane.showMessageDialog(this, "Equipajes ordenados por peso ascendentemente.", "Ordenamiento Burbuja", JOptionPane.INFORMATION_MESSAGE);
    }

    private void accionControlSobrepeso() {
        double pesoTotal = listaEquipaje.calcularPesoTotal();
        int sobrepeso = listaEquipaje.contarSobrepeso(23.0);
        String mensaje = String.format(java.util.Locale.US,
                "CONTROL DE SOBREPESO DE EQUIPAJES (ESTÁNDAR IATA):\n\n" +
                "- Total de piezas registradas: %d\n" +
                "- Peso acumulado en bodega/cabina: %.2f kg\n" +
                "- Maletas con sobrepeso (> 23.0 kg): %d\n\n" +
                "%s",
                listaEquipaje.obtenerTamano(), pesoTotal, sobrepeso,
                sobrepeso > 0 ? "Atención: Las maletas con sobrepeso requieren pago de recargo por exceso de equipaje." : "Todos los equipajes cumplen el estándar de peso permitido.");
        txtResultadoEquipaje.setText(mensaje);
        log("[EQUIPAJE] Control de sobrepeso: Peso total " + pesoTotal + " kg, " + sobrepeso + " piezas en exceso.");
        JOptionPane.showMessageDialog(this, mensaje, "Control de Equipaje", JOptionPane.INFORMATION_MESSAGE);
    }

    // Eventos de Rutas (Lista Doble)
    private void accionListaAgregarRuta() {
        String id = txtListaRutaId.getText().trim();
        String origen = (String) cbListaRutaOrigen.getSelectedItem();
        String destino = (String) cbListaRutaDestino.getSelectedItem();
        String strDistancia = txtListaRutaDistancia.getText().trim();

        if (id.isEmpty() || origen == null || destino == null || strDistancia.isEmpty() || origen.equalsIgnoreCase(destino)) {
            mostrarErrorRuta("Complete los datos y asegúrese de que origen y destino sean distintos.");
            return;
        }

        try {
            double distancia = Double.parseDouble(strDistancia.replace(',', '.'));
            if (distancia <= 0.0) {
                mostrarErrorRuta("La distancia debe ser mayor a 0 km.");
                return;
            }
            if (listaRutas.existeRuta(id)) {
                mostrarErrorRuta("El ID de ruta " + id + " ya existe.");
                return;
            }

            Ruta nuevaRuta = new Ruta(id, origen, destino, distancia);
            if (controlador.agregarRuta(nuevaRuta)) {
                actualizarContadorRutas();
                actualizarNavegadorRutas();
                actualizarMetricasDashboard();
                actualizarMetricasSQLTab();
                txtResultadoRutas.setText("Ruta agregada en Lista Doble y MySQL:\n" + nuevaRuta.toString());
                log("[RUTAS] Ruta " + id + " (" + origen + " -> " + destino + ", " + distancia + " km) registrada.");
                JOptionPane.showMessageDialog(this, "Ruta registrada con éxito.", "Gestión de Rutas", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            mostrarErrorRuta("La distancia debe ser numérica.");
        }
    }

    private void accionListaBuscarRuta() {
        String id = txtListaRutaId.getText().trim();
        if (id.isEmpty()) {
            mostrarErrorRuta("Ingrese el ID de ruta a buscar.");
            return;
        }
        Ruta encontrada = listaRutas.buscarRuta(id);
        if (encontrada == null) {
            mostrarErrorRuta("No se encontró la ruta con ID " + id);
            return;
        }
        cbListaRutaOrigen.setSelectedItem(encontrada.obtenerOrigen());
        cbListaRutaDestino.setSelectedItem(encontrada.obtenerDestino());
        txtListaRutaDistancia.setText(String.valueOf(encontrada.obtenerDistanciaKm()));
        txtResultadoRutas.setText("Ruta localizada en Lista Doble:\n" + encontrada.toString());
        log("[RUTAS] Búsqueda en Lista Doble: Ruta " + id + " encontrada.");
    }

    private void accionListaEliminarRuta() {
        if (!verificarPermisoAdmin("Eliminar Ruta")) return;
        String id = txtListaRutaId.getText().trim();
        if (id.isEmpty()) {
            mostrarErrorRuta("Ingrese el ID de ruta a eliminar.");
            return;
        }
        if (controlador.eliminarRuta(id)) {
            actualizarContadorRutas();
            cursorRutaNavegacion = null;
            actualizarNavegadorRutas();
            actualizarMetricasDashboard();
            actualizarMetricasSQLTab();
            txtResultadoRutas.setText("Ruta " + id + " eliminada con reconexión de enlaces anterior y siguiente.");
            log("[RUTAS] Ruta " + id + " eliminada de Lista Doble y MySQL.");
            JOptionPane.showMessageDialog(this, "Ruta eliminada con éxito.", "Gestión de Rutas", JOptionPane.INFORMATION_MESSAGE);
        } else {
            mostrarErrorRuta("No se encontró la ruta con ID " + id);
        }
    }

    private void accionListaActualizarRuta() {
        if (!verificarPermisoAdmin("Actualizar Ruta")) return;
        String id = txtListaRutaId.getText().trim();
        String origen = (String) cbListaRutaOrigen.getSelectedItem();
        String destino = (String) cbListaRutaDestino.getSelectedItem();
        String strDist = txtListaRutaDistancia.getText().trim();

        if (id.isEmpty() || origen == null || destino == null || strDist.isEmpty()) {
            mostrarErrorRuta("Complete todos los campos para actualizar la ruta.");
            return;
        }
        try {
            double dist = Double.parseDouble(strDist.replace(',', '.'));
            Ruta rNueva = new Ruta(id, origen, destino, dist);
            if (controlador.actualizarRuta(id, rNueva)) {
                actualizarContadorRutas();
                actualizarNavegadorRutas();
                actualizarMetricasDashboard();
                actualizarMetricasSQLTab();
                txtResultadoRutas.setText("Ruta actualizada en Lista Doble y MySQL:\n" + rNueva.toString());
                log("[RUTAS] Ruta " + id + " actualizada.");
                JOptionPane.showMessageDialog(this, "Ruta actualizada correctamente.", "Gestión de Rutas", JOptionPane.INFORMATION_MESSAGE);
            } else {
                mostrarErrorRuta("No se encontró la ruta con ID " + id);
            }
        } catch (NumberFormatException e) {
            mostrarErrorRuta("La distancia debe ser numérica.");
        }
    }

    private void accionListaRecorrerRutasAdelante() {
        String rec = listaRutas.recorrerAdelante();
        txtResultadoRutas.setText("=== RECORRIDO HACIA ADELANTE (CABEZA -> COLA) ===\n\n" + rec);
        log("[RUTAS] Recorrido de rutas hacia adelante en Lista Doble.");
    }

    private void accionListaRecorrerRutasAtras() {
        String rec = listaRutas.recorrerAtras();
        txtResultadoRutas.setText("=== RECORRIDO HACIA ATRÁS (COLA -> CABEZA) ===\n\n" + rec);
        log("[RUTAS] Recorrido de rutas hacia atrás utilizando puntero anterior.");
    }

    private void accionNavegarRutaAnterior() {
        if (cursorRutaNavegacion != null && cursorRutaNavegacion.anterior != null) {
            cursorRutaNavegacion = cursorRutaNavegacion.anterior;
            actualizarNavegadorRutas();
            log("[RUTAS] Navegador: Retroceso a ruta anterior (" + cursorRutaNavegacion.dato.obtenerId() + ").");
        } else {
            JOptionPane.showMessageDialog(this, "Ya se encuentra en el primer nodo (cabeza de la lista).", "Inicio de Lista", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void accionNavegarRutaSiguiente() {
        if (cursorRutaNavegacion != null && cursorRutaNavegacion.siguiente != null) {
            cursorRutaNavegacion = cursorRutaNavegacion.siguiente;
            actualizarNavegadorRutas();
            log("[RUTAS] Navegador: Avance a ruta siguiente (" + cursorRutaNavegacion.dato.obtenerId() + ").");
        } else {
            JOptionPane.showMessageDialog(this, "Ya se encuentra en el último nodo (cola de la lista).", "Fin de Lista", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void accionNavegarRutaPrimero() {
        if (listaRutas != null && !listaRutas.estaVacia()) {
            cursorRutaNavegacion = listaRutas.obtenerCabeza();
            actualizarNavegadorRutas();
            mostrarToast("Navegación: Salto a la Cabeza de la Lista Doble", Theme.getInfo());
            log("[RUTAS] Navegador: Salto directo al primer nodo / Cabeza (" + cursorRutaNavegacion.dato.obtenerId() + ").");
        } else {
            JOptionPane.showMessageDialog(this, "La lista de rutas está vacía. Use 'Cargar Rutas Troncales (Demo)' para poblarla.", "Lista Vacía", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void accionNavegarRutaUltimo() {
        if (listaRutas != null && !listaRutas.estaVacia()) {
            cursorRutaNavegacion = listaRutas.obtenerCola();
            actualizarNavegadorRutas();
            mostrarToast("Navegación: Salto a la Cola de la Lista Doble", Theme.getInfo());
            log("[RUTAS] Navegador: Salto directo al último nodo / Cola (" + cursorRutaNavegacion.dato.obtenerId() + ").");
        } else {
            JOptionPane.showMessageDialog(this, "La lista de rutas está vacía. Use 'Cargar Rutas Troncales (Demo)' para poblarla.", "Lista Vacía", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void accionCargarRutasTroncalesDemo() {
        if (listaRutas == null) {
            listaRutas = new ListaDoble();
        }
        if (listaRutas.estaVacia()) {
            listaRutas.agregarRuta(new Ruta("RT301", "Lima", "Cusco", 580.0));
            listaRutas.agregarRuta(new Ruta("RT302", "Lima", "Arequipa", 760.0));
            listaRutas.agregarRuta(new Ruta("RT303", "Lima", "Iquitos", 1010.0));
            listaRutas.agregarRuta(new Ruta("RT304", "Lima", "Piura", 850.0));
            listaRutas.agregarRuta(new Ruta("RT305", "Lima", "Tarapoto", 620.0));
            listaRutas.agregarRuta(new Ruta("RT306", "Lima", "Trujillo", 490.0));
        }
        cursorRutaNavegacion = listaRutas.obtenerCabeza();
        actualizarContadorRutas();
        actualizarNavegadorRutas();
        if (txtResultadoRutas != null) {
            txtResultadoRutas.setText(listaRutas.recorrerAdelante());
        }
        mostrarToast("6 Rutas Troncales cargadas en Lista Doblemente Enlazada", Theme.getSuccess());
    }

    private void actualizarNavegadorRutas() {
        if (listaRutas == null || listaRutas.estaVacia()) {
            cursorRutaNavegacion = null;
            if (lblRutaIataOrigen != null) lblRutaIataOrigen.setText("---");
            if (lblRutaIataDestino != null) lblRutaIataDestino.setText("---");
            if (lblRutaDistanciaKm != null) lblRutaDistanciaKm.setText("Distancia: 0 km");
            if (lblRutaPunteros != null) lblRutaPunteros.setText("[ null ] <==== [ Lista Vacia ] ====> [ null ]");
            return;
        }
        if (cursorRutaNavegacion == null) {
            cursorRutaNavegacion = listaRutas.obtenerCabeza();
        }
        if (cursorRutaNavegacion != null && cursorRutaNavegacion.dato != null) {
            Ruta r = cursorRutaNavegacion.dato;
            if (lblRutaIataOrigen != null) lblRutaIataOrigen.setText(r.obtenerOrigen().toUpperCase());
            if (lblRutaIataDestino != null) lblRutaIataDestino.setText(r.obtenerDestino().toUpperCase());
            if (lblRutaDistanciaKm != null) {
                int dur = (int)(r.obtenerDistanciaKm() / 7.5);
                lblRutaDistanciaKm.setText(String.format("ID: %s | Distancia: %.1f km | Tiempo estimado: %d min", r.obtenerId(), r.obtenerDistanciaKm(), dur));
            }
            String ant = (cursorRutaNavegacion.anterior != null && cursorRutaNavegacion.anterior.dato != null) ? cursorRutaNavegacion.anterior.dato.obtenerId() : "null";
            String sig = (cursorRutaNavegacion.siguiente != null && cursorRutaNavegacion.siguiente.dato != null) ? cursorRutaNavegacion.siguiente.dato.obtenerId() : "null";
            if (lblRutaPunteros != null) {
                lblRutaPunteros.setText("[ Ant: " + ant + " ] <==== [ NODO ACTUAL: " + r.obtenerId() + " ] ====> [ Sig: " + sig + " ]");
            }
        } else {
            if (lblRutaPunteros != null) lblRutaPunteros.setText("[ null ] <==== [ Lista Vacia ] ====> [ null ]");
        }
    }

    // Eventos de Puertas de Embarque (Lista Circular)
    private void accionAvanzarPuertaCircular() {
        PuertaEmbarque siguiente = listaPuertas.avanzarSiguiente();
        actualizarVistaPuertas();
        actualizarMetricasDashboard();
        if (siguiente != null) {
            log("[PUERTAS] Lista Circular: Turno avanzado continuamente a " + siguiente.obtenerNumeroPuerta());
        }
    }

    private void accionAsignarVueloAPuerta() {
        String vuelo = (String) cbVueloAsignarPuerta.getSelectedItem();
        if (vuelo == null || vuelo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un vuelo.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (controlador.asignarVueloAPuerta(vuelo)) {
            actualizarVistaPuertas();
            actualizarMetricasDashboard();
            actualizarMetricasSQLTab();
            log("[PUERTAS] Lista Circular: Asignado vuelo " + vuelo + " a la puerta activa en turno.");
            JOptionPane.showMessageDialog(this, "Vuelo " + vuelo + " asignado a la puerta de embarque activa.", "Asignación Circular", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void actualizarVistaPuertas() {
        boolean vacia = (listaPuertas == null || listaPuertas.estaVacia());
        if (vacia) {
            if (lblPuertaActualInfo != null) {
                lblPuertaActualInfo.setText("Sin puertas cargadas. Presione 'Cargar 5 Puertas Estándar'");
                lblPuertaActualInfo.setForeground(new Color(245, 158, 11));
            }
            if (btnAvanzarTurnoPuerta != null) btnAvanzarTurnoPuerta.setEnabled(false);
            if (btnAsignarVueloPuerta != null) btnAsignarVueloPuerta.setEnabled(false);
            if (txtResultadoPuertas != null) {
                txtResultadoPuertas.setText("=== CICLO ROTATIVO DE PUERTAS DE EMBARQUE - LISTA CIRCULAR ===\n\n" +
                        "La lista circular se encuentra vacía. Cargue las 5 puertas estándar para iniciar el carrusel de asignaciones.");
            }
        } else {
            PuertaEmbarque actual = listaPuertas.obtenerPuertaActual();
            if (actual != null && lblPuertaActualInfo != null) {
                lblPuertaActualInfo.setText("Puerta Activa en Turno: " + actual.obtenerNumeroPuerta() + " · " + actual.obtenerTerminal() + " | Estado: " + actual.obtenerEstado() + " | Vuelo: " + actual.obtenerCodigoVuelo());
                lblPuertaActualInfo.setForeground(verdeLibre);
            }
            if (btnAvanzarTurnoPuerta != null) btnAvanzarTurnoPuerta.setEnabled(true);
            if (btnAsignarVueloPuerta != null) btnAsignarVueloPuerta.setEnabled(true);
            if (txtResultadoPuertas != null) {
                txtResultadoPuertas.setText("=== CICLO ROTATIVO DE PUERTAS DE EMBARQUE - LISTA CIRCULAR ===\n\n" +
                        listaPuertas.listarPuertas());
            }
        }
        actualizarTarjetasVisualesGates();
        if (panelRadarSmartAirport != null) {
            panelRadarSmartAirport.refrescarDatos();
        }
    }

    public void actualizarVistaPuertasExterna() {
        actualizarVistaPuertas();
        actualizarMetricasDashboard();
        actualizarMetricasSQLTab();
    }

    // Sincronizacion y Base de Datos (Asíncrona con SwingWorker para no bloquear la interfaz)
    private void accionSincronizarMySQL() {
        btnSincronizarTodo.setEnabled(false);
        btnSincronizarTodo.setText("Sincronizando...");
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                com.aeropuerto.util.ConexionBD.invalidarCache();
                return controlador.sincronizarConBD();
            }

            @Override
            protected void done() {
                try {
                    String resultado = get();
                    log("[MYSQL] " + resultado);
                    actualizarEstadoMySQL();
                    actualizarMetricasSQLTab();
                    JOptionPane.showMessageDialog(VentanaPrincipal.this, resultado, "Sincronización MySQL", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    log("[MYSQL ERROR] " + ex.getMessage());
                } finally {
                    btnSincronizarTodo.setEnabled(true);
                    btnSincronizarTodo.setText("Sincronizar Todo a MySQL");
                }
            }
        };
        worker.execute();
    }

    private void accionCargarDesdeMySQL() {
        btnRecargarTodo.setEnabled(false);
        btnRecargarTodo.setText("Cargando...");
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                com.aeropuerto.util.ConexionBD.invalidarCache();
                controlador.cargarDesdeBD();
                return null;
            }

            @Override
            protected void done() {
                try {
                    listaEquipaje = controlador.obtenerListaEquipaje();
                    listaRutas = controlador.obtenerListaRutas();
                    actualizarTablaVuelos();
                    actualizarMatrizCabina();
                    actualizarContadorMaletas();
                    actualizarContadorRutas();
                    actualizarNavegadorRutas();
                    actualizarMetricasDashboard();
                    actualizarEstadoMySQL();
                    if (panelSupervisoresAeropuertos != null) {
                        panelSupervisoresAeropuertos.actualizarTablas();
                    }
                    if (panelRadarSmartAirport != null) {
                        panelRadarSmartAirport.refrescarDatos();
                    }
                    log("[MYSQL] Datos sincronizados y recargados desde MySQL en tiempo real.");
                    JOptionPane.showMessageDialog(VentanaPrincipal.this, "Datos cargados desde MySQL con éxito.", "Lectura de MySQL", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    log("[MYSQL ERROR] " + ex.getMessage());
                } finally {
                    btnRecargarTodo.setEnabled(true);
                    btnRecargarTodo.setText("Recargar Todo desde MySQL");
                }
            }
        };
        worker.execute();
    }

    private void actualizarEstadoMySQL() {
        boolean conectado = ConexionBD.estaConectado();
        if (lblEstadoBD != null) {
            if (conectado) {
                lblEstadoBD.setText("MySQL: CONECTADO - Tiempo Real");
                lblEstadoBD.setForeground(verdeLibre);
            } else {
                lblEstadoBD.setText("Modo Local - Memoria RAM");
                lblEstadoBD.setForeground(doradoAlerta);
            }
        }
        if (barraEstadoFooter != null) {
            barraEstadoFooter.actualizarEstadoMySQL(conectado ? "Conectado a MySQL - Puerto 3306" : "Modo Local - Memoria RAM", conectado);
        }
    }

    // Actualizaciones visuales
    private void actualizarTablaVuelos() {
        filtrarTablaVuelos();
    }

    private void filtrarTablaVuelos() {
        modeloTablaVuelos.setRowCount(0);
        Vuelo[] arr = controlador.obtenerVuelos();
        int total = controlador.obtenerContadorVuelos();
        String filtro = (txtFiltroVuelos != null) ? txtFiltroVuelos.getText().trim().toLowerCase() : "";

        for (int i = 0; i < total; i++) {
            Vuelo v = arr[i];
            if (v != null) {
                String ruta = v.obtenerOrigen() + " -> " + v.obtenerDestino();
                boolean coincide = filtro.isEmpty() ||
                        v.obtenerCodigo().toLowerCase().contains(filtro) ||
                        v.obtenerOrigen().toLowerCase().contains(filtro) ||
                        v.obtenerDestino().toLowerCase().contains(filtro) ||
                        v.obtenerClaseDisponible().toLowerCase().contains(filtro);

                if (coincide) {
                    String estadoDefecto = (i == 0) ? "En Vuelo" : (i == 1) ? "Abordando" : "A Tiempo";
                    modeloTablaVuelos.addRow(new Object[]{
                            "[" + i + "]",
                            v.obtenerCodigo(),
                            ruta,
                            v.obtenerFecha(),
                            v.obtenerHoraSalida(),
                            v.obtenerHoraLlegada(),
                            "S/. " + String.format(java.util.Locale.US, "%.2f", v.obtenerPrecioBase()),
                            v.obtenerClaseDisponible(),
                            v.contarAsientosLibres() + " / 50",
                            estadoDefecto
                    });
                }
            }
        }
        lblContadorArreglo.setText("Vuelos registrados: " + total);
        actualizarTablaVuelosPasajero();
    }

    private void actualizarMatrizCabina() {
        if (controlador.obtenerContadorVuelos() == 0 || controlador.obtenerVueloActivo() == null) {
            for (int f = 0; f < 10; f++) {
                for (int c = 0; c < 5; c++) {
                    JButton btn = botonesAsientos[f][c];
                    char letra = (char) ('A' + c);
                    if (Theme.esModoOscuro()) {
                        btn.setBackground(new Color(20, 28, 42));
                        btn.setForeground(textoGris);
                        btn.setBorder(new LineBorder(bordeTarjeta, 1));
                    } else {
                        btn.setBackground(Theme.getElevated());
                        btn.setForeground(Theme.getSecundario());
                        btn.setBorder(new LineBorder(Theme.getBorder(), 1));
                    }
                    btn.setText((f + 1) + "" + letra);
                    btn.setToolTipText("Registre o seleccione un vuelo para asignar asientos.");
                }
            }
            if (lblResumenOcupacion != null) {
                lblResumenOcupacion.setText("Total: 0 asientos | Ingrese un vuelo para gestionar la cabina.");
            }
            actualizarCabinaPasajero();
            return;
        }

        int[][] mat = controlador.obtenerMatrizAsientos();
        String[][] pas = controlador.obtenerMatrizPasajeros();
        int ocupados = 0;

        for (int f = 0; f < 10; f++) {
            for (int c = 0; c < 5; c++) {
                JButton btn = botonesAsientos[f][c];
                char letra = (char) ('A' + c);
                String tag = (c == 0 || c == 4) ? "V" : (c == 1 || c == 3) ? "P" : "C";
                String txt = (f + 1) + "" + letra + " [" + tag + "]";

                if (mat[f][c] == 1) {
                    ocupados++;
                    if (Theme.esModoOscuro()) {
                        btn.setBackground(new Color(153, 27, 27)); // Rojo oscuro alerta
                        btn.setForeground(Color.WHITE);
                        btn.setBorder(new LineBorder(rojoOcupado, 1));
                    } else {
                        btn.setBackground(new Color(254, 226, 226)); // Rojo suave pastel
                        btn.setForeground(new Color(185, 28, 28)); // Rojo oscuro
                        btn.setBorder(new LineBorder(new Color(239, 68, 68), 1));
                    }
                    String dni = (pas != null && pas[f][c] != null) ? pas[f][c] : "OCUP";
                    btn.setText(txt);
                    btn.setToolTipText("Fila " + f + ", Col " + c + " | Ocupado por DNI: " + dni);
                } else {
                    if (Theme.esModoOscuro()) {
                        btn.setBackground(new Color(16, 42, 34)); // Esmeralda oscuro elegante
                        btn.setForeground(new Color(52, 211, 153)); // Verde menta brillante
                        btn.setBorder(new LineBorder(new Color(24, 68, 52), 1));
                    } else {
                        btn.setBackground(new Color(220, 252, 231)); // Verde suave pastel
                        btn.setForeground(new Color(4, 120, 87)); // Verde bosque
                        btn.setBorder(new LineBorder(new Color(16, 185, 129), 1));
                    }
                    btn.setText(txt);
                    btn.setToolTipText("Fila " + f + ", Col " + c + " | Disponible");
                }
            }
        }

        int libres = 50 - ocupados;
        if (lblResumenOcupacion != null) {
            lblResumenOcupacion.setText(String.format("Total: 50 | Libres: %d (%d%%) | Ocupados: %d (%d%%)",
                    libres, (libres * 100) / 50, ocupados, (ocupados * 100) / 50));
        }
        actualizarCabinaPasajero();
    }

    private void actualizarEtiquetaVueloActivo() {
        Vuelo v = controlador.obtenerVueloActivo();
        if (lblVueloActivoCabina != null) {
            if (v != null) {
                lblVueloActivoCabina.setText("Vuelo Activo: " + v.obtenerCodigo() + " (" + v.obtenerOrigen() + " -> " + v.obtenerDestino() + ") | Base: S/. " + v.obtenerPrecioBase());
            } else {
                lblVueloActivoCabina.setText("Sin vuelo activo. Ingrese un vuelo en el catálogo para comenzar.");
            }
        }
        actualizarBannerVueloSeleccionado();
    }

    private void actualizarCombosVuelos() {
        if (cbListaEquipajeVuelo == null || cbVueloAsignarPuerta == null) return;
        cbListaEquipajeVuelo.removeAllItems();
        cbVueloAsignarPuerta.removeAllItems();
        Vuelo[] vuelos = controlador.obtenerVuelos();
        int total = controlador.obtenerContadorVuelos();
        for (int i = 0; i < total; i++) {
            if (vuelos[i] != null) {
                cbListaEquipajeVuelo.addItem(vuelos[i].obtenerCodigo());
                cbVueloAsignarPuerta.addItem(vuelos[i].obtenerCodigo());
            }
        }
    }

    private void actualizarContadorMaletas() {
        if (lblTamanoMaletas != null && listaEquipaje != null) {
            lblTamanoMaletas.setText("Maletas: " + listaEquipaje.obtenerTamano());
        }
        if (txtResultadoEquipaje != null && listaEquipaje != null) {
            if (listaEquipaje.estaVacia()) {
                txtResultadoEquipaje.setText("=== LISTA ENLAZADA SIMPLE - EQUIPAJE ===\n\nLista Simple vacía - 0 nodos.\nIngrese los datos en el formulario para registrar la primera maleta.");
            }
        }
    }

    private void actualizarContadorRutas() {
        if (lblTamanoRutas != null && listaRutas != null) {
            lblTamanoRutas.setText("Rutas: " + listaRutas.obtenerTamano());
        }
        if (txtResultadoRutas != null && listaRutas != null) {
            if (listaRutas.estaVacia()) {
                txtResultadoRutas.setText("=== LISTA DOBLEMENTE ENLAZADA - RUTAS ===\n\nLista Doble vacía - 0 nodos.\nAgregue una ruta comercial usando el formulario superior.");
            }
        }
    }

    private void mostrarErrorEquipaje(String mensaje) {
        txtResultadoEquipaje.setText(mensaje);
        log("[EQUIPAJE] " + mensaje);
        JOptionPane.showMessageDialog(this, mensaje, "Validación", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarErrorRuta(String mensaje) {
        txtResultadoRutas.setText(mensaje);
        log("[RUTAS] " + mensaje);
        JOptionPane.showMessageDialog(this, mensaje, "Validación", JOptionPane.WARNING_MESSAGE);
    }

    private void generarIdEquipaje() {
        int num = 100 + (int)(Math.random() * 899);
        String idGen = "EQ" + num;
        while (listaEquipaje.existeMaleta(idGen)) {
            num++;
            idGen = "EQ" + num;
        }
        txtListaEquipajeId.setText(idGen);
    }

    private void generarIdRuta() {
        int num = 300 + (int)(Math.random() * 699);
        String idGen = "RT" + num;
        while (listaRutas.existeRuta(idGen)) {
            num++;
            idGen = "RT" + num;
        }
        txtListaRutaId.setText(idGen);
    }

    private void generarCodigoVuelo() {
        int correlativo = 2020 + controlador.obtenerContadorVuelos() + (int) (Math.random() * 60);
        String cod = "CP-" + correlativo;
        while (controlador.buscarVueloPorCodigo(cod) != null) {
            correlativo++;
            cod = "CP-" + correlativo;
        }
        txtCodigo.setText(cod);
    }

    private void autocompletarFormularioVueloAdmin() {
        generarCodigoVuelo();
        cbOrigen.setSelectedItem("Lima");
        String[] posiblesDestinos = {"Arequipa", "Cusco", "Piura", "Iquitos", "Tarapoto", "Trujillo"};
        int idx = (int) (Math.random() * posiblesDestinos.length);
        cbDestino.setSelectedItem(posiblesDestinos[idx]);
        spinnerFecha.setValue(new Date());
        cbHoraSalida.setSelectedItem("08:30");
        txtPrecioBase.setText("145.00");
        cbClase.setSelectedItem("Turista");
        recalcularHorarioYTarifa();
        mostrarToast("Formulario autocompletado para registro rapido de vuelo", Theme.getAdminAccent());
    }

    private void autocompletarEquipajeAdmin() {
        generarIdEquipaje();
        txtListaEquipajeDni.setText("72104589");
        txtListaEquipajePeso.setText("16.5");
        cbListaEquipajeTipo.setSelectedItem("Bodega");
        mostrarToast("Datos de maleta autocompletados para registro rapido", Theme.getInfo());
    }

    private void autocompletarRutaAdmin() {
        generarIdRuta();
        cbListaRutaOrigen.setSelectedItem("Lima");
        cbListaRutaDestino.setSelectedItem("Arequipa");
        txtListaRutaDistancia.setText("760.0");
        mostrarToast("Datos de ruta comercial autocompletados para registro rapido", Theme.getInfo());
    }

    private void cargarVueloEnFormulario(Vuelo vuelo) {
        if (vuelo == null) return;
        txtCodigo.setText(vuelo.obtenerCodigo());
        cbOrigen.setSelectedItem(vuelo.obtenerOrigen());
        cbDestino.setSelectedItem(vuelo.obtenerDestino());
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            spinnerFecha.setValue(f.parse(vuelo.obtenerFecha()));
        } catch (Exception ex) {}
        cbHoraSalida.setSelectedItem(vuelo.obtenerHoraSalida());
        txtHoraLlegada.setText(vuelo.obtenerHoraLlegada());
        txtDuracion.setText(String.valueOf(vuelo.obtenerDuracion()));
        txtPrecioBase.setText(String.format(java.util.Locale.US, "%.2f", vuelo.obtenerPrecioBase()));
        cbClase.setSelectedItem(vuelo.obtenerClaseDisponible());
    }

    private void limpiarCamposVuelo() {
        generarCodigoVuelo();
        cbOrigen.setSelectedItem("Lima");
        cbDestino.setSelectedItem("Trujillo");
        spinnerFecha.setValue(new Date());
        cbHoraSalida.setSelectedItem("07:30");
        cbClase.setSelectedItem("Turista");
        recalcularHorarioYTarifa();
        tablaVuelos.clearSelection();
    }

    private void recalcularHorarioYTarifa() {
        if (cbOrigen == null || cbDestino == null || cbHoraSalida == null) return;
        String origen = (String) cbOrigen.getSelectedItem();
        String destino = (String) cbDestino.getSelectedItem();
        if (origen == null || destino == null) return;

        int minutos = calcularDuracionRuta(origen, destino);
        if (txtDuracion != null) txtDuracion.setText(String.valueOf(minutos));

        String salida = (String) cbHoraSalida.getSelectedItem();
        if (salida != null && txtHoraLlegada != null) {
            txtHoraLlegada.setText(calcularHoraLlegada(salida.trim(), minutos));
        }

        if (txtPrecioBase != null && cbClase != null) {
            String clase = (String) cbClase.getSelectedItem();
            double tarifa = calcularTarifaSugerida(origen, destino, clase);
            txtPrecioBase.setText(String.format(java.util.Locale.US, "%.2f", tarifa));
        }
    }

    private int calcularDuracionRuta(String origen, String destino) {
        if (origen.equalsIgnoreCase(destino)) return 0;
        String ruta = (origen + "-" + destino).toUpperCase();
        if (ruta.contains("LIMA") && ruta.contains("TRUJILLO")) return 70;
        if (ruta.contains("LIMA") && ruta.contains("CHICLAYO")) return 75;
        if (ruta.contains("LIMA") && ruta.contains("CUSCO")) return 80;
        if (ruta.contains("LIMA") && ruta.contains("AREQUIPA")) return 85;
        if (ruta.contains("LIMA") && ruta.contains("PIURA")) return 95;
        if (ruta.contains("LIMA") && ruta.contains("TARAPOTO")) return 75;
        if (ruta.contains("LIMA") && ruta.contains("IQUITOS")) return 105;
        if (ruta.contains("CUSCO") && ruta.contains("AREQUIPA")) return 50;
        return 80;
    }

    private String calcularHoraLlegada(String horaSalida, int duracionMinutos) {
        try {
            String[] partes = horaSalida.split(":");
            if (partes.length >= 2) {
                int h = Integer.parseInt(partes[0].trim());
                int m = Integer.parseInt(partes[1].trim());
                int tot = (h * 60 + m + duracionMinutos) % (24 * 60);
                if (tot < 0) tot += 24 * 60;
                return String.format("%02d:%02d", tot / 60, tot % 60);
            }
        } catch (Exception e) {}
        return horaSalida;
    }

    private double calcularTarifaSugerida(String origen, String destino, String clase) {
        double base = 130.0;
        String r = (origen + "-" + destino).toUpperCase();
        if (r.contains("TRUJILLO")) base = 120.0;
        else if (r.contains("CHICLAYO")) base = 125.0;
        else if (r.contains("CUSCO")) base = 150.0;
        else if (r.contains("AREQUIPA")) base = 145.0;
        else if (r.contains("PIURA")) base = 160.0;
        else if (r.contains("TARAPOTO")) base = 140.0;
        else if (r.contains("IQUITOS")) base = 180.0;

        double factor = 1.0;
        if ("Ejecutiva".equalsIgnoreCase(clase)) factor = 1.5;
        else if ("Primera".equalsIgnoreCase(clase)) factor = 2.0;

        return base * factor;
    }

    public void accionSincronizarMySQLPublica() {
        accionSincronizarMySQL();
    }

    public void accionCargarDesdeMySQLPublica() {
        accionCargarDesdeMySQL();
    }

    private void log(String mensaje) {
        if (panelRadarSmartAirport != null && panelRadarSmartAirport.getPanelTelemetriaConsola() != null) {
            panelRadarSmartAirport.getPanelTelemetriaConsola().log(mensaje);
        }
        if (txtConsola != null) {
            try {
                txtConsola.append("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + mensaje + "\n");
                txtConsola.setCaretPosition(txtConsola.getDocument().getLength());
            } catch (Exception ignored) {}
        }
    }

    private void iniciarReloj() {
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy · HH:mm:ss");
        Timer timer = new Timer(1000, e -> lblReloj.setText(f.format(new Date())));
        timer.start();
    }

    // HUMANIZACION UI - Sincronización dinámica de tema Claro / Oscuro en toda la aplicación
    public void actualizarTemaGlobal() {
        refrescarPaletaLocal();

        if (panelRaiz != null) {
            panelRaiz.setBackground(Theme.getBg());
        }
        if (panelNorte != null) {
            panelNorte.setBackground(Theme.getBg());
        }
        if (panelEncabezado != null) {
            panelEncabezado.setBackground(Theme.getSurface());
            panelEncabezado.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.getBorder()),
                    BorderFactory.createEmptyBorder(4, 14, 4, 14)));
        }
        if (btnAlternarTemaHeader != null) {
            btnAlternarTemaHeader.setText(Theme.esModoOscuro() ? "Modo Claro" : "Modo Oscuro");
            btnAlternarTemaHeader.setBackground(Theme.getElevated());
            btnAlternarTemaHeader.setForeground(Theme.getTexto());
            btnAlternarTemaHeader.setIcon(AppIcon.of(Theme.esModoOscuro() ? "sun" : "moon", 12, Theme.getTexto()));
            btnAlternarTemaHeader.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getBorder(), 1),
                    BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        }
        if (btnToggleVistaTecnica != null) {
            btnToggleVistaTecnica.setBackground(Theme.getElevated());
            btnToggleVistaTecnica.setForeground(vistaTecnicaActiva ? Theme.getPasajeroAccent() : Theme.getTexto());
            btnToggleVistaTecnica.setIcon(AppIcon.of("settings", 12, vistaTecnicaActiva ? Theme.getPasajeroAccent() : Theme.getTexto()));
            btnToggleVistaTecnica.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getBorder(), 1),
                    BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        }

        if (btnToggleTablaDetallada != null) {
            btnToggleTablaDetallada.setBackground(Theme.getElevated());
            btnToggleTablaDetallada.setForeground(Theme.getPasajeroAccent());
            btnToggleTablaDetallada.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getBorder(), 1),
                    BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        }
        if (lblTituloPrincipalHeader != null) {
            lblTituloPrincipalHeader.setForeground(Theme.getTexto());
        }
        if (lblSubtituloHeader != null) {
            lblSubtituloHeader.setForeground(Theme.getSecundario());
        }
        if (lblEstadoSesion != null) {
            lblEstadoSesion.setBackground(Theme.getInput());
            lblEstadoSesion.setForeground(Theme.getTexto());
            lblEstadoSesion.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getBorder(), 1),
                    BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        }
        if (lblReloj != null) {
            lblReloj.setForeground(Theme.getTexto());
        }

        if (panelPestanas != null) {
            panelPestanas.setBackground(Theme.getBg());
            panelPestanas.setForeground(Theme.getTexto());
        }

        // Pasajero - Pestaña 1 Vuelos
        if (panelPasajeroVuelos != null) {
            panelPasajeroVuelos.setBackground(Theme.getBg());
        }
        if (lblTitPasajeroVuelos != null) lblTitPasajeroVuelos.setForeground(Theme.getTexto());
        if (lblSubPasajeroVuelos != null) lblSubPasajeroVuelos.setForeground(Theme.getSecundario());
        if (pFiltrosPasajero != null) {
            pFiltrosPasajero.setBackground(Theme.getSurface());
            pFiltrosPasajero.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getBorder(), 1),
                    BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        }
        if (lblFiltroDestPasajero != null) lblFiltroDestPasajero.setForeground(Theme.getSecundario());
        if (lblFiltroTurnoPasajero != null) lblFiltroTurnoPasajero.setForeground(Theme.getSecundario());
        if (lblFiltroClasePasajero != null) lblFiltroClasePasajero.setForeground(Theme.getSecundario());
        if (scrollTablaPasajero != null) {
            scrollTablaPasajero.getViewport().setBackground(Theme.getSurface());
            scrollTablaPasajero.setBorder(BorderFactory.createLineBorder(Theme.getBorder(), 1));
        }

        // Pasajero - Pestaña 2 Cabina
        if (panelPasajeroCabinaContenedor != null) {
            panelPasajeroCabinaContenedor.setBackground(Theme.getBg());
        }
        if (panelCabinaPasajeroRaiz != null) {
            panelCabinaPasajeroRaiz.setBackground(Theme.getBg());
        }
        if (pEncabezadoCabinaPasajero != null) {
            pEncabezadoCabinaPasajero.setBackground(Theme.getSurface());
            pEncabezadoCabinaPasajero.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getBorder(), 1),
                    BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        }
        if (lblCabinaVueloActivoInfo != null) lblCabinaVueloActivoInfo.setForeground(Theme.getTexto());
        if (lblCabinaResumenLibres != null) lblCabinaResumenLibres.setForeground(Theme.getSecundario());
        if (pFuselajeContenedor != null) {
            pFuselajeContenedor.setBackground(Theme.getInput());
            pFuselajeContenedor.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getBorder(), 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        }
        if (scrollFuselaje != null) {
            scrollFuselaje.getViewport().setBackground(Theme.getInput());
            scrollFuselaje.setBorder(null);
        }
        if (pLateralCabinaPasajero != null) {
            pLateralCabinaPasajero.setBackground(Theme.getSurface());
            pLateralCabinaPasajero.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getBorder(), 1),
                    BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        }
        if (lblTitLatCabina != null) lblTitLatCabina.setForeground(Theme.getTexto());
        if (lblCockpitCabina != null) {
            lblCockpitCabina.setBackground(Theme.getElevated());
            lblCockpitCabina.setForeground(Theme.getTexto());
            lblCockpitCabina.setBorder(BorderFactory.createLineBorder(Theme.getBorder(), 1));
        }
        if (lblVipBannerCabina != null) {
            lblVipBannerCabina.setBackground(Theme.getWarningSoft());
            lblVipBannerCabina.setForeground(Theme.getWarning());
            lblVipBannerCabina.setBorder(BorderFactory.createLineBorder(Theme.getWarning(), 1));
        }
        if (lblTuristaBannerCabina != null) {
            lblTuristaBannerCabina.setBackground(Theme.getSuccessSoft());
            lblTuristaBannerCabina.setForeground(Theme.getSuccess());
            lblTuristaBannerCabina.setBorder(BorderFactory.createLineBorder(Theme.getSuccess(), 1));
        }
        if (btnVolverVuelosCabina != null) {
            btnVolverVuelosCabina.setBackground(Theme.getElevated());
            btnVolverVuelosCabina.setForeground(Theme.getTexto());
            btnVolverVuelosCabina.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getBorder(), 1),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        }

        // Pasajero - Pestaña 3 Equipaje
        if (panelPasajeroEquipajeContenedor != null) {
            panelPasajeroEquipajeContenedor.setBackground(Theme.getBg());
        }
        if (lblTitEquipajePasajero != null) lblTitEquipajePasajero.setForeground(Theme.getTexto());
        if (lblSubEquipajePasajero != null) lblSubEquipajePasajero.setForeground(Theme.getSecundario());
        if (pFormularioEquipajePasajero != null) {
            pFormularioEquipajePasajero.setBackground(Theme.getSurface());
            pFormularioEquipajePasajero.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getBorder(), 1),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        }
        if (scrollTablaEquipajePasajero != null) {
            scrollTablaEquipajePasajero.getViewport().setBackground(Theme.getSurface());
            scrollTablaEquipajePasajero.setBorder(BorderFactory.createLineBorder(Theme.getBorder(), 1));
        }
        if (btnVolverCabinaEquipaje != null) {
            btnVolverCabinaEquipaje.setBackground(Theme.getElevated());
            btnVolverCabinaEquipaje.setForeground(Theme.getTexto());
            btnVolverCabinaEquipaje.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getBorder(), 1),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        }

        // Pasajero - Pestaña 4 Pase de Abordar
        if (panelPasajeroPaseAbordar != null) {
            panelPasajeroPaseAbordar.setBackground(Theme.getBg());
        }
        if (lblTitPasePasajero != null) lblTitPasePasajero.setForeground(Theme.getTexto());
        if (lblSubPasePasajero != null) lblSubPasePasajero.setForeground(Theme.getSecundario());
        if (pTarjetaBoleto != null) {
            pTarjetaBoleto.setBackground(Theme.getSurface());
            pTarjetaBoleto.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getPasajeroAccent(), 1),
                    BorderFactory.createEmptyBorder(16, 20, 16, 20)));
        }
        if (pDatosBoleto != null) {
            pDatosBoleto.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 1, 0, Theme.getBorder()),
                    BorderFactory.createEmptyBorder(12, 0, 12, 0)));
        }
        if (lblPaseNombrePasajero != null) lblPaseNombrePasajero.setForeground(Theme.getTexto());
        if (lblPaseRutaVuelo != null) lblPaseRutaVuelo.setForeground(Theme.getTexto());
        if (lblPaseHorario != null) lblPaseHorario.setForeground(Theme.getSecundario());
        if (lblPaseEquipajeDetalle != null) lblPaseEquipajeDetalle.setForeground(Theme.getTexto());
        if (pMonitorSalas != null) {
            pMonitorSalas.setBackground(Theme.getElevated());
            pMonitorSalas.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getBorder(), 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        }
        if (btnVolverEquipajePase != null) {
            btnVolverEquipajePase.setBackground(Theme.getElevated());
            btnVolverEquipajePase.setForeground(Theme.getTexto());
            btnVolverEquipajePase.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getBorder(), 1),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        }
        if (btnNuevaReservaPase != null) {
            btnNuevaReservaPase.setBackground(Theme.getElevated());
            btnNuevaReservaPase.setForeground(Theme.getPasajeroAccent());
            btnNuevaReservaPase.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getPasajeroAccent(), 1),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        }

        // Tablas y Renderers
        if (tablaVuelosPasajero != null) RenderersTablaVuelos.aplicarEstilosTabla(tablaVuelosPasajero);
        if (tablaEquipajePasajero != null) RenderersTablaVuelos.aplicarEstilosTabla(tablaEquipajePasajero);
        if (tablaVuelos != null) RenderersTablaVuelos.aplicarEstilosTabla(tablaVuelos);
        if (scrollTablaVuelos != null) {
            scrollTablaVuelos.getViewport().setBackground(Theme.getSurface());
            scrollTablaVuelos.setBorder(BorderFactory.createLineBorder(Theme.getBorder(), 1));
        }

        // Módulos Admin y Radares
        aplicarTemaAComponentesAdmin(panelModuloVuelos);
        aplicarTemaAComponentesAdmin(panelModuloCabina);
        aplicarTemaAComponentesAdmin(panelGestionEquipaje);
        aplicarTemaAComponentesAdmin(panelGestionRutas);
        aplicarTemaAComponentesAdmin(panelModuloPuertas);
        aplicarTemaAComponentesAdmin(panelModuloAdministracion);

        if (panelRadarSmartAirport != null) panelRadarSmartAirport.actualizarTema();
        if (panelSupervisoresAeropuertos != null) panelSupervisoresAeropuertos.actualizarTema();

        actualizarCabinaPasajero();
        actualizarMatrizCabina();
        actualizarMetricasDashboard();
        actualizarEstadoSesionUI();

        if (stepperPasajero != null) stepperPasajero.repaint();
        if (panelDashboardKpi != null) panelDashboardKpi.repaint();
        if (stripVueloSeleccionado != null) stripVueloSeleccionado.repaint();
        if (barraEstadoFooter != null) barraEstadoFooter.repaint();

        SwingUtilities.updateComponentTreeUI(this);
        repaint();
    }

    private void aplicarTemaAComponentesAdmin(Container cont) {
        if (cont == null) return;
        boolean oscuro = Theme.esModoOscuro();
        Color surfaceTheme = Theme.getSurface();
        Color borderTheme = Theme.getBorder();
        Color textTheme = Theme.getTexto();
        Color inputTheme = Theme.getInput();

        for (Component c : cont.getComponents()) {
            if (c instanceof JPanel) {
                JPanel p = (JPanel) c;
                if (p.isOpaque()) {
                    Color bg = p.getBackground();
                    if (bg != null) {
                        if (bg.getRed() < 40 && bg.getGreen() < 40 && bg.getBlue() < 40 && !oscuro) {
                            p.setBackground(surfaceTheme);
                        } else if (bg.getRed() > 200 && bg.getGreen() > 200 && bg.getBlue() > 200 && oscuro) {
                            p.setBackground(surfaceTheme);
                        }
                    }
                }
                Border b = p.getBorder();
                if (b instanceof TitledBorder) {
                    TitledBorder tb = (TitledBorder) b;
                    tb.setTitleColor(Theme.getAdminAccent());
                    tb.setBorder(new LineBorder(borderTheme, 1));
                }
                aplicarTemaAComponentesAdmin(p);
            } else if (c instanceof JScrollPane) {
                JScrollPane sp = (JScrollPane) c;
                sp.getViewport().setBackground(surfaceTheme);
                sp.setBorder(BorderFactory.createLineBorder(borderTheme, 1));
                aplicarTemaAComponentesAdmin(sp.getViewport());
            } else if (c instanceof JTable) {
                JTable t = (JTable) c;
                RenderersTablaVuelos.aplicarEstilosTabla(t);
            } else if (c instanceof JLabel) {
                JLabel l = (JLabel) c;
                Color fg = l.getForeground();
                if (fg != null) {
                    if ((fg.getRed() > 200 && fg.getGreen() > 200 && fg.getBlue() > 200 && !oscuro) ||
                        (fg.getRed() < 60 && fg.getGreen() < 60 && fg.getBlue() < 60 && oscuro)) {
                        l.setForeground(textTheme);
                    }
                }
            } else if (c instanceof JTextField) {
                JTextField tf = (JTextField) c;
                tf.setBackground(inputTheme);
                tf.setForeground(textTheme);
                tf.setCaretColor(textTheme);
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderTheme, 1),
                        BorderFactory.createEmptyBorder(4, 8, 4, 8)));
            } else if (c instanceof JTextArea) {
                JTextArea ta = (JTextArea) c;
                ta.setBackground(inputTheme);
                ta.setForeground(textTheme);
                ta.setCaretColor(textTheme);
            } else if (c instanceof JComboBox) {
                JComboBox<?> cb = (JComboBox<?>) c;
                cb.setBackground(inputTheme);
                cb.setForeground(textTheme);
            } else if (c instanceof Container) {
                aplicarTemaAComponentesAdmin((Container) c);
            }
        }
    }

    public void seleccionarDestinoPublico(String destino) {
        if (cbFiltroDestinoPasajero != null) {
            cbFiltroDestinoPasajero.setSelectedItem(destino);
        }
    }
}
