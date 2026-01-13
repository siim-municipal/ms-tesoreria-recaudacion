package com.tuxoftware.ms_tesoreria_recaudacion.controller;

import com.tuxoftware.ms_tesoreria_recaudacion.dto.request.AperturaCajaRequest;
import com.tuxoftware.ms_tesoreria_recaudacion.service.CajaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
}