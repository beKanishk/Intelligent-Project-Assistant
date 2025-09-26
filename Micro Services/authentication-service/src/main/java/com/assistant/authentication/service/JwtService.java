package com.assistant.authentication.service;


import com.assistant.authentication.client.UserClient;
import com.assistant.authentication.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Slf4j
@Component
public class JwtService {


    public static final String SECRET = "4367286B59703373317639792F423F4528482B4D6251695468576D5A71347437";

    @Autowired
    private UserClient userClient;

//    @Autowired
//    private UserRepository userRepository;

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);
            return true; // token is valid
        } catch (Exception e) {
            return false; // token invalid / expired / tampered
        }
    }



    public String generateToken(String email) throws Exception {
        try{
            Map<String, Object> claims = new HashMap<>();
            User user = userClient.getUserByEmail(email);
            log.info("User" + user.toString());
            claims.put("roles", user.getRoles());
            claims.put("userId", user.getId());
            return createToken(claims, email);
        }
        catch (Exception e){
            throw new Exception("User not found");
        }

    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public List<String> extractRoles(String token) {
        return extractAllClaims(token).get("roles", List.class);
    }

    private String createToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }
}
