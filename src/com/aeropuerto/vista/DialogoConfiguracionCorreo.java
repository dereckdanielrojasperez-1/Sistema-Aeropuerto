package com.aeropuerto.vista;

import com.aeropuerto.servicio.ServicioCorreo;
import com.aeropuerto.vista.tema.FontManager;
import com.aeropuerto.vista.tema.GlassPanel;
import com.aeropuerto.vista.tema.Theme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Diálogo interactivo para configuración del Servidor de Correo Electrónico (Gmail SMTP SSL).
 * Permite al usuario/evaluador ingresar sus credenciales oficiales o App Password
 * para envíos automatizados directos de Vouchers y Boletas Electrónicas a Gmail.
 */
public class DialogoConfiguracionCorreo extends JDialog {

    private static final long serialVersionUID = 1L;

    private JTextField txtServidor;
    private JTextField txtPuerto;
    private JTextField txtNombreRemitente;
    private JTextField txtRemitente;
    private JPasswordField txtPassword;
    private JCheckBox chkMostrarPassword;
    private JLabel lblEstado;
    private JButton btnProbar;
    private JButton btnGuardar;
    private JButton btnCerrar;

    public DialogoConfiguracionCorreo(Frame padre) {
        super(padre, "CORPAC - Configuración de Servidor de Correo (Gmail SMTP)", true);
        inicializarComponentes();
        pack();
        setResizable(false);
        setLocationRelativeTo(padre);
    }

