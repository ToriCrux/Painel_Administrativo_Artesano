package com.sistema.proposta_service.infra.mensageria.evento;

public record PropostaCriadaDomainEvent(Long propostaId, String authorization) {}
