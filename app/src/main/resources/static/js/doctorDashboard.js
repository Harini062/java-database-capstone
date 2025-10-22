import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";


const patientTableBody = document.getElementById("patientTableBody");
let selectedDate = new Date().toISOString().split("T")[0]; 
const token = localStorage.getItem("token");
let patientName ="";


document.getElementById("searchBar").addEventListener("input", (event) => {
  const value = event.target.value.trim();
  patientName = value;
  loadAppointments();
});


document.getElementById("todayButton").addEventListener("click", () => {
  selectedDate = new Date().toISOString().split("T")[0];
  document.getElementById("datePicker").value = selectedDate;
  loadAppointments();
});


document.getElementById("datePicker").addEventListener("change", (event) => {
  selectedDate = event.target.value;
  loadAppointments();
});


async function loadAppointments() {
  try {
    const data= await getAllAppointments(selectedDate, patientName, token);
    const appointments = data.appointments || [];

    
    patientTableBody.innerHTML = "";

    if (!appointments || appointments.length === 0) {
      const row = document.createElement("tr");
      row.innerHTML = `
        <td colspan="4" class="text-center py-4">No Appointments found for this date.</td>
      `;
      patientTableBody.appendChild(row);
      return;
    }

    appointments.forEach((appt) => {
      const patient = {
        id: appt.patientId,
        name: appt.patientName,
        phone: appt.patientPhone,
        email: appt.patientEmail,
      };
      const row = createPatientRow(patient,appt.id,appt.doctorId);
      patientTableBody.appendChild(row);
    });
  } 
  catch (err) {
    console.error("Error loading appointments:", err);
    patientTableBody.innerHTML = `
      <tr>
        <td colspan="4" class="text-center py-4 text-red-600">
          Error loading appointments. Try again later.
        </td>
      </tr>
    `;
  }
}


document.addEventListener("DOMContentLoaded", () => {
  document.getElementById("datePicker").value = selectedDate;
  loadAppointments();
});
