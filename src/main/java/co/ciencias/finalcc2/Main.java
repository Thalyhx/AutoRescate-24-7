package co.ciencias.finalcc2;

import co.ciencias.finalcc2.controller.GuiController;
import co.ciencias.finalcc2.view.MainWindow;
import javax.swing.UIManager;

/**
 * Clase principal que lanza la aplicación con interfaz gráfica.
 */
public class Main {
    public static void main(String[] args) {
        // Configurar apariencia nativa del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si falla, usa el look and feel por defecto
        }

        // Ejecutar en el hilo de despacho de eventos de Swing
        javax.swing.SwingUtilities.invokeLater(() -> {
            MainWindow vista = new MainWindow();
            GuiController controller = new GuiController(vista);
            controller.iniciar();
        });
    }
}
