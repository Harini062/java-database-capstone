function renderHeader() {
  const headerDiv = document.getElementById("header");

  
  if (window.location.pathname.endsWith("/")) {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");

    headerDiv.innerHTML = `
      <header class="header">
        <div class="logo-section">
          <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
          <span class="logo-title">Hospital CMS</span>
        </div>
      </header>`;
    return;
  }

  
  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  }

  let headerContent = `
    <header class="header">
      <div class="logo-section">
        <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
        <span class="logo-title">Hospital CMS</span>
      </div>
      <nav>
  `;

  if (role === "admin") {
    headerContent += `
      <button id="addDocBtn" class="adminBtn">Add Doctor</button>
      <a href="#" id="logoutBtn">Logout</a>`;
  } else if (role === "doctor") {
    headerContent += `
      <button id="doctorHomeBtn" class="adminBtn">Home</button>
      <a href="#" id="logoutBtn">Logout</a>`;
  } else if (role === "patient") {
    headerContent += `
      <button id="patientLogin" class="adminBtn">Login</button>
      <button id="patientSignup" class="adminBtn">Sign Up</button>`;
  } else if (role === "loggedPatient") {
    headerContent += `
      <button id="home" class="adminBtn">Home</button>
      <button id="patientAppointments" class="adminBtn">Appointments</button>
      <a href="#" id="logoutPatientBtn">Logout</a>`;
  }

  headerContent += `
      </nav>
    </header>
  `;

  headerDiv.innerHTML = headerContent;

  attachHeaderButtonListeners();
}


function attachHeaderButtonListeners() {
  
  const addDocBtn = document.getElementById("addDocBtn");
  if (addDocBtn) {
    addDocBtn.addEventListener("click", () => openModal("addDoctor"));
  }

  
  const loginBtn = document.getElementById("patientLogin");
  if (loginBtn) {
    loginBtn.addEventListener("click", () => openModal("patientLogin"));
  }

  const signupBtn = document.getElementById("patientSignup");
  if (signupBtn) {
    signupBtn.addEventListener("click", () => openModal("patientSignup"));
  }

    const doctorHomeBtn = document.getElementById("doctorHomeBtn");
  if (doctorHomeBtn) {
    doctorHomeBtn.addEventListener("click", () => {
      window.location.href = "/pages/doctorDashboard.html";
    });
  }

  // LoggedPatient navigation
  const homeBtn = document.getElementById("home");
  if (homeBtn) {
    homeBtn.addEventListener("click", () => {
      window.location.href = "/pages/loggedPatientDashboard.html";
    });
  }

  const patientAppointments = document.getElementById("patientAppointments");
  if (patientAppointments) {
    patientAppointments.addEventListener("click", () => {
      window.location.href = "/pages/patientAppointments.html";
    });
  }

  // Logout buttons
  const logoutBtn = document.getElementById("logoutBtn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", logout);
  }

  const logoutPatientBtn = document.getElementById("logoutPatientBtn");
  if (logoutPatientBtn) {
    logoutPatientBtn.addEventListener("click", logoutPatient);
  }
}

 
function logout() {
  localStorage.removeItem("userRole");
  localStorage.removeItem("token");
  window.location.href = "/";
}

 
function logoutPatient() {
  localStorage.removeItem("token");
  localStorage.setItem("userRole", "patient");
  window.location.href = "/pages/patientDashboard.html";
}

 
document.addEventListener("DOMContentLoaded", renderHeader);
