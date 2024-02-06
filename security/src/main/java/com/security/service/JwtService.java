package com.security.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Slf4j
@Service("JwtService")
public class JwtService {

//    @Value("${secret.key}")
    private static String SECRET_KEY = "123176543234567890087654322345678900987654322345678909876543";
    public String extractUserLogin(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(UserDetailsImpl userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public boolean isTokenValid(String token, @NotNull UserDetailsImpl userDetails) {
        final String login = extractUserLogin(token);
        return login.equals(userDetails.getUsername());
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateToken(Map<String, Object> extraClaims,
                                @NotNull UserDetailsImpl userDetails) {
        var isAdmin = !userDetails.getAuthorities().stream().toList().get(0).getAuthority().equals("ROLE_USER");
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .addClaims(Map.of("isAdmin", isAdmin))
                .signWith(getSingKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public <T> T extractClaim(String token, @NotNull Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private @NotNull Key getSingKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
