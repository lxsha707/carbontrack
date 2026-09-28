package com.example.carbontrack.service;

import com.example.carbontrack.model.Challenge;
import com.example.carbontrack.model.Household;
import com.example.carbontrack.model.Participation;
import com.example.carbontrack.repository.ChallengeRepository;
import com.example.carbontrack.repository.HouseholdRepository;
import com.example.carbontrack.repository.ParticipationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final HouseholdRepository householdRepository;
    private final ChallengeRepository challengeRepository;

    public ParticipationService(
            ParticipationRepository participationRepository,
            HouseholdRepository householdRepository,
            ChallengeRepository challengeRepository) {

        this.participationRepository = participationRepository;
        this.householdRepository = householdRepository;
        this.challengeRepository = challengeRepository;
    }

    // JOIN A CHALLENGE
    public Participation joinChallenge(
            Long householdId,
            Long challengeId) {

        Household household = householdRepository.findById(householdId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Household not found with id: " + householdId
                        ));

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Challenge not found with id: " + challengeId
                        ));

        // Prevent duplicate participation
        List<Participation> existing =
                participationRepository.findByHouseholdId(householdId);

        for (Participation participation : existing) {

            if (participation.getChallenge()
                    .getId()
                    .equals(challengeId)) {

                throw new RuntimeException(
                        "Household is already participating in this challenge"
                );
            }
        }

        // Check challenge dates
        LocalDate today = LocalDate.now();

        if (today.isBefore(challenge.getStartDate())) {
            throw new RuntimeException(
                    "Challenge has not started yet"
            );
        }

        if (today.isAfter(challenge.getEndDate())) {
            throw new RuntimeException(
                    "Challenge has already ended"
            );
        }

        Participation participation = new Participation();

        participation.setHousehold(household);
        participation.setChallenge(challenge);
        participation.setJoinedDate(today);

        return participationRepository.save(participation);
    }

    // GET ALL PARTICIPATIONS
    public List<Participation> getAllParticipations() {
        return participationRepository.findAll();
    }

    // GET PARTICIPATION BY ID
    public Participation getParticipationById(Long id) {

        return participationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Participation not found with id: " + id
                        ));
    }

    // GET PARTICIPATIONS OF A HOUSEHOLD
    public List<Participation> getByHousehold(Long householdId) {

        if (!householdRepository.existsById(householdId)) {
            throw new RuntimeException(
                    "Household not found with id: " + householdId
            );
        }

        return participationRepository
                .findByHouseholdId(householdId);
    }

    // GET PARTICIPANTS OF A CHALLENGE
    public List<Participation> getByChallenge(Long challengeId) {

        if (!challengeRepository.existsById(challengeId)) {
            throw new RuntimeException(
                    "Challenge not found with id: " + challengeId
            );
        }

        return participationRepository
                .findByChallengeId(challengeId);
    }

    // DELETE PARTICIPATION / LEAVE CHALLENGE
    public void deleteParticipation(Long id) {

        if (!participationRepository.existsById(id)) {
            throw new RuntimeException(
                    "Participation not found with id: " + id
            );
        }

        participationRepository.deleteById(id);
    }
}