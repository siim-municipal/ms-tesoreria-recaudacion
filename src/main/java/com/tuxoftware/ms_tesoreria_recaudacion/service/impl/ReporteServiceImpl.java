package com.tuxoftware.ms_tesoreria_recaudacion.service.impl;


import com.tuxoftware.ms_tesoreria_recaudacion.dto.reporte.ComparativoAnualDTO;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.projection.ResumenIngresoDiarioView;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.projection.TotalesComparativosView;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.repository.IngresoRepository;
import com.tuxoftware.ms_tesoreria_recaudacion.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final IngresoRepository ingresoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ResumenIngresoDiarioView> obtenerReporteDiario(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(LocalTime.MAX);

        return ingresoRepository.obtenerResumenDiario(inicio, fin);
    }

    @Override
    @Transactional(readOnly = true)
    public ComparativoAnualDTO obtenerComparativoAnual(int anio, int mes) {
        // 1. Definir Rangos de Fechas
        LocalDate inicioMesActual = LocalDate.of(anio, mes, 1);
        LocalDateTime inicioActual = inicioMesActual.atStartOfDay();
        LocalDateTime finActual = inicioMesActual.withDayOfMonth(inicioMesActual.lengthOfMonth()).atTime(LocalTime.MAX);

        LocalDateTime inicioAnterior = inicioActual.minusYears(1);
        LocalDateTime finAnterior = finActual.minusYears(1);

        // 2. Ejecutar Query
        // La query retorna UN solo arreglo de objetos: [TotalActual, TotalAnterior]
        TotalesComparativosView resultados = ingresoRepository.obtenerTotalesComparativos(
                inicioActual, finActual, inicioAnterior, finAnterior
        );

        BigDecimal actual = Optional.ofNullable(resultados.getTotalActual())
                .orElse(BigDecimal.ZERO);

        BigDecimal anterior = Optional.ofNullable(resultados.getTotalAnterior())
                .orElse(BigDecimal.ZERO);


        // 4. Calcular y Retornar
        return calcularDTO(mes, actual, anterior);
    }

    private ComparativoAnualDTO calcularDTO(int mes, BigDecimal actual, BigDecimal anterior) {
        BigDecimal variacion = BigDecimal.ZERO;
        if (anterior.compareTo(BigDecimal.ZERO) > 0) {
            variacion = actual.subtract(anterior)
                    .divide(anterior, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        } else if (actual.compareTo(BigDecimal.ZERO) > 0) {
            variacion = BigDecimal.valueOf(100); // Crecimiento infinito (0 a algo)
        }

        return new ComparativoAnualDTO(mes, actual, anterior, variacion);
    }
}
