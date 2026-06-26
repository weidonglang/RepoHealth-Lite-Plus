package com.repohealth.service;

import com.repohealth.model.ActivityLevel;
import com.repohealth.model.RepositoryActivity;
import com.repohealth.model.RepositoryInfo;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class RepositoryActivityAnalyzer {

    public RepositoryActivity analyze(RepositoryInfo repo) {
        String repositoryName = repo.getName();
        Instant createdAt = repo.getCreatedAt();
        Instant updatedAt = repo.getUpdatedAt();
        Instant pushedAt = repo.getPushedAt();

        Instant now = Instant.now();
        long daysSinceLastPush = -1;
        long daysSinceLastUpdate = -1;
        List<String> activityNotes = new ArrayList<>();

        if (pushedAt != null) {
            daysSinceLastPush = Duration.between(pushedAt, now).toDays();
        }
        if (updatedAt != null) {
            daysSinceLastUpdate = Duration.between(updatedAt, now).toDays();
        }

        ActivityLevel activityLevel = determineActivityLevel(pushedAt, daysSinceLastPush);

        if (activityLevel == ActivityLevel.INACTIVE) {
            activityNotes.add("No push in over 180 days. Consider archiving if no longer maintained.");
        } else if (activityLevel == ActivityLevel.STALE) {
            activityNotes.add("Last push was between 90 and 180 days ago. Consider updating if still active.");
        } else if (activityLevel == ActivityLevel.STABLE) {
            activityNotes.add("Repository is stable with active maintenance.");
        } else if (activityLevel == ActivityLevel.ACTIVE) {
            activityNotes.add("Actively maintained with recent pushes.");
        } else {
            activityNotes.add("Cannot determine activity: no push date available.");
        }

        return new RepositoryActivity(
                repositoryName, createdAt, updatedAt, pushedAt,
                daysSinceLastPush, daysSinceLastUpdate, activityLevel, activityNotes
        );
    }

    private ActivityLevel determineActivityLevel(Instant pushedAt, long daysSinceLastPush) {
        if (pushedAt == null) {
            return ActivityLevel.UNKNOWN;
        }
        if (daysSinceLastPush <= 30) {
            return ActivityLevel.ACTIVE;
        } else if (daysSinceLastPush <= 90) {
            return ActivityLevel.STABLE;
        } else if (daysSinceLastPush <= 180) {
            return ActivityLevel.STALE;
        } else {
            return ActivityLevel.INACTIVE;
        }
    }
}