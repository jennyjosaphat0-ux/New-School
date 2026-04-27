package com.example.NewSchool.repository;
import com.example.NewSchool.model.Affectation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface AffectationRepository extends JpaRepository<Affectation, Long> {
    List<Affectation> findByProfesseurId(Long profId);
    List<Affectation> findByClasseId(Long classeId);
}
