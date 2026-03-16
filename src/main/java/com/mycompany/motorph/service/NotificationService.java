package com.mycompany.motorph.service;

import com.mycompany.motorph.dao.NotificationDAO;
import com.mycompany.motorph.model.NotificationEntry;
import com.mycompany.motorph.model.User;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationService {

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final NotificationDAO notificationDAO;
    private final Clock clock;

    public NotificationService() {
        this(new NotificationDAO(), AppClock.clock());
    }

    public NotificationService(NotificationDAO notificationDAO, Clock clock) {
        this.notificationDAO = Objects.requireNonNull(notificationDAO, "notificationDAO");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public void record(User actor, String action, String details) {
        if (actor == null) {
            record("System", "SYSTEM", action, details);
            return;
        }

        record(actor.getUsername(), actor.getRole(), action, details);
    }

    public void record(String actor, String role, String action, String details) {
        notificationDAO.append(new NotificationEntry(
                LocalDateTime.now(clock).format(TIMESTAMP),
                actor == null || actor.isBlank() ? "System" : actor.trim(),
                role == null || role.isBlank() ? "SYSTEM" : role.trim(),
                action == null ? "" : action.trim(),
                details == null ? "" : details.trim()
        ));
    }

    public List<NotificationEntry> getRecentNotifications(int limit) {
        List<NotificationEntry> allNotifications = new ArrayList<>(notificationDAO.loadAll());
        if (limit <= 0 || allNotifications.size() <= limit) {
            return allNotifications;
        }
        return new ArrayList<>(allNotifications.subList(0, limit));
    }

    public void markAsRead(List<NotificationEntry> targetNotifications) {
        if (targetNotifications == null || targetNotifications.isEmpty()) {
            return;
        }

        List<NotificationEntry> allNotifications = new ArrayList<>(notificationDAO.loadAll());
        boolean updated = false;

        for (NotificationEntry stored : allNotifications) {
            if (stored.isRead()) {
                continue;
            }

            for (NotificationEntry target : targetNotifications) {
                if (sameNotification(stored, target)) {
                    stored.setRead(true);
                    updated = true;
                    break;
                }
            }
        }

        if (updated) {
            notificationDAO.saveAll(allNotifications);
        }
    }

    private boolean sameNotification(NotificationEntry left, NotificationEntry right) {
        return safe(left.getTimestamp()).equals(safe(right.getTimestamp()))
                && safe(left.getActor()).equals(safe(right.getActor()))
                && safe(left.getRole()).equals(safe(right.getRole()))
                && safe(left.getAction()).equals(safe(right.getAction()))
                && safe(left.getDetails()).equals(safe(right.getDetails()));
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
