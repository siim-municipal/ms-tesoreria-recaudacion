package com.tuxoftware.ms_tesoreria_recaudacion.persistence.projection;

import java.math.BigDecimal;

public interface TotalesComparativosView {
    BigDecimal getTotalActual();
    BigDecimal getTotalAnterior();
}
