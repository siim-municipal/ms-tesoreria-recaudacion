package com.tuxoftware.ms_tesoreria_recaudacion.repository;

import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.CatalogoRubros;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.DetalleIngreso;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.Ingreso;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.repository.CatalogoRubrosRepository;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.repository.IngresoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class IngresoRepositoryTest {

    @Autowired
    private IngresoRepository ingresoRepository;

    @Autowired
    private CatalogoRubrosRepository rubrosRepository;

    @Test
    @DisplayName("Debe registrar Ingreso y sus Detalles vinculados al CRI correctamente")
    void debeRegistrarIngresoConDetalleCRI() {
        // 1. GIVEN: Un Rubro existente (del Seed de Liquibase)
        // Nota: Si usas H2 en memoria sin cargar liquibase, descomenta la línea de abajo para crear el rubro al vuelo.
        // rubrosRepository.save(new CatalogoRubros("1.2.1", "Predial Mock", "4.1.1", 3));

        String codigoCri = "1.2.1"; // Impuesto Predial según tu Seed
        CatalogoRubros rubroPredial = rubrosRepository.findById(codigoCri)
                .orElseThrow(() -> new IllegalStateException("El Seed de Liquibase no cargó el rubro " + codigoCri));

        // 2. WHEN: Creamos el Header (Ingreso)
        var ingreso = Ingreso.builder()
                .fechaEmision(LocalDateTime.now())
                .sesionCajaId(UUID.randomUUID()) // ID simulado de sesión
                .contribuyenteId(UUID.randomUUID()) // ID simulado de contribuyente
                .totalCobrado(new BigDecimal("1500.50"))
                .estatus("PAGADO")
                .build();

        // Creamos el Line (Detalle) vinculado
        var detalle = DetalleIngreso.builder()
                .ingreso(ingreso) // Vinculación bidireccional (necesaria para JPA)
                .rubro(rubroPredial)
                .monto(new BigDecimal("1500.50"))
                .conceptoDescripcion("Pago Anual Predial 2025")
                .build();

        // Agregamos a la lista del padre (CascadeType.ALL se encargará de guardar el detalle)
        ingreso.getDetalles().add(detalle);

        // Guardamos solo el padre
        ingresoRepository.save(ingreso);

        // 3. THEN: Verificamos la persistencia y recuperación
        // Limpiamos caché de hibernate para forzar SELECT real a BD
        ingresoRepository.flush();

        var ingresoGuardado = ingresoRepository.findById(ingreso.getId()).orElseThrow();

        // Validaciones Header
        assertNotNull(ingresoGuardado.getId());
        assertEquals("PAGADO", ingresoGuardado.getEstatus());
        assertEquals(0, new BigDecimal("1500.50").compareTo(ingresoGuardado.getTotalCobrado()));

        // Validaciones Detalle (Lines)
        assertFalse(ingresoGuardado.getDetalles().isEmpty(), "La lista de detalles no debería estar vacía");
        assertEquals(1, ingresoGuardado.getDetalles().size());

        var detalleGuardado = ingresoGuardado.getDetalles().getFirst(); // Java 21 feature
        assertEquals("1.2.1", detalleGuardado.getRubro().getCodigoCri(), "El detalle debe apuntar al CRI correcto");
        assertEquals("Pago Anual Predial 2025", detalleGuardado.getConceptoDescripcion());
        assertEquals(0, new BigDecimal("1500.50").compareTo(detalleGuardado.getMonto()));
    }
}