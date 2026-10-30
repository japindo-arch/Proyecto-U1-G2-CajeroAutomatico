package Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Movimiento {

    private final String tipo;       // RETIRO o DEPOSITO
    private final double monto;
    private final String fecha;
    private final boolean aprobado;
    private final double saldo;      // saldo después de la operación

    public Movimiento(String tipo, double monto, boolean aprobado, double saldo) {
        this.tipo = tipo;
        this.monto = monto;
        this.aprobado = aprobado;
        this.saldo = saldo;
        this.fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getTipo() { return tipo; }
    public double getMonto() { return monto; }
    public String getFecha() { return fecha; }
    public boolean isAprobado() { return aprobado; }
    public double getSaldo() { return saldo; }
}
