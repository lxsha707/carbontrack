package com.example.carbontrack.controller;

import com.example.carbontrack.model.Challenge;
import com.example.carbontrack.service.ChallengeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    // CREATE CHALLENGE
    @PostMapping
    public ResponseEntity<Challenge> createChallenge(
            @RequestBody Challenge challenge) {

        Challenge created =
                challengeService.createChallenge(challenge);

        return new ResponseEntity<>(
                created,
                HttpStatus.CREATED
        );
    }

    // GET ALL CHALLENGES
    @GetMapping
    public ResponseEntity<List<Challenge>> getAllChallenges() {

        return ResponseEntity.ok(
                challengeService.getAllChallenges()
        );
    }

    // GET CHALLENGE BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Challenge> getChallengeById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                challengeService.getChallengeById(id)
        );
    }

    // UPDATE CHALLENGE
    @PutMapping("/{id}")
    public ResponseEntity<Challenge> updateChallenge(
            @PathVariable Long id,
            @RequestBody Challenge challenge) {

        return ResponseEntity.ok(
                challengeService.updateChallenge(
                        id,
                        challenge
                )
        );
    }

    // DELETE CHALLENGE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteChallenge(
            @PathVariable Long id) {

        challengeService.deleteChallenge(id);

        return ResponseEntity.ok(
                "Challenge deleted successfully"
        );
    }
}