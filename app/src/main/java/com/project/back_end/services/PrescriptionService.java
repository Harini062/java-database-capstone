package com.project.back_end.services;

import com.project.back_end.models.Prescription;
import com.project.back_end.models.Appointment;
import com.project.back_end.repo.PrescriptionRepository;
import com.project.back_end.repo.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    // Save or Update Prescription 
    public ResponseEntity<Map<String, String>> savePrescription(Prescription prescription, String token) {
        Map<String, String> response = new HashMap<>();
        try {
            Optional<Appointment> apptOpt = appointmentRepository.findById(prescription.getAppointment().getId());
            if (apptOpt.isEmpty()) {
                response.put("message", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Appointment appt = apptOpt.get();
            Long doctorId = tokenService.extractUserId(token);

            if (!appt.getDoctor().getId().equals(doctorId)) {
                response.put("message", "Unauthorized: Only assigned doctor can add/update prescription");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            prescription.setAppointment(appt);
            prescriptionRepository.save(prescription);

            response.put("message", "Prescription saved successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("message", "Error saving prescription: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Get Prescription by Appointment (doctor or patient can access)
    public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Appointment> apptOpt = appointmentRepository.findById(appointmentId);
            if (apptOpt.isEmpty()) {
                response.put("message", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Appointment appt = apptOpt.get();
            Long userId = tokenService.extractUserId(token);

            boolean isDoctor = appt.getDoctor() != null && appt.getDoctor().getId().equals(userId);
            boolean isPatient = appt.getPatient() != null && appt.getPatient().getId().equals(userId);

            if (!isDoctor && !isPatient) {
                response.put("message", "Unauthorized: Only assigned doctor or patient can view prescription");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            List<Prescription> prescriptions = prescriptionRepository.findByAppointmentId(appointmentId);
            if (prescriptions == null || prescriptions.isEmpty()) {
                response.put("message", "Prescriptions not found for appointmentId: " + appointmentId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.put("prescriptions", prescriptions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error retrieving prescriptions: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Delete Prescription (only doctor can delete)
    public ResponseEntity<Map<String, String>> deletePrescription(Long prescriptionId, String token) {
        Map<String, String> response = new HashMap<>();
        try {
            Optional<Prescription> prescOpt = prescriptionRepository.findById(prescriptionId);
            if (prescOpt.isEmpty()) {
                response.put("message", "Prescription not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Prescription prescription = prescOpt.get();
            Long doctorId = tokenService.extractUserId(token);

            if (!prescription.getAppointment().getDoctor().getId().equals(doctorId)) {
                response.put("message", "Unauthorized: Only assigned doctor can delete prescription");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            prescriptionRepository.delete(prescription);
            response.put("message", "Prescription deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error deleting prescription: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
