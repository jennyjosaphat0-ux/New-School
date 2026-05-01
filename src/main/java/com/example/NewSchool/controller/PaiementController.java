package com.example.NewSchool.controller;

import com.example.NewSchool.model.*;
import com.example.NewSchool.model.Paiement.StatutPaiement;
import com.example.NewSchool.model.Paiement.TypePaiement;
import com.example.NewSchool.repository.*;
import com.example.NewSchool.service.*;
import com.stripe.model.checkout.Session;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
public class PaiementController {

    private final PaiementService paiementService;
    private final StripeService stripeService;
    private final EleveRepository eleveRepo;
    private final PaiementRepository paiementRepo;
    private final ClasseRepository classeRepo;

    public PaiementController(PaiementService paiementService, StripeService stripeService,
                               EleveRepository eleveRepo, PaiementRepository paiementRepo,
                               ClasseRepository classeRepo) {
        this.paiementService = paiementService;
        this.stripeService = stripeService;
        this.eleveRepo = eleveRepo;
        this.paiementRepo = paiementRepo;
        this.classeRepo = classeRepo;
    }

    // ===== ESPACE ELEVE =====

    // Paj paiement elèv
    @GetMapping("/eleve/paiement")
    public String pagePaiement(@AuthenticationPrincipal UserDetails user, Model m) {
        Eleve eleve = eleveRepo.findByEmail(user.getUsername()).orElseThrow();
        List<Paiement> paiements = paiementService.getPaiementsEleve(eleve.getId());
        m.addAttribute("eleve", eleve);
        m.addAttribute("paiements", paiements);
        m.addAttribute("peutVoirT1", paiementService.peutVoirBulletin(eleve.getId(), 1));
        m.addAttribute("peutVoirT2", paiementService.peutVoirBulletin(eleve.getId(), 2));
        m.addAttribute("peutVoirT3", paiementService.peutVoirBulletin(eleve.getId(), 3));
        return "eleve/paiement";
    }

    // Lanse paiement Stripe
    @PostMapping("/eleve/paiement/checkout")
    public String checkout(@RequestParam Long paiementId,
                           @AuthenticationPrincipal UserDetails user,
                           RedirectAttributes ra) {
        try {
            Eleve eleve = eleveRepo.findByEmail(user.getUsername()).orElseThrow();
            Paiement paiement = paiementRepo.findById(paiementId).orElseThrow();

            // Verifye ke se paiement elèv sa a
            if (!paiement.getEleve().getId().equals(eleve.getId())) {
                ra.addFlashAttribute("error", "Aksyon pa otorize.");
                return "redirect:/eleve/paiement";
            }

            Session session = stripeService.createCheckoutSession(
                paiement.getTypePaiementLabel(),
                paiement.getMontant(),
                eleve.getId().toString(),
                paiement.getTypePaiement().name()
            );

            // Sove session ID nan paiement an
            paiementService.enregistrerStripeSession(paiementId, session.getId());

            // Redirijè elèv la sou paj paiement Stripe
            return "redirect:" + session.getUrl();

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erè paiement: " + e.getMessage());
            return "redirect:/eleve/paiement";
        }
    }

    // Stripe retounen apre siksè
    @GetMapping("/eleve/paiement/succes")
    public String paiementSucces(@RequestParam("session_id") String sessionId,
                                  @AuthenticationPrincipal UserDetails user,
                                  RedirectAttributes ra) {
        try {
            Session session = stripeService.retrieveSession(sessionId);
            if ("paid".equals(session.getPaymentStatus())) {
                paiementService.confirmerPaiement(sessionId);
                ra.addFlashAttribute("success", "✅ Paiement konfime! Mèsi.");
            } else {
                ra.addFlashAttribute("error", "Paiement pa konfime.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erè verifikasyon: " + e.getMessage());
        }
        return "redirect:/eleve/paiement";
    }

    // Stripe retounen si anile
    @GetMapping("/eleve/paiement/annule")
    public String paiementAnnule(RedirectAttributes ra) {
        ra.addFlashAttribute("error", "Paiement anile.");
        return "redirect:/eleve/paiement";
    }

    // ===== ESPACE ADMIN =====

    @GetMapping("/admin/paiements")
    public String historiqueAdmin(Model m) {
        m.addAttribute("paiements", paiementService.getAllPaiements());
        m.addAttribute("classes", classeRepo.findAll());
        return "admin/paiements";
    }

    @GetMapping("/admin/paiements/classe/{classeId}")
    public String paiementsParClasse(@PathVariable Long classeId, Model m) {
        m.addAttribute("paiements", paiementService.getPaiementsParClasse(classeId));
        m.addAttribute("classe", classeRepo.findById(classeId).orElseThrow());
        return "admin/paiements-classe";
    }

    @GetMapping("/admin/paiements/eleve/{eleveId}")
    public String paiementsEleve(@PathVariable Long eleveId, Model m) {
        Eleve eleve = eleveRepo.findById(eleveId).orElseThrow();
        m.addAttribute("eleve", eleve);
        m.addAttribute("paiements", paiementService.getPaiementsEleve(eleveId));
        m.addAttribute("statutResume", paiementService.getStatutPaiementEleve(eleveId));
        return "admin/paiements-eleve";
    }

    // ===== ESPACE SECRETAIRE =====

    @GetMapping("/secretaire/paiements")
    public String paiementsSec(Model m) {
        m.addAttribute("paiements", paiementService.getAllPaiements());
        m.addAttribute("classes", classeRepo.findAll());
        return "secretaire/paiements";
    }

    @GetMapping("/secretaire/paiements/classe/{classeId}")
    public String paiementsClasseSec(@PathVariable Long classeId, Model m) {
        m.addAttribute("paiements", paiementService.getPaiementsParClasse(classeId));
        m.addAttribute("classe", classeRepo.findById(classeId).orElseThrow());
        return "secretaire/paiements-classe";
    }
}
