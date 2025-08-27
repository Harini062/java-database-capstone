package com.project.back_end.controller;

import com.project.back_end.DTO.LoginDTO;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "patient")
public class PatientController {

    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    //  Get patient details by token
    @GetMapping("/details/{token}")
    public ResponseEntity<?> getPatientDetails(@PathVariable String token) {
        try {
            return patientService.getPatientDetails(token); 

        } 
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error retrieving patient details: " + e.getMessage()));
        }
    }

    //  Patient login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            return patientService.validatePatientLogin(loginDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error during login: " + e.getMessage()));
        }
    }

    // Get appointments for patient
    @GetMapping("/appointments/{patientId}/{token}")
    public ResponseEntity<?> getAppointments(
            @PathVariable Long patientId,
            @PathVariable String token) {
        try {
            return patientService.getPatientAppointment(patientId, token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error retrieving appointments: " + e.getMessage()));
        }
    }
}
