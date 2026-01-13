package com.tuxoftware.ms_tesoreria_recaudacion.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.UUID;

public record AperturaCajaRequest(
        @NotNull(message = "El ID de la caja es obligatorio")
        UUID cajaId,

        @NotNull(message = "El saldo inicial es obligatorio")
        @PositiveOrZero(message = "El saldo inicial no puede ser negativo")
        BigDecimal saldoInicial
) {}