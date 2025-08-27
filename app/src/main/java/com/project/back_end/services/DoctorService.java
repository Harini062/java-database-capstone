package com.project.back_end.services;

import com.project.back_end.models.Doctor;
import com.project.back_end.models.Appointment;
import com.project.back_end.DTO.LoginDTO;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    //  Get availability of doctor by date
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        List<Appointment> appointments = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(
                        doctorId,
                        date.atStartOfDay(),
                        date.plusDays(1).atStartOfDay()
                );

        // Define working hours slots
        List<String> slots = new ArrayList<>();
        for (int i = 9; i < 17; i++) {
            slots.add(String.format("%02d:00", i));
        }

        // Collect booked slots
        Set<String> booked = appointments.stream()
                .map(appt -> appt.getAppointmentTime().toLocalTime().withMinute(0).toString())
                .collect(Collectors.toSet());

        return slots.stream()
                .filter(slot -> !booked.contains(slot))
                .collect(Collectors.toList());
    }

    //  Save doctor
    public int saveDoctor(Doctor doctor) {
        if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
            return -1; // doctor already exists
        }
        try {
            doctorRepository.save(doctor);
            return 1; // success
        } catch (Exception e) {
            return 0; // error
        }
    }

    // Update doctor
    public int updateDoctor(Doctor doctor) {
        if (doctorRepository.findById(doctor.getId()).isEmpty()) {
            return -1; // not found
        }
        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    //  Get all doctors
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    // Delete doctor
    public int deleteDoctor(long id) {
        Optional<Doctor> doctor = doctorRepository.findById(id);
        if (doctor.isEmpty()) {
            return -1; // not found
        }
        try {
            appointmentRepository.deleteAllByDoctorId(id); // clear appointments
            doctorRepository.deleteById(id);
            return 1; // success
        } catch (Exception e) {
            return 0; // error
        }
    }

    //  Doctor login
    public ResponseEntity<Map<String, String>> validateDoctor(Map<String, String> login) {
        Map<String, String> response = new HashMap<>();
        String email = login.get("email");
        String password = login.get("password");
    
        Doctor doctor = doctorRepository.findByEmail(email);
        if (doctor == null || !Objects.equals(doctor.getPassword(), password)) {
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    
        String token = tokenService.generateToken(doctor.getId(), "doctor");
        response.put("token", token);
        response.put("message", "Login successful");
        return ResponseEntity.ok(response);
    }

   
    public List<Doctor> findDoctorByName(String name) {
        return doctorRepository.findByNameLike("%" + name + "%");
    }

    public List<Doctor> filterDoctorsByNameSpecialityAndTime(String name, String specialty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        return filterDoctorByTime(doctors, amOrPm);
    }

    public List<Doctor> filterDoctorByNameAndTime(String name, String amOrPm) {
        return filterDoctorByTime(doctorRepository.findByNameLike("%" + name + "%"), amOrPm);
    }

    public List<Doctor> filterDoctorByNameAndSpecialty(String name, String specialty) {
        return doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
    }

    public List<Doctor> filterDoctorByTimeAndSpecialty(String specialty, String amOrPm) {
        return filterDoctorByTime(doctorRepository.findBySpecialtyIgnoreCase(specialty), amOrPm);
    }

    public List<Doctor> filterDoctorBySpecialty(String specialty) {
        return doctorRepository.findBySpecialtyIgnoreCase(specialty);
    }

    public List<Doctor> filterDoctorsByTime(String amOrPm) {
        return filterDoctorByTime(doctorRepository.findAll(), amOrPm);
    }


    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        return doctors;
    }

    public List<Doctor> filterDoctor(String name, String amOrPm, String specialty) {
        name = (name == null ? "" : name);
        specialty = (specialty == null ? "" : specialty);
    
        List<Doctor> base;
        if (!name.isBlank() && !specialty.isBlank()) {
            base = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        } else if (!name.isBlank()) {
            base = doctorRepository.findByNameLike("%" + name + "%");
        } else if (!specialty.isBlank()) {
            base = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        } else {
            base = doctorRepository.findAll();
        }
        return filterDoctorByTime(base, amOrPm);
    }
    
}