    private void inicializarComponentes() {
        ServicioCorreo srv = ServicioCorreo.obtenerInstancia();

        JPanel pRaiz = new JPanel(new BorderLayout());
        pRaiz.setBackground(Theme.getBg());
        pRaiz.setBorder(new LineBorder(Theme.getBorder(), 1));

        // 1. Encabezado
        JPanel pHeader = new JPanel(new GridLayout(2, 1, 0, 4));
        pHeader.setBackground(Theme.getSurface());
        pHeader.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTitulo = new JLabel("CONFIGURACIÓN DE SERVIDOR SMTP (GMAIL)", SwingConstants.CENTER);
        lblTitulo.setFont(FontManager.getIBMPlexSans(Font.BOLD, 13f));
        lblTitulo.setForeground(Theme.getPasajeroAccent());

        JLabel lblSub = new JLabel("CORPAC AIRWAYS - Despacho Digital de Vouchers y Boletas a Bandeja de Entrada", SwingConstants.CENTER);
        lblSub.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
        lblSub.setForeground(Theme.getSecundario());

        pHeader.add(lblTitulo);
        pHeader.add(lblSub);
        pRaiz.add(pHeader, BorderLayout.NORTH);

        // 2. Contenido Central (Campos)
        JPanel pCentro = new JPanel(new GridBagLayout());
        pCentro.setOpaque(false);
        pCentro.setBorder(new EmptyBorder(16, 20, 12, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0: Servidor SMTP y Puerto
        JLabel lblHost = crearEtiquetaCampo("Servidor SMTP:");
        txtServidor = crearCampoTexto(srv.getServidorConfigurado());
        txtServidor.setPreferredSize(new Dimension(170, 28));

        JLabel lblPort = crearEtiquetaCampo("Puerto SSL:");
        txtPuerto = crearCampoTexto(String.valueOf(srv.getPuertoConfigurado()));
        txtPuerto.setPreferredSize(new Dimension(70, 28));

        JPanel pHostPort = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pHostPort.setOpaque(false);
        pHostPort.add(txtServidor);
        pHostPort.add(lblPort);
        pHostPort.add(txtPuerto);

        gbc.gridx = 0; gbc.gridy = 0;
        pCentro.add(lblHost, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        pCentro.add(pHostPort, gbc);

        // Fila 1: Nombre Remitente
        JLabel lblNombre = crearEtiquetaCampo("Nombre Remitente:");
        txtNombreRemitente = crearCampoTexto(srv.getNombreRemitenteConfigurado());
        txtNombreRemitente.setPreferredSize(new Dimension(320, 28));

        gbc.gridx = 0; gbc.gridy = 1;
        pCentro.add(lblNombre, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        pCentro.add(txtNombreRemitente, gbc);

        // Fila 2: Correo Remitente Gmail
        JLabel lblUser = crearEtiquetaCampo("Correo Gmail Remitente:");
        txtRemitente = crearCampoTexto(srv.getRemitenteConfigurado());
        txtRemitente.setToolTipText("Ej: tunombre@gmail.com");
        txtRemitente.setPreferredSize(new Dimension(320, 28));

        gbc.gridx = 0; gbc.gridy = 2;
        pCentro.add(lblUser, gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        pCentro.add(txtRemitente, gbc);

        // Fila 3: Contraseña de Aplicación
        JLabel lblPass = crearEtiquetaCampo("Contraseña de Aplicación:");
        txtPassword = new JPasswordField(srv.getPasswordConfigurado(), 20);
        estilizarCampo(txtPassword);
        txtPassword.setPreferredSize(new Dimension(320, 28));

        gbc.gridx = 0; gbc.gridy = 3;
        pCentro.add(lblPass, gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        pCentro.add(txtPassword, gbc);

        // Fila 4: Checkbox mostrar contraseña
        chkMostrarPassword = new JCheckBox("Mostrar caracteres de contraseña");
        chkMostrarPassword.setOpaque(false);
        chkMostrarPassword.setForeground(Theme.getSecundario());
        chkMostrarPassword.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 10.5f));
        chkMostrarPassword.addActionListener(e -> {
            if (chkMostrarPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('-');
            }
        });

        gbc.gridx = 1; gbc.gridy = 4;
        pCentro.add(chkMostrarPassword, gbc);

        // Fila 5: Caja de ayuda explicativa
        JTextArea txtAyuda = new JTextArea(
                "Guía de Conectividad con Gmail:\n" +
                "- Para envío automático desatendido por SMTP SSL (puerto 465), Google requiere\n" +
                "  una 'Contraseña de Aplicación' de 16 letras (https://myaccount.google.com/apppasswords).\n" +
                "- Si no dispone de contraseña de aplicación, use la opción 'Abrir en Gmail Web' en la\n" +
                "  pantalla del boleto para despachar el correo con 1 solo clic desde su navegador.");
        txtAyuda.setEditable(false);
        txtAyuda.setFont(FontManager.getIBMPlexMono(Font.PLAIN, 10f));
        txtAyuda.setForeground(Theme.getMuted());
        txtAyuda.setBackground(Theme.getSurface());
        txtAyuda.setBorder(new CompoundBorder(new LineBorder(Theme.getBorder(), 1), new EmptyBorder(8, 10, 8, 10)));

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        pCentro.add(txtAyuda, gbc);

        // Fila 6: Estado de prueba
        lblEstado = new JLabel("Estado: Listo para probar conexión o guardar.", SwingConstants.LEFT);
        lblEstado.setFont(FontManager.getIBMPlexSans(Font.BOLD, 10.5f));
        lblEstado.setForeground(Theme.getTexto());

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        pCentro.add(lblEstado, gbc);

        pRaiz.add(pCentro, BorderLayout.CENTER);

        // 3. Panel Inferior (Botones)
        JPanel pBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        pBotones.setBackground(Theme.getSurface());
        pBotones.setBorder(new EmptyBorder(8, 16, 8, 16));

        btnProbar = new JButton("Probar Conexión SMTP");
        estilizarBoton(btnProbar, Theme.getElevated(), Theme.getPasajeroAccent());
        btnProbar.addActionListener(e -> accionProbarConexion());

        btnGuardar = new JButton("Guardar y Aplicar");
        estilizarBoton(btnGuardar, Theme.getPasajeroAccent(), Color.WHITE);
        btnGuardar.addActionListener(e -> accionGuardarConfiguracion());

        btnCerrar = new JButton("Cerrar");
        estilizarBoton(btnCerrar, Theme.getElevated(), Theme.getTexto());
        btnCerrar.addActionListener(e -> dispose());

        pBotones.add(btnProbar);
        pBotones.add(btnGuardar);
        pBotones.add(btnCerrar);

        pRaiz.add(pBotones, BorderLayout.SOUTH);

        setContentPane(pRaiz);
    }

    private JLabel crearEtiquetaCampo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FontManager.getIBMPlexSans(Font.BOLD, 11f));
        lbl.setForeground(Theme.getTexto());
        return lbl;
    }

    private JTextField crearCampoTexto(String valorInicial) {
        JTextField tf = new JTextField(valorInicial != null ? valorInicial : "");
        estilizarCampo(tf);
        return tf;
    }

    private void estilizarCampo(JTextField tf) {
        tf.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11.5f));
        tf.setBackground(Theme.getInput());
        tf.setForeground(Theme.getTexto());
        tf.setCaretColor(Theme.getPasajeroAccent());
        tf.setBorder(new CompoundBorder(new LineBorder(Theme.getBorder(), 1), new EmptyBorder(4, 6, 4, 6)));
    }

