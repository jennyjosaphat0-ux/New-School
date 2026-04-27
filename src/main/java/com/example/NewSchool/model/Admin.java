package com.example.NewSchool.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * @author jenny
 */
@Entity
@Data
public class Admin implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Non an pa ka vid")
    private String nom;

    @NotBlank(message = "Prenon an pa ka vid")
    private String prenom;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Mail la obligatwa")
    @Email(message = "Fòma mail la pa valid")
    @Pattern(
        regexp = "^[A-Za-z0-9._%+-]+@gmail\\.com$", 
        message = "Sèlman kont @gmail.com ki otorize"
    )
    private String mail;

    @NotBlank(message = "Modpas la obligatwa")
    private String password; 

    // --- GETTERS AK SETTERS (A lamen pou ede NetBeans ak Lombok) ---
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    // Nou kenbe metòd sa a pou nou ka itilize l nan AdminService
    public void setPassword(String password) { this.password = password; }

    // --- METÒD SPRING SECURITY (UserDetails) ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Sa bay admin nan tout dwa yon ADMIN
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Override
    public String getUsername() {
        // Spring Security ap itilize mail la pou login
        return this.mail;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}