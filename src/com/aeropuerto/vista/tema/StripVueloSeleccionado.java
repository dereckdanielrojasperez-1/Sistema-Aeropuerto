package com.aeropuerto.vista.tema;

import com.aeropuerto.modelo.Vuelo;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Locale;

// Strip informativo moderno de Vuelo Seleccionado
public class StripVueloSeleccionado extends JPanel {

    private final JLabel lblIcono;
    private final JLabel lblTitulo;
    private final JLabel lblCodigo;
    private final JLabel lblRuta;
    private final JLabel lblHorario;
    private final JLabel lblTarifa;
    private final JLabel lblAsientos;

    public StripVueloSeleccionado() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 12, 6));
        setOpaque(false);
        setPreferredSize(new Dimension(850, 36));
        setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));

        lblIcono = new JLabel(AppIcon.plane(16, Theme.getPasajeroAccent()));
        
        lblTitulo = new JLabel("VUELO SELECCIONADO:");
        lblTitulo.setFont(FontManager.getBarlowCondensed(Font.BOLD, 11f));
        lblTitulo.setForeground(Theme.getPasajeroAccent());

        lblCodigo = new JLabel("CP-XXXX");
        lblCodigo.setFont(FontManager.getIBMPlexMono(Font.BOLD, 12f));
        lblCodigo.setForeground(Theme.getTexto());

        lblRuta = new JLabel("| Ruta: ---");
        lblRuta.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
        lblRuta.setForeground(Theme.getTexto());

        lblHorario = new JLabel("| Horario: --:--");
        lblHorario.setFont(FontManager.getIBMPlexMono(Font.PLAIN, 11f));
        lblHorario.setForeground(Theme.getTexto());

        lblTarifa = new JLabel("| Tarifa: S/. 0.00");
        lblTarifa.setFont(FontManager.getIBMPlexSans(Font.BOLD, 11f));
        lblTarifa.setForeground(Theme.getTexto());

        lblAsientos = new JLabel("| Asientos: 0/50");
        lblAsientos.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));
        lblAsientos.setForeground(Theme.getSecundario());

        add(lblIcono);
        add(lblTitulo);
        add(lblCodigo);
        add(lblRuta);
        add(lblHorario);
        add(lblTarifa);
        add(lblAsientos);

        Theme.addThemeListener(() -> {
            lblIcono.setIcon(AppIcon.plane(16, Theme.getPasajeroAccent()));
            lblTitulo.setForeground(Theme.getPasajeroAccent());
            lblCodigo.setForeground(Theme.getTexto());
            lblRuta.setForeground(Theme.getTexto());
            lblHorario.setForeground(Theme.getTexto());
            lblTarifa.setForeground(Theme.getTexto());
            lblAsientos.setForeground(Theme.getSecundario());
            repaint();
        });
    }

    public void actualizarVuelo(Vuelo vuelo) {
        if (vuelo != null) {
            lblCodigo.setText(vuelo.obtenerCodigo());
            lblRuta.setText("| " + vuelo.obtenerOrigen() + " -> " + vuelo.obtenerDestino());
            lblHorario.setText("| Salida: " + vuelo.obtenerHoraSalida() + " -> " + vuelo.obtenerHoraLlegada());
            lblTarifa.setText("| Tarifa: S/. " + String.format(Locale.US, "%.2f", vuelo.obtenerPrecioBase()));
            lblAsientos.setText("| " + vuelo.contarAsientosLibres() + " / 50 Asientos Libres");
        } else {
            lblCodigo.setText("SIN SELECCIÓN");
            lblRuta.setText("| Seleccione un vuelo de la tabla o tarjetas");
            lblHorario.setText("");
            lblTarifa.setText("");
            lblAsientos.setText("");
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int ancho = getWidth();
        int alto = getHeight();

        // Fondo accent-soft
        g2.setColor(Theme.getPasajeroSoft());
        g2.fillRoundRect(1, 1, ancho - 2, alto - 2, 8, 8);

        // Borde fino de acento
        g2.setColor(Theme.getPasajeroAccent());
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(1, 1, ancho - 2, alto - 2, 8, 8);

        g2.dispose();
    }
}
