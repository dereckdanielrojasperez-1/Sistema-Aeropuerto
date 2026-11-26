// Glassmorphism Container para CORPAC Smart Airport
package com.aeropuerto.vista.tema;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/**
 * Contenedor reutilizable de Vidrio Esmerilado (Glassmorphism) para CORPAC S.A.
 * Proporciona esquinas redondeadas, fondo translúcido con desenfoque simulado,
 * borde luminoso de 1px, línea de highlight superior y doble sombra suave por capas.
 */
public class GlassPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private int radioEsquinas = 16;
    private Color colorAcentoPersonalizado = null;
    private boolean dibujarHighlight = true;
    private boolean sombraSuave = true;
    private float opacidadFondo = 0.82f;

    public GlassPanel() {
        this(16);
    }

    public GlassPanel(int radioEsquinas) {
        super();
        this.radioEsquinas = radioEsquinas;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
    }

    public GlassPanel(java.awt.LayoutManager layout) {
        this(layout, 16);
    }

    public GlassPanel(java.awt.LayoutManager layout, int radioEsquinas) {
        super(layout);
        this.radioEsquinas = radioEsquinas;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
    }

    public void setRadioEsquinas(int radio) {
        this.radioEsquinas = radio;
        repaint();
    }

    public int getRadioEsquinas() {
        return radioEsquinas;
    }

    public void setColorAcento(Color acento) {
        this.colorAcentoPersonalizado = acento;
        repaint();
    }

    public void setOpacidadFondo(float opacidad) {
        this.opacidadFondo = Math.max(0.1f, Math.min(1.0f, opacidad));
        repaint();
    }

    public void setDibujarHighlight(boolean highlight) {
        this.dibujarHighlight = highlight;
        repaint();
    }

    public void setSombraSuave(boolean sombra) {
        this.sombraSuave = sombra;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int ancho = getWidth();
        int alto = getHeight();
        if (ancho <= 0 || alto <= 0) {
            g2.dispose();
            return;
        }

        int margenSombra = sombraSuave ? 2 : 0;
        int x = margenSombra;
        int y = margenSombra;
        int w = ancho - (margenSombra * 2);
        int h = alto - (margenSombra * 2);

        boolean oscuro = Theme.esModoOscuro();

        // 1. Doble sombra suave por capas concéntricas (sin filtros de desenfoque costosos)
        if (sombraSuave && oscuro) {
            g2.setColor(new Color(0, 0, 0, 18));
            g2.drawRoundRect(x - 2, y - 1, w + 3, h + 3, radioEsquinas + 4, radioEsquinas + 4);
            g2.setColor(new Color(0, 0, 0, 32));
            g2.drawRoundRect(x - 1, y, w + 1, h + 2, radioEsquinas + 2, radioEsquinas + 2);
        } else if (sombraSuave) {
            g2.setColor(new Color(0, 0, 0, 12));
            g2.drawRoundRect(x - 1, y, w + 1, h + 2, radioEsquinas + 2, radioEsquinas + 2);
        }

        // 2. Fondo translúcido esmerilado
        Color baseSuperficie = oscuro ? new Color(0x16, 0x1B, 0x22) : new Color(0xFF, 0xFF, 0xFF);
        int alphaFondo = (int) (opacidadFondo * 255);
        Color colorFondo = new Color(baseSuperficie.getRed(), baseSuperficie.getGreen(), baseSuperficie.getBlue(), alphaFondo);

        RoundRectangle2D forma = new RoundRectangle2D.Float(x, y, w, h, radioEsquinas, radioEsquinas);
        g2.setColor(colorFondo);
        g2.fill(forma);

        // 3. Tinte sutil del acento si está configurado (máximo 8-10% alfa)
        if (colorAcentoPersonalizado != null) {
            Color tinte = new Color(colorAcentoPersonalizado.getRed(), colorAcentoPersonalizado.getGreen(),
                    colorAcentoPersonalizado.getBlue(), oscuro ? 18 : 22);
            g2.setColor(tinte);
            g2.fill(forma);
        }

        // 4. Borde luminoso de 1 pixel
        if (oscuro) {
            if (colorAcentoPersonalizado != null) {
                // Borde con toque acentuado
                g2.setColor(new Color(colorAcentoPersonalizado.getRed(), colorAcentoPersonalizado.getGreen(),
                        colorAcentoPersonalizado.getBlue(), 60));
            } else {
                // Borde blanco sutil institucional (14% alfa)
                g2.setColor(new Color(255, 255, 255, 36));
            }
        } else {
            g2.setColor(new Color(0xD0, 0xD7, 0xDE));
        }
        g2.setStroke(new BasicStroke(1.0f));
        g2.draw(forma);

        // 5. Línea de Highlight superior sutil (reflejo de vidrio)
        if (dibujarHighlight && h > 10) {
            int highlightAlpha = oscuro ? 26 : 90;
            g2.setColor(new Color(255, 255, 255, highlightAlpha));
            g2.setStroke(new BasicStroke(1.0f));
            // Trazar arco superior reflectivo
            int arcOffset = Math.max(6, radioEsquinas / 2);
            g2.drawLine(x + arcOffset, y + 1, x + w - arcOffset, y + 1);
        }

        g2.dispose();
        super.paintChildren(g);
    }

    /**
     * Crea un apartado/sección temática de vidrio con encabezado estilizado y píldora distintiva.
     * Ideal para organizar grupos de botones sin conflictos visuales.
     */
    public static GlassPanel crearApartado(String tituloSeccion, JComponent contenido) {
        return crearApartado(tituloSeccion, null, null, contenido);
    }

    public static GlassPanel crearApartado(String tituloSeccion, Color colorAcento, JComponent contenido) {
        return crearApartado(tituloSeccion, null, colorAcento, contenido);
    }

    public static GlassPanel crearApartado(String tituloSeccion, String subtitulo, Color colorAcento, JComponent contenido) {
        GlassPanel panel = new GlassPanel(new BorderLayout(0, 4), 14);
        panel.setColorAcento(colorAcento);

        JPanel pHeader = new JPanel(new GridLayout(subtitulo != null && !subtitulo.trim().isEmpty() ? 2 : 1, 1, 0, 2));
        pHeader.setOpaque(false);
        pHeader.setBorder(BorderFactory.createEmptyBorder(2, 4, 4, 4));

        JPanel pTituloWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pTituloWrapper.setOpaque(false);

        // Píldora o indicador de acento (renderizado vectorial sin depender de fuentes)
        Color acentoEfectivo = (colorAcento != null) ? colorAcento : Theme.getAccentRolActivo();
        JComponent pill = new JComponent() {
            private static final long serialVersionUID = 1L;
            @Override
            public Dimension getPreferredSize() { return new Dimension(10, 14); }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(acentoEfectivo);
                g2.fillOval(1, 4, 7, 7);
                g2.dispose();
            }
        };

        JLabel lblTit = new JLabel(tituloSeccion);
        lblTit.setFont(FontManager.getBarlowCondensed(Font.BOLD, 12.5f));
        lblTit.setForeground(Theme.getTexto());

        pTituloWrapper.add(pill);
        pTituloWrapper.add(lblTit);

        pHeader.add(pTituloWrapper);

        if (subtitulo != null && !subtitulo.trim().isEmpty()) {
            JPanel pSubWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
            pSubWrapper.setOpaque(false);
            JLabel lblSub = new JLabel(subtitulo);
            lblSub.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 10f));
            lblSub.setForeground(Theme.getSecundario());
            pSubWrapper.add(lblSub);
            pHeader.add(pSubWrapper);
        }

        panel.add(pHeader, BorderLayout.NORTH);
        if (contenido != null) {
            contenido.setOpaque(false);
            panel.add(contenido, BorderLayout.CENTER);
        }

        return panel;
    }
}
