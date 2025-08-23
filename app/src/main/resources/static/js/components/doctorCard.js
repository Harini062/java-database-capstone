import { deleteDoctor } from "./services/doctorServices.js";
import { getPatientData } from "./services/patientServices.js";
import { showBookingOverlay } from "./loggedPatient.js";

export function createDoctorCard(doctor) {
  
  const card = document.createElement("div");
  card.classList.add("doctor-card");
  
  const role = localStorage.getItem("userRole");

  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  const name = document.createElement("h3");
  name.textContent = doctor.name;

  const specialization = document.createElement("p");
  specialization.textContent = `Specialization: ${doctor.specialization}`;

  const email = document.createElement("p");
  email.textContent = `Email: ${doctor.email}`;

  const availability = document.createElement("p");
  availability.textContent = `Available: ${
    Array.isArray(doctor.availability) ? doctor.availability.join(", ") : doctor.availability
  }`;

  infoDiv.appendChild(name);
  infoDiv.appendChild(specialization);
  infoDiv.appendChild(email);
  infoDiv.appendChild(availability);

  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  if (role === "admin") {
    const removeBtn = document.createElement("button");
    removeBtn.textContent = "Delete";
    removeBtn.classList.add("btn", "btn-danger");

    removeBtn.addEventListener("click", async () => {
      const confirmDelete = confirm(`Are you sure you want to delete Dr. ${doctor.name}?`);
      if (!confirmDelete) return;

      try {
        const token = localStorage.getItem("token");
        const success = await deleteDoctor(doctor.id, token);
        if (success) {
          alert(`Doctor ${doctor.name} removed successfully.`);
          card.remove(); 
        } else {
          alert("Error: Could not delete doctor.");
        }
      } catch (error) {
        console.error("Error deleting doctor:", error);
        alert("An unexpected error occurred.");
      }
    });

    actionsDiv.appendChild(removeBtn);
  }

  
  else if (role === "patient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.classList.add("btn", "btn-primary");

    bookNow.addEventListener("click", () => {
      alert("Please login first to book an appointment.");
    });

    actionsDiv.appendChild(bookNow);
  }

  
  else if (role === "loggedPatient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.classList.add("btn", "btn-primary");

    bookNow.addEventListener("click", async (e) => {
      try {
        const token = localStorage.getItem("token");
        if (!token) {
          alert("Session expired. Please log in again.");
          return;
        }

        const patientData = await getPatientData(token);
        showBookingOverlay(e, doctor, patientData);
      } catch (error) {
        console.error("Error booking appointment:", error);
        alert("Unable to book appointment at this time.");
      }
    });

    actionsDiv.appendChild(bookNow);
  }

  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);

  return card;
}
