package com.example.NewSchool.controller;

import com.example.NewSchool.model.*;
import com.example.NewSchool.repository.*;
import com.example.NewSchool.service.BulletinPdfService;
import com.example.NewSchool.service.NoteService;
import com.example.NewSchool.service.PaiementService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/eleve")
public class EleveController {

    private final EleveRepository eleveRepo;
    private final NoteService noteService;
    private final PaiementService paiementService;
    private final BulletinPdfService pdfService;

    public EleveController(EleveRepository eleveRepo, NoteService noteService,
                            PaiementService paiementService, BulletinPdfService pdfService) {
        this.eleveRepo      = eleveRepo;
        this.noteService    = noteService;
        this.paiementService = paiementService;
        this.pdfService     = pdfService;
    }

    private Eleve getEleve(UserDetails user) {
        return eleveRepo.findByEmail(user.getUsername()).orElseThrow();
    }

    // ===== DASHBOARD =====
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails user, Model m) {
        Eleve e = getEleve(user);
        m.addAttribute("eleve", e);
        m.addAttribute("moyenne1", noteService.getMoyenneGenerale(e.getId(), 1));
        m.addAttribute("moyenne2", noteService.getMoyenneGenerale(e.getId(), 2));
        m.addAttribute("moyenne3", noteService.getMoyenneGenerale(e.getId(), 3));
        m.addAttribute("statutPaiement", paiementService.getStatutPaiementEleve(e.getId()));
        return "eleve/dashboard";
    }

    // ===== NOTES =====
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

    // ===== BULLETIN (HTML) — verifye paiement =====
    @GetMapping("/bulletin")
    public String bulletin(@AuthenticationPrincipal UserDetails user,
                           @RequestParam(defaultValue = "1") int trimestre, Model m) {
        Eleve e = getEleve(user);

        // Bloke si paiement pa konfime
        if (!paiementService.peutVoirBulletin(e.getId(), trimestre)) {
            m.addAttribute("eleve", e);
            m.addAttribute("trimestre", trimestre);
            m.addAttribute("paiements", paiementService.getPaiementsEleve(e.getId()));
            return "eleve/bulletin-bloque";
        }

        m.addAttribute("eleve", e);
        m.addAttribute("notes", noteService.getNotesEleveTrimestre(e.getId(), trimestre));
        m.addAttribute("moyenne", noteService.getMoyenneGenerale(e.getId(), trimestre));
        m.addAttribute("rang", noteService.getRang(e.getId(), e.getClasse().getId(), trimestre));
        m.addAttribute("trimestre", trimestre);
        return "eleve/bulletin";
    }

    // ===== TELECHAJE PDF — pa ka modifye =====
    @GetMapping("/bulletin/pdf")
    public void bulletinPdf(@AuthenticationPrincipal UserDetails user,
                             @RequestParam(defaultValue = "1") int trimestre,
                             HttpServletResponse response) throws Exception {
        Eleve eleve = getEleve(user);

        // Bloke si paiement pa konfime
        if (!paiementService.peutVoirBulletin(eleve.getId(), trimestre)) {
            response.sendRedirect("/eleve/bulletin?trimestre=" + trimestre);
            return;
        }

        List<Note> notes   = noteService.getNotesEleveTrimestre(eleve.getId(), trimestre);
        double moyenne     = noteService.getMoyenneGenerale(eleve.getId(), trimestre);
        int rang           = noteService.getRang(eleve.getId(), eleve.getClasse().getId(), trimestre);

        byte[] pdf = pdfService.genererBulletin(eleve, notes, moyenne, rang, trimestre);

        // Voye PDF ba navigatè — telechajman, pa ka modifye
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
            "attachment; filename=\"Bulletin_" + eleve.getNom() + "_T" + trimestre + ".pdf\"");
        response.setContentLength(pdf.length);
        response.getOutputStream().write(pdf);
    }

    // ===== PROFIL =====
    @GetMapping("/profil")
    public String profil(@AuthenticationPrincipal UserDetails user, Model m) {
        m.addAttribute("eleve", getEleve(user));
        return "eleve/profil";
    }
}
