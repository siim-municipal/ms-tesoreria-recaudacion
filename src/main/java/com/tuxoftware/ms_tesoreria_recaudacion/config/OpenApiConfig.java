package com.tuxoftware.ms_tesoreria_recaudacion.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Tesorería y Recaudación")
                        .version("1.0")
                        .description("API para la gestión de tesorería y recaudación de pagos"))
                // 1. Definimos el esquema de seguridad (Bearer JWT)
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                // 2. Aplicamos la seguridad globalmente a todos los endpoints
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"));
    }
}
