// PatientDashboard.js
import { getDoctors, filterDoctors } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';
import { renderHeader } from './components/header.js';
import { openModal } from './components/modals.js';
import { patientSignup, patientLogin } from "./services/patientServices.js";

document.addEventListener("DOMContentLoaded", async () => {
  // Render header first
  renderHeader();

  // Load doctors initially
  await loadDoctorCards();

  // Filter/search event listeners
  const searchBar = document.getElementById("searchBar");
  const filterTime = document.getElementById("filterTime");
  const filterSpecialty = document.getElementById("filterSpecialty");

  if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
  if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);
  if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);

  // Header login/signup buttons
  const loginBtn = document.getElementById("patientLogin");
  const signupBtn = document.getElementById("patientSignup");

  if (loginBtn) loginBtn.addEventListener("click", () => openModal("patientLogin"));
  if (signupBtn) signupBtn.addEventListener("click", () => openModal("patientSignup"));
});

async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctors(doctors);
  } catch (err) {
    console.error("Failed to load doctors:", err);
    const contentDiv = document.getElementById("content");
    if (contentDiv) contentDiv.innerHTML = "<p>Error loading doctors.</p>";
  }
}

async function filterDoctorsOnChange() {
  const name = document.getElementById("searchBar")?.value.trim() || null;
  const time = document.getElementById("filterTime")?.value || null;
  const specialty = document.getElementById("filterSpecialty")?.value || null;

  try {
    const response = await filterDoctors(name, time, specialty);
    const doctors = response.doctors || [];
    if (doctors.length > 0) renderDoctors(doctors);
    else {
      const contentDiv = document.getElementById("content");
      if (contentDiv) contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
    }
  } catch (err) {
    console.error("Error filtering doctors:", err);
    alert("An error occurred while filtering doctors.");
  }
}

function renderDoctors(doctors) {
  const contentDiv = document.getElementById("content");
  if (!contentDiv) return;

  contentDiv.innerHTML = "";
  doctors.forEach((doctor) => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}


// SIGNUP
window.signupPatient = async function () {
  try {
    const name = document.getElementById("signupName").value;
    const email = document.getElementById("signupEmail").value;
    const password = document.getElementById("signupPassword").value;

    const response = await patientSignup({ name, email, password });
    if (response.ok) {
      alert("Signup successful! Please log in.");
      document.getElementById("signupForm").reset();
    } else {
      alert("Signup failed. Try again.");
    }
  } catch (error) {
    console.error("Signup error:", error);
    alert("Failed to sign up.");
  }
};


// LOGIN
window.loginPatient = async function () {
  try {
    const email = document.getElementById("loginEmail").value;
    const password = document.getElementById("loginPassword").value;

    const response = await patientLogin({ email, password });
    if (response.ok) {
      const result = await response.json();

      // store token + role
      localStorage.setItem("token", result.token);
      localStorage.setItem("userRole", "loggedPatient"); 

      alert("Login successful!");
      window.location.href = "/pages/loggedPatientDashboard.html";
    } else {
      alert("Invalid credentials!");
    }
  } catch (error) {
    console.error("Login error:", error);
    alert("Failed to login.");
  }
};