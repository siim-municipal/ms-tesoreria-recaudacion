package com.tuxoftware.ms_tesoreria_recaudacion.client;

import com.tuxoftware.ms_tesoreria_recaudacion.config.CalculoFeignConfig;
import com.tuxoftware.ms_tesoreria_recaudacion.dto.request.SolicitudCalculoRequest;
import com.tuxoftware.ms_tesoreria_recaudacion.dto.response.ResultadoCalculoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "ms-calculo-impuestos",
        configuration = CalculoFeignConfig.class
)
public interface CalculoClient {
    @PostMapping("/api/v1/calculos/estimar")
    ResultadoCalculoResponse estimar(@RequestHeader("X-Municipio-Alias") String municipio,
                                     @RequestBody SolicitudCalculoRequest solicitud);
}