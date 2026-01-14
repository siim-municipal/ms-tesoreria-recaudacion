package com.tuxoftware.ms_tesoreria_recaudacion.worker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuxoftware.ms_tesoreria_recaudacion.client.PadronClient;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.EventoIntegracion;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.repository.EventoIntegracionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class IntegracionEventsWorker {

    private final EventoIntegracionRepository eventoRepository;
    private final PadronClient padronClient;
    private final ObjectMapper objectMapper;

    // Se ejecuta cada 10 segundos (10000 ms)
    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void procesarEventosPendientes() {
        // 1. Buscar eventos PENDIENTES
        List<EventoIntegracion> pendientes = eventoRepository.findByEstatus("PENDIENTE");

        if (pendientes.isEmpty()) return;

        log.info("Worker: Procesando {} eventos de integración pendientes...", pendientes.size());

        for (EventoIntegracion evento : pendientes) {
            procesarEventoIndividual(evento);
        }
    }

    private void procesarEventoIndividual(EventoIntegracion evento) {
        try {
            JsonNode payload = objectMapper.readTree(evento.getPayloadJson());
            String municipio = "TUXTEPEC"; // Idealmente vendría en el payload también

            switch (evento.getTipoEvento()) {
                case "ACTUALIZAR_PADRON_PREDIAL" -> {
                    UUID predioId = UUID.fromString(payload.get("referenciaId").asText());
                    Integer anio = payload.get("anio").asInt();

                    // Llamada al microservicio externo
                    padronClient.actualizarUltimoPago(municipio, predioId, anio);
                    log.info("Padrón actualizado para predio {}", predioId);
                }
                case "ACTUALIZAR_CONTRATO_AGUA" -> {
                    // Lógica para llamar a ms-agua
                    log.info("Lógica de agua pendiente de implementar");
                }
                default -> log.warn("Tipo de evento desconocido: {}", evento.getTipoEvento());
            }

            // ÉXITO
            evento.setEstatus("PROCESADO");

        } catch (Exception e) {
            log.error("Error procesando evento {}: {}", evento.getId(), e.getMessage());
            // Manejo de reintentos simple: Lo marcamos ERROR para que no bloquee el loop
            // En un sistema real, usarías un contador de 'reintentos' antes de marcar ERROR
            evento.setEstatus("ERROR");
        }

        // Guardamos el cambio de estatus
        eventoRepository.save(evento);
    }
}
