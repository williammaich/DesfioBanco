package com.example.Banco_Magalu;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.Banco_Magalu")
@OpenAPIDefinition(info = @Info(title = "Banco Magalu API", version = "1.0.0", description = "API do Banco Magalu"))
public class BancoMagaluApplication {

	public static void main(String[] args) {
		SpringApplication.run(BancoMagaluApplication.class, args);
	}

}
