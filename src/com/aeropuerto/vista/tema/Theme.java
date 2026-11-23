// HUMANIZACION UI
package com.aeropuerto.vista.tema;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

// Sistema centralizado de estilos, paletas y Look and Feel de Smart Airport CORPAC
public final class Theme {

    public interface ThemeListener {
        void onThemeChanged();
    }

    private static final Preferences PREFS = Preferences.userNodeForPackage(Theme.class);
    private static final String PREF_KEY_DARK = "corpac_ui_dark_mode";
    private static final String PREF_KEY_ROL = "corpac_ui_active_role";

    private static boolean modoOscuro = true;
    private static String rolActivo = PREFS.get(PREF_KEY_ROL, "PASAJERO");

    private static final List<ThemeListener> LISTENERS = new ArrayList<>();

    // ==========================================
    // PALETA OSCURA (Base GitHub Dark Pro / Aviation)
    // ==========================================
    private static final Color DARK_BG         = new Color(0x0D, 0x11, 0x17); // #0D1117
    private static final Color DARK_SURFACE    = new Color(0x16, 0x1B, 0x22); // #161B22
    private static final Color DARK_ELEVATED   = new Color(0x1C, 0x21, 0x28); // #1C2128
    private static final Color DARK_INPUT      = new Color(0x0D, 0x11, 0x17); // #0D1117
    private static final Color DARK_BORDER     = new Color(0x30, 0x36, 0x3D); // #30363D
    private static final Color DARK_TEXTO      = new Color(0xE6, 0xED, 0xF3); // #E6EDF3 (15.35:1)
    private static final Color DARK_SECUNDARIO = new Color(0x91, 0x98, 0xA1); // #9198A1 (5.92:1)
    private static final Color DARK_MUTED      = new Color(0x8B, 0x94, 0x9E); // Auditado WCAG 4.5:1 (5.55:1) vs #768390 (4.32:1)
    private static final Color DARK_INFO       = new Color(0x38, 0x8B, 0xFD); // #388BFD (5.58:1)
    private static final Color DARK_SUCCESS    = new Color(0x3F, 0xB9, 0x50); // #3FB950
    private static final Color DARK_WARNING    = new Color(0xD2, 0x99, 0x22); // #D29922
    private static final Color DARK_DANGER     = new Color(0xF8, 0x51, 0x49); // #F85149

    // ==========================================
    // PALETA CLARA (Base GitHub Light Clean / Aviation)
    // ==========================================
    private static final Color LIGHT_BG         = new Color(0xF4, 0xF6, 0xF8); // #F4F6F8
    private static final Color LIGHT_SURFACE    = new Color(0xFF, 0xFF, 0xFF); // #FFFFFF
    private static final Color LIGHT_ELEVATED   = new Color(0xEE, 0xF1, 0xF4); // #EEF1F4
    private static final Color LIGHT_INPUT      = new Color(0xFF, 0xFF, 0xFF); // #FFFFFF
    private static final Color LIGHT_BORDER     = new Color(0xD0, 0xD7, 0xDE); // #D0D7DE
    private static final Color LIGHT_TEXTO      = new Color(0x1F, 0x23, 0x28); // #1F2328 (15.0:1)
    private static final Color LIGHT_SECUNDARIO = new Color(0x57, 0x60, 0x6A); // #57606A (6.28:1)
    private static final Color LIGHT_MUTED      = new Color(0x59, 0x63, 0x6E); // Auditado WCAG 4.5:1 (5.47:1) vs #6E7781 (4.11:1)
    private static final Color LIGHT_INFO       = new Color(0x09, 0x69, 0xDA); // #0969DA (5.58:1)
    private static final Color LIGHT_SUCCESS    = new Color(0x1A, 0x7F, 0x37); // #1A7F37
    private static final Color LIGHT_WARNING    = new Color(0x9A, 0x67, 0x00); // #9A6700
    private static final Color LIGHT_DANGER     = new Color(0xCF, 0x22, 0x2E); // #CF222E

