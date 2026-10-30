package Controller;

import Model.Cajero;
import View.VistaDeposito;

public class ControladorDeposito {

    private final Cajero modelo;
    private final VistaDeposito vista;
    private final ControladorMenu menu;

    public ControladorDeposito(Cajero modelo, VistaDeposito vista, ControladorMenu menu) {
        this.modelo = modelo;
        this.vista = vista;
        this.menu = menu;
        vista.btnDepositar.addActionListener(e -> depositar());
        vista.btnVolver.addActionListener(e -> volver());
    }

    private void depositar() {
        modelo.depositar(vista.txtMonto.getText());
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
