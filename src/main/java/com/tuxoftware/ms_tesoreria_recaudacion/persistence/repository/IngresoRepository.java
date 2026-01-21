package com.tuxoftware.ms_tesoreria_recaudacion.persistence.repository;

import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.Ingreso;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.projection.ResumenIngresoDiarioView;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.projection.TotalesComparativosView;
import feign.Param;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.HibernateHints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IngresoRepository extends JpaRepository<Ingreso, UUID> {

    Optional<Ingreso> findByReferenciaUuid(UUID referenciaUuid);

    @Query("SELECT COALESCE(SUM(i.totalCobrado), 0) FROM Ingreso i WHERE i.sesionCajaId = :sesionId AND i.estatus = 'PAGADO'")
    BigDecimal sumarTotalPorSesion(@Param("sesionId") UUID sesionId);

    // REPORTE 1: Ingresos Diarios (Agrupado)
    @Query("""
        SELECT
            r.codigoCri as codigoCri,
            r.descripcion as rubroDescripcion,
            i.metodoPago as metodoPago,
            SUM(d.monto) as totalRecaudado,
            COUNT(i.id) as cantidadTransacciones
        FROM Ingreso i
        JOIN i.detalles d
        JOIN d.rubro r
        WHERE i.fechaEmision BETWEEN :inicio AND :fin
          AND i.estatus = 'PAGADO'
        GROUP BY r.codigoCri, r.descripcion, i.metodoPago
        ORDER BY r.codigoCri ASC
    """)
    List<ResumenIngresoDiarioView> obtenerResumenDiario(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    // REPORTE 2: Comparativo (Cálculo en BD)
    // Retorna un array Object[]: [TotalRango1, TotalRango2]
    @Query("""
        SELECT
            COALESCE(SUM(CASE
                WHEN i.fechaEmision BETWEEN :inicioActual AND :finActual
                THEN i.totalCobrado ELSE 0 END), 0) AS totalActual,
            COALESCE(SUM(CASE
                WHEN i.fechaEmision BETWEEN :inicioAnterior AND :finAnterior
                THEN i.totalCobrado ELSE 0 END), 0) AS totalAnterior
        FROM Ingreso i
        WHERE i.estatus = 'PAGADO'
            AND (i.fechaEmision BETWEEN :inicioActual AND :finActual\s
                       OR i.fechaEmision BETWEEN :inicioAnterior AND :finAnterior)
    """)
    @QueryHints(value = {
            @QueryHint(name = HibernateHints.HINT_READ_ONLY, value = "true"),
            @QueryHint(name = HibernateHints.HINT_FETCH_SIZE, value = "50") // Traer en lotes
    })
    TotalesComparativosView obtenerTotalesComparativos(
            @Param("inicioActual") LocalDateTime inicioActual,
            @Param("finActual") LocalDateTime finActual,
            @Param("inicioAnterior") LocalDateTime inicioAnterior,
            @Param("finAnterior") LocalDateTime finAnterior
    );

    List<Ingreso> findByPredioIdOrderByFechaEmisionDesc(UUID predioId);
}
