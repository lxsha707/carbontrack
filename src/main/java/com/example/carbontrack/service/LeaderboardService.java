package com.example.carbontrack.service;

import com.example.carbontrack.model.Household;
import com.example.carbontrack.repository.HouseholdRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class LeaderboardService {

    private final HouseholdRepository householdRepository;
    private final CarbonService carbonService;

    public LeaderboardService(
            HouseholdRepository householdRepository,
            CarbonService carbonService) {

        this.householdRepository = householdRepository;
        this.carbonService = carbonService;
    }

    public List<Map<String, Object>> getLeaderboard(
            int year,
            int month) {

        // Validate month
        if (month < 1 || month > 12) {
            throw new RuntimeException(
                    "Month must be between 1 and 12"
            );
        }

        // Calculate previous month
        LocalDate currentDate =
                LocalDate.of(year, month, 1);

        LocalDate previousDate =
                currentDate.minusMonths(1);

        List<Household> households =
                householdRepository.findAll();

        List<Map<String, Object>> leaderboard =
                new ArrayList<>();

        for (Household household : households) {

            Long householdId = household.getId();

            double previousMonthCarbon =
                    carbonService.calculateMonthlyCarbon(
                            householdId,
                            previousDate.getYear(),
                            previousDate.getMonthValue()
                    );

            double currentMonthCarbon =
                    carbonService.calculateMonthlyCarbon(
                            householdId,
                            year,
                            month
                    );

            double percentageReduction =
                    carbonService.calculateReductionPercentage(
                            householdId,
                            year,
                            month
                    );

            Map<String, Object> entry =
                    new LinkedHashMap<>();

            entry.put("householdId", householdId);
            entry.put("householdName", household.getName());
            entry.put("previousMonthCarbon",
                    previousMonthCarbon);
            entry.put("currentMonthCarbon",
                    currentMonthCarbon);
            entry.put("percentageReduction",
                    percentageReduction);

            leaderboard.add(entry);
        }

        // Rank by percentage reduction - highest first
        leaderboard.sort(
                (a, b) -> Double.compare(
                        ((Number) b.get("percentageReduction")).doubleValue(),
                        ((Number) a.get("percentageReduction")).doubleValue()
                )
        );

        // Add rank
        for (int i = 0; i < leaderboard.size(); i++) {
            leaderboard.get(i).put("rank", i + 1);
        }

        return leaderboard;
    }
}