package com.sistema.admin.auth.dominio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Código que representa a tabela do banco de dados
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_role")
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String nome; // ex.: "ADMIN", "USUARIO"


}
