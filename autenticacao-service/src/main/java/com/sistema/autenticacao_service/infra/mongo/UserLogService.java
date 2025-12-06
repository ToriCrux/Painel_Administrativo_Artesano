package com.sistema.autenticacao_service.infra.mongo;

import org.springframework.stereotype.Service;

@Service
public class UserLogService {

    private final UserLogRepository repository;

    public UserLogService(UserLogRepository repository) {
        this.repository = repository;
    }

    public void registrarAcao(String usuario, String acao) {
        try {
            repository.save(new UserLog(usuario, acao));
            System.out.printf("🟢 Log salvo no MongoDB: [%s] %s%n", acao, usuario);
        } catch (Exception e) {
            System.err.println("⚠️ Falha ao registrar log no MongoDB: " + e.getMessage());
        }
    }
}
