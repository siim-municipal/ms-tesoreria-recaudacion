package com.tuxoftware.ms_tesoreria_recaudacion.service;

import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.SesionCaja;

import java.util.UUID;

public interface ReciboService {
    byte[] generarReciboPdf(UUID ingresoId);

    byte[] generarPdfCorte(SesionCaja sesion);
}
