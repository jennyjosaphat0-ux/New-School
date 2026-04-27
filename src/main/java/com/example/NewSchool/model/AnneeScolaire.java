package com.example.NewSchool.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "annee_scolaire")
public class AnneeScolaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String libelle;

    private boolean courante = false;

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public boolean isCourante() {
        return courante;
    }

    public void setCourante(boolean courante) {
        this.courante = courante;
    }
}