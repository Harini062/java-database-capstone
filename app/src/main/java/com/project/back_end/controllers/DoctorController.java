package com.project.back_end.controller;

import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.time.LocalDate;

@RestController
@RequestMapping("${api.path}" + "doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final TokenService tokenService;

    @Autowired
    public DoctorController(DoctorService doctorService, TokenService tokenService) {
        this.doctorService = doctorService;
        this.tokenService = tokenService;
    }

    //Get doctor availability (patient/doctor access)
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable String date,
            @PathVariable String token) {

        if (!tokenService.validateToken(token, user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }
        java.time.LocalDate localDate = java.time.LocalDate.parse(date);    
        return ResponseEntity.ok(doctorService.getDoctorAvailability(doctorId, localDate));
    }

    //Get all doctors
    @GetMapping
    public ResponseEntity<?> getDoctors() {
        return ResponseEntity.ok(doctorService.getDoctors());
    }

    //Add a new doctor (Admin only)
    @PostMapping("/{token}")
    public ResponseEntity<?> addDoctor(@PathVariable String token, @RequestBody Doctor doctor) {
        if (!tokenService.validateToken(token, "admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Only admin can add doctors."));
        }
        int added = doctorService.saveDoctor(doctor);
        if (added == -1) return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Doctor already exists."));
        if (added != 1) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal error."));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Doctor added to db."));
    }

    //Doctor login
    @PostMapping("/login")
    public ResponseEntity<?> doctorLogin(@RequestBody Map<String, String> login) {
        return doctorService.validateDoctor(login);
    }

    //Update doctor details (Admin only)
    @PutMapping("/{token}")
    public ResponseEntity<?> updateDoctor(@PathVariable String token, @RequestBody Doctor doctor) {
        if (!tokenService.validateToken(token, "admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Only admin can update doctor details."));
        }
        int updated = doctorService.updateDoctor(doctor);
        if (updated == -1) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Doctor not found."));
        if (updated != 1) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal error."));
        return ResponseEntity.ok(Map.of("message", "Doctor updated successfully."));
    }

    //Delete doctor (Admin only)
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long id, @PathVariable String token) {
        if (!tokenService.validateToken(token, "admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Only admin can delete doctor details."));
        }
        int deleted = doctorService.deleteDoctor(id);
        if (deleted == -1) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Doctor not found with id: " + id));
        if (deleted != 1) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal error."));
        return ResponseEntity.ok(Map.of("message", "Doctor deleted successfully."));
    }
    
    //Filter doctors by name, time, and specialty
    @GetMapping("/filter")
    public ResponseEntity<?> filterDoctors(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String time,
        @RequestParam(required = false) String specialty) {

      return ResponseEntity.ok(doctorService.filterDoctor(
        name != null ? name : "",
        time != null ? time : "",
        specialty != null ? specialty : ""
      ));
    }

}
