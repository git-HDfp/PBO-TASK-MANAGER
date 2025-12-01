package com.taskmanager.utils;

import java.io.*;
import java.util.*;

public class SubjectHelper {
    private static final String SUBJECTS_FILE = "SYNC/data/subjects.txt";

    private static final List<String> DEFAULT_SUBJECTS = Arrays.asList(
            "Basis Data",
            "Desain Analisis dan Algoritma",
            "Pemrograman Berorientasi Objek",
            "Pendidikan Kewarganegaraan",
            "Metode Numerik",
            "Matematika Diskrit",
            "Sistem Operasi");

    public static void initializeSubjects() {
        File file = new File(SUBJECTS_FILE);
        if (!file.exists()) {
            try {
                List<String> subjects = new ArrayList<>(DEFAULT_SUBJECTS);
                saveSubjects(subjects);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static List<String> getAllSubjects() {
        initializeSubjects();
        List<String> subjects = new ArrayList<>();

        try {
            File file = new File(SUBJECTS_FILE);
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        subjects.add(line.trim());
                    }
                }
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (subjects.isEmpty()) {
            subjects.addAll(DEFAULT_SUBJECTS);
        }

        return subjects;
    }

    public static boolean addSubject(String subject) {
        if (subject == null || subject.trim().isEmpty()) {
            return false;
        }

        List<String> subjects = getAllSubjects();

        if (subjects.contains(subject.trim())) {
            return false;
        }

        subjects.add(subject.trim());
        saveSubjects(subjects);
        return true;
    }

    public static boolean deleteSubject(String subject) {
        List<String> subjects = getAllSubjects();
        boolean removed = subjects.remove(subject);

        if (removed) {
            saveSubjects(subjects);
        }

        return removed;
    }

    public static boolean updateSubject(String oldSubject, String newSubject) {
        if (newSubject == null || newSubject.trim().isEmpty()) {
            return false;
        }

        List<String> subjects = getAllSubjects();
        int index = subjects.indexOf(oldSubject);

        if (index != -1) {
            subjects.set(index, newSubject.trim());
            saveSubjects(subjects);

            CSVHelper.updateTasksSubject(oldSubject, newSubject.trim());
            return true;
        }

        return false;
    }

    private static void saveSubjects(List<String> subjects) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(SUBJECTS_FILE));
            for (String subject : subjects) {
                writer.write(subject);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

