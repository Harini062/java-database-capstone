// render.js
import { openModal } from "./components/modals.js";

export function selectRole(role) {
  setRole(role);
  const token = localStorage.getItem('token');

  switch(role) {
    case "admin":
      if (token) {
        window.location.href = `/adminDashboard/${token}`;
      } else {
        openModal('adminLogin');
      }
      break;

    case "doctor":
      if (token) {
        window.location.href = `/doctorDashboard/${token}`;
      } else {
        openModal('doctorLogin');
      }
      break;

    case "patient":
      window.location.href = "/pages/patientDashboard.html";
      break;

    case "loggedPatient":
      if (token) {
        window.location.href = "/pages/loggedPatientDashboard.html";
      } else {
        openModal('patientLogin');
      }
      break;

    default:
      console.error("Unknown role:", role);
  }
}
document.getElementById('adminBtn').addEventListener('click', () => selectRole('admin'));
document.getElementById('doctorBtn').addEventListener('click', () => selectRole('doctor'));
document.getElementById('patientBtn').addEventListener('click', () => selectRole('patient'));