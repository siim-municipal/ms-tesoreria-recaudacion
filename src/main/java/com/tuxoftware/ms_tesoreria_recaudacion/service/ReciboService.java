package com.tuxoftware.ms_tesoreria_recaudacion.service;

import java.util.UUID;

public interface ReciboService {
    byte[] generarReciboPdf(UUID ingresoId);
}
