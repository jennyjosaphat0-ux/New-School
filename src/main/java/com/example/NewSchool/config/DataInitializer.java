package com.example.NewSchool.config;

import com.example.NewSchool.model.*;
import com.example.NewSchool.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    CommandLineRunner init(AdminRepository adminRepo,
                           ClasseRepository classeRepo,
                           MatiereRepository matiereRepo,
                           AnneeScolaireRepository anneeRepo,
                           PasswordEncoder encoder) {
        return args -> {
            // Admin
            if (adminRepo.findByMail("adminschoolvision@gmail.com").isEmpty()) {
                Admin a = new Admin();
                a.setNom("Directeur");
                a.setPrenom("Admin");
                a.setMail("adminschoolvision@gmail.com");
                a.setPassword(encoder.encode("Admin123"));
                adminRepo.save(a);
                System.out.println(">>> ADMIN: adminschoolvision@gmail.com / Admin123");
            }

            // Ane scolaire
            if (anneeRepo.findByCouranteTrue().isEmpty()) {
                AnneeScolaire an = new AnneeScolaire();
                an.setLibelle("2024-2025");
                an.setCourante(true);
                anneeRepo.save(an);
            }

            // Classes primaires
            for (String nom : new String[]{"1ère Année","2ème Année","3ème Année",
                                            "4ème Année","5ème Année","6ème Année"}) {
                if (classeRepo.findByNomClasse(nom).isEmpty()) {
                    classeRepo.save(new Classe(nom));
                }
            }

            // Classes secondaires
            for (String nom : new String[]{"NS1","NS2","NS3","NS4"}) {
                if (classeRepo.findByNomClasse(nom).isEmpty()) {
                    classeRepo.save(new Classe(nom));
                }
            }

            // Matières
            for (String nom : new String[]{"Mathématiques","Français","Sciences",
                    "Histoire-Géo","Anglais","Physique","Chimie",
                    "Informatique","Éducation Civique","Sport"}) {
                if (matiereRepo.findByNom(nom).isEmpty()) {
                    Matiere m = new Matiere(); m.setNom(nom); matiereRepo.save(m);
                }
            }
            System.out.println(">>> Données initiales chargées.");
        };
    }
}
