package Controller;

import Model.Cajero;
import View.VistaDeposito;
import View.VistaLogin;
import View.VistaMenu;
import View.VistaMovimientos;
import View.VistaRetiro;

public class ControladorMenu {

    private final Cajero modelo;
    private final VistaMenu vista;
    private final VistaLogin login;

    public ControladorMenu(Cajero modelo, VistaMenu vista, VistaLogin login) {
        this.modelo = modelo;
        this.vista = vista;
        this.login = login;

        vista.btnRetirar.addActionListener(e -> {
            vista.setVisible(false);
            new ControladorRetiro(modelo, new VistaRetiro(), this).mostrar();
        });
        vista.btnDepositar.addActionListener(e -> {
            vista.setVisible(false);
            new ControladorDeposito(modelo, new VistaDeposito(), this).mostrar();
        });
        vista.btnMovimientos.addActionListener(e -> {
            vista.setVisible(false);
            new ControladorMovimientos(modelo, new VistaMovimientos(), this).mostrar();
        });
        vista.btnConsultar.addActionListener(e ->
                vista.lblSaldo.setText("Saldo: $" + modelo.getSesion().getSaldo()));
        vista.btnSalir.addActionListener(e -> salir());
    }

    public void mostrar() {
        vista.lblBienvenida.setText("Hola, " + modelo.getSesion().getTitular());
        vista.lblSaldo.setText("Saldo: ****");
        vista.setVisible(true);
    }

    private void salir() {
        modelo.salir();
        vista.dispose();
        login.txtCuenta.setText("");
        login.lblMensaje.setText("");
        login.txtLogica.setText("");
        login.setVisible(true);
    }
}
