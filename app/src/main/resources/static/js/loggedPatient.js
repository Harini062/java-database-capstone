// loggedPatient.js
import { getDoctors, filterDoctors } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";
import { showBookingOverlay } from "./bookingOverlay.js";

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
    const contentDiv = document.getElementById("content");
    if (contentDiv) contentDiv.innerHTML = "<p>Error loading doctors.</p>";
  }
}

export function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  if (!contentDiv) return;

  contentDiv.innerHTML = "";


  let patient = null;
  try {
    const storedPatient = localStorage.getItem("loggedPatient");
    if (storedPatient) patient = JSON.parse(storedPatient);
  } catch (e) {
    console.warn("No logged-in patient found in localStorage");
    patient = null;
  }

  doctors.forEach((doctor) => {
    const card = createDoctorCard(doctor);

    const bookBtn = card.querySelector(".book-now");
    if (bookBtn) {
      bookBtn.addEventListener("click", (e) => {
        if (patient) {
          showBookingOverlay(e, doctor, patient);
        } else {
          alert("Please login first to book an appointment.");
        }
      });
    }

    contentDiv.appendChild(card);
  });
}

// Filter doctors
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
