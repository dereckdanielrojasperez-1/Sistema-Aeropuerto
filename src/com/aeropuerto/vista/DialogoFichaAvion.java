package com.aeropuerto.vista;

import com.aeropuerto.controlador.ControladorAeropuerto;
import com.aeropuerto.modelo.Avion;
import com.aeropuerto.vista.tema.Theme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyEvent;

// Ficha técnica oficial y especificaciones aeronáuticas del Airbus A320neo (CORPAC S.A.)
public class DialogoFichaAvion extends JDialog {
    private static final long serialVersionUID = 1L;

    private CardLayout layoutVisualizador;
    private JPanel panelTarjetasVisual;
    private JButton btnVerFoto;
    private JButton btnVerPlano;
    private JButton btnVer3D;

    public DialogoFichaAvion(JFrame ventanaPadre, ControladorAeropuerto controlador) {
        super(ventanaPadre, "Ficha Técnica y Especificaciones de Aeronave - CORPAC / Airbus A320neo", false);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets insets = null;
        try {
            insets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        } catch (Exception ignored) {}
        int maxH = (insets != null) ? screenSize.height - insets.top - insets.bottom : screenSize.height - 50;
        int maxW = (insets != null) ? screenSize.width - insets.left - insets.right : screenSize.width - 40;

        int dialogW = Math.min(940, maxW - 20);
        int dialogH = Math.min(720, maxH - 20);
        setSize(dialogW, dialogH);
        setLocationRelativeTo(ventanaPadre);
        setResizable(true);

        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        configurarIcono();
        inicializarComponentes(controlador.obtenerAvion());
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

    private void inicializarComponentes(Avion avion) {
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
        // 1. Encabezado Institucional CORPAC
        // =========================================================================
        JPanel panelEncabezado = new JPanel(new BorderLayout(14, 0));
        panelEncabezado.setBackground(fondoHeader);
        panelEncabezado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, bordeTarjeta),
                new EmptyBorder(12, 18, 12, 18)
        ));

        JPanel panelTitulos = new JPanel(new GridLayout(2, 1, 0, 2));
        panelTitulos.setOpaque(false);

        JLabel lblTitulo = new JLabel("FICHA TÉCNICA OFICIAL: AIRBUS A320neo - MODELO A320-271N", JLabel.LEFT);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitulo.setForeground(doradoSmart);

        JLabel lblSubtitulo = new JLabel("Aeronave comercial de corto y medio alcance · Flota Oficial CORPAC S.A.", JLabel.LEFT);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(textoGris);

        panelTitulos.add(lblTitulo);
        panelTitulos.add(lblSubtitulo);
        panelEncabezado.add(panelTitulos, BorderLayout.CENTER);

        Image imgLogo = cargarImagen("logo_corpac.png");
        if (imgLogo != null) {
            JLabel lblLogo = new JLabel(new ImageIcon(imgLogo.getScaledInstance(160, 42, Image.SCALE_SMOOTH)));
            panelEncabezado.add(lblLogo, BorderLayout.EAST);
        }

        panelContenedorRaiz.add(panelEncabezado, BorderLayout.NORTH);

        // =========================================================================
        // 2. Contenido Central Desplazable (Evita recortes o solapamiento)
        // =========================================================================
        JPanel panelCuerpo = new JPanel();
        panelCuerpo.setLayout(new BoxLayout(panelCuerpo, BoxLayout.Y_AXIS));
        panelCuerpo.setBackground(fondoOscuro);
        panelCuerpo.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Barra de Control Superior para Alternar Vistas del Avión
        JPanel panelBarraVistas = new JPanel(new BorderLayout(10, 0));
        panelBarraVistas.setOpaque(false);
        panelBarraVistas.setMaximumSize(new Dimension(32767, 34));

        JLabel lblModoVista = new JLabel("Modo de Visualización de Aeronave:");
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

        // =========================================================================
        // Contenedor de Vistas Alternables (CardLayout)
        // =========================================================================
        layoutVisualizador = new CardLayout();
        panelTarjetasVisual = new JPanel(layoutVisualizador);
        panelTarjetasVisual.setBackground(new Color(11, 19, 41));
        panelTarjetasVisual.setBorder(new LineBorder(bordeTarjeta, 1, true));
        panelTarjetasVisual.setPreferredSize(new Dimension(890, 310));
        panelTarjetasVisual.setMaximumSize(new Dimension(32767, 320));

        // Vista 1: Fotografía HD en Vuelo
        Image imgHD = cargarImagen("airbus_a320neo_hd.jpg");
        if (imgHD == null) imgHD = cargarImagen("avion_a320.jpg");
        if (imgHD == null) imgHD = cargarImagen("avion_a320.png");
        PanelVisorImagenHD panelFotoHD = new PanelVisorImagenHD(imgHD, "AIRBUS A320neo - VUELO ANDINO CORPAC");
        panelTarjetasVisual.add(panelFotoHD, "FOTO");

        // Vista 2: Plano Técnico Esquematico (CAD Blueprint)
        Image imgBlueprint = cargarImagen("airbus_a320neo_blueprint.jpg");
        PanelVisorImagenHD panelPlano = new PanelVisorImagenHD(imgBlueprint != null ? imgBlueprint : imgHD, "PLANO TÉCNICO AERONÁUTICO (COTAS Y MOTORES LEAP-1A)");
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

        // Eventos de los botones de selector de vistas
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
        panelRendimiento.add(crearFila("Velocidad Crucero:", "828 km/h (Mach 0.78)", textoGris, azulCyan));
        panelRendimiento.add(crearFila("Alcance Máximo:", "6,500 km (Rutas en Perú sin escalas)", textoGris, azulCyan));
        panelRendimiento.add(crearFila("Techo de Operación:", "12,100 m (39,800 pies)", textoGris, azulCyan));
        panelRendimiento.add(crearFila("Carrera de Despegue:", "1,950 m (Apto para Cuzco/Juliaca)", textoGris, azulCyan));
        panelCuadricula.add(panelRendimiento);

        // Cuadro 2: Dimensiones y Geometría
        JPanel panelDimensiones = crearTarjetaSeccion("Dimensiones y Pesos", doradoSmart, fondoTarjeta, bordeTarjeta);
        panelDimensiones.add(crearFila("Envergadura (Alas):", "35.80 metros (con Sharklets)", textoGris, textoBlanco));
        panelDimensiones.add(crearFila("Longitud Total:", "37.57 metros", textoGris, textoBlanco));
        panelDimensiones.add(crearFila("Altura de Cola:", "11.76 metros", textoGris, textoBlanco));
        panelDimensiones.add(crearFila("Peso Máx. Despegue (MTOW):", "79,000 kg", textoGris, textoBlanco));
        panelCuadricula.add(panelDimensiones);

        // Cuadro 3: Propulsión y Eficiencia
        JPanel panelPropulsion = crearTarjetaSeccion("Propulsión y Planta de Poder", doradoSmart, fondoTarjeta, bordeTarjeta);
        panelPropulsion.add(crearFila("Fabricante / Modelo:", "Airbus SAS / A320-271N", textoGris, textoBlanco));
        panelPropulsion.add(crearFila("Motores Turbofán:", "2x CFM LEAP-1A26 (27,120 lbf c/u)", textoGris, textoBlanco));
        panelPropulsion.add(crearFila("Eficiencia de Combustible:", "-20% consumo frente a generación previa", textoGris, verdeAcento));
        panelPropulsion.add(crearFila("Huella Acústica:", "-50% emisión sonora en aproximación", textoGris, verdeAcento));
        panelCuadricula.add(panelPropulsion);

        // Cuadro 4: Capacidad y Certificación
        JPanel panelCapacidad = crearTarjetaSeccion("Capacidad y Homologación", doradoSmart, fondoTarjeta, bordeTarjeta);
        panelCapacidad.add(crearFila("Capacidad Comercial:", "180 pasajeros en clase mixta", textoGris, textoBlanco));
        panelCapacidad.add(crearFila("Matriz de Cabina CORPAC:", "50 plazas activas (10 filas x 5 col)", textoGris, azulCyan));
        panelCapacidad.add(crearFila("Diferenciación Tarifaria:", "Ventana (+30%), Pasillo (+10%), Centro", textoGris, doradoSmart));
        panelCapacidad.add(crearFila("Certificaciones Oficiales:", "DGAC Perú / OACI Cat IIIa / EASA / FAA", textoGris, textoBlanco));
        panelCuadricula.add(panelCapacidad);

        panelCuerpo.add(panelCuadricula);
        panelCuerpo.add(Box.createVerticalStrut(10));

        // =========================================================================
        // 4. Reseña Técnica Oficial
        // =========================================================================
        JTextArea txtDescripcion = new JTextArea();
        txtDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setEditable(false);
        txtDescripcion.setBackground(fondoTarjeta);
        txtDescripcion.setForeground(textoBlanco);
        txtDescripcion.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(bordeTarjeta, 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        txtDescripcion.setText(
                "Reseña Técnica y Operativa:\n" +
                "El Airbus A320neo es la aeronave insignia de CORPAC para el transporte interdepartamental en el Perú. " +
                "Equipado con dispositivos Sharklets de alta eficiencia aerodinámica y reactores CFM LEAP-1A con relación de derivación 11:1, " +
                "ofrece rendimiento sobresaliente en pistas de altura como el Aeropuerto Alejandro Velasco Astete del Cusco (3,310 msnm) y Juliaca (3,825 msnm). " +
                "Su cabina se encuentra modelada como una matriz bidimensional 10x5 en memoria con despacho dinámico de asientos y tarifas zonales."
        );
        txtDescripcion.setMaximumSize(new Dimension(32767, 95));
        panelCuerpo.add(txtDescripcion);

        // Envolver todo el cuerpo en ScrollPane para garantizar visualización perfecta en cualquier resolución
        JScrollPane scrollPrincipal = new JScrollPane(panelCuerpo);
        scrollPrincipal.setBorder(null);
        scrollPrincipal.getVerticalScrollBar().setUnitIncrement(16);
        scrollPrincipal.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        panelContenedorRaiz.add(scrollPrincipal, BorderLayout.CENTER);

        // =========================================================================
        // 5. Barra Inferior con Botón de Cierre
        // =========================================================================
        JPanel panelPie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        panelPie.setBackground(fondoHeader);
        panelPie.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, bordeTarjeta),
                new EmptyBorder(4, 10, 4, 10)
        ));

        JLabel lblInfoPie = new JLabel("CORPAC S.A. · Gerencia de Operaciones Aeronáuticas · Modelo A320neo");
        lblInfoPie.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblInfoPie.setForeground(textoGris);

        JPanel pInfoWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        pInfoWrapper.setOpaque(false);
        pInfoWrapper.add(lblInfoPie);

        JPanel panelPieCompleto = new JPanel(new BorderLayout());
        panelPieCompleto.setOpaque(false);
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

    private JPanel crearFila(String etiqueta, String valor, Color colorEtiqueta, Color colorValor) {
        JPanel panelFila = new JPanel(new BorderLayout(8, 0));
        panelFila.setOpaque(false);
        JLabel lblEtiq = new JLabel(etiqueta);
        lblEtiq.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblEtiq.setForeground(colorEtiqueta);

        JLabel lblVal = new JLabel(valor, JLabel.RIGHT);
        lblVal.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblVal.setForeground(colorValor);

        panelFila.add(lblEtiq, BorderLayout.WEST);
        panelFila.add(lblVal, BorderLayout.EAST);
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

    // Panel especializado para visualización de imágenes de aeronave con renderizado de alta calidad
    private static class PanelVisorImagenHD extends JPanel {
        private static final long serialVersionUID = 1L;
        private final Image imagen;
        private final String tituloOverlay;

        public PanelVisorImagenHD(Image img, String titulo) {
            this.imagen = img;
            this.tituloOverlay = titulo;
            setOpaque(true);
            setBackground(new Color(11, 19, 41));
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
                String msg = "Imagen oficial Airbus A320neo en proceso de carga...";
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
