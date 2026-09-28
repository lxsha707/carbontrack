package com.example.carbontrack.controller;

import com.example.carbontrack.service.CarbonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/carbon")
public class CarbonController {

    private final CarbonService carbonService;

    public CarbonController(CarbonService carbonService) {
        this.carbonService = carbonService;
    }

    // Calculate total carbon for a household
    @GetMapping("/household/{householdId}")
    public ResponseEntity<Map<String, Object>> getHouseholdCarbon(
            @PathVariable Long householdId) {

        double carbon =
                carbonService.calculateHouseholdCarbon(
                        householdId
                );

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("householdId", householdId);
        response.put("totalCarbonKg", carbon);

        return ResponseEntity.ok(response);
    }

    // Calculate monthly carbon
    // Example:
    // /api/carbon/household/1/monthly?year=2026&month=9
    @GetMapping("/household/{householdId}/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyCarbon(
            @PathVariable Long householdId,
            @RequestParam int year,
            @RequestParam int month) {

        if (month < 1 || month > 12) {
            throw new RuntimeException(
                    "Month must be between 1 and 12"
            );
        }

        double carbon =
                carbonService.calculateMonthlyCarbon(
                        householdId,
                        year,
                        month
                );

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("householdId", householdId);
        response.put("year", year);
        response.put("month", month);
        response.put("monthlyCarbonKg", carbon);

        return ResponseEntity.ok(response);
    }

    // Calculate percentage reduction compared
    // with the previous month
    @GetMapping("/household/{householdId}/reduction")
    public ResponseEntity<Map<String, Object>> getReduction(
            @PathVariable Long householdId,
            @RequestParam int year,
            @RequestParam int month) {

        if (month < 1 || month > 12) {
            throw new RuntimeException(
                    "Month must be between 1 and 12"
            );
        }

        double reduction =
                carbonService.calculateReductionPercentage(
                        householdId,
                        year,
                        month
                );

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("householdId", householdId);
        response.put("year", year);
        response.put("month", month);
        response.put("percentageReduction", reduction);

        return ResponseEntity.ok(response);
    }
}