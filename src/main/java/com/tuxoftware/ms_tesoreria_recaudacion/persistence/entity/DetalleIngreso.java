package com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "operacion_detalles_ingreso")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleIngreso {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingreso_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Ingreso ingreso;

    // Vinculación estricta al CRI
    @ManyToOne(fetch = FetchType.EAGER) // Eager porque casi siempre necesitamos saber qué rubro es
    @JoinColumn(name = "codigo_cri", nullable = false)
    private CatalogoRubros rubro;

    @Column(name = "monto", nullable = false, precision = 18, scale = 2)
    private BigDecimal monto;

    @Column(name = "concepto_descripcion")
    private String conceptoDescripcion; // Ej: "Predial 2025"
}