    // ==========================================
    // IDENTIDADES POR ROL (data-mode)
    // ==========================================
    // Pasajero:
    private static final Color PASAJERO_ACCENT_DARK  = new Color(0x58, 0xA6, 0xFF); // #58A6FF (6.82:1)
    private static final Color PASAJERO_ACCENT_LIGHT = new Color(0x09, 0x69, 0xDA); // #0969DA (5.58:1 en fondo claro)
    private static final Color PASAJERO_SOFT_DARK    = new Color(0x15, 0x29, 0x4A); // #15294A
    private static final Color PASAJERO_SOFT_LIGHT   = new Color(0xDD, 0xF4, 0xFF); // #DDF4FF

    // Administrador:
    private static final Color ADMIN_ACCENT_DARK  = new Color(0xE3, 0xB3, 0x41); // #E3B341 (7.95:1)
    private static final Color ADMIN_ACCENT_LIGHT = new Color(0x9A, 0x67, 0x00); // #9A6700 (5.12:1 en fondo claro)
    private static final Color ADMIN_SOFT_DARK    = new Color(0x33, 0x27, 0x0F); // #33270F
    private static final Color ADMIN_SOFT_LIGHT   = new Color(0xFF, 0xF8, 0xC5); // #FFF8C5
    private static final Color ADMIN_RADAR_VERDE  = new Color(0x2E, 0xA0, 0x43); // #2EA043 (Radar ATC)

    private Theme() {}

    // Inicializa FlatLaf en modo oscuro permanente segun directriz oficial de CORPAC
    public static void inicializar() {
        FontManager.inicializar();
        modoOscuro = true;
        PREFS.putBoolean(PREF_KEY_DARK, true);
        configurarFlatLaf(true);
    }

