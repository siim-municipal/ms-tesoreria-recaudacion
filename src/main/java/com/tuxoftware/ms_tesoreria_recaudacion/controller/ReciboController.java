package com.tuxoftware.ms_tesoreria_recaudacion.controller;

import com.tuxoftware.ms_tesoreria_recaudacion.service.ReciboService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recibos")
@RequiredArgsConstructor
@Tag(name = "Tesorería - Emisión de Recibos", description = "Generación y descarga de documentos oficiales probatorios de pago (PDF).")
public class ReciboController {

    private final ReciboService reciboService;

    @Operation(
            summary = "Descargar Recibo Oficial (PDF)",
            description = "Genera al vuelo el PDF del recibo de pago basándose en un Ingreso existente. " +
                    "El documento incluye folio, desglose de conceptos, totales en letra y QR de seguridad."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Documento generado exitosamente. Se descarga como archivo binario.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PDF_VALUE,
                            schema = @Schema(type = "string", format = "binary") // <--- ESTO ES CRUCIAL PARA ARCHIVOS
                    )
            ),
            @ApiResponse(responseCode = "404", description = "El ingreso solicitado no existe.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno en el motor de reportes (JasperReports).", content = @Content)
    })
    @GetMapping(value = "/{ingresoId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> descargarRecibo(
            @Parameter(description = "UUID del ingreso registrado previamente", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID ingresoId) {

        byte[] pdfBytes = reciboService.generarReciboPdf(ingresoId);

        HttpHeaders headers = new HttpHeaders();
        // Usamos "inline" para que el navegador intente mostrarlo en su visor PDF nativo.
        // Si prefieres que siempre se descargue, usa "attachment".
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=recibo_" + ingresoId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
