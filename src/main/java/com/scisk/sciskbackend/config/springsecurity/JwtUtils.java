package com.scisk.sciskbackend.config.springsecurity;

import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Log4j2
public class JwtUtils {

    @Value("${scisk.app.jwtSecret}")
    private String jwtSecret;

    @Value("${scisk.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(UserDetailsImpl userPrincipal) {
        return generateTokenFromUsername(
                (List<? extends GrantedAuthority>) userPrincipal.getAuthorities(),
                userPrincipal.getFirstname(),
                userPrincipal.getLastname(),
                userPrincipal.getEmail()
        );
    }

    public String generateTokenFromUsername(List<? extends GrantedAuthority> authorities, String firstname, String lastname, String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("authorities", authorities)
                .claim("firstname", firstname)
                .claim("lastname", lastname)
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) throws ExpiredJwtException {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
