package com.taskmanager.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * User Model Class
 * Represents a user in the Task Management System
 *
 * Features:
 * - Password hashing with SHA-256
 * - Password verification
 * - CSV serialization
 * - Timestamp tracking
 */
public class User {
    private String username;
    private String passwordHash;
    private String email;
    private String createdAt;

    /**
     * Constructor for creating new user (with plain password)
     * Password will be automatically hashed
     *
     * @param username User's username
     * @param password Plain text password (will be hashed)
     * @param email User's email address
     */
    public User(String username, String password, String email) {
        this.username = username;
        this.passwordHash = hashPassword(password);
        this.email = email;
        this.createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * Constructor for loading existing user from CSV
     * Password is already hashed
     *
     * @param username User's username
     * @param passwordHash Pre-hashed password
     * @param email User's email address
     * @param createdAt Creation timestamp
     */
    public User(String username, String passwordHash, String email, String createdAt) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.createdAt = createdAt;
    }

    /**
     * Hash a plain text password using SHA-256
     *
     * @param password Plain text password
     * @return Hashed password as hexadecimal string
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Verify if provided password matches the stored hash
     *
     * @param password Plain text password to verify
     * @return true if password matches, false otherwise
     */
    public boolean verifyPassword(String password) {
        return this.passwordHash.equals(hashPassword(password));
    }

    /**
     * Convert User object to CSV format string
     * Format: username,password_hash,email,created_at
     *
     * @return CSV formatted string
     */
    public String toCSV() {
        return username + "," + passwordHash + "," + email + "," + createdAt;
    }

    // ==================== Getters and Setters ====================

    /**
     * Get username
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set username
     * @param username new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get password hash
     * @return hashed password
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Set password hash
     * Note: Use hashPassword() method to hash plain text password first
     * @param passwordHash pre-hashed password
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Get email address
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set email address
     * @param email new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get account creation timestamp
     * @return creation timestamp in ISO format
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Set creation timestamp
     * @param createdAt timestamp string
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // ==================== Utility Methods ====================

    /**
     * String representation of User object
     * @return User information as string
     */
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }

    /**
     * Check if two User objects are equal
     * @param obj object to compare
     * @return true if users have same username
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return username != null && username.equals(user.username);
    }

    /**
     * Generate hash code for User object
     * @return hash code based on username
     */
    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }
}