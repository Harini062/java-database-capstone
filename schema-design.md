# Smart Clinic Management System – Schema Design  

This schema supports both **structured** and **flexible** data storage using:  
- **MySQL** for relational entities  
- **MongoDB** for dynamic documents like prescriptions, feedback, chat logs, and uploads  

---

##  MySQL Database Design  

###  Table: `patient`  
| Column       | Type          | Constraints                               |
|--------------|--------------|-------------------------------------------|
| id           | BIGINT       | PRIMARY KEY, AUTO_INCREMENT               |
| name         | VARCHAR(100) | NOT NULL                                  |
| email        | VARCHAR(100) | NOT NULL, UNIQUE                          |
| password_hash| VARCHAR(255) | NOT NULL                                  |
| phone        | VARCHAR(10)  | NOT NULL                                  |
| address      | VARCHAR(255) | NOT NULL                                  |

---

###  Table: `doctor`  
| Column        | Type          | Constraints                               |
|---------------|--------------|-------------------------------------------|
| id            | BIGINT       | PRIMARY KEY, AUTO_INCREMENT               |
| name          | VARCHAR(100) | NOT NULL                                  |
| specialty     | VARCHAR(50)  | NOT NULL                                  |
| email         | VARCHAR(100) | NOT NULL, UNIQUE                          |
| password_hash | VARCHAR(255) | NOT NULL                                  |
| phone         | VARCHAR(10)  | NOT NULL                                  |

---

###  Table: `doctor_available_times`  
| Column        | Type          | Constraints                               |
|---------------|--------------|-------------------------------------------|
| doctor_id     | BIGINT       | FOREIGN KEY → doctor(id) ON DELETE CASCADE |
| available_time| VARCHAR(50)  | NOT NULL                                  |

>  Stores availability slots as simple strings (`"Monday 09:00-12:00"`, `"Friday 14:00-18:00"`).  

---

###  Table: `appointment`  
| Column           | Type      | Constraints                               |
|------------------|-----------|-------------------------------------------|
| id               | BIGINT    | PRIMARY KEY, AUTO_INCREMENT               |
| doctor_id        | BIGINT    | FOREIGN KEY → doctor(id) ON DELETE CASCADE |
| patient_id       | BIGINT    | FOREIGN KEY → patient(id) ON DELETE CASCADE |
| appointment_time | TIMESTAMP | NOT NULL                                  |
| status           | INT       | NOT NULL (0 = Scheduled, 1 = Completed)   |
| created_at       | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP                 |

>  Doctor or patient deletion removes related appointments automatically.  
>  `endTime` is calculated in **Java code**, not stored in the DB.  

---

###  Table: `admin`  
| Column        | Type          | Constraints                               |
|---------------|--------------|-------------------------------------------|
| id            | BIGINT       | PRIMARY KEY, AUTO_INCREMENT               |
| username      | VARCHAR(50)  | NOT NULL, UNIQUE                          |
| password_hash | VARCHAR(255) | NOT NULL                                  |
| email         | VARCHAR(100) | NOT NULL, UNIQUE                          |
| role          | ENUM('SUPERADMIN','MODERATOR') | DEFAULT 'MODERATOR'     |
| created_at    | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP                 |

>  Passwords stored using **secure hashing**.  

---

##  MongoDB Collection Design  

###  Collection: `prescriptions`  
Stores flexible prescription data linked to appointments, patients, and doctors.  

```json
{
  "_id": "ObjectId('64f4d2b78912f5a6cabc1234')",
  "appointmentId": 101,
  "patientId": 5,
  "doctorId": 3,
  "issuedAt": "2025-08-17T14:30:00Z",
  "medications": [
    {
      "name": "Amoxicillin",
      "dosage": "500mg",
      "frequency": "3 times a day",
      "duration_days": 7
    }
  ],
  "doctorNotes": "Take with food. Avoid alcohol.",
  "pharmacy": {
    "name": "CareWell Pharmacy",
    "location": "Downtown Branch"
  },
  "refillAllowed": true,
  "tags": ["antibiotic", "followup"]
}
