import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = API_BASE_URL + '/doctor';

export async function getDoctors() {
    try {
        const response = await fetch(DOCTOR_API, {
            method : "GET",
            headers: {"Content-Type": "application/json" },
        });

        if(response.ok){
            const data = await response.json();
            return data || [];
        }
        else {
            console.error("Failed to fetch doctors");
            return [];
        }
    }
    catch(err){
        console.error("Error fetching doctors:", err);
        return [];
    }

}

export async function deleteDoctor(id, token) {
    try {
        const response = await fetch(`${DOCTOR_API}/${id}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
        });

        if (response.ok) {
            const data = await response.json();
            return { success: true, message: data.message || "Doctor deleted successfully" };
        } 
        else {
            const errorData = await response.json();
            return { success: false, message: errorData.message || "Failed to delete doctor" };
        }
    } 
    catch (err) {
        console.error("Error deleting doctor:", err);
        return { success: false, message: "Something went wrong while deleting doctor" };
  }
}

export async function saveDoctor(doctor, token) {
    try {
        const response = await fetch(DOCTOR_API, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(doctor),
        });

        if (response.ok) {
            const data = await response.json();
            return { success: true, message: data.message || "Doctor saved successfully" };
        } 
        else {
            const errorData = await response.json();
            return { success: false, message: errorData.message || "Failed to save doctor" };
        }
    } 
    catch (err) {
        console.error("Error saving doctor:", err);
        return { success: false, message: "Something went wrong while saving doctor" };
    }
}


export async function filterDoctors(name = "", time = "", specialty = "") {
    try {
        const query = new URLSearchParams();

        if (name) query.append("name", name);
        if (time) query.append("time", time);
        if (specialty) query.append("specialty", specialty);

        const url = `${DOCTOR_API}/filter?${query.toString()}`;

        const response = await fetch(url, {
        method: "GET",
        headers: { "Content-Type": "application/json" },
        });

        if (response.ok) {
            const data = await response.json();
            return data || [];
        } 
        else {
            console.error("Failed to filter doctors");
            return [];
        }
    } 
    catch (err) {
        console.error("Error filtering doctors:", err);
        return [];
  }
}
