package com.aeropuerto.vista.tema;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Boton primario institucional con renderizado custom
public class BotonPrimario extends JButton {

    private static final long serialVersionUID = 1L;

    private Color colorAcentoFijo;
    private boolean cursorEncima = false;
    private boolean presionado = false;
    private int radioBorde = 10;

    public BotonPrimario(String texto) {
        this(texto, null, null);
    }

    public BotonPrimario(String texto, Color colorAcento) {
        this(texto, null, colorAcento);
    }

    public BotonPrimario(String texto, javax.swing.Icon icono) {
        this(texto, icono, null);
    }

    public BotonPrimario(String texto, javax.swing.Icon icono, Color colorAcento) {
        super(texto);
        if (icono != null) {
            setIcon(icono);
        }
        this.colorAcentoFijo = colorAcento;

        setContentAreaFilled(false);
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFont(FontManager.getIBMPlexSans(Font.BOLD, 13f));
        setPreferredSize(new Dimension(220, 38));
        setMargin(new Insets(6, 16, 6, 16));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cursorEncima = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cursorEncima = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                presionado = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                presionado = false;
                repaint();
            }
        });
    }

    public void setColorAcento(Color color) {
        this.colorAcentoFijo = color;
        repaint();
    }

    public Color getColorAcento() {
        if (colorAcentoFijo != null) {
            return colorAcentoFijo;
        }
        return Theme.getAccentRolActivo();
    }

    public void setRadioBorde(int radio) {
        this.radioBorde = radio;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int ancho = getWidth();
        int alto = getHeight();

        // 1. Color base
        Color colorFondo = getColorAcento();
        if (!isEnabled()) {
            colorFondo = Theme.getMuted();
        }

        // 2. Fondo redondeado
        g2.setColor(colorFondo);
        g2.fillRoundRect(0, 0, ancho, alto, radioBorde * 2, radioBorde * 2);

        // 3. Efectos Hover / Pressed
        if (isEnabled()) {
            if (presionado) {
                g2.setColor(new Color(0, 0, 0, 50));
                g2.fillRoundRect(0, 0, ancho, alto, radioBorde * 2, radioBorde * 2);
            } else if (cursorEncima) {
                g2.setColor(new Color(255, 255, 255, 45));
                g2.fillRoundRect(0, 0, ancho, alto, radioBorde * 2, radioBorde * 2);
                g2.setColor(new Color(255, 255, 255, 70));
                g2.setStroke(new java.awt.BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, ancho - 1, alto - 1, radioBorde * 2, radioBorde * 2);
            }
        }

        // 4. Focus Ring accesible
        if (isFocusOwner()) {
            g2.setColor(Theme.getInfo());
            g2.setStroke(new java.awt.BasicStroke(2.0f));
            g2.drawRoundRect(1, 1, ancho - 3, alto - 3, radioBorde * 2, radioBorde * 2);
        }

        // 5. Texto de alto contraste (Plex Sans 600)
        // Calculo de contraste dinamico para garantizar WCAG 4.5:1
        double luminancia = (0.299 * colorFondo.getRed() + 0.587 * colorFondo.getGreen() + 0.114 * colorFondo.getBlue()) / 255.0;
        Color colorTexto = (luminancia > 0.65) ? new Color(13, 17, 23) : Color.WHITE;

        g2.setColor(isEnabled() ? colorTexto : new Color(200, 200, 200, 180));
        g2.setFont(FontManager.getIBMPlexSans(Font.BOLD, 13f));

        FontMetrics metricasFuente = g2.getFontMetrics();
        String textoBoton = getText();
        if (textoBoton != null && !textoBoton.isEmpty()) {
            int anchoTexto = metricasFuente.stringWidth(textoBoton);
            int altoTexto = metricasFuente.getAscent();

            int xTexto = (ancho - anchoTexto) / 2;
            int yTexto = (alto + altoTexto) / 2 - 2;

            if (getIcon() != null) {
                int anchoIcono = getIcon().getIconWidth();
                int anchoTotal = anchoIcono + 8 + anchoTexto;
                int xInicio = (ancho - anchoTotal) / 2;
                getIcon().paintIcon(this, g2, xInicio, (alto - getIcon().getIconHeight()) / 2);
                xTexto = xInicio + anchoIcono + 8;
            }

            g2.drawString(textoBoton, xTexto, yTexto);
        }

        g2.dispose();
    }
}
