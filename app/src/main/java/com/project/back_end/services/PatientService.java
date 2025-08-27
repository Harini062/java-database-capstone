package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.DTO.LoginDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;

    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          DoctorRepository doctorRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
    }

    //  Create Patient
    public int createPatient(Patient patient) {
        if (patientRepository.findByEmail(patient.getEmail()) != null) {
            return -1; // already exists
        }
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception ex) {
            return 0;
        }
    }

    // Patient Login & Token Generation
    public ResponseEntity<Map<String, String>> validatePatient(LoginDTO login) {
        Map<String, String> response = new HashMap<>();
        Patient patient = patientRepository.findByEmail(login.getIdentifier());

        if (patient == null || !patient.getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = tokenService.generateToken(patient.getId(), "patient");
        response.put("token", token);
        response.put("message", "Login successful");
        return ResponseEntity.ok(response);
    }

    // Get Patient Appointments (token-aware)
    public ResponseEntity<Map<String, Object>> getPatientAppointments(Long patientId, String token) {
        Map<String, Object> body = new HashMap<>();
        try {
            Long tokenPatientId = tokenService.extractPatientId(token);
            if (tokenPatientId == null || !Objects.equals(tokenPatientId, patientId)) {
                body.put("message", "Unauthorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
            }

            List<AppointmentDTO> dto = appointmentRepository.findByPatientId(patientId)
                    .stream().map(this::toDTO).collect(Collectors.toList());

            body.put("appointments", dto);
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            body.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    // Filter by Condition ("past" or "future")
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long patientId) {
        Map<String, Object> body = new HashMap<>();
        try {
            int status;
            if ("past".equalsIgnoreCase(condition)) {
                status = 1; // completed
            } else if ("future".equalsIgnoreCase(condition) || "upcoming".equalsIgnoreCase(condition)) {
                status = 0; // scheduled
            } else {
                body.put("message", "Invalid condition. Use 'past' or 'future'");
                return ResponseEntity.badRequest().body(body);
            }

            List<AppointmentDTO> dto = appointmentRepository
                    .findByPatient_IdAndStatusOrderByAppointmentTimeAsc(patientId, status)
                    .stream().map(this::toDTO).collect(Collectors.toList());

            body.put("appointments", dto);
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            body.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    // Filter by Doctor Name
    public ResponseEntity<Map<String, Object>> filterByDoctor(String doctorName, Long patientId) {
        Map<String, Object> body = new HashMap<>();
        try {
            List<AppointmentDTO> dto = appointmentRepository
                    .filterByDoctorNameAndPatientId(doctorName, patientId)
                    .stream().map(this::toDTO).collect(Collectors.toList());

            body.put("appointments", dto);
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            body.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    // Filter by Doctor + Condition
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String doctorName, Long patientId) {
        Map<String, Object> body = new HashMap<>();
        try {
            int status;
            if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else if ("future".equalsIgnoreCase(condition) || "upcoming".equalsIgnoreCase(condition)) {
                status = 0;
            } else {
                body.put("message", "Invalid condition");
                return ResponseEntity.badRequest().body(body);
            }

            List<AppointmentDTO> dto = appointmentRepository
                    .filterByDoctorNameAndPatientIdAndStatus(doctorName, patientId, status)
                    .stream().map(this::toDTO).collect(Collectors.toList());

            body.put("appointments", dto);
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            body.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    
    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String doctorName, String token) {
        Map<String, Object> body = new HashMap<>();
        try {
            Long patientId = tokenService.extractPatientId(token);
            if (patientId == null) {
                body.put("message", "Unauthorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
            }

            if (doctorName == null || doctorName.isBlank()) {
                return filterByCondition(condition, patientId);
            } else if (condition == null || condition.isBlank()) {
                return filterByDoctor(doctorName, patientId);
            } else {
                return filterByDoctorAndCondition(condition, doctorName, patientId);
            }
        } catch (Exception ex) {
            body.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    //  Get Patient Details
    public ResponseEntity<Map<String, Object>> getPatientDetailsResponse(String token) {
        Map<String, Object> body = new HashMap<>();
        try {
            Long patientId = tokenService.extractPatientId(token);
            if (patientId == null) {
                body.put("message", "Unauthorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
            }

            Patient patient = patientRepository.findById(patientId).orElse(null);
            if (patient == null) {
                body.put("message", "Patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
            }

            body.put("patient", patient);
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            body.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    
    public boolean existsByEmailOrPhone(String email, String phone) {
        return patientRepository.findByEmailOrPhone(email, phone) != null;
    }

    private AppointmentDTO toDTO(Appointment a) {
        Doctor d = a.getDoctor();
        Patient p = a.getPatient();

        return new AppointmentDTO(
                a.getId(),
                d != null ? d.getId() : null,
                d != null ? d.getName() : null,
                p != null ? p.getId() : null,
                p != null ? p.getName() : null,
                p != null ? p.getEmail() : null,
                p != null ? p.getPhone() : null,
                p != null ? p.getAddress() : null,
                a.getAppointmentTime(),
                a.getStatus() == null ? 0 : a.getStatus()
        );
    }
}
