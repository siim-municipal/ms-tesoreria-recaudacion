package com.tuxoftware.ms_tesoreria_recaudacion.service;

import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.CatalogoRubros;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.DetalleIngreso;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.Ingreso;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.repository.IngresoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReciboServiceTest {

    @Mock
    private IngresoRepository ingresoRepository;

    @InjectMocks
    private ReciboService reciboService;

    @Test
    @DisplayName("Debe generar el PDF correctamente cuando el ingreso existe")
    void generarReciboPdf_Exito() {
        // 1. GIVEN (Precondiciones)
        UUID ingresoId = UUID.randomUUID();
        UUID contribuyenteId = UUID.randomUUID();
        UUID sesionId = UUID.randomUUID();

        // Creamos los datos simulados (Mock Data)
        // Necesitamos 'Rubro' para evitar NPE en el mapeo
        CatalogoRubros rubroMock = CatalogoRubros.builder()
                .codigoCri("1.2.1")
                .descripcion("Impuesto Predial")
                .build();

        // Necesitamos 'Detalle'
        DetalleIngreso detalleMock = DetalleIngreso.builder()
                .rubro(rubroMock)
                .monto(new BigDecimal("1500.00"))
                .conceptoDescripcion("Pago Predial 2025")
                .build();

        // Necesitamos el 'Ingreso' padre
        Ingreso ingresoMock = Ingreso.builder()
                .id(ingresoId)
                .referenciaUuid(UUID.randomUUID()) // Para el Folio
                .fechaEmision(LocalDateTime.now())
                .contribuyenteId(contribuyenteId)
                .sesionCajaId(sesionId)
                .totalCobrado(new BigDecimal("1500.00"))
                .estatus("PAGADO")
                .detalles(List.of(detalleMock)) // Lista con 1 elemento
                .build();

        // Configuramos el comportamiento del repositorio
        when(ingresoRepository.findById(ingresoId)).thenReturn(Optional.of(ingresoMock));

        // 2. WHEN (Ejecución)
        // NOTA: Para que esto no falle, el archivo 'reports/recibo_pago.jrxml'
        // debe existir en 'src/main/resources' o 'src/test/resources'.
        byte[] pdfBytes = reciboService.generarReciboPdf(ingresoId);

        // 3. THEN (Verificaciones)
        assertNotNull(pdfBytes, "El arreglo de bytes no debe ser nulo");
        assertTrue(pdfBytes.length > 0, "El PDF debe tener contenido");

        // Validación de la firma del archivo PDF (Magic Numbers)
        // Los archivos PDF siempre inician con bytes: 0x25 (% - 37) y 0x50 (P - 80)
        assertEquals(0x25, pdfBytes[0], "El archivo debe iniciar con %");
        assertEquals(0x50, pdfBytes[1], "El archivo debe iniciar con P");

        // Verificamos que se llamó al repositorio
        verify(ingresoRepository, times(1)).findById(ingresoId);
    }

    @Test
    @DisplayName("Debe lanzar excepción si el ingreso no existe")
    void generarReciboPdf_NoEncontrado() {
        // 1. GIVEN
        UUID ingresoId = UUID.randomUUID();
        when(ingresoRepository.findById(ingresoId)).thenReturn(Optional.empty());

        // 2. WHEN & THEN
        Exception exception = assertThrows(RuntimeException.class, () -> {
            reciboService.generarReciboPdf(ingresoId);
        });

        assertEquals("Ingreso no encontrado", exception.getMessage());
    }
}
