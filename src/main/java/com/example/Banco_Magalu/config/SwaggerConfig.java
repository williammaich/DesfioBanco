package com.example.Banco_Magalu.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Banco Magalu")
                        .version("1.0")
                        .description("API para operações bancárias, incluindo criação de contas, saques, depósitos, transferências e auditoria de transações.")
                        .contact(new Contact()
                                .name("Suporte Banco Magalu")
                                .email("suporte@banco_magalu.com")
                                .url("https://github.com/williammaich/DesfioBanco")));
    }
}
