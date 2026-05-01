package com.example.NewSchool.controller;

import com.example.NewSchool.model.Eleve;
import com.example.NewSchool.repository.EleveRepository;
import com.example.NewSchool.repository.ClasseRepository;
import com.example.NewSchool.service.PaiementService;
import com.example.NewSchool.service.SecretaireService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/secretaire")
public class SecretaireController {

    private final SecretaireService secService;
    private final EleveRepository eleveRepo;
    private final ClasseRepository classeRepo;
    private final PaiementService paiementService;

    public SecretaireController(SecretaireService secService, EleveRepository eleveRepo,
                                 ClasseRepository classeRepo, PaiementService paiementService) {
        this.secService = secService;
        this.eleveRepo = eleveRepo;
        this.classeRepo = classeRepo;
        this.paiementService = paiementService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalEleves", eleveRepo.count());
        model.addAttribute("totalClasses", classeRepo.count());
        // Dènye 5 elèv enrejistre
        List<Eleve> tous = eleveRepo.findAll();
        List<Eleve> derniers = tous.size() > 5 ? tous.subList(tous.size()-5, tous.size()) : tous;
        Collections.reverse(derniers);
        model.addAttribute("derniersEleves", derniers);
        return "secretaire/dashboard";
    }

    @GetMapping("/eleve/nouveau")
    public String form(Model m) {
        m.addAttribute("eleve", new Eleve());
        m.addAttribute("classes", classeRepo.findAll());
        return "secretaire/eleve-form";
    }

    @PostMapping("/eleve/sauvegarder")
    public String save(@ModelAttribute Eleve eleve,
                       @RequestParam Long classeId,
                       RedirectAttributes ra) {
        try {
            String code = secService.enregistrerEleve(eleve, classeId);
            ra.addFlashAttribute("success",
                "✅ Élève " + eleve.getNom() + " enregistré! Code: " + code +
                " — Un email a été envoyé avec les instructions de paiement.");
            return "redirect:/secretaire/eleves/liste";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "❌ Erreur: " + e.getMessage());
            return "redirect:/secretaire/eleve/nouveau";
        }
    }

    @GetMapping("/eleve/liste")
    public String listeEleves(Model model) {
        List<Eleve> eleves = eleveRepo.findAll();
        Map<Long, String> statutsPaiement = new LinkedHashMap<>();
        for (Eleve e : eleves) {
            statutsPaiement.put(e.getId(), paiementService.getStatutPaiementEleve(e.getId()));
        }
        model.addAttribute("eleves", eleves);
        model.addAttribute("statutsPaiement", statutsPaiement);
        return "secretaire/eleves-liste";
    }

    @GetMapping("/eleves/liste")
    public String listeElevesAlt(Model model) {
        return listeEleves(model);
    }

    @GetMapping("/eleves/classe/{id}")
    public String elevesClasse(@PathVariable Long id, Model m) {
        List<Eleve> eleves = eleveRepo.findByClasseId(id);
        Map<Long, String> statutsPaiement = new LinkedHashMap<>();
        for (Eleve e : eleves) {
            statutsPaiement.put(e.getId(), paiementService.getStatutPaiementEleve(e.getId()));
        }
        m.addAttribute("eleves", eleves);
        m.addAttribute("statutsPaiement", statutsPaiement);
        m.addAttribute("classe", classeRepo.findById(id).orElseThrow());
        return "secretaire/eleves-liste";
    }

    @GetMapping("/eleves")
    public String classesEleves(Model m) {
        m.addAttribute("classes", classeRepo.findAll());
        return "secretaire/eleves-classes";
    }
}
