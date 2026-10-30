package Model;

import java.util.List;
import java.util.function.Predicate;

/**
 * Temas del sílabo usados por el cajero:
 *  - Proposiciones y tabla de verdad (1.1.2)
 *  - Ley de De Morgan (1.1.3)
 *  - Modus Ponens / Modus Tollens (1.1.4)
 *  - Cuantificadores ∀ y ∃ (1.1.5)
 *  - Alfabeto, lenguaje y gramática (1.2)
 */
public class Logica {

    // ── 1.1.2 / 1.1.3 / 1.1.4: explica la decisión  A ↔ (p ∧ q ∧ r) ──────────
    // Las proposiciones son siempre afirmativas; negadas[i] = true hace que entren
    // a la fórmula como ¬x  (ej. p ∧ q ∧ ¬r)
    public static String explicar(String accion, String[] letras, String[] textos,
                                  boolean[] valores, boolean[] negadas) {
        String[] literales = literales(letras, negadas);
        boolean resultado = conjuncion(aplicar(valores, negadas));
        String formula = String.join(" ∧ ", literales);
        StringBuilder sb = new StringBuilder();

        sb.append("Proposiciones:\n");
        for (int i = 0; i < letras.length; i++) {
            sb.append("  ").append(letras[i]).append(": ").append(textos[i])
              .append(" = ").append(vf(valores[i])).append("\n");
        }
        sb.append("\nRegla: A ↔ (").append(formula).append(")    A: ").append(accion).append("\n\n");
        sb.append(tablaVerdad(letras, valores, negadas));

        if (resultado) {
            sb.append("\nModus Ponens: (").append(formula).append(") → A,  ")
              .append(formula).append(" es V   ∴ A\n");
        } else {
            sb.append("\nModus Tollens: A → (").append(formula).append("),  ¬(")
              .append(formula).append(")   ∴ ¬A\n");
            StringBuilder demorgan = new StringBuilder();
            StringBuilder simplificada = new StringBuilder();
            StringBuilder falla = new StringBuilder();
            boolean hayDobleNegacion = false;
            for (int i = 0; i < letras.length; i++) {
                if (i > 0) {
                    demorgan.append(" ∨ ");
                    simplificada.append(" ∨ ");
                }
                demorgan.append("¬").append(literales[i]);
                // ¬¬x ≡ x  (doble negación)
                simplificada.append(negadas[i] ? letras[i] : "¬" + letras[i]);
                if (negadas[i]) hayDobleNegacion = true;
                if (negadas[i] == valores[i]) {          // el literal es F
                    falla.append(" ").append(negadas[i] ? letras[i] : "¬" + letras[i]);
                }
            }
            sb.append("De Morgan: ¬(").append(formula).append(") ≡ ").append(demorgan).append("\n");
            if (hayDobleNegacion) {
                sb.append("Doble negación (¬¬x ≡ x): ≡ ").append(simplificada).append("\n");
            }
            sb.append("Falla:").append(falla).append("\n");
        }
        return sb.toString();
    }

    // Versión sin negaciones: A ↔ (p ∧ q ∧ ...)
    public static String explicar(String accion, String[] letras, String[] textos, boolean[] valores) {
        return explicar(accion, letras, textos, valores, new boolean[letras.length]);
    }

    public static boolean conjuncion(boolean[] valores) {
        for (boolean v : valores) {
            if (!v) return false;
        }
        return true;
    }

    // Valor de cada literal: x  o  ¬x
    private static boolean[] aplicar(boolean[] valores, boolean[] negadas) {
        boolean[] r = new boolean[valores.length];
        for (int i = 0; i < valores.length; i++) {
            r[i] = negadas[i] ? !valores[i] : valores[i];
        }
        return r;
    }

    private static String[] literales(String[] letras, boolean[] negadas) {
        String[] r = new String[letras.length];
        for (int i = 0; i < letras.length; i++) {
            r[i] = negadas[i] ? "¬" + letras[i] : letras[i];
        }
        return r;
    }

