// HUMANIZACION UI - Botón de Asiento de Cabina con Glassmorphism para CORPAC
package com.aeropuerto.vista.tema;

import javax.swing.JButton;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Botón para la matriz física de cabina (Airbus A320neo) con esquinas redondeadas
 * (radio 8), efecto hover de vidrio, y estados semánticos:
 * - Verde esmeralda translúcido (Libre)
 * - Rojo apagado (Ocupado)
 * - Azul cyan brillante con borde blanco (Seleccionado)
 * - Dorado ámbar (VIP Libre)
 */
public class BotonAsientoCabina extends JButton {

    private static final long serialVersionUID = 1L;

    public enum EstadoAsiento {
        LIBRE_TURISTA,
        LIBRE_VIP,
        OCUPADO,
        SELECCIONADO,
        CONFIRMADO,
        BLOQUEADO_CLASE
    }

    private EstadoAsiento estado = EstadoAsiento.LIBRE_TURISTA;
    private final int radioEsquinas = 8;
    private boolean hover = false;

    public BotonAsientoCabina() {
        super();
        setContentAreaFilled(false);
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFont(new Font("Segoe UI", Font.BOLD, 10));
        setPreferredSize(new Dimension(50, 26));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled() && estado != EstadoAsiento.OCUPADO && estado != EstadoAsiento.BLOQUEADO_CLASE) {
                    hover = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    public void setEstadoAsiento(EstadoAsiento nuevoEstado) {
        this.estado = nuevoEstado;
        repaint();
    }

    public EstadoAsiento getEstadoAsiento() {
        return estado;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) {
            g2.dispose();
            return;
        }

        boolean oscuro = Theme.esModoOscuro();

        Color colorFondo;
        Color colorBorde;
        Color colorTexto;
        float grosorBorde = 1.0f;

        switch (estado) {
            case SELECCIONADO:
                colorFondo = oscuro ? new Color(0x58, 0xA6, 0xFF) : new Color(0x09, 0x69, 0xDA);
                colorBorde = oscuro ? Color.WHITE : new Color(13, 17, 23);
                colorTexto = Color.WHITE;
                grosorBorde = 2.0f;
                break;
            case CONFIRMADO:
                colorFondo = new Color(2, 132, 199);
                colorBorde = new Color(56, 189, 248);
                colorTexto = Color.WHITE;
                grosorBorde = 1.8f;
                break;
            case OCUPADO:
                if (oscuro) {
                    colorFondo = new Color(42, 20, 25, 210);
                    colorBorde = new Color(75, 26, 32);
                    colorTexto = new Color(248, 81, 73, 195);
                } else {
                    colorFondo = new Color(254, 226, 226, 220);
                    colorBorde = new Color(252, 165, 165);
                    colorTexto = new Color(185, 28, 28);
                }
                break;
            case LIBRE_VIP:
                if (oscuro) {
                    colorFondo = new Color(38, 28, 14, 210);
                    colorBorde = new Color(180, 110, 20);
                    colorTexto = new Color(251, 191, 36);
                } else {
                    colorFondo = new Color(254, 243, 199, 220);
                    colorBorde = new Color(245, 158, 11);
                    colorTexto = new Color(180, 83, 9);
                }
                break;
            case BLOQUEADO_CLASE:
                if (oscuro) {
                    colorFondo = new Color(20, 24, 33, 160);
                    colorBorde = new Color(38, 44, 55, 140);
                    colorTexto = new Color(75, 85, 99, 140);
                } else {
                    colorFondo = new Color(241, 245, 249, 180);
                    colorBorde = new Color(203, 213, 225, 160);
                    colorTexto = new Color(148, 163, 184, 150);
                }
                break;
            case LIBRE_TURISTA:
            default:
                if (oscuro) {
                    colorFondo = new Color(16, 42, 28, 210);
                    colorBorde = new Color(35, 95, 55);
                    colorTexto = new Color(63, 185, 80);
                } else {
                    colorFondo = new Color(220, 252, 231, 220);
                    colorBorde = new Color(134, 239, 172);
                    colorTexto = new Color(4, 120, 87);
                }
                break;
        }

        // 1. Relleno con esquinas redondeadas radio 8
        g2.setColor(colorFondo);
        g2.fillRoundRect(1, 1, w - 2, h - 2, radioEsquinas, radioEsquinas);

        // 2. Efecto hover de vidrio (alfa sube 5%)
        if (hover && estado != EstadoAsiento.OCUPADO) {
            g2.setColor(new Color(255, 255, 255, 38));
            g2.fillRoundRect(1, 1, w - 2, h - 2, radioEsquinas, radioEsquinas);
        }

        // 3. Borde luminoso / semántico
        g2.setColor(colorBorde);
        g2.setStroke(new BasicStroke(grosorBorde));
        g2.drawRoundRect(1, 1, w - 2, h - 2, radioEsquinas, radioEsquinas);

        // 4. Highlight reflectivo superior sutil
        if (h > 12) {
            g2.setColor(new Color(255, 255, 255, oscuro ? 25 : 80));
            g2.drawLine(4, 2, w - 4, 2);
        }

        // 5. Texto de alto contraste centrado
        String txt = getText();
        if (txt != null && !txt.isEmpty()) {
            g2.setColor(colorTexto);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int strW = fm.stringWidth(txt);
            int strH = fm.getAscent();
            int tx = (w - strW) / 2;
            int ty = (h + strH) / 2 - 2;
            g2.drawString(txt, tx, ty);
        }

        g2.dispose();
    }
}
