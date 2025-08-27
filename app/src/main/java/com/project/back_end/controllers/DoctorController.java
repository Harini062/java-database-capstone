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

    
    //Get Doctor Availability
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable String date,
            @PathVariable String token) {

        if (!tokenService.validateToken(token, user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token."));
        }

        Map<String, Object> availability = doctorService.getDoctorAvailability(doctorId, date);
        return ResponseEntity.ok(availability);
    }


    //Get List of Doctors
    
    @GetMapping
    public ResponseEntity<?> getDoctors() {
        List<Doctor> doctors = doctorService.getDoctors();
        return ResponseEntity.ok(doctors);
    }

    //Add New Doctor (Admin only)

    @PostMapping("/{token}")
    public ResponseEntity<?> addDoctor(
            @PathVariable String token,
            @RequestBody Doctor doctor) {

        if (!tokenService.validateToken(token, "admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Only admin can add doctors."));
        }

        try {
            boolean added = doctorService.saveDoctor(doctor);
            if (!added) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Doctor already exists."));
            }
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Doctor added to db."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Some internal error occurred."));
        }
    }


    //Doctor Login

    @PostMapping("/login")
    public ResponseEntity<?> doctorLogin(@RequestBody Map<String, String> login) {
        try {
            return doctorService.validateDoctor(login);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Some internal error occurred."));
        }
    }


    //Update Doctor Details

    @PutMapping("/{token}")
    public ResponseEntity<?> updateDoctor(
            @PathVariable String token,
            @RequestBody Doctor doctor) {

        if (!tokenService.validateToken(token, "admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Only admin can update doctor details."));
        }

        try {
            boolean updated = doctorService.updateDoctor(doctor);
            if (!updated) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Doctor not found."));
            }
            return ResponseEntity.ok(Map.of("message", "Doctor updated."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Some internal error occurred."));
        }
    }

    
    //Delete Doctor

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<?> deleteDoctor(
            @PathVariable Long id,
            @PathVariable String token) {

        if (!tokenService.validateToken(token, "admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Only admin can delete doctor details."));
        }

        try {
            boolean deleted = doctorService.deleteDoctor(id);
            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Doctor not found with id: " + id));
            }
            return ResponseEntity.ok(Map.of("message", "Doctor deleted successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Some internal error occurred."));
        }
    }


    //Filter Doctors

    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<?> filterDoctors(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality) {

        try {
            List<Doctor> filtered = doctorService.filterDoctor(name, time, speciality);
            return ResponseEntity.ok(filtered);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Some internal error occurred."));
        }
    }
}
