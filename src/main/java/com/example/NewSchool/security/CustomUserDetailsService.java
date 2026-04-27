package com.example.NewSchool.security;

import com.example.NewSchool.repository.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepo;
    private final ProfesseurRepository profRepo;
    private final SecretaireRepository secRepo;
    private final EleveRepository eleveRepo;

    // Constructor a lamen pou ranje liy wouj yo
    public CustomUserDetailsService(AdminRepository adminRepo, 
                                    ProfesseurRepository profRepo, 
                                    SecretaireRepository secRepo, 
                                    EleveRepository eleveRepo) {
        this.adminRepo = adminRepo;
        this.profRepo = profRepo;
        this.secRepo = secRepo;
        this.eleveRepo = eleveRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Tcheke si se yon Admin
        var admin = adminRepo.findByMail(email);
        if (admin.isPresent()) return admin.get();

        // 2. Tcheke si se yon Pwofesè
        var prof = profRepo.findByEmail(email);
        if (prof.isPresent()) return prof.get();

        // 3. Tcheke si se yon Sekretè
        var sec = secRepo.findByEmail(email);
        if (sec.isPresent()) return sec.get();

        // 4. Tcheke si se yon Elèv
        var eleve = eleveRepo.findByEmail(email);
        if (eleve.isPresent()) return eleve.get();

        throw new UsernameNotFoundException("Itilizatè pa jwenn: " + email);
    }
}