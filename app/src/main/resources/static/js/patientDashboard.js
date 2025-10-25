// PatientDashboard.js
import { getDoctors, filterDoctors } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';
import { renderHeader } from './components/header.js';
import { openModal,closeModal } from './components/modals.js';
import { patientSignup, patientLogin } from "./services/patientServices.js";

document.addEventListener("DOMContentLoaded", async () => {
  renderHeader();
  setTimeout(() => {
    const searchBar = document.getElementById("searchBar");
    const filterTime = document.getElementById("filterTime");
    const filterSpecialty = document.getElementById("filterSpecialty");

    if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
    if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);
    if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);


    const loginBtn = document.getElementById("patientLogin");
    const signupBtn = document.getElementById("patientSignup");

    if (loginBtn) loginBtn.addEventListener("click", () => openModal("patientLogin"));
    if (signupBtn) signupBtn.addEventListener("click", () => openModal("patientSignup"));
  }, 50); 

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

// SIGNUP
window.signupPatient = async function () {
  try {
    const name = document.getElementById("signupName").value;
    const email = document.getElementById("signupEmail").value;
    const password = document.getElementById("signupPassword").value;
    const phone = document.getElementById("signupPhone").value.trim();
    const address = document.getElementById("signupAddress").value.trim();

    const response = await patientSignup({ name, email, password, phone, address });
    console.log("Signup response:", response);
    if (response.success) {
      alert("Signup successful! Please log in.");
      const signupForm =document.getElementById("signupForm");
      if (signupForm) signupForm.reset();
    } else {
      alert(`Signup failed: ${response.message}`);
      
    }
  } catch (error) {
    console.error("Signup error:", error);
    alert("Failed to sign up.");
  }
  closeModal("modal");
};

// Login 
window.loginPatient = async function () {
  try {
    const email = document.getElementById("loginEmail").value;
    const password = document.getElementById("loginPassword").value;

    const response = await patientLogin({ email, password });
    if (response.ok) {
      const result = await response.json();


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
