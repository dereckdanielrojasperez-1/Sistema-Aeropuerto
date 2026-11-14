// HUMANIZACION UI
package com.aeropuerto.vista.tema;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BasicStroke;
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

// Barra de estado inferior moderna para Smart Airport CORPAC
public class BarraEstadoFooter extends JPanel {

    private final ChipEstadoPanel chipMysql;
    private final ChipEstadoPanel chipRam;
    private final ChipEstadoPanel chipTad;

    private final JButton btnSincronizar;
    private final JButton btnCargarBD;
    private final JLabel lblEstadoTexto;

    private final Timer temporizadorRam;

    public BarraEstadoFooter(ActionListener onSincronizar, ActionListener onCargarBD) {
        setLayout(new BorderLayout(10, 0));
        setOpaque(true);
        setBackground(Theme.getSurface());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.getBorder()),
                BorderFactory.createEmptyBorder(4, 16, 4, 16)));
        setPreferredSize(new Dimension(1000, 38));

        // Panel Izquierdo / Central con Chips
        JPanel panelChips = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        panelChips.setOpaque(false);

        chipMysql = new ChipEstadoPanel("MySQL", "Verificando...", Theme.getWarning());
        chipRam = new ChipEstadoPanel("RAM", "Calculando...", Theme.getInfo());
        chipTad = new ChipEstadoPanel("TADs", "Vuelos · Matriz 10x5 · Listas Enlazadas", Theme.getSuccess());

        panelChips.add(chipMysql);
        panelChips.add(chipRam);
        panelChips.add(chipTad);
        add(panelChips, BorderLayout.WEST);

        // Panel Derecho con Acciones de Base de Datos
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        panelAcciones.setOpaque(false);

        lblEstadoTexto = new JLabel("");
        lblEstadoTexto.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 10f));
        lblEstadoTexto.setForeground(Theme.getMuted());

        btnSincronizar = new JButton("Sincronizar MySQL");
        estilizarBotonChip(btnSincronizar, true);
        if (onSincronizar != null) btnSincronizar.addActionListener(onSincronizar);

        btnCargarBD = new JButton("Cargar BD");
        estilizarBotonChip(btnCargarBD, false);
        if (onCargarBD != null) btnCargarBD.addActionListener(onCargarBD);

        panelAcciones.add(lblEstadoTexto);
        panelAcciones.add(btnSincronizar);
        panelAcciones.add(btnCargarBD);
        add(panelAcciones, BorderLayout.EAST);

        // Timer para monitoreo continuo de memoria JVM
        temporizadorRam = new Timer(4000, e -> actualizarMemoriaRam());
        temporizadorRam.setInitialDelay(200);
        temporizadorRam.start();

        Theme.addThemeListener(this::actualizarTema);
    }

    private void actualizarTema() {
        setBackground(Theme.getSurface());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.getBorder()),
                BorderFactory.createEmptyBorder(4, 16, 4, 16)));
        estilizarBotonChip(btnSincronizar, true);
        estilizarBotonChip(btnCargarBD, false);
        repaint();
    }

    private void estilizarBotonChip(JButton btn, boolean primario) {
        btn.setFont(FontManager.getIBMPlexSans(Font.BOLD, 10f));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primario ? Theme.getPasajeroAccent() : Theme.getBorder(), 1),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)));
        if (primario) {
            btn.setBackground(Theme.getPasajeroSoft());
            btn.setForeground(Theme.getPasajeroAccent());
        } else {
            btn.setBackground(Theme.getElevated());
            btn.setForeground(Theme.getTexto());
        }
    }

    public void actualizarEstadoMySQL(String mensaje, boolean conectado) {
        if (conectado) {
            chipMysql.actualizar("MySQL", "En Línea (3306)", Theme.getSuccess());
        } else {
            chipMysql.actualizar("MySQL", "Offline / Local", Theme.getWarning());
        }
        if (lblEstadoTexto != null && mensaje != null) {
            lblEstadoTexto.setText(mensaje);
        }
    }

    public void actualizarEstructuras(String detalleTad) {
        if (chipTad != null && detalleTad != null) {
            chipTad.actualizar("TADs", detalleTad, Theme.getSuccess());
        }
    }

    private void actualizarMemoriaRam() {
        Runtime tiempoEjecucion = Runtime.getRuntime();
        long memoriaTotal = tiempoEjecucion.totalMemory() / (1024 * 1024);
        long memoriaLibre = tiempoEjecucion.freeMemory() / (1024 * 1024);
        long memoriaUsada = memoriaTotal - memoriaLibre;
        chipRam.actualizar("RAM", memoriaUsada + " MB / " + memoriaTotal + " MB", Theme.getInfo());
    }

    // Componente visual de Chip semantico con indicador LED
    public static class ChipEstadoPanel extends JPanel {
        private String prefijo;
        private String valor;
        private Color ledColor;

        public ChipEstadoPanel(String prefijo, String valor, Color ledColor) {
            this.prefijo = prefijo;
            this.valor = valor;
            this.ledColor = ledColor;
            setOpaque(false);
            setPreferredSize(new Dimension(210, 26));
        }

        public void actualizar(String prefijo, String valor, Color ledColor) {
            this.prefijo = prefijo;
            this.valor = valor;
            this.ledColor = ledColor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            int ancho = getWidth();
            int alto = getHeight();

            // Fondo del chip
            g2.setColor(Theme.getInput());
            g2.fillRoundRect(1, 1, ancho - 2, alto - 2, 8, 8);
            g2.setColor(Theme.getBorder());
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(1, 1, ancho - 2, alto - 2, 8, 8);

            // Indicador LED circular (8px)
            int xLed = 8;
            int yLed = (alto - 8) / 2;
            g2.setColor(ledColor != null ? ledColor : Theme.getSuccess());
            g2.fillOval(xLed, yLed, 8, 8);

            // Texto en IBM Plex Mono
            g2.setColor(Theme.getMuted());
            g2.setFont(FontManager.getIBMPlexSans(Font.BOLD, 10f));
            g2.drawString(prefijo + ":", xLed + 13, 17);

            int anchoPrefijo = g2.getFontMetrics().stringWidth(prefijo + ":");
            g2.setColor(Theme.getTexto());
            g2.setFont(FontManager.getIBMPlexMono(Font.PLAIN, 10f));
            g2.drawString(valor, xLed + 17 + anchoPrefijo, 17);

            g2.dispose();
        }
    }
}
