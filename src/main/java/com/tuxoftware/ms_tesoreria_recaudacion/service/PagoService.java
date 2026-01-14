package com.tuxoftware.ms_tesoreria_recaudacion.service;

import com.tuxoftware.ms_tesoreria_recaudacion.dto.request.IntencionPagoDTO;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.Ingreso;

public interface PagoService {

    Ingreso procesarPago(IntencionPagoDTO request, String municipioAlias);

}
