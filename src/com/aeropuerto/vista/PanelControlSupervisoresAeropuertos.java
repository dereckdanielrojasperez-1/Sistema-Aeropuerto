package com.aeropuerto.vista;

import com.aeropuerto.controlador.ControladorAeropuerto;
import com.aeropuerto.modelo.Aeropuerto;
import com.aeropuerto.modelo.Supervisor;
import com.aeropuerto.vista.tema.Theme;
import com.aeropuerto.vista.tema.GlassPanel;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// Formato y panel de control para supervision y disponibilidad de aeropuertos
public class PanelControlSupervisoresAeropuertos extends JPanel {
    private static final long serialVersionUID = 1L;

    private ControladorAeropuerto controlador;
    private VentanaPrincipal ventanaPrincipal;

    // Paleta de colores Smart Airport
    private Color fondoOscuro = new Color(11, 14, 20);
    private Color fondoTarjeta = new Color(19, 25, 36);
    private Color fondoEntrada = new Color(15, 20, 29);
    private Color bordeTarjeta = new Color(34, 44, 63);
    private Color doradoSmart = new Color(245, 158, 11);
    private Color verdeLibre = new Color(16, 185, 129);
    private Color rojoAlerta = new Color(239, 68, 68);
    private Color azulCyan = new Color(56, 189, 248);
    private Color textoBlanco = new Color(248, 250, 252);
    private Color textoGris = new Color(148, 163, 184);

    // Indicadores KPI superiores
    private JLabel lblTotalAeropuertos;
    private JLabel lblAeropuertosLibres;
    private JLabel lblAeropuertosOcupados;
    private JLabel lblTotalSupervisores;

    // Componentes de control de aeropuertos
    private JTable tablaAeropuertos;
    private DefaultTableModel modeloAeropuertos;
    private JTextField txtBuscarAeropuerto;
    private JComboBox<String> cbFiltroEstadoAeropuerto;

    // Componentes de gestion de supervisores
    private JTable tablaSupervisores;
    private DefaultTableModel modeloSupervisores;
    private JTextField txtSupervisorId;
    private JTextField txtSupervisorNombre;
    private JTextField txtSupervisorDni;
    private JComboBox<String> cbSupervisorTurno;
    private JComboBox<String> cbSupervisorRol;
    private JComboBox<String> cbSupervisorAeropuerto;
    private JComboBox<String> cbAsignarAeropuertoDestino;
    private JComboBox<String> cbAsignarSupervisorOrigen;

    public PanelControlSupervisoresAeropuertos(ControladorAeropuerto controlador, VentanaPrincipal ventanaPrincipal) {
        this.controlador = controlador;
        this.ventanaPrincipal = ventanaPrincipal;
        refrescarPaleta();
        setLayout(new BorderLayout(0, 10));
        setBackground(fondoOscuro);
        setBorder(new EmptyBorder(12, 14, 12, 14));
        inicializarComponentes();
        actualizarTablas();
        Theme.addThemeListener(this::actualizarTema);
    }

    private void refrescarPaleta() {
        boolean oscuro = Theme.esModoOscuro();
        fondoOscuro = Theme.getBg();
        fondoTarjeta = Theme.getSurface();
        fondoEntrada = Theme.getInput();
        bordeTarjeta = Theme.getBorder();
        textoBlanco = Theme.getTexto();
        textoGris = Theme.getSecundario();
        doradoSmart = oscuro ? new Color(245, 158, 11) : new Color(180, 83, 9);
        azulCyan = oscuro ? new Color(56, 189, 248) : new Color(9, 105, 218);
    }

    public void actualizarTema() {
        refrescarPaleta();
        setBackground(fondoOscuro);
        actualizarTablas();
        revalidate();
        repaint();
    }

    private void inicializarComponentes() {
        // 1. Cabecera con KPIs de disponibilidad aeroportuaria
        add(crearBarraKPIsDisponibilidad(), BorderLayout.NORTH);

        // 2. Division central: Aeropuertos Libres (Izquierda) y Supervisores (Derecha)
        JSplitPane splitCentral = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                crearPanelControlAeropuertos(), crearPanelSupervisores());
        splitCentral.setDividerLocation(580);
        splitCentral.setResizeWeight(0.5);
        splitCentral.setBorder(null);
        splitCentral.setOpaque(false);

