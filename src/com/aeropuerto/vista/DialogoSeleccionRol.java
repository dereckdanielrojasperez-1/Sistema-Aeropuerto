// HUMANIZACION UI
package com.aeropuerto.vista;

import com.aeropuerto.controlador.ControladorAeropuerto;
import com.aeropuerto.modelo.Usuario;
import com.aeropuerto.vista.tema.AppIcon;
import com.aeropuerto.vista.tema.BotonPrimario;
import com.aeropuerto.vista.tema.FontManager;
import com.aeropuerto.vista.tema.GlassPanel;
import com.aeropuerto.vista.tema.Theme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

// Modal de seleccion de rol inicial (Pasajero vs Administrador)
public class DialogoSeleccionRol extends JDialog implements Theme.ThemeListener {

    private static final long serialVersionUID = 1L;

    private final Frame marcoPadre;
    private final ControladorAeropuerto controlador;
    private String rolSeleccionado = "PASAJERO";

    private BufferedImage fondoBlur;

    // Componentes interactivos
    private JTextField txtUsuarioAdmin;
    private JPasswordField txtClaveAdmin;
    private JLabel btnAlternarMostrarClave;
    private boolean claveVisible = false;
    private JButton btnAlternarTema;

    public DialogoSeleccionRol(Frame padre, ControladorAeropuerto controlador) {
        super(padre, true);
        this.marcoPadre = padre;
        this.controlador = controlador;

        Theme.addThemeListener(this);
        prepararOverlayYVentana();
        construirInterfaz();
    }

    private void prepararOverlayYVentana() {
        if (marcoPadre != null && marcoPadre.isShowing()) {
            setUndecorated(true);
            setBounds(marcoPadre.getBounds());
            capturarFondoBlur();
        } else {
            setTitle("Sistema de Aeropuerto - CORPAC");
            setSize(900, 620);
            setLocationRelativeTo(null);
        }
        setBackground(new Color(0, 0, 0, 0));
    }

