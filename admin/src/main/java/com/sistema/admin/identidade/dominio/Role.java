package com.sistema.admin.identidade.dominio;

import jakarta.persistence.*;

// Código que representa a tabela do banco de dados
@Entity
@Table(name = "tb_role")
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String nome; // ex.: "ADMIN", "USUARIO"

    // getters/setters
}
