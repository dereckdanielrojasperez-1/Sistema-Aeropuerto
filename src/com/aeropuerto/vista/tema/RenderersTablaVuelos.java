// HUMANIZACION UI
package com.aeropuerto.vista.tema;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Renderers de alta legibilidad para la Tabla de Vuelos
public final class RenderersTablaVuelos {

    private RenderersTablaVuelos() {}

    public static void aplicarEstilosTabla(JTable tabla) {
        if (tabla == null) return;

        tabla.setRowHeight(32);
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(Theme.getBorder());
        tabla.setBackground(Theme.getSurface());
        tabla.setForeground(Theme.getTexto());
        tabla.setSelectionBackground(Theme.getPasajeroSoft());
        tabla.setSelectionForeground(Theme.getTexto());

        // Header
        JTableHeader th = tabla.getTableHeader();
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 32));
        th.setDefaultRenderer(new TableHeaderRenderer());
        th.setReorderingAllowed(false);

        // Hover tracking
        final int[] filaHover = {-1};
        tabla.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int r = tabla.rowAtPoint(e.getPoint());
                if (r != filaHover[0]) {
                    filaHover[0] = r;
                    tabla.repaint();
                }
            }
        });
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                filaHover[0] = -1;
                tabla.repaint();
            }
        });

        // Asignar renderers por columna
        BaseZebraRenderer baseRenderer = new BaseZebraRenderer(filaHover);
        MonoZebraRenderer monoRenderer = new MonoZebraRenderer(filaHover);
        BadgeEstadoRenderer badgeRenderer = new BadgeEstadoRenderer(filaHover);
        MiniBarraAsientosRenderer asientosRenderer = new MiniBarraAsientosRenderer(filaHover);

        int colCount = tabla.getColumnCount();
        for (int c = 0; c < colCount; c++) {
            String colName = tabla.getColumnName(c);
            if ("Código".equalsIgnoreCase(colName) || "Salida".equalsIgnoreCase(colName) ||
                "Llegada".equalsIgnoreCase(colName) || "Duración".equalsIgnoreCase(colName)) {
                tabla.getColumnModel().getColumn(c).setCellRenderer(monoRenderer);
            } else if ("Estado Operativo".equalsIgnoreCase(colName) || "Estado".equalsIgnoreCase(colName)) {
                tabla.getColumnModel().getColumn(c).setCellRenderer(badgeRenderer);
            } else if ("Asientos Libres".equalsIgnoreCase(colName) || "Asientos".equalsIgnoreCase(colName)) {
                tabla.getColumnModel().getColumn(c).setCellRenderer(asientosRenderer);
            } else {
                tabla.getColumnModel().getColumn(c).setCellRenderer(baseRenderer);
            }
        }
    }

    // Renderer del Encabezado: Fondo elevated, texto Barlow en muted
    private static class TableHeaderRenderer extends DefaultTableCellRenderer {
        public TableHeaderRenderer() {
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.getBorder()),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBackground(Theme.getElevated());
            setForeground(Theme.getMuted());
            setFont(FontManager.getBarlowCondensed(Font.BOLD, 11f));
            if (value != null) {
                setText(value.toString().toUpperCase());
            }
            return this;
        }
    }

    // Renderer base con Zebra sutil y hover
    private static class BaseZebraRenderer extends DefaultTableCellRenderer {
        private final int[] filaHover;

        public BaseZebraRenderer(int[] filaHover) {
            this.filaHover = filaHover;
            setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(FontManager.getIBMPlexSans(Font.PLAIN, 11f));

            if (value == null || value.toString().trim().isEmpty()) {
                setText("-");
            } else {
                setText(value.toString());
            }

            if (isSelected) {
                setBackground(Theme.getPasajeroSoft());
                setForeground(Theme.getTexto());
            } else if (filaHover != null && filaHover[0] == row) {
                setBackground(Theme.getElevated());
                setForeground(Theme.getTexto());
            } else {
                setBackground((row % 2 == 0) ? Theme.getSurface() : Theme.getBg());
                setForeground(Theme.getTexto());
            }
            return this;
        }
    }

    // Renderer para Codigos y Horas en IBM Plex Mono
    private static class MonoZebraRenderer extends BaseZebraRenderer {
        public MonoZebraRenderer(int[] filaHover) {
            super(filaHover);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(FontManager.getIBMPlexMono(Font.PLAIN, 11f));
            return c;
        }
    }

    // Renderer semantico para el Estado Operativo con capsula de color
    private static class BadgeEstadoRenderer extends JPanel implements TableCellRenderer {
        private final int[] filaHover;
        private String textoEstado = "";
        private boolean isSelected = false;
        private int rowIdx = 0;

        public BadgeEstadoRenderer(int[] filaHover) {
            this.filaHover = filaHover;
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            this.textoEstado = (value != null) ? value.toString() : "";
            this.isSelected = isSelected;
            this.rowIdx = row;

            if (isSelected) {
                setBackground(Theme.getPasajeroSoft());
            } else if (filaHover != null && filaHover[0] == row) {
                setBackground(Theme.getElevated());
            } else {
                setBackground((row % 2 == 0) ? Theme.getSurface() : Theme.getBg());
            }
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            int w = getWidth();
            int h = getHeight();

            Color badgeBg;
            Color badgeFg;
            String st = textoEstado.toLowerCase();

            if (st.contains("vuelo")) {
                badgeBg = Theme.getInfoSoft();
                badgeFg = Theme.getInfo();
            } else if (st.contains("abordando")) {
                badgeBg = Theme.getWarningSoft();
                badgeFg = Theme.getWarning();
            } else if (st.contains("tiempo")) {
                badgeBg = Theme.getSuccessSoft();
                badgeFg = Theme.getSuccess();
            } else {
                badgeBg = Theme.getDangerSoft();
                badgeFg = Theme.getDanger();
            }

            // Dimensiones de la capsula
            g2.setFont(FontManager.getIBMPlexSans(Font.BOLD, 10f));
            int strW = g2.getFontMetrics().stringWidth(textoEstado);
            int pillW = strW + 16;
            int pillH = 20;
            int pillX = 8;
            int pillY = (h - pillH) / 2;

            g2.setColor(badgeBg);
            g2.fillRoundRect(pillX, pillY, pillW, pillH, 8, 8);
            g2.setColor(badgeFg);
            g2.drawString(textoEstado, pillX + 8, pillY + 14);

            g2.dispose();
        }
    }

    // Renderer para la columna Asientos Libres con mini-barra de ocupacion
    private static class MiniBarraAsientosRenderer extends JPanel implements TableCellRenderer {
        private final int[] filaHover;
        private String texto = "0 / 50";
        private int asientosLibres = 0;
        private int totalAsientos = 50;

        public MiniBarraAsientosRenderer(int[] filaHover) {
            this.filaHover = filaHover;
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            this.texto = (value != null) ? value.toString() : "0 / 50";
            try {
                String[] partes = texto.replace("libres", "").trim().split("/");
                if (partes.length == 2) {
                    this.asientosLibres = Integer.parseInt(partes[0].trim());
                    this.totalAsientos = Integer.parseInt(partes[1].trim());
                }
            } catch (Exception ignored) {}

            if (isSelected) {
                setBackground(Theme.getPasajeroSoft());
            } else if (filaHover != null && filaHover[0] == row) {
                setBackground(Theme.getElevated());
            } else {
                setBackground((row % 2 == 0) ? Theme.getSurface() : Theme.getBg());
            }
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            int w = getWidth();
            int h = getHeight();

            // Texto en IBM Plex Mono
            g2.setFont(FontManager.getIBMPlexMono(Font.PLAIN, 11f));
            g2.setColor(Theme.getTexto());
            g2.drawString(texto, 8, (h / 2) + 4);

            // Mini barra proporcional
            int xBarra = 75;
            int yBarra = (h - 6) / 2;
            int anchoBarra = Math.max(20, w - xBarra - 12);
            int altoBarra = 5;

            g2.setColor(Theme.getInput());
            g2.fillRoundRect(xBarra, yBarra, anchoBarra, altoBarra, 4, 4);

            if (totalAsientos > 0) {
                int anchoRelleno = Math.max(2, (anchoBarra * asientosLibres) / totalAsientos);
                Color colorRelleno = (asientosLibres > 25) ? Theme.getSuccess() : (asientosLibres > 10) ? Theme.getWarning() : Theme.getDanger();
                g2.setColor(colorRelleno);
                g2.fillRoundRect(xBarra, yBarra, anchoRelleno, altoBarra, 4, 4);
            }

            g2.dispose();
        }
    }
}
