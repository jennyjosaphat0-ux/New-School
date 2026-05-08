package com.example.NewSchool.controller;

import com.example.NewSchool.model.Eleve;
import com.example.NewSchool.model.Paiement;
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
                " — Email envoyé avec instructions de paiement.");
            return "redirect:/secretaire/paiement/" + eleve.getId();
        } catch (Exception e) {
            ra.addFlashAttribute("error", "❌ Erreur: " + e.getMessage());
            return "redirect:/secretaire/eleve/nouveau";
        }
    }

    // ===== PAIEMENT =====
    @GetMapping("/paiement/{eleveId}")
    public String pagePaiement(@PathVariable Long eleveId, Model m) {
        Eleve eleve = eleveRepo.findById(eleveId).orElseThrow();
        m.addAttribute("eleve", eleve);
        m.addAttribute("paiements", paiementService.getPaiementsEleve(eleveId));
        return "secretaire/paiement-eleve";
    }

    @PostMapping("/paiement/checkout")
    public String checkout(@RequestParam Long paiementId) throws Exception {
        String successUrl = "https://new-school-7h76.onrender.com/secretaire/paiement/succes/" + paiementId;
        String cancelUrl  = "https://new-school-7h76.onrender.com/secretaire/paiement/annule/" + paiementId;
        String url = paiementService.creerSessionStripe(paiementId, successUrl, cancelUrl);
        return "redirect:" + url;
    }

    @GetMapping("/paiement/succes/{paiementId}")
    public String succes(@PathVariable Long paiementId, RedirectAttributes ra) {
        paiementService.confirmerPaiement(paiementId);
        Paiement p = paiementService.findById(paiementId);
        ra.addFlashAttribute("success", "✅ Paiement confirmé!");
        return "redirect:/secretaire/paiement/" + p.getEleve().getId();
    }

    @GetMapping("/paiement/annule/{paiementId}")
    public String annule(@PathVariable Long paiementId, RedirectAttributes ra) {
        Paiement p = paiementService.findById(paiementId);
        ra.addFlashAttribute("error", "❌ Paiement annulé.");
        return "redirect:/secretaire/paiement/" + p.getEleve().getId();
    }

    // ===== LISTES =====
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