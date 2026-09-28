package com.example.carbontrack.service;

import com.example.carbontrack.model.ActivityLog;
import com.example.carbontrack.repository.ActivityLogRepository;
import com.example.carbontrack.repository.HouseholdRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final HouseholdRepository householdRepository;

    public ActivityLogService(
            ActivityLogRepository activityLogRepository,
            HouseholdRepository householdRepository) {

        this.activityLogRepository = activityLogRepository;
        this.householdRepository = householdRepository;
    }


    // ============================================================
    // CREATE ACTIVITY
    // ============================================================

    public ActivityLog createActivity(ActivityLog activityLog) {

        validateActivity(activityLog);

        if (activityLog.getHousehold() == null ||
                activityLog.getHousehold().getId() == null) {

            throw new RuntimeException(
                    "Household ID is required"
            );
        }

        Long householdId =
                activityLog.getHousehold().getId();

        var household =
                householdRepository.findById(householdId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Household not found with id: "
                                                + householdId
                                ));

        activityLog.setHousehold(household);

        return activityLogRepository.save(activityLog);
    }


    // ============================================================
    // GET ALL ACTIVITIES
    // ============================================================

    public List<ActivityLog> getAllActivities() {

        return activityLogRepository.findAll();
    }


    // ============================================================
    // GET ACTIVITY BY ID
    // ============================================================

    public ActivityLog getActivityById(Long id) {

        return activityLogRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Activity not found with id: " + id
                        ));
    }


    // ============================================================
    // GET ACTIVITIES BY HOUSEHOLD
    // ============================================================

    public List<ActivityLog> getActivitiesByHousehold(
            Long householdId) {

        if (!householdRepository.existsById(householdId)) {

            throw new RuntimeException(
                    "Household not found with id: "
                            + householdId
            );
        }

        return activityLogRepository
                .findByHouseholdId(householdId);
    }


    // ============================================================
    // UPDATE ACTIVITY
    // ============================================================

    public ActivityLog updateActivity(
            Long id,
            ActivityLog updatedActivity) {

        validateActivity(updatedActivity);

        ActivityLog existingActivity =
                getActivityById(id);

        existingActivity.setDate(
                updatedActivity.getDate()
        );

        existingActivity.setCommuteMode(
                updatedActivity.getCommuteMode()
        );

        existingActivity.setCommuteDistanceKm(
                updatedActivity.getCommuteDistanceKm()
        );

        existingActivity.setElectricityConsumption(
                updatedActivity.getElectricityConsumption()
        );

        existingActivity.setWasteGenerated(
                updatedActivity.getWasteGenerated()
        );

        return activityLogRepository.save(
                existingActivity
        );
    }


    // ============================================================
    // DELETE ACTIVITY
    // ============================================================

    public void deleteActivity(Long id) {

        if (!activityLogRepository.existsById(id)) {

            throw new RuntimeException(
                    "Activity not found with id: " + id
            );
        }

        activityLogRepository.deleteById(id);
    }


    // ============================================================
    // VALIDATION
    // ============================================================

    private void validateActivity(
            ActivityLog activityLog) {

        if (activityLog == null) {

            throw new RuntimeException(
                    "Activity data is required"
            );
        }

        if (activityLog.getDate() == null) {

            throw new RuntimeException(
                    "Activity date is required"
            );
        }

        if (activityLog.getCommuteMode() == null ||
                activityLog.getCommuteMode()
                        .trim()
                        .isEmpty()) {

            throw new RuntimeException(
                    "Commute mode is required"
            );
        }

        if (activityLog.getCommuteDistanceKm() == null) {

            throw new RuntimeException(
                    "Commute distance is required"
            );
        }

        if (activityLog.getCommuteDistanceKm() < 0) {

            throw new RuntimeException(
                    "Commute distance cannot be negative"
            );
        }

        if (activityLog.getElectricityConsumption() == null) {

            throw new RuntimeException(
                    "Electricity consumption is required"
            );
        }

        if (activityLog.getElectricityConsumption() < 0) {

            throw new RuntimeException(
                    "Electricity consumption cannot be negative"
            );
        }

        if (activityLog.getWasteGenerated() == null) {

            throw new RuntimeException(
                    "Waste generated is required"
            );
        }

        if (activityLog.getWasteGenerated() < 0) {

            throw new RuntimeException(
                    "Waste generated cannot be negative"
            );
        }
    }
}