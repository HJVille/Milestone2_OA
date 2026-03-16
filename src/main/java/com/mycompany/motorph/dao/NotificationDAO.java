package com.mycompany.motorph.dao;

import com.mycompany.motorph.model.NotificationEntry;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationDAO {

    private static final String HEADER = "timestamp,actor,role,action,details,isRead";
    private static final Logger LOGGER = Logger.getLogger(NotificationDAO.class.getName());
    private final Path notificationPath;

    public NotificationDAO() {
        this(CsvFilePaths.NOTIFICATIONS);
    }

    public NotificationDAO(Path notificationPath) {
        this.notificationPath = notificationPath;
    }

    public List<NotificationEntry> loadAll() {
        List<NotificationEntry> notifications = new ArrayList<>();

        if (!Files.exists(notificationPath)) {
            return notifications;
        }

        try {
            boolean firstLine = true;
            for (String line : Files.readAllLines(notificationPath)) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",", 6);
                if (parts.length < 5) {
                    continue;
                }

                notifications.add(new NotificationEntry(
                        clean(parts[0]),
                        clean(parts[1]),
                        clean(parts[2]),
                        clean(parts[3]),
                        clean(parts[4]),
                        parts.length >= 6 && Boolean.parseBoolean(clean(parts[5]))
                ));
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Unable to load notifications.", e);
        }

        notifications.sort(Comparator.comparing(NotificationEntry::getTimestamp).reversed());
        return notifications;
    }

    public void append(NotificationEntry notification) {
        try {
            if (notificationPath.getParent() != null) {
                Files.createDirectories(notificationPath.getParent());
            }

            boolean fileExists = Files.exists(notificationPath);
            try (BufferedWriter writer = Files.newBufferedWriter(
                    notificationPath,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND)) {

                if (!fileExists) {
                    writer.write(HEADER);
                    writer.newLine();
                }

                writer.write(toCsvRow(notification));
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Unable to append notification.", e);
        }
    }

    public void saveAll(List<NotificationEntry> notifications) {
        try {
            if (notificationPath.getParent() != null) {
                Files.createDirectories(notificationPath.getParent());
            }

            try (BufferedWriter writer = Files.newBufferedWriter(
                    notificationPath,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {

                writer.write(HEADER);
                writer.newLine();
                for (NotificationEntry notification : notifications) {
                    writer.write(toCsvRow(notification));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Unable to save notifications.", e);
        }
    }

    private String toCsvRow(NotificationEntry notification) {
        return String.join(",",
                sanitize(notification.getTimestamp()),
                sanitize(notification.getActor()),
                sanitize(notification.getRole()),
                sanitize(notification.getAction()),
                sanitize(notification.getDetails()),
                String.valueOf(notification.isRead()));
    }

    private String sanitize(String value) {
        return clean(value)
                .replace("\r", " ")
                .replace("\n", " ")
                .replace(",", ";");
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
