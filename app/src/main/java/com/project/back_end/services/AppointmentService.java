package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              TokenService tokenService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
    }

    // Book new appointment
    public ResponseEntity<Map<String, Object>> bookAppointment(Appointment appointment, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Ensure patient from token matches appointment
            Long patientId = tokenService.extractPatientId(token);
            if (patientId == null || !appointment.getPatient().getId().equals(patientId)) {
                response.put("message", "Unauthorized: You can only book for yourself");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            appointmentRepository.save(appointment);
            response.put("appointment", AppointmentDTO.fromEntity(appointment));
            response.put("message", "Appointment booked successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("message", "Failed to book appointment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Update appointment
    public ResponseEntity<Map<String, Object>> updateAppointment(Appointment appointment, String token) {
        Map<String, Object> response = new HashMap<>();

        Optional<Appointment> existing = appointmentRepository.findById(appointment.getId());
        if (existing.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        try {
            // Validate that the patient owns the appointment
            Long patientId = tokenService.extractPatientId(token);
            if (patientId == null || !appointment.getPatient().getId().equals(patientId)) {
                response.put("message", "Unauthorized: You can only update your own appointment");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            appointmentRepository.save(appointment);
            response.put("appointment", AppointmentDTO.fromEntity(appointment));
            response.put("message", "Appointment updated successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Failed to update appointment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Cancel appointment
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();

        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Appointment appointment = appointmentOpt.get();

        // Extract patient ID from token
        Long patientId = tokenService.extractPatientId(token);
        if (patientId == null || !appointment.getPatient().getId().equals(patientId)) {
            response.put("message", "Unauthorized: You can only cancel your own appointment");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            appointmentRepository.delete(appointment);
            response.put("message", "Appointment cancelled successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Failed to cancel appointment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Get appointments for a doctor on a specific date
    public ResponseEntity<Map<String, Object>> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Extract doctor ID from token
            Long doctorId = tokenService.extractDoctorId(token);
            System.out.println("Doctor ID from token: " + doctorId);
            System.out.println("Token role: " + tokenService.extractRole(token));
            System.out.println("Appointments fetched for doctorId=" + doctorId + " on date=" + date);
            if (doctorId == null) {
                response.put("message", "Unauthorized: Invalid doctor token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(23, 59, 59);

            String patientNameFilter = (pname != null && !pname.equalsIgnoreCase("all")) ? pname : null;

            List<Appointment> appointments = appointmentRepository
                .findAppointmentsByDoctorAndPatientNameAndDate(doctorId, patientNameFilter, start, end);

            List<AppointmentDTO> dtos = appointments.stream()
                    .map(AppointmentDTO::fromEntity)
                    .collect(Collectors.toList());

            response.put("appointments", dtos);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Failed to fetch appointments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
