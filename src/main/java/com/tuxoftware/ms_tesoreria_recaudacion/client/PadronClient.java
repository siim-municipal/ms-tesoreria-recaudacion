package com.tuxoftware.ms_tesoreria_recaudacion.client;

import com.tuxoftware.ms_tesoreria_recaudacion.config.CalculoFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@FeignClient(
        name = "ms-padron-unico",
        configuration = CalculoFeignConfig.class
)
public interface PadronClient {

    @PatchMapping("/api/v1/predios/{predioId}/historial-pagos")
    void actualizarUltimoPago(
            @RequestHeader("X-Municipio-Alias") String municipio,
            @PathVariable("predioId") UUID predioId,
            @RequestParam("anioPagado") Integer anioPagado
    );

    @PatchMapping("/api/v1/licencias/{licenciaId}/renovacion")
    void actualizarLicencia(
            @RequestHeader("X-Municipio-Alias") String municipio,
            @PathVariable("licenciaId") UUID licenciaId,
            @RequestParam("anioFiscal") Integer anioFiscal
    );
}