package com.project.back_end.services;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final TokenService tokenService;

    public AdminService(AdminRepository adminRepository,DoctorRepository doctorRepository,PatientRepository patientRepository,TokenService tokenService) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.tokenService = tokenService;
    }

    // Create Admin
    public int createAdmin(Admin admin) {
        try {
            adminRepository.save(admin);
            return 1;
        } catch (Exception ex) {
            return 0;
        }
    }

    // Admin Login -> issue JWT
    public ResponseEntity<Map<String, String>> login(String username, String password) {
        Map<String, String> body = new HashMap<>();
        try {
            Admin admin = adminRepository.findByUsername(username);
            if (admin == null || !password.equals(admin.getPassword())) {
                body.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
            }

            String token = tokenService.generateToken(admin.getId(), "admin");
            body.put("token", token);
            body.put("message", "Login successful");
            return ResponseEntity.ok(body);

        } catch (Exception ex) {
            body.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    // Validate Admin Token
    public boolean validateAdminToken(String token) {
        return tokenService.validateToken(token, "admin");
    }

    // Get Admin details from token
    public Admin getAdminDetails(String token) {
        String username = tokenService.extractIdentifier(token);
        return adminRepository.findByUsername(username);
    }


    public ResponseEntity<Map<String, Object>> getAllDoctors() {
        Map<String, Object> body = new HashMap<>();
        try {
            List<Doctor> doctors = doctorRepository.findAll();
            body.put("doctors", doctors);
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            body.put("message", "Failed to fetch doctors");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    public ResponseEntity<Map<String, Object>> addDoctor(Doctor doctor) {
        Map<String, Object> body = new HashMap<>();
        try {
            doctorRepository.save(doctor);
            body.put("doctor", doctor);
            body.put("message", "Doctor created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(body);
        } catch (Exception ex) {
            body.put("message", "Failed to create doctor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    public ResponseEntity<Map<String, Object>> deleteDoctor(Long doctorId) {
        Map<String, Object> body = new HashMap<>();
        try {
            Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
            if (doctorOpt.isEmpty()) {
                body.put("message", "Doctor not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
            }
            doctorRepository.deleteById(doctorId);
            body.put("message", "Doctor deleted successfully");
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            body.put("message", "Failed to delete doctor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }


    public ResponseEntity<Map<String, Object>> getAllPatients() {
        Map<String, Object> body = new HashMap<>();
        try {
            List<Patient> patients = patientRepository.findAll();
            body.put("patients", patients);
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            body.put("message", "Failed to fetch patients");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    public ResponseEntity<Map<String, Object>> addPatient(Patient patient) {
        Map<String, Object> body = new HashMap<>();
        try {
            patientRepository.save(patient);
            body.put("patient", patient);
            body.put("message", "Patient created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(body);
        } catch (Exception ex) {
            body.put("message", "Failed to create patient");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    public ResponseEntity<Map<String, Object>> deletePatient(Long patientId) {
        Map<String, Object> body = new HashMap<>();
        try {
            Optional<Patient> patientOpt = patientRepository.findById(patientId);
            if (patientOpt.isEmpty()) {
                body.put("message", "Patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
            }
            patientRepository.deleteById(patientId);
            body.put("message", "Patient deleted successfully");
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            body.put("message", "Failed to delete patient");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }
}
