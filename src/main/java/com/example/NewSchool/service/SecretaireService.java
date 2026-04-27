package com.example.NewSchool.service;

import com.example.NewSchool.model.Eleve;
import com.example.NewSchool.repository.ClasseRepository;
import com.example.NewSchool.repository.EleveRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SecretaireService {

    private final EleveRepository eleveRepo;
    private final ClasseRepository classeRepo;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public SecretaireService(EleveRepository eleveRepo,
                             ClasseRepository classeRepo,
                             EmailService emailService,
                             PasswordEncoder passwordEncoder) {
        this.eleveRepo = eleveRepo;
        this.classeRepo = classeRepo;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public String enregistrerEleve(Eleve eleve, Long classeId) {

        // ✅ GENERER CODE
        String code = "EL-" + (1000 + (int)(Math.random() * 9000));

        // ✅ SECURITE - encoder code connexion
        eleve.setCodeConnexion(passwordEncoder.encode(code));

        eleve.setActif(true);
        eleve.setExclu(false);

        // ✅ SEXE DEFAULT SI NULL
        if (eleve.getSexe() == null) {
            eleve.setSexe(Eleve.Sexe.MASCULIN);
        }

        // ✅ VERIFIER EMAIL DUPLICATION
        if (eleve.getEmail() != null &&
            eleveRepo.findByEmail(eleve.getEmail()).isPresent()) {
            throw new RuntimeException("Email deja utilise");
        }

        // ✅ ASSOCIER CLASSE
        eleve.setClasse(
                classeRepo.findById(classeId)
                        .orElseThrow(() -> new RuntimeException("Classe introuvable"))
        );

        // ✅ SAUVEGARDE ELEVE
        Eleve saved = eleveRepo.save(eleve);

        // ✅ ENVOI EMAIL UNIQUEMENT A L’ELEVE
        if (saved.getEmail() != null && !saved.getEmail().isBlank()) {
            emailService.envoyerCodeEleve(
                    saved.getEmail(),
                    saved.getNom(),
                    code
            );
        }

        return code;
    }
}