package com.taskmanager.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Task {
    private String id;
    private String title;
    private String description;
    private String category;
    private String priority;
    private String status;
    private int progress;
    private String createdBy;
    private String createdAt;
    // Field Baru
    private String deadline;

    // Constructor untuk Task Baru
    public Task(String title, String description, String category, String priority, String createdBy, String deadline) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.status = "draft";
        this.progress = 0;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.deadline = deadline; // Simpan deadline
    }

    // Constructor untuk Load dari CSV
    public Task(String id, String title, String description, String category, String priority,
            String status, int progress, String createdBy, String createdAt, String deadline) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.status = status;
        this.progress = progress;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.deadline = deadline;
    }

    public String toCSV() {
        // Escape newlines in description to prevent CSV breakage
        String safeDescription = description != null ? description.replace("\n", "\\n") : "";
        return id + "," + title + "," + safeDescription + "," + category + "," + priority + "," +
                status + "," + progress + "," + createdBy + "," + createdAt + "," + (deadline != null ? deadline : "");
    }

    // --- LOGIKA HITUNG MUNDUR ---
    public String getTimeRemaining() {
        if (deadline == null || deadline.isEmpty())
            return "No Deadline";

        try {
            LocalDateTime due = LocalDateTime.parse(deadline, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime now = LocalDateTime.now();

            long days = ChronoUnit.DAYS.between(now, due);
            long hours = ChronoUnit.HOURS.between(now, due) % 24;
            long minutes = ChronoUnit.MINUTES.between(now, due) % 60;

            if (now.isAfter(due)) {
                return "OVERDUE!";
            } else if (days > 0) {
                return days + "d " + hours + "h left";
            } else {
                return hours + "h " + minutes + "m left";
            }
        } catch (Exception e) {
            return "Invalid Date";
        }
    }

    // Getters & Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}