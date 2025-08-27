package com.project.back_end.DTO;

public class LoginDTO {

    private String identifier; // email (Doctor/Patient) or username (Admin)
    private String password;   

    
    public LoginDTO() {
    }

    public LoginDTO(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    // Getters & Setters
    public String getIdentifier() {
        return identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
