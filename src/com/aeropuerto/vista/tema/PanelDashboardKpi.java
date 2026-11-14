// HUMANIZACION UI
package com.aeropuerto.vista.tema;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;

// Dashboard de 4 KPIs operacionales para Smart Airport CORPAC
public class PanelDashboardKpi extends JPanel {

    private final KpiCard tarjetaVuelos;
    private final KpiCard tarjetaOcupacion;
    private final KpiCard tarjetaEquipaje;
    private final KpiCard tarjetaPuerta;

    private int tarjetasVisibles = 0;
    private Timer temporizadorEscalonado;

    public PanelDashboardKpi() {
        setLayout(new GridBagLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(3, 12, 3, 12));
        setPreferredSize(new Dimension(960, 68));
        setMinimumSize(new Dimension(720, 62));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Card 1: Vuelos Activos (KPI prioritario con mayor peso y borde accent)
        tarjetaVuelos = new KpiCard("VUELOS ACTIVOS", "plane", "Arreglo Dinámico", true);
        tarjetaVuelos.setValor("0 vuelos");
        gbc.gridx = 0;
        gbc.weightx = 1.15; // Rompe simetria con mas peso
        add(tarjetaVuelos, gbc);

        // Card 2: Ocupacion de Cabina con barra gradiente
        tarjetaOcupacion = new KpiCard("OCUPACIÓN DE CABINA", "users", "Matriz 10x5", false);
        tarjetaOcupacion.setProgresoVisible(true);
        tarjetaOcupacion.setValor("0 / 50 (0%)");
        gbc.gridx = 1;
        gbc.weightx = 1.05;
        add(tarjetaOcupacion, gbc);

        // Card 3: Carga y Equipaje
        tarjetaEquipaje = new KpiCard("EQUIPAJE EN TRÁNSITO", "luggage", "Lista Simple IATA", false);
        tarjetaEquipaje.setValor("0.0 kg (0 maletas)");
        gbc.gridx = 2;
        gbc.weightx = 0.95;
        add(tarjetaEquipaje, gbc);

        // Card 4: Puerta Activa en Rotacion
        tarjetaPuerta = new KpiCard("PUERTA DE EMBARQUE", "radar", "Rotación Circular", false);
        tarjetaPuerta.setValor("Puerta 01 (Libre)");
        gbc.gridx = 3;
        gbc.weightx = 0.85;
        add(tarjetaPuerta, gbc);

        // Inicialmente visibles, y stagger animado en addNotify
        tarjetaVuelos.setOpacidadStagger(1.0f);
        tarjetaOcupacion.setOpacidadStagger(1.0f);
        tarjetaEquipaje.setOpacidadStagger(1.0f);
        tarjetaPuerta.setOpacidadStagger(1.0f);

        Theme.addThemeListener(this::repaint);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (isShowing()) {
            iniciarAnimacionStagger();
        } else {
            forzarVisibilidadCompleta();
        }
    }

    public void forzarVisibilidadCompleta() {
        if (temporizadorEscalonado != null && temporizadorEscalonado.isRunning()) {
            temporizadorEscalonado.stop();
        }
        tarjetasVisibles = 4;
        tarjetaVuelos.setOpacidadStagger(1.0f);
        tarjetaOcupacion.setOpacidadStagger(1.0f);
        tarjetaEquipaje.setOpacidadStagger(1.0f);
        tarjetaPuerta.setOpacidadStagger(1.0f);
        repaint();
    }

    // Entrada escalonada de 60ms por tarjeta mediante Timer
    public void iniciarAnimacionStagger() {
        tarjetasVisibles = 0;
        tarjetaVuelos.setOpacidadStagger(0.0f);
        tarjetaOcupacion.setOpacidadStagger(0.0f);
        tarjetaEquipaje.setOpacidadStagger(0.0f);
        tarjetaPuerta.setOpacidadStagger(0.0f);

        if (temporizadorEscalonado != null && temporizadorEscalonado.isRunning()) {
            temporizadorEscalonado.stop();
        }

        temporizadorEscalonado = new Timer(60, e -> {
            tarjetasVisibles++;
            if (tarjetasVisibles == 1) tarjetaVuelos.setOpacidadStagger(1.0f);
            else if (tarjetasVisibles == 2) tarjetaOcupacion.setOpacidadStagger(1.0f);
            else if (tarjetasVisibles == 3) tarjetaEquipaje.setOpacidadStagger(1.0f);
            else if (tarjetasVisibles >= 4) {
                tarjetaPuerta.setOpacidadStagger(1.0f);
                ((Timer) e.getSource()).stop();
            }
        });
        temporizadorEscalonado.setInitialDelay(20);
        temporizadorEscalonado.start();
    }

