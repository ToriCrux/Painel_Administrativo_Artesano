package com.sistema.proposta_service.dominio;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "tb_cliente",
        indexes = {
                @Index(name = "ix_cliente_cpf_cnpj_hash", columnList = "cpf_cnpj_hash"),
                @Index(name = "ix_cliente_email_hash", columnList = "email_hash"),
                @Index(name = "ix_cliente_nome", columnList = "nome")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Em claro (útil para UI/relatórios)
    @Column(name="nome", nullable = false, length = 120)
    private String nome;

    // =========================
    // ✅ Campos criptografados + hash (PII)
    // =========================

    @Column(name="cpf_cnpj_enc", length = 512)
    private String cpfCnpjEnc;

    @Column(name="cpf_cnpj_hash", length = 64, unique = true)
    private String cpfCnpjHash;

    @Column(name="telefone_enc", length = 512)
    private String telefoneEnc;

    @Column(name="telefone_hash", length = 64)
    private String telefoneHash;

    @Column(name="email_enc", length = 512)
    private String emailEnc;

    @Column(name="email_hash", length = 64)
    private String emailHash;

    @Column(name="cep_enc", length = 512)
    private String cepEnc;

    @Column(name="endereco_enc", length = 512)
    private String enderecoEnc;

    @Column(name="numero_enc", length = 512)
    private String numeroEnc;

    @Column(name="complemento_enc", length = 512)
    private String complementoEnc;

    @Column(name="bairro_enc", length = 512)
    private String bairroEnc;

    // ✅ Pode ficar em claro se quiser filtrar/relatório
    @Column(name="cidade", length = 80)
    private String cidade;

    @Column(name="uf", length = 2)
    private String uf;

    @Column(name="referencia_enc", length = 512)
    private String referenciaEnc;

    // =========================
    // 🔸 Campos legados (fase de migração) — podem existir no banco hoje
    // (depois você remove em outra migration)
    // =========================
    @Column(name="cpf_cnpj", length = 20)
    private String cpfCnpjLegacy;

    @Column(name="telefone", length = 15)
    private String telefoneLegacy;

    @Column(name="email", length = 120)
    private String emailLegacy;

    @Column(name="cep", length = 10)
    private String cepLegacy;

    @Column(name="endereco", length = 150)
    private String enderecoLegacy;

    @Column(name="numero", length = 20)
    private String numeroLegacy;

    @Column(name="complemento", length = 100)
    private String complementoLegacy;

    @Column(name="bairro", length = 80)
    private String bairroLegacy;

    @Column(name="referencia", length = 150)
    private String referenciaLegacy;

    /**
     * Atualiza campos (já criptografados) se vieram preenchidos.
     * Use isso no service após criptografar o "recebido".
     */
    public void aplicarAtualizacoesSeVieramCriptografadas(Cliente recebidoCripto) {
        if (recebidoCripto == null) return;

        if (recebidoCripto.nome != null) this.nome = recebidoCripto.nome;

        if (recebidoCripto.cpfCnpjEnc != null) this.cpfCnpjEnc = recebidoCripto.cpfCnpjEnc;
        if (recebidoCripto.cpfCnpjHash != null) this.cpfCnpjHash = recebidoCripto.cpfCnpjHash;

        if (recebidoCripto.telefoneEnc != null) this.telefoneEnc = recebidoCripto.telefoneEnc;
        if (recebidoCripto.telefoneHash != null) this.telefoneHash = recebidoCripto.telefoneHash;

        if (recebidoCripto.emailEnc != null) this.emailEnc = recebidoCripto.emailEnc;
        if (recebidoCripto.emailHash != null) this.emailHash = recebidoCripto.emailHash;

        if (recebidoCripto.cepEnc != null) this.cepEnc = recebidoCripto.cepEnc;
        if (recebidoCripto.enderecoEnc != null) this.enderecoEnc = recebidoCripto.enderecoEnc;
        if (recebidoCripto.numeroEnc != null) this.numeroEnc = recebidoCripto.numeroEnc;
        if (recebidoCripto.complementoEnc != null) this.complementoEnc = recebidoCripto.complementoEnc;
        if (recebidoCripto.bairroEnc != null) this.bairroEnc = recebidoCripto.bairroEnc;

        if (recebidoCripto.cidade != null) this.cidade = recebidoCripto.cidade;
        if (recebidoCripto.uf != null) this.uf = recebidoCripto.uf;

        if (recebidoCripto.referenciaEnc != null) this.referenciaEnc = recebidoCripto.referenciaEnc;
    }

    /** Cria um novo cliente já no formato criptografado */
    public static Cliente novoCriptografado(Cliente recebidoCripto) {
        return Cliente.builder()
                .nome(recebidoCripto.getNome())
                .cpfCnpjEnc(recebidoCripto.getCpfCnpjEnc())
                .cpfCnpjHash(recebidoCripto.getCpfCnpjHash())
                .telefoneEnc(recebidoCripto.getTelefoneEnc())
                .telefoneHash(recebidoCripto.getTelefoneHash())
                .emailEnc(recebidoCripto.getEmailEnc())
                .emailHash(recebidoCripto.getEmailHash())
                .cepEnc(recebidoCripto.getCepEnc())
                .enderecoEnc(recebidoCripto.getEnderecoEnc())
                .numeroEnc(recebidoCripto.getNumeroEnc())
                .complementoEnc(recebidoCripto.getComplementoEnc())
                .bairroEnc(recebidoCripto.getBairroEnc())
                .cidade(recebidoCripto.getCidade())
                .uf(recebidoCripto.getUf())
                .referenciaEnc(recebidoCripto.getReferenciaEnc())
                .build();
    }
}
