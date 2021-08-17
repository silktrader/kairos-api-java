package eu.silktrader.kairos.auth;

import eu.silktrader.kairos.exception.KairosException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

import static io.jsonwebtoken.Jwts.parserBuilder;

@Service
public class JwtService {

    private KeyStore keyStore;
    private final char[] secret = "c#isbetter".toCharArray();

    @PostConstruct
    public void init() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
            keyStore = KeyStore.getInstance("JKS");
            var resourceAsStream = getClass().getResourceAsStream("/silktrader.jks");
            keyStore.load(resourceAsStream, secret);
    }


    public String generateToken(Authentication authentication) throws GeneralSecurityException {
        var principal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .signWith(getPrivateKey())
                .compact();
    }

    private PrivateKey getPrivateKey() throws GeneralSecurityException {
        // tk hard code char array
       return (PrivateKey) keyStore.getKey("silktrader", secret);

    }

    // tk make it static?
    private PublicKey getPublicKey() {
        try {
            return keyStore.getCertificate("silktrader").getPublicKey();
        } catch (KeyStoreException e) {
            throw new KairosException("Exception while retrieving public key from keystore", e);
        }
    }

    public boolean validateToken(String jwt) {
        parserBuilder().setSigningKey(getPublicKey()).build().parseClaimsJws(jwt);
        return true;    // tk???
    }

    public String getUserNameFromJwt(String jwt) {
        return parserBuilder()
                .setSigningKey(getPublicKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }

}
