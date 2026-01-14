package com.tuxoftware.ms_tesoreria_recaudacion.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Schema(description = "Datos para realizar el corte y arqueo de caja")
public record CorteCajaRequest(
        @NotNull UUID sesionCajaId,
        @Schema(description = "Mapa de Denominación -> Cantidad de Piezas (Ej: '500.00': 10)", example = "{\"500.00\": 10, \"200.00\": 5, \"50.00\": 20}")
        @NotNull Map<BigDecimal, Integer> desgloseEfectivo,
        String observaciones
) {}