package com.tuxoftware.ms_tesoreria_recaudacion.persistence.repository;


import com.tuxoftware.ms_tesoreria_recaudacion.enums.EstadoSesion;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.SesionCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.UUID;

public interface SesionCajaRepository extends JpaRepository<SesionCaja, UUID> {

    // Validar si la CAJA ya tiene sesión abierta
    boolean existsByCajaIdAndEstatus(UUID cajaId, EstadoSesion estatus);

    // Validar si el USUARIO ya tiene sesión abierta en cualquier caja
    boolean existsByUsuarioIdAndEstatus(String usuarioId, EstadoSesion estatus);
}