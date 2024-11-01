package com.example.minecraftrpg.database;

import com.example.minecraftrpg.MinecraftRPG;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;
import com.example.minecraftrpg.leveling.ExperienceManager;

public class DatabaseManager {
    private final MinecraftRPG plugin;
    private Connection connection;

    public DatabaseManager(MinecraftRPG plugin) {
        this.plugin = plugin;
        initializeDatabase();
    }

    private void initializeDatabase() {
        File dataFolder = new File(plugin.getDataFolder(), "database.db");
        if (!dataFolder.exists()) {
            plugin.getDataFolder().mkdir();
        }

        String url = "jdbc:sqlite:" + dataFolder;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(url);

            // Create all necessary tables
            createPlayerDataTable();
            createClassExperienceTable();
            createAttributesTable();
            createSkillsTable();

            plugin.getLogger().info("Database connection established successfully!");
        } catch (SQLException | ClassNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not connect to database!", e);
        }
    }

    private void createPlayerDataTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            String sql = """
                CREATE TABLE IF NOT EXISTS player_data (
                    uuid VARCHAR(36) PRIMARY KEY,
                    username VARCHAR(16) NOT NULL,
                    current_class VARCHAR(32),
                    last_login TIMESTAMP,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;
            stmt.execute(sql);
        }
    }

    private void createClassExperienceTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            String sql = """
                CREATE TABLE IF NOT EXISTS class_experience (
                    uuid VARCHAR(36),
                    class_name VARCHAR(32),
                    level INT DEFAULT 1,
                    experience INT DEFAULT 0,
                    PRIMARY KEY (uuid, class_name),
                    FOREIGN KEY (uuid) REFERENCES player_data(uuid) ON DELETE CASCADE
                )
                """;
            stmt.execute(sql);
        }
    }

    private void createAttributesTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            String sql = """
                CREATE TABLE IF NOT EXISTS player_attributes (
                    uuid VARCHAR(36),
                    class_name VARCHAR(32),
                    attribute_name VARCHAR(32),
                    attribute_value INT DEFAULT 0,
                    PRIMARY KEY (uuid, class_name, attribute_name),
                    FOREIGN KEY (uuid) REFERENCES player_data(uuid) ON DELETE CASCADE
                )
                """;
            stmt.execute(sql);
        }
    }

    private void createSkillsTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            String sql = """
                CREATE TABLE IF NOT EXISTS player_skills (
                    uuid VARCHAR(36),
                    class_name VARCHAR(32),
                    skill_name VARCHAR(32),
                    skill_level INT DEFAULT 0,
                    skill_points INT DEFAULT 0,
                    PRIMARY KEY (uuid, class_name, skill_name),
                    FOREIGN KEY (uuid) REFERENCES player_data(uuid) ON DELETE CASCADE
                )
                """;
            stmt.execute(sql);
        }
    }

    // Experience Methods
    public void saveClassExperience(UUID uuid, String className, int level, int experience) {
        String sql = """
        INSERT OR REPLACE INTO class_experience (uuid, class_name, level, experience)
        VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, className);
            pstmt.setInt(3, level);
            pstmt.setInt(4, experience);
            int updated = pstmt.executeUpdate();
            plugin.getLogger().info("Saved experience data for " + uuid + ": Class=" + className +
                    ", Level=" + level + ", Exp=" + experience + ", Rows updated=" + updated);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save class experience!", e);
            e.printStackTrace();
        }
    }

    public void loadClassExperience(UUID uuid, String className, ExperienceManager expManager) {
        String sql = "SELECT level, experience FROM class_experience WHERE uuid = ? AND class_name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, className);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int level = rs.getInt("level");
                int experience = rs.getInt("experience");
                expManager.setExperience(uuid, className, level, experience);
                plugin.getLogger().info("Loaded experience data for " + uuid + ": Class=" + className +
                        ", Level=" + level + ", Exp=" + experience);
            } else {
                plugin.getLogger().info("No experience data found for " + uuid + " with class " + className);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not load class experience!", e);
            e.printStackTrace();
        }
    }

    // Player Data Methods
    public void savePlayerData(Player player, String currentClass) {
        String sql = """
            INSERT OR REPLACE INTO player_data (uuid, username, current_class, last_login)
            VALUES (?, ?, ?, CURRENT_TIMESTAMP)
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, player.getUniqueId().toString());
            pstmt.setString(2, player.getName());
            pstmt.setString(3, currentClass);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save player data!", e);
        }
    }

    public String loadCurrentClass(UUID uuid) {
        String sql = "SELECT current_class FROM player_data WHERE uuid = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("current_class");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not load current class!", e);
        }
        return null;
    }

    // Add methods for attributes and skills as needed

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error closing database connection!", e);
        }
    }
}