// bookingOverlay.js
import { bookAppointment } from "./services/appointmentRecordService.js";

export function showBookingOverlay(e, doctor, patient) {
  if (!patient) {
    alert("No logged-in patient found.");
    return;
  }

  // Ripple effect
  const ripple = document.createElement("div");
  ripple.classList.add("ripple-overlay");
  ripple.style.left = `${e.clientX}px`;
  ripple.style.top = `${e.clientY}px`;
  document.body.appendChild(ripple);
  setTimeout(() => ripple.classList.add("active"), 50);

  // Modal overlay
  const modalApp = document.createElement("div");
  modalApp.classList.add("modalApp");

  modalApp.innerHTML = `
    <h2>Book Appointment</h2>
    <input class="input-field" type="text" value="${patient.name}" disabled />
    <input class="input-field" type="text" value="${doctor.name}" disabled />
    <input class="input-field" type="text" value="${doctor.specialty}" disabled/>
    <input class="input-field" type="email" value="${doctor.email}" disabled/>
    <input class="input-field" type="date" id="appointment-date" />
    <select class="input-field" id="appointment-time">
      <option value="">Select time</option>
      ${doctor.availableTimes.map(t => `<option value="${t}">${t}</option>`).join('')}
    </select>
    <button class="confirm-booking">Confirm Booking</button>
  `;

  document.body.appendChild(modalApp);
  setTimeout(() => modalApp.classList.add("active"), 100);

  // Confirm booking button
  modalApp.querySelector(".confirm-booking").addEventListener("click", async () => {
    const date = modalApp.querySelector("#appointment-date").value;
    const time = modalApp.querySelector("#appointment-time").value;
    const token = localStorage.getItem("token");

    if (!token) {
      alert("Please login first to book an appointment.");
      return;
    }

    if (!date || !time) {
      alert("Please select both date and time.");
      return;
    }

    const startTime = time.split('-')[0];
    const appointment = {
      doctor: { id: doctor.id },
      patient: { id: patient.id },
      appointmentTime: `${date}T${startTime}:00`,
      status: 0
    };

    try {
      const { success, message } = await bookAppointment(appointment, token);
      if (success) {
        alert("Appointment booked successfully!");
        ripple.remove();
        modalApp.remove();
      } else {
        alert("Failed to book appointment: " + message);
      }
    } catch (error) {
      console.error("Booking error:", error);
      alert("An error occurred while booking the appointment.");
    }
  });
}