    private void capturarFondoBlur() {
        if (marcoPadre == null || !marcoPadre.isShowing()) return;
        int anchoVentana = marcoPadre.getWidth();
        int altoVentana = marcoPadre.getHeight();
        if (anchoVentana <= 10 || altoVentana <= 10) return;

        try {
            // 1. Capturar ventana padre
            BufferedImage imagenOriginal = new BufferedImage(anchoVentana, altoVentana, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = imagenOriginal.createGraphics();
            marcoPadre.paint(g2);
            g2.dispose();

            // 2. Downsample a 1/6 para difuminado suave
            int anchoReducido = Math.max(16, anchoVentana / 6);
            int altoReducido = Math.max(16, altoVentana / 6);
            BufferedImage imagenReducida = new BufferedImage(anchoReducido, altoReducido, BufferedImage.TYPE_INT_RGB);
            Graphics2D gReducido = imagenReducida.createGraphics();
            gReducido.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            gReducido.drawImage(imagenOriginal, 0, 0, anchoReducido, altoReducido, null);
            gReducido.dispose();

            // 3. Upsample con interpolacion bilineal
            BufferedImage imagenDifuminada = new BufferedImage(anchoVentana, altoVentana, BufferedImage.TYPE_INT_RGB);
            Graphics2D gDifuminado = imagenDifuminada.createGraphics();
            gDifuminado.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            gDifuminado.drawImage(imagenReducida, 0, 0, anchoVentana, altoVentana, null);

            // 4. Tinte acrilico segun tema activo (evita negro solido)
            Color tinte = Theme.esModoOscuro() ? new Color(13, 17, 23, 190) : new Color(244, 246, 248, 200);
            gDifuminado.setColor(tinte);
            gDifuminado.fillRect(0, 0, anchoVentana, altoVentana);
            gDifuminado.dispose();

            this.fondoBlur = imagenDifuminada;
        } catch (Exception ignored) {
            this.fondoBlur = null;
        }
    }

    private void construirInterfaz() {
        // Panel contenedor que pinta el overlay blur
        JPanel panelOverlay = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                if (fondoBlur != null) {
                    g2.drawImage(fondoBlur, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2.setColor(Theme.esModoOscuro() ? new Color(13, 17, 23, 210) : new Color(244, 246, 248, 215));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                g2.dispose();
            }
        };
        panelOverlay.setOpaque(false);

        // Tarjeta Modal Central Glassmorphism con bordes redondeados y profundidad por capas
        GlassPanel tarjetaModal = new GlassPanel(new BorderLayout(0, 0), 20);
        tarjetaModal.setPreferredSize(new Dimension(870, 580));
        tarjetaModal.setBorder(new EmptyBorder(20, 26, 16, 26));

        // 1. Cabecera del modal
        tarjetaModal.add(crearCabeceraModal(), BorderLayout.NORTH);

        // 2. Cuerpo con 2 tarjetas de jerarquia distinta
        JPanel panelTarjetas = new JPanel(new GridLayout(1, 2, 22, 0));
        panelTarjetas.setOpaque(false);
        panelTarjetas.setBorder(new EmptyBorder(16, 0, 16, 0));
        panelTarjetas.add(crearTarjetaPasajero());
        panelTarjetas.add(crearTarjetaAdmin());
        tarjetaModal.add(panelTarjetas, BorderLayout.CENTER);

        // 3. Pie del modal
        tarjetaModal.add(crearPieModal(), BorderLayout.SOUTH);

        // Centrar tarjeta modal en el overlay
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        panelOverlay.add(tarjetaModal, gbc);

        // Atajo teclado ESC para cerrar
        panelOverlay.registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        setContentPane(panelOverlay);
    }

    private JPanel crearCabeceraModal() {
        JPanel panelCabecera = new JPanel(new BorderLayout(14, 0));
        panelCabecera.setOpaque(false);
        panelCabecera.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.getBorder()),
                new EmptyBorder(0, 4, 14, 4)));

        // Lado Izquierdo: Logo CORPAC + Titulos institucionales
        JPanel panelIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panelIzquierdo.setOpaque(false);

        // Logo oficial CORPAC
        JLabel lblLogo = new JLabel();
        ImageIcon icoLogo = obtenerLogoCorpac(96, 26);
        if (icoLogo != null) lblLogo.setIcon(icoLogo);
        panelIzquierdo.add(lblLogo);

        JPanel panelTitulos = new JPanel(new GridLayout(2, 1, 0, 2));
        panelTitulos.setOpaque(false);

        // Titulo SIN emoji en Barlow Condensed
        JLabel lblTitulo = new JLabel("Sistema de Gestion Aeroportuaria · CORPAC");
        lblTitulo.setFont(FontManager.getBarlowCondensed(Font.BOLD, 19f));
        lblTitulo.setForeground(Theme.getTexto());

        JLabel lblSub = new JLabel("Seleccione su perfil de acceso para ingresar a la plataforma");
        lblSub.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 12f));
        lblSub.setForeground(Theme.getSecundario());

        panelTitulos.add(lblTitulo);
        panelTitulos.add(lblSub);
        panelIzquierdo.add(panelTitulos);
        panelCabecera.add(panelIzquierdo, BorderLayout.WEST);

        // Lado Derecho: Toggle Claro/Oscuro y Boton Salir
        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        panelDerecho.setOpaque(false);

        // Boton de salida discreto
        JButton btnCerrar = new JButton("Salir");
        btnCerrar.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
        btnCerrar.setForeground(Theme.getSecundario());
        btnCerrar.setBackground(Theme.getSurface());
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.setBorder(new CompoundBorder(new LineBorder(Theme.getBorder(), 1), new EmptyBorder(4, 10, 4, 10)));
        btnCerrar.addActionListener(e -> System.exit(0));
        panelDerecho.add(btnCerrar);

        panelCabecera.add(panelDerecho, BorderLayout.EAST);
        return panelCabecera;
    }

    // Tarjeta de acceso para Pasajero en GlassPanel translúcido con acento Cyan
    private JPanel crearTarjetaPasajero() {
        GlassPanel panelTarjetaPasajero = new GlassPanel(new BorderLayout(0, 10), 16);
        panelTarjetaPasajero.setColorAcento(Theme.getPasajeroAccent());
        panelTarjetaPasajero.setBorder(new EmptyBorder(16, 18, 16, 18));

        // Encabezado de la tarjeta
        JPanel panelNorte = new JPanel(new BorderLayout(10, 0));
        panelNorte.setOpaque(false);

        // Badge circular de icono de avion
        JLabel lblIconoAvion = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.getPasajeroSoft());
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblIconoAvion.setPreferredSize(new Dimension(38, 38));
        lblIconoAvion.setHorizontalAlignment(SwingConstants.CENTER);
        lblIconoAvion.setIcon(AppIcon.plane(18, Theme.getPasajeroAccent()));
        panelNorte.add(lblIconoAvion, BorderLayout.WEST);

        JPanel panelTitulos = new JPanel(new GridLayout(2, 1, 0, 2));
        panelTitulos.setOpaque(false);
        JLabel lblRol = new JLabel("PORTAL DEL PASAJERO");
        lblRol.setFont(FontManager.getBarlowCondensed(Font.BOLD, 17f));
        lblRol.setForeground(Theme.getTexto());

        JLabel lblSub = new JLabel("Acceso para clientes y viajeros");
        lblSub.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 12f));
        lblSub.setForeground(Theme.getSecundario());

        panelTitulos.add(lblRol);
        panelTitulos.add(lblSub);
        panelNorte.add(panelTitulos, BorderLayout.CENTER);
        panelTarjetaPasajero.add(panelNorte, BorderLayout.NORTH);

        // Lista de caracteristicas con bullets vectoriales SVG
        JPanel panelCaracteristicas = new JPanel(new GridLayout(5, 1, 0, 7));
        panelCaracteristicas.setOpaque(false);
        panelCaracteristicas.setBorder(new EmptyBorder(14, 4, 14, 4));

        panelCaracteristicas.add(crearFilaFeatureSvg("Selección de vuelo comercial y destino"));
        panelCaracteristicas.add(crearFilaFeatureSvg("Elección interactiva de asiento - VIP o Turista"));
        panelCaracteristicas.add(crearFilaFeatureSvg("Ficha técnica interactiva del Airbus A320neo"));
        panelCaracteristicas.add(crearFilaFeatureSvg("Facturación de equipaje y medidor IATA"));
        panelCaracteristicas.add(crearFilaFeatureSvg("Emisión de pase de abordar digital y puerta"));

        panelTarjetaPasajero.add(panelCaracteristicas, BorderLayout.CENTER);

        // BotonPrimario en tono azul de pasajero
        BotonPrimario btnIngresarPasajero = new BotonPrimario("INGRESAR COMO PASAJERO", Theme.getPasajeroAccent());
        btnIngresarPasajero.setPreferredSize(new Dimension(220, 40));
        btnIngresarPasajero.addActionListener(e -> seleccionarModoPasajero());
        panelTarjetaPasajero.add(btnIngresarPasajero, BorderLayout.SOUTH);

        return panelTarjetaPasajero;
    }

    // Tarjeta de acceso administrativo con autenticación en GlassPanel translúcido con acento Ámbar
    private JPanel crearTarjetaAdmin() {
        GlassPanel panelTarjetaAdmin = new GlassPanel(new BorderLayout(0, 10), 16);
        panelTarjetaAdmin.setColorAcento(Theme.getAdminAccent());
        panelTarjetaAdmin.setBorder(new EmptyBorder(16, 18, 16, 18));

        // Encabezado Admin: Icono + Titulos + Badge ACCESO RESTRINGIDO
        JPanel panelNorte = new JPanel(new BorderLayout(10, 4));
        panelNorte.setOpaque(false);

        // Fila superior con icono y badge
        JPanel panelFilaSuperior = new JPanel(new BorderLayout());
        panelFilaSuperior.setOpaque(false);

        // Icono Radar / Shield ambar
        JLabel lblIconoRadar = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.getAdminSoft());
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblIconoRadar.setPreferredSize(new Dimension(38, 38));
        lblIconoRadar.setHorizontalAlignment(SwingConstants.CENTER);
        lblIconoRadar.setVerticalAlignment(SwingConstants.CENTER);
        lblIconoRadar.setIcon(AppIcon.shield(18, Theme.getAdminAccent()));
        panelFilaSuperior.add(lblIconoRadar, BorderLayout.WEST);

        // Sello discreto tipo badge "ACCESO RESTRINGIDO"
        JLabel lblBadge = new JLabel(" ACCESO RESTRINGIDO ");
        lblBadge.setFont(FontManager.getIBMPlexMono(Font.BOLD, 10f));
        lblBadge.setForeground(Theme.getAdminAccent());
        lblBadge.setOpaque(true);
        lblBadge.setBackground(Theme.getAdminSoft());
        lblBadge.setBorder(new CompoundBorder(new LineBorder(Theme.getAdminAccent(), 1), new EmptyBorder(2, 6, 2, 6)));
        panelFilaSuperior.add(lblBadge, BorderLayout.EAST);
        panelNorte.add(panelFilaSuperior, BorderLayout.NORTH);

        JPanel panelTitulos = new JPanel(new GridLayout(2, 1, 0, 2));
        panelTitulos.setOpaque(false);
        JLabel lblRol = new JLabel("CENTRO DE CONTROL ATC - ADMINISTRADOR");
        lblRol.setFont(FontManager.getBarlowCondensed(Font.BOLD, 17f));
        lblRol.setForeground(Theme.getTexto());

        JLabel lblSub = new JLabel("Operaciones aeroportuarias y despacho aéreo");
        lblSub.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 12f));
        lblSub.setForeground(Theme.getSecundario());

        panelTitulos.add(lblRol);
        panelTitulos.add(lblSub);
        panelNorte.add(panelTitulos, BorderLayout.CENTER);
        panelTarjetaAdmin.add(panelNorte, BorderLayout.NORTH);

        // SUB-CARD ELEVADA para campos Usuario y Contrasena
        JPanel panelSubTarjeta = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int ancho = getWidth();
                int alto = getHeight();

                // Sombra sutil pintada
                g2.setColor(new Color(0, 0, 0, Theme.esModoOscuro() ? 40 : 15));
                g2.fillRoundRect(2, 2, ancho - 4, alto - 4, 10, 10);

                // Fondo elevated
                g2.setColor(Theme.getElevated());
                g2.fillRoundRect(0, 0, ancho - 1, alto - 1, 10, 10);

                // Borde suave
                g2.setColor(Theme.getBorder());
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, ancho - 1, alto - 1, 10, 10);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        panelSubTarjeta.setOpaque(false);
        panelSubTarjeta.setBorder(new EmptyBorder(10, 14, 10, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 4, 3, 4);

        // Campo Usuario
        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setFont(FontManager.getIBMPlexSans(Font.BOLD, 11f));
        lblUser.setForeground(Theme.getSecundario());
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        panelSubTarjeta.add(lblUser, gbc);

        txtUsuarioAdmin = new JTextField("admin", 12);
        estilizarCampoConFocusRing(txtUsuarioAdmin, Theme.getAdminAccent());
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        panelSubTarjeta.add(txtUsuarioAdmin, gbc);

        // Fila Contrasena con link "Mostrar"
        JPanel panelCabeceraClave = new JPanel(new BorderLayout());
        panelCabeceraClave.setOpaque(false);
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setFont(FontManager.getIBMPlexSans(Font.BOLD, 11f));
        lblPass.setForeground(Theme.getSecundario());
        panelCabeceraClave.add(lblPass, BorderLayout.WEST);

        // Link "Mostrar" como toggle interactivo color accent legible
        btnAlternarMostrarClave = new JLabel("Mostrar");
        btnAlternarMostrarClave.setFont(FontManager.getIBMPlexSans(Font.BOLD, 11f));
        btnAlternarMostrarClave.setForeground(Theme.getAdminAccent());
        btnAlternarMostrarClave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAlternarMostrarClave.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                claveVisible = !claveVisible;
                if (claveVisible) {
                    txtClaveAdmin.setEchoChar((char) 0);
                    btnAlternarMostrarClave.setText("Ocultar");
                } else {
                    txtClaveAdmin.setEchoChar('-');
                    btnAlternarMostrarClave.setText("Mostrar");
                }
            }
        });
        panelCabeceraClave.add(btnAlternarMostrarClave, BorderLayout.EAST);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        panelSubTarjeta.add(panelCabeceraClave, gbc);

        txtClaveAdmin = new JPasswordField("admin123", 12);
        estilizarCampoConFocusRing(txtClaveAdmin, Theme.getAdminAccent());
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        panelSubTarjeta.add(txtClaveAdmin, gbc);

        // Hint discreto con Theme.muted (NO rojo neon)
        JLabel lblHint = new JLabel("Credenciales sugeridas: admin / admin123", SwingConstants.CENTER);
        lblHint.setFont(FontManager.getIBMPlexMono(Font.PLAIN, 10f));
        lblHint.setForeground(Theme.getMuted());
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(6, 4, 2, 4);
        panelSubTarjeta.add(lblHint, gbc);

        // Agrupar centro de la tarjeta Admin
        JPanel panelCentroAdmin = new JPanel(new BorderLayout(0, 8));
        panelCentroAdmin.setOpaque(false);
        panelCentroAdmin.add(panelSubTarjeta, BorderLayout.CENTER);

        JLabel lblCapacidades = new JLabel("Acceso a Radar 3D, supervisión de pistas y base de datos SQL.", SwingConstants.CENTER);
        lblCapacidades.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 10f));
        lblCapacidades.setForeground(Theme.getMuted());
        panelCentroAdmin.add(lblCapacidades, BorderLayout.SOUTH);
        panelTarjetaAdmin.add(panelCentroAdmin, BorderLayout.CENTER);

        // BotonPrimario en tono ambar de administrador
        BotonPrimario btnIngresarAdmin = new BotonPrimario("INICIAR SESIÓN COMO ADMIN", Theme.getAdminAccent());
        btnIngresarAdmin.setPreferredSize(new Dimension(220, 40));
        btnIngresarAdmin.addActionListener(e -> autenticarComoAdmin());
        panelTarjetaAdmin.add(btnIngresarAdmin, BorderLayout.SOUTH);

        return panelTarjetaAdmin;
    }

    private JPanel crearFilaFeatureSvg(String texto) {
        JPanel panelFila = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelFila.setOpaque(false);

        JLabel lblBullet = new JLabel(AppIcon.bulletSvg(13, Theme.getPasajeroAccent()));
        JLabel lblTexto = new JLabel(texto);
        lblTexto.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 12f));
        lblTexto.setForeground(Theme.getTexto());

        panelFila.add(lblBullet);
        panelFila.add(lblTexto);
        return panelFila;
    }

    private void estilizarCampoConFocusRing(JTextField campo, Color colorAccent) {
        campo.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 12f));
        campo.setBackground(Theme.getInput());
        campo.setForeground(Theme.getTexto());
        campo.setCaretColor(colorAccent);
        campo.setBorder(new CompoundBorder(new LineBorder(Theme.getBorder(), 1), new EmptyBorder(4, 8, 4, 8)));

        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                campo.setBorder(new CompoundBorder(new LineBorder(colorAccent, 1), new EmptyBorder(4, 8, 4, 8)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                campo.setBorder(new CompoundBorder(new LineBorder(Theme.getBorder(), 1), new EmptyBorder(4, 8, 4, 8)));
            }
        });
    }

    private JPanel crearPieModal() {
        JPanel pie = new JPanel(new BorderLayout(12, 0));
        pie.setOpaque(false);
        pie.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.getBorder()),
                new EmptyBorder(8, 4, 4, 4)));

        JLabel lblPie = new JLabel("Sistema Integrado de Gestion Aeroportuaria - CORPAC S.A.", SwingConstants.CENTER);
        lblPie.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
        lblPie.setForeground(Theme.getMuted());

        pie.add(lblPie, BorderLayout.CENTER);
        return pie;
    }

    private ImageIcon obtenerLogoCorpac(int w, int h) {
        try {
            java.net.URL urlLogo = getClass().getResource("/com/aeropuerto/vista/logo_corpac.png");
            if (urlLogo == null) urlLogo = getClass().getResource("logo_corpac.png");
            Image img = null;
            if (urlLogo != null) {
                img = new ImageIcon(urlLogo).getImage();
            } else {
                java.io.File f = new java.io.File("src/com/aeropuerto/vista/logo_corpac.png");
                if (f.exists()) img = new ImageIcon(f.getAbsolutePath()).getImage();
            }
            if (img != null) {
                return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void actualizarTextosTema() {
        if (btnAlternarTema != null) {
            btnAlternarTema.setText(Theme.esModoOscuro() ? "Modo Claro" : "Modo Oscuro");
        }
        if (btnAlternarMostrarClave != null) {
            btnAlternarMostrarClave.setForeground(Theme.getAdminAccent());
        }
    }

    @Override
    public void onThemeChanged() {
        capturarFondoBlur();
        actualizarTextosTema();
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    private void seleccionarModoPasajero() {
        Theme.setRolActivo("PASAJERO");
        if (controlador != null) {
            controlador.iniciarSesionPasajero();
        }
        this.rolSeleccionado = "PASAJERO";
        Theme.removeThemeListener(this);
        dispose();
    }

    private void autenticarComoAdmin() {
        String usuario = (txtUsuarioAdmin != null) ? txtUsuarioAdmin.getText().trim() : "admin";
        String clave = (txtClaveAdmin != null) ? new String(txtClaveAdmin.getPassword()).trim() : "admin123";

        if (usuario.isEmpty() || clave.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingrese usuario y contraseña.",
                    "Datos Requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario u = controlador != null ? controlador.autenticarUsuario(usuario, clave) : null;
        if (u != null && u.esAdministrador()) {
            Theme.setRolActivo("ADMIN");
            this.rolSeleccionado = "ADMIN";
            Theme.removeThemeListener(this);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Credenciales incorrectas para el perfil Administrador.\n" +
                    "Verifique el usuario y contraseña (por defecto: admin / admin123).",
                    "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
            if (txtClaveAdmin != null) {
                txtClaveAdmin.setText("");
                txtClaveAdmin.requestFocus();
            }
        }
    }

    public String obtenerRolSeleccionado() {
        return rolSeleccionado;
    }

    public boolean esRolAdmin() {
        return "ADMIN".equalsIgnoreCase(rolSeleccionado);
    }
}
