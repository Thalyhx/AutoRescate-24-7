package co.ciencias.finalcc2.view;

import co.ciencias.finalcc2.controller.GuiController;
import co.ciencias.finalcc2.model.enums.TipoEmergencia;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MainWindow extends JFrame {

    private JTextField txtNombre, txtTelefono, txtX, txtY;
    private JComboBox<TipoEmergencia> cbTipo;
    private JButton btnRegistrar;
    
    private JTable tblCanales;
    private JTable tblEnRuta;
    private JTable tblTaller;
    
    private DefaultTableModel modelCanales;
    private DefaultTableModel modelEnRuta;
    private DefaultTableModel modelTaller;
    
    private JTextArea txtLog;
    private GuiController controller;

    public MainWindow() {
        setTitle("Sistema de AutoRescate 24/7 - Monitoreo en Tiempo Real");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        initHeader();
        initLeftPanel();
        initCenterPanel();
        initBottomPanel();
    }

    public void setController(GuiController controller) {
        this.controller = controller;
    }

    private void initHeader() {
        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(new Color(41, 128, 185));
        JLabel lblTitle = new JLabel("AUTORESCATE 24/7 - PANEL DE CONTROL");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);
    }

    private void initLeftPanel() {
        JPanel pnlLeft = new JPanel(new GridBagLayout());
        pnlLeft.setBorder(BorderFactory.createTitledBorder("Nueva Emergencia"));
        pnlLeft.setPreferredSize(new Dimension(300, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        pnlLeft.add(new JLabel("Nombre Cliente:"), gbc);
        gbc.gridy++;
        txtNombre = new JTextField();
        pnlLeft.add(txtNombre, gbc);

        gbc.gridy++;
        pnlLeft.add(new JLabel("Teléfono:"), gbc);
        gbc.gridy++;
        txtTelefono = new JTextField();
        pnlLeft.add(txtTelefono, gbc);

        gbc.gridy++;
        pnlLeft.add(new JLabel("Tipo Emergencia:"), gbc);
        gbc.gridy++;
        cbTipo = new JComboBox<>(TipoEmergencia.values());
        pnlLeft.add(cbTipo, gbc);

        gbc.gridy++;
        pnlLeft.add(new JLabel("Coordenada X:"), gbc);
        gbc.gridy++;
        txtX = new JTextField();
        pnlLeft.add(txtX, gbc);

        gbc.gridy++;
        pnlLeft.add(new JLabel("Coordenada Y:"), gbc);
        gbc.gridy++;
        txtY = new JTextField();
        pnlLeft.add(txtY, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 5, 5, 5);
        btnRegistrar = new JButton("REGISTRAR LLAMADA");
        btnRegistrar.setBackground(new Color(39, 174, 96));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegistrar.addActionListener(e -> registrarEmergencia());
        pnlLeft.add(btnRegistrar, gbc);

        gbc.gridy++;
        gbc.weighty = 1.0;
        pnlLeft.add(new JPanel(), gbc); // Spacer

        add(pnlLeft, BorderLayout.WEST);
    }

    private void initCenterPanel() {
        JPanel pnlCenter = new JPanel(new GridLayout(3, 1, 10, 10));
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        // Tabla Canales
        modelCanales = new DefaultTableModel(new Object[]{"Canal", "Estado Reloj", "En Cola"}, 0);
        tblCanales = new JTable(modelCanales);
        pnlCenter.add(createTablePanel("Canales de Despacho (Automático cada 10s)", tblCanales));

        // Tabla En Ruta
        modelEnRuta = new DefaultTableModel(new Object[]{"Cliente", "Ubicación", "Unidad", "Técnico", "Tiempo Restante"}, 0);
        tblEnRuta = new JTable(modelEnRuta);
        pnlCenter.add(createTablePanel("Servicios en Ruta (Atención Activa)", tblEnRuta));

        // Tabla Taller
        modelTaller = new DefaultTableModel(new Object[]{"Unidad", "Técnico", "Estado", "Listo en"}, 0);
        tblTaller = new JTable(modelTaller);
        pnlCenter.add(createTablePanel("Taller de Mantenimiento y Reabastecimiento", tblTaller));

        add(pnlCenter, BorderLayout.CENTER);
    }

    private void initBottomPanel() {
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBorder(BorderFactory.createTitledBorder("Log del Sistema"));
        pnlBottom.setPreferredSize(new Dimension(0, 150));

        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setBackground(Color.BLACK);
        txtLog.setForeground(new Color(0, 255, 0));
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 12));
        pnlBottom.add(new JScrollPane(txtLog), BorderLayout.CENTER);

        add(pnlBottom, BorderLayout.SOUTH);
    }

    private JPanel createTablePanel(String title, JTable table) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void registrarEmergencia() {
        if (controller != null) {
            String nombre = txtNombre.getText();
            String tel = txtTelefono.getText();
            TipoEmergencia tipo = (TipoEmergencia) cbTipo.getSelectedItem();
            String xStr = txtX.getText();
            String yStr = txtY.getText();

            if (nombre.isEmpty() || tel.isEmpty() || xStr.isEmpty() || yStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor complete todos los campos.");
                return;
            }

            try {
                double x = Double.parseDouble(xStr);
                double y = Double.parseDouble(yStr);
                controller.registrarNuevaSolicitud(nombre, tel, tipo, x, y);
                
                // Limpiar campos
                txtNombre.setText("");
                txtTelefono.setText("");
                txtX.setText("");
                txtY.setText("");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Coordenadas inválidas.");
            }
        }
    }

    // Métodos para actualizar la UI desde el controlador
    public void actualizarCanales(Object[][] data) {
        updateTableModel(modelCanales, data);
    }

    public void actualizarEnRuta(Object[][] data) {
        updateTableModel(modelEnRuta, data);
    }

    public void actualizarTaller(Object[][] data) {
        updateTableModel(modelTaller, data);
    }

    private void updateTableModel(DefaultTableModel model, Object[][] data) {
        model.setRowCount(0);
        for (Object[] row : data) {
            model.addRow(row);
        }
    }

    public void log(String msg) {
        txtLog.append(msg + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }
}
