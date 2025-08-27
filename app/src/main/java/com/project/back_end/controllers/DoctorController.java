package com.project.back_end.controller;

import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

        return ResponseEntity.ok(doctorService.getDoctorAvailability(doctorId, date));
    }

    //Get all doctors
    @GetMapping
    public ResponseEntity<?> getDoctors() {
        return ResponseEntity.ok(doctorService.getDoctors());
    }

    //Add a new doctor (Admin only)
    @PostMapping("/add/{token}")
    public ResponseEntity<?> addDoctor(
            @PathVariable String token,
            @RequestBody Doctor doctor) {

        if (!tokenService.validateToken(token, "admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Only admin can add doctors"));
        }

        boolean added = doctorService.saveDoctor(doctor);
        if (!added) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Doctor already exists"));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Doctor added successfully"));
    }

    //Doctor login
    @PostMapping("/login")
    public ResponseEntity<?> doctorLogin(@RequestBody Map<String, String> login) {
        return doctorService.validateDoctor(login);
    }

    //Update doctor details (Admin only)
    @PutMapping("/update/{token}")
    public ResponseEntity<?> updateDoctor(
            @PathVariable String token,
            @RequestBody Doctor doctor) {

        if (!tokenService.validateToken(token, "admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Only admin can update doctors"));
        }

        boolean updated = doctorService.updateDoctor(doctor);
        if (!updated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Doctor not found"));
        }

        return ResponseEntity.ok(Map.of("message", "Doctor updated successfully"));
    }

    //Delete doctor (Admin only)
    @DeleteMapping("/delete/{id}/{token}")
    public ResponseEntity<?> deleteDoctor(
            @PathVariable Long id,
            @PathVariable String token) {

        if (!tokenService.validateToken(token, "admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Only admin can delete doctors"));
        }

        boolean deleted = doctorService.deleteDoctor(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Doctor not found with id: " + id));
        }

        return ResponseEntity.ok(Map.of("message", "Doctor deleted successfully"));
    }

    //Filter doctors by name, time, and speciality
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<?> filterDoctors(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality) {

        return ResponseEntity.ok(doctorService.filterDoctor(name, time, speciality));
    }
}
