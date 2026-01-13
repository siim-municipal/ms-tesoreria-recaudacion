package com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "cat_cajas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Caja {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 200)
    private String ubicacion;

    @Column(name = "ip_asignada", length = 45) // Soporte IPv6
    private String ipAsignada;

    @Column(name = "activa")
    private Boolean activa;
}
