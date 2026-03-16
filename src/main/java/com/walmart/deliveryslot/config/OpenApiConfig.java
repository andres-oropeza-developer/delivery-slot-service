package com.walmart.deliveryslot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Delivery Slot Service API")
                        .description("""
                                API REST para reserva de ventanas de despacho a domicilio.
                                
                                Permite a los clientes seleccionar una fecha y horario de entrega
                                según su comuna de destino, respetando la capacidad disponible
                                por zona operacional.
                                
                                **Flujo principal:**
                                1. Buscar comuna por nombre → obtener zona
                                2. Listar ventanas disponibles para la zona y rango de fechas
                                3. Crear orden con dirección de entrega
                                4. Reservar una ventana de despacho
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Walmart Chile — Equipo de Ingeniería")
                                .email("engineering@walmart.cl"))
                        .license(new License()
                                .name("Privado — uso interno")
                                .url("https://www.walmart.cl")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Desarrollo local"),
                        new Server().url("https://api.walmart.cl").description("Producción")
                ));
    }
}
