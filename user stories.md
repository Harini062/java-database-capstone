#  User Stories  

##  Admin User Stories  

### **User Story 1: Admin Login**  
**Title:** As an admin, I want to log into the portal with my username and password, so that I can manage the platform securely.  
**Acceptance Criteria:**  
- Admin can enter a valid username and password to log in.  
- Invalid login attempts show an appropriate error message.  
- After login, the admin is redirected to the Admin Dashboard.
  
**Priority:** High  
**Story Points:** 3  
**Notes:** Consider handling multiple failed login attempts by temporarily locking the account.  

---

### **User Story 2: Admin Logout**  
**Title:** As an admin, I want to log out of the portal, so that I can protect access to the system.  
**Acceptance Criteria:**  
- Logout button is accessible from the Admin Dashboard.  
- Clicking logout ends the session and redirects to the login page.  
- Session cookies are invalidated after logout.
  
**Priority:** High  
**Story Points:** 2  
**Notes:** Ensure browser back button does not allow access to restricted pages after logout.  

---

### **User Story 3: Add Doctor**  
**Title:** As an admin, I want to add doctors to the portal, so that new doctors can access the system.  
**Acceptance Criteria:**  
- Admin can access a form to enter doctor details (name, email, specialization, etc.).  
- Submitted data is validated before saving to the database.  
- A confirmation message is shown upon successful creation.
  
**Priority:** High  
**Story Points:** 5  
**Notes:** Duplicate email addresses should not be allowed.  

---

### **User Story 4: Delete Doctor**  
**Title:** As an admin, I want to delete a doctor’s profile from the portal, so that outdated or inactive profiles can be removed.  
**Acceptance Criteria:**  
- Admin can view a list of doctors with delete options.  
- Clicking delete removes the doctor from the system.  
- A confirmation dialog appears before deletion.
  
**Priority:** Medium  
**Story Points:** 3  
**Notes:** Prevent deletion if the doctor has future appointments. Consider soft delete if data retention is needed.  

---

### **User Story 5: Run Stored Procedure for Reports**  
**Title:** As an admin, I want to run a stored procedure in the MySQL CLI to get the number of appointments per month, so that I can track usage statistics.  
**Acceptance Criteria:**  
- A stored procedure exists to return appointment counts grouped by month.  
- Admin can manually execute the procedure via MySQL CLI.  
- Results are displayed in a readable format.
  
**Priority:** Medium  
**Story Points:** 2  
**Notes:** Display these stats on the dashboard in the future.  

---

##  Patient User Stories  

### **User Story 1: View Doctors Publicly**  
**Title:** As a patient, I want to view a list of doctors without logging in, so that I can explore my options before registering.  
**Acceptance Criteria:**  
- Patients can access a public page listing all available doctors.  
- The list includes each doctor's name, specialization, and availability.  
- No login or registration is required to view this list.
  
**Priority:** High  
**Story Points:** 3  
**Notes:** If no doctors are currently available, display a friendly message.  

---

### **User Story 2: Patient Registration**  
**Title:** As a patient, I want to sign up using my email and password, so that I can book appointments.  
**Acceptance Criteria:**  
- Patients can access a registration form.  
- Email and password must be validated before account creation.  
- After successful registration, the patient is redirected to the login page or logged in automatically.
  
**Priority:** High  
**Story Points:** 3  
**Notes:** Prevent registration with duplicate emails. Require a strong password.  

---

### **User Story 3: Patient Login**  
**Title:** As a patient, I want to log into the portal, so that I can manage my bookings.  
**Acceptance Criteria:**  
- Patients can log in with their registered email and password.  
- After login, patients are taken to their dashboard.  
- Incorrect credentials show an appropriate error message.
  
**Priority:** High  
**Story Points:** 2  
**Notes:** Provide feedback for locked or deactivated accounts. Include “Forgot Password” feature.  

---

### **User Story 4: Patient Logout**  
**Title:** As a patient, I want to log out of the portal, so that I can secure my account.  
**Acceptance Criteria:**  
- A logout button is available on the patient dashboard.  
- Clicking it ends the session and redirects to the homepage or login screen.  
- No restricted pages are accessible after logout.
  
