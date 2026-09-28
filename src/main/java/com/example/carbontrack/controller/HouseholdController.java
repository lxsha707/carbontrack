package com.example.carbontrack.controller;

import com.example.carbontrack.model.Household;
import com.example.carbontrack.service.HouseholdService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/households")
public class HouseholdController {

    private final HouseholdService householdService;

    public HouseholdController(HouseholdService householdService) {
        this.householdService = householdService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Household> createHousehold(
            @RequestBody Household household) {

        Household created =
                householdService.createHousehold(household);

        return new ResponseEntity<>(
                created,
                HttpStatus.CREATED
        );
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<Household>> getAllHouseholds() {

        return ResponseEntity.ok(
                householdService.getAllHouseholds()
        );
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Household> getHouseholdById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                householdService.getHouseholdById(id)
        );
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Household> updateHousehold(
            @PathVariable Long id,
            @RequestBody Household household) {

        return ResponseEntity.ok(
                householdService.updateHousehold(
                        id,
                        household
                )
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteHousehold(
            @PathVariable Long id) {

        householdService.deleteHousehold(id);

        return ResponseEntity.ok(
                "Household deleted successfully"
        );
    }
}