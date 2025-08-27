package com.project.back_end.controller;

import com.project.back_end.models.Patient;
import com.project.back_end.models.Appointment;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("${api.path}" + "patient")
public class PatientController {

    private final PatientService patientService;
    private final TokenService tokenService;

    @Autowired
    public PatientController(PatientService patientService, TokenService tokenService) {
        this.patientService = patientService;
        this.tokenService = tokenService;
    }

    //Get patient details by token
    @GetMapping("/details/{token}")
    public ResponseEntity<?> getPatientDetails(@PathVariable String token) {
        if (!tokenService.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }

        Patient patient = patientService.getPatientDetails(token);
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Patient not found"));
        }
        return ResponseEntity.ok(patient);
    }

    //Patient signup (new registration)
    @PostMapping("/signup")
    public ResponseEntity<?> createPatient(@RequestBody Patient patient) {
        if (patientService.existsByEmailOrPhone(patient.getEmail(), patient.getPhone())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Patient with email or phone already exists"));
        }

        boolean created = patientService.createPatient(patient);
        if (!created) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not create patient"));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Signup successful"));
    }

    //Patient login
    @PostMapping("/login")
    public ResponseEntity<?> patientLogin(@RequestBody Map<String, String> login) {
        return patientService.validatePatientLogin(login);
    }

    //Get all appointments for a patient
    @GetMapping("/{id}/appointments/{token}")
    public ResponseEntity<?> getPatientAppointments(
            @PathVariable Long id,
            @PathVariable String token) {

        if (!tokenService.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }

        List<Appointment> appointments = patientService.getPatientAppointment(id);
        return ResponseEntity.ok(appointments);
    }

    //Filter patient appointments
    @GetMapping("/appointments/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterPatientAppointments(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token) {

        if (!tokenService.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }

        List<Appointment> filteredAppointments = patientService.filterPatient(condition, name);
        return ResponseEntity.ok(filteredAppointments);
    }
}
