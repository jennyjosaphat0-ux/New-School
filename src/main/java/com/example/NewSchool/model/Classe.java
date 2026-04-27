/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.NewSchool.model;

/**
 *
 * @author jenny
 */


import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "classe")
public class Classe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Non klas la (ex: NS1, Philo A)
    @Column(unique = true, nullable = false)
    private String nomClasse;

    /**
     * Relasyon ak Affectation.
     * Sa ap pèmèt nou konnen tout pwofesè ki afekte nan klas sa a.
     */
    @OneToMany(mappedBy = "classe")
    private List<Affectation> affectations;

    // --- CONSTRUCTEURS ---
    public Classe() {
    }

    public Classe(String nomClasse) {
        this.nomClasse = nomClasse;
    }

    // --- GETTERS AND SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomClasse() {
        return nomClasse;
    }

    public void setNomClasse(String nomClasse) {
        this.nomClasse = nomClasse;
    }

    public List<Affectation> getAffectations() {
        return affectations;
    }

    public void setAffectations(List<Affectation> affectations) {
        this.affectations = affectations;
    }
}