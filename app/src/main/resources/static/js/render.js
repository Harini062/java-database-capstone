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

document.addEventListener("DOMContentLoaded", () => {
    const adminBtn = document.getElementById('adminBtn');
    const doctorBtn = document.getElementById('doctorBtn');
    const patientBtn = document.getElementById('patientBtn');
  
    if (adminBtn) adminBtn.addEventListener('click', () => selectRole('admin'));
    if (doctorBtn) doctorBtn.addEventListener('click', () => selectRole('doctor'));
    if (patientBtn) patientBtn.addEventListener('click', () => selectRole('patient'));
  });
  