    public void setModoTecnico(boolean tech) {
        tarjetaVuelos.setModoTecnico(tech);
        tarjetaOcupacion.setModoTecnico(tech);
        tarjetaEquipaje.setModoTecnico(tech);
        tarjetaPuerta.setModoTecnico(tech);
        repaint();
    }

    // Actualizaciones de datos en tiempo real
    public void actualizarVuelos(int totalVuelos) {
        tarjetaVuelos.setValor(totalVuelos + " registrados");
    }

    public void actualizarOcupacion(int ocupados, int capacidadTotal) {
        if (capacidadTotal <= 0) capacidadTotal = 50;
        int pct = (ocupados * 100) / capacidadTotal;
        tarjetaOcupacion.setValor(ocupados + " / " + capacidadTotal + " (" + pct + "%)");
        tarjetaOcupacion.setPorcentajeProgreso(pct);
    }

    public void actualizarEquipaje(double pesoKg, int maletas, int sobrepeso) {
        String texto = String.format(java.util.Locale.US, "%.1f kg (%d bultos)", pesoKg, maletas);
        if (sobrepeso > 0) {
            texto += " [" + sobrepeso + " sobrepeso]";
        }
        tarjetaEquipaje.setValor(texto);
    }

    public void actualizarPuerta(String puertaTexto) {
        tarjetaPuerta.setValor(puertaTexto != null ? puertaTexto : "Puerta 01");
    }

    // Tarjeta individual de metrica KPI
    public static class KpiCard extends JPanel {
        private final String titulo;
        private final String iconoNombre;
        private final String sublabelTecnico;
        private final boolean esPrioritario;

        private String valor = "---";
        private boolean progresoVisible = false;
        private int porcentajeProgreso = 0;
        private float opacidadStagger = 1.0f;
        private boolean modoTecnico = false;

        public void setModoTecnico(boolean modoTecnico) {
            this.modoTecnico = modoTecnico;
            repaint();
        }

        public KpiCard(String titulo, String iconoNombre, String sublabelTecnico, boolean esPrioritario) {
            this.titulo = titulo;
            this.iconoNombre = iconoNombre;
            this.sublabelTecnico = sublabelTecnico;
            this.esPrioritario = esPrioritario;
            setOpaque(false);
            setPreferredSize(new Dimension(180, 60));
        }

        public void setValor(String val) {
            this.valor = val;
            repaint();
        }

        public void setProgresoVisible(boolean vis) {
            this.progresoVisible = vis;
            repaint();
        }

        public void setPorcentajeProgreso(int pct) {
            this.porcentajeProgreso = Math.max(0, Math.min(100, pct));
            repaint();
        }

        public void setOpacidadStagger(float op) {
            this.opacidadStagger = op;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (opacidadStagger <= 0.05f) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            if (opacidadStagger < 1.0f) {
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, opacidadStagger));
            }

            int ancho = getWidth();
            int alto = getHeight();

            // Fondo GlassPanel translúcido con radio 16 y jerarquía diferenciada
            int radioCard = 16;
            boolean oscuro = Theme.esModoOscuro();

            // Sombra suave en capas concéntricas
            if (oscuro) {
                g2.setColor(new Color(0, 0, 0, 20));
                g2.drawRoundRect(1, 2, ancho - 3, alto - 2, radioCard + 2, radioCard + 2);
            }

