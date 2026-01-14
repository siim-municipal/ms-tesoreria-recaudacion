package com.tuxoftware.ms_tesoreria_recaudacion.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuxoftware.ms_tesoreria_recaudacion.client.CalculoClient;
import com.tuxoftware.ms_tesoreria_recaudacion.dto.request.IntencionPagoDTO;
import com.tuxoftware.ms_tesoreria_recaudacion.dto.request.SolicitudCalculoRequest;
import com.tuxoftware.ms_tesoreria_recaudacion.dto.response.ResultadoCalculoResponse;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.CatalogoRubros;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.DetalleIngreso;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.EventoIntegracion;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.Ingreso;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.repository.CatalogoRubrosRepository;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.repository.EventoIntegracionRepository;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.repository.IngresoRepository;
import com.tuxoftware.ms_tesoreria_recaudacion.service.PagoService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PagoServiceImpl implements PagoService {

    private final IngresoRepository ingresoRepository;
    private final EventoIntegracionRepository eventoRepository;
    private final CalculoClient calculoClient;
    private final ObjectMapper objectMapper; // Para serializar el evento outbox
    private final CatalogoRubrosRepository rubrosRepository; // Para buscar el rubro del concepto

    @Transactional
    @Override
    public Ingreso procesarPago(IntencionPagoDTO request, String municipioAlias) {
        // 1. IDEMPOTENCIA: Verificar si ya existe este UUID de petición
        Optional<Ingreso> ingresoExistente = ingresoRepository.findByReferenciaUuid(request.paymentRequestUUID());
        if (ingresoExistente.isPresent()) {
            log.info("Idempotencia: Retornando pago ya procesado {}", request.paymentRequestUUID());
            return ingresoExistente.get();
        }

        // 2. VALIDACIÓN: Consultar a ms-calculo (Defensa contra manipulación)
        validarMontoConBackend(request, municipioAlias);

        // 3. PERSISTENCIA: Crear Header y Line
        Ingreso nuevoIngreso = construirIngreso(request);

        Ingreso ingresoGuardado = ingresoRepository.save(nuevoIngreso);

        // 4. OUTBOX: Registrar el efecto secundario (Notificación)
        registrarEventoOutbox(request, ingresoGuardado);

        return ingresoRepository.save(nuevoIngreso);
    }

    private void validarMontoConBackend(IntencionPagoDTO request, String municipio) {
        SolicitudCalculoRequest solicitudCalculo = getSolicitudCalculoRequest(request);

        // Llamada al cliente Feign
        ResultadoCalculoResponse calculoReal = calculoClient.estimar(municipio, solicitudCalculo);

        // Comparación segura de BigDecimal
        if (request.montoTotal().compareTo(calculoReal.getTotal()) != 0) {
            log.error("Fraude detectado o desactualización. Front: {}, Back: {}", request.montoTotal(), calculoReal.getTotal());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El monto enviado no coincide con el cálculo vigente del sistema.");
        }
    }

    private static SolicitudCalculoRequest getSolicitudCalculoRequest(IntencionPagoDTO request) {
        SolicitudCalculoRequest solicitudCalculo = new SolicitudCalculoRequest();

        solicitudCalculo.setClaveConcepto(request.claveConcepto());
        solicitudCalculo.setCantidad(1); // Valor por defecto

        if (request.referenciaId() != null) {
            solicitudCalculo.setReferenciaId(request.referenciaId().toString());
        }

        solicitudCalculo.setAnioFiscal(request.anioFiscal());

        solicitudCalculo.setBaseCalculo(null);

        solicitudCalculo.setParametrosExtra(null);
        return solicitudCalculo;
    }

    private Ingreso construirIngreso(IntencionPagoDTO request) {
        Ingreso ingreso = Ingreso.builder()
                .referenciaUuid(request.paymentRequestUUID())
                .fechaEmision(LocalDateTime.now())
                .sesionCajaId(request.sesionCajaId())
                .contribuyenteId(request.contribuyenteId())
                .totalCobrado(request.montoTotal())
                .estatus("PAGADO")
                .build();

        // Buscar rubro basado en clave concepto (Simplificado)
        // En producción, esto vendría del resultado de ms-calculo o un mapa de configuración
        CatalogoRubros rubro = rubrosRepository.findByClaveConcepto(request.claveConcepto())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Rubro no configurado"));

        DetalleIngreso detalle = DetalleIngreso.builder()
                .ingreso(ingreso)
                .rubro(rubro)
                .monto(request.montoTotal())
                .conceptoDescripcion("Cobro de " + request.claveConcepto())
                .build();

        ingreso.setDetalles(new ArrayList<>(List.of(detalle)));
        return ingreso;
    }

    @SneakyThrows
    private void registrarEventoOutbox(IntencionPagoDTO request, Ingreso ingreso) {
        String tipoEvento = switch (request.claveConcepto()) {
            case "IMP_PREDIAL_URBANO" -> "ACTUALIZAR_PADRON_PREDIAL";
            case "DERECHO_AGUA" -> "ACTUALIZAR_CONTRATO_AGUA";
            case "LIC_CONST" -> "ACTUALIZAR_LICENCIA";
            default -> "GENERICO";
        };

        if (!"GENERICO".equals(tipoEvento)) {
            var payload = new EventoPayload(request.referenciaId(), request.anioFiscal(), ingreso.getId());

            EventoIntegracion evento = EventoIntegracion.builder()
                    .tipoEvento(tipoEvento)
                    .payloadJson(objectMapper.writeValueAsString(payload))
                    .estatus("PENDIENTE")
                    .fechaCreacion(LocalDateTime.now())
                    .build();

            eventoRepository.save(evento);
        }
    }

    // Record auxiliar local
    record EventoPayload(UUID referenciaId, Integer anio, UUID ingresoId) {}
}