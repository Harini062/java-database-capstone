import { openModal, closeModal } from "./components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";

// Wait for DOM
document.addEventListener("DOMContentLoaded", () => {
  const addBtn = document.getElementById("addDocBtn");
  if (addBtn) addBtn.addEventListener("click", () => openModal("addDoctor"));

  const searchBar = document.getElementById("searchBar");
  if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);

  const filterTime = document.getElementById("filterTime");
  if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);

  const filterSpecialty = document.getElementById("filterSpecialty");
  if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);

  const addForm = document.getElementById("addDoctorForm");
  if (addForm) addForm.addEventListener("submit", adminAddDoctor);

  loadDoctorCards();
});


// Load all doctors
async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (err) {
    console.error("Error loading doctors:", err);
    alert("Failed to load doctors.");
  }
}

// Render doctor cards
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  if (!contentDiv) return;

  contentDiv.innerHTML = "";

  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML = "<p>No doctors found</p>";
    return;
  }

  doctors.forEach((doctor) => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

// Filters
async function filterDoctorsOnChange() {
  try {
    const name = document.getElementById("searchBar")?.value.trim() || null;
    const time = document.getElementById("filterTime")?.value || null;
    const specialty = document.getElementById("filterSpecialty")?.value || null;

    const doctors = await filterDoctors(name, time, specialty);

    if (doctors?.length > 0) {
      renderDoctorCards(doctors);
    } else {
      const contentDiv = document.getElementById("content");
      if (contentDiv) contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
    }
  } catch (err) {
    console.error("Error filtering doctors:", err);
    alert("Error applying filters.");
  }
}

// Add Doctor
window.adminAddDoctor =async function(event) {
  event.preventDefault();

  try {
    const name = document.getElementById("doctorName").value.trim();
    const email = document.getElementById("doctorEmail").value.trim();
    const password = document.getElementById("doctorPassword").value.trim();
    const mobile = document.getElementById("doctorMobile").value.trim();
    const specialty = document.getElementById("doctorSpecialty").value.trim();
    const availability = Array.from(document.querySelectorAll("input[name='availability']:checked"))
      .map(el => el.value);

    const token = localStorage.getItem("token");
    if (!token) return alert("Unauthorized: Please login as admin.");

    const doctor = { name, email, password, mobile, specialty, availability };
    const result = await saveDoctor(doctor, token);

    if (result?.success) {
      alert("Doctor added successfully!");
      closeModal("addDoctor");
      loadDoctorCards();
    } else {
      alert(result?.message || "Failed to add doctor.");
    }
  } catch (err) {
    console.error("Error adding doctor:", err);
    alert("Something went wrong while adding the doctor.");
  }
}
