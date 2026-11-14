// HUMANIZACION UI
package com.aeropuerto.vista.tema;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

// Gestor centralizado de fuentes institucionales para Smart Airport CORPAC
public final class FontManager {

    private static final Logger LOGGER = Logger.getLogger(FontManager.class.getName());

    private static Font barlowCondensedSemiBold;
    private static Font barlowCondensedBold;
    private static Font ibmPlexSansRegular;
    private static Font ibmPlexSansSemiBold;
    private static Font ibmPlexMonoRegular;
    private static Font ibmPlexMonoMedium;

    private static boolean inicializado = false;

    private FontManager() {}

    public static synchronized void inicializar() {
        if (inicializado) return;
        try {
            barlowCondensedSemiBold = cargarYRegistrar("/com/aeropuerto/vista/recursos/fuentes/BarlowCondensed-SemiBold.ttf", "Barlow Condensed", Font.PLAIN);
            barlowCondensedBold = cargarYRegistrar("/com/aeropuerto/vista/recursos/fuentes/BarlowCondensed-Bold.ttf", "Barlow Condensed", Font.BOLD);

            ibmPlexSansRegular = cargarYRegistrar("/com/aeropuerto/vista/recursos/fuentes/IBMPlexSans-Regular.ttf", "IBM Plex Sans", Font.PLAIN);
            ibmPlexSansSemiBold = cargarYRegistrar("/com/aeropuerto/vista/recursos/fuentes/IBMPlexSans-SemiBold.ttf", "IBM Plex Sans", Font.BOLD);

            ibmPlexMonoRegular = cargarYRegistrar("/com/aeropuerto/vista/recursos/fuentes/IBMPlexMono-Regular.ttf", "IBM Plex Mono", Font.PLAIN);
            ibmPlexMonoMedium = cargarYRegistrar("/com/aeropuerto/vista/recursos/fuentes/IBMPlexMono-Medium.ttf", "IBM Plex Mono", Font.BOLD);

            inicializado = true;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Advertencia cargando fuentes institucionales, usando fuentes del sistema como respaldo", e);
        }
    }

    private static Font cargarYRegistrar(String ruta, String familiaFallback, int estiloFallback) {
        try (InputStream is = FontManager.class.getResourceAsStream(ruta)) {
            if (is != null) {
                Font f = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
                return f;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.FINE, "No se pudo cargar fuente desde: " + ruta, ex);
        }
        return new Font(familiaFallback, estiloFallback, 12);
    }

    private static final java.util.Map<String, Font> FONT_CACHE = new java.util.concurrent.ConcurrentHashMap<>();

    public static Font getBarlowCondensed(int estilo, float tamano) {
        String claveCache = "BC_" + estilo + "_" + (int)(tamano * 10);
        Font fuenteEnCache = FONT_CACHE.get(claveCache);
        if (fuenteEnCache != null) return fuenteEnCache;
        if (!inicializado) inicializar();
        Font base = (estilo == Font.BOLD && barlowCondensedBold != null) ? barlowCondensedBold : barlowCondensedSemiBold;
        if (base == null) base = new Font("Segoe UI", estilo, (int) tamano);
        Font resultadoFuente = base.deriveFont(estilo, tamano);
        FONT_CACHE.put(claveCache, resultadoFuente);
        return resultadoFuente;
    }

    public static Font getIBMPlexSans(int estilo, float tamano) {
        String claveCache = "IPS_" + estilo + "_" + (int)(tamano * 10);
        Font fuenteEnCache = FONT_CACHE.get(claveCache);
        if (fuenteEnCache != null) return fuenteEnCache;
        if (!inicializado) inicializar();
        Font base = (estilo == Font.BOLD && ibmPlexSansSemiBold != null) ? ibmPlexSansSemiBold : ibmPlexSansRegular;
        if (base == null) base = new Font("Segoe UI", estilo, (int) tamano);
        Font resultadoFuente = base.deriveFont(estilo, tamano);
        FONT_CACHE.put(claveCache, resultadoFuente);
        return resultadoFuente;
    }

    public static Font getIBMPlexMono(int estilo, float tamano) {
        String claveCache = "IPM_" + estilo + "_" + (int)(tamano * 10);
        Font fuenteEnCache = FONT_CACHE.get(claveCache);
        if (fuenteEnCache != null) return fuenteEnCache;
        if (!inicializado) inicializar();
        Font base = (estilo == Font.BOLD && ibmPlexMonoMedium != null) ? ibmPlexMonoMedium : ibmPlexMonoRegular;
        if (base == null) base = new Font("Consolas", estilo, (int) tamano);
        Font resultadoFuente = base.deriveFont(estilo, tamano);
        FONT_CACHE.put(claveCache, resultadoFuente);
        return resultadoFuente;
    }
}
