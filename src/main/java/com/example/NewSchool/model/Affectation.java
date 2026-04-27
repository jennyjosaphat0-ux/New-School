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

@Entity
@Table(name = "affectation")
public class Affectation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "professeur_id", nullable = false)
    private Professeur professeur;

    @ManyToOne
    @JoinColumn(name = "matiere_id", nullable = false)
    private Matiere matiere;

    @ManyToOne
    @JoinColumn(name = "classe_id", nullable = false)
    private Classe classe;

    // Se isit la li dwe ye!
    private Integer nbreHeures;

    public Affectation() {}

    public Affectation(Professeur professeur, Matiere matiere, Classe classe, Integer nbreHeures) {
        this.professeur = professeur;
        this.matiere = matiere;
        this.classe = classe;
        this.nbreHeures = nbreHeures;
    }

    // --- GETTERS & SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Professeur getProfesseur() { return professeur; }
    public void setProfesseur(Professeur professeur) { this.professeur = professeur; }
    public Matiere getMatiere() { return matiere; }
    public void setMatiere(Matiere matiere) { this.matiere = matiere; }
    public Classe getClasse() { return classe; }
    public void setClasse(Classe classe) { this.classe = classe; }
    public Integer getNbreHeures() { return nbreHeures; }
    public void setNbreHeures(Integer nbreHeures) { this.nbreHeures = nbreHeures; }
}

