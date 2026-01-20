package com.sistema.proposta_service.config.crypto;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.crypto")
public class CryptoProperties {

    /**
     * Chave AES (Base64) para criptografia reversível (AES-GCM).
     * Recomendado: 32 bytes (256-bit) em Base64.
     */
    private String aesKeyBase64;

    /**
     * Chave HMAC (Base64) para hash determinístico (busca/unique).
     * Recomendado: 32 bytes em Base64.
     */
    private String hmacKeyBase64;

    public String getAesKeyBase64() {
        return aesKeyBase64;
    }

    public void setAesKeyBase64(String aesKeyBase64) {
        this.aesKeyBase64 = aesKeyBase64;
    }

    public String getHmacKeyBase64() {
        return hmacKeyBase64;
    }

    public void setHmacKeyBase64(String hmacKeyBase64) {
        this.hmacKeyBase64 = hmacKeyBase64;
    }
}
