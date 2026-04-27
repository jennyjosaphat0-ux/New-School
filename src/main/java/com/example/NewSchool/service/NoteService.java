package com.example.NewSchool.service;

import com.example.NewSchool.model.*;
import com.example.NewSchool.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class NoteService {

    private final NoteRepository noteRepo;
    private final EleveRepository eleveRepo;

    // Constructor a lamen pou ranje erè konpilasyon NetBeans yo
    public NoteService(NoteRepository noteRepo, EleveRepository eleveRepo) {
        this.noteRepo = noteRepo;
        this.eleveRepo = eleveRepo;
    }

    @Transactional
    public void sauvegarder(Note note) { 
        noteRepo.save(note); 
    }

    public List<Note> getNotesClasse(Long classeId, Integer trimestre) {
        return noteRepo.findByClasseIdAndTrimestre(classeId, trimestre);
    }

    public List<Note> getNotesEleve(Long eleveId) {
        return noteRepo.findByEleveId(eleveId);
    }

    public List<Note> getNotesEleveTrimestre(Long eleveId, Integer trimestre) {
        return noteRepo.findByEleveIdAndTrimestre(eleveId, trimestre);
    }

    public Double getMoyenneGenerale(Long eleveId, Integer trimestre) {
        return noteRepo.findByEleveIdAndTrimestre(eleveId, trimestre)
            .stream()
            // Asire w ou te jenere getMoyenne() nan klas Note a lamen
            .map(Note::getMoyenne)
            .filter(Objects::nonNull)
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);
    }

    public int getRang(Long eleveId, Long classeId, Integer trimestre) {
        List<Eleve> tous = eleveRepo.findByClasseId(classeId);
        double maMoyenne = getMoyenneGenerale(eleveId, trimestre);
        
        long devant = tous.stream()
            // Sèvi ak metòd getId() ou te jenere a lamen nan klas Eleve
            .filter(e -> !e.getId().equals(eleveId))
            .filter(e -> getMoyenneGenerale(e.getId(), trimestre) > maMoyenne)
            .count();
            
        return (int) devant + 1;
    }
}