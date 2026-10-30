package Controller;

import Model.Cajero;
import View.VistaLogin;
import View.VistaMenu;

public class ControladorLogin {

    private final Cajero modelo;
    private final VistaLogin vista;

    public ControladorLogin(Cajero modelo, VistaLogin vista) {
        this.modelo = modelo;
        this.vista = vista;
        vista.btnIngresar.addActionListener(e -> ingresar());
        vista.btnSalir.addActionListener(e -> System.exit(0));
    }

    private void ingresar() {
        String cuenta = vista.txtCuenta.getText().trim();
        String pin = new String(vista.txtPin.getPassword());

        boolean ok = modelo.ingresar(cuenta, pin);
        vista.lblMensaje.setText(modelo.getMensaje());
        vista.txtLogica.setText(modelo.getExplicacion());
        vista.txtPin.setText("");

        if (ok) {
            vista.setVisible(false);
            new ControladorMenu(modelo, new VistaMenu(), vista).mostrar();
        }
    }
}
