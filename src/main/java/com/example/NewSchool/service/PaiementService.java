package com.example.NewSchool.service;

import com.example.NewSchool.model.*;
import com.example.NewSchool.model.Paiement.StatutPaiement;
import com.example.NewSchool.model.Paiement.TypePaiement;
import com.example.NewSchool.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaiementService {

    public static final BigDecimal FRAIS_INSCRIPTION = new BigDecimal("1000.00");
    public static final BigDecimal FRAIS_VERSEMENT   = new BigDecimal("3000.00");

    private final PaiementRepository paiementRepo;
    private final EleveRepository eleveRepo;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public PaiementService(PaiementRepository paiementRepo, EleveRepository eleveRepo) {
        this.paiementRepo = paiementRepo;
        this.eleveRepo = eleveRepo;
    }

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

    @Transactional
    public void creerVersements(Eleve eleve) {
        for (TypePaiement type : new TypePaiement[]{
                TypePaiement.VERSEMENT_1, TypePaiement.VERSEMENT_2, TypePaiement.VERSEMENT_3}) {
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

    // Konfime pa Stripe Session ID (webhook)
    @Transactional
    public void confirmerPaiement(String sessionId) {
        paiementRepo.findByStripeSessionId(sessionId).ifPresent(p -> {
            p.setStatut(StatutPaiement.PAYE);
            p.setDatePaiement(LocalDateTime.now());
            paiementRepo.save(p);
            if (p.getTypePaiement() == TypePaiement.INSCRIPTION) {
                creerVersements(p.getEleve());
            }
        });
    }

    // Konfime pa paiement ID (pou secrétè)
    @Transactional
    public void confirmerPaiement(Long paiementId) {
        paiementRepo.findById(paiementId).ifPresent(p -> {
            p.setStatut(StatutPaiement.PAYE);
            p.setDatePaiement(LocalDateTime.now());
            paiementRepo.save(p);
            if (p.getTypePaiement() == TypePaiement.INSCRIPTION) {
                creerVersements(p.getEleve());
            }
        });
    }

    @Transactional
    public void enregistrerStripeSession(Long paiementId, String sessionId) {
        paiementRepo.findById(paiementId).ifPresent(p -> {
            p.setStripeSessionId(sessionId);
            paiementRepo.save(p);
        });
    }

    // Jwenn paiement pa ID
    public Paiement findById(Long id) {
        return paiementRepo.findById(id).orElseThrow();
    }

    // Kreye session Stripe epi retounen URL
    public String creerSessionStripe(Long paiementId, String successUrl, String cancelUrl)
            throws Exception {
        Paiement p = paiementRepo.findById(paiementId).orElseThrow();

        com.stripe.Stripe.apiKey = stripeSecretKey;

        com.stripe.param.checkout.SessionCreateParams params =
            com.stripe.param.checkout.SessionCreateParams.builder()
                .setMode(com.stripe.param.checkout.SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(
                    com.stripe.param.checkout.SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                            com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(p.getMontant().multiply(new BigDecimal("100")).longValue())
                                .setProductData(
                                    com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData
                                        .ProductData.builder()
                                        .setName(p.getTypePaiementLabel())
                                        .setDescription("NewScool — " + p.getEleve().getNom()
                                            + " " + p.getEleve().getPrenom())
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .build();

        com.stripe.model.checkout.Session session =
            com.stripe.model.checkout.Session.create(params);

        enregistrerStripeSession(paiementId, session.getId());

        return session.getUrl();
    }

    public boolean peutVoirBulletin(Long eleveId, int trimestre) {
        boolean inscriptionPayee = paiementRepo.existsByEleveIdAndTypePaiementAndStatut(
                eleveId, TypePaiement.INSCRIPTION, StatutPaiement.PAYE);
        if (!inscriptionPayee) return false;

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