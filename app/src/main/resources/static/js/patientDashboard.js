// PatientDashboard.js
import { getDoctors, filterDoctors } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';
import { renderHeader } from './components/header.js';
import { openModal } from './components/modals.js';
import { patientSignup, patientLogin } from "./services/patientServices.js";

document.addEventListener("DOMContentLoaded", async () => {
  // Render header first
  renderHeader();

  // Wait until header is in DOM before attaching event listeners
  setTimeout(() => {
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
  }, 50); // small delay to ensure DOM exists

  // Load doctors initially
  await loadDoctorCards();
});

// Load doctors
async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (err) {
    console.error("Failed to load doctors:", err);
    const contentDiv = document.getElementById("content");
    if (contentDiv) contentDiv.innerHTML = "<p>Error loading doctors.</p>";
  }
}

// Filter doctors on input/change
async function filterDoctorsOnChange() {
  const name = document.getElementById("searchBar")?.value.trim() || "";
  const time = document.getElementById("filterTime")?.value || "";
  const specialty = document.getElementById("filterSpecialty")?.value || "";

  try {
    let doctors = [];
    if (!name && !time && !specialty) {
      // If all filters cleared, load all doctors
      doctors = await getDoctors();
    } else {
      doctors = await filterDoctors(name, time, specialty);
    }

    if (doctors.length > 0) renderDoctorCards(doctors);
    else document.getElementById("content").innerHTML = "<p>No doctors found.</p>";
  } catch (err) {
    console.error(err);
    alert("Error filtering doctors.");
  }
}

// Render doctor cards
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  if (!contentDiv) return;

  contentDiv.innerHTML = "";
  doctors.forEach((doctor) => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

// --- SIGNUP ---
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

// --- LOGIN ---
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

      // Save patient info for booking
      localStorage.setItem("loggedPatient", JSON.stringify(result.patient));

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