    private static void configurarFlatLaf(boolean oscuro) {
        try {
            // Parametros globales FlatLaf para estetica limpia enterprise
            UIManager.put("Component.arc", 6);
            UIManager.put("Button.arc", 6);
            UIManager.put("TextComponent.arc", 6);
            UIManager.put("ScrollBar.showButtons", false);
            UIManager.put("TabbedPane.showTabSeparators", true);

            FlatDarkLaf.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Modo oscuro permanente
    public static void toggleTheme() {
        setModoOscuro(true);
    }

    public static void setModoOscuro(boolean oscuro) {
        modoOscuro = true; // Forzado estrictamente a modo oscuro
        PREFS.putBoolean(PREF_KEY_DARK, true);
        configurarFlatLaf(true);
        FlatLaf.updateUI();

        // Repintado recursivo en todas las ventanas activas
        for (Window w : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(w);
            w.validate();
            w.repaint();
        }

        notificarListeners();
    }

    public static boolean esModoOscuro() {
        return true;
    }

    public static void setRolActivo(String rol) {
        if (rol == null) rol = "PASAJERO";
        rolActivo = rol.toUpperCase().trim();
        PREFS.put(PREF_KEY_ROL, rolActivo);
        notificarListeners();
        for (Window w : Window.getWindows()) {
            w.repaint();
        }
    }

    public static String getRolActivo() {
        return rolActivo;
    }

    public static boolean esRolAdmin() {
        return "ADMIN".equalsIgnoreCase(rolActivo);
    }

    public static void addThemeListener(ThemeListener l) {
        if (l != null && !LISTENERS.contains(l)) {
            LISTENERS.add(l);
        }
    }

    public static void removeThemeListener(ThemeListener l) {
        LISTENERS.remove(l);
    }

    private static void notificarListeners() {
        for (ThemeListener l : new ArrayList<>(LISTENERS)) {
            try {
                l.onThemeChanged();
            } catch (Exception ignored) {}
        }
    }

    // ==========================================
    // GETTERS DINAMICOS DE LA PALETA SEGUN TEMA ACTIVO
    // ==========================================
    public static Color getBg() {
        return modoOscuro ? DARK_BG : LIGHT_BG;
    }

    public static Color getSurface() {
        return modoOscuro ? DARK_SURFACE : LIGHT_SURFACE;
    }

    public static Color getElevated() {
        return modoOscuro ? DARK_ELEVATED : LIGHT_ELEVATED;
    }

    public static Color getInput() {
        return modoOscuro ? DARK_INPUT : LIGHT_INPUT;
    }

    public static Color getBorder() {
        return modoOscuro ? DARK_BORDER : LIGHT_BORDER;
    }

    public static Color getTexto() {
        return modoOscuro ? DARK_TEXTO : LIGHT_TEXTO;
    }

    public static Color getSecundario() {
        return modoOscuro ? DARK_SECUNDARIO : LIGHT_SECUNDARIO;
    }

    public static Color getMuted() {
        return modoOscuro ? DARK_MUTED : LIGHT_MUTED;
    }

    public static Color getInfo() {
        return modoOscuro ? DARK_INFO : LIGHT_INFO;
    }

    public static Color getSuccess() {
        return modoOscuro ? DARK_SUCCESS : LIGHT_SUCCESS;
    }

    public static Color getWarning() {
        return modoOscuro ? DARK_WARNING : LIGHT_WARNING;
    }

    public static Color getDanger() {
        return modoOscuro ? DARK_DANGER : LIGHT_DANGER;
    }

    // ==========================================
    // FONDOS SOFT PARA BADGES SEMANTICOS
    // ==========================================
    public static Color getInfoSoft() {
        return modoOscuro ? new Color(0x0D, 0x2D, 0x5E) : new Color(0xDD, 0xF4, 0xFF);
    }

    public static Color getSuccessSoft() {
        return modoOscuro ? new Color(0x0F, 0x33, 0x18) : new Color(0xE6, 0xF8, 0xEB);
    }

    public static Color getWarningSoft() {
        return modoOscuro ? new Color(0x3A, 0x2C, 0x08) : new Color(0xFF, 0xF8, 0xC5);
    }

    public static Color getDangerSoft() {
        return modoOscuro ? new Color(0x3D, 0x14, 0x18) : new Color(0xFF, 0xEB, 0xE9);
    }

    // ==========================================
    // ACENTOS DINAMICOS POR ROL E IDENTIDAD
    // ==========================================
    public static Color getAccentRolActivo() {
        return esRolAdmin() ? getAdminAccent() : getPasajeroAccent();
    }

    public static Color getAcento() {
        return getAccentRolActivo();
    }

    public static Color getSoftRolActivo() {
        return esRolAdmin() ? getAdminSoft() : getPasajeroSoft();
    }

    public static Color getPasajeroAccent() {
        return modoOscuro ? PASAJERO_ACCENT_DARK : PASAJERO_ACCENT_LIGHT;
    }

    public static Color getPasajeroSoft() {
        return modoOscuro ? PASAJERO_SOFT_DARK : PASAJERO_SOFT_LIGHT;
    }

    public static Color getAdminAccent() {
        return modoOscuro ? ADMIN_ACCENT_DARK : ADMIN_ACCENT_LIGHT;
    }

    public static Color getAdminSoft() {
        return modoOscuro ? ADMIN_SOFT_DARK : ADMIN_SOFT_LIGHT;
    }

    public static Color getRadarVerde() {
        return ADMIN_RADAR_VERDE;
    }

    // Consulta centralizada de color por clave de token
    public static Color getColor(String claveColor) {
        if (claveColor == null) return getTexto();
        switch (claveColor.toLowerCase().trim()) {
            case "bg": return getBg();
            case "surface": return getSurface();
            case "elevated": return getElevated();
            case "input": return getInput();
            case "border": return getBorder();
            case "texto": return getTexto();
            case "secundario": return getSecundario();
            case "muted": return getMuted();
            case "info": return getInfo();
            case "success": return getSuccess();
            case "warning": return getWarning();
            case "danger": return getDanger();
            case "pasajero.accent": return getPasajeroAccent();
            case "pasajero.soft": return getPasajeroSoft();
            case "admin.accent": return getAdminAccent();
            case "admin.soft": return getAdminSoft();
            case "radar": return getRadarVerde();
            case "info.soft": return getInfoSoft();
            case "success.soft": return getSuccessSoft();
            case "warning.soft": return getWarningSoft();
            case "danger.soft": return getDangerSoft();
            default: return getTexto();
        }
    }
}
