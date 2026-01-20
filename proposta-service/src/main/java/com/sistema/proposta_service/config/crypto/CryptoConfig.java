package com.sistema.proposta_service.config.crypto;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CryptoProperties.class)
public class CryptoConfig {
}
