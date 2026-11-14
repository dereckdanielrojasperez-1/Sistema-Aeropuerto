// HUMANIZACION UI
package com.aeropuerto.vista.tema;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

// Panel de Telemetria y Consola de Operaciones para el Centro de Control ATC
public class PanelTelemetriaConsola extends JPanel {

    private final JTextPane textPane;
    private final StyledDocument doc;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    // Chips de estado de memoria de estructuras TAD
    private final JLabel chipArreglo;
    private final JLabel chipMatriz;
    private final JLabel chipListaSimple;
    private final JLabel chipListaDoble;
    private final JLabel chipListaCircular;

    public PanelTelemetriaConsola(ActionListener onSincronizarMySQL,
                                  ActionListener onCargarBD,
                                  ActionListener onAlternarVista,
                                  ActionListener onRotarPuerta) {
        setLayout(new BorderLayout(0, 8));
        setBackground(Theme.getSurface());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.getBorder()),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        // 1. Cabecera: Chips de estado de memoria de estructuras TAD
        JPanel panelNorte = new JPanel(new BorderLayout(8, 4));
        panelNorte.setOpaque(false);

        JPanel panelChips = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        panelChips.setOpaque(false);

        JLabel lblTitulo = new JLabel("ESTRUCTURAS TAD:");
        lblTitulo.setFont(FontManager.getBarlowCondensed(Font.BOLD, 11f));
        lblTitulo.setForeground(Theme.getSecundario());
        panelChips.add(lblTitulo);

        chipArreglo = crearChipMemoria("Vuelos [8]");
        chipMatriz = crearChipMemoria("Cabina 10x5");
        chipListaSimple = crearChipMemoria("Equipaje: Lista Simple");
        chipListaDoble = crearChipMemoria("Rutas: Lista Doble");
        chipListaCircular = crearChipMemoria("Puertas: Lista Circular");

        panelChips.add(chipArreglo);
        panelChips.add(chipMatriz);
        panelChips.add(chipListaSimple);
        panelChips.add(chipListaDoble);
        panelChips.add(chipListaCircular);

        panelNorte.add(panelChips, BorderLayout.WEST);

        // Botonera de acciones operacionales con jerarquia
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        panelAcciones.setOpaque(false);

        // Boton Primario Ambar (Rol Admin)
        BotonPrimario btnSincronizar = new BotonPrimario("Sincronizar MySQL", AppIcon.database(13, Color.WHITE));
        btnSincronizar.setPreferredSize(new Dimension(145, 28));
        if (onSincronizarMySQL != null) btnSincronizar.addActionListener(onSincronizarMySQL);
        panelAcciones.add(btnSincronizar);

        // Botones Secundarios OUTLINE
        JButton btnCargar = crearBotonOutline("Cargar BD", Theme.getSecundario(), onCargarBD);
        JButton btnGemelo = crearBotonOutline("Gemelo 3D / Radar", Theme.getAdminAccent(), onAlternarVista);
        JButton btnRotar = crearBotonOutline("Rotar Puerta", Theme.getInfo(), onRotarPuerta);
        JButton btnLimpiar = crearBotonOutline("Limpiar", Theme.getMuted(), e -> limpiar());

        panelAcciones.add(btnCargar);
        panelAcciones.add(btnGemelo);
        panelAcciones.add(btnRotar);
        panelAcciones.add(btnLimpiar);

        panelNorte.add(panelAcciones, BorderLayout.EAST);
        add(panelNorte, BorderLayout.NORTH);

        // 2. Centro: JTextPane con StyledDocument
        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBackground(Theme.getInput());
        textPane.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        doc = textPane.getStyledDocument();

        configurarEstilosTexto();