    private void estilizarBoton(JButton btn, Color bg, Color fg) {
        btn.setFont(FontManager.getIBMPlexSans(Font.BOLD, 11f));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(new LineBorder(Theme.getBorder(), 1), new EmptyBorder(6, 14, 6, 14)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void accionProbarConexion() {
        String host = txtServidor.getText().trim();
        int puerto = 465;
        try {
            puerto = Integer.parseInt(txtPuerto.getText().trim());
        } catch (NumberFormatException e) {
            lblEstado.setText("✗ Puerto inválido.");
            lblEstado.setForeground(Theme.getDanger());
            return;
        }

        String user = txtRemitente.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        final String finalHost = host;
        final int finalPuerto = puerto;
        final String finalUser = user;
        final String finalPass = pass;

        btnProbar.setEnabled(false);
        lblEstado.setText("Conectando con " + finalHost + ":" + finalPuerto + " vía SSL...");
        lblEstado.setForeground(Theme.getWarning());

        SwingWorker<ServicioCorreo.ResultadoSmtp, Void> worker = new SwingWorker<ServicioCorreo.ResultadoSmtp, Void>() {
            @Override
            protected ServicioCorreo.ResultadoSmtp doInBackground() {
                return ServicioCorreo.obtenerInstancia().probarConexionSmtp(finalHost, finalPuerto, finalUser, finalPass);
            }

            @Override
            protected void done() {
                btnProbar.setEnabled(true);
                try {
                    ServicioCorreo.ResultadoSmtp res = get();
                    if (res.isExitoso()) {
                        lblEstado.setText("✓ " + res.getMensaje());
                        lblEstado.setForeground(Theme.getSuccess());
                    } else {
                        lblEstado.setText("✗ " + res.getMensaje());
                        lblEstado.setForeground(Theme.getDanger());
                    }
                } catch (Exception ex) {
                    lblEstado.setText("✗ Error durante la prueba: " + ex.getMessage());
                    lblEstado.setForeground(Theme.getDanger());
                }
            }
        };
        worker.execute();
    }

    private void accionGuardarConfiguracion() {
        String host = txtServidor.getText().trim();
        int puerto = 465;
        try {
            puerto = Integer.parseInt(txtPuerto.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Puerto SMTP numérico requerido (ej. 465).", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String remitente = txtRemitente.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String nombre = txtNombreRemitente.getText().trim();

        ServicioCorreo.obtenerInstancia().guardarConfiguracion(host, puerto, remitente, password, nombre);
        JOptionPane.showMessageDialog(this,
                "Configuración SMTP guardada exitosamente en config_correo.properties.\n\n" +
                "- Servidor: " + host + ":" + puerto + "\n" +
                "- Remitente: " + (remitente.isEmpty() ? "(No configurado)" : remitente) + "\n" +
                "- Estado Credenciales: " + (remitente.isEmpty() || password.isEmpty() ? "Incompletas (se usará Gmail Web)" : "Completas para envío directo"),
                "Configuración Actualizada", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
