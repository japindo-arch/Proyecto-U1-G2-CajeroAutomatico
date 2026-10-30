package Model;

public class Cuenta {

    private final String numero;
    private final String titular;
    private final String pin;
    private double saldo;
    private int intentos;        // PIN incorrectos seguidos
    private boolean bloqueada;

    public Cuenta(String numero, String titular, String pin, double saldo) {
        this.numero = numero;
        this.titular = titular;
        this.pin = pin;
        this.saldo = saldo;
    }

    public String getNumero() { return numero; }
    public String getTitular() { return titular; }
    public double getSaldo() { return saldo; }
    public int getIntentos() { return intentos; }
    public boolean isBloqueada() { return bloqueada; }

    public boolean pinCorrecto(String intento) { return pin.equals(intento); }

    public void depositar(double monto) { saldo += monto; }
    public void retirar(double monto) { saldo -= monto; }

    // Al tercer fallo la cuenta se bloquea
    public void fallarPin() {
        intentos++;
        if (intentos >= 3) bloqueada = true;
    }

    public void reiniciarIntentos() { intentos = 0; }
}
