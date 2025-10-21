// modals.js
export function openModal(type) {
    let modalContent = '';
  
    switch(type) {
      case 'addDoctor':
        modalContent = `
          <h2>Add Doctor</h2>
          <form id="addDoctorForm">
          <label for="doctorName">Doctor Name</label>
          <input type="text" id="doctorName" placeholder="Doctor Name" class="input-field">
          <label for="doctorSpecialty">Specialization</label>
          <select id="doctorSpecialty" class="input-field select-dropdown">
            <option value="">--Select specialization---</option>
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
          <label for="doctorEmail">Email</label>
          <input type="email" id="doctorEmail" placeholder="Email" class="input-field">
          <label for="doctorPassword">Password</label>
          <input type="password" id="doctorPassword" placeholder="Password" class="input-field">
          <label for="doctorPhone">Phone</label>
          <input type="text" id="doctorPhone" placeholder="Phone No." class="input-field">
          <div class="availability-container">
            <label class="availabilityLabel">Select Availability:</label>
            <div class="checkbox-group">
              <label><input type="checkbox" name="availability" value="09:00-10:00"> 9:00 AM - 10:00 AM</label>
              <label><input type="checkbox" name="availability" value="10:00-11:00"> 10:00 AM - 11:00 AM</label>
              <label><input type="checkbox" name="availability" value="11:00-12:00"> 11:00 AM - 12:00 PM</label>
              <label><input type="checkbox" name="availability" value="12:00-13:00"> 12:00 PM - 01:00 PM</label>
              <label><input type="checkbox" name="availability" value="13:00-14:00"> 01:00 PM - 02:00 PM</label>
              <label><input type="checkbox" name="availability" value="14:00-15:00"> 02:00 PM - 03:00 PM</label>
              <label><input type="checkbox" name="availability" value="15:00-16:00"> 03:00 PM - 04:00 PM</label>
              <label><input type="checkbox" name="availability" value="16:00-17:00"> 04:00 PM - 05:00 PM</label>
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
          <form id="signupForm">
          <input type="text" id="signupName" placeholder="Name" class="input-field">
          <input type="email" id="signupEmail" placeholder="Email" class="input-field">
          <input type="password" id="signupPassword" placeholder="Password" class="input-field">
          <input type="text" id="signupPhone" placeholder="Phone" class="input-field">
          <input type="text" id="signupAddress" placeholder="Address" class="input-field">
          <button type="button" class="dashboard-btn" id="signupBtn">Signup</button>
          </form>
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
    document.body.classList.remove("modal-open");
}

  