            Color baseColor = esPrioritario ? Theme.getElevated() : Theme.getSurface();
            int alphaCard = oscuro ? 205 : 220;
            g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alphaCard));
            g2.fillRoundRect(2, 2, ancho - 4, alto - 4, radioCard, radioCard);

            // Borde luminoso de 1px o borde prioritario accent
            if (esPrioritario) {
                g2.setColor(Theme.getPasajeroAccent());
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(2, 2, ancho - 4, alto - 4, radioCard, radioCard);
            } else {
                g2.setColor(oscuro ? new Color(255, 255, 255, 33) : Theme.getBorder());
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(2, 2, ancho - 4, alto - 4, radioCard, radioCard);
            }

            // Highlight superior reflectivo
            g2.setColor(new Color(255, 255, 255, oscuro ? 28 : 90));
            g2.drawLine(8, 3, ancho - 8, 3);

            // Circulo circleSoft para el icono Ikonli segun semantica
            int diametroCirculo = 28;
            int xCirculo = 10;
            int yCirculo = Math.max(4, (alto - diametroCirculo) / 2);

            Color colorFondoCirculo;
            Color colorIcono;
            if ("plane".equalsIgnoreCase(iconoNombre)) {
                colorFondoCirculo = Theme.getPasajeroSoft();
                colorIcono = Theme.getPasajeroAccent();
            } else if ("users".equalsIgnoreCase(iconoNombre)) {
                colorFondoCirculo = Theme.getInfoSoft();
                colorIcono = Theme.getInfo();
            } else if ("luggage".equalsIgnoreCase(iconoNombre)) {
                colorFondoCirculo = Theme.getWarningSoft();
                colorIcono = Theme.getWarning();
            } else {
                colorFondoCirculo = Theme.getSuccessSoft();
                colorIcono = Theme.getSuccess();
            }

            g2.setColor(colorFondoCirculo);
            g2.fillOval(xCirculo, yCirculo, diametroCirculo, diametroCirculo);

            // Icono Ikonli centrado
            javax.swing.Icon icon = AppIcon.of(iconoNombre, 15, colorIcono);
            if (icon != null) {
                int xIcono = xCirculo + (diametroCirculo - icon.getIconWidth()) / 2;
                int yIcono = yCirculo + (diametroCirculo - icon.getIconHeight()) / 2;
                icon.paintIcon(this, g2, xIcono, yIcono);
            }

            // Label Barlow mayusculo muted
            int xContenido = xCirculo + diametroCirculo + 8;
            g2.setColor(Theme.getMuted());
            g2.setFont(FontManager.getBarlowCondensed(java.awt.Font.BOLD, 9.5f));
            g2.drawString(titulo.toUpperCase(), xContenido, 17);

            // Valor grande Plex Sans texto
            g2.setColor(Theme.getTexto());
            g2.setFont(FontManager.getIBMPlexSans(java.awt.Font.BOLD, 13.5f));
            g2.drawString(valor, xContenido, 35);

            // Barra de progreso si aplica, o sub-label tecnico
            if (progresoVisible) {
                int anchoBarra = Math.max(20, ancho - xContenido - 14);
                int altoBarra = 4;
                int yBarra = 41;

                // Fondo de la barra
                g2.setColor(Theme.getInput());
                g2.fillRoundRect(xContenido, yBarra, anchoBarra, altoBarra, 3, 3);

                // Gradiente semantico de la barra: success -> warning -> danger
                int anchoLleno = Math.max(3, (anchoBarra * porcentajeProgreso) / 100);
                Color colorBarra;
                if (porcentajeProgreso < 60) {
                    colorBarra = Theme.getSuccess();
                } else if (porcentajeProgreso < 85) {
                    colorBarra = Theme.getWarning();
                } else {
                    colorBarra = Theme.getDanger();
                }

                g2.setColor(colorBarra);
                g2.fillRoundRect(xContenido, yBarra, anchoLleno, altoBarra, 3, 3);

                // Sub-label técnico al pie (solo en vista técnica)
                if (modoTecnico) {
                    g2.setColor(Theme.getSecundario());
                    g2.setFont(FontManager.getIBMPlexSans(java.awt.Font.PLAIN, 8.5f));
                    g2.drawString(sublabelTecnico, xContenido, 53);
                }
            } else {
                // Sub-label técnico secundario (solo en vista técnica)
                if (modoTecnico) {
                    g2.setColor(Theme.getSecundario());
                    g2.setFont(FontManager.getIBMPlexSans(java.awt.Font.PLAIN, 9.5f));
                    g2.drawString(sublabelTecnico, xContenido, 49);
                }
            }

            g2.dispose();
        }
    }
}
