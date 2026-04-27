package com.example.NewSchool.repository;
import com.example.NewSchool.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByEleveId(Long eleveId);
    List<Note> findByClasseIdAndTrimestre(Long classeId, Integer trimestre);
    List<Note> findByEleveIdAndTrimestre(Long eleveId, Integer trimestre);
    List<Note> findByProfesseurIdAndClasseId(Long profId, Long classeId);
    List<Note> findByEleveIdAndClasseIdAndTrimestre(Long eleveId, Long classeId, Integer trimestre);
}