**Priority:** High  
**Story Points:** 2  
**Notes:** Ensure that sessions are securely terminated.  

---

### **User Story 5: Book Appointment**  
**Title:** As a patient, I want to log in and book an hour-long appointment, so that I can consult with a doctor.  
**Acceptance Criteria:**  
- Patient can view available time slots for doctors after logging in.  
- Patient can select a one-hour time slot and confirm the appointment.  
- The selected time is blocked and saved after booking.
  
**Priority:** High  
**Story Points:** 5  
**Notes:** Prevent double booking for the same time slot. Show confirmation message and allow cancellation or rescheduling.  

---

### **User Story 6: View Upcoming Appointments**  
**Title:** As a patient, I want to view my upcoming appointments, so that I can prepare accordingly.  
**Acceptance Criteria:**  
- Patients can access a list of future appointments from their dashboard.  
- Each appointment displays date, time, and doctor details.  
- Only future (not past) appointments are shown.
  
**Priority:** Medium  
**Story Points:** 3  
**Notes:** Display a message when no upcoming appointments exist.  

---

##  Doctor User Stories  

### **User Story 1: Doctor Login**  
**Title:** As a doctor, I want to log into the portal, so that I can manage my appointments.  
**Acceptance Criteria:**  
- Doctor can log in using registered email and password.  
- Upon login, the doctor is redirected to their dashboard.  
- Incorrect login credentials show appropriate error messages.
  
**Priority:** High  
**Story Points:** 2  
**Notes:** Allow "Forgot Password" functionality for account recovery.  

---

### **User Story 2: Doctor Logout**  
**Title:** As a doctor, I want to log out of the portal, so that I can protect my data.  
**Acceptance Criteria:**  
- A logout button is available on the doctor dashboard.  
- Logging out ends the session and redirects to the login or home page.  
- Session cookies are cleared and protected content is no longer accessible.
  
**Priority:** High  
**Story Points:** 2  
**Notes:** Session timeout should also auto-logout the user securely. Display a message confirming logout.  

---

### **User Story 3: View Appointment Calendar**  
**Title:** As a doctor, I want to view my appointment calendar, so that I can stay organized.  
**Acceptance Criteria:**  
- The doctor dashboard shows a calendar view with all scheduled appointments.  
- Each appointment includes patient name, date, and time.  
- The calendar is filtered by day, week, or month.
  
**Priority:** High  
**Story Points:** 5  
**Notes:** Display message when no appointments are scheduled.  

---

### **User Story 4: Mark Unavailability**  
**Title:** As a doctor, I want to mark my unavailability, so that patients can only book from my available slots.  
**Acceptance Criteria:**  
- Doctor can mark dates/times as unavailable through the dashboard.  
- Unavailable slots are removed from the patient booking interface.  
- Existing appointments during blocked times trigger a warning or require rescheduling.
  
**Priority:** High  
**Story Points:** 5  
**Notes:** Prevent overlapping between marked unavailability and existing appointments.  

---

### **User Story 5: Update Profile**  
**Title:** As a doctor, I want to update my profile with specialization and contact information, so that patients have up-to-date information.  
**Acceptance Criteria:**  
- Doctor can edit profile details including name, specialization, contact, and bio.  
- Changes are saved and reflected immediately on the public doctor list.  
- Fields are validated before submission.
  
**Priority:** Medium  
**Story Points:** 3  
**Notes:** Avoid blank or invalid entries (e.g., invalid phone number format).  

---

### **User Story 6: View Patient Details**  
**Title:** As a doctor, I want to view patient details for upcoming appointments, so that I can be prepared.  
**Acceptance Criteria:**  
- Doctor can view a list of upcoming appointments from their dashboard.  
- Clicking on an appointment shows patient details (name, age, reason for visit, etc.).  
- Only authorized data is shown for privacy compliance.
  
**Priority:** High  
**Story Points:** 4  
**Notes:** Ensure data is only visible to the assigned doctor.  
