package com.tuxoftware.ms_tesoreria_recaudacion.service;

import com.tuxoftware.ms_tesoreria_recaudacion.dto.request.AperturaCajaRequest;
import com.tuxoftware.ms_tesoreria_recaudacion.dto.request.CorteCajaRequest;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.Caja;

import java.util.List;
import java.util.UUID;

public interface CajaService {

    List<Caja> listarCajasDisponibles();

    UUID abrirCaja(AperturaCajaRequest request, String usuarioId);

    void cerrarSesion(UUID sesionId, String usuarioId);

    byte[] realizarCorteCaja(CorteCajaRequest request);
}
