package com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity;

import com.tuxoftware.ms_tesoreria_recaudacion.enums.EstadoSesion;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "operacion_sesiones_caja")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SesionCaja {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caja_id", nullable = false)
    private Caja caja;

    @Column(name = "usuario_id", nullable = false)
    private String usuarioId; // ID del Keycloak/Auth Server

    @Column(name = "fecha_apertura", nullable = false)
    private LocalDateTime fechaApertura;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "saldo_inicial", nullable = false, precision = 18, scale = 2)
    private BigDecimal saldoInicial;

    @Column(name = "total_sistema", precision = 19, scale = 2)
    private BigDecimal totalSistema;

    @Column(name = "total_declarado", precision = 19, scale = 2)
    private BigDecimal totalDeclarado;

    @Column(name = "diferencia", precision = 19, scale = 2)
    private BigDecimal diferencia;

    // JSON o String para guardar cuántos billetes hubo (opcional pero recomendado para auditoría forense)
    @Column(name = "desglose_arqueo_json", columnDefinition = "TEXT")
    private String desgloseArqueoJson;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Enumerated(EnumType.STRING)
    @Column(name = "estatus", nullable = false)
    private EstadoSesion estatus;
}
