package com.tuxoftware.ms_tesoreria_recaudacion.service;

import com.tuxoftware.ms_tesoreria_recaudacion.dto.reporte.ComparativoAnualDTO;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.projection.ResumenIngresoDiarioView;

import java.time.LocalDate;
import java.util.List;

public interface ReporteService {

    List<ResumenIngresoDiarioView> obtenerReporteDiario(LocalDate fecha);

    ComparativoAnualDTO obtenerComparativoAnual(int anio, int mes);

}