        JScrollPane scroll = new JScrollPane(textPane);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.getBorder(), 1));
        scroll.getViewport().setBackground(Theme.getInput());
        scroll.setPreferredSize(new Dimension(900, 110));
        add(scroll, BorderLayout.CENTER);

        // Inicializar con log institucional del sistema
        logOk("Centro de Control y Despacho ATC CORPAC inicializado correctamente.");
        logInfo("Estructuras TAD activas: Arreglo Dinámico, Matriz 10x5, Listas Enlazadas en memoria RAM.");

        Theme.addThemeListener(this::actualizarColoresTema);
    }

    private JLabel crearChipMemoria(String texto) {
        JLabel chip = new JLabel(texto, AppIcon.dot(6, Theme.getSuccess()), JLabel.LEFT);
        chip.setIconTextGap(5);
        chip.setFont(FontManager.getIBMPlexMono(Font.PLAIN, 10f));
        chip.setForeground(Theme.getTexto());
        chip.setOpaque(true);
        chip.setBackground(Theme.getElevated());
        chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.getBorder(), 1),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));
        return chip;
    }

    public static JButton crearBotonOutline(String texto, Color acento, ActionListener action) {
        JButton btn = new JButton(texto) {
            private boolean cursorEncima = false;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) { cursorEncima = true; repaint(); }
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) { cursorEncima = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (cursorEncima) {
                    g2.setColor(Theme.getElevated());
                    g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 6, 6);
                }
                g2.setColor(acento != null ? acento : Theme.getBorder());
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FontManager.getIBMPlexSans(Font.BOLD, 10f));
        btn.setForeground(acento != null ? acento : Theme.getTexto());
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        if (action != null) btn.addActionListener(action);
        return btn;
    }

    private void configurarEstilosTexto() {
        // Estilo Timestamp
        Style estiloHora = doc.addStyle("time", null);
        StyleConstants.setFontFamily(estiloHora, FontManager.getIBMPlexMono(Font.PLAIN, 10f).getFamily());
        StyleConstants.setFontSize(estiloHora, 10);
        StyleConstants.setForeground(estiloHora, Theme.getMuted());

        // Estilo INFO
        Style estiloInfo = doc.addStyle("INFO", null);
        StyleConstants.setFontFamily(estiloInfo, FontManager.getIBMPlexMono(Font.BOLD, 10f).getFamily());
        StyleConstants.setFontSize(estiloInfo, 10);
        StyleConstants.setForeground(estiloInfo, Theme.getInfo());

        // Estilo WARN
        Style estiloAlerta = doc.addStyle("WARN", null);
        StyleConstants.setFontFamily(estiloAlerta, FontManager.getIBMPlexMono(Font.BOLD, 10f).getFamily());
        StyleConstants.setFontSize(estiloAlerta, 10);
        StyleConstants.setForeground(estiloAlerta, Theme.getWarning());

        // Estilo OK
        Style estiloExito = doc.addStyle("OK", null);
        StyleConstants.setFontFamily(estiloExito, FontManager.getIBMPlexMono(Font.BOLD, 10f).getFamily());
        StyleConstants.setFontSize(estiloExito, 10);
        StyleConstants.setForeground(estiloExito, Theme.getSuccess());

        // Estilo ALERT / DANGER
        Style estiloPeligro = doc.addStyle("ALERT", null);
        StyleConstants.setFontFamily(estiloPeligro, FontManager.getIBMPlexMono(Font.BOLD, 10f).getFamily());
        StyleConstants.setFontSize(estiloPeligro, 10);
        StyleConstants.setForeground(estiloPeligro, Theme.getDanger());

        // Estilo Mensaje
        Style estiloMensaje = doc.addStyle("msg", null);
        StyleConstants.setFontFamily(estiloMensaje, FontManager.getIBMPlexSans(Font.PLAIN, 11f).getFamily());
        StyleConstants.setFontSize(estiloMensaje, 11);
        StyleConstants.setForeground(estiloMensaje, Theme.getTexto());
    }

    public void log(String mensaje) {
        if (mensaje == null || mensaje.trim().isEmpty()) return;
        String nivel = "INFO";
        String limpio = mensaje.trim();
        String textoMayusculas = limpio.toUpperCase();

        if (textoMayusculas.contains("ERROR") || textoMayusculas.contains("CONFLICTO") || textoMayusculas.contains("FALL") || textoMayusculas.contains("DENIEGA")) {
            nivel = "ALERT";
        } else if (textoMayusculas.contains("WARN") || textoMayusculas.contains("SOBREPESO") || textoMayusculas.contains("DEMORA") || textoMayusculas.contains("ATENCIÓN")) {
            nivel = "WARN";
        } else if (textoMayusculas.contains("EXITOS") || textoMayusculas.contains("CORRECT") || textoMayusculas.contains("RESOLVI") || textoMayusculas.contains("DESPEJAD") || textoMayusculas.contains("INICIAD")) {
            nivel = "OK";
        } else if (textoMayusculas.contains("[MYSQL]") || textoMayusculas.contains("[PUERTAS]") || textoMayusculas.contains("[RUTAS]") || textoMayusculas.contains("[VUELOS]")) {
            nivel = "INFO";
        }

        agregarLinea(nivel, limpio);
    }

    public void logInfo(String mensaje) {
        agregarLinea("INFO", mensaje);
    }

    public void logWarn(String mensaje) {
        agregarLinea("WARN", mensaje);
    }

    public void logOk(String mensaje) {
        agregarLinea("OK", mensaje);
    }

    public void logAlert(String mensaje) {
        agregarLinea("ALERT", mensaje);
    }

    private void agregarLinea(String nivel, String mensaje) {
        SwingUtilities.invokeLater(() -> {
            try {
                String hora = dateFormat.format(new Date()) + " ";
                doc.insertString(doc.getLength(), hora, doc.getStyle("time"));
                doc.insertString(doc.getLength(), "[" + nivel + "] ", doc.getStyle(nivel != null ? nivel : "INFO"));
                doc.insertString(doc.getLength(), mensaje + "\n", doc.getStyle("msg"));

                // Mantener auto-scroll al final
                textPane.setCaretPosition(doc.getLength());
            } catch (BadLocationException ignored) {}
        });
    }

    public void limpiar() {
        try {
            doc.remove(0, doc.getLength());
            logInfo("Consola de telemetría reiniciada.");
        } catch (BadLocationException ignored) {}
    }

    public void actualizarChipVuelos(int totalVuelos) {
        chipArreglo.setText("Vuelos [" + totalVuelos + "]");
    }

    public void actualizarChipPuertas(String texto) {
        chipListaCircular.setText(texto);
    }

    public void actualizarColoresTema() {
        setBackground(Theme.getSurface());
        textPane.setBackground(Theme.getInput());
        configurarEstilosTexto();
        chipArreglo.setBackground(Theme.getElevated());
        chipArreglo.setForeground(Theme.getTexto());
        chipMatriz.setBackground(Theme.getElevated());
        chipMatriz.setForeground(Theme.getTexto());
        chipListaSimple.setBackground(Theme.getElevated());
        chipListaSimple.setForeground(Theme.getTexto());
        chipListaDoble.setBackground(Theme.getElevated());
        chipListaDoble.setForeground(Theme.getTexto());
        chipListaCircular.setBackground(Theme.getElevated());
        chipListaCircular.setForeground(Theme.getTexto());
        repaint();
    }
}
