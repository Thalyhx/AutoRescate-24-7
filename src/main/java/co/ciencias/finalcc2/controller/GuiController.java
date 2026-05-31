package co.ciencias.finalcc2.controller;

import co.ciencias.finalcc2.model.*;
import co.ciencias.finalcc2.model.enums.*;
import co.ciencias.finalcc2.model.gestores.*;
import co.ciencias.finalcc2.view.MainWindow;
import javax.swing.SwingUtilities;

public class GuiController implements SimulacionListener {

    private final MainWindow vista;
    private final MotorSimulacion motor;

    public GuiController(MainWindow vista) {
        this.vista = vista;
        this.motor = new MotorSimulacion();
        this.motor.setListener(this);
        this.vista.setController(this);
    }

    public void iniciar() {
        // Arrancar el motor en un hilo secundario
        Thread hiloMotor = new Thread(motor, "Hilo-Reloj-GUI");
        hiloMotor.setDaemon(true);
        hiloMotor.start();
        
        vista.setVisible(true);
        vista.log("[SISTEMA] Motor de simulación iniciado.");
    }

    public void registrarNuevaSolicitud(String nombre, String tel, TipoEmergencia tipo, double x, double y) {
        PuntoVia punto = PuntoVia.desde(x, y);
        if (punto != null) {
            Cliente c = new Cliente(nombre, tel);
            SolicitudServicio s = GestorSolicitudes.getInstancia().crearSolicitud(c, tipo, "Emergencia Vial", punto);
            vista.log("[USUARIO] Registrada llamada de " + nombre + " en " + ZonaCalculadora.NOMBRES[s.getZonaPuesto()]);
        } else {
            vista.log("[ERROR] Las coordenadas (" + x + "," + y + ") están fuera de la vía.");
        }
    }

    @Override
    public void onTick(int[] countdowns, ListaEnlazada<MonitoreoAtencion> enRuta, ListaEnlazada<MonitoreoMantenimiento> enTaller) {
        // Actualizar la UI en el Event Dispatch Thread de Swing
        SwingUtilities.invokeLater(() -> {
            actualizarTablaCanales(countdowns);
            actualizarTablaEnRuta(enRuta);
            actualizarTablaTaller(enTaller);
        });
    }

    @Override
    public void onMensajeEmitido(String mensaje) {
        SwingUtilities.invokeLater(() -> vista.log(mensaje));
    }

    private void actualizarTablaCanales(int[] countdowns) {
        PuestoAtencion[] puestos = GestorRecursos.getInstancia().getPuestos();
        Object[][] data = new Object[4][3];
        for (int i = 0; i < 4; i++) {
            data[i][0] = puestos[i].getNombre();
            data[i][1] = countdowns[i] + "s";
            data[i][2] = puestos[i].getSolicitudesPendientes().getTamanio();
        }
        vista.actualizarCanales(data);
    }

    private void actualizarTablaEnRuta(ListaEnlazada<MonitoreoAtencion> enRuta) {
        int size = enRuta.getTamanio();
        Object[][] data = new Object[size][5];
        Nodo<MonitoreoAtencion> actual = enRuta.getCabeza();
        int i = 0;
        while (actual != null) {
            MonitoreoAtencion mat = actual.getDato();
            SolicitudServicio sol = mat.getSolicitud();
            data[i][0] = sol.getCliente().getNombre();
            data[i][1] = "(" + sol.getUbicacion().getX() + ", " + sol.getUbicacion().getY() + ")";
            data[i][2] = (sol.getUnidadAsignada() != null) ? sol.getUnidadAsignada().getCodigo() : "N/A";
            data[i][3] = (sol.getTecnicoAsignado() != null) ? sol.getTecnicoAsignado().getNombre() : "N/A";
            data[i][4] = mat.getSegundosRestantes() + "s";
            actual = actual.getSiguiente();
            i++;
        }
        vista.actualizarEnRuta(data);
    }

    private void actualizarTablaTaller(ListaEnlazada<MonitoreoMantenimiento> enTaller) {
        int size = enTaller.getTamanio();
        Object[][] data = new Object[size][4];
        Nodo<MonitoreoMantenimiento> actual = enTaller.getCabeza();
        int i = 0;
        while (actual != null) {
            MonitoreoMantenimiento mmnt = actual.getDato();
            data[i][0] = (mmnt.getUnidad() != null) ? mmnt.getUnidad().getCodigo() : "N/A";
            data[i][1] = (mmnt.getTecnico() != null) ? mmnt.getTecnico().getNombre() : "N/A";
            data[i][2] = "REPARANDO/ABASTECIENDO";
            data[i][3] = mmnt.getSegundosRestantes() + "s";
            actual = actual.getSiguiente();
            i++;
        }
        vista.actualizarTaller(data);
    }

    public static void main(String[] args) {
        // Configurar Look & Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        MainWindow vista = new MainWindow();
        GuiController controller = new GuiController(vista);
        controller.iniciar();
    }
}
