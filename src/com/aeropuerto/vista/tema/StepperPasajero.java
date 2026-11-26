package com.aeropuerto.vista.tema;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Stepper aeronautico de 4 pasos para el Portal del Pasajero
public class StepperPasajero extends JPanel {

    public interface PasoSeleccionadoListener {
        void onPasoSeleccionado(int pasoIndex);
    }

    private static final String[] TITULOS_PASOS = {
            "1. Vuelos y Destinos",
            "2. Asiento y Cabina",
            "3. Pasajero y Equipaje",
            "4. Pase de Abordar"
    };

    private static final String[] SUBTITULOS_PASOS = {
            "Itinerario y Selección",
            "Clase y Ubicación",
            "Datos y Control IATA",
            "Emisión y Puerta"
    };

    private int pasoActivo = 0;
    private PasoSeleccionadoListener listener;
    private final ItemPasoPanel[] itemsPaso = new ItemPasoPanel[4];
    private final boolean[] pasosCompletados = new boolean[4];

    public StepperPasajero() {
        setLayout(new GridLayout(1, 4, 10, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(2, 14, 2, 14));
        setPreferredSize(new Dimension(860, 42));

        // Paso 1 inicia completado porque ya hay un vuelo seleccionado por defecto
        pasosCompletados[0] = true;

        for (int i = 0; i < 4; i++) {
            itemsPaso[i] = new ItemPasoPanel(i, TITULOS_PASOS[i], SUBTITULOS_PASOS[i]);
            // Indicador de progreso no clicable (la barra de pestanas es la unica navegacion)
            add(itemsPaso[i]);
        }

        Theme.addThemeListener(this::repaint);
    }

    public void setPasoCompletado(int indice, boolean completado) {
        if (indice >= 0 && indice < 4) {
            this.pasosCompletados[indice] = completado;
            if (itemsPaso[indice] != null) {
                itemsPaso[indice].repaint();
            }
        }
    }

    public boolean esPasoCompletado(int indice) {
        return indice >= 0 && indice < 4 && pasosCompletados[indice];
    }

    public void setListener(PasoSeleccionadoListener listener) {
        this.listener = listener;
    }

    public void setPasoActivo(int nuevoPaso) {
        if (nuevoPaso < 0) nuevoPaso = 0;
        if (nuevoPaso > 3) nuevoPaso = 3;
        this.pasoActivo = nuevoPaso;
        for (int i = 0; i < 4; i++) {
            itemsPaso[i].repaint();
        }
    }

    public int getPasoActivo() {
        return pasoActivo;
    }

    // Sincroniza el Stepper con el JTabbedPane del portal como indicador de progreso
    public void conectarConTabbedPane(JTabbedPane tabbedPane) {
        if (tabbedPane == null) return;
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int sel = tabbedPane.getSelectedIndex();
                if (sel >= 0 && sel < 4) {
                    setPasoActivo(sel);
                }
            }
        });
    }

    private class ItemPasoPanel extends JPanel {
        private final int indice;
        private final String titulo;
        private final String subtitulo;
        private boolean hover = false;

        public ItemPasoPanel(int indice, String titulo, String subtitulo) {
            this.indice = indice;
            this.titulo = titulo;
            this.subtitulo = subtitulo;
            setOpaque(false);
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            int w = getWidth();
            int h = getHeight();

            boolean esCompletado = pasosCompletados[indice];
            boolean esActivo = indice == pasoActivo;
            boolean esPendiente = !esCompletado && !esActivo;

            // Fondo de la capsula Glassmorphism (radio 16)
            int radioCapsula = 16;
            if (esActivo) {
                // Píldora de acento translúcida #58A6FF
                Color softAccent = Theme.getPasajeroSoft();
                g2.setColor(new Color(softAccent.getRed(), softAccent.getGreen(), softAccent.getBlue(), 210));
                g2.fillRoundRect(2, 2, w - 4, h - 4, radioCapsula, radioCapsula);
                g2.setColor(Theme.getPasajeroAccent());
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(2, 2, w - 4, h - 4, radioCapsula, radioCapsula);
                // Highlight superior
                g2.setColor(new Color(255, 255, 255, 45));
                g2.drawLine(8, 3, w - 8, 3);
            } else if (hover) {
                Color elevated = Theme.getElevated();
                g2.setColor(new Color(elevated.getRed(), elevated.getGreen(), elevated.getBlue(), 200));
                g2.fillRoundRect(2, 2, w - 4, h - 4, radioCapsula, radioCapsula);
                g2.setColor(Theme.getBorder());
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(2, 2, w - 4, h - 4, radioCapsula, radioCapsula);
            } else {
                Color surface = Theme.getSurface();
                g2.setColor(new Color(surface.getRed(), surface.getGreen(), surface.getBlue(), 185));
                g2.fillRoundRect(2, 2, w - 4, h - 4, radioCapsula, radioCapsula);
                g2.setColor(Theme.getBorder());
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(2, 2, w - 4, h - 4, radioCapsula, radioCapsula);
            }

            // Circulo indicador (20px)
            int diametro = 20;
            int circuloX = 10;
            int circuloY = (h - diametro) / 2;

            if (esCompletado) {
                // Circulo verde de completado con checkmark
                g2.setColor(Theme.getSuccess());
                g2.fillOval(circuloX, circuloY, diametro, diametro);

                // Dibujar checkmark blanco
                g2.setColor(java.awt.Color.WHITE);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(circuloX + 5, circuloY + 10, circuloX + 8, circuloY + 13);
                g2.drawLine(circuloX + 8, circuloY + 13, circuloX + 15, circuloY + 6);
            } else if (esActivo) {
                // Circulo acentuado con numero
                g2.setColor(Theme.getPasajeroAccent());
                g2.fillOval(circuloX, circuloY, diametro, diametro);

                g2.setColor(java.awt.Color.WHITE);
                g2.setFont(FontManager.getIBMPlexSans(java.awt.Font.BOLD, 10.5f));
                String num = String.valueOf(indice + 1);
                int strW = g2.getFontMetrics().stringWidth(num);
                g2.drawString(num, circuloX + (diametro - strW) / 2, circuloY + 14);
            } else {
                // Circulo gris tenue
                g2.setColor(Theme.getBorder());
                g2.drawOval(circuloX, circuloY, diametro, diametro);

                g2.setColor(Theme.getMuted());
                g2.setFont(FontManager.getIBMPlexSans(java.awt.Font.PLAIN, 10f));
                String num = String.valueOf(indice + 1);
                int strW = g2.getFontMetrics().stringWidth(num);
                g2.drawString(num, circuloX + (diametro - strW) / 2, circuloY + 14);
            }

            // Texto: Titulo y Subtitulo
            int textX = circuloX + diametro + 8;
            if (esActivo) {
                g2.setColor(Theme.getTexto());
                g2.setFont(FontManager.getBarlowCondensed(java.awt.Font.BOLD, 12f));
            } else if (esCompletado) {
                g2.setColor(Theme.getTexto());
                g2.setFont(FontManager.getBarlowCondensed(java.awt.Font.PLAIN, 12f));
            } else {
                g2.setColor(Theme.getMuted());
                g2.setFont(FontManager.getBarlowCondensed(java.awt.Font.PLAIN, 11f));
            }
            g2.drawString(titulo, textX, 17);

            // Subtitulo
            if (esActivo) {
                g2.setColor(Theme.getPasajeroAccent());
                g2.setFont(FontManager.getIBMPlexSans(java.awt.Font.PLAIN, 9f));
            } else {
                g2.setColor(Theme.getSecundario());
                g2.setFont(FontManager.getIBMPlexSans(java.awt.Font.PLAIN, 8.5f));
            }
            g2.drawString(subtitulo, textX, 29);

            g2.dispose();
        }
    }
}
