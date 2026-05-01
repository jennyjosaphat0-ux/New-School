package com.example.NewSchool.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "paiement")
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eleve_id", nullable = false)
    private Eleve eleve;

    // FRAIS INSCRIPTION = 1000 gdes
    // VERSEMENT 1, 2, 3 = frais trimestriel
    public enum TypePaiement {
        INSCRIPTION,
        VERSEMENT_1,
        VERSEMENT_2,
        VERSEMENT_3
    }

    public enum StatutPaiement {
        EN_ATTENTE,
        PAYE,
        ECHOUE
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypePaiement typePaiement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPaiement statut = StatutPaiement.EN_ATTENTE;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    // ID retounen pa Stripe apre paiement
    private String stripeSessionId;
    private String stripePaymentIntentId;

    private LocalDateTime datePaiement;
    private LocalDateTime dateCreation = LocalDateTime.now();

    // CONSTRUCTEURS
    public Paiement() {}

    // GETTERS & SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Eleve getEleve() { return eleve; }
    public void setEleve(Eleve eleve) { this.eleve = eleve; }

    public TypePaiement getTypePaiement() { return typePaiement; }
    public void setTypePaiement(TypePaiement typePaiement) { this.typePaiement = typePaiement; }

    public StatutPaiement getStatut() { return statut; }
    public void setStatut(StatutPaiement statut) { this.statut = statut; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public String getStripeSessionId() { return stripeSessionId; }
    public void setStripeSessionId(String stripeSessionId) { this.stripeSessionId = stripeSessionId; }

    public String getStripePaymentIntentId() { return stripePaymentIntentId; }
    public void setStripePaymentIntentId(String s) { this.stripePaymentIntentId = s; }

    public LocalDateTime getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDateTime datePaiement) { this.datePaiement = datePaiement; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    // HELPER: label afichaj
    public String getTypePaiementLabel() {
        return switch (typePaiement) {
            case INSCRIPTION -> "Frais d'Inscription";
            case VERSEMENT_1 -> "1er Versement (Trimestre 1)";
            case VERSEMENT_2 -> "2ème Versement (Trimestre 2)";
            case VERSEMENT_3 -> "3ème Versement (Trimestre 3)";
        };
    }
}
