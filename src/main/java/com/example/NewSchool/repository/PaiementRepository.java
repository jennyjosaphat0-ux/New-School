package com.example.NewSchool.repository;

import com.example.NewSchool.model.Paiement;
import com.example.NewSchool.model.Paiement.StatutPaiement;
import com.example.NewSchool.model.Paiement.TypePaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    List<Paiement> findByEleveId(Long eleveId);

    Optional<Paiement> findByEleveIdAndTypePaiement(Long eleveId, TypePaiement type);

    Optional<Paiement> findByStripeSessionId(String sessionId);

    List<Paiement> findByStatut(StatutPaiement statut);

    // Tout paiement pou yon klas
    @Query("SELECT p FROM Paiement p WHERE p.eleve.classe.id = :classeId ORDER BY p.dateCreation DESC")
    List<Paiement> findByEleveClasseId(Long classeId);

    // Tout paiement (pou admin - historik konplè)
    @Query("SELECT p FROM Paiement p ORDER BY p.dateCreation DESC")
    List<Paiement> findAllOrderByDate();

    // Verifye si yon elèv peye yon versement
    boolean existsByEleveIdAndTypePaiementAndStatut(Long eleveId, TypePaiement type, StatutPaiement statut);
}
