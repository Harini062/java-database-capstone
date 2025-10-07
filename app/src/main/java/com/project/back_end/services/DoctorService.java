package com.project.back_end.services;

import com.project.back_end.models.Doctor;
import com.project.back_end.models.Appointment;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
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

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // --- Doctor availability (needed by controller) ---
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        List<Appointment> appointments = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(
                        doctorId,
                        date.atStartOfDay(),
                        date.plusDays(1).atStartOfDay()
                );

        List<String> slots = new ArrayList<>();
        for (int i = 9; i < 17; i++) {
            slots.add(String.format("%02d:00", i));
        }

        Set<String> booked = appointments.stream()
                .map(a -> a.getAppointmentTime().toLocalTime().withMinute(0).toString())
                .collect(Collectors.toSet());

        return slots.stream()
                .filter(slot -> !booked.contains(slot))
                .collect(Collectors.toList());
    }

    // --- Doctor login validation (needed by controller) ---
    public ResponseEntity<Map<String, Object>> validateDoctor(Map<String, String> login) {
        Map<String, Object> response = new HashMap<>();
        String email = login.get("email");
        String password = login.get("password");

        Doctor doctor = doctorRepository.findByEmail(email);
        if (doctor == null || !passwordEncoder.matches(password, doctor.getPassword())) {
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = tokenService.generateToken(doctor.getId(), "doctor");
        response.put("token", token);
        response.put("message", "Login successful");
        response.put("doctor",doctor);
        return ResponseEntity.ok(response);
    }

    // --- Basic CRUD methods ---
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }
    
    public int saveDoctor(Doctor doctor) {
        if (doctorRepository.findByEmail(doctor.getEmail()) != null) return -1;
        try {
            doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public int updateDoctor(Doctor doctor) {
        Optional<Doctor> existing = doctorRepository.findById(doctor.getId());
        if (existing.isEmpty()) return -1;
        try {
            if (doctor.getPassword() != null && !doctor.getPassword().isBlank() &&
                    !doctor.getPassword().equals(existing.get().getPassword())) {
                doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public int deleteDoctor(long id) {
        Optional<Doctor> doctor = doctorRepository.findById(id);
        if (doctor.isEmpty()) return -1;
        try {
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // --- Filtering Methods ---
    public List<Doctor> filterDoctor(String name, String amOrPm, String specialty) {
        final String finalName = (name == null) ? "" : name.trim();
        final String finalSpecialty = (specialty == null) ? "" : specialty.trim();
        final String finalPeriod = (amOrPm == null) ? "" : amOrPm.trim().toUpperCase();
    
        List<Doctor> doctors = doctorRepository.findAll();
    
        // Filter by name
        if (!finalName.isEmpty()) {
            doctors = doctors.stream()
                    .filter(d -> d.getName() != null &&
                                 d.getName().toLowerCase().contains(finalName.toLowerCase()))
                    .collect(Collectors.toList());
        }
    
        // Filter by specialty
        if (!finalSpecialty.isEmpty()) {
            doctors = doctors.stream()
                    .filter(d -> d.getSpecialty() != null &&
                                 d.getSpecialty().equalsIgnoreCase(finalSpecialty))
                    .collect(Collectors.toList());
        }
    
        // Filter by time
        if (!finalPeriod.isEmpty()) {
            doctors = filterDoctorByTime(doctors, finalPeriod);
        }
    
        return doctors;
    }
    

    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        if (amOrPm == null || amOrPm.isBlank()) return doctors;
    
        final String period = amOrPm.trim().toUpperCase(); // final for lambda
    
        return doctors.stream()
                .filter(doctor -> isDoctorAvailable(doctor, period))
                .collect(Collectors.toList());
    }
    
    private boolean isDoctorAvailable(Doctor doctor, String period) {
        if (doctor.getAvailableTimes() == null) return false;
    
        for (String time : doctor.getAvailableTimes()) {
            try {
                int hour = Integer.parseInt(time.split(":")[0]);
                if ((period.equals("AM") && hour < 12) || (period.equals("PM") && hour >= 12)) {
                    return true;
                }
            } catch (NumberFormatException e) {
                // ignore invalid time format
            }
        }
        return false;
    }
    
    

    // --- Wrapper methods for all combinations ---
    public List<Doctor> filterDoctorsByNameSpecialityAndTime(String name, String specialty, String amOrPm) {
        return filterDoctor(name, amOrPm, specialty);
    }

    public List<Doctor> filterDoctorByNameAndTime(String name, String amOrPm) {
        return filterDoctor(name, amOrPm, "");
    }

    public List<Doctor> filterDoctorByNameAndSpecialty(String name, String specialty) {
        return filterDoctor(name, "", specialty);
    }

    public List<Doctor> filterDoctorByTimeAndSpecialty(String amOrPm, String specialty) {
        return filterDoctor("", amOrPm, specialty);
    }

    public List<Doctor> filterDoctorBySpecialty(String specialty) {
        return filterDoctor("", "", specialty);
    }

    public List<Doctor> filterDoctorsByTime(String amOrPm) {
        return filterDoctor("", amOrPm, "");
    }
}
