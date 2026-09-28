package com.example.carbontrack.repository;

import com.example.carbontrack.model.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    List<Participation> findByHouseholdId(Long householdId);

    List<Participation> findByChallengeId(Long challengeId);
}