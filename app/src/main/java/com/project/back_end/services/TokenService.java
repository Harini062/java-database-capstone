package com.project.back_end.services;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String secret;

    public TokenService(AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }



    public String generateToken(Long userId, String role) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // subject = ID now
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000)) // 7 days
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
    }

 

    private Claims extractAllClaims(String token) {
        return Jwts.parser()   
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long extractUserId(String token) {
        return Long.valueOf(extractAllClaims(token).getSubject());
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public String extractIdentifier(String token) {
        return extractAllClaims(token).getSubject(); // may be ID or email depending on your token
    }

 

    public Long extractPatientId(String token) {
        return extractUserId(token);
    }

    public Long extractDoctorId(String token) {
        return extractUserId(token);
    }

    

    public boolean validateToken(String token, String userType) {
        try {
            Long userId = extractUserId(token);
            String role = extractRole(token);

            if ("admin".equalsIgnoreCase(userType) && "admin".equalsIgnoreCase(role)) {
                return adminRepository.findById(userId).isPresent();
            } else if ("doctor".equalsIgnoreCase(userType) && "doctor".equalsIgnoreCase(role)) {
                return doctorRepository.findById(userId).isPresent();
            } else if ("patient".equalsIgnoreCase(userType) && "patient".equalsIgnoreCase(role)) {
                return patientRepository.findById(userId).isPresent();
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
  

    private byte[] getSigningKey() {
        return secret.getBytes(StandardCharsets.UTF_8);
    }
}