package mx.edu.utez.proyectointegrador.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

@Service
public class TokenService {

    private final byte[] secreto;
    private final long horas;

    public TokenService(@Value("${app.token.secret}") String secreto,
                        @Value("${app.token.horas}") long horas) {
        this.secreto = secreto.getBytes(StandardCharsets.UTF_8);
        this.horas = horas;
    }

    public String generar(Long usuarioId) {
        long expira = Instant.now().plusSeconds(horas * 3600).getEpochSecond();
        String carga = usuarioId + ":" + expira;
        String parteCarga = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(carga.getBytes(StandardCharsets.UTF_8));
        return parteCarga + "." + firmar(parteCarga);
    }

    public Long validar(String token) {
        try {
            String[] partes = token.split("\\.");
            if (partes.length != 2) {
                return null;
            }
            byte[] esperada = Base64.getUrlDecoder().decode(firmar(partes[0]));
            byte[] recibida = Base64.getUrlDecoder().decode(partes[1]);
            if (!MessageDigest.isEqual(esperada, recibida)) {
                return null;
            }
            String carga = new String(Base64.getUrlDecoder().decode(partes[0]), StandardCharsets.UTF_8);
            String[] datos = carga.split(":");
            if (Instant.now().getEpochSecond() > Long.parseLong(datos[1])) {
                return null;
            }
            return Long.parseLong(datos[0]);
        } catch (Exception e) {
            return null;
        }
    }

    private String firmar(String texto) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secreto, "HmacSHA256"));
            byte[] firma = mac.doFinal(texto.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(firma);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo firmar el token", e);
        }
    }
}
