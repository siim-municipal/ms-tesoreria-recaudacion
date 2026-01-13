package com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cat_rubros_ingresos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogoRubros {

    @Id
    @Column(name = "codigo_cri", length = 20, nullable = false)
    private String codigoCri; // Ej: "1.2.1.1", "4.3.1"

    @Column(nullable = false, length = 255)
    private String descripcion; // Ej: "Impuestos sobre el patrimonio"

    @Column(name = "cuenta_contable", length = 50)
    private String cuentaContable; // Vinculación con Plan de Cuentas (CONAC)

    @Column(name = "nivel", nullable = false)
    private Integer nivel; // 1=Rubro, 2=Tipo, 3=Clase, 4=Concepto
}