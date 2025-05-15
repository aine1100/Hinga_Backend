package com.Hinga.farmMis.services;

import com.Hinga.farmMis.Constants.UserRoles;
import com.Hinga.farmMis.Model.Users;
import com.Hinga.farmMis.utils.Address;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${secret.key}")
    private String SECRET; // Base64 encoded secret key

    // Set to store invalidated tokens
    private final Set<String> invalidatedTokens = ConcurrentHashMap.newKeySet();

    public JwtService() {}

    // Modified to include all user details
    public String generateToken(Users user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());
        claims.put("phoneNumber", user.getPhoneNumber());
        claims.put("address", user.getAddress());
        claims.put("role", user.getUserRole());
        
        // Debug logging
        System.out.println("Generating token with claims: " + claims);
        System.out.println("User details - First Name: " + user.getFirstName() + 
                          ", Last Name: " + user.getLastName() + 
                          ", Phone: " + user.getPhoneNumber());
        
        return createToken(claims, user.getId());
    }

    private String createToken(Map<String, Object> claims, Long id) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(id.toString()) // ID as the subject
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // 30 minutes expiration
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extract specific claims
    public long extractId(String token) {
        return Long.parseLong(extractClaim(token, Claims::getSubject));
    }

    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    public String extractFirstName(String token) {
        return extractClaim(token, claims -> claims.get("firstName", String.class));
    }

    public String extractLastName(String token) {
        return extractClaim(token, claims -> claims.get("lastName", String.class));
    }

    public String extractPhoneNumber(String token) {
        return extractClaim(token, claims -> claims.get("phoneNumber", String.class));
    }

    public Address extractAddress(String token) {
        return extractClaim(token, claims -> claims.get("address", Address.class));
    }

    public UserRoles extractRole(String token) {
        String roleStr = extractClaim(token, claims -> claims.get("role", String.class));
        return UserRoles.valueOf(roleStr);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println("Successfully extracted claims: " + claims);
            return claims;
        } catch (Exception e) {
            System.out.println("Error extracting claims: " + e.getMessage());
            throw e;
        }
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String id = String.valueOf(extractId(token)); // ID as username
        return (id.equals(userDetails.getUsername()) && !isTokenExpired(token) && !isTokenInvalidated(token));
    }

    // Invalidate token
    public void invalidateToken(String token) {
        invalidatedTokens.add(token);
    }

    // Check if token is invalidated
    public boolean isTokenInvalidated(String token) {
        return invalidatedTokens.contains(token);
    }

    // Validate token including invalidation check
    public boolean isTokenValid(String token) {
        try {
            if (isTokenInvalidated(token)) {
                return false;
            }
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}