import { openModal,closeModal } from "./components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";


document.addEventListener("DOMContentLoaded", () => {
    const addDocBtn = document.getElementById("addDocBtn");
    if(addDocBtn) {
        addEventListener("click", () => openModal("addDoctor"));
    }
  });
  

document.addEventListener("DOMContentLoaded", loadDoctorCards);

async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        renderDoctorCards(doctors);
    } 
    catch (err) {
        console.error("Error loading doctors:", err);
        alert("Failed to load doctors. Please try again later.");
    }
}


function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");
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


document.getElementById("searchBar").addEventListener("input", filterDoctorsOnChange);
document.getElementById("filterTime").addEventListener("change", filterDoctorsOnChange);
document.getElementById("filterSpecialty").addEventListener("change", filterDoctorsOnChange);

async function filterDoctorsOnChange() {
  try {
    const name = document.getElementById("searchBar").value.trim() || null;
    const time = document.getElementById("filterTime").value || null;
    const specialty = document.getElementById("filterSpecialty").value || null;

    const doctors = await filterDoctors(name, time, specialty);

    if (doctors && doctors.length > 0) {
      renderDoctorCards(doctors);
    } 
    else {
      const contentDiv = document.getElementById("content");
      contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
    }
  } 
  catch (err) {
    console.error("Error filtering doctors:", err);
    alert("Error applying filters. Please try again.");
  }
}
document.addEventListener("DOMContentLoaded", () => {
    const addDocForm = document.getElementById("addDocForm");
    if(addDocForm) {
        addEventListener("submit", adminAddDoctor);
    }
  });


async function adminAddDoctor(event) {
  event.preventDefault();

  try {
    const name = document.getElementById("doctorName").value.trim();
    const email = document.getElementById("doctorEmail").value.trim();
    const password = document.getElementById("doctorPassword").value.trim();
    const mobile = document.getElementById("doctorMobile").value.trim();
    const specialty = document.getElementById("doctorSpecialty").value.trim();

    const availability = Array.from(document.querySelectorAll("input[name='availability']:checked"))
      .map((el) => el.value);

    const token = localStorage.getItem("token");
    if (!token) {
      alert("Unauthorized: Please log in as admin.");
      return;
    }

    const doctor = { name, email, password, mobile, specialty, availability };

    const result = await saveDoctor(doctor, token);

    if (result && result.success) {
      alert("Doctor added successfully!");
      closeModal("addDoctor");
      loadDoctorCards();
    } 
    else {
      alert(result.message || "Failed to add doctor.");
    }
  } 
  catch (err) {
    console.error("Error adding doctor:", err);
    alert("Something went wrong while adding the doctor.");
  }
}
