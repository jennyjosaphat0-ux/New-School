package com.example.NewSchool.repository;
import com.example.NewSchool.model.Professeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface ProfesseurRepository extends JpaRepository<Professeur, Long> {
    Optional<Professeur> findByEmail(String email);
    List<Professeur> findByActif(boolean actif);
}
