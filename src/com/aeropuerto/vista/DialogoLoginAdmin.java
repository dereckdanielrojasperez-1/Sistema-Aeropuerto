package com.aeropuerto.vista;

import com.aeropuerto.controlador.ControladorAeropuerto;
import com.aeropuerto.modelo.Usuario;
import com.aeropuerto.vista.tema.Theme;
import com.aeropuerto.vista.tema.GlassPanel;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

// Dialogo modal para autenticacion y control de acceso de administrador en Smart Airport
public class DialogoLoginAdmin extends JDialog {
    private static final long serialVersionUID = 1L;

    private ControladorAeropuerto controlador;
    private JTextField txtUsuario;
    private JPasswordField txtClave;
    private boolean autenticadoExitoso = false;

    // Paleta Smart Airport
    private final Color fondoOscuro = Theme.getBg();
    private final Color fondoHeader = Theme.getSurface();
    private final Color fondoEntrada = Theme.getInput();
    private final Color bordeComponente = Theme.getBorder();
    private final Color doradoSmart = Theme.getWarning();
    private final Color textoBlanco = Theme.getTexto();
    private final Color textoGris = Theme.getSecundario();

    public DialogoLoginAdmin(Frame padre, ControladorAeropuerto controlador) {
        super(padre, "CORPAC - Acceso Administrativo", true);
        this.controlador = controlador;
        inicializarComponentes();
        pack();
        setResizable(false);
        setLocationRelativeTo(padre);
    }

    private void inicializarComponentes() {
        GlassPanel panelPrincipal = new GlassPanel(new BorderLayout(), 16);
        panelPrincipal.setColorAcento(Theme.getAdminAccent());
        panelPrincipal.setBorder(new LineBorder(bordeComponente, 1));

        // Encabezado
        JPanel panelEncabezado = new JPanel(new GridLayout(2, 1, 0, 4));
        panelEncabezado.setBackground(fondoHeader);
        panelEncabezado.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel lblTitulo = new JLabel("CONTROL DE ACCESO ADMINISTRATIVO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(doradoSmart);

        JLabel lblSub = new JLabel("CENTRO DE OPERACIONES AERONÁUTICAS - AOC | CORPAC PERÚ", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(textoGris);

        panelEncabezado.add(lblTitulo);
        panelEncabezado.add(lblSub);
        panelPrincipal.add(panelEncabezado, BorderLayout.NORTH);

        // Formulario central
        JPanel panelCentro = new JPanel(new GridBagLayout());
        panelCentro.setOpaque(false);
        panelCentro.setBorder(new EmptyBorder(20, 24, 16, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUsuario.setForeground(textoBlanco);

        txtUsuario = new JTextField("admin", 16);
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtUsuario.setBackground(fondoEntrada);
        txtUsuario.setForeground(textoBlanco);
        txtUsuario.setCaretColor(doradoSmart);
        txtUsuario.setBorder(new CompoundBorder(new LineBorder(bordeComponente, 1), new EmptyBorder(4, 6, 4, 6)));

        JLabel lblClave = new JLabel("Contraseña:");
        lblClave.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblClave.setForeground(textoBlanco);

        txtClave = new JPasswordField("admin123", 16);
        txtClave.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtClave.setBackground(fondoEntrada);
        txtClave.setForeground(textoBlanco);
        txtClave.setCaretColor(doradoSmart);
        txtClave.setBorder(new CompoundBorder(new LineBorder(bordeComponente, 1), new EmptyBorder(4, 6, 4, 6)));

        gbc.gridx = 0; gbc.gridy = 0;
        panelCentro.add(lblUsuario, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        panelCentro.add(txtUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelCentro.add(lblClave, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        panelCentro.add(txtClave, gbc);

        JLabel lblNota = new JLabel("Credenciales autorizadas por defecto: admin / admin123", SwingConstants.CENTER);
        lblNota.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblNota.setForeground(doradoSmart);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panelCentro.add(lblNota, gbc);

        JPanel pChipsAcceso = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
        pChipsAcceso.setOpaque(false);

        JButton btnChipAdmin = new JButton("Acceso Admin (1 Clic)");
        btnChipAdmin.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnChipAdmin.setBackground(Theme.getElevated());
        btnChipAdmin.setForeground(doradoSmart);
        btnChipAdmin.setFocusPainted(false);
        btnChipAdmin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChipAdmin.setBorder(BorderFactory.createCompoundBorder(new LineBorder(bordeComponente, 1), new EmptyBorder(4, 8, 4, 8)));
        btnChipAdmin.addActionListener(e -> {
            txtUsuario.setText("admin");
            txtClave.setText("admin123");
            ejecutarInicioSesion();
        });

        JButton btnChipSup = new JButton("Acceso Supervisor (1 Clic)");
        btnChipSup.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        btnChipSup.setBackground(Theme.getElevated());
        btnChipSup.setForeground(Theme.getPasajeroAccent());
        btnChipSup.setFocusPainted(false);
        btnChipSup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChipSup.setBorder(BorderFactory.createCompoundBorder(new LineBorder(bordeComponente, 1), new EmptyBorder(4, 8, 4, 8)));
        btnChipSup.addActionListener(e -> {
            txtUsuario.setText("sup_lima");
            txtClave.setText("sup123");
            ejecutarInicioSesion();
        });

        pChipsAcceso.add(btnChipAdmin);
        pChipsAcceso.add(btnChipSup);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panelCentro.add(pChipsAcceso, gbc);

        panelPrincipal.add(panelCentro, BorderLayout.CENTER);

        // Botonera inferior
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        panelBotones.setBackground(fondoHeader);
        panelBotones.setBorder(new LineBorder(bordeComponente, 1));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancelar.setBackground(Theme.getElevated());
        btnCancelar.setForeground(textoBlanco);
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnIngresar = new JButton("Iniciar Sesión AOC");
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnIngresar.setBackground(doradoSmart);
        btnIngresar.setForeground(Theme.esModoOscuro() ? new Color(15, 20, 29) : Color.WHITE);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIngresar.addActionListener(e -> ejecutarInicioSesion());

        getRootPane().setDefaultButton(btnIngresar);

        panelBotones.add(btnCancelar);
        panelBotones.add(btnIngresar);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        setContentPane(panelPrincipal);
    }

    private void ejecutarInicioSesion() {
        String usuario = txtUsuario.getText().trim();
        String clave = new String(txtClave.getPassword()).trim();

        if (usuario.isEmpty() || clave.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese usuario y contraseña.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuarioAutenticado = controlador.autenticarUsuario(usuario, clave);
        if (usuarioAutenticado != null) {
            this.autenticadoExitoso = true;
            JOptionPane.showMessageDialog(this,
                    "Autenticación exitosa en Centro de Control CORPAC.\nBienvenido, " + usuarioAutenticado.obtenerNombre() + " · Rol: " + usuarioAutenticado.obtenerRol() + ".",
                    "Acceso Concedido", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Usuario o contraseña incorrectos.\nVerifique sus credenciales e intente nuevamente.",
                    "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
            txtClave.setText("");
            txtClave.requestFocus();
        }
    }

    public boolean estaAutenticado() {
        return autenticadoExitoso;
    }
}
