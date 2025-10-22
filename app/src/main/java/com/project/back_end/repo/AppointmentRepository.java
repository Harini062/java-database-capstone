package com.project.back_end.repo;

import com.project.back_end.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // 1. Retrieve appointments for a doctor within a time range
    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.doctor d " +
           "WHERE d.id = :doctorId AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end);

    // 2. Filter by doctor ID, partial patient name (case-insensitive), and time range
    @Query("SELECT a FROM Appointment a " +
           "JOIN FETCH a.doctor d " +
           "JOIN FETCH a.patient p " +
           "WHERE d.id = :doctorId " +
           "AND (:patientName IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :patientName, '%'))) " +
           "AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findAppointmentsByDoctorAndPatientNameAndDate(
            @Param("doctorId") Long doctorId,
            @Param("patientName") String patientName,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // 3. Delete all appointments related to a doctor
    @Transactional
    @Modifying
    void deleteAllByDoctorId(Long doctorId);

    // 4. Find all appointments for a specific patient
    List<Appointment> findByPatientId(Long patientId);

    // 5. Find appointments by patient + status ordered by time
    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);

    // 6. Search appointments by doctor name and patient ID
    @Query("SELECT a FROM Appointment a " +
           "JOIN a.doctor d " +
           "WHERE a.patient.id = :patientId " +
           "AND LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%'))")
    List<Appointment> filterByDoctorNameAndPatientId(String doctorName, Long patientId);

    // 7. Filter by doctor name + patient ID + status
    @Query("SELECT a FROM Appointment a " +
           "JOIN a.doctor d " +
           "WHERE a.patient.id = :patientId " +
           "AND LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
           "AND a.status = :status")
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(String doctorName, Long patientId, int status);
}