    // Tabla de verdad marcando la fila que ocurrió; agrega una columna por cada ¬x
    public static String tablaVerdad(String[] letras, boolean[] real, boolean[] negadas) {
        int n = letras.length;
        String formula = String.join(" ∧ ", literales(letras, negadas));
        StringBuilder sb = new StringBuilder(" ").append(String.join(" | ", letras)).append(" |");
        for (int j = 0; j < n; j++) {
            if (negadas[j]) sb.append(" ¬").append(letras[j]).append(" |");
        }
        sb.append(" ").append(formula).append("\n");

        for (int fila = 0; fila < (1 << n); fila++) {
            boolean[] v = new boolean[n];
            boolean esReal = true;
            for (int j = 0; j < n; j++) {
                v[j] = ((fila >> (n - 1 - j)) & 1) == 0;   // empieza con todo V
                sb.append(" ").append(vf(v[j])).append(" |");
                if (v[j] != real[j]) esReal = false;
            }
            for (int j = 0; j < n; j++) {
                if (negadas[j]) sb.append("  ").append(vf(!v[j])).append(" |");
            }
            sb.append("   ").append(vf(conjuncion(aplicar(v, negadas))));
            if (esReal) sb.append("   <- caso real");
            sb.append("\n");
        }
        return sb.toString();
    }

    public static String vf(boolean b) { return b ? "V" : "F"; }

    // ── 1.1.5 Cuantificadores ──────────────────────────────────────────────
    public static <T> boolean paraTodo(List<T> dominio, Predicate<T> p) {
        for (T x : dominio) {
            if (!p.test(x)) return false;
        }
        return true;
    }

    public static <T> boolean existe(List<T> dominio, Predicate<T> p) {
        for (T x : dominio) {
            if (p.test(x)) return true;
        }
        return false;
    }

    // ── 1.2 Alfabeto, lenguaje y gramática ─────────────────────────────────
    // Alfabeto Σ = {0,1,2,3,4,5,6,7,8,9}
    public static final String ALFABETO = "0123456789";

    // w ∈ Σ*  (todos sus símbolos pertenecen al alfabeto)
    public static boolean esPalabra(String w) {
        if (w == null || w.isEmpty()) return false;
        for (char c : w.toCharArray()) {
            if (ALFABETO.indexOf(c) < 0) return false;
        }
        return true;
    }

    // Lenguaje L_PIN = { w ∈ Σ* : |w| = 4 }
    public static boolean esPin(String w) {
        return esPalabra(w) && w.length() == 4;
    }

    // Lenguaje de enteros sin ceros a la izquierda (para depósitos)
    public static boolean esEntero(String w) {
        return esPalabra(w) && w.charAt(0) != '0';
    }

    /*
     * Gramática del monto de retiro (múltiplos de 10):
     *   S → A0
     *   A → X | XB
     *   B → Y | YB
     *   X → 1|2|...|9      Y → 0|1|...|9
     */
    public static boolean esMontoRetiro(String w) {
        return esEntero(w) && w.length() >= 2 && w.endsWith("0");
    }

    // Derivación de w con la gramática, ej. 120: S ⇒ A0 ⇒ XB0 ⇒ 1B0 ⇒ 1Y0 ⇒ 120
    public static String derivar(String w) {
        if (!esMontoRetiro(w)) return "El monto no se puede derivar: no pertenece al lenguaje";
        String a = w.substring(0, w.length() - 1);   // parte que genera A
        StringBuilder sb = new StringBuilder("S ⇒ A0");
        if (a.length() == 1) {
            return sb.append(" ⇒ X0 ⇒ ").append(w).toString();
        }
        sb.append(" ⇒ XB0 ⇒ ").append(a.charAt(0)).append("B0");
        for (int i = 1; i < a.length(); i++) {
            String hecho = a.substring(0, i);
            if (i == a.length() - 1) {
                sb.append(" ⇒ ").append(hecho).append("Y0 ⇒ ").append(w);
            } else {
                sb.append(" ⇒ ").append(hecho).append("YB0 ⇒ ").append(a, 0, i + 1).append("B0");
            }
        }
        return sb.toString();
    }
}
