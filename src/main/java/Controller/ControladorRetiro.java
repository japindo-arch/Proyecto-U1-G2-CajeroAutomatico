package Controller;

import Model.Cajero;
import View.VistaRetiro;

public class ControladorRetiro {

    private final Cajero modelo;
    private final VistaRetiro vista;
    private final ControladorMenu menu;

    public ControladorRetiro(Cajero modelo, VistaRetiro vista, ControladorMenu menu) {
        this.modelo = modelo;
        this.vista = vista;
        this.menu = menu;
        vista.btnRetirar.addActionListener(e -> retirar());
        vista.btnVolver.addActionListener(e -> volver());
    }

    private void retirar() {
        modelo.retirar(vista.txtMonto.getText());
        vista.lblMensaje.setText(modelo.getMensaje());
        vista.txtLogica.setText(modelo.getExplicacion());
    }

    public void mostrar() {
        vista.setVisible(true);
    }

    private void volver() {
        vista.dispose();
        menu.mostrar();
    }
}
