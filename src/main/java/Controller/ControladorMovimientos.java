package Controller;

import Model.Cajero;
import Model.Movimiento;
import View.VistaMovimientos;
import javax.swing.table.DefaultTableModel;

public class ControladorMovimientos {

    private final Cajero modelo;
    private final VistaMovimientos vista;
    private final ControladorMenu menu;

    public ControladorMovimientos(Cajero modelo, VistaMovimientos vista, ControladorMenu menu) {
        this.modelo = modelo;
        this.vista = vista;
        this.menu = menu;
        vista.btnVolver.addActionListener(e -> volver());
    }

    public void mostrar() {
        DefaultTableModel tabla = new DefaultTableModel(
                new String[]{"Fecha", "Tipo", "Monto", "Estado", "Saldo"}, 0);
        for (Movimiento m : modelo.getMovimientos()) {
            tabla.addRow(new Object[]{
                m.getFecha(), m.getTipo(), m.getMonto(),
                m.isAprobado() ? "APROBADO" : "RECHAZADO", m.getSaldo()
            });
        }
        vista.tblMovimientos.setModel(tabla);
        vista.txtCuantificadores.setText(modelo.cuantificadores());
        vista.setVisible(true);
    }

    private void volver() {
        vista.dispose();
        menu.mostrar();
    }
}
