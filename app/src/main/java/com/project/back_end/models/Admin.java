package com.project.back_end.models;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import jakarta.persistence.Column;
import java.time.*;

@Entity
public class Admin{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "username cannot be null")
    @Size(min=3,max=50,message = "username must be between 3 and 50 characters")
    @Column(nullable=false,unique=true,length=50)
    private String username;

    @NotNull(message = "password cannot be null")
    @Size(min=8,message = "password must be atleast 8 characters long")
    @Column(name="password_hash",nullable=false, length=255)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotNull(message = "email cannot be null")
    @Email(message = "invalid email format")
    @Column(nullable=false,unique=true,length=100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.MODERATOR;

    @Column(name = "created_at",updatable = false,insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public enum Role {
        SUPERADMIN, MODERATOR
    }

    //Getters and Setters
    public Long getId() { 
        return id; 
    }

    public void setId(Long id) { 
        this.id = id;
    }

    public String getUsername() { 
        return username; 
    }
    public void setUsername(String username) { 
        this.username = username;
    }

    public String getPassword() {
        return password; 
    }
    public void setPassword(String password) { 
        this.password = password;
    }

    public String getEmail() { 
        return email; 
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role; 
    }
    public void setRole(Role role) { 
        this.role = role; 
    }

    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    public void setCreatedAt(LocalDateTime createdAt) {
     this.createdAt = createdAt;
    }

}