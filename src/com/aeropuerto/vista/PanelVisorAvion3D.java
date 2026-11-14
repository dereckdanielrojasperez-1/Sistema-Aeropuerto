package com.aeropuerto.vista;

import com.aeropuerto.vista.tema.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Visualizador 3D Interactivo de Aeronave Comercial (Airbus A320neo / Flota CORPAC).
 * Motor de renderizado tridimensional vectorial con sombreado poligonal difuso (Lambertian),
 * proyección en perspectiva, profundidad con algoritmo de pintor, rotación orbital 360°
 * por arrastre de ratón, zoom dinámico con rueda y modos sólido/alambre.
 */
public class PanelVisorAvion3D extends JPanel {
    private static final long serialVersionUID = 1L;

    // Ángulos de orientación orbital (radianes)
    private double rotX = 0.35; // Inclinación (Pitch)
    private double rotY = -0.75; // Rotación lateral (Yaw)
    private double rotZ = 0.0;   // Alabeo (Roll)
    private double escalaZoom = 1.0;

    // Control de interacción con el ratón
    private Point ultimoRaton = null;
    private boolean animacionAuto = true;
    private boolean modoAlambre = false;
    private final Timer timerGiro;

    // Datos geométricos 3D
    private final List<Punto3D> verticesBase = new ArrayList<>();
    private final List<Cara3D> caras = new ArrayList<>();

    // Vector de luz direccional normalizado (desde arriba a la derecha y frontal)
    private static final double[] LUZ = normalizar(new double[]{0.5, 0.8, -0.6});

    public static class Punto3D {
        public double x, y, z;
        public Punto3D(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static class Cara3D {
        public int[] indices;
        public Color colorBase;
        public double profZ; // Profundidad calculada para el algoritmo del pintor
        public double[] normal;

        public Cara3D(int[] indices, Color colorBase) {
            this.indices = indices;
            this.colorBase = colorBase;
        }
    }

    public PanelVisorAvion3D() {
        setBackground(new Color(13, 17, 23)); // DARK_BG
        setOpaque(true);
        construirGeometriaA320();

        // Control de rotación mediante arrastre del ratón
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ultimoRaton = e.getPoint();
                animacionAuto = false; // Pausa el giro automático al interactuar
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                ultimoRaton = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (ultimoRaton != null) {
                    int dx = e.getX() - ultimoRaton.x;
                    int dy = e.getY() - ultimoRaton.y;
                    rotY += dx * 0.012;
                    rotX += dy * 0.012;
                    // Limitar pitch para evitar inversión de cámara
                    rotX = Math.max(-1.4, Math.min(1.4, rotX));
                    ultimoRaton = e.getPoint();
                    repaint();
                }
            }
        });

        // Control de Zoom mediante rueda del ratón
        addMouseWheelListener(e -> {
            int rot = e.getWheelRotation();
            if (rot < 0) {
                escalaZoom = Math.min(2.5, escalaZoom * 1.12);
            } else {
                escalaZoom = Math.max(0.45, escalaZoom / 1.12);
            }
            repaint();
        });

