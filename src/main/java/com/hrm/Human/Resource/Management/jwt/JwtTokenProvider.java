package com.hrm.Human.Resource.Management.jwt;

import com.hrm.Human.Resource.Management.entity.Employee;
import com.hrm.Human.Resource.Management.repositories.EmployeeRepositories;
import com.hrm.Human.Resource.Management.service.MyUserDetails;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {
    @Autowired
    private EmployeeRepositories employeeRepositories;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    private final SecretKey jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512); // Generate a new secret key

    public String generateToken(Authentication authentication) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        List<String> authorities = myUserDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        // Lấy thông tin Employee từ Database để lấy positionName
        Employee employee = employeeRepositories.findByEmployeeCode(myUserDetails.getUsername());
        String positionName = null;
        if (employee != null) {
            positionName = employee.getPosition().getPositionName();
        }

        return Jwts.builder().setSubject(Long.toString(myUserDetails.getUser().getId()))
                .claim("username", myUserDetails.getUsername())
                .claim("roleName", myUserDetails.getUser().getRole().getName())
                .claim("userId", myUserDetails.getUser().getId())
                .claim("email", myUserDetails.getUser().getEmail())
                .claim("authorities", authorities)
                .claim("positionName", positionName) // Thêm thông tin về positionName vào claims
                .setIssuedAt(new Date()).setExpiration(expiryDate).signWith(jwtSecret)
                .compact();
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret) // Use the new secret key
                .parseClaimsJws(token).getBody();

        return Long.parseLong(claims.getSubject());
    }

    public String getUserRoleNameFromJWT(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret)
                .parseClaimsJws(token).getBody();

        return claims.get("roleName", String.class);
    }

    public List<String> getAuthoritiesFromJWT(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret) // Use the new secret key
                .parseClaimsJws(token).getBody();

        return claims.get("authorities", List.class);
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret) // Use the new secret key
                .parseClaimsJws(token).getBody();

        return claims.get("username", String.class);
    }


    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken); // Use the new secret key
            return true;
        } catch (SignatureException ex) {
            System.out.println("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            System.out.println("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            System.out.println("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            System.out.println("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            System.out.println("JWT claims string is empty.");
        }
        return false;
    }
}
