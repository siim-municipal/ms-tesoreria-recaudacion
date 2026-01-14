package com.tuxoftware.ms_tesoreria_recaudacion.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utilería para convertir montos monetarios a su representación en texto
 * siguiendo el estándar financiero mexicano (Pesos / M.N.).
 */
public class NumberToLetterConverter {

    private static final String[] UNIDADES = {"", "UN ", "DOS ", "TRES ", "CUATRO ", "CINCO ", "SEIS ", "SIETE ", "OCHO ", "NUEVE "};
    private static final String[] DECENAS = {"DIEZ ", "ONCE ", "DOCE ", "TRECE ", "CATORCE ", "QUINCE ", "DIECISEIS ", "DIECISIETE ", "DIECIOCHO ", "DIECINUEVE ", "VEINTE ", "TREINTA ", "CUARENTA ", "CINCUENTA ", "SESENTA ", "SETENTA ", "OCHENTA ", "NOVENTA "};
    private static final String[] CENTENAS = {"", "CIENTO ", "DOSCIENTOS ", "TRESCIENTOS ", "CUATROCIENTOS ", "QUINIENTOS ", "SEISCIENTOS ", "SETECIENTOS ", "OCHOCIENTOS ", "NOVECIENTOS "};

    private NumberToLetterConverter() {
        // Private constructor to prevent instantiation
    }

    public static String convert(BigDecimal monto) {
        if (monto == null) return "CERO PESOS 00/100 M.N.";

        // Aseguramos 2 decimales
        BigDecimal montoBig = monto.setScale(2, RoundingMode.HALF_UP);

        long parteEntera = montoBig.longValue();
        int parteDecimal = montoBig.remainder(BigDecimal.ONE).multiply(new BigDecimal(100)).intValue();

        StringBuilder resultado = new StringBuilder();

        if (parteEntera == 0) {
            resultado.append("CERO ");
        } else if (parteEntera > 999999999) {
            return "ERROR: MONTO DEMASIADO ALTO";
        } else {
            resultado.append(convertirNumero(parteEntera));
        }

        // Regla: Si termina en "MILLONES " se agrega "DE " antes de PESOS (Ej: 1,000,000 DE PESOS)
        String texto = resultado.toString().trim();
        if (texto.endsWith("MILLON") || texto.endsWith("MILLONES")) {
            texto += " DE";
        }

        // Moneda y Centavos (Formato estándar factura/recibo)
        return String.format("%s PESOS %02d/100 M.N.", texto, parteDecimal);
    }

    private static String convertirNumero(long n) {
        // RECURSIVIDAD POR GRUPOS

        if (n >= 1000000) { // Millones
            if (n == 1000000) return "UN MILLON ";
            if (n < 2000000) return "UN MILLON " + convertirNumero(n % 1000000);
            return convertirNumero(n / 1000000) + "MILLONES " + convertirNumero(n % 1000000);
        }

        if (n >= 1000) { // Miles
            if (n == 1000) return "UN MIL ";
            if (n < 2000) return "UN MIL " + convertirNumero(n % 1000);
            return convertirNumero(n / 1000) + "MIL " + convertirNumero(n % 1000);
        }

        // Centenas, Decenas y Unidades (0-999)
        if (n >= 100) {
            if (n == 100) return "CIEN ";
            return CENTENAS[(int) (n / 100)] + convertirNumero(n % 100);
        }

        if (n >= 20) {
            int decena = (int) (n / 10);
            int unidad = (int) (n % 10);

            if (n < 30) { // 20-29 (Veinti...)
                String[] veintes = {"VEINTE ", "VEINTIUN ", "VEINTIDOS ", "VEINTITRES ", "VEINTICUATRO ", "VEINTICINCO ", "VEINTISEIS ", "VEINTISIETE ", "VEINTIOCHO ", "VEINTINUEVE "};
                return veintes[unidad];
            } else { // 30-99
                String salida = DECENAS[decena + 8]; // +8 offset porque DECENAS empieza en DIEZ (index 0) hasta VEINTE (index 10) es lío, mejor mapeo directo.
                // Ajuste rápido para el array DECENAS definido arriba:
                // 10->0, 20->10, 30->11...
                // Mejor usemos switch simple para legibilidad en 30-90
                String nombreDecena = switch (decena) {
                    case 3 -> "TREINTA ";
                    case 4 -> "CUARENTA ";
                    case 5 -> "CINCUENTA ";
                    case 6 -> "SESENTA ";
                    case 7 -> "SETENTA ";
                    case 8 -> "OCHENTA ";
                    case 9 -> "NOVENTA ";
                    default -> "";
                };

                if (unidad > 0) return nombreDecena + "Y " + UNIDADES[unidad];
                return nombreDecena;
            }
        }

        if (n >= 10) { // 10-19
            return DECENAS[(int) (n - 10)];
        }

        if (n > 0) { // 1-9
            return UNIDADES[(int) n];
        }

        return "";
    }
}
