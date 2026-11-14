package com.aeropuerto.vista;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.GradientPaint;

// Panel con fondo degradado
public class PanelDegradado extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private Color colorInicio = new Color(11, 59, 96);
    private Color colorFin = new Color(74, 144, 226);

    // Cambia los colores del degradado
    public void establecerColores(Color colorInicial, Color colorFinal) {
        this.colorInicio = colorInicial;
        this.colorFin = colorFinal;
        repaint();
    }

    // Dibuja el fondo degradado
    @Override
    protected void paintComponent(Graphics graficos) {
        super.paintComponent(graficos);
        Graphics2D graficos2D = (Graphics2D) graficos;
        int ancho = getWidth();
        int alto = getHeight();
        GradientPaint degradado = new GradientPaint(0, 0, colorInicio, 0, alto, colorFin);
        graficos2D.setPaint(degradado);
        graficos2D.fillRect(0, 0, ancho, alto);
    }
}
