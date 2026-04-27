package com.example.NewSchool.controller;

import com.example.NewSchool.model.*;
import com.example.NewSchool.repository.*;
import com.example.NewSchool.service.NoteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/prof")
public class ProfesseurController {

    private final NoteService noteService;
    private final ProfesseurRepository profRepo;
    private final ClasseRepository classeRepo;
    private final EleveRepository eleveRepo;
    private final NoteRepository noteRepo;
    private final MatiereRepository matiereRepo;
    private final AffectationRepository affRepo;
    private final AnneeScolaireRepository anneeRepo;

    // CONSTRUCTOR A LAMEN
    public ProfesseurController(NoteService noteService, 
                                ProfesseurRepository profRepo, 
                                ClasseRepository classeRepo, 
                                EleveRepository eleveRepo, 
                                NoteRepository noteRepo, 
                                MatiereRepository matiereRepo, 
                                AffectationRepository affRepo, 
                                AnneeScolaireRepository anneeRepo) {
        this.noteService = noteService;
        this.profRepo = profRepo;
        this.classeRepo = classeRepo;
        this.eleveRepo = eleveRepo;
        this.noteRepo = noteRepo;
        this.matiereRepo = matiereRepo;
        this.affRepo = affRepo;
        this.anneeRepo = anneeRepo;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails user, Model m) {
        Professeur prof = profRepo.findByEmail(user.getUsername()).orElseThrow();
        // Sèvi ak getId() ou te jenere a lamen nan klas Professeur
        List<Affectation> affs = affRepo.findByProfesseurId(prof.getId());
        m.addAttribute("prof", prof);
        m.addAttribute("affectations", affs);
        return "prof/dashboard";
    }

    @GetMapping("/classe/{classeId}/notes")
    public String saisieNotes(@PathVariable Long classeId,
                              @RequestParam(defaultValue = "1") int trimestre,
                              @AuthenticationPrincipal UserDetails user, Model m) {
        Professeur prof = profRepo.findByEmail(user.getUsername()).orElseThrow();
        m.addAttribute("prof", prof);
        m.addAttribute("eleves", eleveRepo.findByClasseId(classeId));
        m.addAttribute("notes", noteRepo.findByClasseIdAndTrimestre(classeId, trimestre));
        m.addAttribute("classe", classeRepo.findById(classeId).orElseThrow());
        m.addAttribute("matieres", matiereRepo.findAll());
        m.addAttribute("trimestre", trimestre);
        m.addAttribute("annee", anneeRepo.findByCouranteTrue().orElse(null));
        return "prof/notes-saisie";
    }

    @PostMapping("/notes/sauvegarder")
    public String saveNote(@ModelAttribute Note note,
                           @AuthenticationPrincipal UserDetails user,
                           RedirectAttributes ra) {
        Professeur prof = profRepo.findByEmail(user.getUsername()).orElseThrow();
        note.setProfesseur(prof);
        anneeRepo.findByCouranteTrue().ifPresent(note::setAnneeScolaire);
        noteRepo.save(note);
        ra.addFlashAttribute("success", "✅ Nòt yo sove avèk siksè!");
        return "redirect:/prof/classe/" + note.getClasse().getId() + "/notes";
    }

    @GetMapping("/notes/modifier/{id}")
    public String formModifier(@PathVariable Long id, Model m) {
        m.addAttribute("note", noteRepo.findById(id).orElseThrow());
        return "prof/note-modifier";
    }

    @PostMapping("/notes/modifier/{id}")
    public String saveModif(@PathVariable Long id, @ModelAttribute Note data,
                            RedirectAttributes ra) {
        Note note = noteRepo.findById(id).orElseThrow();
        // Asire w metòd sa yo egziste nan klas Note (setNote1, setNote2, etc.)
        note.setNote1(data.getNote1());
        note.setNote2(data.getNote2());
        note.setNoteExamen(data.getNoteExamen());
        noteRepo.save(note);
        ra.addFlashAttribute("success", "✅ Nòt la modifye — bulletin refèt otomatikman!");
        return "redirect:/prof/classe/" + note.getClasse().getId() + "/notes";
    }
}