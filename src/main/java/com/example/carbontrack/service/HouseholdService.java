package com.example.carbontrack.service;

import com.example.carbontrack.model.Household;
import com.example.carbontrack.repository.HouseholdRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HouseholdService {

    private final HouseholdRepository householdRepository;

    public HouseholdService(HouseholdRepository householdRepository) {
        this.householdRepository = householdRepository;
    }

    // CREATE
    public Household createHousehold(Household household) {
        return householdRepository.save(household);
    }

    // READ ALL
    public List<Household> getAllHouseholds() {
        return householdRepository.findAll();
    }

    // READ ONE
    public Household getHouseholdById(Long id) {
        return householdRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Household not found with id: " + id));
    }

    // UPDATE
    public Household updateHousehold(Long id, Household updatedHousehold) {

        Household existingHousehold = getHouseholdById(id);

        existingHousehold.setName(updatedHousehold.getName());
        existingHousehold.setMembers(updatedHousehold.getMembers());
        existingHousehold.setLocation(updatedHousehold.getLocation());

        return householdRepository.save(existingHousehold);
    }

    // DELETE
    public void deleteHousehold(Long id) {

        if (!householdRepository.existsById(id)) {
            throw new RuntimeException(
                    "Household not found with id: " + id
            );
        }

        householdRepository.deleteById(id);
    }
}