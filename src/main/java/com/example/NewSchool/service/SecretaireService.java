package com.example.NewSchool.service;

import com.example.NewSchool.model.*;
import com.example.NewSchool.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class SecretaireService {

    private final EleveRepository eleveRepo;
    private final ClasseRepository classeRepo;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final PaiementService paiementService;

    // Klas secondaire (7ème → NS4)
    private static final Set<String> CLASSES_SECONDAIRES = Set.of(
        "7ème","8ème","9ème","NS1","NS2","NS3","NS4",
        "7eme","8eme","9eme"
    );

    public SecretaireService(EleveRepository eleveRepo, ClasseRepository classeRepo,
                              EmailService emailService, PasswordEncoder passwordEncoder,
                              PaiementService paiementService) {
        this.eleveRepo       = eleveRepo;
        this.classeRepo      = classeRepo;
        this.emailService    = emailService;
        this.passwordEncoder = passwordEncoder;
        this.paiementService = paiementService;
    }

    @Transactional
    public String enregistrerEleve(Eleve eleve, Long classeId) {

        // Jenere kòd
        String codeRaw = "EL-" + (1000 + (int)(Math.random() * 9000));
        eleve.setCodeConnexion(passwordEncoder.encode(codeRaw));
        eleve.setActif(true);
        eleve.setExclu(false);

        // Sexe default
        if (eleve.getSexe() == null) eleve.setSexe(Eleve.Sexe.MASCULIN);

        // Verifye email duplikasyon
        if (eleve.getEmail() != null && !eleve.getEmail().isBlank()
                && eleveRepo.findByEmail(eleve.getEmail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }

        // Asosye klas
        Classe classe = classeRepo.findById(classeId)
            .orElseThrow(() -> new RuntimeException("Classe introuvable"));
        eleve.setClasse(classe);

        // Sove elèv
        Eleve saved = eleveRepo.save(eleve);

        // Kreye frais inscription 1000 gdes otomatikman
        paiementService.creerPaiementInscription(saved);

        // Detèmine si primè oswa segondè
        boolean estSecondaire = CLASSES_SECONDAIRES.contains(classe.getNomClasse());

        if (estSecondaire) {
            // Segondè → voye bay elèv dirèkteman
            if (saved.getEmail() != null && !saved.getEmail().isBlank()) {
                emailService.envoyerCodeEleveAvecPaiement(
                    saved.getEmail(), saved.getNom(), codeRaw);
            }
        } else {
            // Primè → voye bay paran an
            String emailDest = null;
            if (saved.getEmailParent() != null && !saved.getEmailParent().isBlank()) {
                emailDest = saved.getEmailParent();
            } else if (saved.getEmail() != null && !saved.getEmail().isBlank()) {
                emailDest = saved.getEmail();
            }
            if (emailDest != null) {
                emailService.envoyerCodeEleveAvecPaiement(
                    emailDest, saved.getNom(), codeRaw);
            }
        }

        return codeRaw;
    }
}
