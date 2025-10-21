package com.project.back_end.controller;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final TokenService tokenService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, TokenService tokenService) {
        this.appointmentService = appointmentService;
        this.tokenService = tokenService;
    }

    //Fetch appointments for a doctor on a given date (Doctor only)
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(@PathVariable String date,
                                            @PathVariable String patientName,
                                            @PathVariable String token) {
        if (!tokenService.validateToken(token, "doctor")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token. Only doctors can access appointments."));
        }
        java.time.LocalDate localDate = java.time.LocalDate.parse(date);
        if ("all".equalsIgnoreCase(patientName)) {
            return ResponseEntity.ok(appointmentService.getAppointment(null, localDate, token));
        } else {
            return ResponseEntity.ok(appointmentService.getAppointment(patientName, localDate, token));
        }
    }

    //Book an appointment (Patient only)
    @PostMapping("/{token}")
    public ResponseEntity<?> bookAppointment(@PathVariable String token, @RequestBody Appointment appointment) {
        if (!tokenService.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token. Only patients can book appointments."));
        }
        return appointmentService.bookAppointment(appointment, token);
    }

    //Update an appointment (Patient only)
    @PutMapping("/{token}")
    public ResponseEntity<?> updateAppointment(@PathVariable String token, @RequestBody Appointment appointment) {
        if (!tokenService.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token. Only patients can update appointments."));
        }
        return appointmentService.updateAppointment(appointment, token);
    }

    //Cancel an appointment (Patient only)
    @DeleteMapping("/cancel/{id}/{token}")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable Long id,
            @PathVariable String token) {

        if (!tokenService.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Only patients can cancel appointments"));
        }

        return appointmentService.cancelAppointment(id, token);
    }
}
