package com.example.carbontrack.controller;

import com.example.carbontrack.model.ActivityLog;
import com.example.carbontrack.service.ActivityLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(
            ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    // CREATE ACTIVITY
    @PostMapping
    public ResponseEntity<ActivityLog> createActivity(
            @RequestBody ActivityLog activityLog) {

        ActivityLog created =
                activityLogService.createActivity(activityLog);

        return new ResponseEntity<>(
                created,
                HttpStatus.CREATED
        );
    }

    // GET ALL ACTIVITIES
    @GetMapping
    public ResponseEntity<List<ActivityLog>> getAllActivities() {

        return ResponseEntity.ok(
                activityLogService.getAllActivities()
        );
    }

    // GET ACTIVITY BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ActivityLog> getActivityById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                activityLogService.getActivityById(id)
        );
    }

    // GET ACTIVITIES OF A HOUSEHOLD
    @GetMapping("/household/{householdId}")
    public ResponseEntity<List<ActivityLog>> getActivitiesByHousehold(
            @PathVariable Long householdId) {

        return ResponseEntity.ok(
                activityLogService.getActivitiesByHousehold(
                        householdId
                )
        );
    }

    // UPDATE ACTIVITY
    @PutMapping("/{id}")
    public ResponseEntity<ActivityLog> updateActivity(
            @PathVariable Long id,
            @RequestBody ActivityLog activityLog) {

        return ResponseEntity.ok(
                activityLogService.updateActivity(
                        id,
                        activityLog
                )
        );
    }

    // DELETE ACTIVITY
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteActivity(
            @PathVariable Long id) {

        activityLogService.deleteActivity(id);

        return ResponseEntity.ok(
                "Activity deleted successfully"
        );
    }
}