package com.tuxoftware.ms_tesoreria_recaudacion.controller;

import com.tuxoftware.ms_tesoreria_recaudacion.dto.request.IntencionPagoDTO;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.Ingreso;
import com.tuxoftware.ms_tesoreria_recaudacion.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
@Tag(name = "Tesorería - Procesamiento de Pagos", description = "Orquestador transaccional para la recepción de ingresos y emisión de recibos oficiales.")
public class PagoController {

    private final PagoService pagoService;

    @Operation(
            summary = "Procesar intención de pago (Cobro)",
            description = "Ejecuta el cobro validando previamente el monto con el motor de cálculo. " +
                    "Es una operación idempotente basada en el 'paymentRequestUUID'. " +
                    "Si el pago es exitoso, genera el registro de ingreso y dispara eventos de actualización a los padrones."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pago procesado exitosamente (o recuperado por idempotencia). Retorna el Recibo de Entero.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ingreso.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos en el request o sesión de caja cerrada/inexistente.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto Transaccional: \n" +
                            "1. Fraude detectado (el monto enviado no coincide con el cálculo del backend). \n" +
                            "2. Error de concurrencia.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno (Fallo en comunicación con ms-calculo o base de datos).",
                    content = @Content
            )
    })
    @PostMapping("/procesar")
    public ResponseEntity<Ingreso> procesarPago(
            @Parameter(description = "Alias del municipio para configuración multi-tenant", example = "TUXTEPEC")
            @RequestHeader(value = "X-Municipio-Alias", defaultValue = "TUXTEPEC") String municipio,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la transacción y validación de seguridad")
            @Valid @RequestBody IntencionPagoDTO request) {

        Ingreso recibo = pagoService.procesarPago(request, municipio);
        return ResponseEntity.ok(recibo);
    }
}