        add(splitCentral, BorderLayout.CENTER);
    }

    private JPanel crearBarraKPIsDisponibilidad() {
        JPanel barra = new JPanel(new GridLayout(1, 4, 10, 0));
        barra.setOpaque(false);

        barra.add(crearTarjetaKpi("AEROPUERTOS EN CONTROL", lblTotalAeropuertos = new JLabel("0 terminales"), "Red Nacional", azulCyan));
        barra.add(crearTarjetaKpi("AEROPUERTOS 100% LIBRES", lblAeropuertosLibres = new JLabel("0 disponibles"), "Pistas Despejadas", verdeLibre));
        barra.add(crearTarjetaKpi("CON TRÁFICO / OCUPADOS", lblAeropuertosOcupados = new JLabel("0 con aeronaves"), "Aproximación en curso", doradoSmart));
        barra.add(crearTarjetaKpi("SUPERVISORES EN TURNO", lblTotalSupervisores = new JLabel("0 activos"), "Torre y Plataforma", textoBlanco));

        return barra;
    }

    private JPanel crearTarjetaKpi(String titulo, JLabel lblValor, String subtitulo, Color colorValor) {
        GlassPanel card = new GlassPanel(new GridLayout(3, 1, 0, 2), 14);
        card.setColorAcento(colorValor);
        card.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTit.setForeground(textoGris);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblValor.setForeground(colorValor);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(textoGris);

        card.add(lblTit);
        card.add(lblValor);
        card.add(lblSub);
        return card;
    }

    // Panel izquierdo: Formato de control de aeropuertos libres y pistas en GlassPanel
    private JPanel crearPanelControlAeropuertos() {
        GlassPanel p = new GlassPanel(new BorderLayout(0, 8), 16);
        p.setColorAcento(doradoSmart);
        p.setBorder(new EmptyBorder(10, 12, 10, 12));

        // Cabecera del panel de aeropuertos
        JPanel pSup = new JPanel(new BorderLayout(8, 4));
        pSup.setOpaque(false);

        JLabel lblTit = new JLabel("Monitor de Disponibilidad de Aeropuertos y Pistas de Aterrizaje");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(doradoSmart);

        JLabel lblDesc = new JLabel("Visualización en tiempo real de terminales libres, capacidad de stands y pistas despejadas.");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblDesc.setForeground(textoGris);

        JPanel pTitulos = new JPanel(new GridLayout(2, 1, 0, 2));
        pTitulos.setOpaque(false);
        pTitulos.add(lblTit);
        pTitulos.add(lblDesc);

        // Barra de busqueda y filtrado lineal O(n)
        JPanel pFiltros = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pFiltros.setOpaque(false);

        txtBuscarAeropuerto = new JTextField(8);
        txtBuscarAeropuerto.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtBuscarAeropuerto.setBackground(fondoEntrada);
        txtBuscarAeropuerto.setForeground(textoBlanco);
        txtBuscarAeropuerto.setCaretColor(doradoSmart);
        txtBuscarAeropuerto.setBorder(new CompoundBorder(new LineBorder(bordeTarjeta, 1), new EmptyBorder(2, 4, 2, 4)));
        txtBuscarAeropuerto.setToolTipText("Buscar por código IATA o Ciudad");
        txtBuscarAeropuerto.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrarTablaAeropuertos();
            }
        });

        cbFiltroEstadoAeropuerto = new JComboBox<>(new String[]{"Todos", "Solo Libres", "Ocupados"});
        cbFiltroEstadoAeropuerto.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFiltroEstadoAeropuerto.setBackground(fondoEntrada);
        cbFiltroEstadoAeropuerto.setForeground(textoBlanco);
        cbFiltroEstadoAeropuerto.addActionListener(e -> filtrarTablaAeropuertos());

        JLabel lblFiltro = new JLabel("Filtrar:");
        lblFiltro.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblFiltro.setForeground(textoGris);
        pFiltros.add(lblFiltro);
        pFiltros.add(cbFiltroEstadoAeropuerto);

        JLabel lblBuscar = new JLabel("Buscar IATA:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblBuscar.setForeground(textoGris);
        pFiltros.add(lblBuscar);
        pFiltros.add(txtBuscarAeropuerto);

        pSup.add(pTitulos, BorderLayout.NORTH);
        pSup.add(pFiltros, BorderLayout.SOUTH);
        p.add(pSup, BorderLayout.NORTH);

        // Tabla de aeropuertos estilizada en modo oscuro
        String[] columnas = {"IATA", "Aeropuerto", "Ciudad", "Pistas Libres", "Stands", "Estado", "Supervisor Asignado"};
        modeloAeropuertos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tablaAeropuertos = new JTable(modeloAeropuertos);
        tablaAeropuertos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaAeropuertos.setRowHeight(24);
        tablaAeropuertos.setBackground(fondoEntrada);
        tablaAeropuertos.setForeground(textoBlanco);
        tablaAeropuertos.setGridColor(new Color(28, 36, 52));
        tablaAeropuertos.setSelectionBackground(new Color(36, 48, 70));
        tablaAeropuertos.setSelectionForeground(doradoSmart);

        JTableHeader th = tablaAeropuertos.getTableHeader();
        th.setBackground(new Color(22, 29, 41));
        th.setForeground(doradoSmart);
        th.setFont(new Font("Segoe UI", Font.BOLD, 10));

        DefaultTableCellRenderer renderCentro = new DefaultTableCellRenderer();
        renderCentro.setHorizontalAlignment(SwingConstants.CENTER);
        tablaAeropuertos.getColumnModel().getColumn(0).setCellRenderer(renderCentro);
        tablaAeropuertos.getColumnModel().getColumn(3).setCellRenderer(renderCentro);
        tablaAeropuertos.getColumnModel().getColumn(4).setCellRenderer(renderCentro);
        tablaAeropuertos.getColumnModel().getColumn(5).setCellRenderer(new RenderEstadoAeropuerto());

        JScrollPane scroll = new JScrollPane(tablaAeropuertos);
        scroll.getViewport().setBackground(fondoEntrada);
        scroll.setBorder(BorderFactory.createLineBorder(bordeTarjeta));
        p.add(scroll, BorderLayout.CENTER);

        // Botonera de acciones sobre aeropuertos organizada en 2 apartados temáticos de GlassPanel
        JPanel pBotones = new JPanel(new GridLayout(2, 1, 0, 6));
        pBotones.setOpaque(false);

        JButton btnLiberar = new JButton("Despejar Pista / Liberar");
        btnLiberar.setBackground(verdeLibre);
        btnLiberar.setForeground(Color.WHITE);
        btnLiberar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnLiberar.setMargin(new Insets(3, 8, 3, 8));
        btnLiberar.setFocusPainted(false);
        btnLiberar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLiberar.addActionListener(e -> accionLiberarPista());

        JButton btnOcupar = new JButton("Ocupar Pista / Tráfico");
        btnOcupar.setBackground(new Color(220, 100, 0));
        btnOcupar.setForeground(Color.WHITE);
        btnOcupar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnOcupar.setMargin(new Insets(3, 8, 3, 8));
        btnOcupar.setFocusPainted(false);
        btnOcupar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOcupar.addActionListener(e -> accionOcuparPista());

        JButton btnOrdenar = new JButton("Ordenar por Capacidad");
        btnOrdenar.setBackground(new Color(30, 41, 59));
        btnOrdenar.setForeground(textoBlanco);
        btnOrdenar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnOrdenar.setMargin(new Insets(3, 8, 3, 8));
        btnOrdenar.setFocusPainted(false);
        btnOrdenar.addActionListener(e -> accionOrdenarAeropuertos());

        JButton btnCargarEstandar = new JButton("Cargar 8 Aeropuertos Nacionales");
        btnCargarEstandar.setBackground(doradoSmart);
        btnCargarEstandar.setForeground(new Color(15, 20, 29));
        btnCargarEstandar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnCargarEstandar.setMargin(new Insets(3, 8, 3, 8));
        btnCargarEstandar.setFocusPainted(false);
        btnCargarEstandar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCargarEstandar.addActionListener(e -> {
            if (!verificarPermisoAdmin("Cargar Aeropuertos Estándar")) return;
            controlador.cargarAeropuertosEstandar();
            actualizarTablas();
            JOptionPane.showMessageDialog(this, "8 aeropuertos nacionales cargados (LIM, CUZ, AQP, PIU, IQT, TRU, TPP, CIX).", "Aeropuertos", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnDespejarTodo = new JButton("Despejar Todo (Express)");
        btnDespejarTodo.setBackground(new Color(20, 50, 35));
        btnDespejarTodo.setForeground(verdeLibre);
        btnDespejarTodo.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnDespejarTodo.setMargin(new Insets(3, 8, 3, 8));
        btnDespejarTodo.setFocusPainted(false);
        btnDespejarTodo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDespejarTodo.setToolTipText("Despejar todas las pistas de aterrizaje nacionales con un solo clic");
        btnDespejarTodo.addActionListener(e -> accionDespejarTodasPistas());

        JPanel filaBtnAero1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 1));
        filaBtnAero1.setOpaque(false);
        filaBtnAero1.add(btnLiberar);
        filaBtnAero1.add(btnOcupar);
        filaBtnAero1.add(btnDespejarTodo);
        GlassPanel cardPistas = GlassPanel.crearApartado("Control Operativo de Pistas", "Disponibilidad en tiempo real", verdeLibre, filaBtnAero1);

        JPanel filaBtnAero2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 1));
        filaBtnAero2.setOpaque(false);
        filaBtnAero2.add(btnOrdenar);
        filaBtnAero2.add(btnCargarEstandar);
        GlassPanel cardCatalogo = GlassPanel.crearApartado("Catálogo y Algoritmos", "Ordenación por capacidad y carga", doradoSmart, filaBtnAero2);

        pBotones.add(cardPistas);
        pBotones.add(cardCatalogo);

        p.add(pBotones, BorderLayout.SOUTH);
        return p;
    }

    // Panel derecho: Formato de Supervisores de Torre y Operaciones en GlassPanel
    private JPanel crearPanelSupervisores() {
        GlassPanel p = new GlassPanel(new BorderLayout(0, 8), 16);
        p.setColorAcento(doradoSmart);
        p.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel pSup = new JPanel(new BorderLayout());
        pSup.setOpaque(false);

        JLabel lblTit = new JLabel("Supervisores de Operaciones y Control de Trafico Aereo - ATC");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTit.setForeground(doradoSmart);

        JLabel lblDesc = new JLabel("Personal responsable de asignación de pistas, despacho y supervisión de terminales.");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblDesc.setForeground(textoGris);

        JPanel pTitulos = new JPanel(new GridLayout(2, 1, 0, 2));
        pTitulos.setOpaque(false);
        pTitulos.add(lblTit);
        pTitulos.add(lblDesc);
        pSup.add(pTitulos, BorderLayout.WEST);
        p.add(pSup, BorderLayout.NORTH);

        // Tabla de supervisores
        String[] columnas = {"ID", "Supervisor", "DNI", "Turno", "Rol", "Aeropuerto", "Estado"};
        modeloSupervisores = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tablaSupervisores = new JTable(modeloSupervisores);
        tablaSupervisores.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaSupervisores.setRowHeight(24);
        tablaSupervisores.setBackground(fondoEntrada);
        tablaSupervisores.setForeground(textoBlanco);
        tablaSupervisores.setGridColor(new Color(28, 36, 52));
        tablaSupervisores.setSelectionBackground(new Color(36, 48, 70));
        tablaSupervisores.setSelectionForeground(doradoSmart);

        JTableHeader th = tablaSupervisores.getTableHeader();
        th.setBackground(new Color(22, 29, 41));
        th.setForeground(doradoSmart);
        th.setFont(new Font("Segoe UI", Font.BOLD, 10));

        JScrollPane scroll = new JScrollPane(tablaSupervisores);
        scroll.getViewport().setBackground(fondoEntrada);
        scroll.setBorder(BorderFactory.createLineBorder(bordeTarjeta));
        p.add(scroll, BorderLayout.CENTER);

        // Formulario inferior de registro y asignacion de supervisor
        JPanel pFormulario = new JPanel(new BorderLayout(0, 6));
        pFormulario.setOpaque(false);
        pFormulario.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(bordeTarjeta),
                "Asignación Rápida de Supervisor a Aeropuerto", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 10), doradoSmart));

        JPanel pAsignacion = new JPanel(new GridLayout(2, 1, 0, 3));
        pAsignacion.setOpaque(false);

        JPanel filaAsig1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 1));
        filaAsig1.setOpaque(false);

        JPanel filaAsig2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 1));
        filaAsig2.setOpaque(false);

        cbAsignarSupervisorOrigen = new JComboBox<>();
        cbAsignarSupervisorOrigen.setBackground(fondoEntrada);
        cbAsignarSupervisorOrigen.setForeground(textoBlanco);
        cbAsignarSupervisorOrigen.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        cbAsignarAeropuertoDestino = new JComboBox<>();
        cbAsignarAeropuertoDestino.setBackground(fondoEntrada);
        cbAsignarAeropuertoDestino.setForeground(textoBlanco);
        cbAsignarAeropuertoDestino.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        JButton btnAsignar = new JButton("Asignar a Aeropuerto");
        btnAsignar.setBackground(doradoSmart);
        btnAsignar.setForeground(new Color(15, 20, 29));
        btnAsignar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnAsignar.setMargin(new Insets(3, 8, 3, 8));
        btnAsignar.setFocusPainted(false);
        btnAsignar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAsignar.addActionListener(e -> accionAsignarSupervisor());

        JButton btnCargarSupervisores = new JButton("Cargar 4 Supervisores");
        btnCargarSupervisores.setBackground(new Color(30, 41, 59));
        btnCargarSupervisores.setForeground(textoBlanco);
        btnCargarSupervisores.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnCargarSupervisores.setMargin(new Insets(3, 8, 3, 8));
        btnCargarSupervisores.setFocusPainted(false);
        btnCargarSupervisores.addActionListener(e -> {
            if (!verificarPermisoAdmin("Cargar Supervisores Estándar")) return;
            controlador.cargarSupervisoresEstandar();
            actualizarTablas();
            JOptionPane.showMessageDialog(this, "4 supervisores de operaciones inicializados.", "Supervisores", JOptionPane.INFORMATION_MESSAGE);
        });

        JLabel lblSup = new JLabel("Supervisor:");
        lblSup.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblSup.setForeground(textoGris);
        filaAsig1.add(lblSup);
        filaAsig1.add(cbAsignarSupervisorOrigen);

        JLabel lblAero = new JLabel("Aeropuerto:");
        lblAero.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblAero.setForeground(textoGris);
        filaAsig1.add(lblAero);
        filaAsig1.add(cbAsignarAeropuertoDestino);

        filaAsig2.add(btnAsignar);
        filaAsig2.add(btnCargarSupervisores);

        pAsignacion.add(filaAsig1);
        pAsignacion.add(filaAsig2);

        GlassPanel cardAsignacion = GlassPanel.crearApartado("Asignación Operativa a Terminal", "Despliegue en terminal", doradoSmart, pAsignacion);
        pFormulario.add(cardAsignacion, BorderLayout.CENTER);
        p.add(pFormulario, BorderLayout.SOUTH);

        return p;
    }

    private boolean verificarPermisoAdmin(String accion) {
        if (controlador.esSesionAdmin()) {
            return true;
        }
        int resp = JOptionPane.showConfirmDialog(this,
                "La acción '" + accion + "' requiere privilegios de Administrador.\n" +
                "En modo Consulta o Usuario Registrado solo se permite visualizar telemetría.\n\n" +
                "¿Desea iniciar sesión como Administrador ahora?",
                "Permiso de Administrador Requerido", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (resp == JOptionPane.YES_OPTION) {
            if (ventanaPrincipal != null) {
                ventanaPrincipal.abrirDialogoLoginAdminPublico();
            }
            return controlador.esSesionAdmin();
        }
        return false;
    }

    public void actualizarEstadoPermisos() {
        actualizarTablas();
    }

    public void actualizarTablas() {
        // Actualizar tabla de aeropuertos
        modeloAeropuertos.setRowCount(0);
        cbAsignarAeropuertoDestino.removeAllItems();

        Aeropuerto[] listaA = controlador.obtenerAeropuertos();
        int totalA = controlador.obtenerContadorAeropuertos();
        int libres = 0;
        int ocupados = 0;

        for (int i = 0; i < totalA; i++) {
            if (listaA[i] != null) {
                Aeropuerto a = listaA[i];
                if (a.esLibre()) libres++;
                else ocupados++;

                cbAsignarAeropuertoDestino.addItem(a.obtenerCodigoIata());

                modeloAeropuertos.addRow(new Object[]{
                        a.obtenerCodigoIata(),
                        a.obtenerNombre(),
                        a.obtenerCiudad(),
                        a.obtenerPistasLibres() + " / " + a.obtenerTotalPistas() + " Libres",
                        a.obtenerAeronavesActuales() + " / " + a.obtenerCapacidadStands() + " Aeronaves",
                        a.obtenerEstado(),
                        a.obtenerSupervisorACargo()
                });
            }
        }

        lblTotalAeropuertos.setText(totalA + " terminales");
        lblAeropuertosLibres.setText(libres + " disponibles");
        lblAeropuertosOcupados.setText(ocupados + " con tráfico");

        // Actualizar tabla de supervisores
        modeloSupervisores.setRowCount(0);
        cbAsignarSupervisorOrigen.removeAllItems();

        Supervisor[] listaS = controlador.obtenerSupervisores();
        int totalS = controlador.obtenerContadorSupervisores();

        for (int i = 0; i < totalS; i++) {
            if (listaS[i] != null) {
                Supervisor s = listaS[i];
                cbAsignarSupervisorOrigen.addItem(s.obtenerIdSupervisor() + " - " + s.obtenerNombre());

                modeloSupervisores.addRow(new Object[]{
                        s.obtenerIdSupervisor(),
                        s.obtenerNombre(),
                        s.obtenerDni(),
                        s.obtenerTurno(),
                        s.obtenerRol(),
                        s.obtenerAeropuertoAsignado(),
                        s.obtenerEstado()
                });
            }
        }
        lblTotalSupervisores.setText(totalS + " activos");
    }

    private void filtrarTablaAeropuertos() {
        String texto = txtBuscarAeropuerto.getText().trim().toLowerCase();
        String filtroEstado = (String) cbFiltroEstadoAeropuerto.getSelectedItem();

        modeloAeropuertos.setRowCount(0);
        Aeropuerto[] listaA = controlador.obtenerAeropuertos();
        int totalA = controlador.obtenerContadorAeropuertos();

        for (int i = 0; i < totalA; i++) {
            if (listaA[i] != null) {
                Aeropuerto a = listaA[i];
                boolean coincideTexto = a.obtenerCodigoIata().toLowerCase().contains(texto) ||
                                       a.obtenerCiudad().toLowerCase().contains(texto) ||
                                       a.obtenerNombre().toLowerCase().contains(texto);

                boolean coincideEstado = true;
                if ("Solo Libres".equals(filtroEstado)) {
                    coincideEstado = a.esLibre();
                } else if ("Ocupados".equals(filtroEstado)) {
                    coincideEstado = !a.esLibre();
                }

                if (coincideTexto && coincideEstado) {
                    modeloAeropuertos.addRow(new Object[]{
                            a.obtenerCodigoIata(),
                            a.obtenerNombre(),
                            a.obtenerCiudad(),
                            a.obtenerPistasLibres() + " / " + a.obtenerTotalPistas() + " Libres",
                            a.obtenerAeronavesActuales() + " / " + a.obtenerCapacidadStands() + " Aeronaves",
                            a.obtenerEstado(),
                            a.obtenerSupervisorACargo()
                    });
                }
            }
        }
    }

    private void accionDespejarTodasPistas() {
        if (!verificarPermisoAdmin("Despejar Todas las Pistas")) return;
        Aeropuerto[] aeros = controlador.obtenerAeropuertos();
        int total = controlador.obtenerContadorAeropuertos();
        if (aeros != null) {
            for (int i = 0; i < total; i++) {
                Aeropuerto a = aeros[i];
                if (a != null) {
                    while (controlador.liberarPistaAeropuerto(a.obtenerCodigoIata()));
                }
            }
            actualizarTablas();
            JOptionPane.showMessageDialog(this, "Todas las pistas de aterrizaje nacionales han sido despejadas satisfactoriamente.", "Control de Pistas", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void accionLiberarPista() {
        if (!verificarPermisoAdmin("Despejar Pista / Liberar")) return;
        int fila = tablaAeropuertos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un aeropuerto de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String iata = (String) tablaAeropuertos.getValueAt(fila, 0);
        if (controlador.liberarPistaAeropuerto(iata)) {
            actualizarTablas();
            JOptionPane.showMessageDialog(this, "Pista del aeropuerto [" + iata + "] liberada. Pistas disponibles incrementadas.", "Control de Pistas", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Todas las pistas ya se encuentran completamente libres en [" + iata + "].", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void accionOcuparPista() {
        if (!verificarPermisoAdmin("Ocupar Pista / Asignar Tráfico")) return;
        int fila = tablaAeropuertos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un aeropuerto de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String iata = (String) tablaAeropuertos.getValueAt(fila, 0);
        if (controlador.ocuparPistaAeropuerto(iata)) {
            actualizarTablas();
            JOptionPane.showMessageDialog(this, "Pista asignada para operación de despegue/aterrizaje en [" + iata + "].", "Tráfico Asignado", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No hay pistas disponibles en [" + iata + "]. Capacidad máxima alcanzada.", "Pistas Saturadas", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void accionOrdenarAeropuertos() {
        controlador.ordenarAeropuertosPorCapacidad(false);
        actualizarTablas();
        JOptionPane.showMessageDialog(this, "Aeropuertos ordenados por capacidad de stands de mayor a menor.", "Ordenamiento", JOptionPane.INFORMATION_MESSAGE);
    }

    private void accionAsignarSupervisor() {
        if (!verificarPermisoAdmin("Asignar Supervisor a Torre")) return;
        String supSeleccionado = (String) cbAsignarSupervisorOrigen.getSelectedItem();
        String iata = (String) cbAsignarAeropuertoDestino.getSelectedItem();

        if (supSeleccionado == null || iata == null) {
            JOptionPane.showMessageDialog(this, "Seleccione supervisor y aeropuerto.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idSup = supSeleccionado.split(" - ")[0].trim();
        if (controlador.asignarSupervisorAAeropuerto(iata, idSup)) {
            actualizarTablas();
            JOptionPane.showMessageDialog(this, "Supervisor [" + idSup + "] asignado a la torre/control del aeropuerto [" + iata + "].", "Asignación Exitosa", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Renderizador visual para el estado del aeropuerto (LIBRE = Verde, OCUPADO = Rojo/Ambar)
    private class RenderEstadoAeropuerto extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));

            String estado = (value != null) ? value.toString() : "";
            if ("LIBRE".equalsIgnoreCase(estado)) {
                lbl.setForeground(verdeLibre);
                lbl.setText("[LIBRE]");
            } else if ("OCUPADO".equalsIgnoreCase(estado)) {
                lbl.setForeground(rojoAlerta);
                lbl.setText("[OCUPADO]");
            } else {
                lbl.setForeground(doradoSmart);
                lbl.setText("[" + estado.toUpperCase() + "]");
            }
            return lbl;
        }
    }
}
