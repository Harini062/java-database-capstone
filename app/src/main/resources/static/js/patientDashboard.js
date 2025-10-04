import { getDoctors, filterDoctors } from './services/doctorServices.js';
import { openModal } from './components/modals.js';
import {renderHeader } from './components/header.js';
import { createDoctorCard } from './components/doctorCard.js';
import { patientSignup, patientLogin } from './services/patientServices.js';

document.addEventListener("DOMContentLoaded", () => {
  renderHeader();
});


document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();

  const signupBtn = document.getElementById("patientSignup");
  if (signupBtn) signupBtn.addEventListener("click", () => openModal("patientSignup"));

  const loginBtn = document.getElementById("patientLogin");
  if (loginBtn) loginBtn.addEventListener("click", () => openModal("patientLogin"));

  const searchBar = document.getElementById("searchBar");
  if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);

  const filterTime = document.getElementById("filterTime");
  if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);

  const filterSpecialty = document.getElementById("filterSpecialty");
  if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);
});


async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctors(doctors);
  } 
  catch (error) {
    console.error("Failed to load doctors:", error);
    document.getElementById("content").innerHTML = "<p>Error loading doctors.</p>";
  }
}


async function filterDoctorsOnChange() {
  const name = document.getElementById("searchBar")?.value.trim() || null;
  const time = document.getElementById("filterTime")?.value || null;
  const specialty = document.getElementById("filterSpecialty")?.value || null;

  try {
    const doctors = await filterDoctors(name, time, specialty);
    if (doctors && doctors.length > 0) {
      renderDoctors(doctors);
    } 
    else {
      document.getElementById("content").innerHTML = "<p>No doctors found with the given filters.</p>";
    }
  }
   catch (error) {
    console.error("Failed to filter doctors:", error);
    alert("An error occurred while filtering doctors.");
  }
}


function renderDoctors(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";
  doctors.forEach(doctor => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}


window.signupPatient = async function () {
  try {
    const name = document.getElementById("signupName").value;
    const email = document.getElementById("signupEmail").value;
    const password = document.getElementById("signupPassword").value;
    const phone = document.getElementById("signupPhone").value;
    const address = document.getElementById("signupAddress").value;

    const { success, message } = await patientSignup({ name, email, password, phone, address });
    if (success) {
      alert(message);
      document.getElementById("patientSignup")?.classList.remove("open"); // close modal
      window.location.reload();
    } 
    else {
      alert(message);
    }
  } 
  catch (error) {
    console.error("Signup failed:", error);
    alert("An error occurred while signing up.");
  }
};


window.loginPatient = async function () {
  try {
    const email = document.getElementById("loginEmail").value;
    const password = document.getElementById("loginPassword").value;

    const response = await patientLogin({ email, password });
    if (response.ok) {
      const result = await response.json();
      localStorage.setItem('token', result.token);
      alert(" Login successful!");
      window.location.href = '/pages/loggedPatientDashboard.html';
    }
    else {
      alert('Invalid credentials!');
    }
  } 
  catch (error) {
    console.error("Login error:", error);
    alert("Failed to login.");
  }
};
