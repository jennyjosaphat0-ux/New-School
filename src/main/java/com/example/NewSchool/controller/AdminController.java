package com.example.NewSchool.controller;

import com.example.NewSchool.model.*;
import com.example.NewSchool.repository.*;
import com.example.NewSchool.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final NoteService noteService;
    private final EleveRepository eleveRepo;
    private final ProfesseurRepository profRepo;
    private final ClasseRepository classeRepo;
    private final MatiereRepository matiereRepo;
    private final NoteRepository noteRepo;
    private final SecretaireRepository secRepo;

    // CONSTRUCTOR A LAMEN (Paske Lombok gen pwoblèm nan NetBeans)
    public AdminController(AdminService adminService, 
                           NoteService noteService, 
                           EleveRepository eleveRepo, 
                           ProfesseurRepository profRepo, 
                           ClasseRepository classeRepo, 
                           MatiereRepository matiereRepo, 
                           NoteRepository noteRepo, 
                           SecretaireRepository secRepo) {
        this.adminService = adminService;
        this.noteService = noteService;
        this.eleveRepo = eleveRepo;
        this.profRepo = profRepo;
        this.classeRepo = classeRepo;
        this.matiereRepo = matiereRepo;
        this.noteRepo = noteRepo;
        this.secRepo = secRepo;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model m) {
        m.addAttribute("totalProfs", profRepo.count());
        m.addAttribute("totalClasses", classeRepo.count());
        m.addAttribute("totalEleves", eleveRepo.count());
        m.addAttribute("totalSecretaires", secRepo.count());
        return "admin/dashboard";
    }

    // ===== PROFESSEURS =====
    @GetMapping("/prof/nouveau")
    public String formProf(Model m) {
        m.addAttribute("professeur", new Professeur());
        m.addAttribute("classes", classeRepo.findAll());
        m.addAttribute("matieres", matiereRepo.findAll());
        return "admin/prof-form";
    }

    @PostMapping("/prof/sauvegarder")
    public String saveProf(@ModelAttribute Professeur prof,
                           @RequestParam Long classeId,
                           @RequestParam(required = false) Long matiereId,
                           @RequestParam(defaultValue = "0") Integer nbreHeures,
                           RedirectAttributes ra) {
        String code = adminService.enregistrerProfesseur(prof, classeId, matiereId, nbreHeures);
        ra.addFlashAttribute("success", "✅ Pwofesè " + prof.getNom() + " anrejistre! Code: " + code);
        return "redirect:/admin/prof/liste";
    }

    @GetMapping("/prof/liste")
    public String listeProfs(Model m) {
        m.addAttribute("profs", adminService.getAllProfesseurs());
        return "admin/prof-liste";
    }

    @PostMapping("/prof/revoquer/{id}")
    public String revoquerProf(@PathVariable Long id, RedirectAttributes ra) {
        adminService.revoquerProfesseur(id);
        ra.addFlashAttribute("success", "Pwofesè a revoké avèk siksè.");
        return "redirect:/admin/prof/liste";
    }

    // ===== SECRETAIRES =====
    @GetMapping("/secretaire/nouveau")
    public String formSec(Model m) {
        m.addAttribute("secretaire", new Secretaire());
        return "admin/secretaire-form";
    }

    @PostMapping("/secretaire/sauvegarder")
    public String saveSec(@ModelAttribute Secretaire sec, RedirectAttributes ra) {
        String code = adminService.enregistrerSecretaire(sec);
        ra.addFlashAttribute("success", "✅ Secrétè " + sec.getNom() + " anrejistre! Code: " + code);
        return "redirect:/admin/secretaire/liste";
    }

    @GetMapping("/secretaire/liste")
    public String listeSec(Model m) {
        m.addAttribute("secretaires", adminService.getAllSecretaires());
        return "admin/secretaire-liste";
    }

    @PostMapping("/secretaire/revoquer/{id}")
    public String revoquerSec(@PathVariable Long id, RedirectAttributes ra) {
        adminService.revoquerSecretaire(id);
        ra.addFlashAttribute("success", "Secrétè a revoké.");
        return "redirect:/admin/secretaire/liste";
    }

    // ===== ÉLÈVES =====
    @GetMapping("/eleves")
    public String elevesClasses(Model m) {
        m.addAttribute("classes", classeRepo.findAll());
        return "admin/eleves-classes";
    }

    @GetMapping("/eleves/classe/{id}")
    public String elevesDeClasse(@PathVariable Long id, Model m) {
        m.addAttribute("eleves", eleveRepo.findByClasseId(id));
        m.addAttribute("classe", classeRepo.findById(id).orElseThrow());
        return "admin/eleves-liste";
    }

    @GetMapping("/eleves/{id}")
    public String ficheEleve(@PathVariable Long id, Model m) {
        Eleve e = eleveRepo.findById(id).orElseThrow();
        m.addAttribute("eleve", e);
        m.addAttribute("notes", noteRepo.findByEleveId(id));
        return "admin/eleve-fiche";
    }

    @PostMapping("/eleves/exclure/{id}")
    public String exclure(@PathVariable Long id, RedirectAttributes ra) {
        adminService.exclureEleve(id);
        ra.addFlashAttribute("success", "Elèv la eksklui.");
        return "redirect:/admin/eleves";
    }

    // ===== NOTES =====
    @GetMapping("/notes")
    public String notesClasses(Model m) {
        m.addAttribute("classes", classeRepo.findAll());
        return "admin/notes-classes";
    }

    @GetMapping("/notes/classe/{id}")
    public String notesClasse(@PathVariable Long id,
                              @RequestParam(defaultValue = "1") int trimestre,
                              Model m) {
        m.addAttribute("notes", noteService.getNotesClasse(id, trimestre));
        m.addAttribute("eleves", eleveRepo.findByClasseId(id));
        m.addAttribute("classe", classeRepo.findById(id).orElseThrow());
        m.addAttribute("trimestre", trimestre);
        return "admin/notes-classe";
    }

    // ===== BULLETINS =====
    @GetMapping("/bulletins")
    public String bulletinsClasses(Model m) {
        m.addAttribute("classes", classeRepo.findAll());
        return "admin/bulletins-classes";
    }

    @GetMapping("/bulletins/classe/{id}")
    public String bulletinsClasse(@PathVariable Long id,
                                  @RequestParam(defaultValue = "1") int trimestre,
                                  Model m) {
        m.addAttribute("eleves", eleveRepo.findByClasseId(id));
        m.addAttribute("classe", classeRepo.findById(id).orElseThrow());
        m.addAttribute("trimestre", trimestre);
        m.addAttribute("noteService", noteService);
        return "admin/bulletins-liste";
    }
}