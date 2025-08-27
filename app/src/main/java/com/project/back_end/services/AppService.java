package com.project.back_end.services;

import com.project.back_end.DTO.LoginDTO;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
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
public class AppService {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public AppService(TokenService tokenService,
                      AdminRepository adminRepository,
                      DoctorRepository doctorRepository,
                      PatientRepository patientRepository,
                      DoctorService doctorService,
                      PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    //  Validate Token
    public ResponseEntity<Map<String, String>> validateToken(String token, String userType) {
        Map<String, String> response = new HashMap<>();
        if (!tokenService.validateToken(token, userType)) {
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        response.put("message", "Token valid");
        return ResponseEntity.ok(response);
    }

    // Validate Admin Login
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        try {
            Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());
            if (admin != null && admin.getPassword().equals(receivedAdmin.getPassword())) {
                String token = tokenService.generateToken(admin.getId(), "admin");
                response.put("token", token);
                return ResponseEntity.ok(response);
            }
            response.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            response.put("message", "Error validating admin");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    //  Filter Doctors by Name, Specialty & Time
    public ResponseEntity<Map<String, Object>> filterDoctor(String name, String specialty, String time) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Doctor> doctors = doctorService.filterDoctorsByNameSpecialityAndTime(name, specialty, time);
            response.put("doctors", doctors);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error filtering doctors");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    //  Validate Appointment slot
    public int validateAppointment(Appointment appointment) {
        if (appointment.getDoctor() == null) {
            return -1; // no doctor assigned
        }

        Optional<Doctor> doctorOpt = doctorRepository.findById(appointment.getDoctor().getId());
        if (doctorOpt.isEmpty()) {
            return -1; // doctor not found
        }

        Doctor doctor = doctorOpt.get();
        List<String> availableSlots = doctorService.getDoctorAvailability(
            doctor.getId(),appointment.getAppointmentTime().toLocalDate());

        if (availableSlots.contains(appointment.getAppointmentTime().toString())) {
            return 1; // valid
        }
        return 0; // unavailable
    }

    //  Validate Patient uniqueness
    public boolean validatePatient(Patient patient) {
        Patient existing = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());
        return existing == null;
    }

    //  Validate Patient Login
    public ResponseEntity<Map<String, String>> validatePatientLogin(LoginDTO login) {
        Map<String, String> response = new HashMap<>();
        try {
            Patient patient = patientRepository.findByEmail(login.getIdentifier());
            if (patient != null && patient.getPassword().equals(login.getPassword())) {
                String token = tokenService.generateToken(patient.getId(), "patient");
                response.put("token", token);
                return ResponseEntity.ok(response);
            }
            response.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            response.put("message", "Error validating patient login");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    //  Filter Patient Appointments
    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractIdentifier(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null) {
                response.put("message", "Patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (condition != null && name != null) {
                return patientService.filterByDoctorAndCondition(condition, name, patient.getId());
            } else if (condition != null) {
                return patientService.filterByCondition(condition, patient.getId());
            } else if (name != null) {
                return patientService.filterByDoctor(name, patient.getId());
            } else {
                response.put("message", "No filter applied");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("message", "Error filtering patient appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
