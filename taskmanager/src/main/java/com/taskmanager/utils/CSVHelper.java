package com.taskmanager.utils;

import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CSVHelper {
    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + "/users.csv";
    private static final String TASKS_FILE = DATA_DIR + "/tasks.csv";

    // Inisialisasi file dan folder jika belum ada
    public static void initializeFiles() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            createFileIfNotExists(USERS_FILE);
            createFileIfNotExists(TASKS_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createFileIfNotExists(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    // ==================== USER OPERATIONS ====================

    public static void saveUser(User user) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
            bw.write(user.toCSV());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static User getUserByUsername(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1); // -1 to keep empty strings
                if (parts.length >= 4 && parts[0].equals(username)) {
                    return new User(parts[0], parts[1], parts[2], parts[3]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean userExists(String username) {
        return getUserByUsername(username) != null;
    }

    /**
     * Update existing user in CSV file
     * 
     * @param updatedUser User object with updated information
     * @return true if update was successful, false otherwise
     */
    public static boolean updateUser(User updatedUser) {
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 4 && parts[0].equals(updatedUser.getUsername())) {
                    // Replace with updated user
                    lines.add(updatedUser.toCSV());
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (found) {
            writeLinesToFile(USERS_FILE, lines);
            return true;
        }
        return false;
    }

    // ==================== TASK OPERATIONS ====================

    public static void saveTask(Task task) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(TASKS_FILE, true))) {
            bw.write(task.toCSV());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Task> getTasksByUsername(String username) {
        List<Task> tasks = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(TASKS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty())
                    continue;

                String[] parts = parseCSVLine(line);

                // Cek format csv:
                // id,title,description,category,priority,status,progress,created_by,created_at
                // Di dalam loop while ((line = br.readLine()) != null) ...
                if (parts.length >= 10 && parts[7].equals(username)) {
                    Task task = new Task(
                            parts[0],
                            parts[1],
                            parts[2].replace("\\n", "\n"),
                            parts[3],
                            parts[4],
                            parts[5],
                            Integer.parseInt(parts[6]),
                            parts[7],
                            parts[8],
                            parts.length > 9 ? parts[9] : "" // Ambil deadline dari kolom ke-10
                    );
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public static void updateTask(Task updatedTask) {
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(TASKS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = parseCSVLine(line);
                if (parts.length > 0 && parts[0].equals(updatedTask.getId())) {
                    // Replace with updated task
                    lines.add(updatedTask.toCSV());
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (found) {
            writeLinesToFile(TASKS_FILE, lines);
        }
    }

    public static void deleteTask(String taskId) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(TASKS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = parseCSVLine(line);
                if (parts.length > 0 && !parts[0].equals(taskId)) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        writeLinesToFile(TASKS_FILE, lines);
    }

    // Helper sederhana untuk menangani koma dalam CSV jika diperlukan (implementasi
    // basic)
    private static String[] parseCSVLine(String line) {
        // Dalam implementasi nyata yang kompleks, gunakan library CSV seperti OpenCSV
        // Di sini kita asumsi delimiter koma sederhana sesuai format Task.java
        return line.split(",", -1);
    }

    private static void writeLinesToFile(String filePath, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}