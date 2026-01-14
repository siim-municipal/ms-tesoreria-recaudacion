package com.tuxoftware.ms_tesoreria_recaudacion.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuxoftware.ms_tesoreria_recaudacion.dto.request.AperturaCajaRequest;
import com.tuxoftware.ms_tesoreria_recaudacion.dto.request.CorteCajaRequest;
import com.tuxoftware.ms_tesoreria_recaudacion.enums.EstadoSesion;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.Caja;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.entity.SesionCaja;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.repository.CajaRepository;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.repository.IngresoRepository;
import com.tuxoftware.ms_tesoreria_recaudacion.persistence.repository.SesionCajaRepository;
import com.tuxoftware.ms_tesoreria_recaudacion.service.CajaService;
import com.tuxoftware.ms_tesoreria_recaudacion.service.ReciboService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CajaServiceImpl implements CajaService {

    private final CajaRepository cajaRepository;
    private final SesionCajaRepository sesionRepository;
    private final ObjectMapper objectMapper;
    private final IngresoRepository ingresoRepository;
    private final ReciboService reciboService;

    @Transactional
    @Override
    public UUID abrirCaja(AperturaCajaRequest request, String usuarioId) {
        log.info("Intento de apertura de caja {} por usuario {}", request.cajaId(), usuarioId);

        // 1. Validar existencia de caja
        Caja caja = cajaRepository.findById(request.cajaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Caja no encontrada"));

        if (Boolean.FALSE.equals(caja.getActiva())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La caja está inhabilitada administrativamente");
        }

        // 2. Validar que la CAJA no esté ocupada
        if (sesionRepository.existsByCajaIdAndEstatus(request.cajaId(), EstadoSesion.ABIERTA)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta caja ya tiene una sesión abierta por otro operador.");
        }

        // 3. Validar que el USUARIO no tenga otra sesión activa
        if (sesionRepository.existsByUsuarioIdAndEstatus(usuarioId, EstadoSesion.ABIERTA)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya tiene una sesión abierta en otra caja.");
        }

        // 4. Crear sesión
        SesionCaja nuevaSesion = SesionCaja.builder()
                .caja(caja)
                .usuarioId(usuarioId)
                .fechaApertura(LocalDateTime.now())
                .saldoInicial(request.saldoInicial())
                .estatus(EstadoSesion.ABIERTA)
                .build();

        sesionRepository.save(nuevaSesion);
        log.info("Sesión {} abierta exitosamente.", nuevaSesion.getId());

        return nuevaSesion.getId();
    }

    @Transactional
    @Override
    public void cerrarSesion(UUID sesionId, String usuarioId) {
        SesionCaja sesion = sesionRepository.findById(sesionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));

        if (!sesion.getUsuarioId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes cerrar una sesión que no es tuya");
        }

        if (sesion.getEstatus() != EstadoSesion.ABIERTA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La sesión no está abierta");
        }

        sesion.setEstatus(EstadoSesion.CERRADA); // O ARQUEO_PENDIENTE según flujo
        sesion.setFechaCierre(LocalDateTime.now());
        sesionRepository.save(sesion);
    }

    @Override
    @Transactional
    public byte[] realizarCorteCaja(CorteCajaRequest request) {
        // 1. Recuperar Sesión y Validar Estado
        SesionCaja sesion = sesionRepository.findById(request.sesionCajaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));

        if (sesion.getEstatus() != EstadoSesion.ABIERTA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La sesión ya no está abierta.");
        }

        // 2. Calcular Total Sistema (Lo que dice la BD)
        BigDecimal totalSistema = ingresoRepository.sumarTotalPorSesion(sesion.getId());

        // 3. Calcular Total Declarado (Lo que contó el cajero)
        BigDecimal totalDeclarado = request.desgloseEfectivo().entrySet().stream()
                .map(entry -> entry.getKey().multiply(BigDecimal.valueOf(entry.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Calcular Diferencia
        BigDecimal diferencia = totalDeclarado.subtract(totalSistema);

        // 5. Actualizar y Cerrar Sesión
        sesion.setTotalSistema(totalSistema);
        sesion.setTotalDeclarado(totalDeclarado);
        sesion.setDiferencia(diferencia);
        sesion.setFechaCierre(LocalDateTime.now());
        sesion.setEstatus(EstadoSesion.CERRADA);
        sesion.setObservaciones(request.observaciones());

        try {
            sesion.setDesgloseArqueoJson(objectMapper.writeValueAsString(request.desgloseEfectivo()));
        } catch (Exception e) {
            log.warn("No se pudo serializar el desglose de efectivo", e);
        }

        sesionRepository.save(sesion);
        log.info("Corte de caja realizado. Sesión: {}, Diferencia: {}", sesion.getId(), diferencia);

        // 6. Generar PDF del Corte
        return reciboService.generarPdfCorte(sesion);
    }


}