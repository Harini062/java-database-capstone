// doctorCard.js
import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";
import { showBookingOverlay } from "../bookingOverlay.js";

export function createDoctorCard(doctor) {
  const card = document.createElement("div");
  card.classList.add("doctor-card");

  // Doctor Info
  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  const name = document.createElement("h3");
  name.textContent = doctor.name;

  const specialty = document.createElement("p");
  specialty.textContent = `Specialization: ${doctor.specialty || "N/A"}`;

  const email = document.createElement("p");
  email.textContent = `Email: ${doctor.email}`;

  const availableTimes = document.createElement("p");
  availableTimes.textContent = `Available: ${
    Array.isArray(doctor.availableTimes) ? doctor.availableTimes.join(", ") : "N/A"
  }`;

  infoDiv.appendChild(name);
  infoDiv.appendChild(specialty);
  infoDiv.appendChild(email);
  infoDiv.appendChild(availableTimes);

  // Actions
  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  const role = localStorage.getItem("userRole");

  // Book Now only for patients
  if (role === "patient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.classList.add("btn", "btn-primary");

    bookNow.addEventListener("click", async (e) => {
      const token = localStorage.getItem("token");
      if (!token) return alert("Please login first.");
      try {
        const patientData = await getPatientData(token);
        if (!patientData) return alert("Session expired. Please login again.");
        showBookingOverlay(e, doctor, patientData);
      } catch (err) {
        console.error(err);
        alert("Unable to book appointment.");
      }
    });

    actionsDiv.appendChild(bookNow);
  }

  // Delete only for admin
  if (role === "admin") {
    const removeBtn = document.createElement("button");
    removeBtn.textContent = "Delete";
    removeBtn.classList.add("btn", "btn-danger");

    removeBtn.addEventListener("click", async () => {
      if (!confirm(`Are you sure you want to delete Dr. ${doctor.name}?`)) return;
      try {
        const token = localStorage.getItem("token");
        const success = await deleteDoctor(doctor.id, token);
        if (success) {
          alert(`Doctor ${doctor.name} removed successfully.`);
          card.remove();
        } else {
          alert("Failed to delete doctor.");
        }
      } catch (err) {
        console.error(err);
        alert("Error deleting doctor.");
      }
    });

    actionsDiv.appendChild(removeBtn);
  }

  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);

  return card;
}
