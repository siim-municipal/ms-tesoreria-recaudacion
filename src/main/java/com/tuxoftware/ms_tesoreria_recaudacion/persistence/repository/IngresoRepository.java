package com.tuxoftware.ms_tesoreria_recaudacion.persistence.repository;

import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.Ingreso;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface IngresoRepository extends JpaRepository<Ingreso, UUID> {

    Optional<Ingreso> findByReferenciaUuid(UUID referenciaUuid);

    @Query("SELECT COALESCE(SUM(i.totalCobrado), 0) FROM Ingreso i WHERE i.sesionCajaId = :sesionId AND i.estatus = 'PAGADO'")
    BigDecimal sumarTotalPorSesion(@Param("sesionId") UUID sesionId);
}
