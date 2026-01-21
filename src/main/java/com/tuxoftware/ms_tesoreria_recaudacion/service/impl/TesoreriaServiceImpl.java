package com.tuxoftware.ms_tesoreria_recaudacion.service.impl;

import com.tuxoftware.ms_tesoreria_recaudacion.dto.response.HistorialPagoResponse;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.Ingreso;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.repository.IngresoRepository;
import com.tuxoftware.ms_tesoreria_recaudacion.service.TesoreriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TesoreriaServiceImpl implements TesoreriaService {

    private final IngresoRepository ingresoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HistorialPagoResponse> obtenerHistorialPorPredio(UUID predioId) {
        List<Ingreso> ingresos = ingresoRepository.findByPredioIdOrderByFechaEmisionDesc(predioId);

        return ingresos.stream()
                .map(this::mapToDto)
                .toList();
    }

    private HistorialPagoResponse mapToDto(Ingreso ingreso) {
        int anioFiscal = ingreso.getFechaEmision().getYear();

        // Generamos un Folio legible si no existe uno de negocio, usando el UUID corto
        String folioLegible = "REC-" + anioFiscal + "-" + ingreso.getId().toString().substring(0, 8).toUpperCase();

        return new HistorialPagoResponse(
                folioLegible,
                anioFiscal,
                ingreso.getFechaEmision(),
                ingreso.getTotalCobrado(),
                ingreso.getEstatus()
        );
    }
}