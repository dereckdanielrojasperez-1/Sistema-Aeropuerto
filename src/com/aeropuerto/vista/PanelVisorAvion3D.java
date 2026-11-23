package com.aeropuerto.vista;

import com.aeropuerto.vista.tema.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * Visualizador de Ficha Técnica Aeronáutica de Alta Definición (Airbus A320neo / Flota CORPAC S.A.).
 * Renderiza imágenes de alta resolución fotográficas y planos CAD técnicos con soporte para
 * zoom dinámico (0.5x a 3.5x), desplazamiento interactivo (pan), cuadrícula aeronáutica
 * y alternancia entre vista fotográfica real y plano esquemático de ingeniería.
 */
public class PanelVisorAvion3D extends JPanel {
    private static final long serialVersionUID = 1L;

    // Imágenes de alta resolución
    private BufferedImage imagenFotoHD;
    private BufferedImage imagenBlueprint;
    private boolean modoBlueprint = false;

    // Control de navegación interactiva (Zoom y Pan)
    private double escalaZoom = 1.0;
    private int panX = 0;
    private int panY = 0;
    private Point ultimoRaton = null;

    // Clases auxiliares para compatibilidad con código existente
    public static class Punto3D {
        public double x, y, z;
        public Punto3D(double x, double y, double z) {
            this.x = x; this.y = y; this.z = z;
        }
    }

    public static class Cara3D {
        public int[] indices;
        public Color colorBase;
        public double profZ;
        public double[] normal;
        public Cara3D(int[] indices, Color colorBase) {
            this.indices = indices; this.colorBase = colorBase;
        }
    }

