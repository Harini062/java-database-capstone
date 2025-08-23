# Section 1: Architecture summary
This Spring Boot application uses both MVC and REST controllers. The Admin and Doctor Dashboards use Thymeleaf templates (MVC), while other modules such as Appointments and Patient Records are served via REST APIs. All requests are routed through a central Service Layer, which handles the core business logic and delegates operations to the appropriate data repositories. 

The application connects to two databases: 
- MySQL for structured data (Patients, Doctors, Appointments, Admins), using JPA entities.
- MongoDB for unstructured data like Prescriptions, using document-based models. This hybrid setup provides flexibility and efficiency in handling various data types within the Smart Clinic Management System. 
------------------------------------------------------

# Section 2: Numbered flow of data and control
1. User accesses AdminDashboard, DoctorDashboard, or REST modules like Appointments or Patient Records through the web interface.
2. Request is handled by either Thymeleaf Controllers (for dashboards) or REST Controllers (for API modules) depending on the type of interface.
3. Controllers pass the request to the Service Layer, which contains the core business logic.
4. The Service Layer uses either MySQL or MongoDB repositories, based on the data required.
5. MySQL Repositories access the MySQL Database to fetch or store structured data such as patients, doctors, appointments, or admin info.
6. JPA Entities represent this MySQL data, mapping the database tables to Java objects (models).
7. For unstructured data like prescriptions, the Service Layer uses MongoDB Repositories, which access the MongoDB Database using document-based models.
