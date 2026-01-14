package com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "eventos_integracion")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoIntegracion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tipo_evento", nullable = false)
    private String tipoEvento; // ACTUALIZAR_PADRON, ACTUALIZAR_AGUA

    @Column(name = "payload_json", columnDefinition = "TEXT")
    private String payloadJson;

    @Column(name = "estatus")
    private String estatus; // PENDIENTE, PROCESADO, ERROR

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
}