package com.tuxoftware.ms_tesoreria_recaudacion.controller;

import com.tuxoftware.ms_tesoreria_recaudacion.dto.response.HistorialPagoResponse;
import com.tuxoftware.ms_tesoreria_recaudacion.service.TesoreriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ingresos")
@RequiredArgsConstructor
@Tag(name = "Consultas de Ingresos", description = "Endpoints para recuperar historial y reportes de recaudación.")
public class IngresoController {
    private final TesoreriaService tesoreriaService;

    @Operation(summary = "Obtener historial de pagos de un predio",
            description = "Devuelve la lista de recibos (pagados y cancelados) asociados a un predio específico.")
    @GetMapping("/historial/predio/{predioId}")
    public ResponseEntity<List<HistorialPagoResponse>> obtenerHistorialPredio(
            @Parameter(description = "UUID del predio a consultar", required = true)
            @PathVariable UUID predioId) {

        return ResponseEntity.ok(tesoreriaService.obtenerHistorialPorPredio(predioId));
    }
}
