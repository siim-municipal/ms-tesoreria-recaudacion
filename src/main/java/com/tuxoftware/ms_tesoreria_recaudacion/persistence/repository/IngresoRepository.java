package com.tuxoftware.ms_tesoreria_recaudacion.persistence.repository;

import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.Ingreso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IngresoRepository extends JpaRepository<Ingreso, UUID> {
    Optional<Ingreso> findByReferenciaUuid(UUID referenciaUuid);
}
