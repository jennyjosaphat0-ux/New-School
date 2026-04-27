package com.example.NewSchool.service;

import com.example.NewSchool.model.*;
import com.example.NewSchool.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AdminService {

    private final ProfesseurRepository profRepo;
    private final SecretaireRepository secRepo;
    private final EleveRepository eleveRepo;
    private final AffectationRepository affRepo;
    private final ClasseRepository classeRepo;
    private final MatiereRepository matiereRepo;
    private final EmailService emailService;
    private final PasswordEncoder encoder;

    // Constructor a lamen pou evite pwoblèm Lombok
    public AdminService(ProfesseurRepository profRepo, 
                        SecretaireRepository secRepo, 
                        EleveRepository eleveRepo, 
                        AffectationRepository affRepo, 
                        ClasseRepository classeRepo, 
                        MatiereRepository matiereRepo, 
                        EmailService emailService, 
                        PasswordEncoder encoder) {
        this.profRepo = profRepo;
        this.secRepo = secRepo;
        this.eleveRepo = eleveRepo;
        this.affRepo = affRepo;
        this.classeRepo = classeRepo;
        this.matiereRepo = matiereRepo;
        this.emailService = emailService;
        this.encoder = encoder;
    }

    private String genererCode(String prefix) {
        return prefix + "-" + (1000 + (int)(Math.random() * 8999));
    }

    // ===== PROFESSEUR =====
  @Transactional
  public String enregistrerProfesseur(Professeur prof, Long classeId,
                                     Long matiereId, Integer nbreHeures) {

    String code = genererCode("NS");

    // 🔥 codeConnexion = password
    prof.setCodeConnexion(encoder.encode(code));

    prof.setActif(true);

    Professeur saved = profRepo.save(prof);

    if (classeId != null) {
        Affectation aff = new Affectation();
        aff.setProfesseur(saved);
        aff.setClasse(classeRepo.findById(classeId).orElseThrow());

        if (matiereId != null)
            aff.setMatiere(matiereRepo.findById(matiereId).orElse(null));

        aff.setNbreHeures(nbreHeures != null ? nbreHeures : 0);

        affRepo.save(aff);
    }

    emailService.envoyerCodeProfesseur(
        saved.getEmail(),
        saved.getNom(),
        code
    );

    return code;
}

    // ===== SECRETAIRE =====
    @Transactional
public String enregistrerSecretaire(Secretaire sec) {

    String code = genererCode("SEC");

    // 🔥 encode li
    sec.setCodeConnexion(encoder.encode(code));

    sec.setActif(true);

    Secretaire saved = secRepo.save(sec);

    // voye code brut bay user
    emailService.envoyerCodeSecretaire(
        saved.getEmail(),
        saved.getNom(),
        code
    );

    return code;
}

    @Transactional
    public void revoquerProfesseur(Long id) {
        profRepo.findById(id).ifPresent(p -> {
            p.setActif(false); 
            profRepo.save(p);
        });
    }

    public List<Professeur> getAllProfesseurs() { return profRepo.findAll(); }

    @Transactional
    public void revoquerSecretaire(Long id) {
        secRepo.findById(id).ifPresent(s -> {
            s.setActif(false); 
            secRepo.save(s);
        });
    }

    public List<Secretaire> getAllSecretaires() { return secRepo.findAll(); }

    @Transactional
    public void exclureEleve(Long id) {
        eleveRepo.findById(id).ifPresent(e -> {
            e.setExclu(true); 
            eleveRepo.save(e);
        });
    }

    public List<Classe> getAllClasses() { return classeRepo.findAll(); }
    public List<Matiere> getAllMatieres() { return matiereRepo.findAll(); }
}