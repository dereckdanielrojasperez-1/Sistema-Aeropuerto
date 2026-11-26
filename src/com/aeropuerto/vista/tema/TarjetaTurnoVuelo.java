// Tarjeta Comercial Ejecutiva de Vuelo
package com.aeropuerto.vista.tema;

import com.aeropuerto.modelo.Vuelo;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;

// Tarjeta comercial moderna de vuelo (Estilo Google Flights / LATAM)
public class TarjetaTurnoVuelo extends JPanel {

    private final Vuelo vuelo;
    private final boolean esSeleccionado;
    private final String turnoNombre;
    private final String iconoNombre;
    private final Color turnoColor;
    private boolean isHovered = false;

    public TarjetaTurnoVuelo(Vuelo vuelo, int turnoIndice, boolean esSeleccionado, ActionListener onSeleccionar) {
        this(vuelo, turnoIndice, esSeleccionado, onSeleccionar, null);
    }

    public TarjetaTurnoVuelo(Vuelo vuelo, int turnoIndice, boolean esSeleccionado, ActionListener onSeleccionar, ActionListener onContinuar) {
        this.vuelo = vuelo;
        this.esSeleccionado = esSeleccionado;

        // Clasificación por horario del vuelo o índice
        int hora = 8;
        try {
            hora = Integer.parseInt(vuelo.obtenerHoraSalida().split(":")[0].trim());
        } catch (Exception ignored) {}

        if (hora >= 6 && hora < 12) {
            this.turnoNombre = "TURNO MAÑANA (06:00 - 12:00)";
            this.iconoNombre = "sun";
            this.turnoColor = new Color(0xFB, 0xBF, 0x24); // Ámbar sol
        } else if (hora >= 12 && hora < 18) {
            this.turnoNombre = "TURNO TARDE (12:00 - 18:00)";
            this.iconoNombre = "sunset";
            this.turnoColor = new Color(0xF9, 0x73, 0x16); // Naranja atardecer
        } else {
            this.turnoNombre = "TURNO NOCHE (18:00 - 23:59)";
            this.iconoNombre = "moon";
            this.turnoColor = new Color(0x81, 0x8C, 0xF8); // Índigo noche
        }

        setLayout(new BorderLayout(0, 6));
        setOpaque(false);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 118));
        setPreferredSize(new Dimension(720, 114));
        setMinimumSize(new Dimension(380, 108));
        setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Evento de clic en toda la tarjeta para máxima naturalidad
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onSeleccionar != null) {
                    onSeleccionar.actionPerformed(null);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });

        // 1. CABECERA: Badge Turno + Aerolínea a la izq | Badges de Asientos y Clase a la der
        JPanel panelSuperior = new JPanel(new BorderLayout(8, 0));
        panelSuperior.setOpaque(false);

        JPanel panelIzqSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelIzqSuperior.setOpaque(false);

        JLabel lblIconoTurno = new JLabel(AppIcon.of(iconoNombre, 13, turnoColor));
        JLabel lblTurnoTexto = new JLabel(turnoNombre);
        lblTurnoTexto.setFont(FontManager.getBarlowCondensed(Font.BOLD, 10.5f));
        lblTurnoTexto.setForeground(turnoColor);

        JLabel lblSep = new JLabel("-");
        lblSep.setForeground(Theme.getMuted());

        JLabel lblAerolinea = new JLabel("CORPAC Airways  |  " + vuelo.obtenerCodigo() + "  |  Airbus A320neo");
        lblAerolinea.setFont(FontManager.getIBMPlexMono(Font.PLAIN, 10f));
        lblAerolinea.setForeground(Theme.getSecundario());

        panelIzqSuperior.add(lblIconoTurno);
        panelIzqSuperior.add(lblTurnoTexto);
        panelIzqSuperior.add(lblSep);
        panelIzqSuperior.add(lblAerolinea);

        JPanel panelDerSuperior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        panelDerSuperior.setOpaque(false);

        int libres = vuelo.contarAsientosLibres();
        JLabel lblAsientosBadge = new JLabel(" " + libres + " Asientos Libres ");
        lblAsientosBadge.setFont(FontManager.getBarlowCondensed(Font.BOLD, 10.5f));
        lblAsientosBadge.setOpaque(true);
        if (libres > 15) {
            lblAsientosBadge.setBackground(Theme.getSuccessSoft());
            lblAsientosBadge.setForeground(Theme.getSuccess());
            lblAsientosBadge.setBorder(BorderFactory.createLineBorder(Theme.getSuccess(), 1, true));
        } else {
            lblAsientosBadge.setBackground(Theme.getWarningSoft());
            lblAsientosBadge.setForeground(Theme.getWarning());
            lblAsientosBadge.setBorder(BorderFactory.createLineBorder(Theme.getWarning(), 1, true));
        }

        JLabel lblClaseBadge = new JLabel(" Clase " + vuelo.obtenerClaseDisponible() + " ");
        lblClaseBadge.setFont(FontManager.getBarlowCondensed(Font.BOLD, 10.5f));
        lblClaseBadge.setOpaque(true);
        lblClaseBadge.setBackground(Theme.getElevated());
        lblClaseBadge.setForeground(Theme.getPasajeroAccent());
        lblClaseBadge.setBorder(BorderFactory.createLineBorder(Theme.getBorder(), 1, true));

        panelDerSuperior.add(lblAsientosBadge);
        panelDerSuperior.add(lblClaseBadge);

        panelSuperior.add(panelIzqSuperior, BorderLayout.WEST);
        panelSuperior.add(panelDerSuperior, BorderLayout.EAST);
        add(panelSuperior, BorderLayout.NORTH);

        // 2. CENTRO: Diagrama de Vuelo (Horas y Ruta) + Bloque de Precio
        JPanel panelCentro = new JPanel(new BorderLayout(16, 0));
        panelCentro.setOpaque(false);

        // Diagrama Horario / Origen -> Destino
        JPanel pDiagramaRuta = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 2));
        pDiagramaRuta.setOpaque(false);

        // Origen
        JPanel pSalida = new JPanel(new GridLayout(2, 1, 0, 0));
        pSalida.setOpaque(false);
        JLabel lblHoraSalida = new JLabel(vuelo.obtenerHoraSalida());
        lblHoraSalida.setFont(FontManager.getBarlowCondensed(Font.BOLD, 17f));
        lblHoraSalida.setForeground(Theme.getTexto());
        JLabel lblOrigen = new JLabel(vuelo.obtenerOrigen() + " [LIM]");
        lblOrigen.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 10.5f));
        lblOrigen.setForeground(Theme.getSecundario());
        pSalida.add(lblHoraSalida);
        pSalida.add(lblOrigen);

        // Travesía gráfica con icono real de avión
        JPanel pTravesia = new JPanel(new GridLayout(2, 1, 0, 0));
        pTravesia.setOpaque(false);

        JPanel pLineaAvion = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        pLineaAvion.setOpaque(false);
        JLabel lblL1 = new JLabel("──────");
        lblL1.setForeground(Theme.getSecundario());
        lblL1.setFont(FontManager.getIBMPlexMono(Font.PLAIN, 10f));
        JLabel lblAvionIcon = new JLabel(AppIcon.plane(13, Theme.getPasajeroAccent()));
        JLabel lblL2 = new JLabel("──────>");
        lblL2.setForeground(Theme.getSecundario());
        lblL2.setFont(FontManager.getIBMPlexMono(Font.PLAIN, 10f));
        pLineaAvion.add(lblL1);
        pLineaAvion.add(lblAvionIcon);
        pLineaAvion.add(lblL2);

        JLabel lblDuracion = new JLabel(vuelo.obtenerDuracion() + " min - Vuelo Directo", SwingConstants.CENTER);
        lblDuracion.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 10f));
        lblDuracion.setForeground(Theme.getSecundario());

        pTravesia.add(pLineaAvion);
        pTravesia.add(lblDuracion);

        // Llegada
        JPanel pLlegada = new JPanel(new GridLayout(2, 1, 0, 0));
        pLlegada.setOpaque(false);
        JLabel lblHoraLlegada = new JLabel(vuelo.obtenerHoraLlegada());
        lblHoraLlegada.setFont(FontManager.getBarlowCondensed(Font.BOLD, 17f));
        lblHoraLlegada.setForeground(Theme.getTexto());
        String iataDest = obtenerIata(vuelo.obtenerDestino());
        JLabel lblDestino = new JLabel(vuelo.obtenerDestino() + " [" + iataDest + "]");
        lblDestino.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 10.5f));
        lblDestino.setForeground(Theme.getSecundario());
        pLlegada.add(lblHoraLlegada);
        pLlegada.add(lblDestino);

        pDiagramaRuta.add(pSalida);
        pDiagramaRuta.add(pTravesia);
        pDiagramaRuta.add(pLlegada);
        panelCentro.add(pDiagramaRuta, BorderLayout.WEST);

        // Bloque de Precio a la derecha
        JPanel pPrecioBloque = new JPanel(new GridLayout(2, 1, 0, 0));
        pPrecioBloque.setOpaque(false);
        String textoPrecio = String.format(Locale.US, "%.2f", vuelo.obtenerPrecioBase());
        JLabel lblPrecio = new JLabel("S/. " + textoPrecio, SwingConstants.RIGHT);
        lblPrecio.setFont(FontManager.getBarlowCondensed(Font.BOLD, 19f));
        lblPrecio.setForeground(esSeleccionado ? Theme.getPasajeroAccent() : Theme.getWarning());

        JLabel lblTasas = new JLabel("por pasajero (tasas e IGV incl.)", SwingConstants.RIGHT);
        lblTasas.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 9.5f));
        lblTasas.setForeground(Theme.getMuted());

        pPrecioBloque.add(lblPrecio);
        pPrecioBloque.add(lblTasas);
        panelCentro.add(pPrecioBloque, BorderLayout.EAST);

        add(panelCentro, BorderLayout.CENTER);

        // 3. PIE: Beneficios incluidos (izq) | Botón de acción (der)
        JPanel panelInferior = new JPanel(new BorderLayout(8, 0));
        panelInferior.setOpaque(false);

        JPanel pBeneficios = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pBeneficios.setOpaque(false);
        JLabel b1 = new JLabel("✓ 1 Equipaje de mano (10kg)");
        b1.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 9.5f));
        b1.setForeground(Theme.getSecundario());
        JLabel b2 = new JLabel("✓ Asiento en cabina 3D");
        b2.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 9.5f));
        b2.setForeground(Theme.getSecundario());
        JLabel b3 = new JLabel("✓ Voucher digital SUNAT");
        b3.setFont(FontManager.getIBMPlexSans(Font.PLAIN, 9.5f));
        b3.setForeground(Theme.getSecundario());

        pBeneficios.add(b1);
        pBeneficios.add(b2);
        pBeneficios.add(b3);
        panelInferior.add(pBeneficios, BorderLayout.WEST);

        // Botones de acción a la derecha
        JPanel pAccionesDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pAccionesDerecha.setOpaque(false);

        JButton btnAccion = new JButton(esSeleccionado ? "✓ Vuelo Seleccionado" : "Elegir este Vuelo");
        btnAccion.setFont(FontManager.getIBMPlexSans(Font.BOLD, 10.5f));
        btnAccion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAccion.setFocusPainted(false);
        btnAccion.setPreferredSize(new Dimension(140, 24));

        if (esSeleccionado) {
            btnAccion.setBackground(Theme.getPasajeroSoft());
            btnAccion.setForeground(Theme.getPasajeroAccent());
            btnAccion.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getPasajeroAccent(), 1, true),
                    BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        } else {
            btnAccion.setBackground(Theme.getElevated());
            btnAccion.setForeground(Theme.getTexto());
            btnAccion.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.getBorder(), 1, true),
                    BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        }

        if (onSeleccionar != null) {
            btnAccion.addActionListener(onSeleccionar);
        }
        pAccionesDerecha.add(btnAccion);

        if (esSeleccionado && onContinuar != null) {
            JButton btnContinuarRapido = new JButton("Continuar a Asiento ->");
            btnContinuarRapido.setFont(FontManager.getIBMPlexSans(Font.BOLD, 10.5f));
            btnContinuarRapido.setBackground(Theme.getPasajeroAccent());
            btnContinuarRapido.setForeground(Color.WHITE);
            btnContinuarRapido.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnContinuarRapido.setFocusPainted(false);
            btnContinuarRapido.setPreferredSize(new Dimension(150, 24));
            btnContinuarRapido.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
            btnContinuarRapido.addActionListener(onContinuar);
            pAccionesDerecha.add(btnContinuarRapido);
        }

        panelInferior.add(pAccionesDerecha, BorderLayout.EAST);
        add(panelInferior, BorderLayout.SOUTH);
    }

    private static String obtenerIata(String ciudad) {
        if (ciudad == null) return "PER";
        String c = ciudad.toUpperCase().trim();
        if (c.contains("LIMA")) return "LIM";
        if (c.contains("TRUJILLO")) return "TRU";
        if (c.contains("AREQUIPA")) return "AQP";
        if (c.contains("CUSCO") || c.contains("CUZCO")) return "CUZ";
        if (c.contains("PIURA")) return "PIU";
        if (c.contains("IQUITOS")) return "IQT";
        if (c.contains("TARAPOTO")) return "TPP";
        if (c.contains("CHICLAYO")) return "CIX";
        return c.length() >= 3 ? c.substring(0, 3) : c;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int ancho = getWidth();
        int alto = getHeight();

        if (esSeleccionado) {
            // Fondo con acento activo
            g2.setColor(Theme.getSurface());
            g2.fillRoundRect(2, 2, ancho - 4, alto - 4, 12, 12);

            g2.setColor(Theme.getPasajeroSoft());
            g2.fillRoundRect(2, 2, ancho - 4, alto - 4, 12, 12);

            // Borde acentuado de 2px
            g2.setColor(Theme.getPasajeroAccent());
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawRoundRect(2, 2, ancho - 4, alto - 4, 12, 12);
        } else {
            // Fondo surface con realce si el cursor está encima
            g2.setColor(isHovered ? Theme.getElevated() : Theme.getSurface());
            g2.fillRoundRect(2, 2, ancho - 4, alto - 4, 12, 12);

            g2.setColor(isHovered ? Theme.getPasajeroAccent() : Theme.getBorder());
            g2.setStroke(new BasicStroke(isHovered ? 1.5f : 1.0f));
            g2.drawRoundRect(2, 2, ancho - 4, alto - 4, 12, 12);
        }

        g2.dispose();
    }
}
