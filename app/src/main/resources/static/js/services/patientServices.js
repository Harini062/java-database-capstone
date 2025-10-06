import { API_BASE_URL } from "../config/config.js";

const PATIENT_API = API_BASE_URL + 'patient';

// Signup
export async function patientSignup(data) {
  try {
    const response = await fetch(PATIENT_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data)
    });

    const result = await response.json();
    return { success: response.ok, message: result.message };
  } catch (err) {
    console.error("Error during patient signup:", err);
    return { success: false, message: "Something went wrong during signup" };
  }
}

// Login
export async function patientLogin(data) {
  try {
    const response = await fetch(`${PATIENT_API}/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data)
    });
    return response;
  } catch (err) {
    console.error("Error during patient login:", err);
    return null;
  }
}

// Get patient data by token
export async function getPatientData(token) {
  try {
    const response = await fetch(`${PATIENT_API}/details/${token}`, {
      method: "GET",
      headers: { "Content-Type": "application/json" }
    });

    if (!response.ok) throw new Error("Failed to fetch patient data");
    return await response.json();
  } catch (err) {
    console.error("Failed to fetch patient data", err);
    return null;
  }
}

// Appointments
export async function getPatientAppointments(id, token) {
  try {
    const response = await fetch(`${PATIENT_API}/appointments/${id}/${token}`, {
      method: "GET",
      headers: { "Content-Type": "application/json" }
    });
    if (!response.ok) throw new Error("Failed to fetch appointments");
    return await response.json();
  } catch (err) {
    console.error("Error fetching appointments:", err);
    return null;
  }
}
