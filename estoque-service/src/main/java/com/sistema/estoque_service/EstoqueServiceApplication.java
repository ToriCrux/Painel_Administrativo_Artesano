package com.sistema.estoque_service;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class EstoqueServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EstoqueServiceApplication.class, args);
	}

}
