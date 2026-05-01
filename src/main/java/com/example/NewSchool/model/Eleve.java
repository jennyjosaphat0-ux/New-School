package com.example.NewSchool.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "eleve")
public class Eleve implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;

    @Column(unique = true)
    private String email;

    private String adresse;
    private LocalDate dateNaissance;

    // PARAN 1
    private String nomPere;
    private String prenomPere;
    // PARAN 2
    private String nomMere;
    private String prenomMere;

    // EMAIL PARAN (pou primaire)
    private String emailParent;

    private String codeConnexion;

    public enum Sexe { MASCULIN, FEMININ }

    @Enumerated(EnumType.STRING)
    @Column(name = "sexe")
    private Sexe sexe;

    private boolean actif = true;
    private boolean exclu = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_id")
    private Classe classe;

    // GETTERS & SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getNomPere() { return nomPere; }
    public void setNomPere(String nomPere) { this.nomPere = nomPere; }

    public String getPrenomPere() { return prenomPere; }
    public void setPrenomPere(String prenomPere) { this.prenomPere = prenomPere; }

    public String getNomMere() { return nomMere; }
    public void setNomMere(String nomMere) { this.nomMere = nomMere; }

    public String getPrenomMere() { return prenomMere; }
    public void setPrenomMere(String prenomMere) { this.prenomMere = prenomMere; }

    public String getEmailParent() { return emailParent; }
    public void setEmailParent(String emailParent) { this.emailParent = emailParent; }

    public String getCodeConnexion() { return codeConnexion; }
    public void setCodeConnexion(String codeConnexion) { this.codeConnexion = codeConnexion; }

    public Sexe getSexe() { return sexe; }
    public void setSexe(Sexe sexe) { this.sexe = sexe; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public boolean isExclu() { return exclu; }
    public void setExclu(boolean exclu) { this.exclu = exclu; }

    public Classe getClasse() { return classe; }
    public void setClasse(Classe classe) { this.classe = classe; }

    // LOGIK
    @Transient
    public int getAge() {
        if (dateNaissance == null) return 0;
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }

    // Non konplè paran ki disponib (youn oswa 2)
    @Transient
    public String getNomParentComplet() {
        String pere = (prenomPere != null ? prenomPere : "") + " " + (nomPere != null ? nomPere : "");
        String mere = (prenomMere != null ? prenomMere : "") + " " + (nomMere != null ? nomMere : "");
        pere = pere.trim();
        mere = mere.trim();
        if (!pere.isEmpty() && !mere.isEmpty()) return pere + " / " + mere;
        if (!pere.isEmpty()) return pere;
        if (!mere.isEmpty()) return mere;
        return "-";
    }

    // Klas primè: 1ère Année → 6ème Année
    @Transient
    public boolean isPrimaire() {
        if (classe == null) return false;
        String nom = classe.getNomClasse();
        return nom != null && nom.contains("Année");
    }

    // Klas segondè: 7ème → NS4
    @Transient
    public boolean isSecondaire() {
        return !isPrimaire();
    }

    // SPRING SECURITY
    @Override public String getUsername() { return this.email; }
    @Override public String getPassword() { return this.codeConnexion; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_ELEVE"));
    }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return !exclu; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return actif && !exclu; }
}
