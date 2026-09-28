package com.example.carbontrack.service;

import com.example.carbontrack.model.ActivityLog;
import com.example.carbontrack.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CarbonService {

    private final ActivityLogRepository activityLogRepository;

    public CarbonService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    // ============================================================
    // FIXED EMISSION FACTORS
    // ============================================================

    // Transport factors: kg CO2e per passenger-km
    private static final double CAR_FACTOR = 0.115;
    private static final double BUS_FACTOR = 0.10385;
    private static final double BIKE_FACTOR = 0.0;
    private static final double WALK_FACTOR = 0.0;

    // Indian grid electricity factor:
    // 0.716 kg CO2 per kWh
    private static final double ELECTRICITY_FACTOR = 0.716;

    // Project assumption:
    // 0.50 kg CO2e per kg of household waste
    private static final double WASTE_FACTOR = 0.50;


    // ============================================================
    // CALCULATE CARBON FOR ONE ACTIVITY
    // ============================================================

    public double calculateActivityCarbon(ActivityLog activity) {

        double transportCarbon =
                calculateTransportCarbon(activity);

        double electricityCarbon =
                safeValue(activity.getElectricityConsumption())
                        * ELECTRICITY_FACTOR;

        double wasteCarbon =
                safeValue(activity.getWasteGenerated())
                        * WASTE_FACTOR;

        return round(
                transportCarbon
                        + electricityCarbon
                        + wasteCarbon
        );
    }


    // ============================================================
    // TRANSPORT CARBON
    // ============================================================

    private double calculateTransportCarbon(ActivityLog activity) {

        if (activity.getCommuteMode() == null) {
            return 0.0;
        }

        double distance =
                safeValue(activity.getCommuteDistanceKm());

        String mode =
                activity.getCommuteMode()
                        .trim()
                        .toUpperCase();

        double factor;

        switch (mode) {

            case "CAR":
                factor = CAR_FACTOR;
                break;

            case "BUS":
                factor = BUS_FACTOR;
                break;

            case "BIKE":
            case "BICYCLE":
                factor = BIKE_FACTOR;
                break;

            case "WALK":
            case "WALKING":
                factor = WALK_FACTOR;
                break;

            default:
                factor = 0.0;
        }

        return distance * factor;
    }


    // ============================================================
    // TOTAL HOUSEHOLD CARBON
    // ============================================================

    public double calculateHouseholdCarbon(Long householdId) {

        List<ActivityLog> activities =
                activityLogRepository.findByHouseholdId(
                        householdId
                );

        double total = 0.0;

        for (ActivityLog activity : activities) {

            total += calculateActivityCarbon(activity);
        }

        return round(total);
    }


    // ============================================================
    // MONTHLY CARBON
    // ============================================================

    public double calculateMonthlyCarbon(
            Long householdId,
            int year,
            int month) {

        List<ActivityLog> activities =
                activityLogRepository.findByHouseholdId(
                        householdId
                );

        double total = 0.0;

        for (ActivityLog activity : activities) {

            LocalDate date =
                    activity.getDate();

            if (date != null
                    && date.getYear() == year
                    && date.getMonthValue() == month) {

                total += calculateActivityCarbon(
                        activity
                );
            }
        }

        return round(total);
    }


    // ============================================================
    // MONTH-TO-MONTH REDUCTION
    // ============================================================

    public double calculateReductionPercentage(
            Long householdId,
            int currentYear,
            int currentMonth) {

        LocalDate currentDate =
                LocalDate.of(
                        currentYear,
                        currentMonth,
                        1
                );

        LocalDate previousDate =
                currentDate.minusMonths(1);

        double previousCarbon =
                calculateMonthlyCarbon(
                        householdId,
                        previousDate.getYear(),
                        previousDate.getMonthValue()
                );

        double currentCarbon =
                calculateMonthlyCarbon(
                        householdId,
                        currentYear,
                        currentMonth
                );

        if (previousCarbon <= 0) {
            return 0.0;
        }

        double reduction =
                ((previousCarbon - currentCarbon)
                        / previousCarbon)
                        * 100.0;

        return round(reduction);
    }


    // ============================================================
    // SAFE NUMERIC VALUE
    // ============================================================

    private double safeValue(Double value) {

        if (value == null) {
            return 0.0;
        }

        return Math.max(value, 0.0);
    }


    // ============================================================
    // ROUND TO 2 DECIMAL PLACES
    // ============================================================

    private double round(double value) {

        return Math.round(value * 100.0) / 100.0;
    }
}