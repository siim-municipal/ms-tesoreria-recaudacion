package com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "operacion_ingresos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ingreso {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    // Referencia al contribuyente (Sujeto Pasivo)
    @Column(name = "contribuyente_id")
    private UUID contribuyenteId;

    // Referencia a la sesión de caja donde se cobró
    @Column(name = "sesion_caja_id", nullable = false)
    private UUID sesionCajaId;

    @Column(name = "total_cobrado", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalCobrado;

    @Column(length = 20) // PAGADO, CANCELADO
    private String estatus;

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;

    // Relación OneToMany con el detalle
    @OneToMany(mappedBy = "ingreso", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DetalleIngreso> detalles = new ArrayList<>();

    @Column(name = "referencia_uuid", unique = true, updatable = false)
    private UUID referenciaUuid;
}