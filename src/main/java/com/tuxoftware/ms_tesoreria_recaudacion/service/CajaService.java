package com.tuxoftware.ms_tesoreria_recaudacion.service;

import com.tuxoftware.ms_tesoreria_recaudacion.dto.request.AperturaCajaRequest;
import com.tuxoftware.ms_tesoreria_recaudacion.dto.request.CorteCajaRequest;

import java.util.UUID;

public interface CajaService {

    UUID abrirCaja(AperturaCajaRequest request, String usuarioId);

    void cerrarSesion(UUID sesionId, String usuarioId);

    byte[] realizarCorteCaja(CorteCajaRequest request);
}
