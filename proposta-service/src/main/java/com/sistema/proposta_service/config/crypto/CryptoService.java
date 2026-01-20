package com.sistema.proposta_service.config.crypto;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CryptoService {

    private static final String ENC_PREFIX = "v1:";
    private static final String AES_ALGO = "AES";
    private static final String AES_TRANSFORM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_BITS = 128;
    private static final int IV_BYTES = 12;

    private final SecretKey aesKey;
    private final SecretKey hmacKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public CryptoService(CryptoProperties props) {
        if (!StringUtils.hasText(props.getAesKeyBase64()) || !StringUtils.hasText(props.getHmacKeyBase64())) {
            throw new IllegalStateException("app.crypto.aes-key-base64 e app.crypto.hmac-key-base64 são obrigatórios.");
        }

        byte[] aesBytes = Base64.getDecoder().decode(props.getAesKeyBase64());
        byte[] hmacBytes = Base64.getDecoder().decode(props.getHmacKeyBase64());

        this.aesKey = new SecretKeySpec(aesBytes, AES_ALGO);
        this.hmacKey = new SecretKeySpec(hmacBytes, "HmacSHA256");
    }

    /** Normaliza para hashing (cpf/email/telefone) */
    public String normalize(String value) {
        if (!StringUtils.hasText(value)) return null;
        String v = value.trim().toLowerCase();

        // Normalizações úteis:
        // cpf/cnpj/telefone: remove não-dígitos
        // email: mantém @ e letras, mas trim/lower já ajuda
        // Aqui vamos usar uma regra simples:
        // - se for "majoritariamente" dígito -> remove não-dígitos
        int digits = 0;
        for (char c : v.toCharArray()) if (Character.isDigit(c)) digits++;
        if (digits >= Math.max(6, v.length() / 2)) {
            v = v.replaceAll("\\D+", "");
        }
        return v.isBlank() ? null : v;
    }

    /** Hash determinístico para busca/unique */
    public String hmacSha256Hex(String normalizedValue) {
        if (!StringUtils.hasText(normalizedValue)) return null;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(hmacKey);
            byte[] out = mac.doFinal(normalizedValue.getBytes(StandardCharsets.UTF_8));
            return toHex(out);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao gerar HMAC SHA-256", e);
        }
    }

    /** Criptografa texto (AES-GCM) -> "v1:BASE64(iv+cipher)" */
    public String encrypt(String plain) {
        if (!StringUtils.hasText(plain)) return null;
        try {
            byte[] iv = new byte[IV_BYTES];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(AES_TRANSFORM);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_BITS, iv));

            byte[] cipherBytes = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[iv.length + cipherBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherBytes, 0, combined, iv.length, cipherBytes.length);

            return ENC_PREFIX + Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao criptografar", e);
        }
    }

    /**
     * Descriptografa se estiver no formato "v1:...".
     * Se NÃO estiver, retorna como está (compat com legado / fase de migração).
     */
    public String decryptOrPassThrough(String value) {
        if (!StringUtils.hasText(value)) return null;
        if (!value.startsWith(ENC_PREFIX)) return value; // legado
        return decrypt(value);
    }

    /** Descriptografa "v1:..." */
    public String decrypt(String encrypted) {
        if (!StringUtils.hasText(encrypted)) return null;
        if (!encrypted.startsWith(ENC_PREFIX)) {
            throw new IllegalArgumentException("Formato inválido (esperado prefixo v1:)");
        }

        try {
            String payload = encrypted.substring(ENC_PREFIX.length());
            byte[] combined = Base64.getDecoder().decode(payload);

            byte[] iv = new byte[IV_BYTES];
            byte[] cipherBytes = new byte[combined.length - IV_BYTES];

            System.arraycopy(combined, 0, iv, 0, IV_BYTES);
            System.arraycopy(combined, IV_BYTES, cipherBytes, 0, cipherBytes.length);

            Cipher cipher = Cipher.getInstance(AES_TRANSFORM);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_BITS, iv));

            byte[] plain = cipher.doFinal(cipherBytes);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao descriptografar", e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
