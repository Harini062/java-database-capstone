import { API_BASE_URL } from "../config/config.js";

const PATIENT_API = API_BASE_URL + '/patient';

export async function patientSignup(data) {
    try {
        const repsonse = await fetch(PATIENT_API, {
            method : "POST",
            headers : {"Content-Type": "application/json" },
            body: JSON.stringify(data)
        });

        if(Response.ok){
            const result = await repsonse.json();
            return { success : true, message : result.message || "Signup successful" };
        }
        else {
            const errorData = await response.json();
            return { success: false, message: errorData.message || "Signup failed" };
        }
    }
    catch(err) {
        console.error("Error during patient signup:", err);
        return { success: false, message: "Something went wrong during signup" };
    }
}

export async function patientLogin(data) {
    try {
        const response = await fetch(`${PATIENT_API}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
        });

        return response;
    } 
    catch (err) {
        console.error("Error during patient login:", err);
        return null;
    }
}


export async function getPatientData(token) {
    try {
        const response = await fetch(`${PATIENT_API}/me`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
        });

        if (response.ok) {
            const patient = await response.json();
            return patient;
        } 
        else {
            console.error("Failed to fetch patient data");
            return null;
        }
    } 
    catch (err) {
        console.error("Error fetching patient data:", err);
        return null;
  }
}


export async function getPatientAppointments(id, token, user) {
    try {
        const url = `${PATIENT_API}/${user}/${id}/appointments`;
        const response = await fetch(url, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
        });

        if (response.ok) {
            const appointments = await response.json();
            return appointments;
        } 
        else {
            console.error("Failed to fetch appointments");
            return null;
        }
    } 
    catch (err) {
        console.error("Error fetching appointments:", err);
        return null;
  }
}


export async function filterAppointments(condition = "", name = "", token) {
    try {
        const query = new URLSearchParams();

        if (condition) query.append("condition", condition);
        if (name) query.append("name", name);

        const url = `${PATIENT_API}/appointments/filter?${query.toString()}`;

        const response = await fetch(url, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
        });

        if (response.ok) {
            const data = await response.json();
            return data || [];
        } 
        else {
            console.error("Failed to filter appointments");
            return [];
        }
    } 
    catch (err) {
        console.error("Error filtering appointments:", err);
        return [];
  }
}