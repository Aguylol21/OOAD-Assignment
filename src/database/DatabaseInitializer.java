package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private DatabaseInitializer() {
        // Prevent object creation
    }

    public static void initialize() {
        createEquipmentTable();
    }

    private static void createEquipmentTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS equipment (
                equipment_id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                daily_rate INTEGER NOT NULL CHECK (daily_rate > 0),
                category TEXT NOT NULL,
                status TEXT NOT NULL
            )
            """;

        try (
            Connection connection = DatabaseManager.getConnection();
            Statement statement = connection.createStatement()
        ) {
            statement.execute(sql);
            System.out.println("Equipment table created successfully.");

        } catch (SQLException e) {
            System.out.println("Error creating equipment table.");
            e.printStackTrace();
        }
    }
}