        // Animación de rotación continua y suave cuando no se interactúa
        timerGiro = new Timer(35, e -> {
            if (animacionAuto && isShowing()) {
                rotY += 0.008;
                if (rotY > Math.PI * 2) rotY -= Math.PI * 2;
                repaint();
            }
        });
        timerGiro.start();
    }

    public void alternarAnimacion() {
        this.animacionAuto = !this.animacionAuto;
        repaint();
    }

    public void alternarModoAlambre() {
        this.modoAlambre = !this.modoAlambre;
        repaint();
    }

    public void reiniciarCamara() {
        this.rotX = 0.35;
        this.rotY = -0.75;
        this.escalaZoom = 1.0;
        this.animacionAuto = true;
        repaint();
    }

    /**
     * Construcción de la geometría tridimensional del Airbus A320neo:
     * Fuselaje aerodinámico, alas en flecha con sharklets, turbofanes bajo las alas,
     * estabilizadores horizontales y empenaje vertical.
     */
    private void construirGeometriaA320() {
        verticesBase.clear();
        caras.clear();

        Color colFuselaje = new Color(225, 232, 240);
        Color colFuselajeOsc = new Color(180, 192, 205);
        Color colAlas = new Color(148, 163, 184);
        Color colTurbina = new Color(71, 85, 105);
        Color colVentana = new Color(56, 189, 248);
        Color colCorpacAzul = new Color(13, 59, 102);
        Color colCorpacDorado = new Color(227, 179, 65);

        // --- FUSELAJE (Secciones a lo largo del eje Z: Z=-160 a Z=160) ---
        // Nariz, cabina de mando, fuselaje medio, cono de cola
        int[] seccionesZ = {-170, -145, -110, -50, 20, 80, 130, 165};
        double[] radiosX = {   0,   12,   19,  20, 20, 19,  14,   2};
        double[] radiosY = {  -2,   10,   18,  19, 19, 18,  13,   2};
        int numSeg = 10;

        int offsetVertice = 0;
        for (int s = 0; s < seccionesZ.length; s++) {
            double z = seccionesZ[s];
            double rx = radiosX[s];
            double ry = radiosY[s];
            for (int i = 0; i < numSeg; i++) {
                double ang = i * (Math.PI * 2 / numSeg);
                double x = Math.cos(ang) * rx;
                double y = Math.sin(ang) * ry;
                // Acomodar piso plano del fuselaje
                if (y < -ry * 0.7) y = -ry * 0.7;
                verticesBase.add(new Punto3D(x, y, z));
            }
        }

        // Crear caras del fuselaje conectando anillos adyacentes
        for (int s = 0; s < seccionesZ.length - 1; s++) {
            int anillo1 = s * numSeg;
            int anillo2 = (s + 1) * numSeg;
            for (int i = 0; i < numSeg; i++) {
                int next = (i + 1) % numSeg;
                int v1 = anillo1 + i;
                int v2 = anillo1 + next;
                int v3 = anillo2 + next;
                int v4 = anillo2 + i;

                Color c = colFuselaje;
                // Parabrisas de cabina en sección frontal superior
                if (s == 1 && (i == 2 || i == 3)) {
                    c = colVentana;
                } else if (s >= 3 && s <= 5 && (i == 1 || i == 4)) {
                    // Banda decorativa CORPAC
                    c = (s == 4) ? colCorpacDorado : colCorpacAzul;
                } else if (i >= 5 && i <= 8) {
                    c = colFuselajeOsc; // Panza inferior
                }
                caras.add(new Cara3D(new int[]{v1, v2, v3, v4}, c));
            }
        }

        // --- ALAS EN FLECHA (Swept Wings) ---
        int alaBaseL = verticesBase.size();
        // Ala Izquierda (vistas desde cola hacia nariz: X < 0)
        verticesBase.add(new Punto3D(-18,  -2,  -30)); // Raíz frente 0
        verticesBase.add(new Punto3D(-145,  14,   35)); // Punta frente 1
        verticesBase.add(new Punto3D(-148,  28,   45)); // Sharklet arriba 2
        verticesBase.add(new Punto3D(-138,  12,   55)); // Punta atrás 3
        verticesBase.add(new Punto3D(-18,  -2,   45)); // Raíz atrás 4
        caras.add(new Cara3D(new int[]{alaBaseL, alaBaseL+1, alaBaseL+3, alaBaseL+4}, colAlas));
        caras.add(new Cara3D(new int[]{alaBaseL+4, alaBaseL+3, alaBaseL+1, alaBaseL}, colAlas.darker()));
        // Sharklet izquierdo
        caras.add(new Cara3D(new int[]{alaBaseL+1, alaBaseL+2, alaBaseL+3}, colCorpacAzul));

        int alaBaseR = verticesBase.size();
        // Ala Derecha (X > 0)
        verticesBase.add(new Punto3D( 18,  -2,  -30)); // Raíz frente 0
        verticesBase.add(new Punto3D( 145,  14,   35)); // Punta frente 1
        verticesBase.add(new Punto3D( 148,  28,   45)); // Sharklet arriba 2
        verticesBase.add(new Punto3D( 138,  12,   55)); // Punta atrás 3
        verticesBase.add(new Punto3D( 18,  -2,   45)); // Raíz atrás 4
        caras.add(new Cara3D(new int[]{alaBaseR, alaBaseR+1, alaBaseR+3, alaBaseR+4}, colAlas));
        caras.add(new Cara3D(new int[]{alaBaseR+4, alaBaseR+3, alaBaseR+1, alaBaseR}, colAlas.darker()));
        // Sharklet derecho
        caras.add(new Cara3D(new int[]{alaBaseR+1, alaBaseR+2, alaBaseR+3}, colCorpacAzul));

        // --- MOTORES TURBOFÁN CFM LEAP-1A ---
        agregarTurbina(-55, -16, 2, colTurbina);
        agregarTurbina( 55, -16, 2, colTurbina);

        // --- ESTABILIZADORES HORIZONTALES (Cola) ---
        int hTailBase = verticesBase.size();
        // Izquierdo
        verticesBase.add(new Punto3D(-10,   8,  135));
        verticesBase.add(new Punto3D(-55,  15,  162));
        verticesBase.add(new Punto3D(-50,  14,  176));
        verticesBase.add(new Punto3D( -8,   8,  160));
        caras.add(new Cara3D(new int[]{hTailBase, hTailBase+1, hTailBase+2, hTailBase+3}, colAlas));
        caras.add(new Cara3D(new int[]{hTailBase+3, hTailBase+2, hTailBase+1, hTailBase}, colAlas.darker()));

        // Derecho
        int hTailR = verticesBase.size();
        verticesBase.add(new Punto3D( 10,   8,  135));
        verticesBase.add(new Punto3D( 55,  15,  162));
        verticesBase.add(new Punto3D( 50,  14,  176));
        verticesBase.add(new Punto3D(  8,   8,  160));
        caras.add(new Cara3D(new int[]{hTailR, hTailR+1, hTailR+2, hTailR+3}, colAlas));
        caras.add(new Cara3D(new int[]{hTailR+3, hTailR+2, hTailR+1, hTailR}, colAlas.darker()));

        // --- ESTABILIZADOR VERTICAL (Deriva / Timón de Cola) ---
        int vFinBase = verticesBase.size();
        verticesBase.add(new Punto3D( -1.5,  12,  115)); // Base delantera
        verticesBase.add(new Punto3D(  1.5,  12,  115));
        verticesBase.add(new Punto3D( -0.8,  72,  160)); // Cúspide delantera
        verticesBase.add(new Punto3D(  0.8,  72,  160));
        verticesBase.add(new Punto3D(  0.0,  68,  178)); // Cúspide trasera
        verticesBase.add(new Punto3D( -1.0,  10,  170)); // Base trasera
        verticesBase.add(new Punto3D(  1.0,  10,  170));

        caras.add(new Cara3D(new int[]{vFinBase, vFinBase+2, vFinBase+4, vFinBase+5}, colCorpacAzul));
        caras.add(new Cara3D(new int[]{vFinBase+1, vFinBase+3, vFinBase+4, vFinBase+6}, colCorpacAzul));
        caras.add(new Cara3D(new int[]{vFinBase+2, vFinBase+3, vFinBase+4}, colCorpacDorado));
    }

    private void agregarTurbina(double centroX, double centroY, double centroZ, Color col) {
        int baseIdx = verticesBase.size();
        double r = 8.5;
        double len = 26.0;
        int seg = 8;
        // Anillo frontal y trasero
        for (int i = 0; i < seg; i++) {
            double ang = i * (Math.PI * 2 / seg);
            double x = centroX + Math.cos(ang) * r;
            double y = centroY + Math.sin(ang) * r;
            verticesBase.add(new Punto3D(x, y, centroZ - len/2));
            verticesBase.add(new Punto3D(x, y, centroZ + len/2));
        }
        for (int i = 0; i < seg; i++) {
            int next = (i + 1) % seg;
            int v1 = baseIdx + i * 2;
            int v2 = baseIdx + next * 2;
            int v3 = baseIdx + next * 2 + 1;
            int v4 = baseIdx + i * 2 + 1;
            caras.add(new Cara3D(new int[]{v1, v2, v3, v4}, col));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();

        // Fondo degradado tecnológico de sala de operaciones CORPAC
        GradientPaint gradFondo = new GradientPaint(0, 0, new Color(13, 17, 23), 0, h, new Color(22, 27, 34));
        g2.setPaint(gradFondo);
        g2.fillRect(0, 0, w, h);

        // Cuadrícula isométrica de fondo tipo radar / hangar
        g2.setColor(new Color(48, 54, 61, 60));
        int gridStep = 35;
        for (int x = 0; x < w; x += gridStep) g2.drawLine(x, 0, x, h);
        for (int y = 0; y < h; y += gridStep) g2.drawLine(0, y, w, y);

        // Centro y escala de perspectiva
        double cx = w / 2.0;
        double cy = h / 2.0 + 10;
        double distanciaCamara = 520.0;
        double factorEscala = Math.min(w, h) / 360.0 * 1.65 * escalaZoom;

        // Matrices de Rotación (X: pitch, Y: yaw, Z: roll)
        double cosX = Math.cos(rotX), sinX = Math.sin(rotX);
        double cosY = Math.cos(rotY), sinY = Math.sin(rotY);
        double cosZ = Math.cos(rotZ), sinZ = Math.sin(rotZ);

        // 1. Transformar todos los vértices a espacio de cámara y pantalla
        Point[] puntos2D = new Point[verticesBase.size()];
        double[] transZ = new double[verticesBase.size()];

        for (int i = 0; i < verticesBase.size(); i++) {
            Punto3D p = verticesBase.get(i);

            // Rotación Y (Yaw)
            double x1 = p.x * cosY + p.z * sinY;
            double y1 = p.y;
            double z1 = -p.x * sinY + p.z * cosY;

            // Rotación X (Pitch)
            double x2 = x1;
            double y2 = y1 * cosX - z1 * sinX;
            double z2 = y1 * sinX + z1 * cosX;

            // Proyección en perspectiva
            double zProj = z2 + distanciaCamara;
            if (zProj < 10) zProj = 10;
            double k = factorEscala * (distanciaCamara / zProj);

            int px = (int) (cx + x2 * k);
            int py = (int) (cy - y2 * k); // Invertir eje Y de pantalla
            puntos2D[i] = new Point(px, py);
            transZ[i] = z2;
        }

        // 2. Calcular profundidad promedio de cada cara para ordenamiento (Painter's Algorithm)
        for (Cara3D c : caras) {
            double sumaZ = 0;
            for (int idx : c.indices) sumaZ += transZ[idx];
            c.profZ = sumaZ / c.indices.length;
        }

        // Ordenar caras de mayor profundidad a menor (de atrás hacia adelante)
        List<Cara3D> carasOrdenadas = new ArrayList<>(caras);
        Collections.sort(carasOrdenadas, Comparator.comparingDouble((Cara3D c) -> c.profZ).reversed());

        // 3. Dibujar las caras sombreadas
        for (Cara3D c : carasOrdenadas) {
            Path2D pol = new Path2D.Double();
            Point p0 = puntos2D[c.indices[0]];
            pol.moveTo(p0.x, p0.y);
            for (int i = 1; i < c.indices.length; i++) {
                Point p = puntos2D[c.indices[i]];
                pol.lineTo(p.x, p.y);
            }
            pol.closePath();

            if (!modoAlambre) {
                // Cálculo de vector normal para iluminación difusa
                Punto3D vA = verticesBase.get(c.indices[0]);
                Punto3D vB = verticesBase.get(c.indices[1]);
                Punto3D vC = verticesBase.get(c.indices[2]);
                double[] n = calcularNormal(vA, vB, vC);

                // Rotar la normal a coordenadas de cámara
                double nx1 = n[0] * cosY + n[2] * sinY;
                double ny1 = n[1];
                double nz1 = -n[0] * sinY + n[2] * cosY;
                double nx2 = nx1;
                double ny2 = ny1 * cosX - nz1 * sinX;
                double nz2 = ny1 * sinX + nz1 * cosX;

                // Producto punto con la luz (Lambertian Diffuse)
                double dot = nx2 * LUZ[0] + ny2 * LUZ[1] + nz2 * LUZ[2];
                double iluminacion = Math.max(0.25, Math.min(1.0, 0.45 + dot * 0.55));

                Color colFinal = sombrarColor(c.colorBase, iluminacion);
                g2.setColor(colFinal);
                g2.fill(pol);

                // Borde suave de malla estructural
                g2.setColor(new Color(c.colorBase.getRed()/2, c.colorBase.getGreen()/2, c.colorBase.getBlue()/2, 80));
                g2.setStroke(new BasicStroke(1.0f));
                g2.draw(pol);
            } else {
                // Modo malla vectorial transparente
                g2.setColor(new Color(88, 166, 255, 180));
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(pol);
            }
        }

        // 4. HUD Telemetría flotante superior e inferior
        dibujarHud(g2, w, h);

        g2.dispose();
    }

    private void dibujarHud(Graphics2D g2, int w, int h) {
        // Tag superior de aeronave
        g2.setColor(new Color(22, 27, 34, 210));
        g2.fillRoundRect(14, 12, 340, 48, 8, 8);
        g2.setColor(new Color(48, 54, 61));
        g2.drawRoundRect(14, 12, 340, 48, 8, 8);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.setColor(Theme.getWarning());
        g2.drawString("AIRBUS A320neo - GEMELO 3D VECTORIAL", 24, 30);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.setColor(Theme.getTexto());
        g2.drawString(String.format("Pitch: %+.1f° | Yaw: %+.1f° | Zoom: %.2fx | 180 Asientos",
                Math.toDegrees(rotX), Math.toDegrees(rotY), escalaZoom), 24, 48);

        // Barra inferior instructiva de interacción
        int barH = 24;
        g2.setColor(new Color(22, 27, 34, 230));
        g2.fillRect(0, h - barH, w, barH);
        g2.setColor(new Color(48, 54, 61));
        g2.drawLine(0, h - barH, w, h - barH);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(new Color(148, 163, 184));
        String instruccion = "Arrastre con el ratón para rotar 360° | Rueda de scroll para Zoom | Clic para pausar giro";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(instruccion, (w - fm.stringWidth(instruccion)) / 2, h - 7);
    }

    private static double[] normalizar(double[] v) {
        double mag = Math.sqrt(v[0]*v[0] + v[1]*v[1] + v[2]*v[2]);
        if (mag == 0) return new double[]{0, 1, 0};
        return new double[]{v[0]/mag, v[1]/mag, v[2]/mag};
    }

    private static double[] calcularNormal(Punto3D a, Punto3D b, Punto3D c) {
        double ux = b.x - a.x, uy = b.y - a.y, uz = b.z - a.z;
        double vx = c.x - a.x, vy = c.y - a.y, vz = c.z - a.z;
        double nx = uy * vz - uz * vy;
        double ny = uz * vx - ux * vz;
        double nz = ux * vy - uy * vx;
        return normalizar(new double[]{nx, ny, nz});
    }

    private static Color sombrarColor(Color c, double factor) {
        int r = (int) Math.min(255, Math.max(0, c.getRed() * factor));
        int g = (int) Math.min(255, Math.max(0, c.getGreen() * factor));
        int b = (int) Math.min(255, Math.max(0, c.getBlue() * factor));
        return new Color(r, g, b, c.getAlpha());
    }
}
