package Model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Cajero {

    public static final double MAX_RETIRO = 500;
    public static final double MAX_DEPOSITO = 5000;

    private final Map<String, Cuenta> cuentas = new LinkedHashMap<>();
    private final List<Movimiento> movimientos = new ArrayList<>();
    private Cuenta sesion;
    private String mensaje = "";
    private String explicacion = "";

    public Cajero() {
        // Cuentas de prueba (número / PIN)
        agregar(new Cuenta("1001", "Juan Pérez", "1234", 850));
        agregar(new Cuenta("1002", "María López", "4321", 2300));
    }

    private void agregar(Cuenta c) { cuentas.put(c.getNumero(), c); }

    // Ingreso:  A ↔ (p ∧ q ∧ ¬r)
    public boolean ingresar(String numero, String pin) {
        Cuenta c = cuentas.get(numero);
        boolean p = c != null;
        boolean q = p && Logica.esPin(pin) && c.pinCorrecto(pin);
        boolean r = p && c.isBloqueada();   // una cuenta que no existe no está bloqueada

        boolean ok = p && q && !r;
        explicacion = Logica.explicar("se permite el ingreso",
                new String[]{"p", "q", "r"},
                new String[]{"La cuenta existe", "El PIN es correcto", "La cuenta está bloqueada"},
                new boolean[]{p, q, r},
                new boolean[]{false, false, true});   // r entra negada: ¬r

        if (ok) {
            c.reiniciarIntentos();
            sesion = c;
            mensaje = "Bienvenido " + c.getTitular();
        } else if (!p) {
            mensaje = "La cuenta no existe";
        } else if (r) {
            mensaje = "Cuenta bloqueada";
        } else {
            c.fallarPin();
            mensaje = c.isBloqueada() ? "PIN incorrecto. Cuenta BLOQUEADA"
                                      : "PIN incorrecto. Intentos: " + c.getIntentos() + " de 3";
            if (c.isBloqueada()) {
                explicacion += "\nSilogismo hipotético: si falla 3 veces → se bloquea; "
                             + "si se bloquea → no ingresa  ∴ si falla 3 veces → no ingresa\n";
            }
        }
        return ok;
    }

    // Retiro:  A ↔ (p ∧ q ∧ r)
    public boolean retirar(String texto) {
        String w = texto.trim();
        if (!Logica.esPalabra(w)) {
            return rechazarNoNumero("RETIRO", w);
        }
        // Cada proposición se evalúa por separado: ninguna depende de otra
        double monto = Double.parseDouble(w);
        boolean p = Logica.esMontoRetiro(w);
        boolean q = monto <= sesion.getSaldo();
        boolean r = monto <= MAX_RETIRO;

        boolean ok = Logica.conjuncion(new boolean[]{p, q, r});
        explicacion = "Gramática: " + Logica.derivar(w) + "\n\n"
                + Logica.explicar("se aprueba el retiro",
                        new String[]{"p", "q", "r"},
                        new String[]{"El monto pertenece al lenguaje (múltiplo de 10, sin ceros a la izquierda)",
                                     "Monto ≤ saldo ($" + sesion.getSaldo() + ")", "Monto ≤ $" + MAX_RETIRO},
                        new boolean[]{p, q, r});

        if (ok) {
            sesion.retirar(monto);
            mensaje = "Retire su dinero. Saldo: $" + sesion.getSaldo();
        } else {
            // Se informan todas las condiciones que fallaron
            StringBuilder m = new StringBuilder();
            if (!p) m.append("Debe ser múltiplo de 10 y sin ceros a la izquierda. ");
            if (!q) m.append("Saldo insuficiente. ");
            if (!r) m.append("Máximo $").append(MAX_RETIRO).append(" por retiro.");
            mensaje = m.toString().trim();
        }
        movimientos.add(new Movimiento("RETIRO", monto, ok, sesion.getSaldo()));
        return ok;
    }

    // Depósito:  A ↔ (p ∧ q)
    public boolean depositar(String texto) {
        String w = texto.trim();
        if (!Logica.esPalabra(w)) {
            return rechazarNoNumero("DEPOSITO", w);
        }
        double monto = Double.parseDouble(w);
        boolean p = Logica.esEntero(w);
        boolean q = monto <= MAX_DEPOSITO;

        boolean ok = Logica.conjuncion(new boolean[]{p, q});
        explicacion = Logica.explicar("se acepta el depósito",
                new String[]{"p", "q"},
                new String[]{"El monto es un entero positivo sin ceros a la izquierda",
                             "Monto ≤ $" + MAX_DEPOSITO},
                new boolean[]{p, q});

        if (ok) {
            sesion.depositar(monto);
            mensaje = "Depósito exitoso. Saldo: $" + sesion.getSaldo();
        } else {
            StringBuilder m = new StringBuilder();
            if (!p) m.append("Monto inválido (sin ceros a la izquierda y mayor a 0). ");
            if (!q) m.append("Máximo $").append(MAX_DEPOSITO).append(".");
            mensaje = m.toString().trim();
        }
        movimientos.add(new Movimiento("DEPOSITO", monto, ok, sesion.getSaldo()));
        return ok;
    }

    /*
     * Si el texto tiene símbolos fuera de Σ = {0..9} (letras, signos o vacío),
     * no representa un monto. "abc ≤ saldo" no es V ni F, así que NO es una
     * proposición: la operación se rechaza antes de armar p, q, r.
     */
    private boolean rechazarNoNumero(String tipo, String w) {
        mensaje = "Ingrese solo números";
        explicacion = "Entrada: \"" + w + "\"\n\n"
                + "w ∉ Σ*  (Σ = {0,1,2,3,4,5,6,7,8,9}): tiene símbolos que no son del alfabeto"
                + (w.isEmpty() ? " (está vacía)" : "") + ".\n\n"
                + "Como w no representa un monto, enunciados como \"w ≤ saldo\" no son\n"
                + "verdaderos ni falsos: NO son proposiciones. Por eso no se evalúan\n"
                + "p, q, r y la operación se rechaza.\n";
        movimientos.add(new Movimiento(tipo, 0, false, sesion.getSaldo()));
        return false;
    }

    // Cuantificadores sobre los movimientos
    public String cuantificadores() {
        return "∀m: m fue aprobado        = " + Logica.vf(Logica.paraTodo(movimientos, Movimiento::isAprobado)) + "\n"
             + "∃m: m fue rechazado       = " + Logica.vf(Logica.existe(movimientos, m -> !m.isAprobado())) + "\n"
             + "∃m: m es un depósito      = " + Logica.vf(Logica.existe(movimientos, m -> m.getTipo().equals("DEPOSITO"))) + "\n"
             + "∀m: saldo de m ≥ 0        = " + Logica.vf(Logica.paraTodo(movimientos, m -> m.getSaldo() >= 0)) + "\n"
             + "Ley: ¬∀m aprobado(m) ≡ ∃m ¬aprobado(m)";
    }

    public void salir() {
        sesion = null;
        movimientos.clear();
    }

    public Cuenta getSesion() { return sesion; }
    public List<Movimiento> getMovimientos() { return movimientos; }
    public String getMensaje() { return mensaje; }
    public String getExplicacion() { return explicacion; }
}
