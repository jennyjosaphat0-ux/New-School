package com.example.NewSchool.repository;
import com.example.NewSchool.model.Secretaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface SecretaireRepository extends JpaRepository<Secretaire, Long> {
    Optional<Secretaire> findByEmail(String email);
}
