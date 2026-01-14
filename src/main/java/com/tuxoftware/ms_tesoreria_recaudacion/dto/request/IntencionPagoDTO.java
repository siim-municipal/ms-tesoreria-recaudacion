package com.tuxoftware.ms_tesoreria_recaudacion.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Estructura para solicitar un cobro. Contiene tokens de seguridad e idempotencia.")
public record IntencionPagoDTO(
        @Schema(description = "UUID único generado por el Frontend para evitar dobles cobros (Idempotencia)", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
        @NotNull UUID paymentRequestUUID,

        @Schema(description = "ID del ciudadano que realiza el pago", example = "55555555-5555-5555-5555-555555555555")
        @NotNull UUID contribuyenteId,

        @Schema(description = "ID de la sesión de caja abierta actualmente", example = "caja-uuid-sesion-abierta")
        @NotNull UUID sesionCajaId,

        @Schema(description = "Clave de negocio del concepto a cobrar", example = "IMP_PREDIAL_URBANO")
        @NotNull String claveConcepto,

        @Schema(description = "Monto total calculado previamente. Se re-validará en backend.", example = "1500.00")
        @NotNull BigDecimal montoTotal,

        @Schema(description = "ID del objeto cobrado (Predio, Contrato Agua, Licencia)", example = "02fa39a4-810b-4ab1-a55d-b282d0ec15e2")
        @NotNull UUID referenciaId,

        @Schema(description = "Año fiscal al que aplica el pago", example = "2025")
        Integer anioFiscal
) {}