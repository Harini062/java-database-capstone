package com.project.back_end.repo;

import com.project.back_end.models.Prescription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    // Find prescriptions by appointmentId
    List<Prescription> findByAppointmentId(Long appointmentId);

    // Find prescriptions by prescription Id
    Optional<Prescription> findById(Long id);
}
