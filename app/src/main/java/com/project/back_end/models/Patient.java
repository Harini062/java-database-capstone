package com.project.back_end.models;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Patient{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "patient's name cannot be null")
    @Size(min=3,max=100,message = "patient's name must be between 3 and 100 characters")
    @Column(nullable=false,length=100)
    private String name;

    @NotNull(message = "email cannot be null")
    @Email(message = "invalid email format")
    @Column(nullable=false,unique=true,length=100)
    private String email;

    @NotNull(message = "password cannot be null")
    @Size(min=6,message = "password must be atleast 6 characters long")
    @Column(name="password_hash",nullable=false,length=255)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotNull(message = "phone number cannot be null")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    @Column(nullable=false,length=10)
    private String phone;

    @NotNull(message = "patient's address cannot be null")
    @Size(max=255,message = "Address must not exceed 255 characters")
    @Column(nullable=false,length=255)
    private String address;

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
}