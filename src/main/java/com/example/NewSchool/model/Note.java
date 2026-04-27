package com.example.NewSchool.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity 
@Data 
@Table(name = "note")
public class Note {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne 
    @JoinColumn(name = "eleve_id", nullable = false)
    private Eleve eleve;

    @ManyToOne 
    @JoinColumn(name = "matiere_id", nullable = false)
    private Matiere matiere;

    @ManyToOne 
    @JoinColumn(name = "classe_id", nullable = false)
    private Classe classe;

    @ManyToOne 
    @JoinColumn(name = "annee_scolaire_id")
    private AnneeScolaire anneeScolaire;

    @ManyToOne 
    @JoinColumn(name = "professeur_id")
    private Professeur professeur;

    private Double note1;
    private Double note2;
    private Double noteExamen;
    private Integer trimestre;

    // --- GETTERS AK SETTERS (A lamen pou NetBeans) ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Eleve getEleve() { return eleve; }
    public void setEleve(Eleve eleve) { this.eleve = eleve; }

    public Matiere getMatiere() { return matiere; }
    public void setMatiere(Matiere matiere) { this.matiere = matiere; }

    public Classe getClasse() { return classe; }
    public void setClasse(Classe classe) { this.classe = classe; }

    public AnneeScolaire getAnneeScolaire() { return anneeScolaire; }
    public void setAnneeScolaire(AnneeScolaire anneeScolaire) { this.anneeScolaire = anneeScolaire; }

    public Professeur getProfesseur() { return professeur; }
    public void setProfesseur(Professeur professeur) { this.professeur = professeur; }

    public Double getNote1() { return note1; }
    public void setNote1(Double note1) { this.note1 = note1; }

    public Double getNote2() { return note2; }
    public void setNote2(Double note2) { this.note2 = note2; }

    public Double getNoteExamen() { return noteExamen; }
    public void setNoteExamen(Double noteExamen) { this.noteExamen = noteExamen; }

    public Integer getTrimestre() { return trimestre; }
    public void setTrimestre(Integer trimestre) { this.trimestre = trimestre; }

    // --- METÒD KALKIL YO ---

    @Transient
    public Double getMoyenne() {
        double sum = 0; int cnt = 0;
        if (note1 != null) { sum += note1; cnt++; }
        if (note2 != null) { sum += note2; cnt++; }
        // Examen an konte pou 2 (Coefficient 2)
        if (noteExamen != null) { sum += noteExamen * 2; cnt += 2; }
        if (cnt == 0) return null;
        return Math.round((sum / cnt) * 100.0) / 100.0;
    }

    @Transient
    public String getMention() {
        Double m = getMoyenne();
        if (m == null) return "-";
        if (m >= 18) return "Excellent";
        if (m >= 15) return "Très Bien";
        if (m >= 12) return "Bien";
        if (m >= 10) return "Passable";
        return "Insuffisant";
    }
}