package com.example.NewSchool.controller;

import com.example.NewSchool.model.*;
import com.example.NewSchool.repository.*;
import com.example.NewSchool.service.NoteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/eleve")
public class EleveController {

    private final EleveRepository eleveRepo;
    private final NoteService noteService;

    // CONSTRUCTOR A LAMEN (Ranplase @RequiredArgsConstructor)
    public EleveController(EleveRepository eleveRepo, NoteService noteService) {
        this.eleveRepo = eleveRepo;
        this.noteService = noteService;
    }

    private Eleve getEleve(UserDetails user) {
        return eleveRepo.findByEmail(user.getUsername()).orElseThrow();
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails user, Model m) {
        Eleve e = getEleve(user);
        m.addAttribute("eleve", e);
        // Sèvi ak getId() ou te jenere nan klas Eleve a
        m.addAttribute("moyenne1", noteService.getMoyenneGenerale(e.getId(), 1));
        m.addAttribute("moyenne2", noteService.getMoyenneGenerale(e.getId(), 2));
        m.addAttribute("moyenne3", noteService.getMoyenneGenerale(e.getId(), 3));
        return "eleve/dashboard";
    }

    @GetMapping("/notes")
    public String notes(@AuthenticationPrincipal UserDetails user,
                        @RequestParam(defaultValue = "1") int trimestre, Model m) {
        Eleve e = getEleve(user);
        m.addAttribute("eleve", e);
        m.addAttribute("notes", noteService.getNotesEleveTrimestre(e.getId(), trimestre));
        m.addAttribute("moyenne", noteService.getMoyenneGenerale(e.getId(), trimestre));
        m.addAttribute("trimestre", trimestre);
        return "eleve/notes";
    }

    @GetMapping("/bulletin")
    public String bulletin(@AuthenticationPrincipal UserDetails user,
                           @RequestParam(defaultValue = "1") int trimestre, Model m) {
        Eleve e = getEleve(user);
        m.addAttribute("eleve", e);
        m.addAttribute("notes", noteService.getNotesEleveTrimestre(e.getId(), trimestre));
        m.addAttribute("moyenne", noteService.getMoyenneGenerale(e.getId(), trimestre));
        // Asire w ke klas Eleve a gen yon metòd getClasse() k ap mache
        m.addAttribute("rang", noteService.getRang(e.getId(), e.getClasse().getId(), trimestre));
        m.addAttribute("trimestre", trimestre);
        return "eleve/bulletin";
    }

    @GetMapping("/profil")
    public String profil(@AuthenticationPrincipal UserDetails user, Model m) {
        m.addAttribute("eleve", getEleve(user));
        return "eleve/profil";
    }
}