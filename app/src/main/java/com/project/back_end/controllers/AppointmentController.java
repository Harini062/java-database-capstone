package com.project.back_end.controller;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.TokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final TokenService tokenService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, TokenService tokenService) {
        this.appointmentService = appointmentService;
        this.tokenService = tokenService;
    }


    //Fetch appointments for a given date and patient name.
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(
            @PathVariable String date,
            @PathVariable String patientName,
            @PathVariable String token) {

        if (!tokenService.validateToken(token, "doctor")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token. Only doctors can access appointments."));
        }

        List<Appointment> appointments = appointmentService.getAppointments(date, patientName);
        return ResponseEntity.ok(appointments);
    }


    //Book an appointment (patients only).

    @PostMapping("/{token}")
    public ResponseEntity<?> bookAppointment(
            @PathVariable String token,
            @RequestBody Appointment appointment) {

        if (!tokenService.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token. Only patients can book appointments."));
        }

        // Validate appointment before booking
        boolean isValid = appointmentService.validateAppointment(appointment);
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid appointment details."));
        }

        Appointment bookedAppointment = appointmentService.bookAppointment(appointment);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Appointment booked successfully.", "id", bookedAppointment.getId().toString()));
    }


    //Update an existing appointment (patients only).

    @PutMapping("/{token}")
    public ResponseEntity<?> updateAppointment(
            @PathVariable String token,
            @RequestBody Appointment appointment) {

        if (!tokenService.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token. Only patients can update appointments."));
        }

        Appointment updated = appointmentService.updateAppointment(appointment);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Appointment not found."));
        }

        return ResponseEntity.ok(Map.of("message", "Appointment updated successfully."));
    }

    //Cancel an appointment by ID (patients only).
        @DeleteMapping("/{id}/{token}")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable Long id,
            @PathVariable String token) {

        if (!tokenService.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token. Only patients can cancel appointments."));
        }

        boolean canceled = appointmentService.cancelAppointment(id);
        if (!canceled) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Appointment not found or could not be cancelled."));
        }

        return ResponseEntity.ok(Map.of("message", "Appointment cancelled successfully."));
    }
}
