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

    // Generic token generator: subject = identifier (email or username)
    public String generateToken(String identifier) {
        return Jwts.builder()
                .setSubject(identifier)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000)) // 7 days
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Overload: generate token with role claim
    public String generateToken(String identifier, String role) {
        return Jwts.builder()
                .setSubject(identifier)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000)) // 7 days
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Generate token for doctor (using email as subject)
    public String generateTokenForDoctor(Doctor doctor) {
        if (doctor == null || doctor.getEmail() == null) {
            throw new IllegalArgumentException("Doctor or doctor email is null");
        }
        return generateToken(doctor.getEmail(), "doctor");
    }

    // Extract identifier (subject) from token
    public String extractIdentifier(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractEmail(String token) {
        return extractIdentifier(token);
    }

    // Extract Patient ID from token (subject = patient email)
    public Long extractPatientId(String token) {
        String email = extractIdentifier(token);
        Patient p = patientRepository.findByEmail(email);
        return p != null ? p.getId() : null;
    }

    // Extract Doctor ID from token (subject = doctor email)
    public Long extractDoctorId(String token) {
        String email = extractIdentifier(token);
        Doctor d = doctorRepository.findByEmail(email);
        return d != null ? d.getId() : null;
    }

    // Validate token against DB
    public boolean validateToken(String token, String userType) {
        try {
            String identifier = extractIdentifier(token);

            if ("admin".equalsIgnoreCase(userType)) {
                Admin admin = adminRepository.findByUsername(identifier);
                return admin != null;
            } else if ("doctor".equalsIgnoreCase(userType)) {
                Doctor doctor = doctorRepository.findByEmail(identifier);
                return doctor != null;
            } else if ("patient".equalsIgnoreCase(userType)) {
                Patient patient = patientRepository.findByEmail(identifier);
                return patient != null;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    // Build signing key from secret
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extract all claims from JWT
    private Claims extractAllClaims(String token) {
        return Jwts.parser()  
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
