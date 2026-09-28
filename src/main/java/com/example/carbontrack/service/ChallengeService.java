package com.example.carbontrack.service;

import com.example.carbontrack.model.Challenge;
import com.example.carbontrack.repository.ChallengeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    public ChallengeService(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    // CREATE
    public Challenge createChallenge(Challenge challenge) {

        validateChallenge(challenge);

        return challengeRepository.save(challenge);
    }

    // GET ALL
    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }

    // GET BY ID
    public Challenge getChallengeById(Long id) {

        return challengeRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Challenge not found with id: " + id
                        ));
    }

    // UPDATE
    public Challenge updateChallenge(
            Long id,
            Challenge updatedChallenge) {

        Challenge existingChallenge =
                getChallengeById(id);

        validateChallenge(updatedChallenge);

        existingChallenge.setName(
                updatedChallenge.getName()
        );

        existingChallenge.setDescription(
                updatedChallenge.getDescription()
        );

        existingChallenge.setTargetReduction(
                updatedChallenge.getTargetReduction()
        );

        existingChallenge.setStartDate(
                updatedChallenge.getStartDate()
        );

        existingChallenge.setEndDate(
                updatedChallenge.getEndDate()
        );

        return challengeRepository.save(existingChallenge);
    }

    // DELETE
    public void deleteChallenge(Long id) {

        if (!challengeRepository.existsById(id)) {
            throw new RuntimeException(
                    "Challenge not found with id: " + id
            );
        }

        challengeRepository.deleteById(id);
    }

    // VALIDATION
    private void validateChallenge(Challenge challenge) {

        if (challenge.getName() == null ||
                challenge.getName().trim().isEmpty()) {

            throw new RuntimeException(
                    "Challenge name is required"
            );
        }

        if (challenge.getTargetReduction() == null ||
                challenge.getTargetReduction() < 0 ||
                challenge.getTargetReduction() > 100) {

            throw new RuntimeException(
                    "Target reduction must be between 0 and 100"
            );
        }

        if (challenge.getStartDate() == null) {
            throw new RuntimeException(
                    "Start date is required"
            );
        }

        if (challenge.getEndDate() == null) {
            throw new RuntimeException(
                    "End date is required"
            );
        }

        if (challenge.getEndDate()
                .isBefore(challenge.getStartDate())) {

            throw new RuntimeException(
                    "End date cannot be before start date"
            );
        }
    }
}