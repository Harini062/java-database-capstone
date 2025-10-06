// loggedPatient.js
import { getDoctors, filterDoctors } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";
import { bookAppointment } from "./services/appointmentRecordService.js";

// Load doctors & filters on DOMContentLoaded
document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();

  const searchBar = document.getElementById("searchBar");
  const filterTime = document.getElementById("filterTime");
  const filterSpecialty = document.getElementById("filterSpecialty");

  if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
  if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);
  if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);
});

async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (err) {
    console.error("Failed to load doctors:", err);
    document.getElementById("content").innerHTML = "<p>Error loading doctors.</p>";
  }
}

export function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  if (!contentDiv) return;

  contentDiv.innerHTML = "";
  doctors.forEach((doctor) => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

// Booking overlay
export function showBookingOverlay(e, doctor, patient) {
  if (!patient) {
    alert("No logged-in patient found.");
    return;
  }

  const ripple = document.createElement("div");
  ripple.classList.add("ripple-overlay");
  ripple.style.left = `${e.clientX}px`;
  ripple.style.top = `${e.clientY}px`;
  document.body.appendChild(ripple);
  setTimeout(() => ripple.classList.add("active"), 50);

  const modal = document.createElement("div");
  modal.classList.add("modalApp");
  modal.innerHTML = `
    <h2>Book Appointment</h2>
    <input class="input-field" value="${patient.name}" disabled />
    <input class="input-field" value="${doctor.name}" disabled />
    <input class="input-field" value="${doctor.specialty}" disabled />
    <input class="input-field" value="${doctor.email}" disabled />
    <input class="input-field" type="date" id="appointment-date" />
    <select class="input-field" id="appointment-time">
      <option value="">Select time</option>
      ${doctor.availableTimes.map((t) => `<option value="${t}">${t}</option>`).join("")}
    </select>
    <button class="confirm-booking">Confirm Booking</button>
  `;
  document.body.appendChild(modal);
  setTimeout(() => modal.classList.add("active"), 100);

  modal.querySelector(".confirm-booking").addEventListener("click", async () => {
    const date = modal.querySelector("#appointment-date").value;
    const time = modal.querySelector("#appointment-time").value;

    if (!date || !time) {
      alert("Please select both date and time.");
      return;
    }

    const token = localStorage.getItem("token");
    const startTime = time.split("-")[0];
    const appointment = {
      doctor: { id: doctor.id },
      patient: { id: patient.id },
      appointmentTime: `${date}T${startTime}:00`,
      status: 0,
    };

    try {
      const { success, message } = await bookAppointment(appointment, token);
      if (success) {
        alert("Appointment booked successfully!");
        ripple.remove();
        modal.remove();
      } else {
        alert("Failed to book appointment: " + message);
      }
    } catch (err) {
      console.error(err);
      alert("Error booking appointment.");
    }
  });
}

// Filter doctors
async function filterDoctorsOnChange() {
  const name = document.getElementById("searchBar")?.value.trim() || null;
  const time = document.getElementById("filterTime")?.value || null;
  const specialty = document.getElementById("filterSpecialty")?.value || null;

  try {
    const response = await filterDoctors(name, time, specialty);
    const doctors = response.doctors || [];
    if (doctors.length > 0) renderDoctorCards(doctors);
    else document.getElementById("content").innerHTML = "<p>No doctors found.</p>";
  } catch (err) {
    console.error(err);
    alert("Error filtering doctors.");
  }
}
