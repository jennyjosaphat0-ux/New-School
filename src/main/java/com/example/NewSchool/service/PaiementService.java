package com.example.NewSchool.service;

import com.example.NewSchool.model.*;
import com.example.NewSchool.model.Paiement.StatutPaiement;
import com.example.NewSchool.model.Paiement.TypePaiement;
import com.example.NewSchool.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaiementService {

    // TARIF FIXE
    public static final BigDecimal FRAIS_INSCRIPTION = new BigDecimal("1000.00");
    public static final BigDecimal FRAIS_VERSEMENT   = new BigDecimal("3000.00");

    private final PaiementRepository paiementRepo;
    private final EleveRepository eleveRepo;

    public PaiementService(PaiementRepository paiementRepo, EleveRepository eleveRepo) {
        this.paiementRepo = paiementRepo;
        this.eleveRepo = eleveRepo;
    }

    // Kreye paiement inscription otomatikman lè elèv enrejistre
    @Transactional
    public Paiement creerPaiementInscription(Eleve eleve) {
        Paiement p = new Paiement();
        p.setEleve(eleve);
        p.setTypePaiement(TypePaiement.INSCRIPTION);
        p.setMontant(FRAIS_INSCRIPTION);
        p.setStatut(StatutPaiement.EN_ATTENTE);
        p.setDateCreation(LocalDateTime.now());
        return paiementRepo.save(p);
    }

    // Kreye 3 versement otomatikman lè inscription peye
    @Transactional
    public void creerVersements(Eleve eleve) {
        for (TypePaiement type : new TypePaiement[]{
                TypePaiement.VERSEMENT_1, TypePaiement.VERSEMENT_2, TypePaiement.VERSEMENT_3}) {
            // Pa kreye si deja egziste
            if (!paiementRepo.existsByEleveIdAndTypePaiementAndStatut(
                    eleve.getId(), type, StatutPaiement.PAYE)) {
                boolean existe = paiementRepo.findByEleveIdAndTypePaiement(
                        eleve.getId(), type).isPresent();
                if (!existe) {
                    Paiement p = new Paiement();
                    p.setEleve(eleve);
                    p.setTypePaiement(type);
                    p.setMontant(FRAIS_VERSEMENT);
                    p.setStatut(StatutPaiement.EN_ATTENTE);
                    p.setDateCreation(LocalDateTime.now());
                    paiementRepo.save(p);
                }
            }
        }
    }

    // Confirme paiement apre Stripe retounen succes
    @Transactional
    public void confirmerPaiement(String sessionId) {
        paiementRepo.findByStripeSessionId(sessionId).ifPresent(p -> {
            p.setStatut(StatutPaiement.PAYE);
            p.setDatePaiement(LocalDateTime.now());
            paiementRepo.save(p);

            // Si inscription peye → kreye 3 versement otomatikman
            if (p.getTypePaiement() == TypePaiement.INSCRIPTION) {
                creerVersements(p.getEleve());
            }
        });
    }

    // Enrejistre session Stripe nan yon paiement
    @Transactional
    public void enregistrerStripeSession(Long paiementId, String sessionId) {
        paiementRepo.findById(paiementId).ifPresent(p -> {
            p.setStripeSessionId(sessionId);
            paiementRepo.save(p);
        });
    }

    // Verifye si yon elèv ka wè bulletin trimès X
    public boolean peutVoirBulletin(Long eleveId, int trimestre) {
        // Dwe peye inscription D'ABORD
        boolean inscriptionPayee = paiementRepo.existsByEleveIdAndTypePaiementAndStatut(
                eleveId, TypePaiement.INSCRIPTION, StatutPaiement.PAYE);
        if (!inscriptionPayee) return false;

        // Selon trimès, verifye versement ki nesesè yo
        return switch (trimestre) {
            case 1 -> paiementRepo.existsByEleveIdAndTypePaiementAndStatut(
                    eleveId, TypePaiement.VERSEMENT_1, StatutPaiement.PAYE);
            case 2 -> paiementRepo.existsByEleveIdAndTypePaiementAndStatut(
                    eleveId, TypePaiement.VERSEMENT_1, StatutPaiement.PAYE) &&
                      paiementRepo.existsByEleveIdAndTypePaiementAndStatut(
                    eleveId, TypePaiement.VERSEMENT_2, StatutPaiement.PAYE);
            case 3 -> paiementRepo.existsByEleveIdAndTypePaiementAndStatut(
                    eleveId, TypePaiement.VERSEMENT_1, StatutPaiement.PAYE) &&
                      paiementRepo.existsByEleveIdAndTypePaiementAndStatut(
                    eleveId, TypePaiement.VERSEMENT_2, StatutPaiement.PAYE) &&
                      paiementRepo.existsByEleveIdAndTypePaiementAndStatut(
                    eleveId, TypePaiement.VERSEMENT_3, StatutPaiement.PAYE);
            default -> false;
        };
    }

    // Statut rezime pou afichaj nan tab elèv
    public String getStatutPaiementEleve(Long eleveId) {
        List<Paiement> paiements = paiementRepo.findByEleveId(eleveId);
        if (paiements.isEmpty()) return "Non effectué";

        long totalPaye = paiements.stream()
            .filter(p -> p.getStatut() == StatutPaiement.PAYE).count();
        long total = paiements.size();

        if (totalPaye == 0) return "Non effectué";
        if (totalPaye == total) return "Paiement effectué";
        return "Paiement en cours";
    }

    public List<Paiement> getPaiementsEleve(Long eleveId) {
        return paiementRepo.findByEleveId(eleveId);
    }

    public List<Paiement> getAllPaiements() {
        return paiementRepo.findAllOrderByDate();
    }

    public List<Paiement> getPaiementsParClasse(Long classeId) {
        return paiementRepo.findByEleveClasseId(classeId);
    }
}
