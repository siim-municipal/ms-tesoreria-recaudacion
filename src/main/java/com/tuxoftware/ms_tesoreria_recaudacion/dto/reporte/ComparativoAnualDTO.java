package com.tuxoftware.ms_tesoreria_recaudacion.dto.reporte;

import java.math.BigDecimal;

public record ComparativoAnualDTO(
        Integer mes,
        BigDecimal totalActual,
        BigDecimal totalAnterior,
        BigDecimal variacionPorcentual
) {}
