package com.project.back_end.models;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.*;

@Entity
public class Appointment{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "doctor cannot be null")
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @NotNull(message = "patient cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull(message = "appointment time is required")
    @Future(message = "Appointment time must be in the future")
    @Column(name = "appointment_time", nullable = false)
    private LocalDateTime appointmentTime;

    /**
     * Status: 
     * 0 = Scheduled
     * 1 = Completed
     */
    @NotNull(message = "status is required")
    @Column(nullable = false)
    private Integer status;

    @Column(name = "created_at", updatable = false, insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

     @Transient
    public LocalDateTime getEndTime() {
        return appointmentTime != null ? appointmentTime.plusHours(1) : null;
    }

    @Transient
    public LocalDate getAppointmentDate() {
        return appointmentTime != null ? appointmentTime.toLocalDate() : null;
    }

    @Transient
    public LocalTime getAppointmentTimeOnly() {
        return appointmentTime != null ? appointmentTime.toLocalTime() : null;
    }

    //Getters and Setters
    public Long getId() { 
        return id; 
    }
    public void setId(Long id) { 
        this.id = id; 
    }

    public Doctor getDoctor() { 
        return doctor; 
    }
    public void setDoctor(Doctor doctor) { 
        this.doctor = doctor;
    }

    public Patient getPatient() { 
        return patient; 
    }
    public void setPatient(Patient patient) {
         this.patient = patient; 
    }

    public LocalDateTime getAppointmentTime() { 
        return appointmentTime; 
    }
    public void setAppointmentTime(LocalDateTime appointmentTime) { 
        this.appointmentTime = appointmentTime;
    }

    public Integer getStatus() { 
        return status; 
    }
    public void setStatus(Integer status) { 
        this.status = status; 
    }

    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
}