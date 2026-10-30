package com.mycompany.cajeroautomatico;

import Controller.ControladorLogin;
import Model.Cajero;
import View.VistaLogin;

public class CajeroAutomatico {

    public static void main(String[] args) {
        Cajero modelo = new Cajero();
        VistaLogin vista = new VistaLogin();
        new ControladorLogin(modelo, vista);
        vista.setVisible(true);
    }
}
