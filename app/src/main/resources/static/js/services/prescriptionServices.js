// prescriptionServices.js
import { API_BASE_URL } from '../config/config.js'

const PRESCRITION_API = API_BASE_URL + "prescription"
export async function savePrescription(prescription, token) {
  try {
    const response = await fetch(`${PRESCRITION_API}/${token}`, {
      method: "POST",
      headers: {
        "Content-type": "application/json"
      },
      body: JSON.stringify(prescription)
    });
    const result = await response.json();
    return { success: response.ok, message: result.message }
  }
  catch (error) {
    console.error("Error :: savePrescription :: ", error)
    return { success: false, message: result.message }
  }
}

export async function getPrescription(appointmentId, token) {
  try {
    const response = await fetch(`${PRESCRITION_API}/${appointmentId}/${token}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json"
      }
    });
    const result = await response.json();

    if (!result.prescriptions || result.prescriptions.length === 0) {   
        console.info(`No prescription exists yet for appointmentId: ${appointmentId}`);
        return null; 
    }
    
    return result;

  } catch (error) {
    console.error("Error fetching prescription:", err);
    return null ;
  }
}
