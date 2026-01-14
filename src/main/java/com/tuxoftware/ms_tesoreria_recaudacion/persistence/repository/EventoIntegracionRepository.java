package com.tuxoftware.ms_tesoreria_recaudacion.persistence.repository;

import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.EventoIntegracion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventoIntegracionRepository extends JpaRepository<EventoIntegracion, UUID> {
    List<EventoIntegracion> findByEstatus(String estatus);
}
