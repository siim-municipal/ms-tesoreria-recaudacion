package com.tuxoftware.ms_tesoreria_recaudacion.controller;

import com.tuxoftware.ms_tesoreria_recaudacion.dto.reporte.ComparativoAnualDTO;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.projection.ResumenIngresoDiarioView;
import com.tuxoftware.ms_tesoreria_recaudacion.service.ReporteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes Directivos")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/ingresos-diarios")
    public ResponseEntity<List<ResumenIngresoDiarioView>> getIngresosDiarios(
            @RequestParam(required = false) LocalDate fecha) {

        LocalDate fechaConsulta = (fecha != null) ? fecha : LocalDate.now();
        return ResponseEntity.ok(reporteService.obtenerReporteDiario(fechaConsulta));
    }

    @GetMapping("/comparativo-anual")
    public ResponseEntity<ComparativoAnualDTO> getComparativoAnual(
            @RequestParam Integer anio,
            @RequestParam Integer mes) {

        return ResponseEntity.ok(reporteService.obtenerComparativoAnual(anio, mes));
    }
}
