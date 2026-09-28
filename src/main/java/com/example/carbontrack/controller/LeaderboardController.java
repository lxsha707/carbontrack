package com.example.carbontrack.controller;

import com.example.carbontrack.service.LeaderboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(
            LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    // GET LEADERBOARD
    // Example:
    // /api/leaderboard?year=2026&month=9
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getLeaderboard(
            @RequestParam int year,
            @RequestParam int month) {

        return ResponseEntity.ok(
                leaderboardService.getLeaderboard(
                        year,
                        month
                )
        );
    }
}