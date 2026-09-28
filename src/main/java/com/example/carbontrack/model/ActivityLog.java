package com.example.carbontrack.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "activity_logs")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String commuteMode;

    @Column(nullable = false)
    private Double commuteDistanceKm;

    @Column(nullable = false)
    private Double electricityConsumption;

    @Column(nullable = false)
    private Double wasteGenerated;

    public ActivityLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Household getHousehold() {
        return household;
    }

    public void setHousehold(Household household) {
        this.household = household;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCommuteMode() {
        return commuteMode;
    }

    public void setCommuteMode(String commuteMode) {
        this.commuteMode = commuteMode;
    }

    public Double getCommuteDistanceKm() {
        return commuteDistanceKm;
    }

    public void setCommuteDistanceKm(Double commuteDistanceKm) {
        this.commuteDistanceKm = commuteDistanceKm;
    }

    public Double getElectricityConsumption() {
        return electricityConsumption;
    }

    public void setElectricityConsumption(Double electricityConsumption) {
        this.electricityConsumption = electricityConsumption;
    }

    public Double getWasteGenerated() {
        return wasteGenerated;
    }

    public void setWasteGenerated(Double wasteGenerated) {
        this.wasteGenerated = wasteGenerated;
    }
}