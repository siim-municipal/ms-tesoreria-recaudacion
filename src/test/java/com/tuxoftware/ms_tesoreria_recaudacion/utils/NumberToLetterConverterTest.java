package com.tuxoftware.ms_tesoreria_recaudacion.utils;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class NumberToLetterConverterTest {

    @Test
    void testConversiones() {
        assertEquals("UN MIL QUINIENTOS PESOS 50/100 M.N.",
                NumberToLetterConverter.convert(new BigDecimal("1500.50")));

        assertEquals("CIEN PESOS 00/100 M.N.",
                NumberToLetterConverter.convert(new BigDecimal("100.00")));

        assertEquals("CIENTO UN PESOS 00/100 M.N.",
                NumberToLetterConverter.convert(new BigDecimal("101")));

        assertEquals("UN MIL PESOS 00/100 M.N.",
                NumberToLetterConverter.convert(new BigDecimal("1000")));

        assertEquals("DOS MIL PESOS 00/100 M.N.",
                NumberToLetterConverter.convert(new BigDecimal("2000")));

        assertEquals("UN MILLON DE PESOS 00/100 M.N.",
                NumberToLetterConverter.convert(new BigDecimal("1000000")));

        assertEquals("UN MILLON QUINIENTOS MIL PESOS 00/100 M.N.",
                NumberToLetterConverter.convert(new BigDecimal("1500000")));

        assertEquals("VEINTIUN MIL QUINIENTOS DOCE PESOS 34/100 M.N.",
                NumberToLetterConverter.convert(new BigDecimal("21512.34")));

        assertEquals("CERO PESOS 50/100 M.N.",
                NumberToLetterConverter.convert(new BigDecimal("0.50")));
    }
}