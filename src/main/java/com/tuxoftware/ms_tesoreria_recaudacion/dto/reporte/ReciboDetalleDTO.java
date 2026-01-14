package com.tuxoftware.ms_tesoreria_recaudacion.dto.reporte;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReciboDetalleDTO {
    private String clave;
    private String concepto;
    private BigDecimal importe;
}