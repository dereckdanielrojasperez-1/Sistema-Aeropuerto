package com.aeropuerto.test;

import com.aeropuerto.vista.VentanaPrincipal;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class GeneradorCapturasInforme {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = null;
            try {
                System.out.println("Iniciando VentanaPrincipal para capturas...");
                VentanaPrincipal v = new VentanaPrincipal();
                v.configurarModoVista(true); // Modo Administrador
                
                frame = new JFrame("Capturador");
                frame.setUndecorated(true);
                frame.setContentPane(v.getContentPane());
                frame.setSize(1366, 768);
                frame.addNotify();
                frame.validate();
                frame.setVisible(true);

                // Esperar un breve instante para que la interfaz Swing termine de posicionar componentes
                Thread.sleep(1200);

                File dirSalida = new File("c:/Users/Computecno/.gemini/antigravity/brain/7636a38a-de07-4825-b393-d7912d80d741/capturas_informe");
                if (!dirSalida.exists()) dirSalida.mkdirs();

                JTabbedPane tabbed = findTabbedPane(frame.getContentPane());
                if (tabbed == null) {
                    System.err.println("No se encontró JTabbedPane!");
                    if (frame != null) frame.dispose();
                    System.exit(1);
                    return;
                }

                System.out.println("Total pestañas encontradas: " + tabbed.getTabCount());
                String[] nombres = {
                    "01_radar_atc",
                    "02_supervision_aeropuertos",
                    "03_arreglos_matrices",
                    "04_listas_dinamicas",
                    "05_puertas_embarque",
                    "06_arboles_abb_avl",
                    "07_grafo_rutas_dijkstra",
                    "08_hash_heap_pasajeros",
                    "09_benchmarking_ordenamiento",
                    "10_control_admin_sql"
                };

                for (int i = 0; i < tabbed.getTabCount(); i++) {
                    tabbed.setSelectedIndex(i);
                    frame.validate();
                    frame.repaint();
                    Thread.sleep(300);

                    BufferedImage img = new BufferedImage(1366, 768, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2 = img.createGraphics();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    frame.paintAll(g2);
                    g2.dispose();

                    String nombreArchivo = (i < nombres.length ? nombres[i] : "tab_" + i) + ".png";
                    File f = new File(dirSalida, nombreArchivo);
                    ImageIO.write(img, "png", f);
                    System.out.println("Guardada captura: " + f.getName() + " (" + f.length() + " bytes)");
                }

                System.out.println("¡Todas las capturas se generaron exitosamente!");
                frame.dispose();
                System.exit(0);
            } catch (Exception ex) {
                ex.printStackTrace();
                if (frame != null) frame.dispose();
                System.exit(1);
            }
        });
    }

    private static JTabbedPane findTabbedPane(Container c) {
        for (Component comp : c.getComponents()) {
            if (comp instanceof JTabbedPane) {
                return (JTabbedPane) comp;
            }
            if (comp instanceof Container) {
                JTabbedPane sub = findTabbedPane((Container) comp);
                if (sub != null) return sub;
            }
        }
        return null;
    }
}
