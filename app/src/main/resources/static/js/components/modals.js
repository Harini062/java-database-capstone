// modals.js
export function openModal(type) {
    let modalContent = '';
  
    switch(type) {
      case 'addDoctor':
        modalContent = `
          <h2>Add Doctor</h2>
          <form id="addDoctorForm">
          <input type="text" id="doctorName" placeholder="Doctor Name" class="input-field">
          <select id="doctorSpecialty" class="input-field select-dropdown">
            <option value="">Specialization</option>
            <option value="cardiologist">Cardiologist</option>
            <option value="dermatologist">Dermatologist</option>
            <option value="neurologist">Neurologist</option>
            <option value="pediatrician">Pediatrician</option>
            <option value="orthopedic">Orthopedic</option>
            <option value="gynecologist">Gynecologist</option>
            <option value="psychiatrist">Psychiatrist</option>
            <option value="dentist">Dentist</option>
            <option value="ophthalmologist">Ophthalmologist</option>
            <option value="ent">ENT Specialist</option>
            <option value="urologist">Urologist</option>
            <option value="oncologist">Oncologist</option>
            <option value="gastroenterologist">Gastroenterologist</option>
            <option value="general">General Physician</option>
          </select>
          <input type="email" id="doctorEmail" placeholder="Email" class="input-field">
          <input type="password" id="doctorPassword" placeholder="Password" class="input-field">
          <input type="text" id="doctorMobile" placeholder="Mobile No." class="input-field">
          <div class="availability-container">
            <label class="availabilityLabel">Select Availability:</label>
            <div class="checkbox-group">
              <label><input type="checkbox" name="availability" value="09:00-10:00"> 9:00 AM - 10:00 AM</label>
              <label><input type="checkbox" name="availability" value="10:00-11:00"> 10:00 AM - 11:00 AM</label>
              <label><input type="checkbox" name="availability" value="11:00-12:00"> 11:00 AM - 12:00 PM</label>
              <label><input type="checkbox" name="availability" value="12:00-13:00"> 12:00 PM - 1:00 PM</label>
            </div>
          </div>
          <button type="submit" class="dashboard-btn" id="saveDoctorBtn">Save</button>
          </form>
        `;
        break;
  
      case 'patientLogin':
        modalContent = `
          <h2>Patient Login</h2>
          <input type="text" id="loginEmail" placeholder="Email" class="input-field">
          <input type="password" id="loginPassword" placeholder="Password" class="input-field">
          <button class="dashboard-btn" id="loginBtn">Login</button>
        `;
        break;
  
      case 'patientSignup':
        modalContent = `
          <h2>Patient Signup</h2>
          <input type="text" id="signupName" placeholder="Name" class="input-field">
          <input type="email" id="signupEmail" placeholder="Email" class="input-field">
          <input type="password" id="signupPassword" placeholder="Password" class="input-field">
          <input type="text" id="signupPhone" placeholder="Phone" class="input-field">
          <input type="text" id="signupAddress" placeholder="Address" class="input-field">
          <button class="dashboard-btn" id="signupBtn">Signup</button>
        `;
        break;
  
      case 'adminLogin':
        modalContent = `
          <h2>Admin Login</h2>
          <input type="text" id="adminEmail" placeholder="Email" class="input-field">
          <input type="password" id="adminPassword" placeholder="Password" class="input-field">
          <button class="dashboard-btn" id="adminLoginBtn">Login</button>
        `;
        break;
  
      case 'doctorLogin':
        modalContent = `
          <h2>Doctor Login</h2>
          <input type="text" id="doctorEmail" placeholder="Email" class="input-field">
          <input type="password" id="doctorPassword" placeholder="Password" class="input-field">
          <button class="dashboard-btn" id="doctorLoginBtn">Login</button>
        `;
        break;
  
      default:
        console.error("Unknown modal type:", type);
        return;
    }
  
    // Insert content and display modal
    const modal = document.getElementById('modal');
    const modalBody = document.getElementById('modal-body');
  
    if (!modal || !modalBody) return;
  
    modalBody.innerHTML = modalContent;
    modal.style.display = 'block';
    document.body.classList.add('modal-open');
  
    // Close button
    const closeBtn = document.getElementById('closeModal');
    if (closeBtn) closeBtn.onclick = () => {
      modal.style.display = 'none';
      document.body.classList.remove('modal-open');
    };
  
    // Attach handlers
    if (type === "patientSignup") document.getElementById("signupBtn")?.addEventListener("click", signupPatient);
    if (type === "patientLogin") document.getElementById("loginBtn")?.addEventListener("click", loginPatient);
    if (type === "addDoctor") document.getElementById("saveDoctorBtn")?.addEventListener("click", adminAddDoctor);
    if (type === "adminLogin") document.getElementById("adminLoginBtn")?.addEventListener("click", adminLoginHandler);
    if (type === "doctorLogin") document.getElementById("doctorLoginBtn")?.addEventListener("click", doctorLoginHandler);
  }

export function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.style.display = "none";
}

  