    public PanelVisorAvion3D() {
        setBackground(new Color(11, 19, 41)); // Azul marino aeroespacial
        setOpaque(true);
        cargarRecursosImagenes();

        // Control de desplazamiento (Pan) mediante arrastre del ratón
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ultimoRaton = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                ultimoRaton = null;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    reiniciarCamara();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (ultimoRaton != null) {
                    int dx = e.getX() - ultimoRaton.x;
                    int dy = e.getY() - ultimoRaton.y;
                    panX += dx;
                    panY += dy;
                    ultimoRaton = e.getPoint();
                    repaint();
                }
            }
        });

        // Control de Zoom dinámico mediante la rueda del ratón
        addMouseWheelListener(e -> {
            int rot = e.getWheelRotation();
            if (rot < 0) {
                escalaZoom = Math.min(3.5, escalaZoom * 1.15);
            } else {
                escalaZoom = Math.max(0.5, escalaZoom / 1.15);
            }
            repaint();
        });
    }

    private void cargarRecursosImagenes() {
        try {
            imagenFotoHD = cargarImagen("airbus_a320neo_hd.jpg");
            if (imagenFotoHD == null) {
                imagenFotoHD = cargarImagen("avion_a320.jpg");
            }
            imagenBlueprint = cargarImagen("airbus_a320neo_blueprint.jpg");
        } catch (Exception ex) {
            System.err.println("Aviso: No se pudieron cargar las imágenes de alta resolución: " + ex.getMessage());
        }
    }

    private BufferedImage cargarImagen(String nombre) {
        try {
            InputStream is = getClass().getResourceAsStream(nombre);
            if (is == null) {
                is = getClass().getResourceAsStream("/" + nombre);
            }
            if (is == null) {
                is = getClass().getResourceAsStream("/com/aeropuerto/vista/" + nombre);
            }
            if (is != null) {
                return ImageIO.read(is);
            }
            // Intentar carga directa de archivo
            java.io.File f = new java.io.File("src/com/aeropuerto/vista/" + nombre);
            if (f.exists()) {
                return ImageIO.read(f);
            }
        } catch (Exception ignored) {}
        return null;
    }

    public void alternarAnimacion() {
        alternarModoAlambre();
    }

    public void alternarModoAlambre() {
        this.modoBlueprint = !this.modoBlueprint;
        repaint();
    }

    public void reiniciarCamara() {
        this.escalaZoom = 1.0;
        this.panX = 0;
        this.panY = 0;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();

        // 1. Fondo degradado técnico espacial
        GradientPaint gp = new GradientPaint(0, 0, new Color(11, 19, 41), 0, h, new Color(5, 10, 24));
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);

        // 2. Cuadrícula de ingeniería
        g2.setColor(new Color(30, 58, 102, 35));
        int grid = 40;
        for (int x = 0; x < w; x += grid) {
            g2.drawLine(x, 0, x, h);
        }
        for (int y = 0; y < h; y += grid) {
            g2.drawLine(0, y, w, y);
        }

        // 3. Renderizar imagen activa (Fotografía HD o Plano CAD)
        BufferedImage imgActual = modoBlueprint ? imagenBlueprint : imagenFotoHD;
        if (imgActual == null && imagenFotoHD != null) imgActual = imagenFotoHD;
        if (imgActual == null && imagenBlueprint != null) imgActual = imagenBlueprint;

        if (imgActual != null) {
            int imgW = imgActual.getWidth();
            int imgH = imgActual.getHeight();

            // Ajuste de escala base para caber dentro del viewport
            double escalaBase = Math.min((double) (w - 40) / imgW, (double) (h - 100) / imgH);
            double escalaFinal = escalaBase * escalaZoom;

            int drawW = (int) (imgW * escalaFinal);
            int drawH = (int) (imgH * escalaFinal);
            int x = (w - drawW) / 2 + panX;
            int y = (h - drawH) / 2 + panY - 8;

            // Marco decorativo de radar
            g2.drawImage(imgActual, x, y, drawW, drawH, null);

            // Borde sutil y esquinas de mira técnica
            g2.setColor(new Color(56, 189, 248, 70));
            g2.drawRoundRect(x, y, drawW, drawH, 6, 6);

            // Marcadores de mira en esquinas
            int cornerLen = 16;
            g2.setColor(new Color(56, 189, 248, 180));
            // Top-left
            g2.drawLine(x - 4, y - 4, x + cornerLen, y - 4);
            g2.drawLine(x - 4, y - 4, x - 4, y + cornerLen);
            // Top-right
            g2.drawLine(x + drawW + 4, y - 4, x + drawW - cornerLen, y - 4);
            g2.drawLine(x + drawW + 4, y - 4, x + drawW + 4, y + cornerLen);
            // Bottom-left
            g2.drawLine(x - 4, y + drawH + 4, x + cornerLen, y + drawH + 4);
            g2.drawLine(x - 4, y + drawH + 4, x - 4, y + drawH - cornerLen);
            // Bottom-right
            g2.drawLine(x + drawW + 4, y + drawH + 4, x + drawW - cornerLen, y + drawH + 4);
            g2.drawLine(x + drawW + 4, y + drawH + 4, x + drawW + 4, y + drawH - cornerLen);
        } else {
            // Mensaje si no cargó imagen
            g2.setColor(new Color(148, 163, 184));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            String msg = "AIRBUS A320neo - ESPECIFICACIÓN TÉCNICA OFICIAL (CORPAC S.A.)";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2);
        }

        // 4. HUD Superior Izquierdo: Estado de Telemetría
        dibujarHud(g2, w, h);

        g2.dispose();
    }

    private void dibujarHud(Graphics2D g2, int w, int h) {
        // Tag superior de aeronave
        g2.setColor(new Color(15, 23, 42, 220));
        g2.fillRoundRect(14, 12, 380, 52, 8, 8);
        g2.setColor(new Color(30, 58, 102));
        g2.drawRoundRect(14, 12, 380, 52, 8, 8);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.setColor(new Color(251, 191, 36)); // Amber Gold
        String tituloModo = modoBlueprint ? "AIRBUS A320neo - PLANO TÉCNICO CAD Y DIMENSIONES" : "AIRBUS A320neo - FICHA FOTOGRÁFICA DE ALTA DEFINICIÓN";
        g2.drawString(tituloModo, 24, 30);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.setColor(new Color(226, 232, 240));
        String detalle = String.format("Zoom: %.2fx | Pan: (%+d, %+d) | Envergadura: 35.80 m | Longitud: 37.57 m",
                escalaZoom, panX, panY);
        g2.drawString(detalle, 24, 48);

        // Tag superior derecho: Certificación CORPAC
        String badge = "CORPAC FLOTA OFICIAL · DGAC PERÚ";
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        FontMetrics fmBadge = g2.getFontMetrics();
        int bW = fmBadge.stringWidth(badge) + 20;
        int bX = w - bW - 14;
        g2.setColor(new Color(15, 23, 42, 220));
        g2.fillRoundRect(bX, 12, bW, 26, 6, 6);
        g2.setColor(new Color(56, 189, 248, 120));
        g2.drawRoundRect(bX, 12, bW, 26, 6, 6);
        g2.setColor(new Color(56, 189, 248));
        g2.drawString(badge, bX + 10, 29);

        // Barra inferior instructiva
        int barH = 26;
        g2.setColor(new Color(11, 19, 41, 230));
        g2.fillRect(0, h - barH, w, barH);
        g2.setColor(new Color(30, 58, 102));
        g2.drawLine(0, h - barH, w, h - barH);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(new Color(148, 163, 184));
        String instruccion = "Arrastre para desplazar · Rueda de scroll para Zoom · Doble clic para reiniciar · Alternar botón para Plano CAD / Foto HD";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(instruccion, (w - fm.stringWidth(instruccion)) / 2, h - 8);
    }
}
