package com.tuxoftware.ms_tesoreria_recaudacion.persistence.projection;

import java.math.BigDecimal;

public interface ResumenIngresoDiarioView {
    String getCodigoCri();
    String getRubroDescripcion();
    String getMetodoPago(); // EFECTIVO, TARJETA, TRANSFERENCIA
    BigDecimal getTotalRecaudado();
    Long getCantidadTransacciones();
}