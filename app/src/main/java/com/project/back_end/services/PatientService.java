package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
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

    //Create Patient
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception ex) {
            return 0;
        }
    }

    //Get Patient Appointments (token-aware)
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> body = new HashMap<>();
        try {
            Long tokenPatientId = tokenService.extractPatientId(token);
            if (tokenPatientId == null || !Objects.equals(tokenPatientId, id)) {
                body.put("message", "Unauthorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
            }

            List<Appointment> appts = appointmentRepository.findByPatientId(id);
            List<AppointmentDTO> dto = appts.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());

            body.put("appointments", dto);
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            body.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id) {
        Map<String, Object> body = new HashMap<>();
        try {
            List<Appointment> appts = appointmentRepository.findByPatientId(id);
            List<AppointmentDTO> dto = appts.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
            body.put("appointments", dto);
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            body.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    //Filter by Condition ("past" or "future")
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> body = new HashMap<>();
        try {
            int status;
            if ("past".equalsIgnoreCase(condition)) {
                status = 1; // Completed
            } else if ("future".equalsIgnoreCase(condition) || "upcoming".equalsIgnoreCase(condition)) {
                status = 0; // Scheduled
            } else {
                body.put("message", "Invalid condition. Use 'past' or 'future'.");
                return ResponseEntity.badRequest().body(body);
            }

            List<Appointment> appts =
                    appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(id, status);

            List<AppointmentDTO> dto = appts.stream().map(this::toDTO).collect(Collectors.toList());
            body.put("appointments", dto);
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            body.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    //Filter by Doctor Name (partial, case-insensitive)
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> body = new HashMap<>();
        try {
            List<Appointment> appts =
                    appointmentRepository.filterByDoctorNameAndPatientId(name, patientId);

            List<AppointmentDTO> dto = appts.stream().map(this::toDTO).collect(Collectors.toList());
            body.put("appointments", dto);
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            body.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    // Filter by Doctor Name and Condition
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition,
                                                                          String name,
                                                                          long patientId) {
        Map<String, Object> body = new HashMap<>();
        try {
            int status;
            if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else if ("future".equalsIgnoreCase(condition) || "upcoming".equalsIgnoreCase(condition)) {
                status = 0;
            } else {
                body.put("message", "Invalid condition. Use 'past' or 'future'.");
                return ResponseEntity.badRequest().body(body);
            }

            List<Appointment> appts =
                    appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(name, patientId, status);

            List<AppointmentDTO> dto = appts.stream().map(this::toDTO).collect(Collectors.toList());
            body.put("appointments", dto);
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            body.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    //Get Patient Details from token
    public ResponseEntity<Map<String, Object>> getPatientDetailsResponse(String token) {
        Map<String, Object> body = new HashMap<>();
        try {
            String email = tokenService.extractIdentifier(token);
            Patient p = patientRepository.findByEmail(email);
            if (p == null) {
                body.put("message", "Patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
            }
            body.put("patient", p);
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            body.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }


    public Patient getPatientDetails(String token) {
        String email = tokenService.extractIdentifier(token);
        return patientRepository.findByEmail(email);
    }


    public boolean existsByEmailOrPhone(String email, String phone) {
        Patient p = patientRepository.findByEmailOrPhone(email, phone);
        return p != null;
    }


    public ResponseEntity<Map<String, String>> validatePatientLogin(Map<String, String> login) {
        Map<String, String> body = new HashMap<>();
        try {
            String identifier = login.get("identifier");
            String password = login.get("password");

            if (identifier == null || password == null) {
                body.put("message", "Identifier and password are required");
                return ResponseEntity.badRequest().body(body);
            }

            Patient patient = patientRepository.findByEmail(identifier);
            if (patient == null || !password.equals(patient.getPassword())) {
                body.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
            }

            String token = tokenService.generateToken(patient.getEmail());
            body.put("token", token);
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            body.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

 
    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        Map<String, Object> body = new HashMap<>();
        try {
            Long patientId = tokenService.extractPatientId(token);
            if (patientId == null) {
                body.put("message", "Unauthorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
            }

            if (name == null || name.isBlank()) {
                return filterByCondition(condition, patientId);
            } else if (condition == null || condition.isBlank()) {
                return filterByDoctor(name, patientId);
            } else {
                return filterByDoctorAndCondition(condition, name, patientId);
            }
        } catch (Exception ex) {
            body.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    private AppointmentDTO toDTO(Appointment a) {
        Doctor d = a.getDoctor();
        Patient p = a.getPatient();

        Long id = a.getId();
        Long doctorId = (d != null ? d.getId() : null);
        String doctorName = (d != null ? d.getName() : null);
        Long patientId = (p != null ? p.getId() : null);
        String patientName = (p != null ? p.getName() : null);
        String patientEmail = (p != null ? p.getEmail() : null);
        String patientPhone = (p != null ? p.getPhone() : null);
        String patientAddress = (p != null ? p.getAddress() : null);

        LocalDateTime time = a.getAppointmentTime();
        int status = (a.getStatus() == null ? 0 : a.getStatus());

        return new AppointmentDTO(
                id, doctorId, doctorName,
                patientId, patientName, patientEmail, patientPhone, patientAddress,
                time, status
        );
    }
}
