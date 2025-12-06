package com.sistema.autenticacao_service.infra.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "user_logs")
public class UserLog {

    @Id
    private String id;
    private String usuario;
    private String acao;
    private LocalDateTime dataHora;

    public UserLog(String usuario, String acao) {
        this.usuario = usuario;
        this.acao = acao;
        this.dataHora = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getUsuario() { return usuario; }
    public String getAcao() { return acao; }
    public LocalDateTime getDataHora() { return dataHora; }
}
