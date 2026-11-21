package com.aeropuerto.vista;

import com.aeropuerto.controlador.ControladorAeropuerto;
import com.aeropuerto.modelo.Avion;
import com.aeropuerto.modelo.Vuelo;
import com.aeropuerto.vista.tema.Theme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyEvent;

// Ficha técnica oficial y catálogo dinámico de flota de aeronaves comerciales CORPAC S.A.
public class DialogoFichaAvion extends JDialog {
    private static final long serialVersionUID = 1L;

    private final ControladorAeropuerto controlador;
    private final Avion avionActivo;

    private CardLayout layoutVisualizador;
    private JPanel panelTarjetasVisual;
    private JButton btnVerFoto;
    private JButton btnVerPlano;
    private JButton btnVer3D;

    private JLabel lblTitulo;
    private JLabel lblSubtitulo;
    private JComboBox<String> cbSelectorVuelo;

    private PanelVisorImagenHD panelFotoHD;
    private PanelVisorImagenHD panelPlano;

    private JLabel lblValVelocidad;
    private JLabel lblValAlcance;
    private JLabel lblValTecho;
    private JLabel lblValDespegue;

    private JLabel lblValEnvergadura;
    private JLabel lblValLongitud;
    private JLabel lblValAltura;
    private JLabel lblValMTOW;

    private JLabel lblValFabricante;
    private JLabel lblValMatricula;
    private JLabel lblValMotores;
    private JLabel lblValEficiencia;

    private JLabel lblValCapacidad;
    private JLabel lblValCabina;
    private JLabel lblValZonificacion;
    private JLabel lblValCert;

    private JTextArea txtDescripcion;

