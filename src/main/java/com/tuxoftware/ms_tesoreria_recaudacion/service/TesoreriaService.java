package com.tuxoftware.ms_tesoreria_recaudacion.service;

import com.tuxoftware.ms_tesoreria_recaudacion.dto.response.HistorialPagoResponse;

import java.util.List;
import java.util.UUID;

public interface TesoreriaService {
    List<HistorialPagoResponse> obtenerHistorialPorPredio(UUID predioId);
}