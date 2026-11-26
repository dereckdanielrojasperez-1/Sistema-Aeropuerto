package com.aeropuerto.vista.tema;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.Icon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.Map;

// Mapeo centralizado de iconografia Ikonli
public final class AppIcon {

    private static final Map<String, Ikon> MAPA_IKONS = new HashMap<>();

    static {
        MAPA_IKONS.put("plane", FontAwesomeSolid.PLANE);
        MAPA_IKONS.put("building", FontAwesomeSolid.BUILDING);
        MAPA_IKONS.put("luggage", FontAwesomeSolid.SUITCASE);
        MAPA_IKONS.put("radar", FontAwesomeSolid.BROADCAST_TOWER);
        MAPA_IKONS.put("shield", FontAwesomeSolid.SHIELD_ALT);
        MAPA_IKONS.put("warning", FontAwesomeSolid.EXCLAMATION_TRIANGLE);
        MAPA_IKONS.put("eye", FontAwesomeSolid.EYE);
        MAPA_IKONS.put("eye_slash", FontAwesomeSolid.EYE_SLASH);
        MAPA_IKONS.put("pencil", FontAwesomeSolid.PEN);
        MAPA_IKONS.put("more", FontAwesomeSolid.ELLIPSIS_H);
        MAPA_IKONS.put("database", FontAwesomeSolid.DATABASE);
        MAPA_IKONS.put("settings", FontAwesomeSolid.COG);
        MAPA_IKONS.put("check", FontAwesomeSolid.CHECK);
        MAPA_IKONS.put("sun", FontAwesomeSolid.SUN);
        MAPA_IKONS.put("sunset", FontAwesomeSolid.CLOUD_SUN);
        MAPA_IKONS.put("moon", FontAwesomeSolid.MOON);
        MAPA_IKONS.put("users", FontAwesomeSolid.USERS);
        MAPA_IKONS.put("flask", FontAwesomeSolid.FLASK);
    }

    private AppIcon() {}

    // Obtiene el icono Ikonli segun el nombre mapeado, con color y tamano
    public static Icon of(String nombre, int tamano, Color color) {
        if (nombre == null) return crearIconoVacio(tamano);
        String clave = nombre.toLowerCase().trim();
        Ikon ikon = MAPA_IKONS.get(clave);
        if (ikon != null) {
            try {
                FontIcon fi = FontIcon.of(ikon, tamano, color != null ? color : Theme.getTexto());
                return fi;
            } catch (Exception ignored) {}
        }
        return crearFallbackVectorial(clave, tamano, color);
    }

    // Metodos directos de conveniencia
    public static Icon plane(int tamano, Color color) { return of("plane", tamano, color); }
    public static Icon building(int tamano, Color color) { return of("building", tamano, color); }
    public static Icon luggage(int tamano, Color color) { return of("luggage", tamano, color); }
    public static Icon radar(int tamano, Color color) { return of("radar", tamano, color); }
    public static Icon shield(int tamano, Color color) { return of("shield", tamano, color); }
    public static Icon warning(int tamano, Color color) { return of("warning", tamano, color); }
    public static Icon eye(int tamano, Color color) { return of("eye", tamano, color); }
    public static Icon eyeSlash(int tamano, Color color) { return of("eye_slash", tamano, color); }
    public static Icon pencil(int tamano, Color color) { return of("pencil", tamano, color); }
    public static Icon more(int tamano, Color color) { return of("more", tamano, color); }
    public static Icon database(int tamano, Color color) { return of("database", tamano, color); }
    public static Icon settings(int tamano, Color color) { return of("settings", tamano, color); }
    public static Icon check(int tamano, Color color) { return of("check", tamano, color); }
    public static Icon sun(int tamano, Color color) { return of("sun", tamano, color); }
    public static Icon sunset(int tamano, Color color) { return of("sunset", tamano, color); }
    public static Icon moon(int tamano, Color color) { return of("moon", tamano, color); }
    public static Icon users(int tamano, Color color) { return of("users", tamano, color); }

    // Dibuja un punto LED o marcador de color vectorial antialiased
    public static Icon dot(int diametro, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color != null ? color : Theme.getSuccess());
                g2.fillOval(x + 1, y + (diametro > 8 ? 2 : 1), diametro - 2, diametro - 2);
                g2.dispose();
            }

            @Override public int getIconWidth() { return diametro; }
            @Override public int getIconHeight() { return diametro; }
        };
    }

    // Genera un bullet vectorial SVG de alta precision para las caracteristicas de las tarjetas
    public static Icon bulletSvg(int tamano, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color != null ? color : Theme.getAccentRolActivo());

                // Dibujar checkmark vectorial de precision
                Path2D.Float p = new Path2D.Float();
                float s = tamano;
                p.moveTo(x + s * 0.20f, y + s * 0.52f);
                p.lineTo(x + s * 0.42f, y + s * 0.74f);
                p.lineTo(x + s * 0.82f, y + s * 0.28f);
                g2.setStroke(new java.awt.BasicStroke(Math.max(1.5f, s * 0.14f), java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
                g2.draw(p);
                g2.dispose();
            }

            @Override public int getIconWidth() { return tamano; }
            @Override public int getIconHeight() { return tamano; }
        };
    }

    private static Icon crearIconoVacio(int s) {
        return new Icon() {
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {}
            @Override public int getIconWidth() { return s; }
            @Override public int getIconHeight() { return s; }
        };
    }

    private static Icon crearFallbackVectorial(String clave, int tamano, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color != null ? color : Theme.getTexto());
                int pad = Math.max(2, tamano / 6);
                g2.fillOval(x + pad, y + pad, tamano - (pad * 2), tamano - (pad * 2));
                g2.dispose();
            }

            @Override public int getIconWidth() { return tamano; }
            @Override public int getIconHeight() { return tamano; }
        };
    }
}
