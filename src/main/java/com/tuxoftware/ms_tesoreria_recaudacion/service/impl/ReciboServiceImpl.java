package com.tuxoftware.ms_tesoreria_recaudacion.service.impl;

import com.tuxoftware.ms_tesoreria_recaudacion.dto.reporte.ReciboDetalleDTO;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.Ingreso;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.repository.IngresoRepository;
import com.tuxoftware.ms_tesoreria_recaudacion.service.ReciboService;
import com.tuxoftware.ms_tesoreria_recaudacion.utils.NumberToLetterConverter;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReciboServiceImpl implements ReciboService {
    private final IngresoRepository ingresoRepository;

    @Transactional(readOnly = true)
    @Override
    public byte[] generarReciboPdf(UUID ingresoId) {
        // 1. Obtener datos
        Ingreso ingreso = ingresoRepository.findById(ingresoId)
                .orElseThrow(() -> new RuntimeException("Ingreso no encontrado"));

        // 2. Mapear a DTOs (DataSource para la tabla de detalles)
        List<ReciboDetalleDTO> detalles = ingreso.getDetalles().stream()
                .map(d -> new ReciboDetalleDTO(
                        d.getRubro().getCodigoCri(),
                        d.getConceptoDescripcion(),
                        d.getMonto()
                )).toList();

        // 3. Parámetros del Header/Footer
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("FOLIO", ingreso.getReferenciaUuid().toString().substring(0, 8).toUpperCase());
        parameters.put("FECHA_EMISION", java.sql.Timestamp.valueOf(ingreso.getFechaEmision()));
        parameters.put("CONTRIBUYENTE", "PÚBLICO EN GENERAL"); // O buscar nombre real
        parameters.put("TOTAL", ingreso.getTotalCobrado());
        parameters.put("TOTAL_LETRA", NumberToLetterConverter.convert(ingreso.getTotalCobrado()));
        parameters.put("CAJERO", "Caja Principal"); // Obtener del contexto de seguridad

        // QR String: URL pública para validar el documento
        String qrData = String.format("https://tuxtepec.gob.mx/validar?uuid=%s&monto=%s",
                ingreso.getId(), ingreso.getTotalCobrado());
        parameters.put("QR_DATA", qrData);

        // Logo (opcional, stream o ruta)
        // parameters.put("LOGO", ...);

        try {
            // 4. Cargar plantilla .jasper (precompilada es más rápido) o .jrxml
            InputStream reportStream = new ClassPathResource("reports/recibo_pago.jrxml").getInputStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // 5. Llenar reporte
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(detalles);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // 6. Exportar a PDF
            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (Exception e) {
            log.error("Error generando PDF para ingreso {}", ingresoId, e);
            throw new RuntimeException("Error al generar el recibo oficial", e);
        }
    }
}
