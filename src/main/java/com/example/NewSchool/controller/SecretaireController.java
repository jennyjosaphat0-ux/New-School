package com.example.NewSchool.controller;

import com.example.NewSchool.model.Eleve;
import com.example.NewSchool.repository.EleveRepository;
import com.example.NewSchool.repository.ClasseRepository;
import com.example.NewSchool.service.SecretaireService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/secretaire")
public class SecretaireController {

    private final SecretaireService secService;
    private final EleveRepository eleveRepo;
    private final ClasseRepository classeRepo;

    public SecretaireController(SecretaireService secService,
                                EleveRepository eleveRepo,
                                ClasseRepository classeRepo) {
        this.secService = secService;
        this.eleveRepo = eleveRepo;
        this.classeRepo = classeRepo;
    }

    // =====================================================
    // 🟢 DASHBOARD SECRETAIRE
    // =====================================================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("totalEleves", eleveRepo.count());
        model.addAttribute("totalClasses", classeRepo.count());
        model.addAttribute("derniersEleves", eleveRepo.findAll());

        return "secretaire/dashboard";
    }

    // =====================================================
    // 🟢 FORM ELEVE
    // =====================================================
    @GetMapping("/eleve/nouveau")
    public String form(Model m) {
        m.addAttribute("eleve", new Eleve());
        m.addAttribute("classes", classeRepo.findAll());
        return "secretaire/eleve-form";
    }

    // =====================================================
    // 🟢 SAVE ELEVE
    // =====================================================
    @PostMapping("/eleve/sauvegarder")
    public String save(@ModelAttribute Eleve eleve,
                       @RequestParam Long classeId,
                       RedirectAttributes ra) {

        try {
            String code = secService.enregistrerEleve(eleve, classeId);

            ra.addFlashAttribute("success",
                    "✅ Élève enregistré! Code: " + code);

            return "redirect:/secretaire/eleve/liste";

        } catch (Exception e) {

            ra.addFlashAttribute("error",
                    "❌ Erreur: " + e.getMessage());

            return "redirect:/secretaire/eleve/nouveau";
        }
    }

    // =====================================================
    // 🟢 LISTE ELEVE
    // =====================================================
    @GetMapping("/eleves/liste")
    public String listeEleves(Model model) {

        model.addAttribute("eleves", eleveRepo.findAll());

        return "secretaire/eleves_liste";
    }
}