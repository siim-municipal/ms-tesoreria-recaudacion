package com.tuxoftware.ms_tesoreria_recaudacion.controller;

import com.tuxoftware.ms_tesoreria_recaudacion.dto.request.AperturaCajaRequest;
import com.tuxoftware.ms_tesoreria_recaudacion.dto.request.CorteCajaRequest;
import com.tuxoftware.ms_tesoreria_recaudacion.service.CajaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cajas")
@RequiredArgsConstructor
@Tag(name = "Operación de Cajas", description = "Gestión de sesiones de cobro")
public class CajaController {

    private final CajaService cajaService;

    @PostMapping("/apertura")
    @Operation(summary = "Abrir sesión de caja")
    public ResponseEntity<Map<String, UUID>> abrirCaja(
            @Valid @RequestBody AperturaCajaRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        // Extraemos el ID del usuario del token (Subject o campo custom)
        String usuarioId = jwt.getSubject();

        UUID sesionId = cajaService.abrirCaja(request, usuarioId);

        return ResponseEntity
                .created(URI.create("/api/v1/cajas/sesiones/" + sesionId))
                .body(Map.of("sesionId", sesionId));
    }

    @PostMapping("/cierre/{sesionId}")
    @Operation(summary = "Cerrar sesión de caja")
    public ResponseEntity<Void> cerrarCaja(
            @PathVariable UUID sesionId,
            @AuthenticationPrincipal Jwt jwt) {

        cajaService.cerrarSesion(sesionId, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Realizar Corte de Caja", description = "Cierra la sesión, calcula diferencias y devuelve el reporte PDF.")
    @PostMapping(value = "/corte", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> realizarCorte(@Valid @RequestBody CorteCajaRequest request) {

        byte[] pdfBytes = cajaService.realizarCorteCaja(request);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=corte_" + request.sesionCajaId() + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}