    public DialogoFichaAvion(JFrame ventanaPadre, ControladorAeropuerto controlador) {
        super(ventanaPadre, "Ficha Técnica y Especificaciones de Aeronave - Flota CORPAC S.A.", false);
        this.controlador = controlador;
        this.avionActivo = controlador.obtenerAvion();

        // Si hay un vuelo activo en el controlador, configurar el avión automáticamente para su destino
        String codActivo = controlador.obtenerCodigoVueloActivo();
        if (codActivo != null && !codActivo.isEmpty()) {
            Vuelo v = controlador.buscarVueloPorCodigo(codActivo);
            if (v != null) {
                this.avionActivo.configurarSegunDestino(v.obtenerDestino(), v.obtenerCodigo());
            }
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets insets = null;
        try {
            insets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        } catch (Exception ignored) {}
        int maxH = (insets != null) ? screenSize.height - insets.top - insets.bottom : screenSize.height - 50;
        int maxW = (insets != null) ? screenSize.width - insets.left - insets.right : screenSize.width - 40;

        int dialogW = Math.min(980, maxW - 20);
        int dialogH = Math.min(760, maxH - 20);
        setSize(dialogW, dialogH);
        setLocationRelativeTo(ventanaPadre);
        setResizable(true);

        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        configurarIcono();
        inicializarComponentes();
        actualizarDatosAeronave(this.avionActivo);
    }

    private void configurarIcono() {
        try {
            Image img = cargarImagen("logo_corpac.png");
            if (img != null) {
                setIconImage(img);
            }
        } catch (Exception ignored) {}
    }

    private Image cargarImagen(String nombre) {
        try {
            java.net.URL url = getClass().getResource("/com/aeropuerto/vista/" + nombre);
            if (url == null) url = getClass().getResource(nombre);
            if (url != null) return new ImageIcon(url).getImage();
            java.io.File archivo = new java.io.File("src/com/aeropuerto/vista/" + nombre);
            if (archivo.exists()) return new ImageIcon(archivo.getAbsolutePath()).getImage();
        } catch (Exception ignored) {}
        return null;
    }

    private void inicializarComponentes() {
        Color fondoOscuro = Theme.getBg();
        Color fondoTarjeta = Theme.getSurface();
        Color fondoHeader = Theme.getElevated();
        Color bordeTarjeta = Theme.getBorder();
        Color doradoSmart = Theme.getWarning();
        Color azulCyan = Theme.getPasajeroAccent();
        Color textoBlanco = Theme.getTexto();
        Color textoGris = Theme.getSecundario();
        Color verdeAcento = Theme.getSuccess();

        JPanel panelContenedorRaiz = new JPanel(new BorderLayout(0, 0));
        panelContenedorRaiz.setBackground(fondoOscuro);
        setContentPane(panelContenedorRaiz);

        // =========================================================================
        // 1. Encabezado Institucional CORPAC con Selector Rápido de Vuelos de la Flota
        // =========================================================================
        JPanel panelEncabezado = new JPanel();
        panelEncabezado.setLayout(new BoxLayout(panelEncabezado, BoxLayout.Y_AXIS));
        panelEncabezado.setBackground(fondoHeader);
        panelEncabezado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, bordeTarjeta),
                new EmptyBorder(10, 18, 10, 18)
        ));

        // Fila 1: Título principal a la izquierda y Logo CORPAC a la derecha
        JPanel filaSuperior = new JPanel(new BorderLayout(12, 0));
        filaSuperior.setOpaque(false);

        lblTitulo = new JLabel("FICHA TÉCNICA OFICIAL: " + avionActivo.obtenerModelo().toUpperCase(), JLabel.LEFT);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitulo.setForeground(doradoSmart);
        filaSuperior.add(lblTitulo, BorderLayout.WEST);

        Image imgLogo = cargarImagen("logo_corpac.png");
        if (imgLogo != null) {
            JLabel lblLogo = new JLabel(new ImageIcon(imgLogo.getScaledInstance(130, 32, Image.SCALE_SMOOTH)));
            filaSuperior.add(lblLogo, BorderLayout.EAST);
        }

        // Fila 2: Subtítulo de ruta a la izquierda y selector de aeronave/vuelo a la derecha
        JPanel filaInferior = new JPanel(new BorderLayout(12, 0));
        filaInferior.setOpaque(false);

        lblSubtitulo = new JLabel("Aeronave asignada a ruta comercial nacional · Flota Oficial CORPAC S.A.", JLabel.LEFT);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(textoGris);
        filaInferior.add(lblSubtitulo, BorderLayout.WEST);

        JPanel pSelectorWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pSelectorWrapper.setOpaque(false);

        JLabel lblSelVuelo = new JLabel("Vuelo / Destino:");
        lblSelVuelo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSelVuelo.setForeground(azulCyan);

        String[] opcionesFlota = {
                "CP-2023: Lima -> Cusco  [Airbus A320neo - Altura Extrema]",
                "CP-2024: Lima -> Arequipa  [Boeing 737-800 MAX - Troncal Sur]",
                "CP-2025: Lima -> Piura  [Airbus A321neo - Corredor Norte]",
                "CP-2026: Lima -> Iquitos  [Dash 8-Q400 - Selva Amazónica]",
                "CP-2027: Lima -> Tarapoto  [Embraer E190-E2 - Valles y Selva]",
                "CP-2028: Lima -> Trujillo  [Boeing 737-800 MAX - Costa Norte]",
                "CP-2029: Lima -> Juliaca  [Airbus A320neo - Altiplano Andino]",
                "CP-2030: Lima -> Ayacucho  [Embraer E190-E2 - Sierra Central]"
        };

        cbSelectorVuelo = new JComboBox<>(opcionesFlota);
        cbSelectorVuelo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbSelectorVuelo.setBackground(fondoTarjeta);
        cbSelectorVuelo.setForeground(textoBlanco);
        cbSelectorVuelo.setPreferredSize(new Dimension(310, 26));

        // Seleccionar según el código activo
        seleccionarItemSegunCodigo(controlador.obtenerCodigoVueloActivo());

        cbSelectorVuelo.addActionListener(e -> {
            String item = (String) cbSelectorVuelo.getSelectedItem();
            if (item != null) {
                String cod = extraerCodigoDeItem(item);
                String dest = extraerDestinoDeItem(item);
                avionActivo.configurarSegunDestino(dest, cod);
                actualizarDatosAeronave(avionActivo);
            }
        });

        pSelectorWrapper.add(lblSelVuelo);
        pSelectorWrapper.add(cbSelectorVuelo);
        filaInferior.add(pSelectorWrapper, BorderLayout.EAST);

        panelEncabezado.add(filaSuperior);
        panelEncabezado.add(Box.createVerticalStrut(6));
        panelEncabezado.add(filaInferior);

        panelContenedorRaiz.add(panelEncabezado, BorderLayout.NORTH);

        // =========================================================================
        // 2. Contenido Central Desplazable (JScrollPane para máxima compatibilidad)
        // =========================================================================
        JPanel panelCuerpo = new JPanel();
        panelCuerpo.setLayout(new BoxLayout(panelCuerpo, BoxLayout.Y_AXIS));
        panelCuerpo.setBackground(fondoOscuro);
        panelCuerpo.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Barra de Control para Alternar Vistas del Avión
        JPanel panelBarraVistas = new JPanel(new BorderLayout(10, 0));
        panelBarraVistas.setOpaque(false);
        panelBarraVistas.setMaximumSize(new Dimension(32767, 34));

        JLabel lblModoVista = new JLabel("Perspectiva de la Aeronave:");
        lblModoVista.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblModoVista.setForeground(azulCyan);
        panelBarraVistas.add(lblModoVista, BorderLayout.WEST);

        JPanel pBotonesSelector = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pBotonesSelector.setOpaque(false);

        btnVerFoto = new JButton("Fotografía HD en Vuelo");
        btnVerPlano = new JButton("Plano Técnico y Dimensiones");
        btnVer3D = new JButton("Modelo 3D Interactivo");

        estilizarBotonPill(btnVerFoto, true);
        estilizarBotonPill(btnVerPlano, false);
        estilizarBotonPill(btnVer3D, false);

        pBotonesSelector.add(btnVerFoto);
        pBotonesSelector.add(btnVerPlano);
        pBotonesSelector.add(btnVer3D);
        panelBarraVistas.add(pBotonesSelector, BorderLayout.EAST);

        panelCuerpo.add(panelBarraVistas);
        panelCuerpo.add(Box.createVerticalStrut(8));

        // Contenedor de Vistas Alternables (CardLayout)
        layoutVisualizador = new CardLayout();
        panelTarjetasVisual = new JPanel(layoutVisualizador);
        panelTarjetasVisual.setBackground(new Color(11, 19, 41));
        panelTarjetasVisual.setBorder(new LineBorder(bordeTarjeta, 1, true));
        panelTarjetasVisual.setPreferredSize(new Dimension(890, 310));
        panelTarjetasVisual.setMaximumSize(new Dimension(32767, 320));

        // Vista 1: Fotografía HD en Vuelo
        panelFotoHD = new PanelVisorImagenHD(null, "");
        panelTarjetasVisual.add(panelFotoHD, "FOTO");

        // Vista 2: Plano Técnico Esquematico (CAD Blueprint)
        Image imgBlueprint = cargarImagen("airbus_a320neo_blueprint.jpg");
        panelPlano = new PanelVisorImagenHD(imgBlueprint, "PLANO TÉCNICO AERONÁUTICO DE FLOTA");
        panelTarjetasVisual.add(panelPlano, "PLANO");

        // Vista 3: Modelo 3D Interactivo con sus Controles
        JPanel panelContenedor3D = new JPanel(new BorderLayout(0, 4));
        panelContenedor3D.setOpaque(false);

        PanelVisorAvion3D visor3D = new PanelVisorAvion3D();
        visor3D.setPreferredSize(new Dimension(880, 260));

        JPanel barraBotones3D = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        barraBotones3D.setOpaque(false);

        JButton btnGiro = new JButton("Pausar / Rotar Auto");
        estilizarBotonSecundario(btnGiro, azulCyan, fondoHeader);
        btnGiro.addActionListener(e -> visor3D.alternarAnimacion());

        JButton btnMalla = new JButton("Malla 3D / Sólido");
        estilizarBotonSecundario(btnMalla, textoBlanco, fondoHeader);
        btnMalla.addActionListener(e -> visor3D.alternarModoAlambre());

        JButton btnReset = new JButton("Centrar Cámara");
        estilizarBotonSecundario(btnReset, doradoSmart, fondoHeader);
        btnReset.addActionListener(e -> visor3D.reiniciarCamara());

        barraBotones3D.add(btnGiro);
        barraBotones3D.add(btnMalla);
        barraBotones3D.add(btnReset);

        panelContenedor3D.add(visor3D, BorderLayout.CENTER);
        panelContenedor3D.add(barraBotones3D, BorderLayout.SOUTH);
        panelTarjetasVisual.add(panelContenedor3D, "3D");

        panelCuerpo.add(panelTarjetasVisual);
        panelCuerpo.add(Box.createVerticalStrut(12));

        btnVerFoto.addActionListener(e -> {
            layoutVisualizador.show(panelTarjetasVisual, "FOTO");
            estilizarBotonPill(btnVerFoto, true);
            estilizarBotonPill(btnVerPlano, false);
            estilizarBotonPill(btnVer3D, false);
        });

        btnVerPlano.addActionListener(e -> {
            layoutVisualizador.show(panelTarjetasVisual, "PLANO");
            estilizarBotonPill(btnVerFoto, false);
            estilizarBotonPill(btnVerPlano, true);
            estilizarBotonPill(btnVer3D, false);
        });

        btnVer3D.addActionListener(e -> {
            layoutVisualizador.show(panelTarjetasVisual, "3D");
            estilizarBotonPill(btnVerFoto, false);
            estilizarBotonPill(btnVerPlano, false);
            estilizarBotonPill(btnVer3D, true);
        });

        // =========================================================================
        // 3. Cuadrícula de Especificaciones Técnicas (4 Paneles Estructurados)
        // =========================================================================
        JPanel panelCuadricula = new JPanel(new GridLayout(2, 2, 12, 10));
        panelCuadricula.setOpaque(false);
        panelCuadricula.setMaximumSize(new Dimension(32767, 240));

        // Cuadro 1: Rendimiento Operativo
        JPanel panelRendimiento = crearTarjetaSeccion("Rendimiento Operativo", doradoSmart, fondoTarjeta, bordeTarjeta);
        lblValVelocidad = new JLabel("-", JLabel.RIGHT);
        lblValAlcance = new JLabel("-", JLabel.RIGHT);
        lblValTecho = new JLabel("-", JLabel.RIGHT);
        lblValDespegue = new JLabel("-", JLabel.RIGHT);
        panelRendimiento.add(crearFila("Velocidad Crucero:", lblValVelocidad, textoGris, azulCyan));
        panelRendimiento.add(crearFila("Alcance Máximo:", lblValAlcance, textoGris, azulCyan));
        panelRendimiento.add(crearFila("Techo de Operación:", lblValTecho, textoGris, azulCyan));
        panelRendimiento.add(crearFila("Carrera de Despegue:", lblValDespegue, textoGris, azulCyan));
        panelCuadricula.add(panelRendimiento);

        // Cuadro 2: Dimensiones y Geometría
        JPanel panelDimensiones = crearTarjetaSeccion("Dimensiones y Pesos", doradoSmart, fondoTarjeta, bordeTarjeta);
        lblValEnvergadura = new JLabel("-", JLabel.RIGHT);
        lblValLongitud = new JLabel("-", JLabel.RIGHT);
        lblValAltura = new JLabel("-", JLabel.RIGHT);
        lblValMTOW = new JLabel("-", JLabel.RIGHT);
        panelDimensiones.add(crearFila("Envergadura (Alas):", lblValEnvergadura, textoGris, textoBlanco));
        panelDimensiones.add(crearFila("Longitud Total:", lblValLongitud, textoGris, textoBlanco));
        panelDimensiones.add(crearFila("Altura de Cola:", lblValAltura, textoGris, textoBlanco));
        panelDimensiones.add(crearFila("Peso Máx. Despegue (MTOW):", lblValMTOW, textoGris, textoBlanco));
        panelCuadricula.add(panelDimensiones);

        // Cuadro 3: Propulsión y Eficiencia
        JPanel panelPropulsion = crearTarjetaSeccion("Propulsión y Planta de Poder", doradoSmart, fondoTarjeta, bordeTarjeta);
        lblValFabricante = new JLabel("-", JLabel.RIGHT);
        lblValMatricula = new JLabel("-", JLabel.RIGHT);
        lblValMotores = new JLabel("-", JLabel.RIGHT);
        lblValEficiencia = new JLabel("-", JLabel.RIGHT);
        panelPropulsion.add(crearFila("Fabricante / Modelo:", lblValFabricante, textoGris, textoBlanco));
        panelPropulsion.add(crearFila("Matrícula Oficial:", lblValMatricula, textoGris, doradoSmart));
        panelPropulsion.add(crearFila("Planta de Poder:", lblValMotores, textoGris, textoBlanco));
        panelPropulsion.add(crearFila("Eficiencia Operativa:", lblValEficiencia, textoGris, verdeAcento));
        panelCuadricula.add(panelPropulsion);

        // Cuadro 4: Capacidad y Certificación
        JPanel panelCapacidad = crearTarjetaSeccion("Capacidad y Homologación", doradoSmart, fondoTarjeta, bordeTarjeta);
        lblValCapacidad = new JLabel("-", JLabel.RIGHT);
        lblValCabina = new JLabel("-", JLabel.RIGHT);
        lblValZonificacion = new JLabel("-", JLabel.RIGHT);
        lblValCert = new JLabel("-", JLabel.RIGHT);
        panelCapacidad.add(crearFila("Capacidad Comercial:", lblValCapacidad, textoGris, textoBlanco));
        panelCapacidad.add(crearFila("Matriz de Cabina CORPAC:", lblValCabina, textoGris, azulCyan));
        panelCapacidad.add(crearFila("Diferenciación Tarifaria:", lblValZonificacion, textoGris, doradoSmart));
        panelCapacidad.add(crearFila("Certificaciones Oficiales:", lblValCert, textoGris, textoBlanco));
        panelCuadricula.add(panelCapacidad);

        panelCuerpo.add(panelCuadricula);
        panelCuerpo.add(Box.createVerticalStrut(10));

        // =========================================================================
        // 4. Reseña Técnica Oficial
        // =========================================================================
        txtDescripcion = new JTextArea();
        txtDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setEditable(false);
        txtDescripcion.setBackground(fondoTarjeta);
        txtDescripcion.setForeground(textoBlanco);
        txtDescripcion.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(bordeTarjeta, 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
        txtDescripcion.setPreferredSize(new Dimension(890, 75));
        txtDescripcion.setMaximumSize(new Dimension(32767, 85));
        panelCuerpo.add(txtDescripcion);
        panelCuerpo.add(Box.createVerticalStrut(10));

        JScrollPane scrollPrincipal = new JScrollPane(panelCuerpo);
        scrollPrincipal.setBorder(null);
        scrollPrincipal.getVerticalScrollBar().setUnitIncrement(16);
        scrollPrincipal.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        panelContenedorRaiz.add(scrollPrincipal, BorderLayout.CENTER);

        // =========================================================================
        // 5. Barra Inferior con Botón de Cierre
        // =========================================================================
        JPanel panelPie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 8));
        panelPie.setBackground(fondoHeader);

        JLabel lblInfoPie = new JLabel("CORPAC S.A. · Gerencia de Operaciones Aeronáuticas · Flota Diversificada");
        lblInfoPie.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblInfoPie.setForeground(textoGris);

        JPanel pInfoWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        pInfoWrapper.setOpaque(false);
        pInfoWrapper.add(lblInfoPie);

        JPanel panelPieCompleto = new JPanel(new BorderLayout());
        panelPieCompleto.setBackground(fondoHeader);
        panelPieCompleto.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, bordeTarjeta));
        panelPieCompleto.add(pInfoWrapper, BorderLayout.WEST);

        JButton btnCerrar = new JButton("Cerrar Ficha Técnica (Esc)");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCerrar.setBackground(azulCyan);
        btnCerrar.setForeground(new Color(15, 23, 42));
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(azulCyan.darker(), 1, true),
                new EmptyBorder(7, 16, 7, 16)
        ));
        btnCerrar.addActionListener(e -> dispose());

        panelPie.add(btnCerrar);
        panelPieCompleto.add(panelPie, BorderLayout.EAST);
        panelContenedorRaiz.add(panelPieCompleto, BorderLayout.SOUTH);
    }

    public void actualizarDatosAeronave(Avion av) {
        if (av == null) return;
        setTitle("Ficha Técnica y Especificaciones de Aeronave - " + av.obtenerModelo() + " (" + av.obtenerCodigoVueloAsignado() + ")");
        lblTitulo.setText("FICHA TÉCNICA OFICIAL: " + av.obtenerModelo().toUpperCase());
        lblSubtitulo.setText("Aeronave asignada al Vuelo " + av.obtenerCodigoVueloAsignado() + " (Ruta: Lima -> " + av.obtenerDestinoAsignado() + ") · Flota CORPAC S.A.");

        Image imgHD = cargarImagen(av.obtenerRutaFotoHD());
        if (imgHD == null) imgHD = cargarImagen("airbus_a320neo_hd.jpg");
        panelFotoHD.setImagen(imgHD, av.obtenerModelo() + " - VUELO " + av.obtenerCodigoVueloAsignado() + " (LIMA -> " + av.obtenerDestinoAsignado().toUpperCase() + ")");

        Image imgPlano = cargarImagen(av.obtenerRutaPlano());
        if (imgPlano == null) imgPlano = cargarImagen("airbus_a320neo_blueprint.jpg");
        panelPlano.setImagen(imgPlano, "PLANO TÉCNICO AERONÁUTICO (" + av.obtenerModelo() + ")");

        lblValVelocidad.setText(av.obtenerVelocidadCrucero() + " km/h (Mach " + String.format("%.2f", av.obtenerVelocidadCrucero() / 1062.0) + ")");
        lblValAlcance.setText(String.format("%,.0f km (Lima -> %s)", av.obtenerAlcanceMaximo(), av.obtenerDestinoAsignado()));
        lblValTecho.setText(String.format("%,d m (%,d pies)", av.obtenerTechoServicio(), (int)(av.obtenerTechoServicio() * 3.28084)));
        lblValDespegue.setText((av.obtenerModelo().contains("Dash") ? "1,400 m (Pistas cortas)" : "1,950 m (Apto para altura)"));

        lblValEnvergadura.setText(av.obtenerEnvergadura() + " m");
        lblValLongitud.setText(av.obtenerLongitud() + " m");
        lblValAltura.setText(av.obtenerAltura() + " m");
        lblValMTOW.setText(String.format("%,.0f kg", av.obtenerPesoMaximoDespegue()));

        lblValFabricante.setText(av.obtenerFabricante() + " / " + av.obtenerModelo());
        lblValMatricula.setText(av.obtenerMatricula() + " (Oficial CORPAC)");
        lblValMotores.setText(av.obtenerMotores());
        lblValEficiencia.setText(av.obtenerModelo().contains("Dash") ? "Alta eficiencia turboprop" : "-20% consumo de combustible");

        lblValCapacidad.setText(av.obtenerCapacidadReal() + " plazas comerciales");
        lblValCabina.setText(av.obtenerCapacidadSimulada() + " plazas (Matriz 10x5)");
        lblValZonificacion.setText("Ventana +30% | Pasillo +10%");
        lblValCert.setText("DGAC Perú / OACI Cat IIIa");

        txtDescripcion.setText(
                "Reseña Técnica y Operativa:\n" +
                av.obtenerResena() + "\n" +
                "Cabina modelada como matriz bidimensional 10x5 en memoria con despacho dinámico y tarifas zonales."
        );
        repaint();
    }

    private void seleccionarItemSegunCodigo(String cod) {
        if (cod == null || cod.isEmpty() || cbSelectorVuelo == null) return;
        for (int i = 0; i < cbSelectorVuelo.getItemCount(); i++) {
            String item = cbSelectorVuelo.getItemAt(i);
            if (item != null && item.startsWith(cod)) {
                cbSelectorVuelo.setSelectedIndex(i);
                return;
            }
        }
    }

    private String extraerCodigoDeItem(String item) {
        if (item == null) return "CP-2023";
        int idx = item.indexOf(':');
        return idx > 0 ? item.substring(0, idx).trim() : "CP-2023";
    }

    private String extraerDestinoDeItem(String item) {
        if (item == null) return "Cusco";
        try {
            int arrow = item.indexOf("->");
            if (arrow > 0) {
                int bracket = item.indexOf('[', arrow);
                if (bracket > 0) {
                    return item.substring(arrow + 2, bracket).trim();
                }
            }
        } catch (Exception ignored) {}
        return "Cusco";
    }

    private JPanel crearTarjetaSeccion(String titulo, Color colorTitulo, Color fondo, Color borde) {
        JPanel p = new JPanel(new GridLayout(4, 1, 4, 4));
        p.setBackground(fondo);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        new LineBorder(borde, 1, true),
                        titulo,
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 11),
                        colorTitulo
                ),
                new EmptyBorder(6, 12, 8, 12)
        ));
        return p;
    }

    private JPanel crearFila(String etiqueta, JLabel valorLabel, Color colorEtiqueta, Color colorValor) {
        JPanel panelFila = new JPanel(new BorderLayout(8, 0));
        panelFila.setOpaque(false);
        JLabel lblEtiq = new JLabel(etiqueta);
        lblEtiq.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblEtiq.setForeground(colorEtiqueta);

        valorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        valorLabel.setForeground(colorValor);

        panelFila.add(lblEtiq, BorderLayout.WEST);
        panelFila.add(valorLabel, BorderLayout.EAST);
        return panelFila;
    }

    private void estilizarBotonPill(JButton btn, boolean activo) {
        btn.setFont(new Font("Segoe UI", activo ? Font.BOLD : Font.PLAIN, 11));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (activo) {
            btn.setBackground(Theme.getPasajeroAccent());
            btn.setForeground(new Color(15, 23, 42));
            btn.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(Theme.getPasajeroAccent(), 1, true),
                    new EmptyBorder(5, 12, 5, 12)
            ));
        } else {
            btn.setBackground(Theme.getElevated());
            btn.setForeground(Theme.getTexto());
            btn.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(Theme.getBorder(), 1, true),
                    new EmptyBorder(5, 12, 5, 12)
            ));
        }
    }

    private void estilizarBotonSecundario(JButton btn, Color colorTexto, Color fondo) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        btn.setBackground(fondo);
        btn.setForeground(colorTexto);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Theme.getBorder(), 1, true),
                new EmptyBorder(3, 8, 3, 8)
        ));
    }

    private static class PanelVisorImagenHD extends JPanel {
        private static final long serialVersionUID = 1L;
        private Image imagen;
        private String tituloOverlay;

        public PanelVisorImagenHD(Image img, String titulo) {
            this.imagen = img;
            this.tituloOverlay = titulo;
            setOpaque(true);
            setBackground(new Color(11, 19, 41));
        }

        public void setImagen(Image img, String titulo) {
            this.imagen = img;
            this.tituloOverlay = titulo;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            int w = getWidth();
            int h = getHeight();

            if (imagen != null) {
                int imgW = imagen.getWidth(null);
                int imgH = imagen.getHeight(null);
                if (imgW > 0 && imgH > 0) {
                    double escala = Math.min((double) w / imgW, (double) h / imgH);
                    int drawW = (int) (imgW * escala);
                    int drawH = (int) (imgH * escala);
                    int x = (w - drawW) / 2;
                    int y = (h - drawH) / 2;

                    g2.drawImage(imagen, x, y, drawW, drawH, null);

                    g2.setColor(new Color(56, 189, 248, 60));
                    g2.drawRoundRect(x, y, drawW - 1, drawH - 1, 4, 4);
                }
            } else {
                g2.setColor(new Color(148, 163, 184));
                g2.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                String msg = "Cargando fotografía oficial de la aeronave...";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2);
            }

            if (tituloOverlay != null && !tituloOverlay.isEmpty()) {
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                FontMetrics fm = g2.getFontMetrics();
                int textW = fm.stringWidth(tituloOverlay);
                int tagW = textW + 24;
                int tagH = 22;

                g2.setColor(new Color(15, 23, 42, 215));
                g2.fillRoundRect(10, 10, tagW, tagH, 6, 6);
                g2.setColor(new Color(56, 189, 248, 160));
                g2.drawRoundRect(10, 10, tagW, tagH, 6, 6);

                g2.setColor(new Color(248, 250, 252));
                g2.drawString(tituloOverlay, 22, 10 + 15);
            }

            g2.dispose();
        }
    }
}
