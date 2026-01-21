package com.tuxoftware.ms_tesoreria_recaudacion.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Resumen de un recibo de pago para el historial del predio.")
public record HistorialPagoResponse(
        @Schema(description = "Folio único del recibo", example = "REC-2024-00589")
        String folio,

        @Schema(description = "Año fiscal que cubre el pago", example = "2024")
        Integer anioFiscal,

        @Schema(description = "Fecha exacta de la transacción")
        LocalDateTime fechaPago,

        @Schema(description = "Monto total pagado", example = "1250.50")
        BigDecimal importe,

        @Schema(description = "Estado del pago", example = "PAGADO")
        String estatus
) {}
