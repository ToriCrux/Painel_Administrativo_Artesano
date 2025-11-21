package com.sistema.proposta_service;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class PropostaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PropostaServiceApplication.class, args);
	}

}
