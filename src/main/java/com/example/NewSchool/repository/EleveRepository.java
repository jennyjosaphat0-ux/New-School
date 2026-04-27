package com.example.NewSchool.repository;
import com.example.NewSchool.model.Eleve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface EleveRepository extends JpaRepository<Eleve, Long> {
    Optional<Eleve> findByEmail(String email);
    List<Eleve> findByClasseId(Long classeId);
    List<Eleve> findByExclu(boolean exclu);
}
