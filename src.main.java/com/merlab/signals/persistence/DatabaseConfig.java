package com.merlab.signals.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Loads local database settings for examples and manual runs.
 */
public class DatabaseConfig {
    private static final Path DEFAULT_PATH = Paths.get("config", "local-db.properties");

    private final String url;
    private final String user;
    private final String password;

    public DatabaseConfig(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public static DatabaseConfig loadLocal() {
        return load(DEFAULT_PATH);
    }

    public static DatabaseConfig load(Path path) {
        Properties props = new Properties();

        if (!Files.exists(path)) {
            throw new IllegalStateException(
                "Missing database config: " + path.toAbsolutePath()
                + ". Copy config/local-db.example.properties to config/local-db.properties and adjust it."
            );
        }

        try (InputStream in = Files.newInputStream(path)) {
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read database config: " + path.toAbsolutePath(), e);
        }

        return new DatabaseConfig(
            required(props, "db.url", path),
            required(props, "db.user", path),
            required(props, "db.password", path)
        );
    }

    private static String required(Properties props, String key, Path path) {
        String value = props.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Missing property '" + key + "' in " + path.toAbsolutePath());
        }
        return value.trim();
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public DatabaseManager createDatabaseManager() {
        return new DatabaseManager(url, user, password);
    }
}
